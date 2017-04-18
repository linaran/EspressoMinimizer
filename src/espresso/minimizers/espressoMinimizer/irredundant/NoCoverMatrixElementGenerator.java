package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.utils.MatrixElementGenerator;

import java.util.Collections;
import java.util.List;

public class NoCoverMatrixElementGenerator implements MatrixElementGenerator {

  private int columnCount;
  private List<List<Integer>> minSets;

  /**
   * Warning this class assumes that the given lists are sorted.
   * These lists are expected to be tracking data calculated from
   * {@link Cover#trackingCofactor(Cube, List)}.
   */
  public NoCoverMatrixElementGenerator(List<List<Integer>> minSets, int columnCount) {
    this.columnCount = columnCount;
    this.minSets = minSets;
  }

  @Override
  public boolean generateElement(int rowIndex, int columnIndex) {
    List<Integer> set = minSets.get(rowIndex);
    int foundIndex = Collections.binarySearch(set, columnIndex);
    return foundIndex >= 0 && foundIndex < set.size() && set.get(foundIndex) == columnIndex;
  }

  @Override
  public int getRowCount() {
    return minSets.size();
  }

  @Override
  public int getColumnCount() {
    return columnCount;
  }

}
