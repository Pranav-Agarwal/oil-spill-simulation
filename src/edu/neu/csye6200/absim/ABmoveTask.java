package edu.neu.csye6200.absim;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * task that calculates a path to a passed in destination,
 * and moves it's assigned boat along this path.
 * The A* algorithm using diagonal distance heuristic is used to find
 * this shortest path. 
 */
public class ABmoveTask extends ABtask {
	
	private Logger log = Logger.getLogger(ABmoveTask.class.getName());

	private Node[][] nodes;              //Grid of node objects used to find the path
	ArrayList<Node> open;                //list of nodes under consideration for path
	boolean closed[][];                  //nodes already checked for path. If checked then the nodes[x][y]=1, else 0
	private ArrayList<int[]> path;       //list of [x,y] coordinates in order that the boat will move along
	
	/**
	 * node class that is used in the A* algorithm
	 */
	public class Node{  
	    int heuristicCost = 0;     //Heuristic cost to destination (h)
	    int finalCost = 0;         //Total cost
	    int i, j;                  //coordinates of the node
	    Node parent=null;          //parent node of this node
	    
	    Node(int i, int j,int h){
	        this.i = i;
	        this.j = j; 
	        this.heuristicCost = h;
	        finalCost=h;
	    }

	}
	
	ABmoveTask(ABoat boat,int destX, int destY){
		super(boat);
	
		int size = ABmap.instance().size;
		nodes = new Node[size][size];
		closed =new boolean[size][size];
		open = new ArrayList<Node>();
		
		/* instantiate a grid of nodes corresponding to the cells in the map */
		for (int row=1;row<size-1;row++) {
			for(int col=1;col<size-1;col++) {
				if(ABmap.instance().cells[row][col].getOilCount()>=0) {
					int h = (Math.max(Math.abs(boat.posX-destX),Math.abs(boat.posY-destY)))*10;  //calculate diagonal distance from node to destination and set it as h
					nodes[row][col] = new Node(ABmap.instance().cells[row][col].x,ABmap.instance().cells[row][col].y,h);
				}
			}
		}
		
		/* fill the path list with [x,y] coordinates according to the A* algorithm */
		path = AStar(boat.posX,boat.posY,destX,destY);
	}
	
	/**
	 * moves the boat to the next cell in the path every tick.
	 * If path was not found or path has been fully traversed, return true
	 */
	@Override
	public boolean run() {
		if (path==null || path.size()==0) return true;
		else {
			int lastIndex = path.size()-1;
			boat.move(path.get(lastIndex)[0], path.get(lastIndex)[1]);
			path.remove(lastIndex);
			return false;
		}
	}
	
	/**
	 * Utility function used in the A* algorithm. It updates a node's
	 * total cost and parent and adds it to the list of open nodes
	 * if it is not already there
	 */
    private void checkAndUpdateCost(Node current, Node t, int cost){
        if(t == null || closed[t.i][t.j])return;
        int t_final_cost = t.heuristicCost+cost;
        
        boolean inOpen = open.contains(t);
        if(!inOpen || t_final_cost<t.finalCost){
            t.finalCost = t_final_cost;
            t.parent = current;
            if(!inOpen)open.add(t);
        }
    }
	
	/**
	 * The A* algorithm using diagonal heuristic.
	 * reference - https://www.geeksforgeeks.org/a-search-algorithm/
	 */
	public ArrayList<int[]> AStar(int curX,int curY, int destX, int destY){
		ArrayList<int[]> ans = new ArrayList<int[]>();
		
		Node current = nodes[boat.posX][boat.posY];
		Node dest = nodes[destX][destY];
		open.add(current);
		
		/* run while there are still nodes to consider */
		while (open.size()>0) {
			
			/* select the node with the lowest total cost in open for consideration,
			 *  remove it from open and add it to to closed */
			current = open.get(0);
			for (Node n : open) {
				if(n.finalCost<=current.finalCost) current = n;
			}
			open.remove(current);
			closed[current.i][current.j] = true;
			
			/* if destination reached break the loop */
			if(current==dest) break;
			
            Node t;
			/* run the check and update method on all the current node's neighbours.
			 * horizontal/vertical cost is 10 and diagonal is 14  */
            if(current.i-1>=0){
                t = nodes[current.i-1][current.j];
                checkAndUpdateCost(current, t, current.finalCost+10); 

                if(current.j-1>=0){                      
                    t = nodes[current.i-1][current.j-1];
                    checkAndUpdateCost(current, t, current.finalCost+14); 
                }

                if(current.j+1<nodes[0].length){
                    t = nodes[current.i-1][current.j+1];
                    checkAndUpdateCost(current, t, current.finalCost+14); 
                }
            } 

            if(current.j-1>=0){
                t = nodes[current.i][current.j-1];
                checkAndUpdateCost(current, t, current.finalCost+10); 
            }

            if(current.j+1<nodes[0].length){
                t = nodes[current.i][current.j+1];
                checkAndUpdateCost(current, t, current.finalCost+10); 
            }

            if(current.i+1<nodes.length){
                t = nodes[current.i+1][current.j];
                checkAndUpdateCost(current, t, current.finalCost+10); 

                if(current.j-1>=0){
                    t = nodes[current.i+1][current.j-1];
                    checkAndUpdateCost(current, t, current.finalCost+14); 
                }
                
                if(current.j+1<nodes[0].length){
                   t = nodes[current.i+1][current.j+1];
                    checkAndUpdateCost(current, t, current.finalCost+14); 
                }  
            }
			
		}
		/* if the destination has a parent assigned this means a path was found.
		 * if so keep going up parent by parent and adding the node's
		 * coordinates to the result path. when source is reached return this path*/
		if(dest.parent!=null) {
			Node t = dest;
			while(t!=null) {
				ans.add(new int[] {t.i,t.j});
				t = t.parent;
			}
			return ans;
		}
		return null;  //path not found
		
	}
}
