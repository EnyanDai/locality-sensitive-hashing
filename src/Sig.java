import java.util.*;
public class Sig {
	/*
	 * lsh implentation of the similarty searcher
	 * 
	 * 
	 */
	//TwitterReader reader;
	int N;
	int p;
	int hashNumber;
	private class universalHash{
		int a;
		int b;
		public universalHash(int a,int b){
			this.a=a;
			this.b=b;
		}
		public boolean equals(universalHash another){
			if((another.a==a) && (another.b==b)){
				return true;
			}
			return false;
		}
		public int hash(int s){
			return ((a*s+b)%p)%N;
		}
	}
	Vector<universalHash> Hashs;
	/**build universal hash functions to get signatures
	 * @hashNumber is the number of hash functions
	 * @N is the rows of shingler
	 */
	public Sig(int hashNumber,int N){
		//this.reader = reader;
		getUniversalHash(hashNumber);
		this.p = Primes.findLeastPrimeNumber(N);
		this.N = N;
		this.hashNumber = hashNumber;
	}
	
	private void getUniversalHash(int hashNumber){
		this.Hashs=new Vector<universalHash>();
		while(Hashs.size() < hashNumber){
			int a = (int)(1000.0*Math.random());
			int b = (int)(1000.0*Math.random());
			universalHash tmp = new universalHash(a,b);
			if(!inHash(tmp)){
				this.Hashs.addElement(tmp);
			}
		}
	}
	
	private boolean inHash(universalHash a){
		for(universalHash compare:Hashs){
			if(a.equals(compare)){
				return true;
			}
		}
		return false;
	}
	
	private int getHash(int index,Integer s){
		return Hashs.get(index).hash(s);
	}
	/**
	 * Get signatures
	 * @param shingles
	 * @return signatures 
	 */
	public int[] MinHash(Set<Integer> shingles){
		int[] result = new int[hashNumber];
		
		for(int i=0;i<hashNumber;i++){
			result[i] = Integer.MAX_VALUE;
			for(Integer s:shingles){
				int h = getHash(i,s);
				if(h < result[i]){
					result[i]=h;
				}
			}
		}
		return result;
	}
	/**
	 * @return approximate jaccard similarity of a and b
	 */
	public double signatureSimilarity(Set<Integer> a,Set<Integer> b){
		int[] ASignatures = this.MinHash(a);
		int[] BSignatures = this.MinHash(b);
		int n=0;
		for(int i=0;i<this.hashNumber;i++){
			if(ASignatures[i]==BSignatures[i]){
				n++;
			}
		}
		return ((double)n)/((double)hashNumber);
	}
}
