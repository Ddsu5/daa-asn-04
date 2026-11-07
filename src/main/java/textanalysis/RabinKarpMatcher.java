package textanalysis;

import java.util.ArrayList;
import java.util.List;

public final class RabinKarpMatcher implements StringMatcher {
    private static final long BASE = 256;
    private static final long MOD = 1_000_000_007L;

    private long comparisons;
    private long spuriousHits;

    @Override
    public List<Integer> search(String text, String pattern) {
        comparisons = 0;
        spuriousHits = 0;
        List<Integer> result = new ArrayList<>();
        int n = text.length(), m = pattern.length();
        if (m == 0) {
            for (int i = 0; i <= n; i++) result.add(i);
            return result;
        }
        if (m > n) return result;

        long pow = 1;
        for (int i = 0; i < m - 1; i++) pow = (pow * BASE) % MOD;

        long hp = 0, ht = 0;
        for (int i = 0; i < m; i++) {
            hp = (hp * BASE + pattern.charAt(i)) % MOD;
            ht = (ht * BASE + text.charAt(i)) % MOD;
        }

        for (int i = 0; ; i++) {
            if (hp == ht) {
                int j = 0;
                while (j < m) {
                    comparisons++;
                    if (text.charAt(i + j) != pattern.charAt(j)) break;
                    j++;
                }
                if (j == m) result.add(i);
                else spuriousHits++;
            }
            if (i + m >= n) break;
            ht = (ht - text.charAt(i) * pow % MOD + MOD) % MOD;
            ht = (ht * BASE + text.charAt(i + m)) % MOD;
        }
        return result;
    }

    public long spuriousHits() { return spuriousHits; }
    @Override public long comparisons() { return comparisons; }
    @Override public String name() { return "RabinKarp"; }
}
