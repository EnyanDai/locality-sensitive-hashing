import java.util.*;
public class evaluation {
	
	public static <T> Map<String,Double> testPerformance(Set<T> set1, Set<T> set2) {
		Map<String,Double> performance = new HashMap<String,Double>();
		performance.put("Recall", 0.0);
		performance.put("Precision", 0.0);

		Set<T> intersection = new HashSet<T>(set1);
		intersection.retainAll(set2);
		double Recall = (double)intersection.size()/(double)set1.size();
		double Precision = (double)intersection.size()/(double)set2.size();
		performance.put("Recall",  Recall);
		performance.put("Precision", Precision );
		
//		System.out.println("Recall:  "+Recall+"   Precision: "+Precision);
		return performance;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
