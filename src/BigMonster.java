import java.util.Random;
import java.util.Scanner;

class BigMonster extends Monster {
    public BigMonster(int x, int y) {
        super(x, y);
    }

    @Override
    public String getSymbol() { return "[B]"; }

    @Override
    public boolean interact(int diff) {
        System.out.println("БОЛЬШОЙ МОНСТР!");
        int a = new Random().nextInt(10) + 5;
        int b = new Random().nextInt(5) + 2;
        Scanner sc = new Scanner(System.in);
        if (diff == 1) {
            System.out.print("Реши задачу: " + a + " * " + b + " = ");
            while (!sc.hasNextInt()) {
                System.out.print("Некорректный ввод. Введите число: ");
                sc.next();
            }
            return sc.nextInt() == (a * b);
        } else {
            System.out.print("Сложная задача: (" + a + " * " + b + ") - 5 = ");
            while (!sc.hasNextInt()) {
                System.out.print("Некорректный ввод. Введите число: ");
                sc.next();
            }
            return sc.nextInt() == ((a * b) - 5);
        }
    }
}
