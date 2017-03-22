package espresso.minimizers.espressoMinimizer;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;

import java.util.Iterator;

import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;

public class SingleOutputBlockingMatrix {

  private boolean[][] blockingMatrix;

  public SingleOutputBlockingMatrix(Cover offSet, Cube cube) {
    if (offSet.size() == 0) {
      throw new IllegalArgumentException(
          "Don't know how to create blocking matrix from an empty offSet."
      );
    }

    blockingMatrix = new boolean[offSet.size()][offSet.get(0).inputLength()];

    for (int i = 0; i < offSet.size(); ++i) {
      Cube offSetCube = offSet.get(i);

      for (int j = 0; j < offSetCube.inputLength(); j++) {
        InputState cubeInputState = cube.getInputState(j);
        InputState offSetCubeInputState = offSetCube.getInputState(j);

        blockingMatrix[i][j] =
            cubeInputState == ONE && offSetCubeInputState == ZERO ||
                cubeInputState == ZERO && offSetCubeInputState == ONE;
      }
    }
  }

  public boolean getElement(int i, int j) {
    return blockingMatrix[i][j];
  }
}
