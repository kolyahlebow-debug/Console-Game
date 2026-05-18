import java.util.Random;
import java.util.Scanner;

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
        scanner.nextLine();

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
        hero = new Hero(size - 1, r.nextInt(size));
        castle = new Castle(0, r.nextInt(size));
        board = new GameBoard(size);
        board.generateMonsters(hero, castle, difficulty);
    }

    private MoveValidationResult validateMove(int nx, int ny) {
        if (nx < 0 || nx >= board.getSize() || ny < 0 || ny >= board.getSize()) {
            return MoveValidationResult.OUT_OF_BOUNDS;
        }
        if (Math.abs(nx - hero.getX()) + Math.abs(ny - hero.getY()) == 1) {
            return MoveValidationResult.VALID;
        }
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
