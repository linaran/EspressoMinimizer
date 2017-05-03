package espresso.boolFunction;

import espresso.boolFunction.cube.Cube;
import espresso.boolFunction.cube.CubeArray;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static espresso.boolFunction.InputState.*;
import static espresso.boolFunction.OutputState.OUTPUT;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CubeArrayTest {

  @Test
  public void addMethodShouldIncreaseBitColumnCount() {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, ONE}, new OutputState[]{OUTPUT});
    CubeArray cubes = new CubeArray(3, 1);
    cubes.add(cube1);

    assertTrue(
        "Number of ZERO bits in first column should be 1.",
        cubes.getZeroColumnCount(0) == 1
    );
    assertTrue(
        "Number of ONE bits in first column should be 0.",
        cubes.getOneColumnCount(0) == 0
    );
    assertTrue(
        "Number of both ZERO and ONE bits in the second column should be 0.",
        cubes.getOneColumnCount(1) == 0 && cubes.getZeroColumnCount(1) == 0
    );
  }

  @Test
  public void removeMethodShouldLowerBitColumnCount() {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, ONE}, new OutputState[]{OUTPUT});
    CubeArray cubes = new CubeArray(3, 1);
    cubes.add(cube1);
    cubes.remove(cube1);

    for (int i = 0; i < cubes.size(); ++i) {
      assertTrue(
          "Number of both ZERO and ONE bits anywhere should be 0.",
          cubes.getOneColumnCount(1) == 0 && cubes.getZeroColumnCount(1) == 0
      );
    }
  }

  @Test
  public void addAllShouldIncreaseBitColumnCount() {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{ONE, ONE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{DONTCARE, ONE, ONE}, new OutputState[]{OUTPUT});

    CubeArray cubes = new CubeArray(3, 1);
    cubes.addAll(Arrays.asList(cube1, cube2, cube3));

    int[] oneColumnCount = new int[cubes.getInputLength()];
    int[] zeroColumnCount = new int[cubes.getInputLength()];

    for (int i = 0; i < cubes.getInputLength(); i++) {
      oneColumnCount[i] = cubes.getOneColumnCount(i);
      zeroColumnCount[i] = cubes.getZeroColumnCount(i);
    }

    assertArrayEquals(
        "The amount of ZERO bits in each column is not correct.",
        new int[]{1, 0, 0},
        zeroColumnCount
    );

    assertArrayEquals(
        "The amount of ONE bits in each column is not correct.",
        new int[]{1, 2, 1},
        oneColumnCount
    );
  }

  @Test
  public void iteratorRemoveShouldLowerBitColumnCount() {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{ONE, ONE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{DONTCARE, ONE, ONE}, new OutputState[]{OUTPUT});

    CubeArray cubes = new CubeArray(3, 1);
    cubes.addAll(Arrays.asList(cube1, cube2, cube3));

    for (Iterator<Cube> iterator = cubes.iterator(); iterator.hasNext(); ) {
      iterator.next();
      iterator.remove();
    }

    for (int i = 0; i < cubes.getInputLength(); ++i) {
      assertEquals(
          "Number of ZERO bits should be 0 anywhere.",
          0,
          cubes.getZeroColumnCount(i)
      );
      assertEquals(
          "Number of ONE bits should be 0 anywhere.",
          0,
          cubes.getOneColumnCount(i)
      );
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void cubeArrayShouldNotAcceptIncompatibleCubes() throws Exception {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{ONE, ONE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{DONTCARE, ONE, ONE}, new OutputState[]{OUTPUT});

    CubeArray cubes = new CubeArray(3, 1);
    cubes.addAll(Arrays.asList(cube1, cube2, cube3));

    Cube wrongCube = new Cube(new InputState[]{ONE}, new OutputState[]{OUTPUT});
    cubes.add(wrongCube);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyCubeArrayShouldNotAcceptAnyCubeIteratorCase() throws Exception {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{ONE, ONE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{DONTCARE, ONE, ONE}, new OutputState[]{OUTPUT});

    CubeArray cubes = new CubeArray(3, 1);
    cubes.addAll(Arrays.asList(cube1, cube2, cube3));

    for (Iterator<Cube> iterator = cubes.iterator(); iterator.hasNext(); ) {
      iterator.next();
      iterator.remove();
    }

    assertTrue(
        "Cube wasn't empty.",
        cubes.size() == 0
    );

    Cube wrongCube = new Cube(new InputState[]{ONE}, new OutputState[]{OUTPUT});
    cubes.add(wrongCube);
  }

  @Test(expected = IllegalArgumentException.class)
  public void emptyCubeArrayShouldNotAcceptAnyCubeRemoveCase() throws Exception {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    CubeArray cubes = new CubeArray(3, 1);

    cubes.add(cube1);
    cubes.remove(cube1);

    assertTrue(
        "Cube wasn't empty.",
        cubes.size() == 0
    );

    Cube wrongCube = new Cube(new InputState[]{ONE}, new OutputState[]{OUTPUT});
    cubes.add(wrongCube);
  }

  @Test
  public void setMethodShouldAdjustCountersProperly() throws Exception {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    CubeArray cubes = new CubeArray(3, 1);
    cubes.add(cube1);

    Cube cube2 = new Cube(new InputState[]{ONE, ONE, ONE}, new OutputState[]{OUTPUT});

    assertEquals(
        "Counter maintenance is broken.",
        1,
        cubes.getZeroColumnCount(0)
    );
    assertEquals(
        "Counter maintenance is broken.",
        0,
        cubes.getOneColumnCount(0)
    );

    cubes.set(0, cube2);

    assertEquals(
        "Set method doesn't adjust counters properly.",
        0,
        cubes.getZeroColumnCount(0)
    );
    assertEquals(
        "Set method doesn't adjust counters properly.",
        1,
        cubes.getOneColumnCount(0)
    );
  }
}