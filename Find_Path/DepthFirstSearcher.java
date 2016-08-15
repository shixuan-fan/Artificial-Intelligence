import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Depth-First Search (DFS)
 * 
 * You should fill the search() method of this class.
 */
public class DepthFirstSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public DepthFirstSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main depth first search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {
		// FILL THIS METHOD

		// explored list is a 2D Boolean array that indicates if a state associated with a given position in the maze has already been explored.
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];

		// Some initialization
		State end = null;
		Square start = maze.getPlayerSquare();

		// Stack implementing the Frontier list
		LinkedList<State> stack = new LinkedList<State>();
		stack.push(new State(start, null, 0, 0));

		while (!stack.isEmpty()) {
			// TODO return true if find a solution
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found

			// use stack.pop() to pop the stack.
			// use stack.push(...) to elements to stack
			State current = stack.pop();

			// Maintain parameters
			noOfNodesExpanded++;
			maxDepthSearched = Math.max(maxDepthSearched, current.getDepth());

			// Goal check, mark '.' along the way.
			if (current.isGoal(maze)) {
				cost = current.getGValue();
				char[][] mazeMatrix = maze.getMazeMatrix();
				current = current.getParent();
				while (current.getParent() != null) {
					mazeMatrix[current.getX()][current.getY()] = '.';
					current = current.getParent();
				}

				return true;
			}

			// Push successors onto the stack.
			ArrayList<State> successors = current.getSuccessors(explored, maze);
			for (int i = successors.size() - 1; i >= 0; --i) {
				// Cycle checking.
				State next = successors.get(i);
				State checkCurrent = current;
				boolean isCycle = false;

				while (checkCurrent != null) {
					if (checkCurrent.getX() == next.getX() && checkCurrent.getY() == next.getY()) {
						isCycle = true;
						break;
					}

					checkCurrent = checkCurrent.getParent();
				}
				
				if (!isCycle) {
					stack.push(next);
				}
			}

			maxSizeOfFrontier = Math.max(maxSizeOfFrontier, stack.size());
		}

		// TODO return false if no solution
		return false;
	}
}
