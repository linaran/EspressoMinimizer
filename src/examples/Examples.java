package examples;

import espresso.boolFunction.Cover;
import espresso.boolFunction.InputState;
import espresso.boolFunction.OutputState;
import espresso.boolFunction.cube.Cube;
import espresso.minimizers.Simplify;
import espresso.minimizers.espressoMinimizer.SingleOutputEspressoMinimizer;
import espresso.minimizers.espressoMinimizer.expand.Expand;
import espresso.minimizers.espressoMinimizer.irredundant.Irredundant;
import espresso.minimizers.espressoMinimizer.reduce.Reduce;
import espresso.urpAlgorithms.Complement;
import espresso.urpAlgorithms.Tautology;
import espresso.urpAlgorithms.UnateOperations;
import espresso.utils.Pair;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static espresso.boolFunction.InputState.*;
import static espresso.boolFunction.OutputState.NOT_OUTPUT;
import static espresso.boolFunction.OutputState.OUTPUT;

/**
 * Class for interactive testing java specific stuff.
 * -- I know I should use unit tests. Will read upon that later.
 */
public class Examples {
  public static void main(String[] args) throws IOException {
    wildMinimizationTest();
  }

  public static void hardcoreComplement() throws IOException {
    Cube cube1 = new Cube(new InputState[]{DONTCARE, ONE, ONE, ONE, ONE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{DONTCARE, ONE, ONE, ONE, ZERO}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{DONTCARE, ONE, ZERO, ONE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube4 = new Cube(new InputState[]{DONTCARE, ONE, ZERO, DONTCARE, ONE}, new OutputState[]{OUTPUT});

    Cover cover = new Cover(cube1, cube2, cube3, cube4);

//    cover = new Cover("testCase1.txt");
    Cover coverComplement = Complement.complement(cover, new Cover(cover.inputCount(), cover.outputCount()));

    System.out.println("Cover:");
    System.out.println(cover);

    System.out.println("Complement:");
    System.out.println(coverComplement);

    Cover doubleComplement;
    System.out.println("Double complement:");
    System.out.println(doubleComplement = Complement.complement(coverComplement, new Cover(cover.inputCount(), cover.outputCount())));

    System.out.println("Triple complement:");
    System.out.println(Complement.complement(doubleComplement, new Cover(cover.inputCount(), cover.outputCount())));

    System.out.println("Intersection of cover and its complement:");
    System.out.println(cover.intersect(coverComplement));

    System.out.println("Complement of smallestCubeContainingBoth of the cover and its complement:");
    Cover taut = cover.union(coverComplement);
    System.out.println(Complement.singleOutputComplement(taut));

    System.out.println("Is tautology:");
    System.out.println(Tautology.singleOutputTautologyCheck(taut) + "\n");

    System.out.println("Taut cover:");
    System.out.println(taut);
  }

  public static void unateComplement() {
    Cube cube1 = new Cube(new InputState[]{ZERO, ONE, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{DONTCARE, ONE, DONTCARE, ONE}, new OutputState[]{OUTPUT});
    Cube cube4 = new Cube(new InputState[]{DONTCARE, ONE, ZERO, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube5 = new Cube(new InputState[]{DONTCARE, ONE, ZERO, ONE}, new OutputState[]{OUTPUT});

    Cover cover = new Cover(cube1, cube2, cube3, cube4, cube5);

    System.out.println(UnateOperations.unateComplement(cover) + "Jel dobro?");
//    System.out.println(Arrays.deepToString(cover.getCurrentColumnCount()));
  }

  public static void cofactorTest() {
    Cube cube1 = new Cube(new InputState[]{ONE, ONE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{ONE, DONTCARE, ONE}, new OutputState[]{OUTPUT});
    Cube cube4 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{ONE, ZERO, ZERO}, new OutputState[]{OUTPUT});

    Cube a = new Cube(new InputState[]{ONE, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube p = new Cube(new InputState[]{ONE, ONE, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT, NOT_OUTPUT});
    Cube x = new Cube(new InputState[]{DONTCARE, DONTCARE, DONTCARE, ONE}, new OutputState[]{OUTPUT, OUTPUT});
    Cube weird = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});

    Cover cover = new Cover(cube1, cube2, cube4, cube3);

    //region Collecting Shannon Decomposition
    Cover[] cofactors = cover.shannonCofactors(0);
    Cube AComplement = new Cube(a).inputComplement();
    System.out.println(cofactors[0]);
    System.out.println(cofactors[1]);

    Cover collect = Cover.of(a).intersect(cofactors[1]).union(Cover.of(AComplement).intersect(cofactors[0]));
    System.out.println(cover.toString());
    System.out.println(collect.toString());
    //endregion

//    System.out.println(cover);
//    System.out.println(cover.cofactor(new Cube(cover.inputCount(), cover.outputCount())));

//    System.out.println(cover.toString());
//    System.out.println(Cover.of(p).intersect(cover));
//    System.out.println(Cover.of(p).intersect(cover.cofactor(p)));
  }

  public static void simplifyTestSimple() {
    Cube cube1 = new Cube(new InputState[]{ZERO, DONTCARE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube2 = new Cube(new InputState[]{ONE, ONE, DONTCARE}, new OutputState[]{OUTPUT});
    Cube cube3 = new Cube(new InputState[]{DONTCARE, ONE, ONE}, new OutputState[]{OUTPUT});

    Cover cover = new Cover(cube1, cube2, cube3);

    Cover minCover = Simplify.getInstance().minimize(cover);

    System.out.println(cover.toString());
    System.out.println(minCover.toString());
  }

  public static void simplifyTestBig() throws IOException {
    Cover cover = new Cover("simplifyBigExample.txt");
    System.out.println(cover.size());
    System.out.println(cover);

    Cover min = Simplify.getInstance().minimize(cover);
    System.out.println(min.size());
    System.out.println(min);
  }

  public static void simplifyTestDebug() throws IOException {
    Cover cover = new Cover("testCase3.txt");
    System.out.println(cover.size());
    System.out.println(cover);

    Cover min = Simplify.getInstance().minimize(cover);
    System.out.println(min.size());
    System.out.println(min);
  }

  public static void miniTest() throws IOException {
    Cover cover = new Cover("miniFunction.txt");
    Cover complement = Complement.singleOutputComplement(cover);
    Cube cube = new Cube(new InputState[]{ZERO, ZERO, DONTCARE}, new OutputState[]{OUTPUT});
    cover.complement();

    System.out.println(cover);

    Pair<Cube, List<Integer>> pair = Expand.singleOutputCubeExpand(cube, cover, complement);

    System.out.println(pair.first);
    System.out.println(pair.second + "\n");

    System.out.println(Expand.expandCover(cover, complement));
  }

  public static void expandTest() throws IOException {
    Cover cover = new Cover("testCase3.txt");
    Cover complement = Complement.singleOutputComplement(cover);
    Cube cube = new Cube(new InputState[]{ZERO, ONE, ZERO, ONE}, new OutputState[]{OUTPUT});
    cover.complement();

    System.out.println(cover);

    Pair<Cube, List<Integer>> pair = Expand.singleOutputCubeExpand(cube, cover, complement);

    System.out.println(pair.first + "\n");
    System.out.println(pair.second);

    System.out.println(Expand.expandCover(cover, complement));
  }

  public static void espressoTest() throws IOException {
    Cover onSet = new Cover("testCase3.txt");
    Cover dontcareSet = new Cover(onSet.inputCount(), onSet.outputCount());

    Cover min = SingleOutputEspressoMinimizer.getInstance().minimize(onSet, dontcareSet);

    System.out.println(min);
  }

  public static void wildMinimizationTest() throws IOException {
    Cover onSet = new Cover("notMinimalTestCase1.txt");

    System.out.println(onSet);

    Cover minimized = SingleOutputEspressoMinimizer.getInstance().minimize(onSet);

    System.out.println(minimized);
  }

  public static void calculateMinSetsTest() throws IOException {
    Cover alpha = new Cover("alpha.txt");
    Cover beta = new Cover(alpha.inputCount(), alpha.outputCount());
    List<Integer> alphaTrack = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);
//    List<Integer> alphaTrack = Arrays.asList(4, 5, 6, 7, 8, 9);

    List<List<Integer>> minSets = Irredundant.calculateMinimalSets(alpha, beta, alphaTrack);
    for (List<Integer> set : minSets) {
      System.out.println(set);
    }
  }

  public static void alphaBetaTest() throws IOException {
    Cover onSet = new Cover("irredundantOnSetTestCase.txt");
    Cover dontcareSet = new Cover("irredundantDontcareTestCase.txt");

    System.out.println(Irredundant.irredundantCover(onSet, dontcareSet));
  }

  public static void SCCCTest() {
    Cover cover = new Cover(
        new Cube(new InputState[]{DONTCARE, ONE, DONTCARE}, new OutputState[]{OUTPUT, OUTPUT}),
        new Cube(new InputState[]{DONTCARE, DONTCARE, ONE}, new OutputState[]{NOT_OUTPUT, OUTPUT})
    );

    System.out.println(Reduce.smallestCubeContainingComplementUnateCase(cover));
  }
}
