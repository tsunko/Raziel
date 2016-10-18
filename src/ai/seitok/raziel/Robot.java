package ai.seitok.raziel;

public interface Robot extends Runnable {

    // returns the current X position
    public int getX();

    // returns the current Y position
    public int getY();

    // returns the current direction the robot is facing
    public Direction getDirection();

    // turns the robot left
    public void turnLeft();

    // turns the robot right
    public void turnRight();

    // moves the robot forward, checking if there's a wall.
    public void move();

    // checks if the given direction is blocked by wall or world boundary
    public boolean isBlocked(Direction dir);

    // gets the amount of coins on hand
    public int getCoins();

    public void pickUpCoin();

    public void putDownCoin();

}
