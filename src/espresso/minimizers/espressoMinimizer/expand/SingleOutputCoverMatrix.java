package espresso.minimizers.espressoMinimizer.expand;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;

import java.util.HashSet;

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

  public HashSet<Integer> computeCoveringSet(
      HashSet<Integer> loweringSet,
      HashSet<Integer> raisingSet,
      HashSet<Integer> ignoringColumns,
      int rowIndex
  ) {
    HashSet<Integer> coveringSet = new HashSet<>();

    for (int j = 0; j < getColumnCount(); j++) {
      if (ignoringColumns.contains(j)) continue;

      if (!loweringSet.contains(j) && !raisingSet.contains(j) && getElement(rowIndex, j)) {
        coveringSet.add(j);
      }
    }

    return coveringSet;
  }

  public int maxTrueCountColumnIndex() {
    int maxColumnIndex = -1;
    int maxTrueCount = -1;

    for (int j = 0; j < getColumnCount(); j++) {
      if (getTrueColumnCount(j) > maxTrueCount) {
        maxColumnIndex = j;
        maxTrueCount = getTrueColumnCount(j);
      }
    }

    return maxColumnIndex;
  }
}