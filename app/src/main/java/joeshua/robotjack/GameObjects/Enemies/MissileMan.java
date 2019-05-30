package joeshua.robotjack.GameObjects.Enemies;

import java.util.ArrayList;

import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Managers.Abilities.Firebomb;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 5/8/2017.
 */

public class MissileMan extends Enemy {

    public enum AnimationTypes {
        //order matters; first index will draw first, and so on.
        Missile(0),
        Pack(1),
        Body (2),
        Arms(3);


        private final int index;
        protected static final int INDEX_COUNT = 4;
        AnimationTypes(int _index)
        {
            index = _index;
        }

        public int getIndex()
        {
            return index;
        }

        //use AnimationTypes.values(); to get all the values.
    }

    private int moveCounter = 0;
    private int shootCounter = 0;
    private final int shootThreshold = 2;

    public MissileMan(Tile _tile)
    {
        super(_tile);
        initHealth(200);
        MAX_AI_TIMER = 180;
        AITimer = (int)Math.floor(MAX_AI_TIMER*Math.random());
        animations = new AnimationPlayer[AnimationTypes.INDEX_COUNT];
        animations[AnimationTypes.Missile.getIndex()] = new AnimationPlayer("RedBoy", "Nothing");
        animations[AnimationTypes.Pack.getIndex()] = new AnimationPlayer("RedBoy", "RedBoyIdleMissilePack");
        animations[AnimationTypes.Body.getIndex()] = new AnimationPlayer("RedBoy", "RedBoyIdleBody");
        animations[AnimationTypes.Arms.getIndex()] = new AnimationPlayer("RedBoy", "RedBoyIdleArms");
        defaultAnimation = animations[AnimationTypes.Body.getIndex()];

        animations[AnimationTypes.Pack.getIndex()].setSync(defaultAnimation);
        animations[AnimationTypes.Arms.getIndex()].setSync(defaultAnimation);
    }

    @Override
    public void AI()
    {
        if(moveCounter >= shootThreshold)
        {
            animations[AnimationTypes.Arms.getIndex()].playAnimationByTag("RedBoyFireArms", false);
            Firebomb firebomb = new Firebomb();
            if(shootCounter >= shootThreshold)
            {
                firebomb.setup(false, this, true);
                shootCounter = 0;
            }
            else
            {
                firebomb.setup(false, this);
                shootCounter++;
            }
            Globals.deck.addAbility(firebomb);
            moveCounter = 0;
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
