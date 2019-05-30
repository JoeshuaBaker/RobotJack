package joeshua.robotjack.GameObjects.Enemies;

import java.util.ArrayList;

import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Managers.Abilities.Lightning;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 5/29/2017.
 */

public class Bubble extends Enemy {
    public enum AnimationTypes {
        //order matters; first index will draw first, and so on.
        Bubble(0),
        Bolt1(1),
        Bolt2(2),
        Bolt3(3),
        Bolt4(4),
        Bolt5(5),
        Bolt6(6),
        Bolt7(7),
        Bolt8(8),
        Face(9);



        private final int index;
        protected static final int INDEX_COUNT = 10;
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
    private boolean shoot = false;
    private boolean shootInit = false;
    private int shootTimer = 0;
    private int boltCounter = 0;
    private final int boltMax = 8;
    private int anger = 0;
    private final int angerThreshold = 30;
    private final int angerMax = angerThreshold*3;
    private final int boltFrames = 47;

    public Bubble(Tile _tile)
    {
            super(_tile);
            initHealth(200);
            MAX_AI_TIMER = 240;
            AITimer = (int)Math.floor(MAX_AI_TIMER*Math.random());


            animations = new AnimationPlayer[Bubble.AnimationTypes.INDEX_COUNT];

        animations[AnimationTypes.Bubble.getIndex()] = new AnimationPlayer("Bubble", "circle");
        animations[AnimationTypes.Face.getIndex()] = new AnimationPlayer("Bubble", "FaceOpen");
        animations[AnimationTypes.Bolt1.getIndex()] = new AnimationPlayer("Bubble", "Nothing");
        animations[AnimationTypes.Bolt2.getIndex()] = new AnimationPlayer("Bubble", "Nothing");
        animations[AnimationTypes.Bolt3.getIndex()] = new AnimationPlayer("Bubble", "Nothing");
        animations[AnimationTypes.Bolt4.getIndex()] = new AnimationPlayer("Bubble", "Nothing");
        animations[AnimationTypes.Bolt5.getIndex()] = new AnimationPlayer("Bubble", "Nothing");
        animations[AnimationTypes.Bolt6.getIndex()] = new AnimationPlayer("Bubble", "Nothing");
        animations[AnimationTypes.Bolt7.getIndex()] = new AnimationPlayer("Bubble", "Nothing");
        animations[AnimationTypes.Bolt8.getIndex()] = new AnimationPlayer("Bubble", "Nothing");

        defaultAnimation = animations[AnimationTypes.Face.getIndex()];
        /*
            animations[MissileMan.AnimationTypes.Missile.getIndex()] = new AnimationPlayer("RedBoy", "Nothing");
            animations[MissileMan.AnimationTypes.Pack.getIndex()] = new AnimationPlayer("RedBoy", "RedBoyIdleMissilePack");
            animations[MissileMan.AnimationTypes.Body.getIndex()] = new AnimationPlayer("RedBoy", "RedBoyIdleBody");
            animations[MissileMan.AnimationTypes.Arms.getIndex()] = new AnimationPlayer("RedBoy", "RedBoyIdleArms");
            defaultAnimation = animations[MissileMan.AnimationTypes.Body.getIndex()];

            animations[MissileMan.AnimationTypes.Pack.getIndex()].setSync(defaultAnimation);
            animations[MissileMan.AnimationTypes.Arms.getIndex()].setSync(defaultAnimation);*/
    }

        @Override
        public void AI()
        {
            if(shoot) {
                if (!shootInit) {
                    animations[AnimationTypes.Face.getIndex()].playAnimationByTag("FaceShoot", true);
                    shootTimer = boltCounter;
                    Lightning lightning = new Lightning();
                    lightning.setup(false, this, boltCounter);
                    Globals.deck.addAbility(lightning);
                    shootInit = true;
                    MAX_AI_TIMER = 60;
                    anger = 0;
                }
                shootTimer--;
                if(boltCounter > 0)
                {
                    animations[boltCounter].playAnimationByTag("Nothing", true);
                    boltCounter--;
                }
                if(shootTimer == 0)
                {
                    shoot = false;
                    shootInit = false;
                    animations[AnimationTypes.Face.getIndex()].playAnimationByTag("FaceOpen", true);
                    MAX_AI_TIMER = 240;
                }
            }
            else
            {
                ArrayList<Tile> openTiles = Globals.grid.getEmptyTiles(true);
                Tile moveTo = openTiles.get( (int)Math.floor(Math.random()*openTiles.size()));
                Globals.grid.placeObject(this, moveTo);
                addBolt();
                moveCounter++;

                if(boltCounter == boltMax)
                    shoot = true;
            }
        }

        private void addBolt()
        {
            if(boltCounter < boltMax)
            {
                boltCounter++;
                int i = 1;
                if(boltCounter != boltMax)
                {
                    for(i = 1; i <= boltCounter; ++i)
                    {
                        animations[i].playAnimationByTag("Bolt", (((boltFrames + 1)/3)/boltCounter)*3*i, true);
                    }
                }
                else
                {
                    int counter = 0;
                    for(i = 1; i <= boltMax; ++i)
                    {
                        animations[i].playAnimationByTag("Bolt", counter, true);
                        counter += 6;
                    }
                }
            }
            else
            {
                boltCounter = 8;
            }
        }

        @Override
        public void getHit(int damage)
        {
            if(!shoot)
            {
                anger += damage;
                if(anger < angerThreshold)
                {
                    animations[AnimationTypes.Face.getIndex()].playAnimationByTag("FaceOpen", animations[AnimationTypes.Face.getIndex()].getFramePointer(), true);
                }
                else if(anger < (2*angerThreshold))
                {
                    animations[AnimationTypes.Face.getIndex()].playAnimationByTag("FaceHmm", animations[AnimationTypes.Face.getIndex()].getFramePointer(), true);
                }
                else
                {
                    animations[AnimationTypes.Face.getIndex()].playAnimationByTag("FaceMad", animations[AnimationTypes.Face.getIndex()].getFramePointer(), true);
                }

                if(anger > angerMax)
                {
                    shoot = true;
                }
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
