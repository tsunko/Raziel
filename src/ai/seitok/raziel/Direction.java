package ai.seitok.raziel;

public enum Direction {

    NORTH(0), EAST(90), SOUTH(180), WEST(270);

    // directions enum so that Java doesn't have to generate a copy of all the possible enum values
    // every time values() is called
    private static final Direction[] values = Direction.values();
    // degrees facing (should be rads so we don't have to call toRadians, but this is for simplicity sake)
    private int degrees;

    // define our basic properties
    Direction(int degrees){
        this.degrees = degrees;
    }

    // returns the degrees this rotation is in
    public int getDegrees(){
        return degrees;
    }

    // return the value of an integer
    public static Direction valueOf(int dir){
        // bounds checking
        if(dir > values.length || dir < 0){
            throw new IllegalArgumentException("Direction is invalid! (" + dir + ")");
        }
        return values[dir];
    }

}
