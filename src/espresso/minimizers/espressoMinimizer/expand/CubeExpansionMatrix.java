package espresso.minimizers.espressoMinimizer.expand;


import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CubeExpansionMatrix implements Iterable<List<Boolean>> {

  private int[] trueColumnCount;
  private List<List<Boolean>> matrix = new ArrayList<>();

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

    trueColumnCount = new int[cover.get(0).inputLength()];

    for (Cube coverCube : cover) {
      List<Boolean> row = new ArrayList<>();

      for (int j = 0; j < coverCube.inputLength(); ++j) {
        InputState cubeInputState = cube.getInputState(j);
        InputState coverCubeInputState = coverCube.getInputState(j);

        boolean matrixElement = generateMatrixElement(coverCubeInputState, cubeInputState);
        trueColumnCount[j] += (matrixElement ? 1 : 0);
        row.add(matrixElement);
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

  public int getTrueColumnCount(int index) {
    return trueColumnCount[index];
  }

  public int getFalseColumnCount(int index) {
    return getRowCount() - trueColumnCount[index];
  }

  public boolean getElement(int i, int j) {
    return matrix.get(i).get(j);
  }

  public int getTrueRowCount(int rowIndex) {
    int retValue = 0;

    for (int columnIndex = 0; columnIndex < getColumnCount(); columnIndex++) {
      retValue += (getElement(rowIndex, columnIndex) ? 1 : 0);
    }

    return retValue;
  }

  public int getFalseRowCount(int rowIndex) {
    return getColumnCount() - getTrueColumnCount(rowIndex);
  }

  public boolean isEmpty() {
    return matrix.size() == 0;
  }

  @Override
  public String toString() {
    String retValue = "";
    for (List<Boolean> row : matrix) {
      retValue += rowToString(row) + "\n";
    }
    if (retValue.equals("")) return "empty\n";
    return retValue;
  }

  private String rowToString(List<Boolean> row) {
    StringBuilder retValue = new StringBuilder("[");

    for (int i = 0; i < row.size(); i++) {
      retValue.append(String.valueOf((row.get(i) ? 1 : 0)));

      if (i + 1 != row.size()) {
        retValue.append(", ");
      }
    }

    retValue.append("]");
    return retValue.toString();
  }
}
