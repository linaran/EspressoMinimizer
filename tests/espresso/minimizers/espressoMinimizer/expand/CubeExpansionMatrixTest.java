package espresso.minimizers.espressoMinimizer.expand;

import espresso.boolFunction.Cover;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertArrayEquals;


public class CubeExpansionMatrixTest {

  @Test
  public void columnIteratorWithoutIgnoreTest() throws Exception {
    Cover cover = new Cover("testCase1.txt");
    CubeExpansionMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(0));

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
    CubeExpansionMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(0));
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
    CubeExpansionMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(0));

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
    CubeExpansionMatrix matrix = new SingleOutputCoverMatrix(cover, cover.get(0));
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
}