package espresso.minimizers.espressoMinimizer.reduce;


import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.OutputState;
import espresso.boolFunction.cube.Cube;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Reduce {

  public static Cover reduce(Cover onSet, Cover dontcareSet) {
    Cover retValue = new Cover(onSet);
    List<Cube> reducedCubes = new ArrayList<>();
    List<Integer> reducedCubesIndexes = new ArrayList<>();

    ReduceCubeOrder.getInstance().organize(retValue);

    for (int i = 0; i < retValue.size(); i++) {
      Cube cube = retValue.get(i);

      Cover copy = new Cover(retValue);
      copy.remove(cube);

      Cover mintermsOfCube = copy.union(dontcareSet).intersect(Cover.of(cube.copy()));

      if (mintermsOfCube.size() != 0) {
        Cube smallestCubeContainingComplement = smallestCubeContainingComplement(
            mintermsOfCube.cofactor(cube)
        );
        Cube reducedCube = cube.and(smallestCubeContainingComplement);

        retValue.remove(cube);
        retValue.add(reducedCube);
//        reducedCubes.add(reducedCube);
//        reducedCubesIndexes.add(i);
      }
    }

//    applyReducedCubes(reducedCubes, reducedCubesIndexes, retValue);

    return retValue;
  }

  public static void applyReducedCubes(
      List<Cube> reducedCubes,
      List<Integer> reducedCubesIndexes,
      Cover cover
  ) {
    if (reducedCubes.size() != reducedCubesIndexes.size()) {
      throw new IllegalArgumentException(
          "Lists reducedCubes and reducedCubesIndexes need to have same size."
      );
    }

    for (int i = 0; i < reducedCubes.size(); i++) {
      Cube reducedCube = reducedCubes.get(i);
      int cubeIndex = reducedCubesIndexes.get(i);

      if (reducedCube != null) {
        cover.set(cubeIndex, reducedCube);
      }
    }
  }

  public static Cube smallestCubeContainingComplement(Cover cover) {
    if (cover.isUnate()) {
      return smallestCubeContainingComplementUnateCase(cover);
    }

    int splitIndex = cover.binateSelect();
    Cover[] covers = cover.shannonCofactors(splitIndex);

    Cube positiveCube = smallestCubeContainingComplement(covers[1]);
    Cube negativeCube = smallestCubeContainingComplement(covers[0]);

    Cube variableCube = cover.generateVariableCube(splitIndex);
    Cube complementVariableCube = variableCube.copy().inputComplement();

    Cube positivePart = variableCube.and(positiveCube);
    Cube negativePart = complementVariableCube.and(negativeCube);

    return positiveCube.smallestCubeContainingBoth(negativeCube);
  }

  public static Cube smallestCubeContainingComplementUnateCase(Cover cover) {
    if (!cover.isUnate()) {
      throw new IllegalArgumentException("Given cover must be unate!");
    }

    InputState[] inputStates = new InputState[cover.inputCount()];
    OutputState[] outputStates = new OutputState[cover.outputCount()];

    for (int i = 0; i < cover.inputCount(); i++) {
      Set<Integer> outputTags = new HashSet<>();
      Set<Integer> complementedOutputTags = new HashSet<>();

      Cube variableCube = cover.generateVariableCube(i);
      Cube complementVariableCube = variableCube.copy().inputComplement();

      for (Cube cube : cover) {
        if (cube.generalContain(variableCube)) {
          for (int j = 0; j < cover.outputCount(); j++) {
            if (cube.getOutputState(j) == OutputState.OUTPUT) {
              outputTags.add(j);
            }
          }
        }

        if (cube.generalContain(complementVariableCube)) {
          for (int j = 0; j < cover.outputCount(); j++) {
            if (cube.getOutputState(j) == OutputState.OUTPUT) {
              complementedOutputTags.add(j);
            }
          }
        }
      }

      if (outputTags.size() == cover.outputCount()) {
        inputStates[i] = InputState.ZERO;
      } else if (complementedOutputTags.size() == cover.outputCount()) {
        inputStates[i] = InputState.ONE;
      } else {
        inputStates[i] = InputState.DONTCARE;
      }
    }

    for (int i = 0; i < cover.outputCount(); i++) {
      Cube tautologyForOutput = cover.generateTautologyForOutput(i);

      for (Cube cube : cover) {
        if (tautologyForOutput.getInputState().equals(cube.getInputState()) &&
            cube.getOutputState(i) == OutputState.OUTPUT) {
          outputStates[i] = OutputState.NOT_OUTPUT;
          break;
        }
      }

      if (outputStates[i] == null) {
        outputStates[i] = OutputState.OUTPUT;
      }
    }

    return new Cube(inputStates, outputStates);
  }

}
