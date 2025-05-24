import java.io.*;
import java.util.*;

class MatrixEntry {
    int row, col, value;

    MatrixEntry(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }
}

class SparseMatrix {
    private int numRows, numCols;
    private List<MatrixEntry> elements;
    private static final String CURRENT_DIR = System.getProperty("user.dir");
    private static final String BASE_DIR = CURRENT_DIR + "/../../../../dsa/sparse_matrix/files";
    private static final String INPUT_DIR = BASE_DIR + "/sample_inputs";
    private static final String RESULT_DIR = BASE_DIR + "/results";

    public SparseMatrix(int rows, int cols) {
        this.numRows = rows;
        this.numCols = cols;
        this.elements = new ArrayList<>();
    }

    public static void createDirectoryStructure() throws IOException {
        File baseDir = new File(BASE_DIR);
        File inputDir = new File(INPUT_DIR);
        File resultDir = new File(RESULT_DIR);

        System.out.println("Current working directory: " + CURRENT_DIR);
        
        if (!baseDir.exists() && !baseDir.mkdirs()) {
            throw new IOException("Unable to create directory");
        }
        if (!inputDir.exists() && !inputDir.mkdirs()) {
            throw new IOException("Unable to create input directory");
        }
        if (!resultDir.exists() && !resultDir.mkdirs()) {
            throw new IOException("Unable to create result directory");
        }

        System.out.println("\n All directories are created:");
    }


    public int getElement(int row, int col) {
        for (MatrixEntry entry : elements) {
            if (entry.row == row && entry.col == col) {
                return entry.value;
            }
        }
        return 0;
    }

    public void setElement(int row, int col, int value) {
        for (int i = 0; i < elements.size(); i++) {
            MatrixEntry entry = elements.get(i);
            if (entry.row == row && entry.col == col) {
                if (value != 0) {
                    entry.value = value;
                } else {
                    elements.remove(i);
                }
                return;
            }
        }
        if (value != 0) {
            elements.add(new MatrixEntry(row, col, value));
        }
    }

    public static SparseMatrix fetchInputData(String filename) throws IOException {
        String fullPath = INPUT_DIR + "/" + filename;
        File file = new File(fullPath);
        
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + file.getAbsolutePath() + 
                                         "\nPlease place input files in: " + new File(INPUT_DIR).getAbsolutePath());
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            int rows = Integer.parseInt(line.substring(line.indexOf('=') + 1).trim());

            line = reader.readLine();
            int cols = Integer.parseInt(line.substring(line.indexOf('=') + 1).trim());

            SparseMatrix matrix = new SparseMatrix(rows, cols);

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                
                line = line.substring(1, line.length() - 1);
                String[] values = line.split(",");
                
                if (values.length != 3) {
                    throw new IOException("Each entry should have 3 values in the input file");
                }

                int row = Integer.parseInt(values[0].trim());
                int col = Integer.parseInt(values[1].trim());
                int value = Integer.parseInt(values[2].trim());

                if (row >= rows || col >= cols) {
                    throw new IOException("Mismatch in the input file "+ filename+ " row = "+ row + " col = "+ col + " rows = "+ rows + " cols = "+ cols);
                }

                matrix.setElement(row, col, value);
            }
            return matrix;
        }

    
    }

    public void saveToFile(String filename) throws IOException {
        String fullPath = RESULT_DIR + "/" + filename;
        File file = new File(fullPath);

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("rows=" + numRows);
            writer.println("cols=" + numCols);
            
            for (MatrixEntry entry : elements) {
                writer.printf("(%d, %d, %d)%n", entry.row, entry.col, entry.value);
            }
            System.out.println("Solution saved at: " + file.getAbsolutePath());
        }
    }

    public static SparseMatrix addition(SparseMatrix m1, SparseMatrix m2) {
        if (m1.numRows != m2.numRows || m1.numCols != m2.numCols) {
            throw new IllegalArgumentException("Unable to perform addition");
        }

        SparseMatrix result = new SparseMatrix(m1.numRows, m1.numCols);

        // Add elements from first matrix
        for (MatrixEntry entry : m1.elements) {
            result.setElement(entry.row, entry.col, entry.value);
        }

        // Add elements from second matrix
        for (MatrixEntry entry : m2.elements) {
            int currentVal = result.getElement(entry.row, entry.col);
            result.setElement(entry.row, entry.col, currentVal + entry.value);
        }

        return result;
    }

    public static SparseMatrix subtraction(SparseMatrix m1, SparseMatrix m2) {
        if (m1.numRows != m2.numRows || m1.numCols != m2.numCols) {
            throw new IllegalArgumentException("unable to perform subtraction");
        }

        SparseMatrix result = new SparseMatrix(m1.numRows, m1.numCols);

        // Add elements from first matrix
        for (MatrixEntry entry : m1.elements) {
            result.setElement(entry.row, entry.col, entry.value);
        }

        // Subtract elements from second matrix
        for (MatrixEntry entry : m2.elements) {
            int currentVal = result.getElement(entry.row, entry.col);
            result.setElement(entry.row, entry.col, currentVal - entry.value);
        }

        return result;
    }

    public static SparseMatrix multiplication(SparseMatrix m1, SparseMatrix m2) {
        if (m1.numCols != m2.numRows) {
            throw new IllegalArgumentException("Matrix dimensions don't match for multiplication");
        }

        SparseMatrix result = new SparseMatrix(m1.numRows, m2.numCols);

        for (MatrixEntry entry1 : m1.elements) {
            for (MatrixEntry entry2 : m2.elements) {
                if (entry1.col == entry2.row) {
                    int row = entry1.row;
                    int col = entry2.col;
                    int currentVal = result.getElement(row, col);
                    result.setElement(row, col, currentVal + entry1.value * entry2.value);
                }
            }
        }

        return result;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            SparseMatrix.createDirectoryStructure();

            while (true) {
                try {
                    System.out.println("\nSparse Matrix Program");
                    System.out.println("1. Addition");
                    System.out.println("2. Subtraction");
                    System.out.println("3. Multiplication");
                    System.out.println("4. Exit");
                    System.out.print("Enter choice (1-4): ");

                    int choice = scanner.nextInt();
                    scanner.nextLine();

                    if (choice == 4) {
                        System.out.println("Exiting program...");
                        break;
                    }

                    System.out.print("Enter the 1st filename (input_1.tx): ");
                    String filename1 = scanner.nextLine();
                    System.out.print("Enter the 2nd filename (input_2.txt): ");
                    String filename2 = scanner.nextLine();

                    SparseMatrix m1 = SparseMatrix.fetchInputData(filename1);
                    SparseMatrix m2 = SparseMatrix.fetchInputData(filename2);

                    SparseMatrix result;
                    switch (choice) {
                        case 1:
                            result = SparseMatrix.addition(m1, m2);
                            break;
                        case 2:
                            result = SparseMatrix.subtraction(m1, m2);
                            break;
                        case 3:
                            result = SparseMatrix.multiplication(m1, m2);
                            System.out.println("Matrix multiplication result:"+ result.toString());
                            break;
                        default:
                            System.out.println("Try again please");
                            continue;
                    }

                    result.saveToFile("result.txt");

                } catch (Exception e) {
                    System.out.println("Exception error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Error creating directory structure: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}