import numpy as np
import networkx as nx
import matplotlib.pyplot as plt

class MarkovChain:
    def __init__(self, transition_matrix):
        self.transition_matrix = np.array(transition_matrix)
        self.states = list(range(len(transition_matrix)))

    def display_transition_matrix(self):
        print("Matrice de transition : ")
        print(self.transition_matrix)

    def display_dependency_graph(self):
        print("Graphe de dépendance : ")
        G = nx.DiGraph()

        for i in range(len(self.states)):
            for j in range(len(self.states)):
                if self.transition_matrix[i][j] > 0:
                    G.add_edge(i, j, weight=self.transition_matrix[i][j])

        pos = nx.circular_layout(G)  # Vous pouvez changer la disposition selon vos préférences
        nx.draw(G, pos, with_labels=True, node_size=700, node_color="skyblue", font_size=10, font_color="black", font_weight="bold", edge_color="gray", width=1, arrowsize=15, alpha=0.8)
        plt.title('Graphe de Dépendance')
        plt.show()

    def has_limit(self):
        # Vérifier si la somme des éléments de chaque colonne est égale à 1
        column_sums = np.sum(self.transition_matrix, axis=0)
        return np.allclose(column_sums, 1.0)

    def stationary_distribution(self):
        n = len(self.transition_matrix)
        initial_distribution = np.ones(n) / n

        # Appliquer les règles de la chaîne de Markov homogène pour la limite
        for i in range(1000):  # Nombre arbitraire d'itérations
            initial_distribution = np.dot(initial_distribution, self.transition_matrix)

        return initial_distribution

# Exemple d'utilisation avec la matrice ayant une limite
transition_matrix_limit = [
    [0.4, 0.6],
    [0.2, 0.8]
]

markov_chain_limit = MarkovChain(transition_matrix_limit)

markov_chain_limit.display_transition_matrix()
markov_chain_limit.display_dependency_graph()

if markov_chain_limit.has_limit():
    print("La chaîne de Markov a une limite.")
    stationary_distribution_limit = markov_chain_limit.stationary_distribution()
    print("Distribution stationnaire :", stationary_distribution_limit)
else:
    print("La chaîne de Markov n'a pas de limite.")

