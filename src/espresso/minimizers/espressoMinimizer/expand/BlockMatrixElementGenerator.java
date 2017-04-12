package espresso.minimizers.espressoMinimizer.expand;


import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.utils.MatrixElementGenerator;

import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;

public class BlockMatrixElementGenerator implements MatrixElementGenerator {

  private Cover cover;
  private Cube cube;

  public BlockMatrixElementGenerator(Cover cover, Cube cube) {
    //region Exceptions
    if (cover.size() == 0) {
      throw new IllegalArgumentException(
          "Cover can't be empty."
      );
    }
    if (cover.inputCount() != cube.inputLength()) {
      throw new IllegalArgumentException(
          "Input counts for cover and cube need to be equal."
      );
    }
    //endregion

    this.cover = cover;
    this.cube = cube;
  }

  @Override
  public boolean generateElement(int rowIndex, int columnIndex) {
    InputState coverInputState = cover.get(rowIndex).getInputState(columnIndex);
    InputState cubeInputState = cube.getInputState(columnIndex);

    return cubeInputState == ONE && coverInputState == ZERO ||
        cubeInputState == ZERO && coverInputState == ONE;
  }

  @Override
  public int getRowCount() {
    return cover.size();
  }

  @Override
  public int getColumnCount() {
    return cube.inputLength();
  }

}
