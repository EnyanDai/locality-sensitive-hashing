import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NaiveSearch {
	TwitterReader reader;
	Sig signatures;
	/**Method only use signature compression
	 * @hashNumber is the number of hash functions
	 * @N is the rows of shingler
	 */
	public NaiveSearch(TwitterReader reader,int hashNumber, int N){
		this.reader = reader;
		this.reader.reset();
		this.signatures = new Sig(hashNumber,N);
	}
	
	/**
	 * Get pairs of objects with similarity above threshold.
	 * @param threshold the similarity threshold
	 * @return the pairs
	 */
	public Set<SimilarPair> getSimilarPairsAboveThreshold(double threshold) {

		Map<Integer, Set<Integer>> docToShingle = new HashMap<Integer, Set<Integer>>();
		int id = 0;
		while (reader.hasNext()){
//			System.out.println(id);
			docToShingle.put(id,reader.next());
			id++;
		}

		Set<SimilarPair> cands = new HashSet<SimilarPair>();
		for (Integer obj1 : docToShingle.keySet()){
		    if (obj1 % 10000 == 0){
//		        System.out.println("at " + obj1);
            }
			for (Integer obj2 : docToShingle.keySet()){
				if (obj1 < obj2){
					double sim = this.signatures.signatureSimilarity(docToShingle.get(obj1),docToShingle.get(obj2));
					if (sim > threshold){
						cands.add(new SimilarPair(obj1, obj2, sim));
					}
	   			}
			}
		}
		return cands;
	}


	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String inputPath = "";
		String outputPath = "";
		int maxFiles = -1;
		int shingleLength = -1;
		int nShingles = -1;
		float threshold = -1;

		int i = 0;
		while (i < args.length && args[i].startsWith("-")) {
			String arg = args[i];
			if(arg.equals("-inputPath")) {
				inputPath = args[i + 1];
			}else if(arg.equals("-maxFiles")){
				maxFiles = Integer.parseInt(args[i+1]);
			}else if(arg.equals("-shingleLength")) {
				shingleLength = Integer.parseInt(args[i + 1]);
			}else if(arg.equals("-nShingles")){
				nShingles = Integer.parseInt(args[i+1]);
			}else if(arg.equals("-threshold")){
				threshold = Float.parseFloat(args[i+1]);
			}else if(arg.equals("-outputPath")){
				outputPath = args[i + 1];
			}
			i += 2;
		}
		int[] S=new int[] {1000,10000};
		for(int n:S) {
			nShingles = n;
			Shingler shingler = new Shingler(shingleLength, nShingles);
			TwitterReader reader = new TwitterReader(maxFiles, shingler, inputPath);
			BruteForceSearch BruteSearcher = new BruteForceSearch(reader);
			Set<SimilarPair> similarItemsBrute = BruteSearcher.getSimilarPairsAboveThreshold(threshold);
			int h = 10;
			System.out.println("NumberOfHashes Recall Precision");
			for(int j =0; j < 5; j++) {
				h = 10 + j*10;
				reader.reset();
				NaiveSearch NaiveSearcher = new NaiveSearch(reader,h,nShingles);
				Set<SimilarPair> similarItemsNaive = NaiveSearcher.getSimilarPairsAboveThreshold(threshold);
				Map<String,Double> perf = evaluation.testPerformance(similarItemsBrute, similarItemsNaive);
				System.out.println(h+" "+perf.get("Recall")+" "+perf.get("Precision"));
			}
		}
	}

}
