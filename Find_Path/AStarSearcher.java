import java.util.*;

/**
 * A* algorithm search
 * 
 * You should fill the search() method of this class.
 */
public class AStarSearcher extends Searcher {

	/**
	 * Calls the parent class constructor.
	 * 
	 * @see Searcher
	 * @param maze initial maze.
	 */
	public AStarSearcher(Maze maze) {
		super(maze);
	}

	/**
	 * Main a-star search algorithm.
	 * 
	 * @return true if the search finds a solution, false otherwise.
	 */
	public boolean search() {

		// FILL THIS METHOD

		// explored list is a Boolean array that indicates if a state associated with a given position in the maze has already been explored. 
		boolean[][] explored = new boolean[maze.getNoOfRows()][maze.getNoOfCols()];
		// ...

		PriorityQueue<StateFValuePair> frontier = new PriorityQueue<StateFValuePair>();

		// TODO initialize the root state and add
		// to frontier list 
		// ...
		State startState = new State(maze.getPlayerSquare(), null, 0, 0);
		Square goal = maze.getGoalSquare();
		StateFValuePair startPair = new StateFValuePair(startState, distance(startState.getSquare(), goal));
		frontier.add(startPair);
		
		while (!frontier.isEmpty()) {
			// TODO return true if a solution has been found
			// TODO maintain the cost, noOfNodesExpanded (a.k.a. noOfNodesExplored),
			// maxDepthSearched, maxSizeOfFrontier during
			// the search
			// TODO update the maze if a solution found

			// use frontier.poll() to extract the minimum stateFValuePair.
			// use frontier.add(...) to add stateFValue pairs

			StateFValuePair current = frontier.poll();

			State currentState = current.getState();
			explored[currentState.getX()][currentState.getY()] = true;

			// Maintain parameters
			noOfNodesExpanded++;
			maxDepthSearched = Math.max(maxDepthSearched, currentState.getDepth());

			// Goal test.
			if (currentState.isGoal(maze)) {
				cost = currentState.getGValue();
				char[][] mazeMatrix = maze.getMazeMatrix();
				currentState = currentState.getParent();
				while (currentState.getParent() != null) {
					mazeMatrix[currentState.getX()][currentState.getY()] = '.';
					currentState = currentState.getParent();
				}

				return true;
			}

			ArrayList<State> successors = currentState.getSuccessors(explored, maze);
			for (int i = successors.size() - 1; i >= 0; --i) {
				// Check the elements in the queue.
				State next = successors.get(i);
				Iterator<StateFValuePair> queueIt = frontier.iterator();
				boolean throwAway = false;

				while (queueIt.hasNext()) {
					State queueElement = queueIt.next().getState();
					if (queueElement.getX() == next.getX() && queueElement.getY() == next.getY()) {
						// Current state is better, replace the original one.						
						if (next.getGValue() < queueElement.getGValue()) {
							queueIt.remove();
						}
						else {
							throwAway = true;
						}
						
						break;
					}
				}

				if (!throwAway) {
					frontier.add(new StateFValuePair(next, next.getGValue() + distance(goal, next.getSquare())));
				}
			}

			maxSizeOfFrontier = Math.max(maxSizeOfFrontier, frontier.size());
		}

		// TODO return false if no solution
		return false;
	}

	private int distance(Square square1, Square square2) {
		return Math.abs(square1.X - square2.X) + Math.abs(square1.Y - square2.Y);
	}

}
