class Hero extends GameObject {
    private int lives = 3;

    public Hero(int x, int y) {
        super(x, y);
    }

    public int getLives() { return lives; }
    public void damage(int val) { lives -= val; }

    @Override
    public String getSymbol() { return "[H]"; }
}
