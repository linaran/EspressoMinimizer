package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

import java.util.Iterator;
import java.util.List;

public class NoCoverMatrix extends BooleanMatrix {

  public NoCoverMatrix(List<List<Integer>> minSets, int columnCount) {
    super(new NoCoverMatrixElementGenerator(minSets, columnCount));
  }

  public void simplify() {
    for (Iterator<Integer> iter1 = ignoreRowsIterator(); iter1.hasNext(); ) {
      int rowIndex1 = iter1.next();
      for (Iterator<Integer> iter2 = ignoreRowsIterator(); iter2.hasNext(); ) {
        int rowIndex2 = iter2.next();

        if (isSecondRowRedundant(rowIndex1, rowIndex2)) {
          addIgnoredRows(rowIndex2);
        }
      }
    }
  }

  private boolean isSecondRowRedundant(int firstRow, int secondRow) {
    for (Iterator<Integer> iter1 = ignoreColumnsIterator(); iter1.hasNext(); ) {
      int columnIndex = iter1.next();

      boolean firstRowElement = getElement(firstRow, columnIndex);
      boolean secondRowElement = getElement(secondRow, columnIndex);

      if (firstRowElement && !secondRowElement) {
        return false;
      }
    }

    return true;
  }
}
