package it.andreuzzi.comparestring2;

/**
 * Simple service to use different string similarity algorithms.
 */
public class StringSimilarityService {

    private final StringSimilarity algorithm;

    public StringSimilarityService(StringSimilarity algorithm) {
        this.algorithm = algorithm;
    }

    public double similarity(String s1, String s2) {
        return algorithm.similarity(s1, s2);
    }
}
