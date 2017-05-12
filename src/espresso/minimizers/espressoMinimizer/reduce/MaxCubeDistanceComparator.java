package espresso.minimizers.espressoMinimizer.reduce;


import espresso.boolFunction.cube.Cube;

import java.util.Comparator;
import java.util.Random;

public final class MaxCubeDistanceComparator implements Comparator<Cube> {

  private final Cube maxCube;

  public MaxCubeDistanceComparator(Cube maxCube) {
    this.maxCube = maxCube;
  }

  @Override
  public int compare(Cube o1, Cube o2) {
    Integer distance1 = pseudoDistanceToMaxCube(o1);
    Integer distance2 = pseudoDistanceToMaxCube(o2);

    int comparisonResult = distance1.compareTo(distance2);

    if (comparisonResult == 0) {
      Random random = new Random();
//      Return random -1, 0, 1
      return random.nextInt(3) - 1;
    } else {
      return comparisonResult;
    }
  }

  private int pseudoDistanceToMaxCube(Cube other) {
    if (isInputOutputLengthDifferent(maxCube, other)) {
      throw new IllegalArgumentException(
          "Incompatible max cube. Input and output lengths must be the same as other cubes."
      );
    }

    int retValue = 0;

    for (int i = 0; i < maxCube.inputLength(); i++) {
      if (maxCube.getInputState(i) != other.getInputState(i)) {
        retValue++;
      }
    }

    for (int i = 0; i < maxCube.outputLength(); i++) {
      if (maxCube.getOutputState(i) != other.getOutputState(i)) {
        retValue++;
      }
    }

    return retValue;
  }

  private boolean isInputOutputLengthDifferent(Cube one, Cube two) {
    return one.inputLength() != two.inputLength() || one.outputLength() != two.outputLength();
  }
}

