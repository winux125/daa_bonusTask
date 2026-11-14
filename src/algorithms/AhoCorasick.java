package algorithms;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public class AhoCorasick {
    private static class Node {
        final Map<Character, Node> children = new HashMap<>();
        final List<Integer> patterns = new ArrayList<>();
        Node failure;
        Node output;
    }

    private final Node root = new Node();
    private final int[] lengths;

    public AhoCorasick(List<String> patterns) {
        Objects.requireNonNull(patterns);
        lengths = new int[patterns.size()];
        for (int i = 0; i < patterns.size(); i++) {
            String pattern = Objects.requireNonNull(patterns.get(i));
            lengths[i] = pattern.length();
            Node node = root;
            for (int j = 0; j < pattern.length(); j++) {
                node = node.children.computeIfAbsent(pattern.charAt(j), key -> new Node());
            }
            node.patterns.add(i);
        }
        buildFailureLinks();
    }

    private void buildFailureLinks() {
        root.failure = root;
        Queue<Node> queue = new ArrayDeque<>();
        for (Node child : root.children.values()) {
            child.failure = root;
            child.output = root.patterns.isEmpty() ? null : root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node node = queue.remove();
            for (Map.Entry<Character, Node> edge : node.children.entrySet()) {
                char character = edge.getKey();
                Node child = edge.getValue();
                Node fallback = node.failure;
                while (fallback != root && !fallback.children.containsKey(character)) {
                    fallback = fallback.failure;
                }
                child.failure = fallback.children.getOrDefault(character, root);
                child.output = child.failure.patterns.isEmpty()
                        ? child.failure.output : child.failure;
                queue.add(child);
            }
        }
    }

    public List<Match> search(String text) {
        Objects.requireNonNull(text);
        List<Match> matches = new ArrayList<>();
        Node node = root;
        addMatches(node, 0, matches);
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            while (node != root && !node.children.containsKey(character)) {
                node = node.failure;
            }
            node = node.children.getOrDefault(character, root);
            addMatches(node, i + 1, matches);
        }
        return matches;
    }

    private void addMatches(Node node, int end, List<Match> matches) {
        for (Node current = node; current != null; current = current.output) {
            for (int patternIndex : current.patterns) {
                matches.add(new Match(patternIndex, end - lengths[patternIndex]));
            }
        }
    }
}
