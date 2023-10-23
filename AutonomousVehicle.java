import java.io.IOException;
import java.util.ArrayList;
import java.util.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import java.util.Scanner;
//import java.awt.Insets;
import java.io.*;
import java.util.concurrent.TimeUnit;
import javafx.stage.Stage;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.*;
import java.lang.Object;
import javafx.geometry.Insets;
//import java.awt.*;
//import java.awt.Label;
import javafx.animation.*;
import javafx.concurrent.Task;

public class AutonomousVehicle extends Application {

	
	static int[][] grid1 = new int [][] {
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 3, 0, 1, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
		};

		private static ArrayList<searchNode> frontierSet = new ArrayList<searchNode>();
		private static ArrayList<searchNode> exploredSet = new ArrayList<searchNode>();
		private static ArrayList<searchNode> goalPath = new ArrayList<searchNode>();
		private static ArrayList<searchNode> currentPath = new ArrayList<searchNode>();
		private static int currentMaxDepth;
		private static searchNode expansionNode;
		private static searchNode testNode;
		private static Group root;
		private static Scene scene;
		private static GridPane map;
		private static  VBox mapBox;
		private static Stage stage;
		private static Timeline timeline;
		private static double timelineTime=0;
		private static Label label= new Label(); 
		private static Label depthText= new Label("Test 1");
		
		
		//Print Map - to be run each time node added to explored set
		public static void printMap() {
		
	
		 map = new GridPane();
		 
		 for (int i=0;i<grid1.length;i++) {
				for (int j=0; j<grid1[0].length;j++) {
				 
					
				 int gridVal=grid1[i][j];
				 Label gridString = new Label("      ");
				 int foundCell=0;
				 //Check current i and j location against current path
				 //If grid cell is on path, colour it blue
				 for (int z=0;z<currentPath.size()-1;z++) {
					 testNode=currentPath.get(z);
					 int x=currentPath.get(z).getX();
					 int y=currentPath.get(z).getY();
					 if(i==x && j==y &&gridVal!=2) {
						 gridString.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY,Insets.EMPTY)));
						
						foundCell=1;
					 }
				 }
				 //If grid cell not on current path and is a road block, colour it black 
					  if (foundCell==0 && gridVal==1) {
						 gridString.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY,Insets.EMPTY)));
						 
					 }
				//If grid cell not on current path, and is starting location, colour it gold	  
					 else if (foundCell==0 && gridVal==2) {
						 gridString.setBackground(new Background(new BackgroundFill(Color.GOLD, CornerRadii.EMPTY,Insets.EMPTY)));
					 }
				//If grid cell not on current path and is goal location, colour it green	  
					 else if (foundCell==0 && gridVal==3) {
						 gridString.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY,Insets.EMPTY)));
							
					 }
					
				//Add the cell to gridpane
				 	map.add(gridString, i, j);		
				 }
				
			
		  }
			
		//Use runLater to allow parallel updating of gridpane in JavaFX 
		 Platform.runLater(()->label.setGraphic(map));
		 Platform.runLater(()->	depthText.setText("Current Maximum Depth: "+ currentMaxDepth));
		 
		 try
		 { 
		  Thread.sleep(50);
		 } 
		  catch(InterruptedException e)
		 { 
		 } 
	
	}
		
	
	public void start (Stage stage) throws IOException{
		

			root = new Group();
       		scene = new Scene(root, 700, 500);      	
       		mapBox = new VBox();
       		Button start = new Button("Start");
       		Text titleText = new Text("Iterative Deepening Search");
       		
       		//Run iterative deepening algorithm when start button clicked
       		start.setOnAction(e-> iterativeDeepening());
       		//Add 
       		mapBox.getChildren().addAll(titleText,depthText,start, label);
       		root.getChildren().add(mapBox);
       		
       		stage.setTitle("Iterative Deepening Search");
       		stage.setScene(scene);      
      		stage.show();
	     
      		
      		printMap();
      		
      	
	}
	
	public static void iterativeDeepening() {
		currentMaxDepth=0;
		
		Thread thread1 = new Thread(new Task<Void>() {
				
			 @Override
			 protected Void call() 
			{ 
			//Set depth limit to 20
				 outer: while (currentMaxDepth<20) {
	
					 //Reset frontier and explored set each time depth is incremented
						frontierSet.clear();
						exploredSet.clear();
						//Initialise root node and add to frontier 
						searchNode rootNode= new searchNode(2,3,0,0);
						frontierSet.add(rootNode);
						
					
						//Explore all nodes in frontier set until no more options
						while (frontierSet.size()>0) {
						
							//Only proceed to expand node if at depth less than current tree search depth
							if (frontierSet.get(0).getDepth()<=currentMaxDepth) {
								
								//LIFO so take last node in frontier to explore
								int lastNodeIndex=frontierSet.size()-1;
								expansionNode=frontierSet.remove(lastNodeIndex);
								
								//Get coordinates and depth of expansion node
								int frontierX= expansionNode.getX();
								int frontierY= expansionNode.getY();
								int frontierDepth= expansionNode.getDepth();
								int gridVal=grid1[frontierX][frontierY];
						
								
								//If node has not already been explored at current or less than current depth, proceed to explore
								if (checkExploredSet(frontierX, frontierY, frontierDepth)==0) {
									exploredSet.add(expansionNode);
									updateCurrentPath();
									
								//If node is goal node, break loop and call findGoalPath method	
									if (gridVal==3) {
										System.out.println("Goal Found at depth: "+ currentMaxDepth);
										findGoalPath();
										break outer;
									}
								//If node is 'traffic jam' do not explore this path further	
									else if (gridVal==1) {
									//	System.out.println("Traffic Jam Encountered - Do not expand node");
														}
									
								//If current node is at depth less than current max depth, add neighbouring cells to frontier (they exist)
									
									else if (frontierDepth<currentMaxDepth){
									
										if (frontierX>0) frontierSet.add(new searchNode(frontierX-1,frontierY, frontierDepth+1,(exploredSet.size()-1)));
										if (frontierX<14)frontierSet.add(new searchNode(frontierX+1,frontierY, frontierDepth+1,(exploredSet.size()-1)));
										if (frontierY>0)frontierSet.add(new searchNode(frontierX,frontierY-1, frontierDepth+1,(exploredSet.size()-1)));
										if (frontierY<14)frontierSet.add(new searchNode(frontierX,frontierY+1, frontierDepth+1,(exploredSet.size()-1)));
										
								}
								
								}
								else {
								}
							}
							//If current cell greater than current maximum search depth, remove and do not explore
							else {
								frontierSet.remove(frontierSet.size()-1);
							}
				
					}
					
						
					currentMaxDepth+=1;
				 }
			 try
			 { 
			  Thread.sleep(5000);
			 } 
			  catch(InterruptedException e)
			 { 
			 } 
			  
			 return null;
			}
			
		 });
	
	//Use thread to allow simultaneous updating of gridpane
	thread1.start();
	
		
	
		
	
	

	}
	
	
	//Print current explored set to console
	public static void printExploredSet() {
		for (int i=0; i<exploredSet.size();i++) {
			exploredSet.get(i).printNode();
		}
	}

	//Print goal path to console
	public static void printGoalPath() {
		for (int i=0; i<goalPath.size();i++) {
			goalPath.get(i).printNode();
		}
	}
	
	//
	public static void findGoalPath() {
		
		//Get goal path node and add to goalPath array list
		searchNode goalPathNode=exploredSet.get(exploredSet.size()-1);
		goalPath.add(goalPathNode);
		
		//Find parent  node index 
		int nextNodeIndex=goalPathNode.getNodeIndex();
		int currentDepth=goalPathNode.getDepth();
		
		
		//Add all nodes up to route node 
		while (currentDepth>=0) {
			goalPathNode=exploredSet.get(nextNodeIndex);
			goalPath.add(goalPathNode);
			nextNodeIndex=goalPathNode.getNodeIndex();
			currentDepth-=1;
		//	System.out.println(currentDepth);
		}
		
		Text goalFound = new Text("Optimal Route Found");
		Platform.runLater(()->mapBox.getChildren().add(goalFound));
		printGoalPath();
		
	}
	
	public static void printCurrentPath() {
		for (int i=0; i<currentPath.size();i++) {
			currentPath.get(i).printNode();
		}
	}
	
	public static void updateCurrentPath() {
		searchNode currentPathNode=exploredSet.get(exploredSet.size()-1);
		currentPath.clear();
		currentPath.add(currentPathNode);
		int nextNodeIndex=currentPathNode.getNodeIndex();
		int currentDepth=currentPathNode.getDepth();
		
		
		while (currentDepth>=0) {
			currentPathNode=exploredSet.get(nextNodeIndex);
			currentPath.add(currentPathNode);
			nextNodeIndex=currentPathNode.getNodeIndex();
			currentDepth-=1;
	
		}

	 printMap();
	}

	
	//Check if current node has already been visited at same or less than current depth
	public static int checkExploredSet(int xSearch, int ySearch, int depthSearch) {
		for (int i=0; i<exploredSet.size();i++) {
			testNode=exploredSet.get(i);
			int x=testNode.getX();
			int y=testNode.getY();
			int depth=testNode.getDepth();
			if (x==xSearch && y==ySearch && depth<=depthSearch) {
				return 1;
			}
		}
		return 0;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
		
	}

}



 class searchNode {

	int x;
	int y;
	int depth;
	int nodeIndex;
	
	public searchNode(int xIn, int yIn, int depthIn, int nodeIndexIn) {
		x=xIn;
		y=yIn;
		depth=depthIn;
		nodeIndex=nodeIndexIn;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getDepth() {
		return depth;
	}
	
	public int getNodeIndex(){
		return nodeIndex;
	}
	
	public void printNode() {
		System.out.println("X: "+ x+ " Y: "+ y+ " Depth: "+ depth + " Parent Node:"+ nodeIndex); 
	}
	
	
}
