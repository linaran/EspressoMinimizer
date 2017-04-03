package espresso.minimizers.espressoMinimizer.expand;


import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CubeExpansionMatrix implements Iterable<List<Boolean>> {

  protected List<List<Boolean>> matrix = new ArrayList<>();
//  TODO: Output part blocking matrix.

  public CubeExpansionMatrix(Cover cover, Cube cube) {
    //region Exception Check
    if (cover.size() == 0) {
      throw new IllegalArgumentException(
          "Don't know how to create blocking matrix from an empty offSet."
      );
    }

    if (cover.inputCount() != cube.inputLength()) {
      throw new IllegalArgumentException(
          "Input counts for the given cube and cover need to be equal."
      );
    }
    //endregion

    for (Cube coverCube : cover) {
      List<Boolean> row = new ArrayList<>();

      for (int j = 0; j < coverCube.inputLength(); ++j) {
        InputState cubeInputState = cube.getInputState(j);
        InputState coverCubeInputState = coverCube.getInputState(j);

        row.add(generateMatrixElement(coverCubeInputState, cubeInputState));
      }

      matrix.add(row);
    }

//    TODO: Output part loop.
  }

  protected abstract boolean generateMatrixElement(InputState coverInputState, InputState cubeInputState);

  @Override
  public Iterator<List<Boolean>> iterator() {
    return matrix.iterator();
  }

  public int getRowCount() {
    return matrix.size();
  }

  public int getColumnCount() {
    return matrix.get(0).size();
  }

  public boolean getElement(int i, int j) {
    return matrix.get(i).get(j);
  }

  public boolean isEmpty() {
    return matrix.size() == 0;
  }

  @Override
  public String toString() {
    String retValue = "";
    for (List<Boolean> row : matrix) {
      retValue += row.toString() + "\n";
    }
    if (retValue.equals("")) return "empty\n";
    return retValue;
  }
}