package code;

import java.util.*;
import java.util.Comparator;

public class ComparatorForGreedy1 implements Comparator<Node> {

    // estimate the cost to the goal
    public static int estimateCostToGoal(Node childNode) {

        int currX = childNode.x;
        int currY = childNode.y;
        int numberOfPeopleOntheCoastGuard = childNode.numberOfPeopleOntheCoastGuard;
        int capacityOftheCoastGuard = childNode.capacity;

        // deep clone the list of ships
        ArrayList<Ship> ships = new ArrayList<Ship>();
        for (int i = 0; i < childNode.ships.size(); i++) {
            Ship ship = new Ship();
            ship.numberOfPeopleOntheShip = childNode.ships.get(i).numberOfPeopleOntheShip;
            ship.damage = childNode.ships.get(i).damage;
            ship.wreck = childNode.ships.get(i).wreck;
            ship.blackboxtaken = childNode.ships.get(i).blackboxtaken;
            ship.positionXOftheShip = childNode.ships.get(i).positionXOftheShip;
            ship.positionYOftheShip = childNode.ships.get(i).positionYOftheShip;
            ship.retrievable = childNode.ships.get(i).retrievable;
            ships.add(ship);
        }

        // deep clone the list of stations
        ArrayList<Station> stations = new ArrayList<Station>();
        for (int i = 0; i < childNode.stations.size(); i++) {
            Station station = new Station();
            station.x = childNode.stations.get(i).x;
            station.y = childNode.stations.get(i).y;
            stations.add(station);
        }

        // solve the problem using greedy minimizing the number of death
        int cost = 0;
        int numberOfCollectedBlackboxes = 0;
        int numberOfDeath = 0;

        while (ships.size() > 0) {

            // check if the coast guard has capacity to rescue the people
            if (capacityOftheCoastGuard == numberOfPeopleOntheCoastGuard) {
                // go to the nearest station and drop the people
                Station closestStation = null;
                int closestStationDistance = Integer.MAX_VALUE;
                for (int i = 0; i < stations.size(); i++) {
                    int distance = Math.abs(currX - stations.get(i).x) + Math.abs(currY - stations.get(i).y);
                    if (distance < closestStationDistance) {
                        closestStation = stations.get(i);
                        closestStationDistance = distance;
                    }
                }
                currX = closestStation.x;
                currY = closestStation.y;

                // calculate the cost by looping over the ships and reducing the number of
                // people for each ship for each step
                for (int i = 0; i < ships.size(); i++) {
                    if (closestStationDistance < ships.get(i).numberOfPeopleOntheShip) {
                        ships.get(i).numberOfPeopleOntheShip -= closestStationDistance;
                        cost += closestStationDistance;
                    } else {
                        cost += ships.get(i).numberOfPeopleOntheShip;
                        ships.get(i).numberOfPeopleOntheShip = 0;
                        // remove the ship from the list of ships
                        ships.remove(i);
                        i--;
                    }
                }

                // update the number of people on the coast guard
                numberOfPeopleOntheCoastGuard = 0;
                continue;

            }

            // get the closest ship where the number of people alive is more than the
            // distance
            // between the coast guard and the ship and we the coast guard has capacity to
            // rescue the people
            Ship closestShip = null;
            int closestShipDistance = Integer.MAX_VALUE;
            for (int i = 0; i < ships.size(); i++) {
                int distance = Math.abs(currX - ships.get(i).positionXOftheShip)
                        + Math.abs(currY - ships.get(i).positionYOftheShip);
                if (distance < closestShipDistance && distance < ships.get(i).numberOfPeopleOntheShip
                        && capacityOftheCoastGuard - numberOfPeopleOntheCoastGuard > 0) {
                    closestShip = ships.get(i);
                    closestShipDistance = distance;
                }
            }

            // if there is no ship where the number of people alive is more than the
            // distance
            // between the coast guard and the ship then return the cost
            if (closestShip == null) {
                return cost;
            }

            // go the closest ship and rescue the people
            currX = closestShip.positionXOftheShip;
            currY = closestShip.positionYOftheShip;

            // calculate the cost by looping over the ships and reducing the number of
            // people for each ship for each step
            for (int i = 0; i < ships.size(); i++) {
                if (closestShipDistance < ships.get(i).numberOfPeopleOntheShip) {
                    ships.get(i).numberOfPeopleOntheShip -= closestShipDistance;
                    cost += closestShipDistance;
                } else {
                    cost += ships.get(i).numberOfPeopleOntheShip;
                    ships.get(i).numberOfPeopleOntheShip = 0;
                    // remove the ship from the list of ships
                    ships.remove(i);
                    i--;
                }
            }

            // update the number of people on the coast guard and on the closest ship
            if (capacityOftheCoastGuard - numberOfPeopleOntheCoastGuard > closestShip.numberOfPeopleOntheShip) {
                numberOfPeopleOntheCoastGuard += closestShip.numberOfPeopleOntheShip;
                closestShip.numberOfPeopleOntheShip = 0;
                // remove the ship from the list of ships
                ships.remove(closestShip);
            } else {
                closestShip.numberOfPeopleOntheShip -= capacityOftheCoastGuard - numberOfPeopleOntheCoastGuard;
                numberOfPeopleOntheCoastGuard = capacityOftheCoastGuard;
            }

        }
        // System.out.println("cost: " + cost);
        return cost;
    }

    @Override
    public int compare(Node o1, Node o2) {
        // TODO Auto-generated method stub
        int cost1 = estimateCostToGoal(o1);
        int cost2 = estimateCostToGoal(o2);

        if (cost1 < cost2) {
            return -1;
        } else if (cost1 > cost2) {
            return 1;
        } else {
            return 0;
        }
    }

}
