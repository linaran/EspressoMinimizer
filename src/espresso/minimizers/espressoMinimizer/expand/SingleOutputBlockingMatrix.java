package espresso.minimizers.espressoMinimizer.expand;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;

import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;

public class SingleOutputBlockingMatrix extends CubeExpansionMatrix {

  public SingleOutputBlockingMatrix(Cover cover, Cube cube) {
    super(cover, cube);
  }

  @Override
  protected boolean generateMatrixElement(InputState coverInputState, InputState cubeInputState) {
    return cubeInputState == ONE && coverInputState == ZERO ||
        cubeInputState == ZERO && coverInputState == ONE;
  }
}
