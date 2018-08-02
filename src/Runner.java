import java.util.*;
import java.io.*;


public class Runner {

	public static void main(String[] args) {

		String inputPath = "";
		String outputPath = "";
		int maxFiles = -1;
		int shingleLength = -1;
		float threshold = -1;
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
			}else if(arg.equals("-allcandidates")){
				allcandidates = true;
			}
			i += 2;
		}
		int[] nShingles = new int[]{1000,10000,100000};
		threshold = (float)0.9;
		int LogNumber = 18;
		int[] bands = new int[] {2,3};
		int[] rows = new int[] {4,5,6,7,8,9,10,11,12,13,14,15};
		System.out.println("nShingles band row recall precision time");
		for(int n:nShingles) {
			Shingler shingler = new Shingler(shingleLength, n);
			TwitterReader reader = new TwitterReader(maxFiles, shingler, inputPath);
			BruteForceSearch BruteSearcher = new BruteForceSearch(reader);
			Set<SimilarPair> similarItemsBrute = BruteSearcher.getSimilarPairsAboveThreshold(threshold);
			for(int band:bands) {
				for(int row:rows) {
					int repeat = 10;
					double recall = 0;
					double precision = 0;
					long time = 0;
					for(int j = 0;j < repeat; j++) {
						reader.reset();
						LSH lshSearcher = new LSH(reader,band,row,LogNumber,n,outputPath,0.9);
						long startTime=System.currentTimeMillis(); 
						Set<SimilarPair> lshSimilarItems = lshSearcher.getSimilarPairSet(allcandidates);
						long endTime=System.currentTimeMillis(); 
						reader.reset();
						Map<String,Double> perf2 = evaluation.testPerformance(similarItemsBrute, lshSimilarItems);
						recall =recall + perf2.get("Recall");
						precision = precision + perf2.get("Precision");
						time = time + endTime - startTime;
					}
					recall = recall/(double)repeat;
					precision = precision/(double)repeat;
					double P = 2.0*recall*precision/(recall+precision);
					long runtime = time/repeat; 
					System.out.println(n+" "+band+" "+row+" "+recall+" "+precision+" "+runtime);
				}
			}
		}
	}


	
	/**
	 * Prints pairs and their similarity.
	 * @param similarItems the set of similar items to print
	 * @param outputFile the path of the file to which they will be printed
	 */
	public static void printPairs(Set<SimilarPair> similarItems, String outputFile){
		try {
			File fout = new File(outputFile);
			FileOutputStream fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

			List<SimilarPair> sim = new ArrayList<SimilarPair>(similarItems);
			Collections.sort(sim, Collections.reverseOrder());
			for(SimilarPair p : sim){
				bw.write(p.getId1() + "," + p.getId2() + "," + p.getSimilarity());
				bw.newLine();
			}

			bw.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
