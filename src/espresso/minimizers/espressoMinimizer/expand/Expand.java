package espresso.minimizers.espressoMinimizer.expand;

import espresso.minimizers.espressoMinimizer.minColCover.MaxCliqueHeuristic;
import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;
import espresso.utils.Pair;
import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;

import java.util.*;

public final class Expand {

  private static Set<Integer> loweringSet = new HashSet<>();
  private static Set<Integer> raisingSet = new HashSet<>();

  private static void clearStatic() {
    loweringSet.clear();
    raisingSet.clear();
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
    SingleOutputBlockMatrix blockMatrix = new SingleOutputBlockMatrix(offSet, cube);
    SingleOutputCoverMatrix coverMatrix = new SingleOutputCoverMatrix(onSet, cube);

    while (loweringSet.size() + raisingSet.size() < blockMatrix.getColumnCount() &&
        !blockMatrix.isFullyIgnored() &&
        !coverMatrix.isFullyIgnored()) {

      List<Integer> essentialColumns = essentialColumns(blockMatrix);
      loweringSet.addAll(essentialColumns);

      firstElimination(blockMatrix, essentialColumns);
      firstElimination(coverMatrix, essentialColumns);

      Set<Integer> maximumCoveringSet = maximumFeasibleCoveringSet(blockMatrix, coverMatrix);
      if (maximumCoveringSet.size() == 0) {
        int maxColumn = egProcedure(coverMatrix);
        if (maxColumn != -1) {
          maximumCoveringSet.add(maxColumn);
        }
      }

      List<Integer> inessentialColumns = inessentialColumns(blockMatrix);
      raisingSet.addAll(inessentialColumns);
      raisingSet.addAll(maximumCoveringSet);

      List<Integer> removeColumns = new ArrayList<>(inessentialColumns);
      removeColumns.addAll(maximumCoveringSet);
      secondElimination(blockMatrix, coverMatrix, removeColumns);
    }

    if (!blockMatrix.isFullyIgnored()) {
      loweringSet.addAll(
          MaxCliqueHeuristic.getInstance().calculateMinimumColumnCover(blockMatrix));
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
    return coverMatrix.maxTrueCountColumnIndex(false);
  }

  private static Set<Integer> maximumFeasibleCoveringSet(
      SingleOutputBlockMatrix blockMatrix,
      SingleOutputCoverMatrix coverMatrix
  ) {
    Set<Set<Integer>> coveringSets = computeFeasibleCoveringSets(
        blockMatrix,
        coverMatrix
    );

    if (coveringSets.size() == 0) {
      return new HashSet<>();
    }

    Set<Integer> maxCoveringSet = coveringSets.iterator().next();
    int maxContainmentCount = 0;

    for (Set<Integer> currentSet : coveringSets) {
      int containmentCount = 0;
      for (Set<Integer> otherSet : coveringSets) {
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

  private static Set<Set<Integer>> computeFeasibleCoveringSets(
      SingleOutputBlockMatrix blockingMatrix,
      SingleOutputCoverMatrix coverMatrix
  ) {
    Set<Set<Integer>> coveringSets = new HashSet<>();

    for (Iterator<Integer> rowIter = coverMatrix.ignoreRowsIterator(); rowIter.hasNext(); ) {
      int i = rowIter.next();

      Set<Integer> coveringSet = coverMatrix.computeCoveringSet(loweringSet, raisingSet, i);
      if (!coveringSets.contains(coveringSet) &&
          coveringSet.size() != 0 &&
          isFeasiblyCovered(i, blockingMatrix, coverMatrix)
          ) {
        coveringSets.add(coveringSet);
      }
    }

    return coveringSets;
  }

  private static boolean isFeasiblyCovered(
      int coverRowIndex,
      SingleOutputBlockMatrix blockMatrix,
      SingleOutputCoverMatrix coverMatrix
  ) {

    for (Iterator<Integer> rowIter = blockMatrix.ignoreRowsIterator(); rowIter.hasNext(); ) {
      int i = rowIter.next();

      int rowSum = 0;
      for (Iterator<Integer> columnIter = blockMatrix.ignoreColumnsIterator(); columnIter.hasNext(); ) {
        int j = columnIter.next();
        if (raisingSet.contains(j)) continue;

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

  private static List<Integer> essentialColumns(SingleOutputBlockMatrix blockMatrix) {
    List<Integer> retValue = new ArrayList<>();

    for (Iterator<Integer> rowIter = blockMatrix.ignoreRowsIterator(); rowIter.hasNext(); ) {
      int i = rowIter.next();

      int rowSum = 0;
      int oneIndex = 0;

      for (Iterator<Integer> columnIter = blockMatrix.ignoreColumnsIterator(); columnIter.hasNext(); ) {
        int j = columnIter.next();

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

  private static List<Integer> inessentialColumns(SingleOutputBlockMatrix matrix) {
    List<Integer> retValue = new ArrayList<>();

    for (Iterator<Integer> iter = matrix.ignoreColumnsIterator(); iter.hasNext(); ) {
      int j = iter.next();

      if (matrix.getFalseColumnCount(j) == 0) {
        retValue.add(j);
      }
    }

    return retValue;
  }

  private static void firstElimination(BooleanMatrix matrix, List<Integer> essentialColumns) {
    for (int columnIndex : essentialColumns) {
      if (matrix.isColumnIgnored(columnIndex)) continue;
      matrix.addIgnoredColumns(columnIndex);

      for (Iterator<Integer> iter = matrix.ignoreRowsIterator(); iter.hasNext(); ) {
        int i = iter.next();

        if (matrix.getElement(i, columnIndex)) {
          matrix.addIgnoredRows(i);
        }
      }
    }
  }

  //  When something is added to raisingSet.
  private static void secondElimination(
      SingleOutputBlockMatrix blockMatrix,
      SingleOutputCoverMatrix coverMatrix,
      List<Integer> columns
  ) {
    blockMatrix.addIgnoredColumns(columns);
    coverMatrix.addIgnoredColumns(columns);

    for (Iterator<Integer> rowIter = coverMatrix.ignoreRowsIterator(); rowIter.hasNext(); ) {
      int i = rowIter.next();

      Boolean hasTrue = false;
      for (Iterator<Integer> columnIter = coverMatrix.ignoreColumnsIterator(); columnIter.hasNext(); ) {
        int j = columnIter.next();
        hasTrue = hasTrue || coverMatrix.getElement(i, j);
      }

      if (!hasTrue) {
        coverMatrix.addIgnoredRows(i);
      }
    }
  }
}
