package joeshua.robotjack.GameObjects;

import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 4/13/2017.
 */

public class Jack extends GameObject {

    public enum AnimationTypes {
        //order matters; first index will draw first, and so on.
        Body (0),
        Arms(1);

        private final int index;
        protected static final int INDEX_COUNT = 2;
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

    public Jack(Tile _tile) {
        super(_tile);
        animations = new AnimationPlayer[AnimationTypes.INDEX_COUNT];
        animations[AnimationTypes.Body.getIndex()] = new AnimationPlayer("Jack", "PlayerBodyIdle");
        animations[AnimationTypes.Arms.getIndex()] = new AnimationPlayer("Jack", "PlayerArmsIdle");
        animations[AnimationTypes.Arms.getIndex()].setSync(animations[AnimationTypes.Body.getIndex()]);
        defaultAnimation = animations[AnimationTypes.Body.getIndex()];
        initHealth(300);
    }

    public void upgradeHealth()
    {
        initHealth(getMaxHealth() + 80);
    }

}
