package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;
import espresso.utils.Pair;

import java.util.*;

import static espresso.urpAlgorithms.Tautology.singleOutputTautologyCheck;

public class Irredundant {

  private Irredundant() {
  }

  private static Pair<Set<Cube>, Set<Cube>> partitionRedundancy(Cover onSet, Cover dontcareSet) {
    Set<Cube> relativelyEssential = new HashSet<>();
    Set<Cube> redundant = new HashSet<>();

    Cover unionSet = onSet.union(dontcareSet);

    for (Cube cube : unionSet) {
      unionSet.remove(cube);

      if (singleOutputTautologyCheck(unionSet.cofactor(cube))) {
        redundant.add(cube.copy());
      } else {
        relativelyEssential.add(cube.copy());
      }

      unionSet.add(cube);
    }

    return new Pair<>(relativelyEssential, redundant);
  }

  private static Set<Cube> partiallyRedundant(
      Set<Cube> redundant,
      Set<Cube> relativelyEssential,
      Cover dontcareSet
  ) {
    Set<Cube> partiallyRedundant = new HashSet<>();

    Cover unionSet = new Cover(dontcareSet);
    unionSet.addAll(relativelyEssential);

    for (Cube cube : redundant) {
      if (singleOutputTautologyCheck(unionSet.cofactor(cube))) {
        partiallyRedundant.add(cube.copy());
      }
    }

    return partiallyRedundant;
  }

  private static Set<Integer> minimumColumnCover(NoCoverMatrix noCoverMatrix) {
    Set<Integer> columnCover = new HashSet<>();

    noCoverMatrix.simplify();
    IndependencyMatrix independencyMatrix = new IndependencyMatrix(noCoverMatrix);

    Set<Integer> maxClique = independencyMatrix.computeMaxClique();
    for (Integer rowIndex : maxClique) {
      int maxColumnIndex = -1;
      int maxTrueColumnCount = -1;

      for (Iterator<Integer> iter = noCoverMatrix.ignoreColumnsIterator(); iter.hasNext(); ) {
        int columnIndex = iter.next();
        int trueColumnCount = noCoverMatrix.getTrueColumnCount(columnIndex, false);
        if (noCoverMatrix.getElement(rowIndex, columnIndex) &&
            trueColumnCount > maxTrueColumnCount
            ) {
          maxColumnIndex = columnIndex;
          maxTrueColumnCount = trueColumnCount;
        }
      }

      columnCover.add(maxColumnIndex);
      columnChoiceCleanup(noCoverMatrix, maxColumnIndex);
    }

    if (!noCoverMatrix.isFullyIgnored()) {
      columnCover.addAll(minimumColumnCover(noCoverMatrix));
    } else {
      weed(noCoverMatrix, columnCover);
    }

    return columnCover;
  }

  //  Every day.
  private static void weed(NoCoverMatrix noCoverMatrix, Set<Integer> columnCover) {
    List<Integer> columns = new ArrayList<>(columnCover);
    int[] redundancyCounts = new int[columns.size()];

    for (int i = 0; i < noCoverMatrix.getRowCount(); i++) {
      int sum = 0;
      for (Integer j : columnCover) {
        sum += (noCoverMatrix.getElement(i, j) ? 1 : 0);
      }

      if (sum > 1) {
        for (int columnIndex = 0; columnIndex < columns.size(); columnIndex++) {
          int j = columns.get(columnIndex);

          if (noCoverMatrix.getElement(i, j)) {
            redundancyCounts[columnIndex]++;
          }
        }
      }
    }

    List<Integer> redundantColumns = new ArrayList<>();
    for (int i = 0; i < columns.size(); i++) {
      if (redundancyCounts[i] == noCoverMatrix.getTrueColumnCount(columns.get(i))) {
        redundantColumns.add(columns.get(i));
      }
    }

    if (redundantColumns.size() != 0) {
      columnCover.remove(redundantColumns.get(0));
      weed(noCoverMatrix, columnCover);
    }
  }

  private static void columnChoiceCleanup(NoCoverMatrix matrix, int columnIndex) {
    matrix.addIgnoredColumns(columnIndex);
    for (Iterator<Integer> rowIter = matrix.ignoreRowsIterator(); rowIter.hasNext(); ) {
      int rowIndex = rowIter.next();
      if (matrix.getElement(rowIndex, columnIndex)) {
        matrix.addIgnoredRows(rowIndex);
      }
    }
  }

  private static NoCoverMatrix calculateNoCoverMatrix(
      Set<Cube> relativelyEssential,
      Cover dontCareSet,
      Set<Cube> partiallyRedundant
  ) {
    Cube example = relativelyEssential.iterator().next();

    Cover importantCubes = new Cover(example.inputLength(), example.outputLength());
    importantCubes.addAll(dontCareSet);
    importantCubes.addAll(relativelyEssential);

    Cover partRedundantCover = new Cover(example.inputLength(), example.outputLength());
    partRedundantCover.addAll(partiallyRedundant);

    List<List<Integer>> minSets = new ArrayList<>();
    for (Cube partiallyRedundantCube : partiallyRedundant) {
      Cover beta = importantCubes.cofactor(partiallyRedundantCube);
      Pair<Cover, List<Integer>> alphaPair =
          partRedundantCover.trackingCofactor(partiallyRedundantCube);

      minSets.addAll(calculateMinimalSets(alphaPair.first, beta, alphaPair.second));
    }

    return new NoCoverMatrix(minSets, partRedundantCover.size());
  }

  private static List<List<Integer>> calculateMinimalSets(
      Cover alpha,
      Cover beta,
      List<Integer> alphaTrack
  ) {
    Cover alphaBetaUnion = alpha.union(beta);

    if (alphaBetaUnion.isUnate()) {
      List<Integer> minSet = new ArrayList<>();

      if (beta.hasDONTCARERow()) {
        return wrapInList(minSet);
      }

      for (int i = 0; i < alpha.size(); i++) {
        if (alpha.get(i).isInputPartTautology()) {
          int index = (alphaTrack != null ? alphaTrack.get(i) : i);
          minSet.add(index);
        }
      }

      return wrapInList(minSet);
    } else {
      int splitIndex = alphaBetaUnion.binateSelect();
      Cube variable = alpha.generateVariableCube(splitIndex);
      Cube complement = variable.copy().inputComplement();

      Pair<Cover, List<Integer>> positiveAlphaPair = alpha.trackingCofactor(variable, alphaTrack);
      Pair<Cover, List<Integer>> negativeAlphaPair = alpha.trackingCofactor(complement, alphaTrack);

      Cover positiveBetaCofactor = beta.cofactor(variable);
      Cover negativeBetaCofactor = beta.cofactor(complement);

      List<List<Integer>> positiveSets =
          calculateMinimalSets(positiveAlphaPair.first, positiveBetaCofactor, positiveAlphaPair.second);
      List<List<Integer>> negativeSets =
          calculateMinimalSets(negativeAlphaPair.first, negativeBetaCofactor, positiveAlphaPair.second);

      List<List<Integer>> mergedSets = new ArrayList<>();
      mergedSets.addAll(positiveSets);
      mergedSets.addAll(negativeSets);

      return mergedSets;
    }
  }

  private static List<List<Integer>> wrapInList(List<Integer> minSet) {
    List<List<Integer>> retValue = new ArrayList<>();
    retValue.add(minSet);
    return retValue;
  }
}
