package textanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class AllTests {
    private static int passed = 0, failed = 0;

    private static void check(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name + "  expected=" + expected + " actual=" + actual);
        }
    }

    private static List<Integer> list(Integer... xs) { return Arrays.asList(xs); }

    private static List<Integer> range(int from, int toInclusive) {
        List<Integer> r = new ArrayList<>();
        for (int i = from; i <= toInclusive; i++) r.add(i);
        return r;
    }

    private static void match(String name, String text, String pattern, List<Integer> expected) {
        StringMatcher[] ms = {new NaiveMatcher(), new KMPMatcher(), new RabinKarpMatcher()};
        for (StringMatcher m : ms) {
            check(name + " [" + m.name() + "]", expected, m.search(text, pattern));
        }
    }

    public static void main(String[] args) {
        check("LCS identical", 3, LCS.length("abc", "abc"));
        check("LCS completely different", 0, LCS.length("abc", "xyz"));
        check("LCS empty first", 0, LCS.length("", "abc"));
        check("LCS empty both", 0, LCS.length("", ""));
        check("LCS classic AGGTAB/GXTXAYB", 4, LCS.length("AGGTAB", "GXTXAYB"));

        check("ED identical", 0, EditDistance.distance("abc", "abc"));
        check("ED one empty (a empty)", 3, EditDistance.distance("", "abc"));
        check("ED one empty (b empty)", 3, EditDistance.distance("abc", ""));
        check("ED substitution", 1, EditDistance.distance("abc", "adc"));
        check("ED insertion", 1, EditDistance.distance("abc", "abxc"));
        check("ED deletion", 1, EditDistance.distance("abxc", "abc"));
        check("ED kitten/sitting", 3, EditDistance.distance("kitten", "sitting"));

        String t = "hello world";
        match("match at beginning", t, "hello", list(0));
        match("match in middle", t, "o w", list(4));
        match("match at end", t, "world", list(6));
        match("multiple occurrences", t, "o", list(4, 7));
        match("overlapping occurrences", "aaaaa", "aa", list(0, 1, 2, 3));
        match("pattern absent", t, "abc", list());
        match("pattern longer than text", "abc", "abcd", list());
        match("empty pattern", t, "", range(0, t.length()));
        match("empty text, empty pattern", "", "", list(0));
        match("empty text, non-empty pattern", "", "a", list());
        match("pattern equals text", "abc", "abc", list(0));

        check("KMP LPS table", Arrays.toString(new int[]{0, 0, 1, 2, 3, 0, 1}),
                Arrays.toString(KMPMatcher.lpsTable("ababaca")));

        Random rnd = new Random(1);
        boolean allEqual = true;
        for (int it = 0; it < 2000; it++) {
            String text = randomString(rnd, rnd.nextInt(60), "ab");
            String pat = randomString(rnd, rnd.nextInt(6), "ab");
            List<Integer> a = new NaiveMatcher().search(text, pat);
            List<Integer> b = new KMPMatcher().search(text, pat);
            List<Integer> c = new RabinKarpMatcher().search(text, pat);
            if (!a.equals(b) || !a.equals(c)) { allEqual = false; break; }
        }
        check("randomized: Naive == KMP == RabinKarp (2000 cases)", true, allEqual);

        int[] sa = {5, 3, 1, 0, 4, 2};
        check("suffix array util banana", Arrays.toString(sa), Arrays.toString(SuffixArrayUtil.build("banana")));
        check("Kasai banana LCP", Arrays.toString(new int[]{0, 1, 3, 0, 0, 2}),
                Arrays.toString(Kasai.buildLcp("banana", sa)));
        check("Kasai abab LCP", Arrays.toString(new int[]{0, 2, 0, 1}),
                Arrays.toString(Kasai.buildLcp("abab", new int[]{2, 0, 3, 1})));
        boolean kasaiOk = true;
        for (int it = 0; it < 300 && kasaiOk; it++) {
            String s = randomString(rnd, 1 + rnd.nextInt(40), "abc");
            int[] sar = SuffixArrayUtil.build(s);
            int[] lcp = Kasai.buildLcp(s, sar);
            for (int i = 1; i < s.length(); i++) {
                int h = 0;
                while (sar[i] + h < s.length() && sar[i - 1] + h < s.length()
                        && s.charAt(sar[i] + h) == s.charAt(sar[i - 1] + h)) h++;
                if (lcp[i] != h) { kasaiOk = false; break; }
            }
        }
        check("Kasai randomized vs brute force (300 cases)", true, kasaiOk);

        System.out.println("\nPassed: " + passed + "  Failed: " + failed);
        if (failed > 0) System.exit(1);
    }

    private static String randomString(Random r, int len, String alphabet) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        return sb.toString();
    }
}
