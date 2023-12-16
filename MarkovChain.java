import java.util.Arrays;

public class MarkovChain {

    private double[][] transitionMatrix;

    // Constructeur
    public MarkovChain(double[][] transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
    }

    // Afficher la matrice de transition
    public void displayTransitionMatrix() {
        System.out.println("Matrice de transition : ");
        for (double[] row : transitionMatrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    // Afficher le graphe de dépendance
    public void displayDependencyGraph() {
        System.out.println("Graphe de dépendance : ");
        for (int i = 0; i < transitionMatrix.length; i++) {
            for (int j = 0; j < transitionMatrix[i].length; j++) {
                if (transitionMatrix[i][j] > 0) {
                    System.out.println("État " + (i + 1) + " -> État " + (j + 1));
                }
            }
        }
    }

    // Vérifier l'existence de la limite
    public boolean hasLimit() {
        // Vérifier si la somme des éléments de chaque colonne est égale à 1
        for (int j = 0; j < transitionMatrix[0].length; j++) {
            double sum = 0;
            for (int i = 0; i < transitionMatrix.length; i++) {
                sum += transitionMatrix[i][j];
            }
            if (sum != 1.0) {
                return false;
            }
        }
        return true;
    }

    // Calculer la distribution stationnaire
    public double[] stationaryDistribution() {
        int n = transitionMatrix.length;
        double[] initialDistribution = new double[n];
        Arrays.fill(initialDistribution, 1.0 / n);

        // Appliquer les règles de la chaîne de Markov homogène pour la limite
        for (int i = 0; i < 1000; i++) { // Nombre arbitraire d'itérations
            initialDistribution = multiply(initialDistribution, transitionMatrix);
        }

        return initialDistribution;
    }

    // Fonction utilitaire pour multiplier un vecteur par une matrice
    private double[] multiply(double[] vector, double[][] matrix) {
        int n = matrix.length;
        double[] result = new double[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i] += vector[j] * matrix[j][i];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        // Exemple d'utilisation
        double[][] transitionMatrix = {
                {0, 1},
                {0.3, 0}
        };

        MarkovChain markovChain = new MarkovChain(transitionMatrix);

        markovChain.displayTransitionMatrix();
        markovChain.displayDependencyGraph();

        if (markovChain.hasLimit()) {
            System.out.println("La chaîne de Markov a une limite.");
            double[] stationaryDistribution = markovChain.stationaryDistribution();
            System.out.println("Distribution stationnaire : " + Arrays.toString(stationaryDistribution));
        } else {
            System.out.println("La chaîne de Markov n'a pas de limite.");
        }
    }
}
