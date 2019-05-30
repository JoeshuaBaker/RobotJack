package joeshua.robotjack.Managers.Abilities;

import java.util.LinkedList;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 4/26/2017.
 */

public class Firebomb extends Ability {

    public Firebomb()
    {
        setDamage(40);
        MAX_COOLDOWN = 600;
        setIcon(new AbilityIcon(Globals.animationLibrary.getAnimations("Icons").get("Firebomb"), DamageType.Explosive, "Firebomb",
                "Launches a missile that explodes in a plus when it lands.", "40", this));
        animations = new AnimationPlayer("Firebomb", "MissileFireOne", true);
    }

    @Override
    public void setup(boolean _isFriendly, GameObject _source)
    {
        super.setup(_isFriendly, _source);
        setFriendly(_isFriendly);
        setSource(_source);
        setupTiles();

        LinkedList<Instruction> instructions = new LinkedList<Instruction>();
        instructions.add(new Instruction(InstructionType.AddAnimationTile, animations.getCurrentLength()));
        instructions.add(new Instruction(InstructionType.NextTile, 1));
        instructions.add(new Instruction(InstructionType.And, 30, 4));
        instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "MissileFall"));
        instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
        instructions.add(new Instruction(InstructionType.NextTile, 1));
        instructions.add(new Instruction(InstructionType.Warn, 1, 30));
        instructions.add(new Instruction(InstructionType.And, 30, 4));
        instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Explosion"));
        instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
        instructions.add(new Instruction(InstructionType.ScanTile, 1));
        instructions.add(new Instruction(InstructionType.Damage, 1));
        setInstructions(instructions);

    }

    public void setup(boolean _isFriendly, GameObject _source, boolean doubleShot)
    {
        if(!doubleShot)
        {
            setup(_isFriendly, _source);
        }
        else
        {
            super.setup(_isFriendly, _source);
            setFriendly(_isFriendly);
            setSource(_source);
            setupTiles(true);

            LinkedList<Instruction> instructions = new LinkedList<Instruction>();
            animations.playAnimationByTag("MissileFireTwo", false);
            instructions.add(new Instruction(InstructionType.AddAnimationTile, animations.getCurrentLength()));
            instructions.add(new Instruction(InstructionType.NextTile, 1));

            instructions.add(new Instruction(InstructionType.And, 30, 4));
            instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "MissileFall"));
            instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
            instructions.add(new Instruction(InstructionType.NextTile, 1));
            instructions.add(new Instruction(InstructionType.Warn, 1, 30));

            instructions.add(new Instruction(InstructionType.And, 28, 5));
            instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Explosion"));
            instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
            instructions.add(new Instruction(InstructionType.ScanTile, 1));
            instructions.add(new Instruction(InstructionType.Damage, 1));
            instructions.add(new Instruction(InstructionType.NextTile, 1));

            instructions.add(new Instruction(InstructionType.And, 30, 4));
            instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "MissileFall"));
            instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
            instructions.add(new Instruction(InstructionType.NextTile, 1));
            instructions.add(new Instruction(InstructionType.Warn, 1, 30));

            instructions.add(new Instruction(InstructionType.And, 28, 4));
            instructions.add(new Instruction(InstructionType.PlayAnimation, 1, "Explosion"));
            instructions.add(new Instruction(InstructionType.AddAnimationTile, 1));
            instructions.add(new Instruction(InstructionType.ScanTile, 1));
            instructions.add(new Instruction(InstructionType.Damage, 1));
            setInstructions(instructions);
        }
    }

    private int[] getCross(int bombIndex)
    {
        int[] indicies = new int[2];
        switch(bombIndex)
        {
            case 0:
                indicies[0] = 1;
                indicies[1] = 2;
                break;

            case 1:
                indicies[0] = -1;
                indicies[1] = 1;
                break;

            case 2:
                indicies[0] = -2;
                indicies[1] = -1;
                break;
        }

        return indicies;
    }

    private void setupTiles()
    {
        //setting up list of tiles for the cross bomb pattern
        LinkedList<Tile[]> tiles = new LinkedList<Tile[]>();
        Tile sourceTile = getSource().getTile();

        //adding tile of the source
        tiles.add(new Tile[] {getSource().getTile()});

        //creating the tile for the bomb itself: it will be offset by 3 from the source, to the right or left
        int indexOffset = isFriendly() ? 3 : -3;
        Tile bombTile = Globals.grid.getTile(sourceTile.getxIndex() + indexOffset, sourceTile.getyIndex());
        tiles.add(new Tile[] {bombTile});

        //next we create the cross-bomb pattern. This will always be a tile array of 5, bomberman style on a 3x3 grid.
        Tile[] explosionTiles = new Tile[5];
        explosionTiles[0] = bombTile;
        int[] xIndicies = getCross(bombTile.getxIndex() % 3);
        int[] yIndicies = getCross(bombTile.getyIndex());
        int tilesPlaced = 1;
        //place the tiles to the left and/or right of the bomb.
        for(int x : xIndicies)
        {
            explosionTiles[tilesPlaced] = Globals.grid.getTile(bombTile.getxIndex() + x, bombTile.getyIndex());
            tilesPlaced++;
        }
        //place the tiles above and/or below the bomb.
        for(int y : yIndicies)
        {
            explosionTiles[tilesPlaced] = Globals.grid.getTile(bombTile.getxIndex(), bombTile.getyIndex() + y);
            tilesPlaced++;
        }

        tiles.add(explosionTiles);
        setTiles(tiles);
    }

    private void setupTiles(boolean doubleShot)
    {
        if(!doubleShot)
            setupTiles();
        else
        {
            //setting up list of tiles for the cross bomb pattern
            LinkedList<Tile[]> tiles = new LinkedList<Tile[]>();
            Tile sourceTile = getSource().getTile();

            //adding tile of the source
            tiles.add(new Tile[] {getSource().getTile()});

            //creating the tile for the bomb itself: it will be offset by 3 from the source, to the right or left
            int indexOffset = isFriendly() ? 3 : -3;
            Tile bombTile = Globals.grid.getTile(sourceTile.getxIndex() + indexOffset, sourceTile.getyIndex());
            tiles.add(new Tile[] {bombTile});

            //next we create the cross-bomb pattern. This will always be a tile array of 5, bomberman style on a 3x3 grid.
            Tile[] explosionTiles = new Tile[5];
            explosionTiles[0] = bombTile;
            int[] xIndicies = getCross(bombTile.getxIndex() % 3);
            int[] yIndicies = getCross(bombTile.getyIndex());
            int tilesPlaced = 1;
            //place the tiles to the left and/or right of the bomb.
            for(int x : xIndicies)
            {
                explosionTiles[tilesPlaced] = Globals.grid.getTile(bombTile.getxIndex() + x, bombTile.getyIndex());
                tilesPlaced++;
            }
            //place the tiles above and/or below the bomb.
            for(int y : yIndicies)
            {
                explosionTiles[tilesPlaced] = Globals.grid.getTile(bombTile.getxIndex(), bombTile.getyIndex() + y);
                tilesPlaced++;
            }

            tiles.add(explosionTiles);

            if(Math.random() < 0.5)
            {
                bombTile = Globals.grid.getTile(bombTile.getxIndex() + xIndicies[(int)(Math.random()*xIndicies.length)], bombTile.getyIndex());
            }
            else
            {
                bombTile = Globals.grid.getTile(bombTile.getxIndex(), bombTile.getyIndex() + yIndicies[(int)(Math.random()*yIndicies.length)]);
            }

            tiles.add(new Tile[] {bombTile});

            explosionTiles = new Tile[5];
            explosionTiles[0] = bombTile;
            xIndicies = getCross(bombTile.getxIndex() % 3);
            yIndicies = getCross(bombTile.getyIndex());
            tilesPlaced = 1;
            //place the tiles to the left and/or right of the bomb.
            for(int x : xIndicies)
            {
                explosionTiles[tilesPlaced] = Globals.grid.getTile(bombTile.getxIndex() + x, bombTile.getyIndex());
                tilesPlaced++;
            }
            //place the tiles above and/or below the bomb.
            for(int y : yIndicies)
            {
                explosionTiles[tilesPlaced] = Globals.grid.getTile(bombTile.getxIndex(), bombTile.getyIndex() + y);
                tilesPlaced++;
            }

            tiles.add(explosionTiles);
            setTiles(tiles);
        }
    }


    @Override public Firebomb clone()
    {
        return new Firebomb();
    }
}
