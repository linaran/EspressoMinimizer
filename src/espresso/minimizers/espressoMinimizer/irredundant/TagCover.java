package espresso.minimizers.espressoMinimizer.irredundant;


import espresso.boolFunction.Cover;
import espresso.boolFunction.cube.Cube;

import java.util.Collection;

/**
 * Several contracts are enforced here.
 * This {@link TagCover} must contain {@link TagCube}s only.
 * Every {@link TagCube} must have a tag.
 * Tags are propagated through the cofactor and shannonCofactors operation.
 * <p>
 * Warning: Other operations like {@link Cover#intersect(Cover)} may render
 * the tag system useless. The tag system exists only to monitor cube behaviour
 * during the cofactor operations.
 */
public class TagCover extends Cover {

  public TagCover(int inputCount, int outputCount) {
    super(inputCount, outputCount);
  }

  public TagCover(Cover cover) {
    super(cover.inputCount(), cover.outputCount());
    for (int i = 0; i < cover.size(); i++) {
      super.add(new TagCube(cover.get(i), i));
    }
  }

  @Override
  public void add(Cube cube) {
    if (cube instanceof TagCube) {
      super.add(cube);
    } else {
      super.add(new TagCube(cube, size()));
    }
  }

  @Override
  public void addAll(Cover cover) {
    for (Cube cube : cover) {
      super.add(new TagCube(cube, size()));
    }
  }

  @Override
  public void addAll(Collection<? extends Cube> c) {
    for (Cube cube : c) {
      super.add(new TagCube(cube, size()));
    }
  }

  @Override
  public TagCube get(int index) {
    return (TagCube) super.get(index);
  }

  @Override
  public TagCover cofactor(Cube other) {
    checkCoverCompatibility(Cover.of(other));
    TagCover retValue = new TagCover(inputCount(), outputCount());

    for (Cube cube : this) {
      Cube cubeCofactor = cube.cofactor(other);
      if (cubeCofactor != null) {
        retValue.add(cubeCofactor);
      }
    }

    return retValue;
  }

  @Override
  public TagCover[] shannonCofactors(int splitIndex) {
    if (size() == 0) {
      throw new UnsupportedOperationException("Cube is empty!");
    }

    Cube splitCube = generateVariableCube(splitIndex);
    Cube complement = new Cube(splitCube).inputComplement();

    TagCover[] retValue = new TagCover[2];
    retValue[0] = cofactor(complement);
    retValue[1] = cofactor(splitCube);

    return retValue;
  }
}
