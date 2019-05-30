package joeshua.robotjack.Managers.Abilities;

import java.util.LinkedList;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 5/24/2017.
 */

public class Leech extends Ability {

    public Leech()
    {
        setDamage(30);
        MAX_COOLDOWN = 480;
        setIcon(new AbilityIcon(Globals.animationLibrary.getAnimations("Icons").get("Leech"), DamageType.Mechanical, "Leech",
                "Pull an enemy close, then drain their life. Double damage when below half health", "30/60", this));
        animations = new AnimationPlayer("Leech", "LeechSucc", true);
    }

    @Override
    public void setup(boolean _isFriendly, GameObject _source)
    {
        super.setup(_isFriendly, _source);
        setFriendly(_isFriendly);
        setSource(_source);
        LinkedList<Instruction> instructions = new LinkedList<Instruction>();
        if(!isFriendly())
        {
            instructions.add(new Instruction(InstructionType.Warn, 30, 85));
        }
        instructions.add(new Instruction(InstructionType.NextTile, 1));

        instructions.add(new Instruction(InstructionType.And, 1, 3));
        instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "LeechSucc"));
        instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
        instructions.add(new Instruction(InstructionType.NextTile, 1));

        instructions.add(new Instruction(InstructionType.Listen, 30));

        instructions.add(new Instruction(InstructionType.And, 25, 3));
        instructions.add(new Instruction(InstructionType.ScanRowFirst, 1));
        instructions.add(new Instruction(InstructionType.InflictStatus, 1, 30, StatusEffect.Pull));
        instructions.add(new Instruction(InstructionType.NextTile, 1));

        instructions.add(new Instruction(InstructionType.And, 30, 4));
        instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "LeechBite"));
        instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
        instructions.add(new Instruction(InstructionType.Damage, 1));
        instructions.add(new Instruction(InstructionType.HealSource, 1));
        setInstructions(instructions);

        if(getSource().getHealth() <= (getSource().getMaxHealth()/2))
        {
            setDamage(60);
        }
        setupTiles();
    }

    public void setupTiles()
    {
        LinkedList<Tile[]> tiles = new LinkedList<Tile[]>();
        Tile[] rowTiles;
        int yindex = getSource().getTile().getyIndex();
        if(isFriendly())
        {
            rowTiles = new Tile[] {Globals.grid.getTile(3, yindex),
                                   Globals.grid.getTile(4, yindex),
                                   Globals.grid.getTile(5, yindex)};
        }
        else
        {
            rowTiles = new Tile[] {Globals.grid.getTile(0, yindex),
                                   Globals.grid.getTile(1, yindex),
                                   Globals.grid.getTile(2, yindex)};
        }

        tiles.add(rowTiles);

        Tile sourceTile = getSource().getTile();
        tiles.add(new Tile[] {sourceTile});

        tiles.add(rowTiles);

        if(isFriendly())
            tiles.add(new Tile[] {Globals.grid.getTile(3, sourceTile.getyIndex())});
        else
            tiles.add(new Tile[] {Globals.grid.getTile(2, sourceTile.getyIndex())});



        setTiles(tiles);
    }

    @Override
    public Leech clone()
    {
        return new Leech();
    }
}
