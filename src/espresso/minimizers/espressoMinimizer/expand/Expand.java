package espresso.minimizers.espressoMinimizer.expand;

import espresso.Pair;
import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;

import java.util.*;

final public class Expand {

  private static HashSet<Integer> loweringSet = new HashSet<>();
  private static HashSet<Integer> raisingSet = new HashSet<>();

  private static HashSet<Integer> removedCoverRows = new HashSet<>();
  private static HashSet<Integer> removedBlockRows = new HashSet<>();
  private static HashSet<Integer> removedColumns = new HashSet<>();

  private static void clearStatic() {
    loweringSet.clear();
    raisingSet.clear();

    removedCoverRows.clear();
    removedBlockRows.clear();
    removedColumns.clear();
  }

  private Expand() {
  }

  public static Cover expandCover(Cover onSet, Cover offSet) {
    Cover retValue = new Cover(onSet.inputCount(), onSet.outputCount());
    Cover sortedCover = new Cover(onSet);
    sortedCover.sort(new CubeSizeComparator().reversed());

    HashSet<Integer> removedCubes = new HashSet<>();
    for (int i = 0; i < sortedCover.size(); i++) {
      if (removedCubes.contains(i)) continue;
      Cube cube = sortedCover.get(i);

      Pair<Cube, List<Integer>> pair = singleOutputCubeExpand(cube, sortedCover, offSet);
      removedCubes.addAll(pair.second);
      retValue.add(pair.first);
    }

    return retValue;
  }

  public static Pair<Cube, List<Integer>> singleOutputCubeExpand(Cube cube, Cover onSet, Cover offSet) {
    clearStatic(); //TODO: Code smell.
    SingleOutputBlockingMatrix blockMatrix = new SingleOutputBlockingMatrix(offSet, cube);
    SingleOutputCoverMatrix coverMatrix = new SingleOutputCoverMatrix(onSet, cube);

    while (loweringSet.size() + raisingSet.size() < blockMatrix.getColumnCount() &&
        !isBlockMatrixEmpty(blockMatrix) &&
        !isCoverMatrixEmpty(coverMatrix)) {

      List<Integer> essentialColumns = essentialColumns(blockMatrix);
      loweringSet.addAll(essentialColumns);

      firstElimination(blockMatrix, removedBlockRows, essentialColumns);
      firstElimination(coverMatrix, removedCoverRows, essentialColumns);

      HashSet<Integer> maximumCoveringSet = maximumFeasibleCoveringSet(blockMatrix, coverMatrix);
      if (maximumCoveringSet.size() == 0) {
        maximumCoveringSet.add(egProcedure(coverMatrix));
      }

      List<Integer> inessentialColumns = inessentialColumns(blockMatrix);
      raisingSet.addAll(inessentialColumns);
      raisingSet.addAll(maximumCoveringSet);

      List<Integer> removeColumns = new ArrayList<>(inessentialColumns);
      removeColumns.addAll(maximumCoveringSet);
      secondElimination(coverMatrix, removeColumns);
    }

    if (!isBlockMatrixEmpty(blockMatrix)) {
      throw new UnsupportedOperationException("Not finished yet. MinLow procedure.\n" + loweringSet.toString());
    }

    List<Integer> containedRows = rowsContainedByLoweringSet(coverMatrix);
    Cube expandedCube = loweringSetToCube(cube);

    return new Pair<>(expandedCube, containedRows);
  }

  private static Cube loweringSetToCube(Cube priorExpansionCube) {
    Cube expandedCube = new Cube(priorExpansionCube.inputLength(), priorExpansionCube.outputLength());
    expandedCube.setOutput(priorExpansionCube);

    for (Integer index : loweringSet) {
      expandedCube.setInput(priorExpansionCube.getInputState(index), index);
    }

    return expandedCube;
  }

  private static List<Integer> rowsContainedByLoweringSet(SingleOutputCoverMatrix coverMatrix) {
    List<Integer> retValue = new ArrayList<>();

    for (int i = 0; i < coverMatrix.getRowCount(); i++) {
      int rowSum = 0;
      for (int j = 0; j < coverMatrix.getColumnCount(); j++) {
        if (loweringSet.contains(j)) {
          rowSum += (coverMatrix.getElement(i, j) ? 1 : 0);
        }
      }

      if (rowSum == 0) {
        retValue.add(i);
      }
    }

    return retValue;
  }

  private static int egProcedure(SingleOutputCoverMatrix coverMatrix) {
    return coverMatrix.maxTrueCountColumnIndex();
  }

  private static HashSet<Integer> maximumFeasibleCoveringSet(
      SingleOutputBlockingMatrix blockMatrix,
      SingleOutputCoverMatrix coverMatrix
  ) {
    List<HashSet<Integer>> coveringSets = computeFeasibleCoveringSets(
        blockMatrix,
        coverMatrix
    );

    HashSet<Integer> maxCoveringSet = coveringSets.get(0);
    int maxContainmentCount = 0;

    for (HashSet<Integer> currentSet : coveringSets) {
      int containmentCount = 0;
      for (HashSet<Integer> otherSet : coveringSets) {
//        Didn't mix this up with equals.
        if (currentSet != otherSet && currentSet.containsAll(otherSet)) {
          ++containmentCount;
        }
      }

      if (containmentCount > maxContainmentCount) {
        maxContainmentCount = containmentCount;
        maxCoveringSet = currentSet;
      }
    }

    return maxCoveringSet;
  }

  private static List<HashSet<Integer>> computeFeasibleCoveringSets(
      SingleOutputBlockingMatrix blockingMatrix,
      SingleOutputCoverMatrix coverMatrix
  ) {
    List<HashSet<Integer>> coveringSets = new ArrayList<>();

    for (int i = 0; i < coverMatrix.getRowCount(); i++) {
      if (removedCoverRows.contains(i)) continue;

      if (isFeasiblyCovered(i, blockingMatrix, coverMatrix)) {
        coveringSets.add(coverMatrix.computeCoveringSet(loweringSet, raisingSet, removedColumns, i));
      }
    }

    return coveringSets;
  }

  private static boolean isFeasiblyCovered(
      int coverRowIndex,
      SingleOutputBlockingMatrix blockMatrix,
      SingleOutputCoverMatrix coverMatrix
  ) {

    for (int i = 0; i < blockMatrix.getRowCount(); i++) {
      if (removedBlockRows.contains(i)) continue;

      int rowSum = 0;
      for (int j = 0; j < coverMatrix.getColumnCount(); j++) {
        if (removedColumns.contains(j) || raisingSet.contains(j)) continue;

        if (loweringSet.contains(j) || !coverMatrix.getElement(coverRowIndex, j)) {
          rowSum += (blockMatrix.getElement(i, j) ? 1 : 0);
        }
      }

      if (rowSum < 1) {
        return false;
      }
    }

    return true;
  }

  private static List<Integer> essentialColumns(SingleOutputBlockingMatrix blockMatrix) {
    List<Integer> retValue = new ArrayList<>();

    for (int i = 0; i < blockMatrix.getRowCount(); i++) {
      if (removedBlockRows.contains(i)) continue;

      int rowSum = 0;
      int oneIndex = 0;

      for (int j = 0; j < blockMatrix.getColumnCount(); j++) {
        if (removedColumns.contains(j)) continue;

        rowSum += (blockMatrix.getElement(i, j) ? 1 : 0);
        if (blockMatrix.getElement(i, j)) {
          oneIndex = j;
        }
      }

      if (rowSum == 1) {
        retValue.add(oneIndex);
      }
    }

    return retValue;
  }

  private static List<Integer> inessentialColumns(SingleOutputBlockingMatrix matrix) {
    List<Integer> retValue = new ArrayList<>();

    for (int j = 0; j < matrix.getColumnCount(); j++) {
      if (removedColumns.contains(j)) continue;

      //region Vanilla
      //      boolean hasTrue = false;
      //      for (int i = 0; i < matrix.getRowCount(); i++) {
      //        if (removedBlockRows.contains(i)) continue;
      //        hasTrue = hasTrue || matrix.getElement(i, j);
      //      }
      //
      //      if (!hasTrue) {
      //        retValue.add(j);
      //      }
      //endregion

      if (matrix.getFalseColumnCount(j) == 0) {
        retValue.add(j);
      }
    }

    return retValue;
  }

  private static void firstElimination(
      CubeExpansionMatrix matrix,
      HashSet<Integer> removedRows,
      List<Integer> essentialColumns
  ) {
    for (int columnIndex : essentialColumns) {
      if (removedColumns.contains(columnIndex)) continue;
      removedColumns.add(columnIndex);

      for (int i = 0; i < matrix.getRowCount(); i++) {
        if (removedRows.contains(i)) continue;

        if (matrix.getElement(i, columnIndex)) {
          removedRows.add(i);
        }
      }
    }
  }

  //  When something is added to raisingSet.
  private static void secondElimination(
      SingleOutputCoverMatrix coverMatrix,
      List<Integer> columns
  ) {
    removedColumns.addAll(columns);

    for (int i = 0; i < coverMatrix.getRowCount(); ++i) {
      if (removedCoverRows.contains(i)) continue;

      Boolean hasTrue = false;
      for (int j = 0; j < coverMatrix.getColumnCount(); ++j) {
        if (removedColumns.contains(j)) continue;
        hasTrue = hasTrue || coverMatrix.getElement(i, j);
      }

      if (!hasTrue) {
        removedCoverRows.add(i);
      }
    }
  }

  private static boolean isBlockMatrixEmpty(SingleOutputBlockingMatrix blockMatrix) {
    return removedColumns.size() == blockMatrix.getColumnCount() ||
        removedBlockRows.size() == blockMatrix.getRowCount();
  }

  private static boolean isCoverMatrixEmpty(SingleOutputCoverMatrix coverMatrix) {
    return removedColumns.size() == coverMatrix.getColumnCount() ||
        removedCoverRows.size() == coverMatrix.getRowCount();
  }
}
