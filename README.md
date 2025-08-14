# LLMRandomTests

Java program to evaluate pseudo-random number sequences using classical statistical tests.

## What it does

For integer sequences in **[0, 999]**, the program runs:
- **Frequency (Chi-square)** on 10 classes (hundreds).
- **Runs test** (peaks/valleys version).
- **Poker test** on 3-digit patterns (all different / one pair / three of a kind).
- **Autocorrelation** (lag = 1).
- **Equidistribution** over B bins (default B=50).
- **Series test (k-tuples)** with k=2 (auto-chooses m so expected count >= 5).
- **Gap test** on an interval (default [0..99]).

It prints statistics and **PASS/FAIL** verdicts at **alpha = 5%**.
It also tests sequences split into **5 fragments** (200 values each) and compares with:
`Random(seed=12345)` and `SecureRandom(SHA1PRNG, seed=98765)`.

## Requirements
- Java JDK **8+**
- A terminal (Windows PowerShell / cmd, macOS, or Linux)

## Data format
Put your files in `./data/` as plain text. Each token should be an integer in **[0, 999]**
(separated by spaces or newlines). Example: `0 17 302 999 518 ...`

## Build & Run

**Windows (PowerShell):**
```powershell
javac -d out src\Testdesnombres.java
java -cp out Testdesnombres

macOS/Linux:

javac -d out src/Testdesnombres.java
java -cp out Testdesnombres

The program will:

    Load the 5 sequences from ./data/sequence 1.txt … sequence 5.txt

    Run all tests on each full sequence

    Run the same tests on 5 fragments per sequence

    Generate and test Random(seed=12345) and SecureRandom(SHA1PRNG, seed=98765)

Customize

Open src/Testdesnombres.java and adjust:

    Equidistribution bins: testEquidistribution(sequence, 50)

    Series k-tuples: testSeries(sequence, 2)

    Gap interval: testGapInterval(sequence, 0, 99)

    Seeds: genRandomSeq(...), genSecureRandomSeq(...)

    Fragment count: testerParFragments(sequence, 5, ...)

Notes

With alpha = 5% and many tests (including fragments), occasional false positives
(rejections) are expected. Focus on results over full sequences first.
License

MIT
Author

Ulrich Idée — UQO
