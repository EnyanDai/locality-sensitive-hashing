import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
public class LSH {
	
	TwitterReader reader;
	Sig signtures;
	int band;
	int r;
	int NumberOfBuckets;
	int LogNumberOfBuckets;
	double threshold;
	private class Pairtable{
		int logn;
		int size;
		boolean[] Pairs;
		public Pairtable(int a) {
			logn = a;
			size = 1<<logn;
			Pairs = new boolean[size];
		}
		private int hashPair(final int a,final int b){
			int obj1=a<b?a:b;
			int obj2=b>a?b:a;
			byte[] Ids = new byte[8]; 
			for(int k=0;k<4;k++){
				Ids[k] = (byte)((obj1>>(8*k)) & (0xff));
			}
			for(int k=0;k<4;k++){
				Ids[4+k] = (byte)((obj2>>(8*k)) & (0xff));
			}
			int key = MurmurHash.hash32(Ids, 8);
			int size = this.size-1; 
			int result = key & size;
			return result;
		}
		public boolean contain(final int a,final int b) {
			return this.Pairs[hashPair(a,b)];
		}
		public void add(final int a,final int b) {
			Pairs[hashPair(a,b)] = true;
		}
	};
	private class bucket{
		Vector<Integer> bucket;
		public bucket(){
			this.bucket = new Vector<Integer>(1);
		}
	}
	private class Printer{
		BufferedWriter bw;
		public Printer(String outputFile) {
			try {
				File fout = new File(outputFile);
				FileOutputStream fos = new FileOutputStream(fout);
				bw = new BufferedWriter(new OutputStreamWriter(fos));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		public void printPair(int id1,int id2, double sim) {
			try {
				bw.write(id1 + "," + id2 + "," + sim);
				bw.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public void close() {
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	Pairtable P;
	bucket[][] bucketstable;
	Vector<int[]> records;
	Printer printer;
	/**
	 * 
	 * @param reader
	 * @param band number of bands in this LSH
	 * @param r number of rows per band
	 * @param LogNumberOfBuckets 
	 * @param N the same as nShingler
	 */
	public LSH(TwitterReader reader,int band,int r,int LogNumberOfBuckets,int N,String outputFile,double threshold){
		this.reader = reader;
		this.reader.reset();
		this.signtures = new Sig(r*band,N);
		this.band = band;
		this.r = r;
		this.LogNumberOfBuckets = LogNumberOfBuckets;
		this.NumberOfBuckets = 1<<LogNumberOfBuckets;
		this.threshold = threshold;
		records = new Vector<int[]>();
		P = new Pairtable(18);
		this.printer = new Printer(outputFile);
		//Pairtable = new boolean[this.NumberOfBuckets];
		this.bucketstable = new bucket[band][];
		for(int i=0;i<band;i++){
			bucketstable[i] = new bucket[NumberOfBuckets];
			for(int j=0;j<NumberOfBuckets;j++){
				bucketstable[i][j] = null;
			}
		}
	}
	/**
	 * the hash function to get hash values for different rows
	 * @param data the data you need to hash
	 * @return hash value (location in buckets)
	 */
	private int[] hash(final int[] data){
		int[] result = new int[band]; 
		for(int i=0;i<band;i++){
			byte[] b = new byte[4*r];
			for(int j=0;j<r;j++){
				int index = i*r+j;
				for(int k=0;k<4;k++){
					b[4*j+k] = (byte)((data[index]>>(8*k)) & (0xff));
				}
			}
			int key = MurmurHash.hash32(b, 4*r);
			int size = (1<<LogNumberOfBuckets)-1; 
			result[i] = key & size;
		}
		return result;
	}
	
	
	public void getSimilarPairs(boolean allcandidates){
		int id = 0;
		while(reader.hasNext()){
			Set<Integer> s = reader.next();
			int[] sign = signtures.MinHash(s);
			records.addElement(sign);
			int[] lshash = hash(sign);
			for(int i=0;i<band;i++){
				if(this.bucketstable[i][lshash[i]] == null){
					this.bucketstable[i][lshash[i]] = new bucket();
				}
				else{
					for(int j:bucketstable[i][lshash[i]].bucket){
						if(!P.contain(j, id)){
							double similarity = SignSim(records.get(j),records.get(id));
							if( allcandidates ||(similarity>threshold)) {
								printer.printPair(j, id, similarity);
							}
						};
						P.add(j, id);
					}
				}
				this.bucketstable[i][lshash[i]].bucket.addElement(id);
			}
			if(id % 10000 == 0) {
				System.out.println("at " + id);
			}
			id++;
		}
		printer.close();
		System.out.println("finished");
	}
	
	public Set<SimilarPair> getSimilarPairSet(boolean allcandidates){
		int id = 0;
		Set<SimilarPair> cands = new HashSet<SimilarPair>();
		while(reader.hasNext()){
			Set<Integer> s = reader.next();
			int[] sign = signtures.MinHash(s);
			records.addElement(sign);
			int[] lshash = hash(sign);
			for(int i=0;i<band;i++){
				if(this.bucketstable[i][lshash[i]] == null){
					this.bucketstable[i][lshash[i]] = new bucket();
				}
				else{
					for(int j:bucketstable[i][lshash[i]].bucket){
						double similarity=0;
						if(!P.contain(j, id)){
							similarity = SignSim(records.get(j),records.get(id));
							printer.printPair(j, id, similarity);
						};
						P.add(j, id);
						if( allcandidates ||(similarity>threshold)) {
							cands.add(new SimilarPair(j, id, similarity));
						}
					}
				}
				this.bucketstable[i][lshash[i]].bucket.addElement(id);
			}
			if(id % 10000 == 0) {
//				System.out.println("at " + id);
			}
			id++;
		}
		printer.close();
//		System.out.println("finished");
		return cands;
	}
	private double SignSim(int a[],int b[]){
		double n = a.length;
		double k=0;
		for(int i=0;i<a.length;i++){
			if(a[i] == b[i]){
				k++;
			}
		}
		return k/n;
	}
	public static void main(String[] args) {

		String inputPath = "";
		String outputPath = "";
		int maxFiles = -1;
		int shingleLength = -1;
		int nShingles = 10000;
		double threshold = -1;
		boolean allcandidates = false;
		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			String arg = args[i];
			if(arg.equals("-inputPath")) {
				inputPath = args[i + 1];
			}else if(arg.equals("-maxFiles")){
				maxFiles = Integer.parseInt(args[i+1]);
			}else if(arg.equals("-shingleLength")) {
				shingleLength = Integer.parseInt(args[i + 1]);
			}else if(arg.equals("-outputPath")){
				outputPath = args[i + 1];
			}else if(arg.equals("-threshold")){
				threshold = new Double(args[i + 1]);
			}else if(arg.equals("-allcandidates")){
				allcandidates = true;
			}
			i += 2;
		}

		Shingler shingler = new Shingler(shingleLength, nShingles);
		TwitterReader reader = new TwitterReader(maxFiles, shingler, inputPath);
		reader.reset();
		LSH lshSearcher = new LSH(reader,2,8,18,nShingles,outputPath,threshold);
		lshSearcher.getSimilarPairs(allcandidates);
	}

}
