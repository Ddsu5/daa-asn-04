package textanalysis;

import java.util.ArrayList;
import java.util.List;

public final class KMPMatcher implements StringMatcher {
    private long comparisons;

    static int[] buildLps(String p, long[] counter) {
        int m = p.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        while (i < m) {
            counter[0]++;
            if (p.charAt(i) == p.charAt(len)) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        return lps;
    }

    public static int[] lpsTable(String p) {
        return buildLps(p, new long[1]);
    }

    @Override
    public List<Integer> search(String text, String pattern) {
        comparisons = 0;
        List<Integer> result = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) {
            for (int i = 0; i <= n; i++) result.add(i);
            return result;
        }
        if (m > n) return result;
        long[] counter = new long[1];
        int[] lps = buildLps(pattern, counter);
        int i = 0, j = 0;
        while (i < n) {
            counter[0]++;
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
                if (j == m) {
                    result.add(i - j);
                    j = lps[j - 1];
                }
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        comparisons = counter[0];
        return result;
    }

    @Override public long comparisons() { return comparisons; }
    @Override public String name() { return "KMP"; }
}
