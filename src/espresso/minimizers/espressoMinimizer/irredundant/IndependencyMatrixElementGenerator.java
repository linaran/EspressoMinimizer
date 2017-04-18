package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;
import espresso.minimizers.espressoMinimizer.utils.MatrixElementGenerator;

import java.util.Iterator;

public class IndependencyMatrixElementGenerator implements MatrixElementGenerator {

  private BooleanMatrix matrix;
  private int rowCount;

  public IndependencyMatrixElementGenerator(BooleanMatrix matrix) {
    this.matrix = matrix;
    rowCount = matrix.getNotIgnoredRowCount();
  }

  @Override
  public boolean generateElement(int rowIndex, int columnIndex) {
    rowIndex = skipIgnoredRows(rowIndex);
    columnIndex = skipIgnoredRows(columnIndex);

    for (Iterator<Integer> iter = matrix.ignoreColumnsIterator(); iter.hasNext(); ) {
      int column = iter.next();
      if (matrix.getElement(rowIndex, column) && matrix.getElement(columnIndex, column)) {
        return false;
      }
    }

    return true;
  }

  private int skipIgnoredRows(int rowIndex) {
    int i = rowIndex;
    while (i < rowCount && !matrix.isRowIgnored(rowIndex)) {
      ++i;
    }

    return i;
  }

  @Override
  public int getRowCount() {
    return rowCount;
  }

  @Override
  public int getColumnCount() {
    return rowCount;
  }
}
