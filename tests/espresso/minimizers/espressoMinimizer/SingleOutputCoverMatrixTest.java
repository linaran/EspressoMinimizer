package espresso.minimizers.espressoMinimizer;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.OutputState;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.expand.SingleOutputCoverMatrix;
import org.junit.Test;

import static espresso.boolFunction.InputState.DONTCARE;
import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;
import static espresso.boolFunction.OutputState.OUTPUT;
import static org.junit.Assert.*;


public class SingleOutputCoverMatrixTest {

  @Test
  public void construction() throws Exception {
    Cover cover = new Cover(
        new Cube(new InputState[]{ONE, ZERO, ONE}, new OutputState[]{OUTPUT}),
        new Cube(new InputState[]{ZERO, DONTCARE, ZERO}, new OutputState[]{OUTPUT}),
        new Cube(new InputState[]{ZERO, ZERO, DONTCARE}, new OutputState[]{OUTPUT}),
        new Cube(new InputState[]{ONE, ZERO, ZERO}, new OutputState[]{OUTPUT})
    );

    SingleOutputCoverMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(2));

    boolean[][] actualMatrix = new boolean[matrix.getRowCount()][matrix.getColumnCount()];
    for (int i = 0; i < matrix.getRowCount(); i++) {
      for (int j = 0; j < matrix.getColumnCount(); j++) {
        actualMatrix[i][j] = matrix.getElement(i, j);
      }
    }

    boolean[][] expectedMatrix = new boolean[][]{
        new boolean[]{true, false, false},
        new boolean[]{false, true, false},
        new boolean[]{false, false, false},
        new boolean[]{true, false, false}
    };

    assertArrayEquals(
        "Single cover matrix construction is wrong.",
        expectedMatrix,
        actualMatrix
    );
  }
}