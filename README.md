# Bonus Task: Aho-Corasick

Java 17 implementation of Aho-Corasick and KMP for finding all pattern occurrences.

## Run

```sh
mkdir -p target/classes results
javac -d target/classes $(find src -name '*.java')
java -cp target/classes tests.Tests
java -cp target/classes experiments.Benchmark > results/results.csv
```

Matches store the pattern index and starting position, both from 0. The code supports overlaps, duplicates, and empty patterns. Empty patterns match every boundary. Matching is case-sensitive.

## How it works

The trie shares common pattern prefixes. Terminal nodes store pattern indices. BFS builds failure links to the longest proper suffix that is also a trie prefix. Root children link to the root. Other links are found by following the parent's failure chain until a transition exists or the root is reached.

Searching follows failure links after mismatches without moving backward in the text. Output links visit terminal suffixes and report every matching pattern.

## Complexity

Let `M` be the total pattern length, `k` the number of patterns, `n` the text length, and `z` the number of matches.

| Algorithm | Preprocessing | Search | Stored data |
| --- | --- | --- | --- |
| Aho-Corasick | O(M + k) expected | O(n + z) expected | O(M + k) |
| KMP for every pattern | O(M + k) | O(kn + z) | O(M + k) |

There are at most `M + 1` trie nodes, each with failure and output links. Failure steps reduce the matched length, keeping the scan linear. HashMap lookups take expected constant time. KMP stores each pattern's prefix array. Both algorithms also need O(z) result space.

## Tests and results

All 511 tests passed, including 500 random cases checked against simple search. Tests cover suffixes, overlaps, duplicates, empty inputs, missing patterns, and repeated searches.

Experiments ran on Windows, Temurin OpenJDK 17.0.21. Inputs use `a` to `d`, seed 42, and unique patterns of length 5 to 12. Half are sampled from the text, half generated randomly.

Both algorithms return the same matches. After 10 warm-up runs per algorithm on the largest input, each case runs 5 times with `System.nanoTime()`. Timing includes preprocessing and searching. Generation, validation, and printing are outside the timer. Run order alternates. [results.csv](results/results.csv) contains all measurements.

| Patterns | Text length | Aho-Corasick average (ms) | KMP average (ms) |
| --- | --- | --- | --- |
| 10 | 1,000 | 0.082 | 0.141 |
| 10 | 10,000 | 0.324 | 1.351 |
| 10 | 100,000 | 2.601 | 13.511 |
| 100 | 1,000 | 0.316 | 1.382 |
| 100 | 10,000 | 0.686 | 14.064 |
| 100 | 100,000 | 3.317 | 118.207 |
| 1,000 | 1,000 | 0.686 | 8.680 |
| 1,000 | 10,000 | 1.546 | 113.920 |
| 1,000 | 100,000 | 7.032 | 1,039.372 |

Aho-Corasick was faster in every case, with a larger gap as pattern counts grew. It searches all patterns in one pass. KMP scans once per pattern, but is simpler and has less object overhead for a single pattern. Aho-Corasick needs trie construction and more node objects. JVM activity and other programs can affect timings.
