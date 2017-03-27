package espresso.urpAlgorithms;

import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;

import static espresso.boolFunction.InputState.DONTCARE;

final public class Tautology {

  public static boolean singleOutputTautologyCheck(Cover cover) {
    Boolean specialCaseCheck = specialCase(cover);
    if (specialCaseCheck != null) {
      return specialCaseCheck;
    }

    int splitIndex = cover.binateSelect();
    Cover[] cofactors = cover.shannonCofactors(splitIndex);

    if (!singleOutputTautologyCheck(cofactors[0])) {
      return false;
    }

    if (!singleOutputTautologyCheck(cofactors[1])) {
      return true;
    }

    return true;
  }

  /**
   * Returns null if special case didn't occur.
   *
   * @param cover link{@link Cover}
   * @return {@link Boolean}, true if it is tautology, false if it isn't
   * null if it couldn't tell.
   */
  private static Boolean specialCase(Cover cover) {
    if (cover.hasDONTCARERow()) {
      return true;
    }

    for (int i = 0; i < cover.inputCount(); ++i) {
      if (cover.getZeroColumnCount(i) == cover.size()) {
        return false;
      }
      if (cover.getOneColumnCount(i) == cover.size()) {
        return false;
      }
    }

    double mintermCount = mintermCountUpperBound(cover);
    if (mintermCount < Math.pow(2, cover.inputCount())) {
      return false;
    }

    if (cover.size() <= 7) {
//      TODO: Truth table check.
      return null;
    }

    return null;
  }

  private static double mintermCountUpperBound(Cover cover) {
    double retValue = 0;

    for (Cube cube : cover) {
      int dontcareCount = 0;

      for (int i = 0; i < cube.inputLength(); ++i) {
        if (cube.getInputState(i) == DONTCARE) {
          ++dontcareCount;
        }
      }

      retValue += Math.pow(2, dontcareCount);
    }

    return retValue;
  }
}
