package tests;

import algorithms.AhoCorasick;
import algorithms.Kmp;
import algorithms.Match;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Tests {
    private static int passed;

    public static void main(String[] args) {
        check("ushers", List.of("he", "she", "his", "hers"), List.of(
                new Match(0, 2), new Match(1, 1), new Match(3, 2)));
        check("aaaa", List.of("a", "aa", "aaa"), List.of(
                new Match(0, 0), new Match(0, 1), new Match(0, 2), new Match(0, 3),
                new Match(1, 0), new Match(1, 1), new Match(1, 2),
                new Match(2, 0), new Match(2, 1)));
        check("ababa", List.of("aba", "ba", "aba"), List.of(
                new Match(0, 0), new Match(0, 2), new Match(1, 1),
                new Match(1, 3), new Match(2, 0), new Match(2, 2)));
        check("abc", List.of("", "", "abc"), List.of(
                new Match(0, 0), new Match(0, 1), new Match(0, 2), new Match(0, 3),
                new Match(1, 0), new Match(1, 1), new Match(1, 2), new Match(1, 3),
                new Match(2, 0)));
        check("", List.of("", "a"), List.of(new Match(0, 0)));
        check("", List.of("a"), List.of());
        check("abc", List.of(), List.of());
        check("abc", List.of("d", "abcd"), List.of());
        check("Abc abc", List.of("Abc", "abc"), List.of(
                new Match(0, 0), new Match(1, 4)));
        check("abcxabcd", List.of("abc", "abcd", "bcd", "cd", "x"), List.of(
                new Match(0, 0), new Match(0, 4), new Match(1, 4),
                new Match(2, 5), new Match(3, 6), new Match(4, 3)));
        check("hello world hello", List.of("hello", "world"), List.of(
                new Match(0, 0), new Match(0, 12), new Match(1, 6)));

        Random random = new Random(42);
        for (int test = 0; test < 500; test++) {
            String text = randomString(random, random.nextInt(81));
            List<String> patterns = new ArrayList<>();
            for (int i = 0, count = random.nextInt(21); i < count; i++) {
                patterns.add(randomString(random, random.nextInt(10)));
            }
            check(text, patterns, naive(text, patterns));
        }
        System.out.println("All " + passed + " test cases passed.");
    }

    private static void check(String text, List<String> patterns, List<Match> expected) {
        AhoCorasick aho = new AhoCorasick(patterns);
        Kmp kmp = new Kmp(patterns);
        assertMatches("Aho-Corasick", expected, aho.search(text));
        assertMatches("KMP", expected, kmp.search(text));
        assertMatches("Repeated search", expected, aho.search(text));
        passed++;
    }

    private static void assertMatches(String name, List<Match> expected, List<Match> actual) {
        Comparator<Match> order = Comparator.comparingInt(Match::patternIndex)
                .thenComparingInt(Match::start);
        List<Match> sortedExpected = new ArrayList<>(expected);
        List<Match> sortedActual = new ArrayList<>(actual);
        sortedExpected.sort(order);
        sortedActual.sort(order);
        if (!sortedExpected.equals(sortedActual)) {
            throw new AssertionError(name + " expected " + sortedExpected + " but got " + sortedActual);
        }
    }

    private static List<Match> naive(String text, List<String> patterns) {
        List<Match> matches = new ArrayList<>();
        for (int patternIndex = 0; patternIndex < patterns.size(); patternIndex++) {
            String pattern = patterns.get(patternIndex);
            for (int start = 0; start <= text.length() - pattern.length(); start++) {
                if (text.startsWith(pattern, start)) {
                    matches.add(new Match(patternIndex, start));
                }
            }
        }
        return matches;
    }

    private static String randomString(Random random, int length) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append((char) ('a' + random.nextInt(4)));
        }
        return text.toString();
    }
}
