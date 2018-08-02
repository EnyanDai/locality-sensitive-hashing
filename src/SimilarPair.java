import java.util.Objects;
import java.util.*;
/**
 * SimilarPair contains the ids of two objects and their similarity.
 * 
 * @author Toon Van Craenendonck
 *
 */
public class SimilarPair implements Comparable<SimilarPair>{
	int id1;
	int id2;
	double sim;
	
	/**
	 * Construct a SimilarPair object
	 * @param id1 id of object 1
	 * @param id2 id of object 2
	 * @param sim their similarity
	 */
	public SimilarPair(int id1, int id2, double sim){
		if(id1<id2){
			this.id1 = id1;
			this.id2 = id2;
		}
		else{
			this.id1 = id2;
			this.id2 = id1;
		}
		this.sim = sim;
	}

	/**
	 * Comparing a SimilarPair object to another SimilarPair object.
	 */
	@Override
	public int compareTo(SimilarPair c) {
		if (sim < c.getSimilarity()){
			return -1; 
		}else if (sim == c.getSimilarity()){
			return 0;
		}else{
			return 1;
		}
	}
	
	/**
	 * Returns the id of object 1.
	 */
	public int getId1() {
		return id1;
	}

	/**
	 * Returns the id of object 2.
	 */
	public int getId2() {
		return id2;
	}

	/**
	 * Returns the similarity between the objects.
	 */
	public double getSimilarity(){
		return sim;
	}
	/**
	 * 
	 */
	public boolean equals(Object o){
		if(o == this) return true;
		if(!(o instanceof SimilarPair)) return false;
		SimilarPair pair = (SimilarPair) o;
		return ((pair.id1 == id1) && (pair.id2 == id2));
	}
	public int hashCode(){
		return Objects.hash(id1,id2);
	}
	public static void main(String[] args) {
		SimilarPair a = new SimilarPair(1,3,0.5);
		SimilarPair b = new SimilarPair(3,1,0.2);
		Set<SimilarPair> test = new HashSet();
		test.add(a);
		test.add(b);
		System.out.print(test);
	}

}
