package espresso;

/**
 * This class represents a logic implicant in a special way.
 * In digital design implicants are values that cover minterms.
 * Regarding that statement implicants can be represented like this: 000*<br/>
 * <br/>
 * "000*" would represent an implicant that covers minterms "0000" and "0001".
 * In this class 0 and 1 will be represented with boolean true and false values while a
 * don't care will be represented with a simple null.<br/>
 * <br/>
 * So when using {@link Implicant#getValue(int)} method, remember that getting a null is
 * likely not an error.
 */
public class Implicant {
  private Boolean[] values;

  /**
   * Creates an implicant initialized with don't care values.
   *
   * @param variableCount primitive int implicant length.
   */
  public Implicant(int variableCount) {
    values = new Boolean[variableCount];
  }

  /**
   * Creates an implicant from given boolean values.
   * Remember that null represents don't care value for this class.
   *
   * @param values {@link Boolean}[] values.
   */
  public Implicant(Boolean[] values) {
    this.values = values;
  }

  /**
   * Method returns implicant length.
   *
   * @return primitive int.
   */
  public int length() {
    return values.length;
  }

  /**
   * Sets a value for the implicant at the given index.
   * Remember null represents don't care for this class.
   *
   * @param index primitive int.
   * @param value Boolean true, false or null.
   */
  public void setValue(int index, Boolean value) {
    if (index < 0 || index >= values.length) {
      throw new IndexOutOfBoundsException("Index out of bounds for Implicant#setValue method.");
    }

    values[index] = value;
  }

  /**
   * Returns a value at the given index for this implicant.
   * Remember null represents don't care for this class.
   *
   * @param index primitive int.
   * @return {@link Boolean}.
   */
  public Boolean getValue(int index) {
    if (index < 0 || index >= values.length) {
      throw new IndexOutOfBoundsException("Index out of bounds for Implicant#getValue method.");
    }

    return values[index];
  }
}
