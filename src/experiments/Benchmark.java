package experiments;

import algorithms.AhoCorasick;
import algorithms.Kmp;
import algorithms.Match;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class Benchmark {
    private static final int RUNS = 5;
    private static volatile long checksum;

    public static void main(String[] args) {
        Random random = new Random(42);
        String warmupText = randomString(random, 100000);
        List<String> warmupPatterns = makePatterns(random, warmupText, 1000);
        for (int i = 0; i < 10; i++) {
            measure(warmupText, warmupPatterns, true);
            measure(warmupText, warmupPatterns, false);
        }

        System.out.println("patterns,text_length,algorithm,run1_ns,run2_ns,run3_ns,run4_ns,run5_ns,average_ms,matches");
        for (int count : new int[] {10, 100, 1000}) {
            for (int length : new int[] {1000, 10000, 100000}) {
                String text = randomString(random, length);
                List<String> patterns = makePatterns(random, text, count);
                List<Match> ahoMatches = new AhoCorasick(patterns).search(text);
                List<Match> kmpMatches = new Kmp(patterns).search(text);
                if (ahoMatches.size() != kmpMatches.size()
                        || !new HashSet<>(ahoMatches).equals(new HashSet<>(kmpMatches))) {
                    throw new AssertionError("Different matches for " + count + " patterns and " + length + " characters");
                }
                long[] ahoTimes = new long[RUNS];
                long[] kmpTimes = new long[RUNS];
                for (int run = 0; run < RUNS; run++) {
                    if (run % 2 == 0) {
                        ahoTimes[run] = measure(text, patterns, true);
                        kmpTimes[run] = measure(text, patterns, false);
                    } else {
                        kmpTimes[run] = measure(text, patterns, false);
                        ahoTimes[run] = measure(text, patterns, true);
                    }
                }
                printResult(count, length, "Aho-Corasick", ahoTimes, ahoMatches.size());
                printResult(count, length, "KMP", kmpTimes, kmpMatches.size());
            }
        }
    }

    private static long measure(String text, List<String> patterns, boolean aho) {
        long start = System.nanoTime();
        List<Match> matches = aho
                ? new AhoCorasick(patterns).search(text)
                : new Kmp(patterns).search(text);
        long elapsed = System.nanoTime() - start;
        checksum += matches.size();
        return elapsed;
    }

    private static void printResult(int count, int length, String algorithm, long[] times, int matches) {
        long total = 0;
        System.out.print(count + "," + length + "," + algorithm);
        for (long time : times) {
            System.out.print("," + time);
            total += time;
        }
        System.out.printf(Locale.ROOT, ",%.6f,%d%n", total / (RUNS * 1000000.0), matches);
    }

    private static List<String> makePatterns(Random random, String text, int count) {
        Set<String> patterns = new LinkedHashSet<>();
        while (patterns.size() < count) {
            int length = 5 + random.nextInt(8);
            String pattern;
            if (patterns.size() % 2 == 0) {
                int start = random.nextInt(text.length() - length + 1);
                pattern = text.substring(start, start + length);
            } else {
                pattern = randomString(random, length);
            }
            patterns.add(pattern);
        }
        return new ArrayList<>(patterns);
    }

    private static String randomString(Random random, int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append((char) ('a' + random.nextInt(4)));
        }
        return text.toString();
    }
}
