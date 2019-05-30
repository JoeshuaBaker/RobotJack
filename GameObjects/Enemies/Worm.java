package joeshua.robotjack.GameObjects.Enemies;

import java.util.ArrayList;

import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Managers.Abilities.Firebomb;
import joeshua.robotjack.Managers.Abilities.Leech;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 5/24/2017.
 */

public class Worm extends Enemy {

    private int succTimer = 0;
    private final int SUCC_MAX = 3;
    private int moveCounter = 0;
    private final int shootThreshold = 3;

    public Worm(Tile _tile)
    {
        super(_tile);
        initHealth(200);

        MAX_AI_TIMER = 120;
        AITimer = (int)Math.floor(MAX_AI_TIMER*Math.random());
        animations = new AnimationPlayer[1];
        defaultAnimation = new AnimationPlayer("Worm", "LeechIdle");
        animations[0] = defaultAnimation;
    }

    @Override
    public void AI()
    {
        if(getHealth() <= getMaxHealth()/2) MAX_AI_TIMER = 60;
        else MAX_AI_TIMER = 120;
        ArrayList<Tile> openTiles;
        Tile moveTo;

        if(moveCounter >= shootThreshold)
        {
            if(moveCounter == shootThreshold)
            {
                openTiles = Globals.grid.getEmptyTiles(true, 9, 11);
                if(openTiles.size() > 0) moveTo = openTiles.get( (int)Math.floor(Math.random()*openTiles.size()));
                else moveTo = null;
                if(moveTo == null)
                {
                    moveCounter = shootThreshold + 1;
                    return;
                }
                else
                {
                    Globals.grid.placeObject(this, moveTo);
                }
                defaultAnimation.playAnimationByTag("LeechChannel", true);
                Leech leech = new Leech();
                leech.setup(false, this);
                Globals.deck.addAbility(leech);
                AITimer = 90;
                moveCounter = shootThreshold + 1;
            }
            else
            {
                defaultAnimation.playAnimationByTag("LeechIdle", true);
                moveCounter = 0;
                AITimer = 30;
            }

        }
        else
        {
            openTiles = Globals.grid.getEmptyTiles(true);
            moveTo = openTiles.get( (int)Math.floor(Math.random()*openTiles.size()));
            Globals.grid.placeObject(this, moveTo);
            moveCounter++;
        }
    }

    @Override
    public void getHit(int damage)
    {
        if(damage >= getHealth())
        {
            setHealth(0);
        }
        else
        {
            setHealth(getHealth() - damage);
        }
    }
}
