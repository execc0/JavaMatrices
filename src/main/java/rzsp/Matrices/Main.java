package rzsp.Matrices;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Scanner;

/**
 * Класс с реализацией консольного приложения
 */
public class Main {
    /**
     * Основной метод программы, предлагает пользователю выбрать одну из операций над матрицами,
     * результат выводит в консоль.
     * <p>
     * Логи возникающих исключений выводятся в консоль и в файл logs/matrix.log
     * @param args:
     *            args[0] - путь к первому текстовому файлу с матрицей
     *            args[1] - путь ко второму текстовому файлу с матрицей
     *            Элементы матрицы в файлах записываются через пробел,
     *            каждая новая строка матрицы начинается с символа переноса строки.
     */
    public static void main(String[] args) {
        final Logger logger = LogManager.getLogger(Main.class);
        boolean running = true;
        try {
            if (args.length != 2) {
                throw new MatrixException("Передано число аргументов отличное от 2");
            }
            Matrix Matrix1 = Matrix.fromFile(args[0]);
            Matrix Matrix2 = Matrix.fromFile(args[1]);
            System.out.println("Введённые матрицы:\n");
            Matrix1.print();
            System.out.println();
            Matrix2.print();
            System.out.println();
            while (running) {
                System.out.println("""
                    ************************************************************
                    Введите операцию
                    1 - сложение матриц
                    2 - разность матриц (вычитает из args[0] матрицу args[1])
                    3 - произведение матриц (умножает args[0] справа на args[1])
                    4 - вычисление определителя одной из матриц
                    Любой другой символ для выхода
                    ************************************************************
                    """);
                Matrix result;
                Scanner scanner = new Scanner(System.in);
                String operation = scanner.nextLine();
                switch (operation) {
                    case "1":
                        result = Matrix1.sum(Matrix2);
                        System.out.println("Сумма матриц: ");
                        result.print();
                        break;
                    case "2":
                        result = Matrix1.subtract(Matrix2);
                        System.out.println("Разность матриц: ");
                        result.print();
                        break;
                    case "3":
                        result = Matrix1.multiply(Matrix2);
                        System.out.println("Произведение матриц: ");
                        result.print();
                        break;
                    case "4":
                        System.out.println("""
                                *******************************************
                                Для какой из матриц вычислить определитель?
                                1 - первая матрица (args[0])
                                2 - вторая матрица (args[1])
                                *******************************************
                                """);
                        String choice = scanner.nextLine();
                        switch (choice) {
                            case "1":
                                System.out.println("Определитель первой матрицы равен " + Matrix1.determinant());
                                break;
                            case "2":
                                System.out.println("Определитель второй матрицы равен " + Matrix2.determinant());
                                break;
                        }
                        break;
                    default:
                        System.out.println("Выход из программы");
                        running = false;
                        break;
                }
            }
        } catch (RuntimeException e) {
            logger.error(e);
            throw new RuntimeException("Выполнение программы прервано, для сведений обратитесь к файлу matrix.log");
        }
    }
}