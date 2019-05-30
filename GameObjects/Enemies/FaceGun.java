package joeshua.robotjack.GameObjects.Enemies;

import java.util.ArrayList;

import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Managers.Abilities.Firebomb;
import joeshua.robotjack.Managers.Abilities.Gatling;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 6/4/2017.
 */

public class FaceGun extends Enemy {

    private boolean armor = true;
    private int moveCounter = 0;
    private int shootCounter = 0;
    private final int shootThreshold = 2;

    private boolean shoot = false;
    private int shootStep = 0;

    public FaceGun(Tile _tile)
    {
        super(_tile);
        animations = new AnimationPlayer[1];
        animations[0] = new AnimationPlayer("FaceGun", "FaceGunIdle");
        defaultAnimation = animations[0];

        initHealth(250);
        MAX_AI_TIMER = 200;
        AITimer = (int)Math.floor(MAX_AI_TIMER*Math.random());
    }

    @Override
    public void AI(){
        if(shoot)
        {
            ++shootStep;
            switch(shootStep)
            {
                case 1:
                    defaultAnimation.playAnimationByTag("FaceGunEngage", false);
                    defaultAnimation.setDefaultAnimation("FaceGunSpin");
                    MAX_AI_TIMER = defaultAnimation.getCurrentLength();
                    break;

                case 2:
                    armor = false;
                    MAX_AI_TIMER = 180;
                    break;

                case 3:
                    Gatling gatling = new Gatling();
                    gatling.setup(false, this);
                    Globals.deck.addAbility(gatling);
                    defaultAnimation.playAnimationByTag("FaceGunFire", true);
                    MAX_AI_TIMER = 420;
                    break;

                case 4:
                    defaultAnimation.playAnimationReverse("FaceGunEngage", false);
                    defaultAnimation.setDefaultAnimation("FaceGunIdle");
                    MAX_AI_TIMER = defaultAnimation.getCurrentLength();
                    break;

                case 5:
                    armor = true;
                    defaultAnimation.playAnimationByTag("FaceGunIdle", true);
                    MAX_AI_TIMER = 200;
                    shootStep = 0;
                    shoot = false;
                    moveCounter = 0;
                    break;
            }

        }
        else if(moveCounter >= shootThreshold)
        {
            shoot = true;
        }
        else
        {
            ArrayList<Tile> openTiles = Globals.grid.getEmptyTiles(true);
            Tile moveTo = openTiles.get( (int)Math.floor(Math.random()*openTiles.size()));
            Globals.grid.placeObject(this, moveTo);
            moveCounter++;
        }
    }

    @Override
    public void getHit(int damage)
    {
        if(armor)
        {
            damage = damage/4;
        }

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
