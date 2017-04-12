package espresso.minimizers.espressoMinimizer;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.OutputState;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.expand.SingleOutputBlockMatrix;
import org.junit.Test;

import static espresso.boolFunction.InputState.DONTCARE;
import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;
import static espresso.boolFunction.OutputState.OUTPUT;
import static org.junit.Assert.*;


public class SingleOutputBlockMatrixTest {

  @Test
  public void construction() throws Exception {
    Cover cover = new Cover(
        new Cube(new InputState[]{ONE, ONE, DONTCARE}, new OutputState[]{OUTPUT}),
        new Cube(new InputState[]{DONTCARE, ONE, ONE}, new OutputState[]{OUTPUT})
    );

    Cube cube = new Cube(new InputState[]{ZERO, ZERO, DONTCARE}, new OutputState[]{OUTPUT});

    SingleOutputBlockMatrix matrix = new SingleOutputBlockMatrix(cover, cube);

    boolean[][] actualMatrix = new boolean[matrix.getRowCount()][matrix.getColumnCount()];
    for (int i = 0; i < matrix.getRowCount(); i++) {
      for (int j = 0; j < matrix.getColumnCount(); j++) {
        actualMatrix[i][j] = matrix.getElement(i, j);
      }
    }

    boolean[][] expectedMatrix = new boolean[][]{
        new boolean[]{true, true, false},
        new boolean[]{false, true, false}
    };

    assertArrayEquals(
        "Single block matrix construction is wrong.",
        expectedMatrix,
        actualMatrix
    );
  }

}