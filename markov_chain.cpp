#include <iostream>
#include <vector>
#include <cmath>
#include <iomanip>
#include <fstream>

// Inclure les bibliothèques nécessaires pour Graphviz
#include <graphviz/gvc.h>

class MarkovChain {
public:
    MarkovChain(const std::vector<std::vector<double>>& transition_matrix)
        : transition_matrix(transition_matrix) {}

    void displayTransitionMatrix() const {
        std::cout << "Matrice de transition : \n";
        for (const auto& row : transition_matrix) {
            for (double prob : row) {
                std::cout << std::setw(5) << prob << " ";
            }
            std::cout << '\n';
        }
    }

    void displayDependencyGraph() const {
        std::cout << "Graphe de dépendance : \n";

        // Créer le graphe de dépendance au format DOT
        std::string dotGraph = "digraph G {\n";
        for (size_t i = 0; i < transition_matrix.size(); ++i) {
            for (size_t j = 0; j < transition_matrix[i].size(); ++j) {
                if (transition_matrix[i][j] > 0.0) {
                    dotGraph += "  " + std::to_string(i) + " -> " + std::to_string(j) +
                                " [label=\"" + std::to_string(transition_matrix[i][j]) + "\"];\n";
                }
            }
        }
        dotGraph += "}\n";

        // Écrire le graphe au format DOT dans un fichier temporaire
        std::ofstream dotFile("dependency_graph.dot");
        dotFile << dotGraph;
        dotFile.close();

        // Générer l'image à partir du fichier DOT en utilisant Graphviz
        GVC_t* gvc;
        Agraph_t* g;
        gvc = gvContext();
        FILE* tempFile = fopen("dependency_graph.dot", "r");
        g = agread(tempFile, 0);
        gvLayout(gvc, g, "dot");
        gvRender(gvc, g, "png", fopen("dependency_graph.png", "w"));
        gvFreeLayout(gvc, g);
        agclose(g);
        gvFreeContext(gvc);

        // Afficher l'image générée
        system("xdg-open dependency_graph.png");

        // Supprimer les fichiers temporaires
        remove("dependency_graph.dot");
        remove("dependency_graph.png");
    }

    bool hasLimit() const {
        // Vérifier si la somme des éléments de chaque colonne est égale à 1
        for (size_t j = 0; j < transition_matrix[0].size(); ++j) {
            double sum = 0.0;
            for (size_t i = 0; i < transition_matrix.size(); ++i) {
                sum += transition_matrix[i][j];
            }
            if (std::abs(sum - 1.0) > 1e-6) {
                return false;
            }
        }
        return true;
    }

    std::vector<double> stationaryDistribution() const {
        size_t n = transition_matrix.size();
        std::vector<double> initialDistribution(n, 1.0 / n);

        // Appliquer les règles de la chaîne de Markov homogène pour la limite
        for (int iter = 0; iter < 1000; ++iter) { // Nombre arbitraire d'itérations
            initialDistribution = multiply(initialDistribution, transition_matrix);
        }

        return initialDistribution;
    }

private:
    static std::vector<double> multiply(const std::vector<double>& vector, const std::vector<std::vector<double>>& matrix) {
        size_t n = matrix.size();
        std::vector<double> result(n, 0.0);

        for (size_t i = 0; i < n; ++i) {
            for (size_t j = 0; j < n; ++j) {
                result[i] += vector[j] * matrix[j][i];
            }
        }

        return result;
    }

private:
    std::vector<std::vector<double>> transition_matrix;
};

int main() {
    // Exemple d'utilisation
    std::vector<std::vector<double>> transition_matrix = {
        {0.5, 0.5},
        {0.3, 0.7}
    };

    MarkovChain markovChain(transition_matrix);

    markovChain.displayTransitionMatrix();
    markovChain.displayDependencyGraph();

    if (markovChain.hasLimit()) {
        std::cout << "La chaîne de Markov a une limite.\n";
        std::vector<double> stationaryDistribution = markovChain.stationaryDistribution();
        std::cout << "Distribution stationnaire : ";
        for (double prob : stationaryDistribution) {
            std::cout << std::setw(5) << prob << " ";
        }
        std::cout << '\n';
    } else {
        std::cout << "La chaîne de Markov n'a pas de limite.\n";
    }

    return 0;
}
