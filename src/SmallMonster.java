import java.util.Random;
import java.util.Scanner;

class SmallMonster extends Monster {
    public SmallMonster(int x, int y) {
        super(x, y);
    }

    @Override
    public String getSymbol() { return "[s]"; }

    @Override
    public boolean interact(int diff) {
        int a = new Random().nextInt(10) + 1;
        int b = new Random().nextInt(10) + 1;
        System.out.print("Малый монстр: " + a + " + " + b + " = ");
        Scanner sc = new Scanner(System.in);
        while (!sc.hasNextInt()) {
            System.out.print("Некорректный ввод. Введите число: ");
            sc.next();
        }
        return sc.nextInt() == (a + b);
    }
}
