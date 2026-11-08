"""Reads results/results.csv and produces plots/*.png and results/tables.md (markdown tables for the README)."""
import csv
import collections
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

rows = list(csv.DictReader(open("results/results.csv")))
for r in rows:
    r["size"] = int(r["size"]); r["avg_ms"] = float(r["avg_ms"]); r["operations"] = int(r["operations"])

def series(exp, algo, scenario=None):
    pts = sorted((r["size"], r["avg_ms"]) for r in rows
                 if r["experiment"] == exp and r["algorithm"] == algo and (scenario is None or r["scenario"] == scenario))
    return [p[0] for p in pts], [p[1] for p in pts]

# Plot 1: LCS vs Edit Distance
plt.figure(figsize=(6, 4))
for algo in ("LCS", "EditDistance"):
    x, y = series(algo, algo)
    plt.plot(x, y, marker="o", label=algo)
plt.xlabel("String length n (both strings)"); plt.ylabel("Avg time (ms)")
plt.title("DP algorithms: time vs input size"); plt.grid(alpha=.3); plt.legend()
plt.tight_layout(); plt.savefig("plots/dp_time.png", dpi=150); plt.close()

# Plot 2: string matching (one subplot per scenario)
fig, axes = plt.subplots(1, 3, figsize=(14, 4))
for ax, sc in zip(axes, ("present", "absent", "worst-case")):
    for algo in ("Naive", "KMP", "RabinKarp"):
        x, y = series("Matching", algo, sc)
        ax.plot(x, y, marker="o", label=algo)
    ax.set_title(f"Pattern: {sc}"); ax.set_xlabel("Text length n"); ax.set_ylabel("Avg time (ms)")
    ax.grid(alpha=.3); ax.legend()
plt.tight_layout(); plt.savefig("plots/matching_time.png", dpi=150); plt.close()

# Plot 3: Kasai
plt.figure(figsize=(6, 4))
x, y = series("Kasai", "Kasai")
plt.plot(x, y, marker="o")
plt.xlabel("String length n"); plt.ylabel("Avg time (ms)"); plt.title("Kasai LCP construction: time vs n")
plt.grid(alpha=.3); plt.tight_layout(); plt.savefig("plots/kasai_time.png", dpi=150); plt.close()

# Markdown tables
out = []
out.append("### DP algorithms\n\n| n | LCS time (ms) | Edit Distance time (ms) | DP cells | LCS length | Edit distance |\n|---|---|---|---|---|---|")
lcs = {r["size"]: r for r in rows if r["experiment"] == "LCS"}
ed = {r["size"]: r for r in rows if r["experiment"] == "EditDistance"}
for n in sorted(lcs):
    out.append(f"| {n} | {lcs[n]['avg_ms']:.3f} | {ed[n]['avg_ms']:.3f} | {lcs[n]['operations']} | {lcs[n]['matches']} | {ed[n]['matches']} |")
for sc in ("present", "absent", "worst-case"):
    out.append(f"\n### String matching — pattern {sc}\n\n| Text length | Algorithm | Time (ms) | Char comparisons | Matches |\n|---|---|---|---|---|")
    for r in sorted((r for r in rows if r["experiment"] == "Matching" and r["scenario"] == sc),
                    key=lambda r: (r["size"], ["Naive", "KMP", "RabinKarp"].index(r["algorithm"]))):
        out.append(f"| {r['size']} | {r['algorithm']} | {r['avg_ms']:.4f} | {r['operations']} | {r['matches']} |")
out.append("\n### Kasai's algorithm\n\n| n | Time (ms) |\n|---|---|")
for r in sorted((r for r in rows if r["experiment"] == "Kasai"), key=lambda r: r["size"]):
    out.append(f"| {r['size']} | {r['avg_ms']:.4f} |")
open("results/tables.md", "w").write("\n".join(out) + "\n")
print("ok")
