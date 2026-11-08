package textanalysis;

import java.util.Arrays;

public final class SuffixArrayUtil {
    private SuffixArrayUtil() {}

    public static int[] build(String s) {
        int n = s.length();
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> {
            int i = a, j = b;
            while (i < n && j < n) {
                char ci = s.charAt(i), cj = s.charAt(j);
                if (ci != cj) return Character.compare(ci, cj);
                i++;
                j++;
            }
            return Integer.compare(n - a, n - b);
        });
        int[] sa = new int[n];
        for (int i = 0; i < n; i++) sa[i] = idx[i];
        return sa;
    }
}
