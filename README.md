# LLMRandomTests

Java program to evaluate pseudo-random number sequences using classical statistical tests.

## What it does

For integer sequences in **[0, 999]**, the program runs:
- **Frequency (Chi-square)** on 10 classes (hundreds)
- **Runs test** (peaks/valleys)
- **Poker test** (3-digit patterns)
- **Autocorrelation** (lag=1)
- **Equidistribution** over B bins (default B=50)
- **Series test (k-tuples)** with k=2 (auto-chooses m so expected count ≥ 5)
- **Gap test** on an interval (default [0..99])

It prints statistics and **PASS/FAIL** verdicts at **alpha = 5%**.
It also splits each sequence into **5 fragments** (200 values each) and tests two synthetic sources:
`Random(seed=12345)` and `SecureRandom(SHA1PRNG, seed=98765)`.

## Requirements
- Java JDK **8+**
- A terminal (Windows PowerShell / cmd)

## Project layout

LLMRandomTests/
├─ data/
│ ├─ sequence 1.txt
│ ├─ sequence 2.txt
│ ├─ sequence 3.txt
│ ├─ sequence 4.txt
│ └─ sequence 5.txt
└─ src/
└─ Testdesnombres.java


## Build & Run
From the project root:
```powershell
javac -d out src/Testdesnombres.java
java -cp out Testdesnombres

Notes

    With alpha = 5% and many tests (including fragments), occasional false positives (rejections) are expected.

    Focus on global results on full sequences before drawing conclusions.

License

MIT — see LICENSE.
Author

Ulrich Idée — UQO
