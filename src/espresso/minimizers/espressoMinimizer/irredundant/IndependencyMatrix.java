package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class IndependencyMatrix extends BooleanMatrix {

  public IndependencyMatrix(BooleanMatrix matrix) {
    super(new IndependencyMatrixElementGenerator(matrix));
  }

  public Set<Integer> computeMaxClique() {
    Set<Integer> clique = new HashSet<>();
    int startingNode = rowIndexWithMaxTrueCount();
    clique.add(startingNode);

    for (Iterator<Integer> iter = ignoreRowsIterator(); iter.hasNext(); ) {
      int row = iter.next();
      if (willMaintainCompleteness(clique, row)) {
        clique.add(row);
      }
    }

    return clique;
  }

  private boolean willMaintainCompleteness(Set<Integer> clique, int newNode) {
    for (Integer node : clique) {
      if (!areNodesConnected(node, newNode)) {
        return false;
      }
    }

    return true;
  }

  private boolean areNodesConnected(int node1, int node2) {
    return getElement(node1, node2);
  }

  public int rowIndexWithMaxTrueCount() {
    int maxRowIndex = 0;
    int maxTrueCount = 0;

    for (Iterator<Integer> iter = ignoreRowsIterator(); iter.hasNext(); ) {
      int rowIndex = iter.next();

      int trueRowCount = getTrueRowCount(rowIndex);
      if (trueRowCount > maxTrueCount) {
        maxRowIndex = rowIndex;
        maxTrueCount = trueRowCount;
      }
    }

    return maxRowIndex;
  }

}
