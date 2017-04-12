package espresso.minimizers.espressoMinimizer.utils;


public interface MatrixElementGenerator {

  boolean generateElement(int rowIndex, int columnIndex);

  int getRowCount();

  int getColumnCount();
}
