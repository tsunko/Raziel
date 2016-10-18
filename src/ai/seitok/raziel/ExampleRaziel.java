package ai.seitok.raziel;

import java.util.concurrent.ThreadLocalRandom;

public class ExampleRaziel extends SimpleRaziel {

    @Override
    public void run(){
        while(true){
            if(ThreadLocalRandom.current().nextBoolean()){
                turnLeft();
            } else {
                turnRight();
            }

            if(isFrontClear()){
                move();
            }
        }
    }

}
