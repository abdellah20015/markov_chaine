import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

public class MarkovChain extends JFrame {
    private JTextField orderField;
    private JButton createMatrixButton;
    private JPanel matrixPanel;
    private JButton calculateButton;
    private JTextArea resultArea;
    private JTextField[][] matrixFields;
    private double[][] transitionMatrix;

    public MarkovChain() {
        setTitle("Chaîne de Markov");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        orderField = new JTextField(5);
        createMatrixButton = new JButton("Créer Matrice");
        topPanel.add(new JLabel("Ordre de la matrice:"));
        topPanel.add(orderField);
        topPanel.add(createMatrixButton);
        add(topPanel, BorderLayout.NORTH);

        matrixPanel = new JPanel();
        add(new JScrollPane(matrixPanel), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        calculateButton = new JButton("Calculer");
        bottomPanel.add(calculateButton, BorderLayout.NORTH);
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        bottomPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        createMatrixButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createMatrix();
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateMarkovChain();
            }
        });
    }

    private void createMatrix() {
        int order = Integer.parseInt(orderField.getText());
        matrixPanel.removeAll();
        matrixPanel.setLayout(new GridLayout(order, order));
        matrixFields = new JTextField[order][order];

        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                matrixFields[i][j] = new JTextField(5);
                matrixPanel.add(matrixFields[i][j]);
            }
        }

        matrixPanel.revalidate();
        matrixPanel.repaint();
    }

    private void calculateMarkovChain() {
        int order = matrixFields.length;
        transitionMatrix = new double[order][order];

        for (int i = 0; i < order; i++) {
            for (int j = 0; j < order; j++) {
                transitionMatrix[i][j] = Double.parseDouble(matrixFields[i][j].getText());
            }
        }

        

        StringBuilder result = new StringBuilder();

        // Règle 2: Matrice de transition
        result.append("Matrice de transition:\n");
        result.append(matrixToString(transitionMatrix)).append("\n\n");

        // Règle 3: Vérification des propriétés de la matrice de transition
        boolean isValid = checkTransitionMatrix(transitionMatrix);
        result.append("La matrice est " + (isValid ? "valide" : "invalide") + "\n\n");

        // Règle 5: Classification des états
        Map<String, Set<Integer>> states = identifyStates(transitionMatrix);
        result.append("États transitoires: ").append(states.get("transient")).append("\n");
        result.append("États récurrents: ").append(states.get("recurrent")).append("\n");
        result.append("États absorbants: ").append(states.get("absorbing")).append("\n\n");

        // Règle 6: Périodicité
        result.append("Périodicité des états:\n");
        for (int i = 0; i < order; i++) {
            result.append("État ").append(i).append(": période ").append(calculatePeriod(transitionMatrix, i)).append("\n");
        }
        result.append("\n");

        // Règle 7: Classes de communication
        List<Set<Integer>> communicationClasses = findCommunicationClasses(transitionMatrix);
        result.append("Classes de communication:\n");
        for (int i = 0; i < communicationClasses.size(); i++) {
            result.append("Classe ").append(i + 1).append(": ").append(communicationClasses.get(i)).append("\n");
        }
        result.append("\n");

        // Règle 8: Irréductibilité
        boolean isIrreducible = communicationClasses.size() == 1;
        result.append("La chaîne est " + (isIrreducible ? "irréductible" : "réductible") + "\n\n");

        // Règle 9: Ergodicité
        boolean isErgodic = isIrreducible && isAperiodic(transitionMatrix);
        result.append("La chaîne est " + (isErgodic ? "ergodique" : "non ergodique") + "\n\n");

        // Règle 10: Distribution stationnaire
        if (isErgodic) {
            double[] stationaryDistribution = calculateStationaryDistribution(transitionMatrix);
            result.append("Distribution stationnaire:\n");
            for (int i = 0; i < stationaryDistribution.length; i++) {
                result.append("π").append(i).append(" = ").append(String.format("%.4f", stationaryDistribution[i])).append("\n");
            }
            result.append("\n");
        }

        // Règle 4: Équation de Chapman-Kolmogorov
        int steps = 10;
        result.append("Probabilités après ").append(steps).append(" pas:\n");
        double[][] probMatrix = calculateProbabilityMatrix(transitionMatrix, steps);
        result.append(matrixToString(probMatrix)).append("\n\n");

        // Règle 12: Temps de premier passage
        result.append("Temps moyens de premier passage:\n");
        double[][] mfpt = calculateMeanFirstPassageTime(transitionMatrix);
        result.append(matrixToString(mfpt)).append("\n\n");

        // Règle 14: Processus de naissance et de mort
        result.append("Exemple de processus de naissance et de mort (n = 5):\n");
        double[][] birthDeathMatrix = generateBirthDeathMatrix(5);
        result.append(matrixToString(birthDeathMatrix)).append("\n\n");

        // Règle 15: Processus de Poisson
        result.append("Processus de Poisson (lambda = 2, t = 1, jusqu'à n = 5):\n");
        double[] poissonProb = calculatePoissonProbabilities(2, 1, 5);
        for (int i = 0; i < poissonProb.length; i++) {
            result.append("P(").append(i).append(") = ").append(String.format("%.4f", poissonProb[i])).append("\n");
        }
        result.append("\n");

        // Règle 22: Entropie de la chaîne de Markov
        double entropy = calculateEntropy(transitionMatrix);
        result.append("Entropie de la chaîne de Markov: ").append(String.format("%.4f", entropy)).append("\n\n");

        resultArea.setText(result.toString());
    }

    private boolean checkTransitionMatrix(double[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] < 0 || matrix[i][j] > 1) {
                    return false;
                }
                sum += matrix[i][j];
            }
            if (Math.abs(sum - 1.0) > 1e-10) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Set<Integer>> identifyStates(double[][] matrix) {
        int n = matrix.length;
        Set<Integer> transientStates = new HashSet<>();
        Set<Integer> recurrentStates = new HashSet<>();
        Set<Integer> absorbingStates = new HashSet<>();

        for (int i = 0; i < n; i++) {
            if (matrix[i][i] == 1.0) {
                absorbingStates.add(i);
            } else if (Math.abs(Arrays.stream(matrix[i]).sum() - 1.0) < 1e-10) {
                recurrentStates.add(i);
            } else {
                transientStates.add(i);
            }
        }

        Map<String, Set<Integer>> result = new HashMap<>();
        result.put("transient", transientStates);
        result.put("recurrent", recurrentStates);
        result.put("absorbing", absorbingStates);
        return result;
    }

    private int calculatePeriod(double[][] matrix, int state) {
        int n = matrix.length;
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(state);
        visited[state] = true;
        int level = 0;
        Set<Integer> returnTimes = new HashSet<>();

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int current = queue.poll();
                if (current == state && level > 0) {
                    returnTimes.add(level);
                }
                for (int j = 0; j < n; j++) {
                    if (matrix[current][j] > 0 && !visited[j]) {
                        queue.offer(j);
                        visited[j] = true;
                    }
                }
            }
            level++;
        }

        if (returnTimes.isEmpty()) {
            return 0;  // État transitoire
        }

        int gcd = returnTimes.iterator().next();
        for (int time : returnTimes) {
            gcd = gcd(gcd, time);
        }
        return gcd;
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private List<Set<Integer>> findCommunicationClasses(double[][] matrix) {
        int n = matrix.length;
        boolean[][] reachable = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            dfs(matrix, i, reachable[i]);
        }

        List<Set<Integer>> classes = new ArrayList<>();
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                Set<Integer> newClass = new HashSet<>();
                for (int j = 0; j < n; j++) {
                    if (reachable[i][j] && reachable[j][i]) {
                        newClass.add(j);
                        visited[j] = true;
                    }
                }
                classes.add(newClass);
            }
        }
        return classes;
    }

    private void dfs(double[][] matrix, int start, boolean[] reachable) {
        reachable[start] = true;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[start][i] > 0 && !reachable[i]) {
                dfs(matrix, i, reachable);
            }
        }
    }

    private boolean isAperiodic(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            if (calculatePeriod(matrix, i) != 1) {
                return false;
            }
        }
        return true;
    }

    private double[] calculateStationaryDistribution(double[][] matrix) {
        int n = matrix.length;
        double[][] augmentedMatrix = new double[n + 1][n + 1];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, augmentedMatrix[i], 0, n);
            augmentedMatrix[i][n] = 1;
        }
        Arrays.fill(augmentedMatrix[n], 1);

        double[] b = new double[n + 1];
        b[n] = 1;

        return solveLinearSystem(augmentedMatrix, b);
    }

    private double[] solveLinearSystem(double[][] A, double[] b) {
        int n = A.length;
        for (int i = 0; i < n; i++) {
            int max = i;
            for (int j = i + 1; j < n; j++) {
                if (Math.abs(A[j][i]) > Math.abs(A[max][i])) {
                    max = j;
                }
            }
            double[] temp = A[i];
            A[i] = A[max];
            A[max] = temp;
            double t = b[i];
            b[i] = b[max];
            b[max] = t;
            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                b[j] -= factor * b[i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
            }
        }
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }

    private double[][] calculateMeanFirstPassageTime(double[][] matrix) {
        int n = matrix.length;
        double[][] mfpt = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    double sum = 0;
                    for (int k = 0; k < n; k++) {
                        if (matrix[i][k] > 0) {
                            sum += matrix[i][k] * (k == j ? 1 : mfpt[k][j]);
                        }
                    }
                    mfpt[i][j] = 1 + sum;
                }
            }
        }
        return mfpt;
    }

    private double calculateEntropy(double[][] matrix) {
        double entropy = 0;
        for (double[] row : matrix) {
            for (double p : row) {
                if (p > 0) {
                    entropy -= p * Math.log(p);
                }
            }
        }
        return entropy;
    }

    private String matrixToString(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            for (double val : row) {
                sb.append(String.format("%.4f ", val));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private double[][] calculateProbabilityMatrix(double[][] matrix, int steps) {
        int n = matrix.length;
        double[][] result = new double[n][n];
        for (int i = 0; i < n; i++) {
            result[i][i] = 1.0;
        }

        for (int step = 0; step < steps; step++) {
            result = multiplyMatrices(result, matrix);
        }

        return result;
    }

    private double[][] multiplyMatrices(double[][] a, double[][] b) {
        int m = a.length;
        int n = b[0].length;
        int p = b.length;
        double[][] result = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < p; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }

        return result;
    }

    private double[][] generateBirthDeathMatrix(int n) {
        double[][] matrix = new double[n][n];
        double birth = 0.3;
        double death = 0.2;

        for (int i = 0; i < n; i++) {
            if (i > 0) matrix[i][i-1] = death;
            matrix[i][i] = 1 - birth - death;
            if (i < n-1) matrix[i][i+1] = birth;
        }
        matrix[0][0] = 1 - birth;
        matrix[n-1][n-1] = 1 - death;

        return matrix;
    }

    private double[] calculatePoissonProbabilities(double lambda, double t, int maxN) {
        double[] prob = new double[maxN + 1];
        double poissonParameter = lambda * t;
        prob[0] = Math.exp(-poissonParameter);

        for (int n = 1; n <= maxN; n++) {
            prob[n] = prob[n-1] * poissonParameter / n;
        }

        return prob;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MarkovChain().setVisible(true);
            }
        });
    }
}