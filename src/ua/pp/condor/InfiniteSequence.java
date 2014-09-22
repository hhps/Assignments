package ua.pp.condor;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * We have infinite sequence of positive integers, concatenated together:
 * S = 123456789101112131415...
 * Define the first occurrence of the specified subsequence A in S (numbering starts at 1).
 *
 * Input:
 * 6789
 * 101
 *
 * Output:
 * 6
 * 10
 */
public class InfiniteSequence {

    private static final int C_1_9 = '1' - '9';

    private int minK;
    private int minN;
    private String number;

    private static int calculateMinLength(final char[] subsequence, final int startPoint, final int maxN) {
        final int length = subsequence.length;
        int n = 1;  //length of current number
        int d = 0;  //difference between current number and first number
        int x = startPoint; //save startPoint for modulo
        while (n <= maxN) {
            d = 0;
            int m = 0;
            int i;
            loop: for (i = startPoint; i + n < length;) {
                int difference = subsequence[i + n] - subsequence[i];
                int modulo = (i + 1 - x - m) % n;
                if (difference == 1) {
                    if (modulo == 0) {
                        i++;
                    } else {
                        int numberOf9 = n - modulo;
                        for (int j = 1; j <= numberOf9; j++) {
                            if (subsequence[i + j] != '9'
                                    || (i + n + j < length && subsequence[i + n + j] != '0')) {
                                break loop;
                            }
                        }
                        i += numberOf9 + 1;
                    }
                } else if (difference == 0 && modulo != 0) {
                    if (subsequence[i] == '9' && modulo == 1) {
                        boolean all9 = true;
                        for (int j = i + 1; j < length && j < i + n; j++) {
                            if (subsequence[j] != '9') {
                                all9 = false;
                                break;
                            }
                        }
                        if (all9) {
                            break;
                        }
                    }
                    i++;
                } else if (difference == C_1_9 && (n == 1 || modulo == 1)) {
                    int numberOf9 = n == 1 ? 0 : n - modulo;
                    for (int j = 1; j <= numberOf9; j++) {
                        if (subsequence[i + j] != '9'
                                || (i + n + j < length && subsequence[i + n + j] != '0')) {
                            break loop;
                        }
                    }
                    int last = i + n + numberOf9 + 1;
                    if (last >= length || subsequence[last] == '0') {
                        i += numberOf9 + 1;
                        n++;
                        d++;
                        m = i;
                        x = 0;
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            if (i + n >= length) {
                break;
            }
            n++;
        }
        return n - d;
    }

    private static boolean check(char[] subsequence, int k, int n) {
        if (subsequence[k - 1 + n] - subsequence[k - 1] == 1) { //check difference between last digits
            for (int i = 0; i < k - 1; i++) {   //check remaining digits
                if (subsequence[i] != subsequence[i + n]) {
                    return false;
                }
            }
            return true;
        }
        int zeroCount = 0;
        for (int i = k + n - 1; i > k; i--) {
            if (subsequence[i] == '0') {
                zeroCount++;
            } else {
                break;
            }
        }
        if (zeroCount > 0) { //check skip like 99-100
            int i;
            for (i = k - 1; i >= 0 && zeroCount > 0; i--, zeroCount--) {
                if (subsequence[i] != '9') {
                    return false;
                }
            }
            if (i < 0) {
                return true;
            }
            if (subsequence[i + n] - subsequence[i] == 1) {
                for (i--; i >= 0; i--) {
                    if (subsequence[i] != subsequence[i + n]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    private String parseNumber(String input) {
        char[] subsequence = input.toCharArray();
        int length = subsequence.length;

        minK = 0;
        minN = calculateMinLength(subsequence, 0, length);
        for (int k = 1; k < minN; k++) {
            int n = calculateMinLength(subsequence, k, minN);
            if (n <= k) {
                break;
            }
            if (n <= minN) {
                if (check(subsequence, k, n)) {
                    if (n < minN) {
                        minK = k;
                        minN = n;
                    }
                }
            }
        }
        int dlm1 = (minN << 1) - 1;
        if (minN < length && length < dlm1) {
            if (minK + minN < length) {
                int beginIdx = length - minN;
                int endIdx = beginIdx + dlm1 - length;
                String extendedInput = input + input.substring(beginIdx, endIdx);
                BigInteger bi = new BigInteger(extendedInput);
                bi = bi.add(BigInteger.ONE);
                return parseNumber(bi.toString());
            } else if (subsequence[minK] == '9') {
                String extendedInput = input.substring(0, minK + 1);
                extendedInput = input.substring(0, length - extendedInput.length() + 1) + extendedInput;
                BigInteger bi = new BigInteger(extendedInput);
                bi = bi.add(BigInteger.ONE);
                return parseNumber(bi.toString());
            }
        }
        if (minN == length) {
            char minC = Character.MAX_VALUE;
            for (int k = 0; k < length; k++) {
                if (subsequence[k] < minC && subsequence[k] > '0') {
                    minC = subsequence[k];
                    minK = k;
                }
            }
            if (minK != 0) {
                String result = input.substring(minK) + input.substring(0, minK);
                minK -= input.length();
                return result;
            }
        }

        return input.substring(minK, minK + minN);
    }

    /**
     * D(n) = (n + 1) * d - (10^d - 1) / 9
     * d = length(n)
     *
     * See http://mathworld.wolfram.com/ConsecutiveNumberSequences.html
     *
     * I use {@link java.math.BigInteger} because it may support very long sequences.
     * This method may be improved for short {@link #number}.
     */
    private String getPosition(boolean allZero) {
        BigInteger n = new BigInteger(number);
        n = n.add(BigInteger.ONE);
        BigInteger d = BigInteger.valueOf(minN);
        n = n.multiply(d);

        BigInteger x = BigInteger.valueOf(10);
        x = x.pow(minN);
        x = x.subtract(BigInteger.ONE);
        x = x.divide(BigInteger.valueOf(9));

        n = n.subtract(x).subtract(d).add(BigInteger.ONE).subtract(BigInteger.valueOf(minK));
        if (allZero) {
            n = n.add(BigInteger.ONE);
        }
        return n.toString();
    }

    public String calculate(String str) {
        int length = str.length();
        if (length == 1 && !"0".equals(str)) {
            return str;
        }

        boolean allZero = true;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0') {
                allZero = false;
                break;
            }
        }
        if (allZero) {
            str = '1' + str;
        }

        number = parseNumber(str);
        return getPosition(allZero);
    }

    public int getMinK() {
        return minK;
    }

    public int getMinN() {
        return minN;
    }

    public String getNumber() {
        return number;
    }

    /**
     * Use {@code java InfiniteSequence test-filename} for testing.
     */
    public static void main(String[] args) throws Exception {
        InfiniteSequence is = new InfiniteSequence();
        PrintWriter pw = new PrintWriter(System.out);
        Scanner sc;
        if (args.length > 0) {
            sc = new Scanner(new File(args[0]));
        } else {
            sc = new Scanner(System.in);
        }
        while (sc.hasNextLine()) {
            String input = sc.nextLine();
            if (input.isEmpty()) {
                break;
            }
            String result = is.calculate(input);
            if (args.length > 0) {
                String answer = sc.nextLine();
                boolean boolAnswer = answer.equals(result);
                pw.printf("%b, input: %s, output: %s, number: %s, n: %d, k: %d%n",
                        boolAnswer, input, result, is.getNumber(), is.getMinN(), is.getMinK());
            } else {
                pw.println(result);
                pw.printf("input: %s, output: %s, number: %s, n: %d, k: %d%n",
                        input, result, is.getNumber(), is.getMinN(), is.getMinK());
                pw.flush();
            }
        }
        pw.flush();
    }
}
