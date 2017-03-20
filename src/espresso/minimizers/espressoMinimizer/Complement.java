package espresso.minimizers.espressoMinimizer;

import espresso.boolFunction.Cover;
import espresso.boolFunction.OutputState;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.CoverUtility;

import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;

/**
 * Complement
 */
public class Complement {

  /**
   * Method to calculate a complement of a multiple output boolean function.
   * Boolean function is here defined with two {@link Cover}s. First cover
   * defines inputs for which boolean function returns true.
   * Second cover defines inputs for which boolean function outputs either
   * true or false, doesn't matter.
   * <p>
   * TODO: Needs to be tested.
   *
   * @param onSet       {@link Cover} for which the boolean function will output true
   * @param dontcareSet {@link Cover} for which the boolean function will output true/false
   * @return A {@link Cover} for which the boolean function will return false
   */
  public static Cover complement(Cover onSet, Cover dontcareSet) {
    if (onSet.inputCount() != dontcareSet.inputCount() ||
        onSet.outputCount() != dontcareSet.outputCount()) {
      throw new IllegalArgumentException(
          "Given covers are not compatible. Input/output lengths are different"
      );
    }

    Cover retValue = new Cover();

    for (int i = 0; i < onSet.outputCount(); i++) {
      Cover singleOutputOnSet = extract(onSet, i);
      Cover singleOutputDontcareSet = extract(dontcareSet, i);

      retValue.addAll(singleOutputComplement(singleOutputOnSet.union(singleOutputDontcareSet)));
    }

    return retValue;
  }

  /**
   * Fast method for calculating complement of single output {@link Cover}s.
   * <p>
   * Warning: Method ignores output parts and won't throw any exceptions if
   * given a multiple output {@link Cover}. You may get results that make no sense.
   * For complementing multiple output {@link Cover}s
   * see {@link Complement#complement(Cover, Cover)}.
   *
   * @param f Single output {@link Cover}
   * @return Complement of given {@link Cover} which is a {@link Cover}
   */
  public static Cover singleOutputComplement(Cover f) {
    //region Special cases
    Cover retValue = new Cover();

//    If given cover is empty then the complement is a tautology.
    if (f.size() == 0) {
      retValue.add(new Cube(f.inputCount(), f.outputCount()));
      return retValue;
    }

    if (f.hasDONTCARERow()) {
      return retValue;
    }

    if (f.isUnate()) {
      return UnateOperations.unateComplement(f);
    }

    Cube c = new Cube(f.inputCount(), f.outputCount());
    boolean cubeChanged = false;
    for (int i = 0; i < f.inputCount(); i++) {
      if (f.getOneColumnCount(i) == f.size()) {
        c.setInput(ONE, i);
        cubeChanged = true;
      } else if (f.getZeroColumnCount(i) == f.size()) {
        c.setInput(ZERO, i);
        cubeChanged = true;
      }
    }

    if (cubeChanged) {
      retValue.addAll(c.complement());
      f = f.cofactor(c);
    }
    //endregion

    int splitIndex = f.binateSelect();
    Cube splitCube = new Cube(f.inputCount(), f.outputCount());
    splitCube.setInput(ONE, splitIndex);

    Cover[] cofactors = f.shannonCofactors(splitIndex);

    retValue.addAll(
        CoverUtility.mergeWithContainment(
            singleOutputComplement(cofactors[0]),
            singleOutputComplement(cofactors[1]),
            splitCube,
            false)
    );

    return retValue;
  }

  private static Cover extract(Cover cover, int extractionIndex) {
    if (extractionIndex < 0 || extractionIndex >= cover.outputCount()) {
      throw new IllegalArgumentException("Extraction index out of bounds.");
    }

    Cover retValue = new Cover();

    for (Cube cube : cover) {
      if (cube.getOutputState(extractionIndex) == OutputState.OUTPUT) {
        Cube extractedCube = new Cube(cover.inputCount(), cover.outputCount(), extractionIndex);
        extractedCube.setInput(cube);
        retValue.add(extractedCube);
      }
    }

    return retValue;
  }
}
