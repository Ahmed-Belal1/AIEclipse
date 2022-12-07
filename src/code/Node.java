package code;
import java.util.*;

public class Node {
    Node left;
    Node right;
    Node up;
    Node down;
    Node retrieve;
    Node dropoff;
    Node pickup;
    Node parent;
    int x;
    int y;
    int numberOfPeopleOntheCoastGuard;
    ArrayList<Ship> ships;
    boolean isGoal;
    String action;
    int numberOfCollectedBlackboxes;
    int numberOfdeath;
    int depth;

    public Node() {
        left = null;
        right = null;
        up = null;
        down = null;
        retrieve = null;
        dropoff = null;
        pickup = null;
        parent = null;
        numberOfCollectedBlackboxes = 0;
        numberOfdeath = 0;
        x = 0;
        y = 0;
        numberOfPeopleOntheCoastGuard = 0;
        ships = new ArrayList<Ship>();
        isGoal = false;
        action = "";
    }

    // overwrite the equals method
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Node)) {
            return false;
        }
        Node n = (Node) o;
        if (n.x == this.x && n.y == this.y && n.numberOfPeopleOntheCoastGuard == this.numberOfPeopleOntheCoastGuard) {
            if (n.ships.size() == this.ships.size()) {
                for (int i = 0; i < n.ships.size(); i++) {
                    if (n.ships.get(i).positionXOftheShip != this.ships.get(i).positionXOftheShip
                            || n.ships.get(i).positionYOftheShip != this.ships.get(i).positionYOftheShip
                            || n.ships.get(i).numberOfPeopleOntheShip != this.ships.get(i).numberOfPeopleOntheShip ||
                            n.ships.get(i).retrievable != this.ships.get(i).retrievable
                            || n.ships.get(i).wreck != this.ships.get(i).wreck
                            || n.ships.get(i).blackboxtaken != this.ships.get(i).blackboxtaken
                            || n.ships.get(i).damage != this.ships.get(i).damage) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    // can be changed
    // overwrite the hashcode method
    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + numberOfPeopleOntheCoastGuard;
        for (int i = 0; i < ships.size(); i++) {
            result = 31 * result + ships.get(i).positionXOftheShip;
            result = 31 * result + ships.get(i).positionYOftheShip;
            result = 31 * result + ships.get(i).numberOfPeopleOntheShip;
            result = 31 * result + (ships.get(i).retrievable ? 1 : 0);
            result = 31 * result + (ships.get(i).wreck ? 1 : 0);
            result = 31 * result + (ships.get(i).blackboxtaken ? 1 : 0);
            result = 31 * result + ships.get(i).damage;
        }
        return result;
    }

    public ArrayList<Node> getChildren(int capacity, int gridWidth, int gridLength, ArrayList<Station> stations) {
        ArrayList<Node> children = new ArrayList<Node>();

        if (this.y - 1 >=0) {
            Node leftNode = new Node();
            leftNode.x = this.x;
            leftNode.y = this.y-1;
            leftNode.parent = this;
            leftNode.action = "left";
            leftNode.numberOfPeopleOntheCoastGuard = this.numberOfPeopleOntheCoastGuard;
            leftNode.numberOfCollectedBlackboxes = this.numberOfCollectedBlackboxes;
            leftNode.numberOfdeath = this.numberOfdeath;
            leftNode.depth = this.depth + 1;
            leftNode.ships = handleShips(this.ships, "left", null,
                    capacity - this.numberOfPeopleOntheCoastGuard, leftNode);

            children.add(leftNode);
        }
        if (this.y + 1 < gridLength) {
            Node rightNode = new Node();
            rightNode.x = this.x ;
            rightNode.y = this.y+1;
            rightNode.parent = this;
            rightNode.action = "right";
            rightNode.numberOfPeopleOntheCoastGuard = this.numberOfPeopleOntheCoastGuard;
            rightNode.numberOfCollectedBlackboxes = this.numberOfCollectedBlackboxes;
            rightNode.numberOfdeath = this.numberOfdeath;
            rightNode.depth = this.depth + 1;
            rightNode.ships = handleShips(this.ships, "right", null,
                    capacity - this.numberOfPeopleOntheCoastGuard, rightNode);

            children.add(rightNode);
        }

        if (this.x - 1 >= 0) {
            Node upNode = new Node();
            upNode.x = this.x-1;
            upNode.y = this.y;
            upNode.parent = this;
            upNode.action = "up";
            upNode.numberOfPeopleOntheCoastGuard = this.numberOfPeopleOntheCoastGuard;
            upNode.numberOfCollectedBlackboxes = this.numberOfCollectedBlackboxes;
            upNode.numberOfdeath = this.numberOfdeath;
            upNode.depth = this.depth + 1;
            upNode.ships = handleShips(this.ships, "up", null,
                    capacity - this.numberOfPeopleOntheCoastGuard, upNode);

            children.add(upNode);
        }

        if (this.x+1 < gridWidth) {
            Node downNode = new Node();
            downNode.x = this.x+1;
            downNode.y = this.y;
            downNode.parent = this;
            downNode.action = "down";
            downNode.numberOfPeopleOntheCoastGuard = this.numberOfPeopleOntheCoastGuard;
            downNode.numberOfCollectedBlackboxes = this.numberOfCollectedBlackboxes;
            downNode.numberOfdeath = this.numberOfdeath;
            downNode.depth = this.depth + 1;
            downNode.ships = handleShips(this.ships, "down", null,
                    capacity - this.numberOfPeopleOntheCoastGuard, downNode);
            children.add(downNode);
        }

        Ship ship = onAShip(this.x, this.y, this.ships);
        if (ship != null && ship.numberOfPeopleOntheShip > 0
                && this.numberOfPeopleOntheCoastGuard < capacity) {
            Node pickup = new Node();
            pickup.x = this.x;
            pickup.y = this.y;
            pickup.parent = this;
            pickup.action = "pickup";
            pickup.numberOfCollectedBlackboxes = this.numberOfCollectedBlackboxes;
            pickup.numberOfdeath = this.numberOfdeath;
            pickup.depth = this.depth + 1;
            // System.out.println(ship.numberOfPeopleOntheShip);
            pickup.ships = handleShips(this.ships, "pickup", ship,
                    capacity - this.numberOfPeopleOntheCoastGuard, pickup);
            if (this.numberOfPeopleOntheCoastGuard + ship.numberOfPeopleOntheShip < capacity) {
                pickup.numberOfPeopleOntheCoastGuard = this.numberOfPeopleOntheCoastGuard
                        + ship.numberOfPeopleOntheShip;
            } else {
                pickup.numberOfPeopleOntheCoastGuard = capacity;
            }
            // System.out.println(this.capacity + "Ship " + pickup.numberOfPeopleOntheBoat);
            children.add(pickup);
        }

        if (ship != null && ship.wreck && ship.retrievable && !ship.blackboxtaken) {
            Node retriNode = new Node();
            retriNode.x = this.x;
            retriNode.y = this.y;
            retriNode.parent = this;
            retriNode.action = "retrieve";
            retriNode.numberOfPeopleOntheCoastGuard = this.numberOfPeopleOntheCoastGuard;
            retriNode.numberOfCollectedBlackboxes = this.numberOfCollectedBlackboxes + 1;
            retriNode.numberOfdeath = this.numberOfdeath;
            retriNode.depth = this.depth + 1;
            retriNode.ships = handleShips(this.ships, "retrieve", ship,
                    capacity - this.numberOfPeopleOntheCoastGuard, retriNode);
            children.add(retriNode);
        }

        Station station = isOnAStation(this.x, this.y, stations);
        if (station != null && this.numberOfPeopleOntheCoastGuard > 0) {
            Node dropoff = new Node();

            dropoff.x = this.x;
            dropoff.y = this.y;
            dropoff.parent = this;
            dropoff.action = "dropoff";

            dropoff.numberOfCollectedBlackboxes = this.numberOfCollectedBlackboxes;
            dropoff.numberOfdeath = this.numberOfdeath;
            dropoff.depth = this.depth + 1;
            dropoff.ships = handleShips(this.ships, "dropoff", null,
                    capacity - this.numberOfPeopleOntheCoastGuard, dropoff);
            dropoff.numberOfPeopleOntheCoastGuard = 0;
            // System.out.println(this.visitedSet.contains(dropoff));
            children.add(dropoff);
        }

        return children;

    }

    public ArrayList<Ship> handleShips(ArrayList<Ship> ships, String action, Ship s, int boatCapacity, Node child) {
        ArrayList<Ship> shipsCopy = new ArrayList<Ship>();
        if (action.equals("pickup") || action.equals("retrieve")) {
            for (int i = 0; i < ships.size(); i++) {

                if (ships.get(i).positionXOftheShip == s.positionXOftheShip
                        && ships.get(i).positionYOftheShip == s.positionYOftheShip) {
                    if (action.equals("pickup")) {
                        Ship ship = new Ship();
                        ship.positionXOftheShip = s.positionXOftheShip;
                        ship.positionYOftheShip = s.positionYOftheShip;
                        ship.wreck = s.wreck;
                        ship.damage = s.damage;
                        ship.blackboxtaken = s.blackboxtaken;
                        ship.retrievable = s.retrievable;

                        ship.numberOfPeopleOntheShip -= boatCapacity;
                        if (ship.numberOfPeopleOntheShip <= 0) {
                            ship.numberOfPeopleOntheShip = 0;
                            ship.wreck = true;
                        }
                        shipsCopy.add(ship);
                    } else if (action.equals("retrieve")) {
                        child.numberOfCollectedBlackboxes++;
                        s.blackboxtaken = true;
                        s.retrievable = false;
                    }
                } else {
                    Ship ship = new Ship();
                    ship.positionXOftheShip = ships.get(i).positionXOftheShip;
                    ship.positionYOftheShip = ships.get(i).positionYOftheShip;
                    ship.wreck = ships.get(i).wreck;
                    ship.damage = ships.get(i).damage;
                    ship.blackboxtaken = ships.get(i).blackboxtaken;
                    ship.retrievable = ships.get(i).retrievable;
                    if (ships.get(i).numberOfPeopleOntheShip == 1) {
                        child.numberOfdeath++;
                        ship.numberOfPeopleOntheShip = 0;
                        ship.wreck = true;
                    } else if (ships.get(i).numberOfPeopleOntheShip > 1) {
                        child.numberOfdeath++;
                        ship.numberOfPeopleOntheShip = ships.get(i).numberOfPeopleOntheShip - 1;
                    } else {
                        if (ships.get(i).damage < 20 && ships.get(i).wreck) {
                            ship.damage++;
                        } else if (ships.get(i).damage == 20 && ships.get(i).wreck && !ships.get(i).blackboxtaken) {
                            ship.retrievable = false;
                        }

                    }
                    if (ship.retrievable) {
                        shipsCopy.add(ship);
                    }
                }

            }
        } else {
            for (int i = 0; i < ships.size(); i++) {
                Ship ship = new Ship();
                ship.positionXOftheShip = ships.get(i).positionXOftheShip;
                ship.positionYOftheShip = ships.get(i).positionYOftheShip;
                ship.wreck = ships.get(i).wreck;
                ship.damage = ships.get(i).damage;
                ship.blackboxtaken = ships.get(i).blackboxtaken;
                ship.retrievable = ships.get(i).retrievable;
                if (ships.get(i).numberOfPeopleOntheShip == 1) {
                    child.numberOfdeath++;
                    ship.numberOfPeopleOntheShip = 0;
                    ship.wreck = true;
                } else if (ships.get(i).numberOfPeopleOntheShip > 1) {
                    child.numberOfdeath++;
                    ship.numberOfPeopleOntheShip = ships.get(i).numberOfPeopleOntheShip - 1;
                } else {
                    if (ships.get(i).damage < 20 && ships.get(i).wreck) {
                        ship.damage++;
                    } else if (ships.get(i).damage == 20 && ships.get(i).wreck && !ships.get(i).blackboxtaken) {
                        ship.retrievable = false;
                    }

                }
                if (ship.retrievable) {
                    shipsCopy.add(ship);
                }
            }
        }

        return shipsCopy;

    }

    public Ship onAShip(int x, int y, ArrayList<Ship> ships) {
        Ship ship = new Ship();
        boolean found = false;
        for (int i = 0; i < ships.size(); i++) {
            if (ships.get(i).positionXOftheShip == x && ships.get(i).positionYOftheShip == y) {
                ship.blackboxtaken = ships.get(i).blackboxtaken;
                ship.damage = ships.get(i).damage;
                ship.numberOfPeopleOntheShip = ships.get(i).numberOfPeopleOntheShip;
                ship.positionXOftheShip = ships.get(i).positionXOftheShip;
                ship.positionYOftheShip = ships.get(i).positionYOftheShip;
                ship.retrievable = ships.get(i).retrievable;
                ship.wreck = ships.get(i).wreck;
                found = true;
                break;
            }
        }
        if (found) {
            return ship;
        } else {
            return null;
        }
    }

    public Station isOnAStation(int x, int y, ArrayList<Station> stations) {
        Station station = new Station();
        boolean found = false;
        for (int i = 0; i < stations.size(); i++) {
            if (stations.get(i).x == x && stations.get(i).y == y) {
                station.x = stations.get(i).x;
                station.y = stations.get(i).y;
                found = true;
                break;
            }
        }
        if (found) {
            return station;
        } else {
            return null;
        }
    }

}
