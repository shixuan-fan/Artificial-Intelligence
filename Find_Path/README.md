#Find Path

This program will solve a maze by DFS/A-star algorithm.

To run the program:

1. `javac *.java`

2. `java FindPath <maze> <search-method>`, where search method could be `dfs` or `astar`.

##Maze definition
"S": start position.

"G": goal position.

"%": walls.

Only horizontal and vertical moves are allowed.

##Output
1. The matrix with "." in each square that is part of the solution path.

2. The length of the solution path.

3. The number of nodes expanded.

4. The maximum depth searched.

5. The maximum size of the Frontier at any point during the search.

If goal position is not reachable from the start position, output "No Solution".
