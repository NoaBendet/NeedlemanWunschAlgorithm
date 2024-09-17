/*
 * Needleman - Wunsch algorithm for sequences alignment
 */
public class Main {
    public static void main(String[] args) {
        // Determines the gap penalty
        int gap_penalty = Integer.parseInt(args[0]);
        if (gap_penalty >= 0)
            throw new IllegalArgumentException("Enter negative number");
        int misMatch_score = Integer.parseInt(args[1]);
        if (misMatch_score >= 0)
            throw new IllegalArgumentException("Enter negative number");
        int rap = Integer.parseInt(args[2]);
        if (rap <= 0)
            throw new IllegalArgumentException("Enter positive number");
        int size = Integer.parseInt(args[3]);
        if (size <= 0)
            throw new IllegalArgumentException("Enter positive number");

        experiment(gap_penalty, misMatch_score, rap, size);

        // Randomize an alignment
        randomAlignment(gap_penalty, misMatch_score, size);
    }

    /**
     * Initializing the first row and column of the matrix with the arithmetic
     * progression of gap penalties
     * 
     * @param matrix      - Matrix for the computed values
     * @param gap_penalty - Value of the gap penalty from the user
     */
    public static void initialization(int[][] matrix, int gap_penalty) {
        for (int i = 0; i < matrix[0].length; i++)
            matrix[0][i] = i * gap_penalty;
        for (int i = 0; i < matrix.length; i++)
            matrix[i][0] = i * gap_penalty;
    }

    /**
     * Fills up the matrix according to the Needleman-Wunsch algorithm
     * 
     * @param matrix         - Matrix for the computed values
     * @param target         - First String for alignment
     * @param query          - Second String for alignment
     * @param gap_penalty    - Value of the gap penalty from the user
     * @param misMatch_score - Value of the misMatch score from the user
     */
    public static void matrixFilling(int[][] matrix, String target, String query, int gap_penalty, int misMatch_score) {
        int left, up, diagonal; // The possible penalty scores
        int n = Math.min(matrix.length, matrix[0].length);

        for (int i = 1; i < n; i++) {
            // Computes the row values (takes the max of left, up, and diagonal)
            for (int j = i; j < matrix[0].length; j++) {
                left = matrix[i][j - 1] + gap_penalty;
                up = matrix[i - 1][j] + gap_penalty;
                diagonal = matrix[i - 1][j - 1];

                // Match or Mismatch case
                if (target.charAt(j - 1) == query.charAt(i - 1)) {
                    diagonal -= misMatch_score; // Subtract mismatch score if there is a match
                } else {
                    diagonal += misMatch_score; // Add mismatch score otherwise
                }

                matrix[i][j] = Math.max(Math.max(left, up), diagonal);
            }

            // Computes the column values (takes the max of left, up, and diagonal)
            for (int j = 1 + i; j < matrix.length; j++) {
                left = matrix[j][i - 1] + gap_penalty;
                up = matrix[j - 1][i] + gap_penalty;
                diagonal = matrix[j - 1][i - 1];

                // Match or Mismatch case
                if (target.charAt(i - 1) == query.charAt(j - 1)) {
                    diagonal -= misMatch_score; // Subtract mismatch score if there is a match
                } else {
                    diagonal += misMatch_score; // Add mismatch score otherwise
                }

                matrix[j][i] = Math.max(Math.max(left, up), diagonal);
            }
        }
    }

    /**
     * Computes the matrix of Match/misMatch scores
     * 
     * @param target         - First String for alignment
     * @param query          - Second String for alignment
     * @param misMatch_score - Value of the misMatch score from the user
     * @return a matrix of the Match/misMatch scores
     */
    public static int[][] match(String target, String query, int misMatch_score) {
        int[][] match = new int[query.length()][target.length()];
        for (int i = 0; i < match.length; i++) {
            for (int j = 0; j < match[0].length; j++) {
                // Match
                if (target.charAt(j) == query.charAt(i))
                    match[i][j] = -misMatch_score;
                // Mismatch
                else
                    match[i][j] = misMatch_score;
            }
        }
        return match;
    }

    /**
     * Computes the alignment score and returns it
     * 
     * @param matrix         - Matrix for the computed values
     * @param target         - First String for alignment
     * @param query          - Second String for alignment
     * @param gap_penalty    - Value of the gap penalty from the user
     * @param misMatch_score - Value of the misMatch score from the user
     * @return the score of the alignment
     */
    public static int tracebackScore(int[][] matrix, String target, String query, int gap_penalty, int misMatch_score) {
        int score = 0;
        String s1 = "";
        String s2 = "";
        String s3 = "";
        int j = target.length();
        int i = query.length();
        int[][] match = match(target, query, misMatch_score);

        // Start traceback loop
        while (i > 0 || j > 0) {
            // Match or Mismatch case
            if (i > 0 && j > 0 && matrix[i][j] == (matrix[i - 1][j - 1] + match[i - 1][j - 1])) {
                s1 = target.charAt(j - 1) + s1;
                s2 = query.charAt(i - 1) + s2;

                if (target.charAt(j - 1) == query.charAt(i - 1)) { // Match
                    s3 = "|" + s3;
                    score -= misMatch_score;
                } else { // Mismatch
                    s3 = " " + s3;
                    score += misMatch_score;
                }
                i--;
                j--;

            } else if (j > 0 && matrix[i][j] == matrix[i][j - 1] + gap_penalty) {
                // Gap in the query String s2
                s1 = target.charAt(j - 1) + s1;
                s2 = "-" + s2;
                s3 = " " + s3;
                score += gap_penalty;
                j--;
            } else {
                // Gap in the target String s1
                s1 = "-" + s1;
                s3 = " " + s3;
                s2 = query.charAt(i - 1) + s2;
                score += gap_penalty;
                i--;
            }
        }

        return score; // Returns the score value
    }

    /**
     * The experiment
     * 
     * @param gap_penalty    - Value of the gap penalty from the user
     * @param misMatch_score - Value of the misMatch score from the user
     * @param rap            - Number of iterations from the user
     * @param size           - Size of Strings from the user
     */
    public static void experiment(int gap_penalty, int misMatch_score, int rap, int size) {
        // Array for counting the occurrence of the score value in the iterations
        int[] count = new int[-(gap_penalty + misMatch_score) * size + 1];
        System.out.println("\nStrings Lengths: " + size + "\n");
        for (int k = 0; k < rap; k++) {
            String target = ""; // First String for alignment
            String query = ""; // Second String for alignment
            for (int i = 0; i < size; i++) {
                double random1 = Math.random();
                double random2 = Math.random();
                if (random1 < 0.25)
                    target += 'A';
                else if (random1 < 0.5)
                    target += 'C';
                else if (random1 < 0.75)
                    target += 'T';
                else
                    target += 'G';
                if (random2 < 0.25)
                    query += 'A';
                else if (random2 < 0.5)
                    query += 'C';
                else if (random2 < 0.75)
                    query += 'T';
                else
                    query += 'G';
            }
            int[][] matrix = new int[query.length() + 1][target.length() + 1];
            initialization(matrix, gap_penalty);
            matrixFilling(matrix, target, query, gap_penalty, misMatch_score);
            // The score value of the alignment
            int score = tracebackScore(matrix, target, query, gap_penalty, misMatch_score);
            // Updates the counter score array
            count[score + (-gap_penalty * size)]++;
        }
        System.out.println("Histogram:");
        for (int m = 0; m < count.length; m++) {
            System.out.println("Score " + (m + (gap_penalty * size)) + " appears " + count[m] + " times");
        }
        System.out.println();
    }

    /**
     * Computes the alignment score by the process of traceback and prints the
     * alignment and score.
     * 
     * @param matrix         - Matrix for the computed values
     * @param target         - First String for alignment
     * @param query          - Second String for alignment
     * @param gap_penalty    - Value of the gap penalty from the user
     * @param misMatch_score - Value of the misMatch score from the user
     */
    public static void traceback(int[][] matrix, String target, String query, int gap_penalty, int misMatch_score) {
        int score = 0;
        String s1 = "";
        String s2 = "";
        String s3 = "";
        int j = target.length();
        int i = query.length();
        int[][] match = match(target, query, misMatch_score);

        // Runs the traceback for the right bottom cell
        while (i > 0 || j > 0) {
            // Checks for Match/MisMatch according to the Needleman-Wunsch algorithm (by
            // dynamic programming)
            if (i > 0 && j > 0 && matrix[i][j] == (matrix[i - 1][j - 1] + match[i - 1][j - 1])) {
                s1 = target.charAt(j - 1) + s1;
                s2 = query.charAt(i - 1) + s2;

                // Match
                if (target.charAt(j - 1) == query.charAt(i - 1)) {
                    s3 = "|" + s3;
                    score -= misMatch_score;
                }
                // Mismatch
                else {
                    s3 = " " + s3;
                    score += misMatch_score;
                }
                i--;
                j--;
            }
            // Checks for gaps in the query String s2
            else if (j > 0 && matrix[i][j] == matrix[i][j - 1] + gap_penalty) {
                s1 = target.charAt(j - 1) + s1;
                s2 = "-" + s2;
                s3 = " " + s3;
                score += gap_penalty;
                j--;
            }
            // Checks for gaps in the target String s1
            else {
                s1 = "-" + s1;
                s3 = " " + s3;
                s2 = query.charAt(i - 1) + s2;
                score += gap_penalty;
                i--;
            }
        }

        // Prints the alignment and the score
        System.out.println("Alignment:\n");
        System.out.println(s1);
        System.out.println(s3);
        System.out.println(s2);
        System.out.println("\nScore: " + score);
    }

    /**
     * Demonstrates a random alignment in 6 length
     * 
     * @param gap_penalty    - Value of the gap penalty from the user
     * @param misMatch_score - Value of the misMatch score from the user
     * @param size           - Size of strings from the user
     */
    public static void randomAlignment(int gap_penalty, int misMatch_score, int size) {
        String target = "";
        String query = "";
        for (int i = 0; i < size; i++) {
            double random1 = Math.random();
            double random2 = Math.random();
            if (random1 < 0.25)
                target += 'A';
            else if (random1 < 0.5)
                target += 'C';
            else if (random1 < 0.75)
                target += 'T';
            else
                target += 'G';
            if (random2 < 0.25)
                query += 'A';
            else if (random2 < 0.5)
                query += 'C';
            else if (random2 < 0.75)
                query += 'T';
            else
                query += 'G';
        }
        // Constructs the matrix of values accordingly to the algorithm
        int[][] matrix = new int[query.length() + 1][target.length() + 1];
        // Needleman-Wunsch algorithm
        initialization(matrix, gap_penalty);
        matrixFilling(matrix, target, query, gap_penalty, misMatch_score);
        System.out.println("Random alignment in length of " + size + "\n");
        System.out.println("Matrix of values according to the algorithm:\n");
        print(matrix);
        System.out.println("Matrix of Match/misMatch scores:\n");
        print(match(target, query, misMatch_score));
        traceback(matrix, target, query, gap_penalty, misMatch_score);
    }

    /**
     * Prints a given 2D array
     * 
     * @param matrix - the 2D array to print
     */
    public static void print(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++)
                System.out.print(matrix[i][j] + " ");
            System.out.println();
        }
        System.out.println();
    }
}