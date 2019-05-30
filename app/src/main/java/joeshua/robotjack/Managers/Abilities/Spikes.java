package joeshua.robotjack.Managers.Abilities;

import java.util.LinkedList;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 6/5/2017.
 */

public class Spikes extends Ability {

    public Spikes()
    {
        setDamage(30);
        MAX_COOLDOWN = 360;
        setIcon(new AbilityIcon(Globals.animationLibrary.getAnimations("Icons").get("Spikes"), DamageType.Mechanical, "Spikes",
                "Attacks and immobilizes the two front squares of your row.", "30", this));
        animations = new AnimationPlayer("Spikes", "SpikesProjectile", true);
    }

    @Override
    public void setup(boolean _isFriendly, GameObject _source) {
        super.setup(_isFriendly, _source);
        setFriendly(_isFriendly);
        setSource(_source);
        setupTiles();

        LinkedList<Instruction> instructions = new LinkedList<Instruction>();

        instructions.add(new Instruction(InstructionType.And, 8, 3));
        instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "SpikesProjectile"));
        instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
        instructions.add(new Instruction(InstructionType.NextTile, 1));

        instructions.add(new Instruction(InstructionType.And, 1, 4));
        instructions.add(new Instruction(InstructionType.ScanTile, 1));
        instructions.add(new Instruction(InstructionType.Damage, 1));
        instructions.add(new Instruction(InstructionType.InflictStatus, 1, 300, StatusEffect.Bind));
        instructions.add(new Instruction(InstructionType.FinishAnimation, 1));

        setInstructions(instructions);
    }

    private void setupTiles()
    {
        LinkedList<Tile[]> tiles = new LinkedList<>();

        if(getSource() != null && getSource().getTile() != null)
        {
            tiles.add(new Tile[] {Globals.grid.getTile(2, getSourceY())});
            tiles.add(new Tile[] {Globals.grid.getTile(3, getSourceY()),
                                    Globals.grid.getTile(4, getSourceY())});
        }

        setTiles(tiles);
    }

    @Override
    public Spikes clone()
    {
        return new Spikes();
    }
}
