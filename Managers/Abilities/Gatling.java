package joeshua.robotjack.Managers.Abilities;

import java.util.LinkedList;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 6/4/2017.
 */

public class Gatling extends Ability {

    public Gatling() {
        setDamage(5);
        MAX_COOLDOWN = 720;
        setIcon(new AbilityIcon(Globals.animationLibrary.getAnimations("Icons").get("Gatling"), DamageType.Mechanical, "Gatling",
                "Peppers a whole row with a long stream of gunfire. Hits 12 times.", "5/60", this));
        animations = new AnimationPlayer("Gatling", "FaceGunBullet", true);
    }

    @Override
    public void setup(boolean _isFriendly, GameObject _source) {
        super.setup(_isFriendly, _source);
        setFriendly(_isFriendly);
        setSource(_source);
        setupTiles();

        LinkedList<Instruction> instructions = new LinkedList<Instruction>();
        if(!_isFriendly)
            instructions.add(new Instruction(InstructionType.Warn, 60, 60));

        for(int i = 0; i < 12; ++i)
        {
            instructions.add(new Instruction(InstructionType.And, 13, 4));
            instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "FaceGunBullet"));
            instructions.add(new Instruction(InstructionType.AddAnimationSourceTile, 1));
            instructions.add(new Instruction(InstructionType.ScanTile, 1));
            instructions.add(new Instruction(InstructionType.Damage, 1));
        }

        setInstructions(instructions);
    }

    private void setupTiles() {
        LinkedList<Tile[]> tiles = new LinkedList<>();
        if (isFriendly())
        {
            if(getSource() != null && getSource().getTile() != null)
            {
                tiles.add(new Tile[] {Globals.grid.getTile(3, getSourceY()), Globals.grid.getTile(4, getSourceY()),
                        Globals.grid.getTile(5, getSourceY())});

                sourceTile = getSource().getTile();
            }

        }
        else
        {
            if(getSource() != null && getSource().getTile() != null)
            {
                tiles.add(new Tile[] {Globals.grid.getTile(0, getSourceY()), Globals.grid.getTile(1, getSourceY()),
                        Globals.grid.getTile(2, getSourceY())});
                sourceTile = getSource().getTile();
            }
        }
        setTiles(tiles);
    }

    @Override
    public Gatling clone()
    {
        return new Gatling();
    }
}