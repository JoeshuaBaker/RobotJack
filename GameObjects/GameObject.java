package joeshua.robotjack.GameObjects;

import joeshua.robotjack.Globals;
import joeshua.robotjack.Managers.Abilities.StatusEffect;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 4/13/2017.
 */

public class GameObject {
    //to be inherited by GameObjects
    private Tile tile;
    protected AnimationPlayer[] animations;
    protected AnimationPlayer defaultAnimation;
    private boolean movable = true;
    private int maxHealth = 0;
    private int health = 0;
    private StatusEffect status = StatusEffect.None;
    private int statusTimer = 0;

    public GameObject()
    {

    }

    public GameObject(Tile _tile)
    {
        super();
        setTile(_tile);
        if(tile.getEntity() != null)
        {
            android.util.Log.e("assigned taken tile", "from GameObject initTile. Tile entity: " + tile.getEntity());
        }
        tile.setEntity(this);
    }

    public void update()
    {
        if(status != StatusEffect.None)
        {
            android.util.Log.i("status: ", status.toString());
            switch(status)
            {
                case Pull:
                    if(statusTimer % 10 == 0)
                    {
                        if(tile.getxIndex() < 2)
                        {
                            Tile moveTile = Globals.grid.getTile(tile.getxIndex() + 1, tile.getyIndex());
                            if(moveTile.getEntity() == null)
                            {
                                Globals.grid.placeObjectStatus(this, moveTile);
                            }
                        }
                        else if(tile.getxIndex() > 3)
                        {
                            Tile moveTile = Globals.grid.getTile(tile.getxIndex() - 1, tile.getyIndex());
                            if(moveTile.getEntity() == null)
                            {
                                Globals.grid.placeObjectStatus(this, moveTile);
                            }
                        }
                    }
                    break;
                case Push:
                    if(statusTimer % 10 == 0)
                    {
                        if(tile.getxIndex() > 2 && tile.getxIndex() < 5)
                        {
                            Tile moveTile = Globals.grid.getTile(tile.getxIndex() + 1, tile.getyIndex());
                            if(moveTile.getEntity() == null)
                            {
                                Globals.grid.placeObjectStatus(this, moveTile);
                            }
                        }
                    }
                    break;

            }

            statusTimer--;
            if(statusTimer == 0) setStatus(StatusEffect.None, 0);
        }
    }

    public void setStatus(StatusEffect effect, int timer)
    {
        statusTimer = timer;
        status = effect;
        switch(effect)
        {
            case Bind:
            case Push:
            case Pull:
                movable = false;
                break;

            case None:
                movable = true;
                break;
        }
    }

    public void getHit(int damage)
    {
        if(damage >= health)
        {
            health = 0;
        }
        else
        {
            health -= damage;
        }
        android.util.Log.i("enemy hit", "the enemy got hit yay");
    }

    public void heal(int amount)
    {
        if(amount + health > maxHealth)
        {
            health = maxHealth;
        }
        else
        {
            health += amount;
        }
    }

    //getters and setters

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public AnimationPlayer getDefaultAnimation() {
        return defaultAnimation;
    }

    public AnimationPlayer[] getAnimations() {
        return animations;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void initHealth(int health) {
        this.health = health;
        maxHealth = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMovable(boolean _movable)
    {
        movable = _movable;
    }

    public boolean getMovable()
    {
        return movable;
    }
}
