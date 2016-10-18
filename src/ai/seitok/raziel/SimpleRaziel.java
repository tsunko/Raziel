package ai.seitok.raziel;

public abstract class SimpleRaziel extends Raziel {

    public boolean isFrontClear(){
        return !isBlocked(getDirection());
    }

    public boolean isWestClear(){
        return !isBlocked(Direction.WEST);
    }

    public boolean isEastClear(){
        return !isBlocked(Direction.EAST);
    }

    public boolean isFacingNorth(){
        return getDirection() == Direction.NORTH;
    }

    public boolean isFacingWest(){
        return getDirection() == Direction.WEST;
    }

    public boolean isFacingEast(){
        return getDirection() == Direction.EAST;
    }

    public boolean hasCoins(){
        return getCoins() > 0;
    }

    public boolean doesTileHaveCoins(){
        return Environment.getWorld().getTile(getX(), getY()).hasCoins();
    }

}
