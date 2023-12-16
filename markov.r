# Installer le package 'igraph' si ce n'est pas déjà fait
# install.packages("igraph")

library(igraph)

# Fonction pour initialiser la matrice de transition
initMarkovChain <- function() {
  matrix(c(0.5, 0.5, 0.3, 0.7), nrow = 2, byrow = TRUE)
}

# Fonction pour afficher la matrice de transition
displayTransitionMatrix <- function(matrix) {
  cat("Matrice de transition :\n")
  print(matrix)
}

# Fonction pour afficher le graphe de dépendance
displayDependencyGraph <- function(matrix) {
  cat("Graphe de dépendance :\n")
  graph <- graph_from_adjacency_matrix(matrix, mode = "directed", weighted = TRUE)
  plot(graph, edge.label = matrix, main = "Graphe de Dépendance")
}

# Fonction pour vérifier l'existence de la limite
hasLimit <- function(matrix) {
  # Vérifier si la somme des éléments de chaque colonne est égale à 1
  column_sums <- colSums(matrix)
  all.equal(column_sums, rep(1, ncol(matrix)))
}

# Fonction pour calculer la distribution stationnaire
stationaryDistribution <- function(matrix) {
  cat("Calcul de la distribution stationnaire :\n")

  # Initialiser la distribution initiale (uniforme)
  initial_distribution <- rep(1 / ncol(matrix), ncol(matrix))

  # Appliquer les règles de la chaîne de Markov homogène pour la limite
  for (iter in 1:1000) {  # Nombre arbitraire d'itérations
    initial_distribution <- initial_distribution %*% matrix
  }

  # Afficher la distribution stationnaire
  cat("Distribution stationnaire :", initial_distribution, "\n")
}

# Fonction principale
main <- function() {
  transitionMatrix <- initMarkovChain()

  displayTransitionMatrix(transitionMatrix)
  displayDependencyGraph(transitionMatrix)

  if (hasLimit(transitionMatrix)) {
    cat("La chaîne de Markov a une limite.\n")
    stationaryDistribution(transitionMatrix)
  } else {
    cat("La chaîne de Markov n'a pas de limite.\n")
  }
}

# Exécuter le programme principal
main()
