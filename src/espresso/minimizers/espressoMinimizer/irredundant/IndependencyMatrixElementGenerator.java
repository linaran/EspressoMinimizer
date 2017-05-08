package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;
import espresso.minimizers.espressoMinimizer.utils.MatrixElementGenerator;

import java.util.Iterator;

public class IndependencyMatrixElementGenerator implements MatrixElementGenerator {

  private BooleanMatrix matrix;

  public IndependencyMatrixElementGenerator(BooleanMatrix matrix) {
    this.matrix = matrix;
  }

  @Override
  public boolean generateElement(int rowIndex1, int rowIndex2) {
    for (Iterator<Integer> iter = matrix.ignoreColumnsIterator(); iter.hasNext(); ) {
      int column = iter.next();
      if (matrix.getElement(rowIndex1, column) && matrix.getElement(rowIndex2, column)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public int getRowCount() {
    return matrix.getRowCount();
  }

  @Override
  public int getColumnCount() {
    return matrix.getRowCount();
  }
}
