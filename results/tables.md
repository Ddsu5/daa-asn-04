### DP algorithms

| n | LCS time (ms) | Edit Distance time (ms) | DP cells | LCS length | Edit distance |
|---|---|---|---|---|---|
| 100 | 0.149 | 0.164 | 10000 | 61 | 56 |
| 500 | 0.754 | 1.088 | 250000 | 320 | 262 |
| 1000 | 2.968 | 3.352 | 1000000 | 647 | 524 |

### String matching — pattern present

| Text length | Algorithm | Time (ms) | Char comparisons | Matches |
|---|---|---|---|---|
| 10000 | Naive | 0.1906 | 13416 | 1 |
| 10000 | KMP | 0.1790 | 12529 | 1 |
| 10000 | RabinKarp | 0.1565 | 20 | 1 |
| 50000 | Naive | 0.1929 | 66677 | 1 |
| 50000 | KMP | 0.2029 | 61831 | 1 |
| 50000 | RabinKarp | 0.3828 | 20 | 1 |
| 100000 | Naive | 0.4866 | 132915 | 1 |
| 100000 | KMP | 0.4893 | 124696 | 1 |
| 100000 | RabinKarp | 0.7630 | 20 | 1 |

### String matching — pattern absent

| Text length | Algorithm | Time (ms) | Char comparisons | Matches |
|---|---|---|---|---|
| 10000 | Naive | 0.1921 | 13308 | 0 |
| 10000 | KMP | 0.1892 | 12455 | 0 |
| 10000 | RabinKarp | 0.2211 | 0 | 0 |
| 50000 | Naive | 0.2103 | 66475 | 0 |
| 50000 | KMP | 0.2207 | 62436 | 0 |
| 50000 | RabinKarp | 0.3780 | 0 | 0 |
| 100000 | Naive | 0.4206 | 133283 | 0 |
| 100000 | KMP | 0.4438 | 125023 | 0 |
| 100000 | RabinKarp | 0.7572 | 0 | 0 |

### String matching — pattern worst-case

| Text length | Algorithm | Time (ms) | Char comparisons | Matches |
|---|---|---|---|---|
| 10000 | Naive | 0.2453 | 199620 | 0 |
| 10000 | KMP | 0.1751 | 20018 | 0 |
| 10000 | RabinKarp | 0.0755 | 0 | 0 |
| 50000 | Naive | 1.2296 | 999620 | 0 |
| 50000 | KMP | 0.1760 | 100018 | 0 |
| 50000 | RabinKarp | 0.3830 | 0 | 0 |
| 100000 | Naive | 2.4751 | 1999620 | 0 |
| 100000 | KMP | 0.3552 | 200018 | 0 |
| 100000 | RabinKarp | 0.7552 | 0 | 0 |

### Kasai's algorithm

| n | Time (ms) |
|---|---|
| 1000 | 0.1392 |
| 5000 | 0.1666 |
| 10000 | 0.3294 |
