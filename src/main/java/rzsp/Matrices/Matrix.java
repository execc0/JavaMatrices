package rzsp.Matrices;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;


/**
 * Класс, в котором реализованы все операции для работы с матрицами
 * <p>
 * Реализованы методы сложения, вычитания, умножения, вычисления определителя, считывания матрицы из файла.
 */
public class Matrix {

    private static final Logger logger = LogManager.getLogger(Matrix.class);
    protected double[][] matrix;
    protected int rowsNum;
    protected int colsNum;

    /**
     * Конструктор класса, создает экземпляр класса
     *
     * @param matrix двумерный массив из чисел с плавающей точкой двойной точности (double)
     * @throws MatrixException в случае возникновения ошибки
     */
    public Matrix(double[][] matrix) throws MatrixException {
        if (isMatrix(matrix)) {
            this.matrix = matrix;
            this.rowsNum = matrix.length;
            this.colsNum = matrix[0].length;
            logger.info("Инициализирована матрица {}", (Object) this.matrix);
        }
    }

    /**
     * Метод для проверки, является ли переданный двумерный массив из double матрицей
     *
     * @param matrix двумерный массив из чисел с плавающей точкой двойной точности (double)
     * @throws MatrixException в случае возникновения исключения
     * @return возвращает true если не возникло исключений
     */
    private boolean isMatrix(double[][] matrix) throws MatrixException {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            throw new MatrixException("Матрица равна null или пуста");
        }
        int supposedCols = matrix[0].length;
        IntStream.range(0, matrix.length).forEach(i -> {
            if (matrix[i] == null) {
                throw new MatrixException("Одна из строк матрицы равна null");
            }
            DoubleStream intStream = Arrays.stream(matrix[i]);
            if (intStream.count() != supposedCols) {
                throw new MatrixException("Число элементов " + (i + 1) + " строки матрицы не равно числу элементов первой строки");
            }
        });
        return true;
    }
    /**
     * Метод, позволяющий создать новый экземпляр класса из файла
     *
     * @param path путь к текстовому файлу с матрицей
     * @return Новый экземпляр класса
     * @throws MatrixException в случае возникновения исключение
     */
    public static Matrix fromFile(String path) throws MatrixException {
        try {
            List<String> lines = Files.readAllLines(Path.of(path));
            ArrayList<String[]> rowsArray = new ArrayList<>();
            lines.forEach(line -> rowsArray.add(line.split(" ")));
            double[][] doubleMatrix = new double[rowsArray.size()][];
            IntStream.range(0, rowsArray.size()).forEach(i -> {
                double[] row = new double[rowsArray.get(i).length];
                IntStream.range(0, rowsArray.get(i).length).forEach(j -> {
                    try {
                        row[j] = Double.parseDouble(rowsArray.get(i)[j]);
                    } catch (NumberFormatException e) {
                        throw new MatrixException("Один из элементов введённой матрицы не является числом с плавающей точкой");
                    }
                });
                doubleMatrix[i] = row;
            });
            return new Matrix(doubleMatrix);
        } catch (IOException | NullPointerException e) {
            throw new MatrixException("Указан неверный путь к файлу: " + path);
        }
    }

    /**
     * Возвращает число строк матрицы
     */
    public int rowsNum() {
        return this.rowsNum;
    }

    /**
     * Возвращает число столбцов матрицы
     */
    public int colsNum() {
        return this.colsNum;
    }

    /**
     * Возвращает значение элемента матрицы по указанному индексу строки и столбца
     *
     * @param i индекс строки
     * @param j индекс столбца
     * @return значение элемента i-ой строки j-го столбца
     * @throws MatrixException в случае возникновения исключения
     */
    public double getVal(int i, int j) throws MatrixException {
        if (i < 0 || i >= rowsNum || j < 0 || j >= colsNum) {
            throw new MatrixException("Введены неверные индексы элемента");
        }
        return this.matrix[i][j];
    }

    /**
     * Меняет значение элемента матрицы по указанному индексу строки и столбца
     *
     * @param value новое значение элемента
     * @param i     индекс строки
     * @param j     индекс столбца
     * @throws MatrixException в случае возникновения исключения
     */
    public void changeVal(double value, int i, int j) throws MatrixException {
        if (i < 0 || i >= rowsNum || j < 0 || j >= colsNum) {
            throw new MatrixException("Введены неверные индексы элемента");
        }
        this.matrix[i][j] = value;
    }

    /**
     * Выводит матрицу в консоль
     */
    public void print() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat roundToTenDigits = new DecimalFormat("#0.##########", symbols);
        IntStream.range(0, this.rowsNum()).forEach(i -> {
            IntStream.range(0, this.colsNum()).forEach(j -> System.out.print(Double.parseDouble(roundToTenDigits.format(this.getVal(i, j)))+ " "));
            System.out.println();
        });
    }

    /**
     * Меняет i-ую и j-ую строки матрицы местами
     * @throws MatrixException в случае возникновения исключения
     */
    public void swapRows(int i, int j) throws MatrixException {
        if (i < 0 || i >= rowsNum || j < 0 || j >= rowsNum) {
            throw new MatrixException("Указаны неверные индексы строк");
        }
        double[] buf = this.matrix[i];
        this.matrix[i] = this.matrix[j];
        this.matrix[j] = buf;
    }
    /**
     * Проверяет, совпадают ли размеры двух матриц
     * @param matrix матрица, с которой выполняется сравнение
     * @return true если размеры совпадают, иначе false
     */
    public boolean isSameSize(Matrix matrix) {
        return (this.rowsNum() == matrix.rowsNum()) && (this.colsNum() == matrix.colsNum());
    }

    /**
     * Вычисляет сумму двух матриц
     *
     * @param matrix матрица, с которой будет выполняться сложение
     * @return Матрица, полученная в результате сложения
     * @throws MatrixException в случае возникновения исключения
     */
    public Matrix sum(Matrix matrix) throws MatrixException {
        if (!this.isSameSize(matrix)) throw new MatrixException("Размеры матриц при их сложении должны быть равны");
        double[][] newArray = new double[matrix.rowsNum()][matrix.colsNum()];
        Matrix resultMatrix = new Matrix(newArray);

        IntStream.range(0, this.rowsNum()).forEach(i ->
                IntStream.range(0, this.colsNum()).forEach(j ->
                        resultMatrix.changeVal(this.getVal(i, j) + matrix.getVal(i, j), i, j)));

        logger.info("Выполнено сложение матрицы {} с матрицей {} и получен результат {}",
                this.matrix, matrix.matrix, resultMatrix.matrix);
        return resultMatrix;
    }

    /**
     * Вычисляет разность двух матриц
     *
     * @param matrix матрица, которая будет вычтена
     * @return Матрица, полученная в результате вычитания
     * @throws MatrixException в случае возникновения исключения
     */
    public Matrix subtract(Matrix matrix) throws MatrixException {
        if (!this.isSameSize(matrix)) throw new MatrixException("Размеры матриц при их сложении должны быть равны");
        double[][] newArray = new double[matrix.rowsNum()][matrix.colsNum()];
        Matrix resultMatrix = new Matrix(newArray);

        IntStream.range(0, this.rowsNum()).forEach(i ->
                IntStream.range(0, this.colsNum()).forEach(j ->
                        resultMatrix.changeVal(this.getVal(i, j) - matrix.getVal(i, j), i, j)));

        logger.info("Из матрицы {} вычтена матрица {} и получен результат {}",
                this.matrix, matrix.matrix, resultMatrix.matrix);
        return resultMatrix;
    }

    /**
     * Умножает матрицу справа
     *
     * @param matrix матрица, на которую будет выполнено умножение (справа)
     * @return Матрица, полученная в результате умножения
     * @throws MatrixException в случае возникновения исключения
     */
    public Matrix multiply(Matrix matrix) throws MatrixException {
        if (this.colsNum() != matrix.rowsNum()) {
            throw new MatrixException("При умножении число столбцов первой матрицы должно быть равно числу строк второй");
        }
        double[][] resultArr = Arrays.stream(this.matrix)
                .map(row -> IntStream.range(0, matrix.colsNum())
                        .mapToDouble(i -> IntStream.range(0, matrix.rowsNum())
                                .mapToDouble(j -> row[j] * matrix.getVal(j, i)).sum())
                        .toArray())
                .toArray(double[][]::new);

        logger.info("Выполнено умножение матрицы {} на матрицу {}, получен результат {}", this.matrix, matrix.matrix, resultArr);
        return new Matrix(resultArr);
    }


    /**
     * Выполняет прямой ход метода Гаусса
     *
     * @return Пара значений, первое - матрица приведённая к верхнетреугольному виду,
     * второе - Integer равный 1 или -1 (определяет знак определителя)
     * @see #determinant()
     * @throws MatrixException в случае возникновения исключения
     */
    private Pair<Matrix, Integer> gaussElimination() throws MatrixException{
        if (this.colsNum() != this.rowsNum()) {
            throw new MatrixException("Вычисление определителя возможно только для квадратной матрицы");
        }
        double[][] copyArray = Arrays.stream(matrix).map(double[]::clone).toArray(double[][]::new);
        Matrix resultMatrix = new Matrix(copyArray);
        int swapCount = 0;
        int sign = 1;
        outerLoop:
        for (int i = 0; i < this.rowsNum() - 1; i++) {
            int swapIter = i;
            while (resultMatrix.getVal(i, i) == 0) {
                if (swapIter == this.rowsNum() - 1) {
                    break outerLoop;
                }
                resultMatrix.swapRows(i, swapIter+1);
                swapIter++;
                swapCount++;
            }
            for (int j = i+1; j < this.colsNum(); j++) {
                double factor = resultMatrix.getVal(j, i)/resultMatrix.getVal(i,i);
                for (int m = 0; m < this.matrix[0].length; m++) {
                    double nextElement = resultMatrix.getVal(j, m) - resultMatrix.getVal(i, m)*factor;
                    resultMatrix.changeVal(nextElement, j, m);
                }
            }
        }
        if (swapCount % 2 != 0) {
            sign = -1;
        }
        logger.info("Выполнен прямой ход метода гаусса для матрицы {}, получен результат {}", this.matrix, resultMatrix.matrix);
        return new ImmutablePair<>(resultMatrix, sign);
    }

    /**
     * Вычисляет определитель матрицы, округление до 10 знаков после запятой.
     * @return Определитель
     * @see #gaussElimination()
     * @throws MatrixException в случае возникновения исключения
     */
    public double determinant() throws MatrixException {
        Pair<Matrix, Integer> Pair = this.gaussElimination();
        Matrix resultMatrix = Pair.getLeft();
        int sign = Pair.getRight();
        double determinant = IntStream.range(0, resultMatrix.rowsNum())
                .mapToDouble(i -> resultMatrix.getVal(i, i))
                .reduce((a, b) -> a * b)
                .getAsDouble();
        determinant *= sign;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat roundToTenDigits = new DecimalFormat("#0.##########", symbols);
        roundToTenDigits.setRoundingMode(RoundingMode.HALF_UP);
        double determinantRounded = Double.parseDouble(roundToTenDigits.format(determinant));
        logger.info("Вычислен определитель для матрицы {}, результат равен {}", resultMatrix.matrix, determinant);
        return determinantRounded;
    }
}
