package espresso.minimizers.espressoMinimizer.expand;


import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.cube.Cube;
import espresso.utils.IgnoreIndexIterator;

import java.util.*;

public abstract class CubeExpansionMatrix implements Iterable<List<Boolean>> {

  private int[] trueColumnCount;
  private int[] trueIgnoreColumnCount;

  private Set<Integer> ignoredColumns = new HashSet<>();
  private Set<Integer> ignoredRows = new HashSet<>();

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
    trueIgnoreColumnCount = new int[cover.get(0).inputLength()];

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

  public Iterator<Integer> ignoreRowsIterator() {
    return new IgnoreIndexIterator(getRowCount(), ignoredRows);
  }

  public Iterator<Integer> ignoreColumnsIterator() {
    return new IgnoreIndexIterator(getColumnCount(), ignoredColumns);
  }

  public void clearIgnoredRows() {
    ignoredRows.clear();
    Arrays.fill(trueIgnoreColumnCount, 0);
  }

  public void clearIgnoredColumns() {
    ignoredColumns.clear();
  }

  protected void validateColumnIndex(int column) {
    if (column < 0 || column >= getColumnCount()) {
      throw new IndexOutOfBoundsException("Column index out of bounds.");
    }
  }

  protected void validateRowIndex(int row) {
    if (row < 0 || row >= getRowCount()) {
      throw new IndexOutOfBoundsException("Row index out of bounds.");
    }
  }

  private void increaseTrueIgnoreColumnCount(int row) {
    for (int j = 0; j < getColumnCount(); j++) {
      boolean value = getElement(row, j);
      trueIgnoreColumnCount[j] += (value ? 1 : 0);
    }
  }

  public void addIgnoredColumns(Integer... columns) {
    addIgnoredColumns(Arrays.asList(columns));
  }

  public void addIgnoredColumns(Collection<Integer> columns) {
    for (int column : columns) {
      validateColumnIndex(column);
      ignoredColumns.add(column);
    }
  }

  public void addIgnoredRows(Integer... rows) {
    addIgnoredRows(Arrays.asList(rows));
  }

  public void addIgnoredRows(Collection<Integer> rows) {
    for (int row : rows) {
      validateRowIndex(row);

      if (!ignoredRows.contains(row)) {
        increaseTrueIgnoreColumnCount(row);
      }
      ignoredRows.add(row);
    }
  }

  public boolean isRowIgnored(int row) {
    return ignoredRows.contains(row);
  }

  public boolean isColumnIgnored(int column) {
    return ignoredColumns.contains(column);
  }

  public int getRowCount() {
    return matrix.size();
  }

  public int getColumnCount() {
    return matrix.get(0).size();
  }

  public int getTrueColumnCount(int index) {
    return getTrueColumnCount(index, true);
  }

  public int getTrueColumnCount(int index, boolean countIgnoredValues) {
    if (countIgnoredValues) {
      return trueColumnCount[index];
    } else {
      return trueColumnCount[index] - trueIgnoreColumnCount[index];
    }
  }

  public int getFalseColumnCount(int index) {
    return getFalseColumnCount(index, true);
  }

  public int getFalseColumnCount(int index, boolean countIgnoredValues) {
    if (countIgnoredValues) {
      return getRowCount() - trueColumnCount[index];
    } else {
      int ignoredFalseCount = ignoredRows.size() - trueIgnoreColumnCount[index];
      return (getRowCount() - trueColumnCount[index]) - ignoredFalseCount;
    }
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

  public boolean getElement(int i, int j) {
    return matrix.get(i).get(j);
  }

  public Set<Integer> getIgnoredColumns() {
    return Collections.unmodifiableSet(ignoredColumns);
  }

  public Set<Integer> getIgnoredRows() {
    return Collections.unmodifiableSet(ignoredRows);
  }

  public boolean isFullyIgnored() {
    return getColumnCount() == ignoredColumns.size() ||
        getRowCount() == ignoredRows.size();
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
