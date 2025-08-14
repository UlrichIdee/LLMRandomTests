import java.io.*;
import java.util.*;
import java.nio.file.Paths;

public class Testdesnombres {

    public static void main(String[] args) throws IOException {
        //  forcer l'UTF-8 pour mieux afficher les accents sous Windows.
        try {
            System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
            System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) { /* ignore */ }

       String[] fichiers = {
    Paths.get("data","sequence 1.txt").toString(),
    Paths.get("data","sequence 2.txt").toString(),
    Paths.get("data","sequence 3.txt").toString(),
    Paths.get("data","sequence 4.txt").toString(),
    Paths.get("data","sequence 5.txt").toString()
};

        for (String fichier : fichiers) {
            int[] sequence = chargerSequence(fichier);
            double chi2 = calculerChiCarre(sequence);

            System.out.println();
            System.out.println("Fichier : " + fichier);
            System.out.printf("Resultat du test de frequence (chi2) : %.4f%n", chi2);
            System.out.println("Degre de liberte : 9");
            System.out.println("A comparer avec la valeur critique du chi2 au seuil de 5% : 16.92");

            double valeurCritique = 16.92;
            if (chi2 < valeurCritique) {
                System.out.println("Verdict : Hypothese acceptee (distribution compatible avec l'aleatoire)");
            } else {
                System.out.println("Verdict : Hypothese rejetee (distribution non aleatoire)");
            }

            // Tests 
            System.out.println(testDesRuns(sequence));
            System.out.println(testPoker(sequence));
            System.out.println(testAutocorrelation(sequence, 1)); // lag = 1
            System.out.println(testEquidistribution(sequence, 50)); // B=50 recommande
            System.out.println(testSeries(sequence, 2));            // k=2
            System.out.println(testGapInterval(sequence, 0, 99)); // 10% de l'intervalle
            // Tests par fragments (5 morceaux) pour ce fichier
testerParFragments(sequence, 5, new java.io.File(fichier).getName());


        }

        // ================== Comparaison générateurs ==================
int[] seqR = genRandomSeq(1000, 12345L);
afficherTousLesTests(seqR, "Random(seed=12345)");
testerParFragments(seqR, 5, "Random(seed=12345)");

int[] seqS = genSecureRandomSeq(1000, 98765L);
afficherTousLesTests(seqS, "SecureRandom(SHA1PRNG,seed=98765)");
testerParFragments(seqS, 5, "SecureRandom(SHA1PRNG,seed=98765)");

    }

    // ------------ Chi2 sur 10 classes (centaines) ------------
    public static double calculerChiCarre(int[] sequence) {
        int[] compteurs = new int[10];
        for (int n : sequence) {
            if (n >= 0 && n <= 999) {
                compteurs[n / 100]++;
            }
        }
        int total = sequence.length;
        if (total == 0) return Double.NaN;
        double attendu = total / 10.0;
        double chi2 = 0.0;
        for (int i = 0; i < 10; i++) {
            double ecart = compteurs[i] - attendu;
            chi2 += (ecart * ecart) / attendu;
        }
        return chi2;
    }

    // ------------ Table/approx chi2 critique (alpha = 0.05) ------------
    public static double chiSquareCritical(int df, double alpha) {
        if (alpha != 0.05) alpha = 0.05;
        switch (df) {
            case 1:  return 3.8415;  case 2:  return 5.9915;  case 3:  return 7.8147;
            case 4:  return 9.4877;  case 5:  return 11.0705; case 6:  return 12.5916;
            case 7:  return 14.0671; case 8:  return 15.5073; case 9:  return 16.9189;
            case 10: return 18.3070; case 11: return 19.6751; case 12: return 21.0261;
            case 13: return 22.3620; case 14: return 23.6848; case 15: return 24.9958;
            case 16: return 26.2962; case 17: return 27.5871; case 18: return 28.8693;
            case 19: return 30.1435; case 20: return 31.4104; case 21: return 32.6706;
            case 22: return 33.9245; case 23: return 35.1725; case 24: return 36.4150;
            case 25: return 37.6525; case 26: return 38.8851; case 27: return 40.1133;
            case 28: return 41.3372; case 29: return 42.5570; case 30: return 43.7730;
            default:
                double z = 1.6448536269514722; // 95% une queue
                double a = 1.0 - 2.0/(9.0*df) + z*Math.sqrt(2.0/(9.0*df));
                return df * a*a*a;
        }
    }

    // ------------ Chargement ------------
    public static int[] chargerSequence(String chemin) throws IOException {
        List<Integer> valeurs = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(chemin))) {
            while (sc.hasNext()) {
                if (sc.hasNextInt()) valeurs.add(sc.nextInt());
                else sc.next();
            }
        }
        return valeurs.stream().mapToInt(i -> i).toArray();
    }

    // ------------ Test des runs  ------------
    public static String testDesRuns(int[] sequence) {
        if (sequence.length < 2) {
            return "Sequence trop courte pour le test des runs.";
        }
        int n = sequence.length;
        int runs = 1;
        for (int i = 1; i < n - 1; i++) {
            if ((sequence[i] > sequence[i - 1] && sequence[i] > sequence[i + 1]) ||
                (sequence[i] < sequence[i - 1] && sequence[i] < sequence[i + 1])) {
                runs++;
            }
        }
        double mu = (2 * n - 1) / 3.0;
        double sigma = Math.sqrt((16 * n - 29) / 90.0);
        double z = Math.abs(runs - mu) / sigma;

        StringBuilder sb = new StringBuilder();
        sb.append("Test des runs :\n");
        sb.append("Taille de la sequence : ").append(n).append("\n");
        sb.append("Nombre de runs observes : ").append(runs).append("\n");
        sb.append(String.format("Esperance (mu) : %.2f%n", mu));
        sb.append(String.format("Ecart type (sigma) : %.2f%n", sigma));
        sb.append(String.format("Statistique z : %.2f%n", z));
        sb.append("Seuil critique : 1.96\n");
        if (z < 1.96) sb.append("Verdict : Hypothese acceptee (sequence compatible avec l'alea)\n");
        else sb.append("Verdict : Hypothese rejetee (sequence non aleatoire)\n");
        return sb.toString();
    }

    // ------------ Test Poker  ------------
    public static String testPoker(int[] sequence) {
        int[] categories = new int[3]; // [tous differents, une paire, trois pareils]
        for (int n : sequence) {
            String s = String.format("%03d", n);
            int[] count = new int[10];
            for (char c : s.toCharArray()) count[c - '0']++;
            int max = Arrays.stream(count).max().getAsInt();
            if (max == 3) categories[2]++;
            else if (max == 2) categories[1]++;
            else categories[0]++;
        }
        int total = Arrays.stream(categories).sum();
        if (total == 0) return "Sequence vide ou invalide pour le test de poker.";
        double[] p = {0.72, 0.27, 0.01};
        double chi2 = 0.0;
        for (int i = 0; i < 3; i++) {
            double attendu = total * p[i];
            chi2 += Math.pow(categories[i] - attendu, 2) / attendu;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Test du Poker :\n");
        sb.append("Total analyse : ").append(total).append(" nombres\n");
        sb.append(String.format("Categorie 'tous differents' : %d (attendu ~ %.2f)%n", categories[0], total * p[0]));
        sb.append(String.format("Categorie 'une paire' : %d (attendu ~ %.2f)%n", categories[1], total * p[1]));
        sb.append(String.format("Categorie 'trois pareils' : %d (attendu ~ %.2f)%n", categories[2], total * p[2]));
        sb.append(String.format("Resultat du test chi2 : %.2f%n", chi2));
        sb.append("Valeur critique pour ddl=2 au seuil 5% : 5.99\n");
        if (chi2 < 5.99) sb.append("Verdict : Hypothese acceptee (distribution compatible avec l'alea)\n");
        else sb.append("Verdict : Hypothese rejetee (distribution non aleatoire)\n");
        return sb.toString();
    }

    // ------------ Autocorrelation (lag arbitraire) ------------
    public static String testAutocorrelation(int[] sequence, int lag) {
        int n = sequence.length;
        if (n <= lag) return "Sequence trop courte pour le test d'autocorrelation.";
        double mean = 0.0; for (int x : sequence) mean += x; mean /= n;
        double num = 0.0, denom = 0.0;
        for (int i = 0; i < n - lag; i++) num += (sequence[i] - mean) * (sequence[i + lag] - mean);
        for (int i = 0; i < n; i++) denom += Math.pow(sequence[i] - mean, 2);
        double r = num / denom;
        double z = r * Math.sqrt(n); // approximation
        StringBuilder sb = new StringBuilder();
        sb.append("Test d'autocorrelation (lag = ").append(lag).append(") :\n");
        sb.append(String.format("Coefficient d'autocorrelation r : %.4f%n", r));
        sb.append(String.format("Score z : %.2f%n", z));
        sb.append("Seuil critique |z| < 1.96 pour 5% de signification\n");
        if (Math.abs(z) < 1.96) sb.append("Verdict : Hypothese acceptee (pas d'autocorrelation significative)\n");
        else sb.append("Verdict : Hypothese rejetee (autocorrelation detectee)\n");
        return sb.toString();
    }

    // ------------ Equidistribution ------------
    public static String testEquidistribution(int[] sequence, int B) {
        if (B < 2) B = 2;
        int n = sequence.length;
        int[] counts = new int[B];
        for (int v : sequence) {
            if (v < 0 || v > 999) continue;
            int idx = (int)((long)v * B / 1000); // 0..B-1
            if (idx == B) idx = B - 1;
            counts[idx]++;
        }
        double exp = (double)n / B;
        double chi2 = 0.0;
        for (int c : counts) {
            double d = c - exp; chi2 += (d*d)/exp;
        }
        int ddl = B - 1;
        double crit = chiSquareCritical(ddl, 0.05);
        boolean pass = chi2 <= crit;
        return String.format(
            "Test d'equidistribution (B=%d) :%nChi2 = %.2f | ddl = %d | seuil 5%% = %.2f%nVerdict : %s%n",
            B, chi2, ddl, crit, pass ? "Hypothese acceptee" : "Hypothese rejetee"
        );
    }

    // ------------  Series (k-uples) ------------
    public static String testSeries(int[] sequence, int k) {
        if (k < 2) k = 2;
        int n = sequence.length;
        int tuples = n - k + 1;
        if (tuples <= 0) return "Test des series (k="+k+") : sequence trop courte.\n";

        // Choisir m pour attendu >= 5
        int m = 0;
        for (int cand = 10; cand >= 2; cand--) {
            double cells = Math.pow(cand, k);
            double exp = tuples / cells;
            if (exp >= 5.0) { m = cand; break; }
        }
        if (m == 0) return "Test des series (k="+k+") : attendus insuffisants.\n";

        int cells = 1; for (int i=0;i<k;i++) cells *= m;
        int[] freq = new int[cells];

        for (int i = 0; i <= n - k; i++) {
            int idx = 0;
            for (int j = 0; j < k; j++) {
                int v = sequence[i + j];
                if (v < 0) v = 0; if (v > 999) v = 999;
                int c = (int)((long)v * m / 1000);
                if (c == m) c = m - 1;
                idx = idx * m + c;
            }
            freq[idx]++;
        }

        double exp = (double)tuples / cells;
        double chi2 = 0.0;
        for (int f : freq) { double d = f - exp; chi2 += (d*d)/exp; }

        int ddl = cells - 1;
        double crit = chiSquareCritical(ddl, 0.05);
        boolean pass = chi2 <= crit;

        return String.format(
            "Test des series (k=%d, m=%d, cellules=%d) :%nChi2 = %.2f | ddl = %d | seuil 5%% = %.2f%nVerdict : %s%n",
            k, m, cells, chi2, ddl, crit, pass ? "Hypothese acceptee" : "Hypothese rejetee"
        );
    }

        // ------------  Gap  ------------
    public static String testGapInterval(int[] sequence, int lo, int hi) {
    int n = sequence.length;
    // p empirique = proportion de valeurs dans l'intervalle
    double p = 0.0;
    for (int v : sequence) if (v>=lo && v<=hi) p += 1.0;
    p /= n;
    if (p <= 0.0) return "Gap intervalle ["+lo+".."+hi+"] : aucune occurrence.\n";

    // gaps entre hits successifs
    List<Integer> gaps = new ArrayList<>();
    int gap = 0; boolean seen = false;
    for (int v : sequence) {
        boolean hit = (v>=lo && v<=hi);
        if (hit) { if (seen) gaps.add(gap); gap = 0; seen = true; }
        else if (seen) gap++;
    }
    int N = gaps.size();
    if (N == 0) return "Gap intervalle ["+lo+".."+hi+"] : occurrences insuffisantes.\n";

    // Regroupement 0..K-1 et K+ avec attendus >= 5
    int K = 0;
    while (K < 1000) {
        double expK = N * p * Math.pow(1.0 - p, K);
        if (expK < 5.0) break;
        K++;
    }
    if (K==0) K=1;

    int bins = K + 1;
    int[] obs = new int[bins];
    for (int g : gaps) { if (g<K) obs[g]++; else obs[K]++; }

    double chi2 = 0.0;
    for (int k=0;k<K;k++) {
        double ex = N * p * Math.pow(1.0 - p, k);
        if (ex > 0) { double d = obs[k] - ex; chi2 += (d*d)/ex; }
    }
    double tailExp = N * Math.pow(1.0 - p, K);
    if (tailExp > 0) { double d = obs[K] - tailExp; chi2 += (d*d)/tailExp; }

    int ddl = bins - 1;
    double crit = chiSquareCritical(ddl, 0.05);
    boolean pass = chi2 <= crit;

    return String.format(
        "Gap intervalle [%d..%d] :%nChi2 = %.2f | bacs = %d | ddl = %d | seuil 5%% = %.2f%nVerdict : %s%n",
        lo, hi, chi2, bins, ddl, crit, pass ? "Hypothese acceptee" : "Hypothese rejetee"
    );
}

// Affiche tous les tests déjà présents sur une séquence donnée
public static void afficherTousLesTests(int[] sequence, String label) {
    if (label != null && !label.isEmpty()) {
        System.out.println("\n=== " + label + " ===");
    }
    System.out.println(testDesRuns(sequence));
    System.out.println(testPoker(sequence));
    System.out.println(testAutocorrelation(sequence, 1));
    System.out.println(testEquidistribution(sequence, 50));
    System.out.println(testSeries(sequence, 2));
    System.out.println(testGapInterval(sequence, 0, 99));
}

// Découpe en 'parts' fragments et lance afficherTousLesTests sur chacun
public static void testerParFragments(int[] sequence, int parts, String prefix) {
    int n = sequence.length;
    int chunk = n / parts;
    for (int i = 0; i < parts; i++) {
        int start = i * chunk;
        int end = (i == parts - 1) ? n : (i + 1) * chunk;
        int[] frag = java.util.Arrays.copyOfRange(sequence, start, end);
        afficherTousLesTests(frag, (prefix == null ? "frag" : prefix) + "_frag" + (i+1));
    }
}

public static int[] genRandomSeq(int n, long seed) {
    java.util.Random rnd = new java.util.Random(seed);
    int[] a = new int[n];
    for (int i = 0; i < n; i++) a[i] = rnd.nextInt(1000);
    return a;
}

public static int[] genSecureRandomSeq(int n, long seed) {
    try {
        java.security.SecureRandom sr = java.security.SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = sr.nextInt(1000);
        return a;
    } catch (Exception e) {
        java.security.SecureRandom sr = new java.security.SecureRandom();
        sr.setSeed(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = sr.nextInt(1000);
        return a;
    }
}


}
