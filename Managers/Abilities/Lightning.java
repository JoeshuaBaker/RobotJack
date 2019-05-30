package joeshua.robotjack.Managers.Abilities;

import java.util.LinkedList;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 5/29/2017.
 */

public class Lightning extends Ability {

    private int currentCharge = 0;
    private final int chargeThreshold = 8;
    private final int maxCharge = 8;

    public Lightning()
    {
        setDamage(30);
        MAX_COOLDOWN = 600;
        setIcon(new AbilityIcon(Globals.animationLibrary.getAnimations("Icons").get("Strike"), DamageType.Energy, "Lightning",
                "Spiral of lightning from the center. More charge = more bolts. Current charge: 0 (Max 8)", "30", this));
        animations = new AnimationPlayer("Lightning", "Strike", true);
    }

    public void setup(boolean _isFriendly, GameObject _source, int charge)
    {
        super.setup(_isFriendly, _source);
        setFriendly(_isFriendly);
        setSource(_source);
        setupTiles(_isFriendly, charge);
        MAX_COOLDOWN = 120*charge;

        LinkedList<Instruction> instructions = new LinkedList<Instruction>();

        if(_isFriendly)
        {
            for(int i = 0; i < charge; ++i)
            {
                instructions.add(new Instruction(InstructionType.And, 10, 5));
                instructions.add(new Instruction(InstructionType.ScanTile, 1));
                instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Strike"));
                instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
                instructions.add(new Instruction(InstructionType.Damage, 1));
                instructions.add(new Instruction(InstructionType.NextTile, 1));
            }
            if(charge >= maxCharge)
            {
                instructions.add(new Instruction(InstructionType.And, 10, 5));
                instructions.add(new Instruction(InstructionType.ScanTile, 1));
                instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Strike"));
                instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
                instructions.add(new Instruction(InstructionType.Damage, 1));
                instructions.add(new Instruction(InstructionType.NextTile, 1));
                instructions.add(new Instruction(InstructionType.And, 10, 5));
                instructions.add(new Instruction(InstructionType.ScanTile, 1));
                instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Strike"));
                instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
                instructions.add(new Instruction(InstructionType.Damage, 1));
                instructions.add(new Instruction(InstructionType.NextTile, 1));
            }
        }
        else
        {
            if(charge == maxCharge)
            {

                instructions.add(new Instruction(InstructionType.Warn, 45, 30));
                instructions.add(new Instruction(InstructionType.And, 15, 5));
                instructions.add(new Instruction(InstructionType.ScanTile, 1));
                instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Strike"));
                instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
                instructions.add(new Instruction(InstructionType.Damage, 1));
                instructions.add(new Instruction(InstructionType.NextTile, 1));

                instructions.add(new Instruction(InstructionType.Warn, 45, 30));
                instructions.add(new Instruction(InstructionType.And, 15, 5));
                instructions.add(new Instruction(InstructionType.ScanTile, 1));
                instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Strike"));
                instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
                instructions.add(new Instruction(InstructionType.Damage, 1));
                instructions.add(new Instruction(InstructionType.NextTile, 1));

            }


            for(int i = 0; i < charge; ++i)
            {
                instructions.add(new Instruction(InstructionType.And, 45, 2));
                instructions.add(new Instruction(InstructionType.GetPlayerTile, 1));
                instructions.add(new Instruction(InstructionType.Warn, 1, 30));

                instructions.add(new Instruction(InstructionType.And, 15, 5));
                instructions.add(new Instruction(InstructionType.ScanTile, 1));
                instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Strike"));
                instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
                instructions.add(new Instruction(InstructionType.Damage, 1));
                instructions.add(new Instruction(InstructionType.NextTile, 1));
            }


        }
        setInstructions(instructions);

    }

    @Override
    public void setup(boolean _isFriendly, GameObject _source)
    {
        setup(_isFriendly, _source, (currentCharge/chargeThreshold));
    }

    private void setupTiles(boolean isFriendly, int charge)
    {
        LinkedList<Tile[]> tiles = new LinkedList<>();
        if(isFriendly)
        {
            switch(charge)
            {
                case 8:
                    Tile[] all = Globals.grid.getLinearGrid();
                    Tile[] enemies = new Tile[9];
                    for(int i = 0; i < 9 ; ++i)
                    {
                        enemies[i] = all[9 + i];
                    }
                    tiles.addFirst(enemies);
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(5, 2)});

                case 7:
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(4, 2)});
                case 6:
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(3, 2)});

                case 5:
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(3, 1)});

                case 4:
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(3, 0)});

                case 3:
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(4, 0)});

                case 2:
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(5, 0)});

                case 1:
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(5, 1)});

                case 0:
                    tiles.addFirst(new Tile[] {Globals.grid.getTile(4, 1)});
                    break;

            }
        }
        else
        {
            if(charge == maxCharge)
            {
                tiles.add(new Tile[]{Globals.grid.getTile(0, 0), Globals.grid.getTile(0, 2), Globals.grid.getTile(1, 1),
                        Globals.grid.getTile(2, 0), Globals.grid.getTile(2, 2)});
                tiles.add(new Tile[]{Globals.grid.getTile(0, 1), Globals.grid.getTile(1, 0), Globals.grid.getTile(1, 1),
                        Globals.grid.getTile(1, 2), Globals.grid.getTile(2, 1)});
            }
        }


        setTiles(tiles);
    }

    public void getCharge()
    {
        if((currentCharge/chargeThreshold) < maxCharge)
        {
            currentCharge++;
            getIcon().setTooltip("Spiral of lightning from the center. More charge = more bolts. Current charge: " + (currentCharge/chargeThreshold) + " (Max 8)");
        }
    }

    @Override
    public Lightning clone()
    {
        return new Lightning();
    }
}
