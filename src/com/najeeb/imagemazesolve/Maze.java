package com.najeeb.imagemazesolve;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

import com.najeeb.search.AStarNode;
import com.najeeb.search.AStarSearch;

public class Maze {
	
	public boolean[][] obstacle;
	
	public Maze(boolean[][] obstacle) {
		this.obstacle = obstacle;
	}
	
	public Point[] solve(int sx, int sy, int gx, int gy) {
		
		System.out.println("starting solver");
		ArrayList<Point> validpoints = new ArrayList<Point>();

		for (int j = 0; j < obstacle[0].length; j++) {
			for (int i = 0; i < obstacle.length; i++) {
				Point p = new Point(i,j);
				if(!isInvalidPoint().test(p)) validpoints.add(new Point(i,j));
			}
		}
		validpoints.removeIf(isInvalidPoint());
		System.out.println("filtered obstructed points");
		ArrayList<Region> regions = new ArrayList<Region>(); 	/* to be a disjoint set of rectangular regions 
																   covering all unobstructed points in the maze*/
		Random rand = new Random();
		while(!validpoints.isEmpty()) {
			int arrsize_0 = validpoints.size();
			int nextpoint = rand.nextInt(validpoints.size());
			int tlx = validpoints.get(nextpoint).px;
			int tly = validpoints.get(nextpoint).py;
			int brx = tlx;
			int bry = tly;
			while (brx+1 < obstacle.length && bry+1 < obstacle[0].length && regionSubsetOfArrayList(new Region(new Point(tlx, bry+1),new Point(brx+1, bry+1)), validpoints)
					&& regionSubsetOfArrayList(new Region(new Point(brx+1, tly),new Point(brx+1, bry+1)), validpoints)) {
				brx++;
				bry++;
			}
			while(bry+1 < obstacle[0].length && regionSubsetOfArrayList(new Region(new Point(tlx, bry+1),new Point(brx, bry+1)), validpoints)) {
				bry = bry+1;
			}
			while (tlx-1 > 0 && tly-1 >0 && regionSubsetOfArrayList(new Region(new Point(tlx-1, tly-1),new Point(tlx-1, bry)), validpoints)
					&& regionSubsetOfArrayList(new Region(new Point(tlx-1, tly-1),new Point(brx, tly-1)), validpoints)) {
				tlx--;
				tly--;
			}
			Region r = new Region(new Point(tlx, tly), new Point(brx, bry));
			validpoints.removeIf(r.isInRegion());
			regions.add(r);
			System.out.println(arrsize_0 + " - " + (1 + r.botr.px - r.topl.px) + "*" + (1 + r.botr.py - r.topl.py) + " = " + validpoints.size());
			
			
		}
		
		System.out.println("generated regions");
		
		ArrayList<VertexRegion> vertices = new ArrayList<VertexRegion>();
		
		for (Region r : regions) {
			VertexRegion vr = new VertexRegion(r);
			for(VertexRegion vs : vertices) {
				if(vs.r.isAdjacentTo(r)) {
					vr.adj.add(vs);
					vs.adj.add(vr);
				}
			}
			vertices.add(vr);
		}
		MazeRender.writeImageToPng(MazeRender.drawRegions(regions, obstacle.length, obstacle[0].length, -1), "output/regions");
		
		MazeAStarSearch mass = new MazeAStarSearch(vertices, gx, gy);
		VertexRegion start = null;
		for (VertexRegion vr : vertices) {
			if (vr.r.isInRegion().test(new Point(sx,sy))) { start = vr; break; }
		}
		mass.initialise(new SearchNode(gx, gy, start, 0));
		ArrayList<VertexRegion> goalnode = mass.search().path();
		
		ArrayList<Region> pathregions = new ArrayList<Region>();
		for (VertexRegion vr : goalnode) {
			pathregions.add(vr.r);
		}
		
		MazeRender.writeImageToPng(MazeRender.drawRegions(pathregions, obstacle.length, obstacle[0].length, 0), "output/pathregions");
		
		return null;
		
	}
	
	private boolean pointInArrayList(Point p, ArrayList<Point> arr) {
		for (Point q : arr) {
			if (q.px == p.px && q.py == p.py) return true;
		}
		return false;
		
	}
	
	private boolean regionSubsetOfArrayList(Region r, ArrayList<Point> arr) {
		for (int i = r.topl.px; i <= r.botr.px; i++) {
			for (int j = r.topl.py; j <= r.botr.py; j++) {
				if (!pointInArrayList(new Point(i,j), arr)) return false;
			}
		}
		return true;
	}

	public class Region {
		
		Region(Point topl, Point botr) {
			this.topl = topl;
			this.botr = botr;
		}
		
		Point topl;
		Point botr;
		
		boolean isAdjacentTo(Region r) {
			if (topl.py == r.botr.py + 1 && topl.px <= r.botr.px && botr.px >= r.topl.px) {
				return true;
			}
			if (botr.py == r.topl.py - 1 && topl.px <= r.botr.px && botr.px >= r.topl.px) {
				return true;
			}
			if (topl.px == r.botr.px + 1 && topl.py <= r.botr.py && botr.py >= r.topl.py) {
				return true;
			}
			if (botr.px == r.topl.px - 1 && topl.py <= r.botr.py && botr.py >= r.topl.py) {
				return true;
			}
			return false;
			
		}
		
		Predicate<Point> isInRegion() {
			return p -> p.px >= topl.px && p.px <= botr.px && p.py >= topl.py && p.py <= botr.py;
			
		}
		
	}
	
	private Predicate<Point> isInvalidPoint() {
		return p -> obstacle[p.px][p.py];
	}
	
	private class VertexRegion {
		VertexRegion(Region r) {
			this.r = r;
			adj = new ArrayList<VertexRegion>();
		}
		Region r;
		
		ArrayList<VertexRegion> adj;
	}
	
	private class SearchNode extends AStarNode {
		
		int gx;
		int gy;
		int cost;
		
		VertexRegion current;
		SearchNode predecessor;
		
		SearchNode(int gx, int gy, VertexRegion current, int cost) {
			this.gx = gx;
			this.gy = gy;
			this.current = current;
			predecessor = null;
			this.cost = cost;
			
		}
		public ArrayList<VertexRegion> path() {
			SearchNode sn = this;
			ArrayList<VertexRegion> p = new ArrayList<VertexRegion>();
			while (sn.predecessor != null) {
				p.add(sn.current);
				sn = sn.predecessor;
			}
			p.add(sn.current);
			return p;
		}
		
		SearchNode(int gx, int gy, VertexRegion current, SearchNode predecessor, int cost) {
			this.gx = gx;
			this.gy = gy;
			this.current = current;
			this.predecessor = predecessor;
			this.cost = cost;
			
		}

		public int heuristic() {			
			if (gx < current.r.topl.px) { // goal is to left of region
				if (gy < current.r.topl.py) { // goal is above region
					return current.r.topl.py - gy + current.r.topl.px - gx; // manhat dist from goal to topl corner
				}
				else if (gy <= current.r.botr.py) {
					return current.r.topl.px - gx; // shortest dist from left edge to goal
				}
				else { // goal is below region
					return gy - current.r.botr.py + current.r.topl.px - gx; // manhat dist from goal to botl corner
				}
			}
			else if (gx > current.r.botr.px) { // goal is to right of region
				if (gy < current.r.topl.py) { // goal is above region
					return current.r.topl.py - gy + gx - current.r.botr.px; // manhat dist from goal to topr corner
				}
				else if (gy <= current.r.botr.py) {
					return gx - current.r.botr.px; // shortest dist from right edge to goal
				}
				else { // goal is below region
					return gy - current.r.botr.py + gx - current.r.botr.px; // manhat dist from goal to botr corner
				}
			}
			else if (gy < current.r.topl.py) { // goal is directly above region
				return current.r.topl.py - gy; // shortest dist from top edge to goal
			}
			else if (gy > current.r.botr.py) { // goal is directly above region
				return current.r.topl.py - gy; // shortest dist from bottom edge to goal
			}
			else return 0; // node in region
		}

		public int cost() {
			return cost;
		}
		
		
	}
	
	private class MazeAStarSearch extends AStarSearch<SearchNode> {

		ArrayList<VertexRegion> unvisited;
		
		int gx;
		int gy;
		
		MazeAStarSearch(ArrayList<VertexRegion> unvisited, int gx, int gy) {
			this.unvisited = new ArrayList<VertexRegion>();
			this.unvisited.addAll(unvisited);
			this.gx = gx;
			this.gy = gy;
		}
		
		public ArrayList<SearchNode> successors(SearchNode n) {
			
			ArrayList<SearchNode> succs = new ArrayList<SearchNode>();
			for(VertexRegion vr : n.current.adj) {
				if (unvisited.contains(vr)) succs.add(new SearchNode(gx, gy, vr, n, n.cost + 2));
			}
			return succs;
		}

		protected boolean isGoal(SearchNode n) {
			unvisited.remove(n.current);
			return n.heuristic() == 0;
		}
		
	}
	
}