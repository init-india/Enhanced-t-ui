package it.andreuzzi.comparestring2;

import java.util.HashMap;
import java.util.Map;

/**
 * Cosine similarity based on character bigrams.
 */
public class Cosine implements StringSimilarity {

    @Override
    public double similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;

        Map<String, Integer> profile1 = getProfile(s1);
        Map<String, Integer> profile2 = getProfile(s2);

        double dotProduct = 0;
        for (String key : profile1.keySet()) {
            if (profile2.containsKey(key)) {
                dotProduct += profile1.get(key) * profile2.get(key);
            }
        }

        double norm1 = 0;
        for (int val : profile1.values()) norm1 += val * val;
        double norm2 = 0;
        for (int val : profile2.values()) norm2 += val * val;

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    private Map<String, Integer> getProfile(String s) {
        Map<String, Integer> profile = new HashMap<>();
        for (int i = 0; i < s.length() - 1; i++) {
            String bigram = s.substring(i, i + 2);
            profile.put(bigram, profile.getOrDefault(bigram, 0) + 1);
        }
        return profile;
    }
}
