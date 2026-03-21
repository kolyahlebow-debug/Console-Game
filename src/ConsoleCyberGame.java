
import java.util.Random;
import java.util.Scanner;

/**
 *  Запуски игры.
 */
public class ConsoleCyberGame {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        engine.start();
    }
}

/**
 * Класс для результата хода
 */
enum MoveValidationResult {
    VALID,
    OUT_OF_BOUNDS,
    TOO_FAR_OR_DIAGONAL
}

/**
 * Логика и управление
 */
class GameEngine {
    private GameBoard board;
    private Hero hero;
    private Castle castle;
    private Scanner scanner = new Scanner(System.in);
    private int difficulty;

    public void start() {
        System.out.println("=== КИБЕР-КВЕСТ 2026: КОНСОЛЬНАЯ ВЕРСИЯ ===");
        System.out.print("Выберите уровень сложности (1 - Легко, 2 - Тяжело): ");
        difficulty = (scanner.hasNextInt()) ? scanner.nextInt() : 1;
        scanner.nextLine(); // Очистка буфера

        showInstructions();
        initGame();

        boolean running = true;
        while (running) {
            board.display(hero, castle);
            System.out.println("❤️ Жизни: " + hero.getLives() + " | Позиция: [" + hero.getX() + "][" + hero.getY() + "]");
            System.out.println("Введите координаты (например, '3 2'):");

            String input = scanner.nextLine();
            try {
                String[] coords = input.split(" ");
                if (coords.length != 2) {
                    System.out.println("❌ ОШИБКА: Введите два числа через пробел.");
                    continue;
                }
                int nextX = Integer.parseInt(coords[0]);
                int nextY = Integer.parseInt(coords[1]);

                MoveValidationResult validation = validateMove(nextX, nextY);

                if (validation == MoveValidationResult.VALID) {
                    hero.setX(nextX);
                    hero.setY(nextY);
                    processCell(nextX, nextY);
                } else {
                    // Более специфичные сообщения об ошибках
                    if (validation == MoveValidationResult.OUT_OF_BOUNDS) {
                        System.out.println("❌ ОШИБКА: Координаты должны быть в пределах поля (от 0 до " + (board.getSize() - 1) + ")!");
                    } else if (validation == MoveValidationResult.TOO_FAR_OR_DIAGONAL) {
                        System.out.println("❌ ОШИБКА: Ходить можно только на 1 клетку по горизонтали или вертикали!");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ ОШИБКА: Введите числовые координаты.");
            } catch (Exception e) {
                System.out.println("❌ Неизвестная ошибка ввода. Попробуйте еще раз.");
            }

            // Условия завершения
            if (hero.getLives() <= 0) {
                System.out.println("\n💀 ИГРА ОКОНЧЕНА. Жизни исчерпаны.");
                running = false;
            } else if (hero.getX() == castle.getX() && hero.getY() == castle.getY()) {
                System.out.println("\n🏆 ПОБЕДА! Вы достигли замка!");
                running = false;
            }
        }
    }

    private void showInstructions() {
        System.out.println("\nИНСТРУКЦИЯ:");
        System.out.println("- Цель: Добраться до [C] (Замок)");
        System.out.println("- Вы: [H]. Ходить можно на 1 клетку вверх, вниз, влево, вправо.");
        System.out.println("- Монстры: [s] (малый) и [B] (большой). При встрече нужно решить задачу.");
        System.out.println("- Нажмите ENTER, чтобы начать...");
        scanner.nextLine();
    }

    private void initGame() {
        int size = 5;
        Random r = new Random();
        // Герой внизу, замок вверху (по критериям)
        hero = new Hero(size - 1, r.nextInt(size));
        castle = new Castle(0, r.nextInt(size));
        board = new GameBoard(size);
        board.generateMonsters(hero, castle, difficulty);
    }

    // Обновленный метод для детальной валидации хода
    private MoveValidationResult validateMove(int nx, int ny) {
        // 1. Проверка на выход за границы поля
        if (nx < 0 || nx >= board.getSize() || ny < 0 || ny >= board.getSize()) {
            return MoveValidationResult.OUT_OF_BOUNDS;
        }
        // 2. Проверка на перемещение ровно на 1 клетку по горизонтали или вертикали
        if (Math.abs(nx - hero.getX()) + Math.abs(ny - hero.getY()) == 1) {
            return MoveValidationResult.VALID;
        }
        // 3. Все остальные случаи - некорректный шаг (слишком далеко или по диагонали)
        return MoveValidationResult.TOO_FAR_OR_DIAGONAL;
    }

    private void processCell(int x, int y) {
        Monster monster = board.getMonsterAt(x, y);
        if (monster != null) {
            if (monster.interact(difficulty)) {
                System.out.println("✅ Правильно! Путь свободен.");
            } else {
                hero.damage(1);
                System.out.println("❌ Ошибка! Вы потеряли жизнь.");
            }
            board.removeMonster(x, y);
        }
    }
}

/**
 * ООП: Абстрактный базовый класс для всех объектов на поле
 */
abstract class GameObject {
    protected int x, y;
    public GameObject(int x, int y) { this.x = x; this.y = y; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public abstract String getSymbol();
}

class Hero extends GameObject {
    private int lives = 3;
    public Hero(int x, int y) { super(x, y); }
    public int getLives() { return lives; }
    public void damage(int val) { lives -= val; }
    @Override public String getSymbol() { return "[H]"; }
}

class Castle extends GameObject {
    public Castle(int x, int y) { super(x, y); }
    @Override public String getSymbol() { return "[C]"; }
}


abstract class Monster extends GameObject {
    public Monster(int x, int y) { super(x, y); }
    public abstract boolean interact(int diff);
}

class SmallMonster extends Monster {
    public SmallMonster(int x, int y) { super(x, y); }
    @Override public String getSymbol() { return "[s]"; }
    @Override public boolean interact(int diff) {
        int a = new Random().nextInt(10) + 1;
        int b = new Random().nextInt(10) + 1;
        System.out.print("👾 Малый монстр: " + a + " + " + b + " = ");
        Scanner sc = new Scanner(System.in);
        while (!sc.hasNextInt()) {
            System.out.print("Некорректный ввод. Введите число: ");
            sc.next();
        }
        return sc.nextInt() == (a + b);
    }
}

class BigMonster extends Monster {
    public BigMonster(int x, int y) { super(x, y); }
    @Override public String getSymbol() { return "[B]"; }
    @Override public boolean interact(int diff) {
        System.out.println("👹 БОЛЬШОЙ МОНСТР!");
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

/**
 * Класс поля.
 */
class GameBoard {
    private int size;
    private Monster[][] monsterMap;

    public GameBoard(int size) {
        this.size = size;
        this.monsterMap = new Monster[size][size];
    }

    public void generateMonsters(Hero hero, Castle castle, int diff) {
        Random r = new Random();
        int count = (diff == 1) ? 4 : 7;
        for (int i = 0; i < count; i++) {
            int mx, my;
            do {
                mx = r.nextInt(size);
                my = r.nextInt(size);
            } while ((mx == hero.getX() && my == hero.getY()) ||
                    (mx == castle.getX() && my == castle.getY()) ||
                    monsterMap[mx][my] != null);

            if (r.nextBoolean()) monsterMap[mx][my] = new SmallMonster(mx, my);
            else monsterMap[mx][my] = new BigMonster(mx, my);
        }
    }

    public void display(Hero hero, Castle castle) {
        System.out.println("\n      КАРТА");
        System.out.print("  ");
        for(int i = 0; i < size; i++) System.out.print(" " + i + " ");
        System.out.println();
        for (int i = 0; i < size; i++) {
            System.out.print(i + " ");
            for (int j = 0; j < size; j++) {
                if (i == hero.getX() && j == hero.getY()) System.out.print(hero.getSymbol());
                else if (i == castle.getX() && j == castle.getY()) System.out.print(castle.getSymbol());
                else if (monsterMap[i][j] != null) System.out.print(monsterMap[i][j].getSymbol());
                else System.out.print("[. ]");
            }
            System.out.println();
        }
    }

    public Monster getMonsterAt(int x, int y) { return monsterMap[x][y]; }
    public void removeMonster(int x, int y) { monsterMap[x][y] = null; }
    public int getSize() { return size; }
}
