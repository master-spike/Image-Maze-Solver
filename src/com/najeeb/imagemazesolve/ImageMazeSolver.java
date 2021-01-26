package com.najeeb.imagemazesolve;

public class ImageMazeSolver {
	public static void main(String[] args) {
		
		Maze maze = new Maze(MazeInterpreter.getObstacleFromImage(MazeInterpreter.readPNGFile("input/input")));
		maze.solve(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		// start x, start y, end x, end y
	}
}
