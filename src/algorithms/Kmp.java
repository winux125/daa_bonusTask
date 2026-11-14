package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Kmp {
    private final List<String> patterns;
    private final int[][] prefix;

    public Kmp(List<String> patterns) {
        this.patterns = List.copyOf(patterns);
        prefix = new int[patterns.size()][];
        for (int i = 0; i < patterns.size(); i++) {
            prefix[i] = buildPrefix(patterns.get(i));
        }
    }

    private int[] buildPrefix(String pattern) {
        int[] table = new int[pattern.length()];
        int matched = 0;
        for (int i = 1; i < pattern.length(); i++) {
            while (matched > 0 && pattern.charAt(i) != pattern.charAt(matched)) {
                matched = table[matched - 1];
            }
            if (pattern.charAt(i) == pattern.charAt(matched)) {
                matched++;
            }
            table[i] = matched;
        }
        return table;
    }

    public List<Match> search(String text) {
        Objects.requireNonNull(text);
        List<Match> matches = new ArrayList<>();
        for (int patternIndex = 0; patternIndex < patterns.size(); patternIndex++) {
            String pattern = patterns.get(patternIndex);
            if (pattern.isEmpty()) {
                for (int i = 0; i <= text.length(); i++) {
                    matches.add(new Match(patternIndex, i));
                }
                continue;
            }
            int matched = 0;
            for (int i = 0; i < text.length(); i++) {
                while (matched > 0 && text.charAt(i) != pattern.charAt(matched)) {
                    matched = prefix[patternIndex][matched - 1];
                }
                if (text.charAt(i) == pattern.charAt(matched)) {
                    matched++;
                }
                if (matched == pattern.length()) {
                    matches.add(new Match(patternIndex, i - pattern.length() + 1));
                    matched = prefix[patternIndex][matched - 1];
                }
            }
        }
        return matches;
    }
}
