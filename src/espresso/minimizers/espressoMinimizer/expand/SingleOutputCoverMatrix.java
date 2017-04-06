package espresso.minimizers.espressoMinimizer.expand;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;

public class SingleOutputCoverMatrix extends CubeExpansionMatrix {

  public SingleOutputCoverMatrix(Cover cover, Cube cube) {
    super(cover, cube);
  }

  @Override
  protected boolean generateMatrixElement(InputState coverInputState, InputState cubeInputState) {
    return cubeInputState == ONE && coverInputState != ONE ||
        cubeInputState == ZERO && coverInputState != ZERO;
  }

  public Set<Integer> computeCoveringSet(
      HashSet<Integer> loweringSet,
      HashSet<Integer> raisingSet,
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