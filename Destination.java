package code;

public class Destination {
    private final String name;
    private final int x;
    private final int y;
    
    public Destination(String name, int x, int y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public String getName() { return name; }
}
