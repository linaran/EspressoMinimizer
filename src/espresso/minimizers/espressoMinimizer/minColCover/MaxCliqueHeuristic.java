package espresso.minimizers.espressoMinimizer.minColCover;


import espresso.minimizers.espressoMinimizer.irredundant.IndependencyMatrix;
import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;

import java.util.*;

public class MaxCliqueHeuristic implements MinimumColumnCoverHeuristic {

  private static MaxCliqueHeuristic instance = new MaxCliqueHeuristic();

  public static MaxCliqueHeuristic getInstance() {
    return instance;
  }

  @Override
  public Set<Integer> calculateMinimumColumnCover(BooleanMatrix matrix) {
    Set<Integer> columnCover = new HashSet<>();

    simplify(matrix);
    IndependencyMatrix independencyMatrix = new IndependencyMatrix(matrix);

    Set<Integer> maxClique = independencyMatrix.computeMaxClique();
    for (Integer rowIndex : maxClique) {
      int maxColumnIndex = -1;
      int maxTrueColumnCount = -1;

      for (Iterator<Integer> iter = matrix.ignoreColumnsIterator(); iter.hasNext(); ) {
        int columnIndex = iter.next();
        int trueColumnCount = matrix.getTrueColumnCount(columnIndex, false);
        if (matrix.getElement(rowIndex, columnIndex) &&
            trueColumnCount > maxTrueColumnCount
            ) {
          maxColumnIndex = columnIndex;
          maxTrueColumnCount = trueColumnCount;
        }
      }

//      TODO: Watch this.
      if (maxColumnIndex != -1) {
        columnCover.add(maxColumnIndex);
        columnChoiceCleanup(matrix, maxColumnIndex);
      }
    }

    if (!matrix.isFullyIgnored()) {
      columnCover.addAll(calculateMinimumColumnCover(matrix));
    } else {
      weed(matrix, columnCover);
    }

    return columnCover;
  }

  public void weed(BooleanMatrix matrix, Set<Integer> columnCover) {
    List<Integer> columns = new ArrayList<>(columnCover);
    int[] redundancyCounts = new int[columns.size()];

    for (int i = 0; i < matrix.getRowCount(); i++) {
      int sum = 0;
      for (Integer j : columnCover) {
        sum += (matrix.getElement(i, j) ? 1 : 0);
      }

      if (sum > 1) {
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
          int j = columns.get(columnIndex);

          if (matrix.getElement(i, j)) {
            redundancyCounts[columnIndex]++;
          }
        }
      }
    }

    List<Integer> redundantColumns = new ArrayList<>();
    for (int i = 0; i < columns.size(); i++) {
      if (redundancyCounts[i] == matrix.getTrueColumnCount(columns.get(i))) {
        redundantColumns.add(columns.get(i));
      }
    }

    if (redundantColumns.size() != 0) {
      columnCover.remove(redundantColumns.get(0));
      weed(matrix, columnCover);
    }
  }

  public void simplify(BooleanMatrix matrix) {
    for (Iterator<Integer> iter1 = matrix.ignoreRowsIterator(); iter1.hasNext(); ) {
      int rowIndex1 = iter1.next();
      for (Iterator<Integer> iter2 = matrix.ignoreRowsIterator(); iter2.hasNext(); ) {
        int rowIndex2 = iter2.next();

        if (rowIndex1 != rowIndex2 && isSecondRowRedundant(matrix, rowIndex1, rowIndex2)) {
          matrix.addIgnoredRows(rowIndex2);
        }
      }
    }
  }

  private boolean isSecondRowRedundant(BooleanMatrix matrix, int firstRow, int secondRow) {
    if (matrix.getTrueRowCount(firstRow) == 0 || matrix.getTrueRowCount(secondRow) == 0) {
      throw new IllegalArgumentException(
          "Boolean rows containing only false values are not allowed."
      );
    }

    for (Iterator<Integer> iter1 = matrix.ignoreColumnsIterator(); iter1.hasNext(); ) {
      int columnIndex = iter1.next();

      boolean firstRowElement = matrix.getElement(firstRow, columnIndex);
      boolean secondRowElement = matrix.getElement(secondRow, columnIndex);

      if (firstRowElement && !secondRowElement) {
        return false;
      }
    }

    return true;
  }

  private void columnChoiceCleanup(BooleanMatrix matrix, int columnIndex) {
    matrix.addIgnoredColumns(columnIndex);
    for (Iterator<Integer> rowIter = matrix.ignoreRowsIterator(); rowIter.hasNext(); ) {
      int rowIndex = rowIter.next();
      if (matrix.getElement(rowIndex, columnIndex)) {
        matrix.addIgnoredRows(rowIndex);
      }
    }
  }
}
