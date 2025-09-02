package it.andreuzzi.comparestring2;

/**
 * Base interface for string similarity algorithms.
 */
public interface StringSimilarity {
    /**
     * Compute similarity between two strings.
     *
     * @param s1 first string
     * @param s2 second string
     * @return similarity score
     */
    double similarity(String s1, String s2);
}
