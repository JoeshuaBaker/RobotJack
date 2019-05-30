package joeshua.robotjack.Managers.Abilities;

/**
 * Created by joeshua on 4/21/2017.
 */

public class Instruction {

    private final int placement;  //int field that holds number the instructions might need to use
    private final InstructionType type;
    private final int delay;
    private int currentDelay;
    private boolean finished;
    private boolean executed;
    private String tag;
    private StatusEffect status;

    public Instruction(InstructionType _type, int _delay)
    {
        placement = -99;
        type = _type;
        delay = _delay;
        currentDelay = _delay;
        finished = false;
        executed = false;
    }

    public Instruction(InstructionType _type, int _delay, String _tag)
    {
        placement = -99;
        type = _type;
        delay = _delay;
        currentDelay = delay;
        tag = _tag;
        finished = false;
        executed = false;
    }

    public Instruction(InstructionType _type, int _delay, int _placement)
    {
        placement = _placement;
        type = _type;
        delay = _delay;
        currentDelay = _delay;
        finished = false;
        executed = false;
    }

    public Instruction(InstructionType _type, int _delay, int _placement, StatusEffect _status)
    {
        placement = _placement;
        type = _type;
        delay = _delay;
        currentDelay = _delay;
        finished = false;
        executed = false;
        status = _status;
    }

    public void tickDown()
    {
        tickDown(1);
    }

    public void tickDown(int amount)
    {
        currentDelay -= amount;
        if(currentDelay <= 0 )
        {
            currentDelay = 0;
            finished = true;
        }
    }

    public int getPlacement() {
        return placement;
    }

    public InstructionType getType() {
        return type;
    }

    public int getCurrentDelay() {
        return currentDelay;
    }

    public StatusEffect getStatus() {return status;}

    public boolean isFinished() {
        return finished;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
