package code;

import java.util.*;

public class CoastGuard {
    public static void main(String[] args) throws Exception {

        // System.out.println("Coast Guard");
        CoastGuard cg = new CoastGuard();
        // System.out.println(cg.genGrid());
        // SearchTree tree = new SearchTree("6,7;82;1,4;2,3;1,1,58,3,0,58,4,2,72;");
        // System.out.println(tree.size);
        bfs("10,6;59;1,7;0,0,2,2,3,0,5,3;1,3,69,3,4,80,4,7,94,4,9,14,5,2,39;", true);

    }

    public String genGrid() {
        String gridString = "";
        // random number between 5 and 15 inclusive
        int m = (int) (Math.random() * 11) + 5;
        // random number between 5 and 15 inclusive
        int n = (int) (Math.random() * 11) + 5;

        gridString += m + "," + n + ";";

        // Generate all grid positions in a list
        List<String> gridPositions = new ArrayList<String>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                gridPositions.add(i + "," + j);
            }
        }
        // ship capacity random number between 30 and 100 inclusive
        int c = (int) (Math.random() * 71) + 30;

        gridString += c + ";";

        // generate coast guard position in the grid
        int x = (int) (Math.random() * m);
        int y = (int) (Math.random() * n);
        // remove taken position from the grid
        gridPositions.remove(x + "," + y);

        gridString += x + "," + y + ";";

        // generate random number of stations from the remaining grid positions
        int t = (int) (Math.random() * (gridPositions.size())) + 1;
        for (int i = 0; i < t; i++) {
            int index = (int) (Math.random() * gridPositions.size());
            if (i == t - 1) {
                gridString += gridPositions.get(index);
            } else {
                gridString += gridPositions.get(index) + ",";
            }
            gridPositions.remove(index);
        }

        gridString += ";";

        // generate random number of ships from the remaining grid positions
        int s = (int) (Math.random() * gridPositions.size()) + 1;
        for (int i = 0; i < s; i++) {
            // generate random capacity for each ship between 0 and 100 inclusive
            int capacity = (int) (Math.random() * 101);
            int index = (int) (Math.random() * gridPositions.size());
            if (i == s - 1) {
                gridString += gridPositions.get(index) + "," + capacity;
            } else {
                gridString += gridPositions.get(index) + "," + capacity + ",";
            }
            gridPositions.remove(index);
        }

        gridString += ";";

        return gridString;
    }

    public static String solve(String grid, String strategy, Boolean visualize) {
        Node goal = null;
        if (strategy.equals("BF")) {
            goal = bfs(grid, visualize);
        } else if (strategy.equals("DF")) {
            goal = dfs(grid, visualize);
        } else if (strategy.equals("ID")) {
            goal = ids(grid, visualize);
        } else if (strategy.equals("UC")) {
            goal = ucs(grid, visualize);
        } else if (strategy.equals("A*")) {
            // astar(grid, visualize);
        } else if (strategy.equals("Greedy")) {
            // greedy(grid, visualize);
        }

        String path = "";
        // System.out.println(goal);
        Node current = goal;
        int numberOfnodes = 0;
        int numberOfCollectedBlackboxes = 0;
        if (goal != null) {
            while (current.parent != null) {
                numberOfnodes++;
                if (current.action.equals("retrieve")) {
                    System.out.println(current.parent.parent.action + " " + current.parent.ships.get(0).damage);
                    numberOfCollectedBlackboxes++;
                }
                // System.out.println(current.action +" "+
                // current.numberOfPeopleOntheCoastGuard+" " +current.numberOfdeath+" "+
                // (current.ships.size() !=0? current.ships.get(0).numberOfPeopleOntheShip :0));
                if (current == goal) {
                    path = current.action + path;
                } else {
                    path = current.action + "," + path;
                }
                current = current.parent;
            }
            return path + ";" + goal.numberOfdeath + ";" + numberOfCollectedBlackboxes + ";" + numberOfnodes;
        }

        return "";
    }

    public static Node bfs(String grid, Boolean visualize) {
        // creating the root node of the tree
        // splitting the grid string into an array of strings
        String[] gridArray = grid.split(";");
        // Get grid size
        String[] gridSize = gridArray[0].split(",");
        int gridWidth = Integer.parseInt(gridSize[0]);
        int gridLength = Integer.parseInt(gridSize[1]);
        int capacity = Integer.parseInt(gridArray[1]);

        // Get location of the boat
        String[] boatLocation = gridArray[2].split(",");

        int boatX = Integer.parseInt(boatLocation[0]);
        int boatY = Integer.parseInt(boatLocation[1]);

        ArrayList<Station> stations = getStations(gridArray);
        HashSet<Node> visitedSet;
        ArrayList<Ship> ships = getShips(gridArray);

        Node root = new Node();
        root.x = boatX;
        root.y = boatY;
        root.depth = 0;
        root.ships = ships;
        // System.out.println("Root node created" + root.x + " " + root.y);
        visitedSet = new HashSet<Node>();
        visitedSet.add(root);

        // create a queue for BFS
        LinkedList<Node> queue = new LinkedList<Node>();

        // add root node to the queue
        queue.add(root);

        // while queue is not empty
        while (queue.size() != 0) {
            // remove the first node from the queue
            Node node = queue.poll();
            // System.out.println("Node removed from queue" + node.x + " " + node.y);
            // if the node is the goal node
            if (node.ships.size() == 0 && node.numberOfPeopleOntheCoastGuard == 0) {
                // System.out.println("Goal node found");
                // return the node
                return node;
            }

            // get the children of the node
            ArrayList<Node> children = node.getChildren(capacity, gridWidth, gridLength, stations);
            // System.out.println("Children generated");
            // for each child
            for (Node child : children) {
                // if the child is not in the visited set
                if (!visitedSet.contains(child)) {
                    // get the action of the node
                    switch (child.action) {
                        case "up":
                            node.up = child;
                            break;
                        case "down":
                            node.down = child;
                            break;
                        case "left":
                            node.left = child;
                            break;
                        case "right":
                            node.right = child;
                            break;
                        case "pickup":
                            node.pickup = child;
                            break;
                        case "dropoff":
                            node.dropoff = child;
                            break;
                        case "retrieve":
                            node.retrieve = child;
                            break;
                    }
                    // add the child to the visited set
                    visitedSet.add(child);
                    // add the child to the queue
                    queue.add(child);
                }
            }
        }

        return null;

        // buildTree(this.root);
    }

    public static Node dfs(String grid, Boolean visualize) {
        // creating the root node of the tree
        // splitting the grid string into an array of strings
        String[] gridArray = grid.split(";");
        // Get grid size
        String[] gridSize = gridArray[0].split(",");
        int gridWidth = Integer.parseInt(gridSize[0]);
        int gridLength = Integer.parseInt(gridSize[1]);
        int capacity = Integer.parseInt(gridArray[1]);

        // Get location of the boat
        String[] boatLocation = gridArray[2].split(",");

        int boatX = Integer.parseInt(boatLocation[0]);
        int boatY = Integer.parseInt(boatLocation[1]);

        ArrayList<Station> stations = getStations(gridArray);
        HashSet<Node> visitedSet;
        ArrayList<Ship> ships = getShips(gridArray);

        Node root = new Node();
        root.x = boatX;
        root.y = boatY;
        root.depth = 0;
        root.ships = ships;
        // System.out.println("Root node created" + root.x + " " + root.y);
        visitedSet = new HashSet<Node>();
        visitedSet.add(root);

        // create a stack for DFS
        Stack<Node> stack = new Stack<Node>();

        // add root node to the stack
        stack.push(root);

        // while stack is not empty
        while (stack.size() != 0) {
            // remove the first node from the stack
            Node node = stack.pop();
            // System.out.println("Node removed from stack" + node.x + " " + node.y);
            // if the node is the goal node
            if (node.ships.size() == 0 && node.numberOfPeopleOntheCoastGuard == 0) {
                // System.out.println("Goal node found");
                // return the node
                return node;
            }

            // get the children of the node
            ArrayList<Node> children = node.getChildren(capacity, gridWidth, gridLength, stations);
            // System.out.println("Children generated");
            // for each child
            for (Node child : children) {
                // if the child is not in the visited set
                if (!visitedSet.contains(child)) {
                    // get the action of the node
                    switch (child.action) {
                        case "up":
                            node.up = child;
                            break;
                        case "down":
                            node.down = child;
                            break;
                        case "left":
                            node.left = child;
                            break;
                        case "right":
                            node.right = child;
                            break;
                        case "pickup":
                            node.pickup = child;
                            break;
                        case "dropoff":
                            node.dropoff = child;
                            break;
                        case "retrieve":
                            node.retrieve = child;
                            break;
                    }
                    // add the child to the visited set
                    visitedSet.add(child);
                    // add the child to the stack
                    stack.push(child);
                }
            }
        }

        return null;

    }

    // Iterative Deepening Search
    public static Node ids(String grid, Boolean visualize) {

        // creating the root node of the tree
        // splitting the grid string into an array of strings
        String[] gridArray = grid.split(";");
        // Get grid size
        String[] gridSize = gridArray[0].split(",");
        int gridWidth = Integer.parseInt(gridSize[0]);
        int gridLength = Integer.parseInt(gridSize[1]);
        int capacity = Integer.parseInt(gridArray[1]);

        // Get location of the boat
        String[] boatLocation = gridArray[2].split(",");

        int boatX = Integer.parseInt(boatLocation[0]);
        int boatY = Integer.parseInt(boatLocation[1]);

        ArrayList<Station> stations = getStations(gridArray);
        HashSet<Node> visitedSet;
        ArrayList<Ship> ships = getShips(gridArray);

        // create max depth
        int maxDepth = 150;

        // create current depth
        int currentDepth = 0;

        // while current depth is less than max depth
        while (currentDepth <= maxDepth) {
            // create a stack for DFS
            Stack<Node> stack = new Stack<Node>();
            // System.out.println(currentDepth);
            // add root node to the stack
            Node root = new Node();
            root.x = boatX;
            root.y = boatY;
            root.depth = 0;
            root.ships = ships;
            // System.out.println("Root node created" + root.x + " " + root.y);
            visitedSet = new HashSet<Node>();
            visitedSet.add(root);
            stack.push(root);

            // while stack is not empty
            while (stack.size() != 0) {
                // remove the first node from the stack
                Node node = stack.pop();
                // System.out.println("Node removed from stack" + node.x + " " + node.y);
                // if the node is the goal node
                if (node.ships.size() == 0 && node.numberOfPeopleOntheCoastGuard == 0) {
                    // System.out.println("Goal node found");
                    // return the node
                    return node;
                }

                // check if the node is less than the current depth
                if (node.depth < currentDepth) {
                    // if the node is at the current depth
                    // get the children of the node
                    ArrayList<Node> children = node.getChildren(capacity, gridWidth, gridLength, stations);
                    // System.out.println("Children generated");
                    // for each child
                    for (Node child : children) {
                        // if the child is not in the visited set
                        if (!visitedSet.contains(child)) {
                            // get the action of the node
                            switch (child.action) {
                                case "up":
                                    node.up = child;
                                    break;
                                case "down":
                                    node.down = child;
                                    break;
                                case "left":
                                    node.left = child;
                                    break;
                                case "right":
                                    node.right = child;
                                    break;
                                case "pickup":
                                    node.pickup = child;
                                    break;
                                case "dropoff":
                                    node.dropoff = child;
                                    break;
                                case "retrieve":
                                    node.retrieve = child;
                                    break;
                            }
                            // add the child to the visited set
                            visitedSet.add(child);
                            // add the child to the stack
                            stack.push(child);
                        }
                    }
                }
            }
            // increment the current depth
            currentDepth++;
        }
        return null;

    }

    // Uniform Cost Search
    // The cost is the number of people who died
    public static Node ucs(String grid, Boolean visualize) {
        // creating the root node of the tree
        // splitting the grid string into an array of strings
        String[] gridArray = grid.split(";");
        // Get grid size
        String[] gridSize = gridArray[0].split(",");
        int gridWidth = Integer.parseInt(gridSize[0]);
        int gridLength = Integer.parseInt(gridSize[1]);
        int capacity = Integer.parseInt(gridArray[1]);

        // Get location of the boat
        String[] boatLocation = gridArray[2].split(",");

        int boatX = Integer.parseInt(boatLocation[0]);
        int boatY = Integer.parseInt(boatLocation[1]);

        ArrayList<Station> stations = getStations(gridArray);
        HashSet<Node> visitedSet;
        ArrayList<Ship> ships = getShips(gridArray);

        Node root = new Node();
        root.x = boatX;
        root.y = boatY;
        root.depth = 0;
        root.ships = ships;
        // System.out.println("Root node created" + root.x + " " + root.y);
        visitedSet = new HashSet<Node>();
        visitedSet.add(root);

        // create a priority queue for UCS
        PriorityQueue<Node> queue = new PriorityQueue<Node>(new NodeComparator());

        // add root node to the queue
        queue.add(root);

        // while queue is not empty
        while (queue.size() != 0) {
            // remove the first node from the queue
            Node node = queue.poll();
            // System.out.println("Node removed from queue" + node.x + " " + node.y);
            // if the node is the goal node
            if (node.ships.size() == 0 && node.numberOfPeopleOntheCoastGuard == 0) {
                // System.out.println("Goal node found");
                // return the node
                return node;
            }

            // get the children of the node
            ArrayList<Node> children = node.getChildren(capacity, gridWidth, gridLength, stations);
            // System.out.println("Children generated");
            // for each child
            for (Node child : children) {
                // if the child is not in the visited set
                if (!visitedSet.contains(child)) {
                    // get the action of the node
                    switch (child.action) {
                        case "up":
                            node.up = child;
                            break;
                        case "down":
                            node.down = child;
                            break;
                        case "left":
                            node.left = child;
                            break;
                        case "right":
                            node.right = child;
                            break;
                        case "pickup":
                            node.pickup = child;
                            break;
                        case "dropoff":
                            node.dropoff = child;
                            break;
                        case "retrieve":
                            node.retrieve = child;
                            break;
                    }
                    // add the child to the visited set
                    visitedSet.add(child);
                    // add the child to the queue
                    queue.add(child);
                }
            }
        }
        return null;
    }

    // A* Search
    // The cost is the number of people who died
    public static void astar(String grid, Boolean visualize) {

    }

    public static void greedy(String grid, Boolean visualize) {

    }

    public static ArrayList<Ship> getShips(String[] gridArray) {
        String[] shipPositions = gridArray[4].split(",");

        ArrayList<Ship> ships = new ArrayList<Ship>();

        for (int i = 0; i < shipPositions.length; i += 3) {
            Ship ship = new Ship();
            int shipX = Integer.parseInt(shipPositions[i]);
            int shipY = Integer.parseInt(shipPositions[i + 1]);
            int shipCapacity = Integer.parseInt(shipPositions[i + 2]);

            ship.positionXOftheShip = shipX;
            ship.positionYOftheShip = shipY;
            ship.numberOfPeopleOntheShip = shipCapacity;
            ships.add(ship);
        }

        return ships;
    }

    public static ArrayList<Station> getStations(String[] gridArray) {
        String[] stationPositions = gridArray[3].split(",");
        ArrayList<Station> stations = new ArrayList<Station>();
        for (int i = 0; i < stationPositions.length; i += 2) {
            Station station = new Station();
            int stationX = Integer.parseInt(stationPositions[i]);
            int stationY = Integer.parseInt(stationPositions[i + 1]);

            station.x = stationX;
            station.y = stationY;
            stations.add(station);
        }

        return stations;
    }

}
