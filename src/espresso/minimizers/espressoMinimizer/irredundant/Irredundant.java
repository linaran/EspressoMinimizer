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

  private static BooleanMatrix calculateAuxiliaryMatrix(Cover alpha, Cover beta) {
    List<List<Integer>> minSets = calculateMinimalSets(alpha, beta, null);
    return new AuxiliaryMatrix(minSets, alpha.size());
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
