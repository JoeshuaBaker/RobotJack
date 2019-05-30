package joeshua.robotjack.GameObjects.Enemies;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;

/**
 * Created by joeshua on 4/20/2017.
 */

public class Enemy extends GameObject {

    private boolean dead;
    protected boolean AI;
    protected int AITimer;
    protected int MAX_AI_TIMER;

    public Enemy(Tile _tile)
    {
        super(_tile);
        AI = false;
    }

    public void update()
    {
        super.update();
        if(getHealth() == 0)
        {
            die();
        }
        if(AI)
        {
            --AITimer;
            if(AITimer <= 0)
            {
                AITimer = MAX_AI_TIMER;
                AI();
            }
        }
    }

    //define in subclasses
    public void AI()
    {

    }

    private void die()
    {
        Globals.grid.clearTile(this);
        dead = true;
    }

    public boolean isDead()
    {
        return dead;
    }

    public void disableAI()
    {
        AI = false;
    }

    public void enableAI()
    {
        AI = true;
    }

    public boolean getAI()
    {
        return AI;
    }
}
