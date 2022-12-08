package code;

import java.util.Comparator;

public class ComparatorForAstar1 implements Comparator<Node> {

    @Override
    public int compare(Node arg0, Node arg1) {
        // TODO Auto-generated method stub
        int h1 = ComparatorForGreedy1.estimateCostToGoal(arg0);
        int h2 = ComparatorForGreedy1.estimateCostToGoal(arg1);

        if (h1 + arg0.numberOfdeath < h2 + arg1.numberOfdeath) {
            return -1;
        } else if (h1 + arg0.numberOfdeath > h2 + arg1.numberOfdeath) {
            return 1;
        } else {
            if (arg0.numberOfCollectedBlackboxes > arg1.numberOfCollectedBlackboxes) {
                return -1;
            } else if (arg0.numberOfCollectedBlackboxes < arg1.numberOfCollectedBlackboxes) {
                return 1;
            } else {
                return 0;
            }
        }
    }

}
