package de.uni.koeln.spinfo.bkiss.batcave.analysis;

/**
 * Vector math utility class
 * @author kiss
 *
 */
public class VectorMath {
	
	/**
	 * calculates the cosine similarity of the two given vectors
	 * @param vA
	 * @param vB
	 * @return
	 */
	public static double cosineSimilarity(Double[] vA, Double[] vB) {
	    double dot = 0.0;
	    double normA = 0.0;
	    double normB = 0.0;
	    for (int i = 0; i < vA.length; i++) {
	        dot += vA[i] * vB[i];
	        normA += Math.pow(vA[i], 2);
	        normB += Math.pow(vB[i], 2);
	    }   
	    return dot / (Math.sqrt(normA) * Math.sqrt(normB));
	}

}
