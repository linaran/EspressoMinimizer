package espresso.minimizers.espressoMinimizer.expand;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.OutputState;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.espressoMinimizer.utils.BooleanMatrix;
import org.junit.Test;

import java.util.Iterator;

import static espresso.boolFunction.InputState.ONE;
import static espresso.boolFunction.InputState.ZERO;
import static espresso.boolFunction.OutputState.OUTPUT;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;


public class BooleanMatrixTest {

  @Test
  public void columnIteratorWithoutIgnoreTest() throws Exception {
    Cover cover = new Cover("testCase1.txt");
    BooleanMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(0));

    int[] actual = new int[matrix.getColumnCount()];
    int[] expected = new int[]{0, 1, 2, 3};

    for (Iterator<Integer> iter = matrix.ignoreColumnsIterator(); iter.hasNext(); ) {
      int i = iter.next();

      actual[i] = i;
    }

    assertArrayEquals(
        "Arrays are not equal.",
        expected,
        actual
    );
  }

  @Test
  public void columnIteratorWithIgnoreTest() throws Exception {
    Cover cover = new Cover("testCase1.txt");
    BooleanMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(0));
    matrix.addIgnoredColumns(1, 3);

    int[] actual = new int[matrix.getColumnCount()];
    int[] expected = new int[]{0, 0, 2, 0};

    for (Iterator<Integer> iter = matrix.ignoreColumnsIterator(); iter.hasNext(); ) {
      int i = iter.next();

      actual[i] = i;
    }

    assertArrayEquals(
        "Arrays are not equal.",
        expected,
        actual
    );
  }

  @Test
  public void rowIteratorWithoutIgnoreTest() throws Exception {
    Cover cover = new Cover("testCase1.txt");
    BooleanMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(0));

    int[] actual = new int[matrix.getRowCount()];
    int[] expected = new int[]{0, 1, 2, 3, 4, 5};

    for (Iterator<Integer> iter = matrix.ignoreRowsIterator(); iter.hasNext(); ) {
      int i = iter.next();

      actual[i] = i;
    }

    assertArrayEquals(
        "Arrays are not equal.",
        expected,
        actual
    );
  }

  @Test
  public void rowIteratorWithIgnoreTest() throws Exception {
    Cover cover = new Cover("testCase1.txt");
    BooleanMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(0));
    matrix.addIgnoredRows(1, 3);

    int[] actual = new int[matrix.getRowCount()];
    int[] expected = new int[]{0, 0, 2, 0, 4, 5};

    for (Iterator<Integer> iter = matrix.ignoreRowsIterator(); iter.hasNext(); ) {
      int i = iter.next();

      actual[i] = i;
    }

    assertArrayEquals(
        "Arrays are not equal.",
        expected,
        actual
    );
  }

  @Test
  public void getTrueColumnCountWithoutCountingIgnoredRows() throws Exception {
    Cover cover = new Cover("testCase3.txt");
    Cube cube = new Cube(new InputState[]{ZERO, ONE, ZERO, ONE}, new OutputState[]{OUTPUT});

    BooleanMatrix matrix = new SingleOutputCoverMatrix(cover, cube);
    matrix.addIgnoredRows(0, 4);

    int expected = 3;
    int actual = matrix.getTrueColumnCount(1, false);

    assertEquals(
        "Value count is incorrect.",
        expected,
        actual
    );
  }

  @Test
  public void getTrueColumnCountWithCountingIgnoredRows() throws Exception {
    Cover cover = new Cover("testCase3.txt");
    Cube cube = new Cube(new InputState[]{ZERO, ONE, ZERO, ONE}, new OutputState[]{OUTPUT});

    BooleanMatrix matrix = new SingleOutputCoverMatrix(cover, cube);
    matrix.addIgnoredRows(0, 4);

    int expected = 4;
    int actual = matrix.getTrueColumnCount(1, true);

    assertEquals(
        "Value count is incorrect.",
        expected,
        actual
    );
  }

  @Test
  public void getFalseColumnCountWithoutCountingIgnoredRows() throws Exception {
    Cover cover = new Cover("testCase3.txt");
    Cube cube = new Cube(new InputState[]{ZERO, ONE, ZERO, ONE}, new OutputState[]{OUTPUT});

    BooleanMatrix matrix = new SingleOutputCoverMatrix(cover, cube);
    matrix.addIgnoredRows(0, 4);

    int expected = 5;
    int actual = matrix.getFalseColumnCount(1, false);

    assertEquals(
        "Value count is incorrect.",
        expected,
        actual
    );
  }

  @Test(expected = IndexOutOfBoundsException.class)
  public void outOfRangeIndexForAddingIgnoringElementsShouldThrowException() throws Exception {
    Cover cover = new Cover("testCase1.txt");
    Cube cube = new Cube(new InputState[]{ZERO, ONE, ZERO, ONE}, new OutputState[]{OUTPUT});

    BooleanMatrix matrix = new SingleOutputCoverMatrix(cover, cube);
    matrix.addIgnoredRows(100);
  }
}