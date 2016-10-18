package ai.seitok.raziel;

import ai.seitok.raziel.ui.RazielUI;
import ai.seitok.raziel.ui.WorldPanel;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HumanControlledRaziel extends Raziel {

    private Object congratulations_you_found_the_easter_egg = new Object();
    private Object i_hope_you_enjoy_using_raziel_over_karel = new Object();
    private Object this_application_is_by_seitok_dot_ai = new Object();
    private Object have_fun = new Object();

    public static class WASDListener implements KeyListener {

        private ExecutorService service = Executors.newSingleThreadExecutor();
        private Future<?> future;
        private Raziel robot;

        public WASDListener(Raziel raziel){
            robot = raziel;
        }

        @Override
        public void keyTyped(KeyEvent e){
            if(future != null && !future.isDone()){
                future.cancel(true);
            }

            switch(Character.toLowerCase(e.getKeyChar())){
                case 'w':{
                    future = service.submit(()-> {
                        if(robot.getDirection() != Direction.NORTH){
                            while(robot.getDirection() != Direction.NORTH){
                                robot.turnLeft();
                            }
                        } else {
                            if(!robot.isBlocked(Direction.NORTH)){
                                robot.move();
                            }
                        }
                    });
                    break;
                }

                case 'a':{
                    future = service.submit(()-> {
                        if(robot.getDirection() != Direction.WEST){
                            while(robot.getDirection() != Direction.WEST){
                                robot.turnLeft();
                            }
                        } else {
                            if(!robot.isBlocked(Direction.WEST)){
                                robot.move();
                            }
                        }
                    });
                    break;
                }

                case 's':{
                    future = service.submit(()-> {
                        if(robot.getDirection() != Direction.SOUTH){
                            while(robot.getDirection() != Direction.SOUTH){
                                robot.turnLeft();
                            }
                        } else {
                            if(!robot.isBlocked(Direction.SOUTH)){
                                robot.move();
                            }
                        }
                    });
                    break;
                }

                case 'd':{
                    future = service.submit(()-> {
                        if(robot.getDirection() != Direction.EAST){
                            while(robot.getDirection() != Direction.EAST){
                                robot.turnLeft();
                            }
                        } else {
                            if(!robot.isBlocked(Direction.EAST)){
                                robot.move();
                            }
                        }
                    });
                    break;
                }

                case ' ':{
                    future = service.submit(()->{
                        if(Environment.getWorld().getTile(robot.getX(), robot.getY()).hasCoins()){
                            robot.pickUpCoin();
                        }
                    });
                    break;
                }

                case 'z':{
                    future = service.submit(()->{
                        if(Environment.getRobot().getCoins() > 0){
                            robot.putDownCoin();
                        }
                    });
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e){

        }

        @Override
        public void keyReleased(KeyEvent e){

        }
    }

    @Override
    public void run(){
        try {
            Field singleton_f = Application.class.getDeclaredField("SINGLETON");
            Field ui_f = Application.class.getDeclaredField("ui");
            Field worldView_f = RazielUI.class.getDeclaredField("worldView");
            singleton_f.setAccessible(true);
            ui_f.setAccessible(true);
            worldView_f.setAccessible(true);
            ((WorldPanel)worldView_f.get(ui_f.get(singleton_f.get(null)))).addKeyListener(new WASDListener(this));
        } catch (Throwable t){
            t.printStackTrace();
        }
    }

}
