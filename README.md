# LLMRandomTests

Java program to evaluate pseudo-random number sequences using classical statistical tests.

## What it does
For integer sequences in [0, 999], the program runs:
- Frequency (Chi-square) on 10 classes (hundreds)
- Runs test (peaks/valleys version)
- Poker test (3-digit patterns)
- Autocorrelation (lag = 1)
- Equidistribution over B bins (default B=50)
- Series test (k-tuples) with k=2
- Gap test on an interval (default [0..99])

Alpha = 5% with PASS/FAIL verdicts. Each input sequence is also split into 5 fragments (200 values each).
The tool can also generate `Random(seed=12345)` and `SecureRandom(SHA1PRNG, seed=98765)` sequences for comparison.

## Requirements
- Java JDK 8+
- Terminal (Windows PowerShell / cmd)

## Build & Run
```powershell
javac -d out src\Testdesnombres.java
java -cp out Testdesnombres

Project layout

LLMRandomTests/
├─ data/
│  ├─ sequence 1.txt
│  ├─ sequence 2.txt
│  ├─ sequence 3.txt
│  ├─ sequence 4.txt
│  └─ sequence 5.txt
└─ src/
   └─ Testdesnombres.java

Configuration

Edit src/Testdesnombres.java to tune: equidistribution bins, series k, gap interval, seeds, and fragment count.
License

MIT (add a LICENSE file if needed).
