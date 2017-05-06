package espresso.boolFunction;

import espresso.boolFunction.cube.Cube;
import org.junit.Test;

import static espresso.boolFunction.InputState.*;
import static espresso.boolFunction.OutputState.*;
import static org.junit.Assert.*;

public class CubeTest {

  @Test
  public void complement() throws Exception {
    Cube cube = new Cube(new InputState[]{ONE, ZERO, DONTCARE}, new OutputState[]{OUTPUT, NOT_OUTPUT});
    Cover actualComplement = cube.complement();
    Cover expectedComplement = new Cover(
        new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT, NOT_OUTPUT}),
        new Cube(new InputState[]{DONTCARE, ONE, DONTCARE}, new OutputState[]{OUTPUT, NOT_OUTPUT})
    );

    assertTrue(
        "Size of the complement is wrong.",
        actualComplement.size() == expectedComplement.size()
    );
    assertTrue(
        "Complementing first cube is apparently not working.",
        actualComplement.get(0).equals(expectedComplement.get(0))
    );
    assertTrue(
        "Complementing first cube is apparently not working.",
        actualComplement.get(1).equals(expectedComplement.get(1))
    );
  }

  @Test
  public void inputPartIsTautology() throws Exception {
    Cube tautologyInput = new Cube(new InputState[]{DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    assertTrue(
        "Algorithm said input part isn't tautology which isn't correct.",
        tautologyInput.isInputPartTautology()
    );

    Cube notTautologyInput = new Cube(new InputState[]{ONE, DONTCARE, ZERO}, new OutputState[]{OUTPUT});
    assertTrue(
        "Algorithm said input part is tautology which isn't correct.",
        tautologyInput.isInputPartTautology()
    );
  }

  @Test
  public void InputStateCountTest() throws Exception {
    Cube cube = new Cube(new InputState[]{ONE, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT, NOT_OUTPUT});

    assertEquals(
        "Incorrect ONE count.",
        1,
        cube.oneCount()
    );

    assertEquals(
        "Incorrect ZERO count.",
        0,
        cube.zeroCount()
    );

    assertEquals(
        "Incorrect DONTCARE count.",
        2,
        cube.dontcareCount()
    );
  }

  @Test
  public void unionTest() throws Exception {
    Cube cube1 = new Cube(new InputState[]{ZERO, ONE, DONTCARE, ONE}, new OutputState[]{NOT_OUTPUT, OUTPUT, NOT_OUTPUT});
    Cube cube2 = new Cube(new InputState[]{ONE, ONE, DONTCARE, ZERO}, new OutputState[]{OUTPUT, NOT_OUTPUT, OUTPUT});

    Cube expected = new Cube(new InputState[]{DONTCARE, ONE, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT, OUTPUT, OUTPUT});
    Cube actual = cube1.smallestCubeContainingBoth(cube2);

    assertEquals(
        "Union calculation for cubes is wrong.",
        expected,
        actual
    );
  }
}