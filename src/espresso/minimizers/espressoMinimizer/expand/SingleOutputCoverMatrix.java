package espresso.minimizers.espressoMinimizer.expand;

import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SingleOutputCoverMatrix extends BooleanMatrix {

  public SingleOutputCoverMatrix(Cover cover, Cube cube) {
    super(new CoverMatrixElementGenerator(cover, cube));
  }

  public Set<Integer> computeCoveringSet(
      Set<Integer> loweringSet,
      Set<Integer> raisingSet,
      int rowIndex
  ) {
    Set<Integer> coveringSet = new HashSet<>();

    for (Iterator<Integer> columnIter = ignoreColumnsIterator(); columnIter.hasNext(); ) {
      int j = columnIter.next();

      if (!loweringSet.contains(j) && !raisingSet.contains(j) && getElement(rowIndex, j)) {
        coveringSet.add(j);
      }
    }

    return coveringSet;
  }

  public int maxTrueCountColumnIndex(boolean countIgnoredValues) {
    int maxColumnIndex = -1;
    int maxTrueCount = -1;

    for (int j = 0; j < getColumnCount(); j++) {
      if (!countIgnoredValues && isColumnIgnored(j)) continue;

      if (getTrueColumnCount(j, countIgnoredValues) > maxTrueCount) {
        maxColumnIndex = j;
        maxTrueCount = getTrueColumnCount(j, countIgnoredValues);
      }
    }

    return maxColumnIndex;
  }
}