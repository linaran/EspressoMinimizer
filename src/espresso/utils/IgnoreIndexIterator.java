package espresso.utils;


import java.util.Iterator;
import java.util.Set;


public class IgnoreIndexIterator implements Iterator<Integer> {
  private Integer currentIndex = -1;
  private Set<Integer> ignoredIndexes;
  private int upperLimit;

  public IgnoreIndexIterator(int upperLimit, Set<Integer> ignoredIndexes) {
    this.upperLimit = upperLimit;
    this.ignoredIndexes = ignoredIndexes;
  }

  @Override
  public boolean hasNext() {
    for (int i = currentIndex + 1; i < upperLimit; i++) {
      if (!ignoredIndexes.contains(i)) {
        currentIndex = i;
        return true;
      }
    }
    return false;
  }

  @Override
  public Integer next() {
    return currentIndex;
  }
}
