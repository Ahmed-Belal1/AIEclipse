package code;

//This is the node comparator class that is used to compare the nodes in the priority queue

import java.util.Comparator;

public class NodeComparator implements Comparator<Node> {

  // priority is given to node with lower number of death
  @Override
  public int compare(Node o1, Node o2) {
      if (o1.numberOfdeath < o2.numberOfdeath) {
          return -1;
      } else if (o1.numberOfdeath > o2.numberOfdeath) {
          return 1;
      } else {
          // return 0;
          // if the number of death is same then priority is given to node with higher
          // number of collected black boxes
          if (o1.numberOfCollectedBlackboxes > o2.numberOfCollectedBlackboxes) {
              return -1;
          } else if (o1.numberOfCollectedBlackboxes < o2.numberOfCollectedBlackboxes) {
              return 1;
          } else {
              return 0;
          }
      }
  }

}