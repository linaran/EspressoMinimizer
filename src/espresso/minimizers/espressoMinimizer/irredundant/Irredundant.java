package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;
import espresso.urpAlgorithms.Tautology;
import espresso.utils.Pair;

import java.util.HashSet;
import java.util.Set;

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
}
