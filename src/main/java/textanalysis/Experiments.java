package textanalysis;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

public final class Experiments {
    static final String ALPHABET = "ACGT";
    static final long SEED = 42;
    static final int RUNS = 5;
    static final int WARMUPS = 20;
    static final int PATTERN_LEN = 20;

    static long sink = 0; // consumes results so the JIT cannot discard the work

    static String randomString(Random r, int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHABET.charAt(r.nextInt(ALPHABET.length())));
        return sb.toString();
    }

    static double avgMs(Supplier<Object> task) {
        for (int w = 0; w < WARMUPS; w++) sink += task.get().hashCode(); // untimed JIT warm-up
        long total = 0;
        for (int r = 0; r < RUNS; r++) {
            long t0 = System.nanoTime();
            Object res = task.get();
            long t1 = System.nanoTime();
            total += t1 - t0;
            sink += res.hashCode();
        }
        return total / (double) RUNS / 1e6;
    }

    public static void main(String[] args) throws IOException {
        List<String> rows = new ArrayList<>();
        rows.add("experiment,size,scenario,algorithm,avg_ms,operations,matches");

        for (int n : new int[]{100, 500, 1000}) {
            Random r = new Random(SEED + n);
            String a = randomString(r, n), b = randomString(r, n);
            double tl = avgMs(() -> LCS.length(a, b));
            int lcs = LCS.length(a, b);
            double te = avgMs(() -> EditDistance.distance(a, b));
            int ed = EditDistance.distance(a, b);
            long cells = (long) n * n;
            rows.add(String.format(Locale.ROOT, "LCS,%d,random,LCS,%.4f,%d,%d", n, tl, cells, lcs));
            rows.add(String.format(Locale.ROOT, "EditDistance,%d,random,EditDistance,%.4f,%d,%d", n, te, cells, ed));
        }

        StringMatcher[] matchers = {new NaiveMatcher(), new KMPMatcher(), new RabinKarpMatcher()};
        for (int n : new int[]{10_000, 50_000, 100_000}) {
            Random r = new Random(SEED + n);
            String text = randomString(r, n);
            String present = text.substring(n / 2, n / 2 + PATTERN_LEN);
            String absent;
            do {
                absent = randomString(r, PATTERN_LEN);
            } while (new NaiveMatcher().search(text, absent).size() > 0);
            String worstText = "A".repeat(n);
            String worstPat = "A".repeat(PATTERN_LEN - 1) + "C";

            String[] scen = {"present", "absent", "worst-case"};
            String[] texts = {text, text, worstText};
            String[] pats = {present, absent, worstPat};
            for (int s = 0; s < 3; s++) {
                final String tx = texts[s], pt = pats[s];
                for (StringMatcher m : matchers) {
                    double ms = avgMs(() -> m.search(tx, pt));
                    List<Integer> res = m.search(tx, pt);
                    rows.add(String.format(Locale.ROOT, "Matching,%d,%s,%s,%.4f,%d,%d",
                            n, scen[s], m.name(), ms, m.comparisons(), res.size()));
                }
            }
        }

        for (int n : new int[]{1000, 5000, 10000}) {
            Random r = new Random(SEED + n);
            String s = randomString(r, n);
            int[] sa = SuffixArrayUtil.build(s); // construction NOT timed
            double ms = avgMs(() -> Kasai.buildLcp(s, sa));
            rows.add(String.format(Locale.ROOT, "Kasai,%d,random,Kasai,%.4f,%d,0", n, ms, 0));
        }

        Path out = Paths.get(args.length > 0 ? args[0] : "results/results.csv");
        Files.createDirectories(out.getParent());
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(out))) {
            rows.forEach(pw::println);
        }
        rows.forEach(System.out::println);
        System.out.println("(sink=" + sink + ")  written to " + out);
    }
}
