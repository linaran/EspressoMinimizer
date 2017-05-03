package espresso.minimizers.espressoMinimizer.reduce;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.OutputState;
import espresso.boolFunction.cube.Cube;
import org.junit.Test;


import static espresso.boolFunction.InputState.DONTCARE;
import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.OutputState.OUTPUT;
import static org.junit.Assert.*;


public class ReduceCubeOrderTest {
  @Test
  public void organizeTest() throws Exception {
    Cube maxCub = new Cube(new InputState[]{DONTCARE, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{DONTCARE, DONTCARE, ONE}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{DONTCARE, ONE, ONE}, new OutputState[]{OUTPUT});
    Cube cube4 = new Cube(new InputState[]{ONE, ONE, ONE}, new OutputState[]{OUTPUT});

    Cover expectedCover = new Cover(maxCub, cube2, cube3, cube4);
    Cover actualCover = new Cover(cube4.copy(), cube2.copy(), maxCub.copy(), cube3.copy());
    ReduceCubeOrder.getInstance().organize(actualCover);

    for (int i = 0; i < actualCover.size(); i++) {
      assertEquals(
          "Reduce cube ordering is broken.",
          expectedCover.get(i),
          actualCover.get(i)
      );
    }
  }
}