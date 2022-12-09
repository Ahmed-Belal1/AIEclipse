package code;

import java.util.ArrayList;
import java.util.Comparator;

public class ComparatorForAstar2 implements Comparator<Node> {

    @Override
    public int compare(Node o1, Node o2) {
        // TODO Auto-generated method stub
        ArrayList<Integer> cost1 = ComparatorForGreedy2.estimateCostToGoal2(o1);
        ArrayList<Integer> cost2 = ComparatorForGreedy2.estimateCostToGoal2(o2);

        int numberOfEstimatedDeaths1 = cost1.get(0);
        int numberOfEstimatedBlachBoxes1 = cost1.get(1);
        int numberOfEstimatedDeaths2 = cost2.get(0);
        int numberOfEstimatedBlachBoxes2 = cost2.get(1);

        if (numberOfEstimatedDeaths1 + o1.numberOfdeath < numberOfEstimatedDeaths2 + o2.numberOfdeath) {
            return -1;
        } else if (numberOfEstimatedDeaths1 + o1.numberOfdeath > numberOfEstimatedDeaths2 + o2.numberOfdeath) {
            return 1;
        } else {
            if (numberOfEstimatedBlachBoxes1 + o1.numberOfCollectedBlackboxes > numberOfEstimatedBlachBoxes2
                    + o2.numberOfCollectedBlackboxes) {
                return -1;
            } else if (numberOfEstimatedBlachBoxes1 + o1.numberOfCollectedBlackboxes < numberOfEstimatedBlachBoxes2
                    + o2.numberOfCollectedBlackboxes) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
