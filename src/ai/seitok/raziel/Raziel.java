package ai.seitok.raziel;

import ai.seitok.raziel.world.Tile;
import ai.seitok.raziel.world.World;

public abstract class Raziel implements Robot {

    private int x = 0, y = 0;
    private Direction direction = Direction.NORTH;
    private int coins;

    // sets the x coordinate
    private void setX(int x){
        this.x = x;
    }

    // set the y coordinate
    private void setY(int y){
        this.y = y;
    }

    // sets the direction
    private void setDirection(Direction dir){
        this.direction = dir;
    }

    // returns the x coord
    @Override
    public int getX(){
        return x;
    }

    // returns the y coord
    @Override
    public int getY(){
        return y;
    }

    // returns the direction
    @Override
    public Direction getDirection(){
        return direction;
    }

    @Override
    public int getCoins(){
        return coins;
    }

    @Override
    public void pickUpCoin(){
        Tile tile = Environment.getWorld().getTile(getX(), getY());
        if(!tile.hasCoins()){
            throw new IllegalStateException(String.format("Tile %d,%d has no coins on it!", getX(), getY()));
        }

        tile.setCoins(tile.getCoinCount() - 1);
        coins++;
        doWait();
    }

    @Override
    public void putDownCoin(){
        if(getCoins() <= 0){
            throw new IllegalStateException("No coins left!");
        }

        Tile tile = Environment.getWorld().getTile(getX(), getY());
        tile.setCoins(tile.getCoinCount() + 1);
        coins--;
        doWait();
    }

    @Override
    public void move(){
        boolean blocked = isBlocked(direction);

        // if it's blocked, error
        if(blocked){
            throw new IllegalStateException("Blocked! (dir=" + direction.name() + ")");
        }

        // apply changes
        switch(direction){
            case SOUTH:
                y++;
                break;
            case EAST:
                x++;
                break;
            case NORTH:
                y--;
                break;
            case WEST:
                x--;
                break;
        }

        doWait();
    }

    // turn left based on current direction
    @Override
    public void turnLeft(){
        switch(direction){
            case NORTH: direction = Direction.WEST; break;
            case EAST: direction = Direction.NORTH; break;
            case SOUTH: direction = Direction.EAST; break;
            case WEST: direction = Direction.SOUTH;
        }
        doWait();
    }

    // turn right based on current direction
    @Override
    public void turnRight(){
        switch(direction){
            case NORTH: direction = Direction.EAST; break;
            case EAST: direction = Direction.SOUTH; break;
            case SOUTH: direction = Direction.WEST; break;
            case WEST: direction = Direction.NORTH;
        }
        doWait();
    }

    @Override
    public boolean isBlocked(Direction dir){
        World world = Environment.getWorld();
        boolean blocked = false;
        // check if blocked in a certain direction
        switch(direction){
            // check south
            case SOUTH:{
                blocked = y + 1 >= world.getHeight() || world.getTile(x, y).hasBottomWall();
                break;
            }

            // check east
            case EAST:{
                blocked = x + 1 >= world.getWidth() || world.getTile(x, y).hasRightWall();
                break;
            }

            // check north
            case NORTH:{
                blocked = y - 1 < 0 || world.getTile(x, y - 1).hasBottomWall();
                break;
            }

            // check west
            case WEST:{
                blocked = x - 1 < 0 || world.getTile(x - 1, y).hasRightWall();
                break;
            }

            // no default since it's not needed
        }
        return blocked;
    }

    // waits a certain amount of time. we don't need to worry about WorldPanel being updated as it auto updates every
    // 50ms
    private void doWait(){
        try {
            Thread.sleep(Application.getDelay());
        } catch (InterruptedException e){}
    }

    // output basic information about the current state
    @Override
    public String toString() {
        return String.format("%s{x=%d,y=%d,direction=%s}", getClass().getSimpleName(), x, y, direction.toString());
    }
}
