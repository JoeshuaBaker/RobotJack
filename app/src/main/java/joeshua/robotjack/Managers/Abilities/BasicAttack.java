package joeshua.robotjack.Managers.Abilities;

import java.util.LinkedList;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Jack;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 4/23/2017.
 */

public class BasicAttack extends Ability {

    /*
    private AbilityIcon icon;
    private AnimationBitmap animation;
    private GameObject source;
    private boolean friendly;
    private LinkedList<Tile[]> tiles;
    private LinkedList<Instruction> instructions;
    private DamageType damageType;
    private ArrayList<GameObject> targets;
    */

    public BasicAttack(Jack jack)
    {
        //initialization of stuff. Only jack can basicattack.
        setup(true, jack);
        MAX_COOLDOWN = 0;
        setCooldown(0);


        LinkedList<Instruction> instructions = new LinkedList<Instruction>();
        instructions.add(new Instruction(InstructionType.And, 1, 3));
        instructions.add(new Instruction(InstructionType.ScanRowFirst, 1));
        instructions.add(new Instruction(InstructionType.Damage, 1));
        instructions.add(new Instruction(InstructionType.BasicAttackCD, 1));
        setInstructions(instructions);
        animations = new AnimationPlayer("BasicAttack", "BasicAttack", true);


        setDamage(1);
    }
}
