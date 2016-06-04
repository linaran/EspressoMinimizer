package espresso;

import espresso.lockEnviroment.Cover;
import espresso.lockEnviroment.Cube;

import static espresso.InputState.DONTCARE;
import static espresso.InputState.ONE;

/**
 * Document later. Tired now.
 */
public class UnateOperations {
  private static int inputCount = 0;
  private static int outputCount = 0;
  private static int splitIndex = 0;

  public UnateOperations() {
    throw new UnsupportedOperationException("nope..");
  }

  public static Cover unateComplement(Cover cover) {
    if (!cover.isUnate())
      throw new UnsupportedOperationException("Can't perform unate complement on non unate covers.");

    inputCount = cover.inputCount();
    outputCount = cover.outputCount();

    return recursiveUnateComplement(cover);
  }

  private static Cover recursiveUnateComplement(Cover cover) {
//    System.out.println("Current cover:");
//    System.out.println(cover);
    Cover retValue = specialCase(cover);

    if (retValue != null) {
      return retValue;
    }

    Cube splittingCube = splittingVariable(cover);
    Cover[] cofactors = cover.shannonCofactors(splittingCube);

//    System.out.println("Splitting cubes:");
//    System.out.println(splittingCube.copy().complement());
//    System.out.println(splittingCube);
//    System.out.println("\nCofactors:");
//    System.out.println(cofactors[0]);
//    System.out.println(cofactors[1]);
//    System.out.println("--------------------------------------------------");

    Cover left;
    Cover right;

    if (cover.getZeroColumnCount(splitIndex - 1) == 0) {
      left = recursiveUnateComplement(cofactors[1]);
      right = Cover.of(splittingCube.copy().complement()).intersect(recursiveUnateComplement(cofactors[0]));
    } else if (cover.getOneColumnCount(splitIndex - 1) == 0){
      left = Cover.of(splittingCube).intersect(recursiveUnateComplement(cofactors[1]));
      right = recursiveUnateComplement(cofactors[0]);
    } else {
      throw new UnsupportedOperationException("Crap");
    }

    return left.union(right);
  }

  private static Cover specialCase(Cover cover) {
    Cover retValue = new Cover();
    if (cover.size() == 0) {
      retValue.add(new Cube(inputCount, outputCount));
      return retValue;
    }

    if (isTautology(cover)) {
      return retValue;
    }

    if (cover.size() == 1) {
      retValue.add(cover.iterator().next().copy().complement());
      return retValue;
    }

    return null;
  }

  private static boolean isTautology(Cover cover) {
    for (Cube cube : cover) {
      for (int i = 0; i < cube.inputLength(); i++) {
        if (cube.input(i) != DONTCARE)
          break;
        if (i + 1 == cube.inputLength())
          return true;
      }
    }
    return false;
  }

  private static Cube splittingVariable(Cover cover) {
//    Cube cube = largestCube(cover);
//    int[][] columnCount = cover.getCurrentColumnCount();
//    int maxSum = 0;
//    int maxIndex = -1;
//
//    for (int i = 0; i < cube.inputLength(); i++) {
//      if (columnCount[0][i] + columnCount[1][i] > maxSum) {
//        maxIndex = i;
//      }
//    }
//
    Cube retValue = new Cube(inputCount, outputCount);
    retValue.setInput(ONE, splitIndex++);
    return retValue;
  }

  private static Cube largestCube(Cover cover) {
    Cube retValue = null;
    int maxCount = 0;

    for (Cube cube : cover) {
      if (dontcareCount(cube) > maxCount)
        retValue = cube;
    }

    return retValue;
  }

  private static int dontcareCount(Cube cube) {
    int retValue = 0;
    for (int i = 0; i < cube.inputLength(); i++) {
      if (cube.input(i) == DONTCARE)
        retValue++;
    }
    return retValue;
  }
}
