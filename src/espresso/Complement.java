package espresso;

import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.CoverUtility;

import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;

/**
 * Complement
 */
public class Complement {
  private static int fInputCount = 0;
  private static int fOutputCount = 0;

  public static Cover complement(Cover f, Cover d) {
    if (f.size() == 0 || d.size() == 0)
      throw new UnsupportedOperationException("One of the given covers are empty.");
    if (f.inputCount() != d.outputCount() || f.outputCount() != d.outputCount())
      throw new IllegalArgumentException("Given covers are not compatible. Input/output lengths are different");

    Cover retValue = new Cover();

    fInputCount = f.inputCount();
    fOutputCount = f.outputCount();

    for (int i = 0; i < fOutputCount; i++) {
      Cover singleOutputF = extract(f, i);
      Cover singleOutputD = extract(d, i);

      retValue.addAll(singleOutputComplement(singleOutputF.union(singleOutputD)));
    }

    return retValue;
  }

  public static Cover singleOutputComplement(Cover f) {
    if (f.outputCount() != 1) {
      throw new UnsupportedOperationException(
          "This function complements only single output functions."
      );
    }

    //region Special cases
    Cover retValue = new Cover();

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
            true)
    );

    return retValue;
  }

  public static Cover extract(Cover cover, int outputIndex) {
    Cover retValue = new Cover();

    for (Cube cube : cover) {
      Cube extraction = new Cube(fInputCount, 1); // Single output.

      for (int i = 0; i < fInputCount; i++) extraction.setInput(cube.input(i), i);
      extraction.setOutput(cube.output(outputIndex), 0);

      retValue.add(extraction);
    }

    return retValue;
  }

  private static Cover specialCase(Cover cover) {
    return null;
  }
}
