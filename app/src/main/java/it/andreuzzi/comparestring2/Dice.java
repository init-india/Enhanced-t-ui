package it.andreuzzi.comparestring2;

import java.util.HashSet;
import java.util.Set;

/**
 * Sørensen–Dice coefficient similarity.
 */
public class Dice implements StringSimilarity {

    @Override
    public double similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;

        Set<String> bigrams1 = getBigrams(s1);
        Set<String> bigrams2 = getBigrams(s2);

        int intersection = 0;
        for (String bg : bigrams1) {
            if (bigrams2.contains(bg)) intersection++;
        }

        return (2.0 * intersection) / (bigrams1.size() + bigrams2.size());
    }

    private Set<String> getBigrams(String s) {
        Set<String> bigrams = new HashSet<>();
        for (int i = 0; i < s.length() - 1; i++) {
            bigrams.add(s.substring(i, i + 2));
        }
        return bigrams;
    }
}
