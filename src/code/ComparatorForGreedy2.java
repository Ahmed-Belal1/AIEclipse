package code;

import java.util.ArrayList;
import java.util.Comparator;

public class ComparatorForGreedy2 implements Comparator<Node> {

    // estimate the cost to the goal
    public static ArrayList<Integer> estimateCostToGoal2(Node childNode) {

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

        // solve the problem using greedy minimizing the number of death(cost) and
        // maximizing the number of collected blackboxes
        int cost = 0;
        int numberOfCollectedBlackboxes = 0;

        while (ships.size() > 0) {
            // check if all the ships are wreck
            boolean allWreck = true;
            for (int i = 0; i < ships.size(); i++) {
                if (!ships.get(i).wreck) {
                    allWreck = false;
                    break;
                }
            }
            // handle the black boxes logic
            if (allWreck) {
                // calculate the number of the blackboxes that can be collected
                while (ships.size() > 0) {
                    // get the closest ship where we can collect the blackbox
                    Ship closestShip = null;
                    int closestShipDistance = Integer.MAX_VALUE;
                    for (int i = 0; i < ships.size(); i++) {
                        int distance = Math.abs(currX - ships.get(i).positionXOftheShip)
                                + Math.abs(currY - ships.get(i).positionYOftheShip);
                        if (distance < closestShipDistance && ships.get(i).damage + distance <= 20) {
                            closestShip = ships.get(i);
                            closestShipDistance = distance;
                        }
                    }

                    if (closestShip == null) {
                        // return the cost and the number of collected blackboxes
                        ArrayList<Integer> result = new ArrayList<Integer>();
                        result.add(cost);
                        result.add(numberOfCollectedBlackboxes);
                        return result;
                    }

                    // go to the ship and collect the blackbox
                    currX = closestShip.positionXOftheShip;
                    currY = closestShip.positionYOftheShip;
                    closestShip.blackboxtaken = true;
                    numberOfCollectedBlackboxes++;

                    // remove the ship from the list
                    ships.remove(closestShip);

                    // calculate the cost by looping over the ships and increasing the damage of
                    // each ship for each step
                    for (int i = 0; i < ships.size(); i++) {
                        ships.get(i).damage += closestShipDistance;
                        if (ships.get(i).damage > 20) {
                            ships.remove(i);
                            i--;
                        }
                    }

                }

                // return the cost and the number of collected blackboxes
                ArrayList<Integer> result = new ArrayList<Integer>();
                result.add(cost);
                result.add(numberOfCollectedBlackboxes);

                return result;
            } else {

                // if the coast guard has no capacity to rescue people
                // then go to the neares station and drop the people
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
                        // if the ship is wreck then skip it
                        if (ships.get(i).wreck) {
                            // adjust the damage of the ship by the closest station distance
                            ships.get(i).damage += closestStationDistance;
                            if (ships.get(i).damage > 20) {
                                ships.remove(i);
                                i--;
                            }
                        } else if (closestStationDistance < ships.get(i).numberOfPeopleOntheShip) {
                            ships.get(i).numberOfPeopleOntheShip -= closestStationDistance;
                            cost += closestStationDistance;
                        } else {
                            cost += ships.get(i).numberOfPeopleOntheShip;
                            // mark the ship as wreck
                            ships.get(i).wreck = true;
                            ships.get(i).damage += closestStationDistance - ships.get(i).numberOfPeopleOntheShip;
                            ships.get(i).numberOfPeopleOntheShip = 0;
                            if (ships.get(i).damage > 20) {
                                ships.remove(i);
                                i--;
                            }
                        }
                    }

                    // update the number of people on the coast guard
                    numberOfPeopleOntheCoastGuard = 0;
                    continue;

                }

                // if the coast guard has capacity to rescue people

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

                // if there are no ships where we can rescue people
                // then go to the nearest ship where we can retrieve the blackbox
                if (closestShip == null) {
                    // go to the neares ship where we can retrieve the blackbox
                    Ship closestShipWithBlackbox = null;
                    int closestShipWithBlackboxDistance = Integer.MAX_VALUE;
                    for (int i = 0; i < ships.size(); i++) {
                        int distance = Math.abs(currX - ships.get(i).positionXOftheShip)
                                + Math.abs(currY - ships.get(i).positionYOftheShip);
                        if (distance < closestShipWithBlackboxDistance
                                && ships.get(i).numberOfPeopleOntheShip + 20 > distance) {
                            closestShipWithBlackbox = ships.get(i);
                            closestShipWithBlackboxDistance = distance;
                        }
                    }

                    if (closestShipWithBlackbox == null) {
                        // return the cost and the number of collected blackboxes
                        ArrayList<Integer> result = new ArrayList<Integer>();
                        result.add(cost);
                        result.add(numberOfCollectedBlackboxes);
                        return result;
                    }

                    // go to the ship and collect the blackbox
                    currX = closestShipWithBlackbox.positionXOftheShip;
                    currY = closestShipWithBlackbox.positionYOftheShip;

                    // remove the ship from the list
                    ships.remove(closestShipWithBlackbox);

                    // calculate the cost by looping over the ships and increasing the damage of
                    // each ship for each step
                    for (int i = 0; i < ships.size(); i++) {
                        ships.get(i).wreck = true;
                        ships.get(i).damage += closestShipWithBlackboxDistance - ships.get(i).numberOfPeopleOntheShip;
                        ships.get(i).numberOfPeopleOntheShip = 0;
                        if (ships.get(i).damage > 20) {
                            ships.remove(i);
                            i--;
                        }
                    }

                    continue;
                }

                // if we have the capacity and we found a close ship where we can rescue people

                // go the closest ship and rescue the people
                currX = closestShip.positionXOftheShip;
                currY = closestShip.positionYOftheShip;

                // calculate the cost by looping over the ships and reducing the number of
                // people for each ship for each step
                for (int i = 0; i < ships.size(); i++) {
                    // if the ship is wreck then skip it
                    if (ships.get(i).wreck) {
                        // adjust the damage of the ship by the closest ship distance
                        ships.get(i).damage += closestShipDistance;
                        if (ships.get(i).damage > 20) {
                            ships.remove(i);
                            i--;
                        }
                    } else if (closestShipDistance < ships.get(i).numberOfPeopleOntheShip) {
                        ships.get(i).numberOfPeopleOntheShip -= closestShipDistance;
                        cost += closestShipDistance;
                    } else {
                        cost += ships.get(i).numberOfPeopleOntheShip;
                        // mark the ship as wreck
                        ships.get(i).wreck = true;
                        ships.get(i).damage += closestShipDistance - ships.get(i).numberOfPeopleOntheShip;
                        ships.get(i).numberOfPeopleOntheShip = 0;
                        if (ships.get(i).damage > 20) {
                            ships.remove(i);
                            i--;
                        }
                    }
                }

                // update the number of people on the coast guard and on the closest ship
                if (capacityOftheCoastGuard - numberOfPeopleOntheCoastGuard > closestShip.numberOfPeopleOntheShip) {
                    numberOfPeopleOntheCoastGuard += closestShip.numberOfPeopleOntheShip;
                    closestShip.numberOfPeopleOntheShip = 0;
                    // mark the ship as wreck
                    closestShip.wreck = true;
                } else {
                    closestShip.numberOfPeopleOntheShip -= capacityOftheCoastGuard - numberOfPeopleOntheCoastGuard;
                    numberOfPeopleOntheCoastGuard = capacityOftheCoastGuard;
                }

            }
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        result.add(cost);
        result.add(numberOfCollectedBlackboxes);
        return result;
    }

    @Override
    public int compare(Node o1, Node o2) {
        // TODO Auto-generated method stub
        ArrayList<Integer> cost1 = estimateCostToGoal2(o1);
        ArrayList<Integer> cost2 = estimateCostToGoal2(o2);

        int numberOfEstimatedDeaths1 = cost1.get(0);
        int numberOfEstimatedBlachBoxes1 = cost1.get(1);
        int numberOfEstimatedDeaths2 = cost2.get(0);
        int numberOfEstimatedBlachBoxes2 = cost2.get(1);

        if (numberOfEstimatedDeaths1 < numberOfEstimatedDeaths2) {
            return -1;
        } else if (numberOfEstimatedDeaths1 > numberOfEstimatedDeaths2) {
            return 1;
        } else {
            if (numberOfEstimatedBlachBoxes1 > numberOfEstimatedBlachBoxes2) {
                return -1;
            } else if (numberOfEstimatedBlachBoxes1 < numberOfEstimatedBlachBoxes2) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
