package mclayt30_AI_Assignment2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public class PuzzleProblem {

	static String[][] goalState = {{"1", "2", "3"},{"8", "B", "4"},{"7", "6", "5"}};
	
	public static String[][] copyArray(String[][] mainArray){ // Copies an array to a different location in memory, used to prevent errors when manipulating arrays from different objects
			 String[][] arrayCopy = new String[mainArray.length][];
			 for(int i = 0; i < mainArray.length; i++) {
				 arrayCopy[i] = mainArray[i].clone();
			 }
			 return arrayCopy;
		 }
	
	public static class CostComparator implements Comparator<Node>{ // Compares the cost of two nodes, used in A* 

		@Override
		public int compare(Node o1, Node o2) {
			return Integer.compare(o1.cost, o2.cost);
		}
		
	}
	
	public static class LevelComparator implements Comparator<Node>{ // Compares the level of two nodes, used in UCS

		@Override
		public int compare(Node o1, Node o2) {
			return Integer.compare(o1.level, o2.level);
		}
		
	}
	
	public static class Node {
		 public Node(String[][] mat, Node par, int lev) {
			matrix = copyArray(mat);
			parent = par;
			level = lev;
			cost = 0;
			determineCost();
			findBlank();
			possibleMoves = movesPossible();
			findPrevious();
		}
		 public Node(String[][] mat) { 
				matrix = copyArray(mat);
				parent = null;
				findBlank();
				possibleMoves = movesPossible();
				
			}
		 public Node() {
			 parent = null;
			 level = 0;
			 matrix = generateStart();
			 findBlank();
			 determineCost();
			 possibleMoves = movesPossible();
		 }
		 String matrix[][] = new String[3][3];
		 int x,y; // Coordinates of the blank space
		 int level; // Depth of a node
		 int cost; // Cost of a node
		 int possibleMoves; // Number of children possible for a node
		 Node parent; // Parent node
		 boolean wasUp = false, wasDown = false, wasLeft = false, wasRight = false; // What the previous swap direction was, used to prevent reversing a move
			 
		 public Node[] generateChildren() { // Generates the children of a node, uses the # of possible moves, the level of a node, and a boolean value to prevent reversing the move made to get to the current node
			 int numChildren = 0;
			 if(this.possibleMoves == 4) {
				 if(this.level == 0) {
					 numChildren = 4;
				 }
				 else {
					 numChildren = 3;
				 }
			 }
			 else if(this.possibleMoves == 3) {
				 if(this.level == 0) {
					 numChildren = 3;
				 }
				 else {
					 numChildren = 2;
				 }
			 }
			 else if(this.possibleMoves == 2) {
				 if(this.level == 0) {
					 numChildren = 2;
				 }
				 else {
					 numChildren = 1;
				 }
			 }
			 Node[] children = new Node[numChildren];
			 
			 if(this.possibleMoves == 4) {
				 if(this.level == 0) {
					 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
					 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
					 Node node3 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
					 Node node4 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
					 children = new Node[]{node1, node2, node3, node4};
				 }
				 else {
					 if(this.wasDown) {
						 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
						 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
						 Node node3 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
						 children = new Node[]{node1, node2, node3};
					 }
					 else if(this.wasUp) {
						 
						 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
						 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
						 Node node3 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
						 children = new Node[]{node1, node2, node3};
					 }
					 else if(this.wasLeft) {
						 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
						 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift						
						 Node node3 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
						 children = new Node[]{node1, node2, node3};
					 }
					 else if(this.wasRight){
						 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
						 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
						 Node node3 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift						 
						 children = new Node[]{node1, node2, node3};
					 }
				 }
			 }
			 else if(this.possibleMoves == 3) {
				 if(this.level == 0) {
					 if(this.x == 0 && this.y == 1) {
						 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift					 
						 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
						 Node node3 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
						 children = new Node[]{node1, node2, node3};
					 }
					 else if(this.x == 1 && this.y == 0) {
						 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
						 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
						 Node node3 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift						 
						 children = new Node[]{node1, node2, node3};
					 }
					 else if(this.x == 1 && this.y == 2) {
						 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
						 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift						 
						 Node node3= new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
						 children = new Node[]{node1, node2, node3};
					 }
					 else if(this.x == 2 && this.y == 1) {
						 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
						 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
						 Node node3 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
						 children = new Node[]{node1, node2, node3};
					 }
					
				 }
				 else {
					 if(this.x == 0 && this.y == 1) {
						 if(this.wasDown) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift					 
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
							 Node node3 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2, node3};
						 }
						 else if(this.wasUp) {							 		 
							 Node node1 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2};							
						 }
						 else if(this.wasLeft) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift					 
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2};
						 }
						 else if(this.wasRight){
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift					 
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift							 
							 children = new Node[]{node1, node2};
						 }
						 
					 }
					 else if(this.x == 1 && this.y == 0) {
						 if(this.wasDown) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift							 
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift						 
							 children = new Node[]{node1, node2};
						 }
						 else if(this.wasUp) {							 
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift						 
							 children = new Node[]{node1, node2};
							
						 }
						 else if(this.wasLeft) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
							 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift												 
							 children = new Node[]{node1, node2};
						 }
						 else if(this.wasRight){
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
							 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
							 Node node3 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift						 
							 children = new Node[]{node1, node2, node3};
						 }
					 }
					 else if(this.x == 1 && this.y == 2) {
						 if(this.wasDown) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift							 					 
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2};
						 }
						 else if(this.wasUp) {							 
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift						 
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2};
							
						 }
						 else if(this.wasLeft) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
							 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift						 
							 Node node3= new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2, node3};
						 }
						 else if(this.wasRight){
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
							 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift							 
							 children = new Node[]{node1, node2};
						 }
					 }
					 else if(this.x == 2 && this.y == 1) {
						 if(this.wasDown) {							 
							 Node node1 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2};
						 }
						 else if(this.wasUp) {
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
							 Node node3 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2, node3};
							
						 }
						 else if(this.wasLeft) {
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift							 
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1, node2};
						 }
						 else if(this.wasRight){
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
							 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift							 
							 children = new Node[]{node1, node2};
						 }
					 }
				 }
			 }
			 else if(this.possibleMoves == 2) {
				 if(this.level == 0) {
					 if(this.x == 0 && this.y == 0) {
						 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift					 
						 Node node2 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
						 children = new Node[]{node1, node2};
					 }
					 else if(this.x == 0 && this.y == 2) {
						 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift
						 Node node2 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift 
						 children = new Node[]{node1, node2};
					 }
					 else if(this.x == 2 && this.y == 0) {
						 Node node1 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
						 Node node2 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift						 
						 
						 children = new Node[]{node1, node2};
					 }
					 else if(this.x == 2 && this.y == 2) {
						 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift
						 Node node2 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
						 children = new Node[]{node1, node2};
					 }
					
				 }
				 else {
					 if(this.x == 0 && this.y == 0) {
						 if(this.wasDown) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift	 							 
							 children = new Node[]{node1};
						 }
						 else if(this.wasUp) {							 				 
							 Node node1 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift
							 children = new Node[]{node1};
							
						 }
						 else if(this.wasLeft) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift 							 
							 children = new Node[]{node1};
						 }
						 else if(this.wasRight){
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift			 							
							 children = new Node[]{node1};
						 }
						 
					 }
					 else if(this.x == 0 && this.y == 2) {
						 if(this.wasDown) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift							 
							 children = new Node[]{node1};
						 }
						 else if(this.wasUp) {							 
							 Node node1 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift 
							 children = new Node[]{node1};
							
						 }
						 else if(this.wasLeft) {
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift							 
							 children = new Node[]{node1};
						 }
						 else if(this.wasRight){
							 Node node1 = new Node(copyArray(this.swap(this.x + 1, this.y)), this, this.level + 1); // Down shift							 
							 children = new Node[]{node1};
						 }
					 }
					 else if(this.x == 2 && this.y == 0) {
						 if(this.wasDown) {
							 Node node1 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift							 				 							 
							 children = new Node[]{node1};
						 }
						 else if(this.wasUp) {
							 Node node1 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift										 							 
							 children = new Node[]{node1};
							
						 }
						 else if(this.wasLeft) {							
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift					 							 
							 children = new Node[]{node1};
						 }
						 else if(this.wasRight){
							 Node node1 = new Node(copyArray(this.swap(this.x, this.y + 1)), this, this.level + 1); // Right shift											 							 
							 children = new Node[]{node1};
						 }
					 }
					 else if(this.x == 2 && this.y == 2) {
						 if(this.wasDown) {							 
							 Node node1 = new Node(copyArray(this.swap(this.x, this.y - 1)), this, this.level + 1); // Left shift
							 children = new Node[]{node1};
						 }
						 else if(this.wasUp) {
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift							 
							 children = new Node[]{node1};
							
						 }
						 else if(this.wasLeft) {
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift							 
							 children = new Node[]{node1};
						 }
						 else if(this.wasRight){
							 Node node1 = new Node(copyArray(this.swap(this.x - 1, this.y)), this, this.level + 1); // Up shift							 
							 children = new Node[]{node1};
						 }
					 }
				 }
			 }
			 
		 
			 
			return children;
			 
		 }
		 
		 public void findPrevious() { // Finds the move used to reach the current state
			 
			 if(this.possibleMoves == 4) {
				 if(Arrays.deepEquals(this.swap(x + 1, y), parent.matrix)) { // Down swap
					 this.wasUp = true;
					 return;
				 }
				 else if(Arrays.deepEquals(this.swap(x - 1, y), parent.matrix)) { // Up swap
					 this.wasDown = true;
					 return;
				 }
				 else if(Arrays.deepEquals(this.swap(x, y + 1), parent.matrix)) { // Right swap
					 this.wasLeft = true;
					 return;
				 }
				 else if(Arrays.deepEquals(this.swap(x, y - 1), parent.matrix)) { // Left swap
					 this.wasRight = true;
					 return;
				 }
			 }
			 else if(this.possibleMoves == 3) {
				 if(this.x == 0 && this.y == 1) {
					 if(Arrays.deepEquals(this.swap(x + 1, y), parent.matrix)) { // Down swap
						 this.wasUp = true;
						 return;
					 }
					 
					 else if(Arrays.deepEquals(this.swap(x, y + 1), parent.matrix)) { // Right swap
						 this.wasLeft = true;
						 return;
					 }
					 else if(Arrays.deepEquals(this.swap(x, y - 1), parent.matrix)) { // Left swap
						 this.wasRight = true;
						 return;
					 }
				 }
				 else if(this.x == 1 && this.y == 0) {
					 if(Arrays.deepEquals(this.swap(x + 1, y), parent.matrix)) { // Down swap
						 this.wasUp = true;
						 return;
					 }
					 else if(Arrays.deepEquals(this.swap(x - 1, y), parent.matrix)) { // Up swap
						 this.wasDown = true;
						 return;
					 }
					 else if(Arrays.deepEquals(this.swap(x, y + 1), parent.matrix)) { // Right swap
						 this.wasLeft = true;
						 return;
					 }
					 
				 }
				 else if(this.x == 1 && this.y == 2) {
					 if(Arrays.deepEquals(this.swap(x + 1, y), parent.matrix)) { // Down swap
						 this.wasUp = true;
						 return;
					 }
					 else if(Arrays.deepEquals(this.swap(x - 1, y), parent.matrix)) { // Up swap
						 this.wasDown = true;
						 return;
					 }
					 
					 else if(Arrays.deepEquals(this.swap(x, y - 1), parent.matrix)) { // Left swap
						 this.wasRight = true;
						 return;
					 }
				 }
				 else if(this.x == 2 && this.y == 1) {
					 
					 if(Arrays.deepEquals(this.swap(x - 1, y), parent.matrix)) { // Up swap
						 this.wasDown = true;
						 return;
					 }
					 else if(Arrays.deepEquals(this.swap(x, y + 1), parent.matrix)) { // Right swap
						 this.wasLeft = true;
						 return;
					 }
					 else if(Arrays.deepEquals(this.swap(x, y - 1), parent.matrix)) { // Left swap
						 this.wasRight = true;
						 return;
					 }
				 }
			 }
			 else if (this.possibleMoves == 2) {
				
				 if(this.x == 0 && this.y == 0) { // Upper Left corner
					 if(Arrays.deepEquals(this.swap(x + 1, y), parent.matrix)) { // Down swap
						 this.wasUp = true;
						 return;
					 }
					
					 else if(Arrays.deepEquals(this.swap(x, y + 1), parent.matrix)) { // Right swap
						 this.wasLeft = true;
						 return;
					 }
					 
				 }
				 else if(this.x == 0 && this.y == 2) { // Upper Right corner
					
					 if(Arrays.deepEquals(this.swap(x + 1, y), parent.matrix)) { // Down swap
						 this.wasUp = true;
						 return;
					 }
					
					 else if(Arrays.deepEquals(this.swap(x, y - 1), parent.matrix)) { // Left swap
						 this.wasRight = true;
						 return;
					 }
					 
				 }
				 else if(this.x == 2 && this.y == 0) { // Bottom Left corner
					 
					  if(Arrays.deepEquals(this.swap(x - 1, y), parent.matrix)) { // Up swap
						 this.wasDown = true;
						 return;
					 }
					 else if(Arrays.deepEquals(this.swap(x, y + 1), parent.matrix)) { // Right swap
						 this.wasLeft = true;
						 return;
					 }
					 
				 }
				 else if(this.x == 2 && this.y == 2) { // Bottom Right corner
					
					 if(Arrays.deepEquals(this.swap(x - 1, y), parent.matrix)) { // Up swap
						 this.wasDown = true;
						 return;
					 }
					
					 else if(Arrays.deepEquals(this.swap(x, y - 1), parent.matrix)) { // Left swap
						 this.wasRight = true;
						 return;
					 }
				}
			 }
			 
			 
		 }
		 
		 public void findBlank() { // Finds the coordinates of the blank space
			 for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 3; j++) {
						if(this.matrix[i][j] == "B") {
							this.x = i;
							this.y = j;
							break;
						}
					}
				}
		 }
		 
		 public int movesPossible() { // Calculates the number of possible moves for a node given the location of the blank space
			 int blankX = this.x, blankY = this.y;
			 int moves = 0;
			 
			 if(blankX == 1 && blankY == 1) { // If blank is in the center
				 moves = 4;
			 }
			 else if((blankX == 0 && blankY == 0)||(blankX == 2 && blankY == 2) || (blankX == 0 && blankY == 2) || (blankX == 2 && blankY == 0)) { // If blank is in the corners
				 moves = 2;
			 }
			 else { // If blank is in the middle of one of the edges
				 moves = 3;
			 }
			 
			
			 return moves;
			 
		 }
		 
		 public static String[][] generateStart() { // Generates a starting matrix
		Random rand = new Random();
		String[][] testMatrix = new String[3][3]; 
		
		do {
			for(int i = 0; i < 3; i++) { // Resets the matrix
				for(int j = 0; j < 3; j++) {
					testMatrix[i][j] = "0";
				}
			}
			int x = rand.nextInt(3); // X-coordinate for the blank space
			int y = rand.nextInt(3); // Y-coordinate for the blank space
			testMatrix[x][y] = "B";
			List<Integer> fill = new ArrayList<Integer>(); // List that holds 1-8 so it can be added to the matrix
			for(int i = 1; i < 9; i++) {
				fill.add(i - 1, i);
			}
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 3; j++) {
					if(testMatrix[i][j] == "B") {
						continue;
					}
					int temp = rand.nextInt(fill.size());
					testMatrix[i][j] = fill.get(temp).toString(); // Populates the matrix
					fill.remove(temp);										
				}			
			}			
		}
		while(isSolvable(testMatrix) == false);
		return testMatrix;
	}
		 
		 public static boolean isSolvable(String[][] mat) { // Checks to see if a given starting state is solvable                       
		int inversions = 0;
		
		int[] linearMatrix = new int[8]; // Used to create a single dimensional array from the 2 dimensional array
		int place = 0; 
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(mat[i][j] == "B") {
					continue;
				}
				linearMatrix[place] = Integer.parseInt(mat[i][j]);
				place++;
				
			}
			
		}
		
		for(int i = 0; i < 8; i++) { // Checks the number of inversions in the given starting state
			for(int j = i; j < 8; j++) {
				if (linearMatrix[i] > linearMatrix[j]) {
					inversions++;
				}
			}
		}
		
		if (inversions % 2 == 0) {
			return false;  	
		}
		else { 
			return true;  
		}
		
	}
		
		 public String[][] swap(int newX, int newY){ // Swaps two values in the matrix
			 String[][] newMatrix = copyArray(this.matrix);
			 String temp = newMatrix[newX][newY];
			 newMatrix[this.x][this.y] = temp;
			 newMatrix[newX][newY] = "B";
			 return newMatrix;
			 
		 }
		 
		 public void determineCost() { // Determines the cost of a node
			 if(this.matrix[0][0].equals("1") == false) {
				 this.cost++;
			 }
			 if(this.matrix[0][1].equals("2") == false ) {
				 this.cost++;
			 }
			 if(this.matrix[0][2].equals("3") == false) {
				 this.cost++;
			 }
			 if(this.matrix[1][0].equals("8") == false) {
				 this.cost++;
			 }
			 if(this.matrix[1][2].equals("4") == false) {
				 this.cost++;
			 }
			 if(this.matrix[2][0].equals("7") == false) {
				 this.cost++;
			 }
			 if(this.matrix[2][1].equals("6") == false) {
				 this.cost++;
			 }
			 if(this.matrix[2][2].equals("5") == false) {
				 this.cost++;
			 }
			 this.cost += this.level;
			
		 }
		
		 			 
	}
	
	public static void printPath(Node n) { // Prints the path from the start to the goal node
		Stack<Node> s = new Stack<Node>();
		while(n.parent != null) {
			s.push(n);			
			n = n.parent;
			
		}
		s.push(n);
		System.out.println("path size: " + s.size());
		while(s.isEmpty() == false) {
			
			printMatrix(s.pop().matrix);
			System.out.println();
		}
		
		
		
	}
	
	public static boolean reachedGoal(String[][] mat) { // Checks if a node has reached the goal state
			 if (Arrays.deepEquals(goalState, mat)) {
				 return true;
			 }
			 else {
				 return false;
			 }
		 }	
	
	public static void printMatrix(String[][] mat) { // Prints a node's matrix to the console
			 for(int i = 0; i < 3; i ++) {
				 System.out.print("{");
				 for(int j = 0; j < 3; j++) {
					 if(j == 2) {
						 System.out.print(mat[i][j]);
					 }
					 else {
						System.out.print(mat[i][j] + ", "); 
					 }
					
				 }
				 System.out.println("}");
			 } 
		 }
	
	public static void DFS(Node root) { // Depth First Search
		int limit = 25;
		Stack<Node> s = new Stack<Node>();
		List<Node> visited = new ArrayList<Node>(); 
		s.add(root);
		while(s.isEmpty() == false) { // While the stack is not empty
			
				Node currentNode = s.pop();
				if(reachedGoal(currentNode.matrix) == true) { // If the goal is reached
					printPath(currentNode);
					System.out.println("The goal has been found using Depth First Search");
					System.out.println("Goal Depth: " + currentNode.level);
					System.out.println("Nodes visited: " + visited.size());					
					return;
				}
				else {					
					visited.add(currentNode);
					Node[] children = currentNode.generateChildren();
					for(int i = 0; i < children.length; i++) {				
							if(currentNode.level == limit) {
						 continue;
					}				 
						s.add(children[i]);
					}
			
				}
		
		}
		
		  System.out.println("Unable to find goal with a depth limit of " + limit);
		  
	}
	
	public static void BFS(Node root) { // Breadth First Search
		Queue<Node> q = new LinkedList<Node>();
		List<Node> visited = new ArrayList<Node>(); 
		q.add(root);		
		while(q.isEmpty() == false) { // While the queue is not empty
				Node currentNode = q.poll();
				
				if(reachedGoal(currentNode.matrix) == true) { // If the goal state is reached
					printPath(currentNode);
					System.out.println("The goal has been found using Breadth First Search");
					System.out.println("Goal Depth: " + currentNode.level);
					System.out.println("Nodes visited: " + visited.size());					
					return;
				}
				else {					
					visited.add(currentNode);
					Node[] children = currentNode.generateChildren();
					for(int i = 0; i < children.length; i++) {				
						q.add(children[i]);
					}					
				}			
		}		
		
	}
	
	public static void UCS(Node root) { // Uniform Cost Search
		PriorityQueue<Node> q = new PriorityQueue<Node>(new LevelComparator());
		List<Node> visited = new ArrayList<Node>(); 
		q.add(root);		
		while(q.isEmpty() == false) { // While the Priority Queue is not empty
				Node currentNode = q.poll();
				
				if(reachedGoal(currentNode.matrix) == true) { // If the goal state is reached
					printPath(currentNode);
					System.out.println("The goal has been found using Uniform Cost Search");
					System.out.println("Goal Depth: " + currentNode.level);
					System.out.println("Nodes visited: " + visited.size());					
					return;
				}
				else {					
					visited.add(currentNode);
					Node[] children = currentNode.generateChildren();
					for(int i = 0; i < children.length; i++) {				
						q.add(children[i]);
						
					}											
				}				
		}	
	}
	
	public static void AStar(Node root){ // A* Search
		PriorityQueue<Node> q = new PriorityQueue<Node>(new CostComparator());		
		List<Node> visited = new ArrayList<Node>(); 
		q.add(root);		
		while(q.isEmpty() == false) { // While the Priority Queue is not empty
				Node currentNode = q.remove();				
				if(reachedGoal(currentNode.matrix) == true) { // If the goal state is reached
					printPath(currentNode);
					System.out.println("The goal has been found using A* Search");
					System.out.println("Goal Depth: " + currentNode.level);
					System.out.println("Nodes visited: " + visited.size());					
					return;
				}
				else {					
					visited.add(currentNode);
					Node[] children = currentNode.generateChildren();					
					for(int i = 0; i < children.length; i++) {				
						q.add(children[i]);
						
					}											
				}				
		}	
	}
	
	public static void main(String[] args) {
		
		Node start = new Node();		
		long beginning = System.nanoTime();
		DFS(start); // Run Depth First Search
		long end = System.nanoTime();
		long runtime = (end - beginning)/1000000;
		System.out.println("Runtime of DFS: " + runtime + " milliseconds \n");
		
		beginning = System.nanoTime();
		UCS(start); // Run Uniform Cost Search
		end = System.nanoTime();
		runtime = (end - beginning)/1000000;
		System.out.println("Runtime of UCS: " + runtime + " milliseconds \n");
		
		beginning = System.nanoTime();
		BFS(start);	// Run Breadth First Search
		end = System.nanoTime();
		runtime = (end - beginning)/1000000;
		System.out.println("Runtime of BFS: " + runtime + " milliseconds \n");
		
		beginning = System.nanoTime();
		AStar(start); // Run A* Search
		end = System.nanoTime();
		runtime = (end - beginning)/1000000;
		System.out.println("Runtime of A*: " + runtime + " milliseconds \n");
		
		
	}

}
