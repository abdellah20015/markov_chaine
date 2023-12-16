#include <stdio.h>
#include <stdlib.h>

#define N 2 // Nombre d'états

// Structure pour représenter la matrice de transition
typedef struct {
    double **matrix;
} MarkovChain;

// Fonction pour initialiser la matrice de transition
MarkovChain initMarkovChain() {
    MarkovChain chain;
    chain.matrix = (double **)malloc(N * sizeof(double *));
    for (int i = 0; i < N; i++) {
        chain.matrix[i] = (double *)malloc(N * sizeof(double));
    }

    // Initialisation de la matrice (exemple)
    chain.matrix[0][0] = 0.5;
    chain.matrix[0][1] = 0.5;
    chain.matrix[1][0] = 0.3;
    chain.matrix[1][1] = 0.7;

    return chain;
}

// Fonction pour afficher la matrice de transition
void displayTransitionMatrix(MarkovChain *chain) {
    printf("Matrice de transition : \n");
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            printf("%f ", chain->matrix[i][j]);
        }
        printf("\n");
    }
}

// Fonction pour afficher le graphe de dépendance
void displayDependencyGraph(MarkovChain *chain) {
    printf("Graphe de dépendance : \n");
    for (int i = 0; i < N; i++) {
        for (int j = 0; j < N; j++) {
            if (chain->matrix[i][j] > 0) {
                printf("État %d -> État %d\n", i + 1, j + 1);
            }
        }
    }
}

// Fonction pour vérifier l'existence de la limite
int hasLimit(MarkovChain *chain) {
    // Vérifier si la somme des éléments de chaque colonne est égale à 1
    for (int j = 0; j < N; j++) {
        double sum = 0;
        for (int i = 0; i < N; i++) {
            sum += chain->matrix[i][j];
        }
        if (sum != 1.0) {
            return 0;
        }
    }
    return 1;
}

// Fonction pour calculer la distribution stationnaire
void stationaryDistribution(MarkovChain *chain) {
    printf("Calcul de la distribution stationnaire : \n");

    // Initialiser la distribution initiale (uniforme)
    double *distribution = (double *)malloc(N * sizeof(double));
    for (int i = 0; i < N; i++) {
        distribution[i] = 1.0 / N;
    }

    // Appliquer les règles de la chaîne de Markov homogène pour la limite
    for (int iter = 0; iter < 1000; iter++) { // Nombre arbitraire d'itérations
        double *newDistribution = (double *)malloc(N * sizeof(double));

        // Multiplier la distribution actuelle par la matrice de transition
        for (int i = 0; i < N; i++) {
            newDistribution[i] = 0;
            for (int j = 0; j < N; j++) {
                newDistribution[i] += distribution[j] * chain->matrix[j][i];
            }
        }

        // Remplacer la distribution actuelle par la nouvelle
        for (int i = 0; i < N; i++) {
            distribution[i] = newDistribution[i];
        }

        free(newDistribution);
    }

    // Afficher la distribution stationnaire
    printf("Distribution stationnaire : ");
    for (int i = 0; i < N; i++) {
        printf("%f ", distribution[i]);
    }

    free(distribution);
}

// Fonction principale
int main() {
    MarkovChain chain = initMarkovChain();

    displayTransitionMatrix(&chain);
    displayDependencyGraph(&chain);

    if (hasLimit(&chain)) {
        printf("La chaîne de Markov a une limite.\n");
        stationaryDistribution(&chain);
    } else {
        printf("La chaîne de Markov n'a pas de limite.\n");
    }

    // Libérer la mémoire allouée pour la matrice de transition
    for (int i = 0; i < N; i++) {
        free(chain.matrix[i]);
    }
    free(chain.matrix);

    return 0;
}
