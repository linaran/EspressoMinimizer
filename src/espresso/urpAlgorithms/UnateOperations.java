package espresso.urpAlgorithms;

import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;

import static espresso.boolFunction.InputState.DONTCARE;
import static espresso.boolFunction.InputState.ONE;

final public class UnateOperations {
  private static int inputCount = 0;
  private static int outputCount = 0;

  private UnateOperations() {
  }

  public static Cover unateComplement(Cover cover) {
    if (!cover.isUnate())
      throw new UnsupportedOperationException("Can't perform unate complement on non unate covers.");

    inputCount = cover.inputCount();
    outputCount = cover.outputCount();

    return recursiveUnateComplement(cover);
  }

  private static Cover recursiveUnateComplement(Cover cover) {
    //region Debug
//    System.out.println("Current cover:");
//    System.out.println(cover);
    //endregion
    Cover retValue = specialCase(cover);

    if (retValue != null) {
      return retValue;
    }

    int splitIndex = unateComplementBinateSelect(cover);
    Cube splittingCube = new Cube(inputCount, outputCount);
    splittingCube.setInput(ONE, splitIndex);
    Cover[] cofactors = cover.shannonCofactors(splitIndex);

    //region Debug
//    System.out.println("Splitting cubes:");
//    System.out.println(splittingCube.copy().inputComplement());
//    System.out.println(splittingCube);
//    System.out.println("\nCofactors:");
//    System.out.println(cofactors[0]);
//    System.out.println(cofactors[1]);
//    System.out.println("--------------------------------------------------");
    //endregion

    Cover left;
    Cover right;

    if (cover.getZeroColumnCount(splitIndex) == 0) {
      left = recursiveUnateComplement(cofactors[1]);
      right = splittingCube.complement().intersect(recursiveUnateComplement(cofactors[0]));
    } else if (cover.getOneColumnCount(splitIndex) == 0) {
      left = Cover.of(splittingCube).intersect(recursiveUnateComplement(cofactors[1]));
      right = recursiveUnateComplement(cofactors[0]);
    } else {
      throw new UnsupportedOperationException("Call the poor programmer.");
    }

    return left.union(right);
  }

  private static Cover specialCase(Cover cover) {
    Cover retValue = new Cover(cover.inputCount(), cover.outputCount());

//    Unate cover with don't care rows are tautologies.
    if (cover.hasDONTCARERow()) {
      return retValue;
    }

//    Function is empty so the complement is first tautology.
    if (cover.size() == 0) {
      retValue.add(new Cube(inputCount, outputCount));
      return retValue;
    }

//    Only one cube so complement is calculated using De Morgan laws.
    if (cover.size() == 1) {
      return cover.get(0).complement();
    }

//    Not first special case.
    return null;
  }

  /**
   * Method forced by the algorithm for complementation of unate boolean functions.
   *
   * @param cover {@link Cover}
   * @return int
   */
  public static int unateComplementBinateSelect(Cover cover) {
    Cube cube = largestCube(cover);
    int maxSum = 0;
    int splitIndex = -1;

    for (int i = 0; i < cube.inputLength(); i++) {
      if (cube.input(i) == DONTCARE) continue;

      if (cover.getOneColumnCount(i) + cover.getZeroColumnCount(i) > maxSum) {
        maxSum = cover.getOneColumnCount(i) + cover.getZeroColumnCount(i);
        splitIndex = i;
      }
    }

    return splitIndex;
  }

  private static Cube largestCube(Cover cover) {
    Cube retValue = null;
    int maxCount = 0;

    for (Cube cube : cover) {
      if (cube.dontcareCount() > maxCount)
        retValue = cube;
    }

    return retValue;
  }
}
