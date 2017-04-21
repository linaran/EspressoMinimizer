package espresso.minimizers.espressoMinimizer.utils;


import espresso.utils.IgnoreIndexIterator;

import java.util.*;
import java.util.stream.IntStream;

public class BooleanMatrix implements Iterable<List<Boolean>> {

  private int[] trueColumnCount;
  private int[] trueIgnoreColumnCount;

  private Set<Integer> ignoredColumns = new HashSet<>();
  private Set<Integer> ignoredRows = new HashSet<>();

  private List<List<Boolean>> matrix = new ArrayList<>();

  public BooleanMatrix(MatrixElementGenerator generator) {
    if (generator.getRowCount() == 0 || generator.getColumnCount() == 0) {
      throw new UnsupportedOperationException("Can't create a boolean matrix with 0 columns or rows");
    }

    trueColumnCount = new int[generator.getColumnCount()];
    trueIgnoreColumnCount = new int[generator.getColumnCount()];

    for (int i = 0; i < generator.getRowCount(); i++) {
      List<Boolean> row = new ArrayList<>();

      for (int j = 0; j < generator.getColumnCount(); j++) {
        boolean matrixElement = generator.generateElement(i, j);
        trueColumnCount[j] += (matrixElement ? 1 : 0);
        row.add(matrixElement);
      }

      matrix.add(row);
    }
  }

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

  public int getNotIgnoredRowCount() {
    return getRowCount() - ignoredRows.size();
  }

  public int getNotIgnoredColumnCount() {
    return getColumnCount() - ignoredColumns.size();
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
    return getTrueRowCount(rowIndex, true);
  }

  public int getTrueRowCount(int rowIndex, boolean countIgnoredValues) {
    int retValue = 0;

    Iterator<Integer> iter;
    if (!countIgnoredValues) {
      iter = new IgnoreIndexIterator(getColumnCount(), ignoredColumns);
    } else {
      iter = new IgnoreIndexIterator(getColumnCount(), new HashSet<>());
    }

    for (; iter.hasNext(); ) {
      int columnIndex = iter.next();
      retValue += (getElement(rowIndex, columnIndex) ? 1 : 0);
    }

    return retValue;
  }

  public int getFalseRowCount(int rowIndex) {
    return getColumnCount() - getTrueColumnCount(rowIndex);
  }

//  TODO: getFalseRowCount(int rowIndex, boolean countIgnoredValues){}

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
