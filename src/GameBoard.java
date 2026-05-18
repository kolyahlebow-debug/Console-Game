import java.util.Random;

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
        for (int i = 0; i < size; i++) System.out.print(" " + i + " ");
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
