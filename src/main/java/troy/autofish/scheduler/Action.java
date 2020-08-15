package troy.autofish.scheduler;

import net.minecraft.util.Util;

public class Action {

    private ActionType actionType;
    private long delay;
    private long timeToComplete;
    private Runnable runnable;

    public Action(ActionType actionType, long delay, Runnable runnable) {
        this.actionType = actionType;
        this.delay = delay;
        this.timeToComplete = Util.getMeasuringTimeMs() + delay;
        this.runnable = runnable;

//        System.out.println("regd " + actionType + ". Complete in " + delay + " at " + timeToComplete);
    }

    /**
     * @return true if the action was completed
     */
    public boolean tick(){

        if(Util.getMeasuringTimeMs() >= timeToComplete){

            runnable.run();

            //If this is a repeating action, we need to reset the timer
            if(actionType == ActionType.REPEATING_ACTION){
                this.timeToComplete = Util.getMeasuringTimeMs() + delay;
            }

            return true;
        }
        return false;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void resetTimer(){
        this.timeToComplete = Util.getMeasuringTimeMs() + delay;
    }
}
