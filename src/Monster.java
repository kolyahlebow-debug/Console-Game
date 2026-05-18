abstract class Monster extends GameObject {
    public Monster(int x, int y) {
        super(x, y);
    }

    public abstract boolean interact(int diff);
}
