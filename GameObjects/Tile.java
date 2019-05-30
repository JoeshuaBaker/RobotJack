package joeshua.robotjack.GameObjects;

import android.graphics.Bitmap;

import joeshua.robotjack.Rendering.AnimationBitmap;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 4/17/2017.
 */

public class Tile {

    //bottom right edge of the tile:
    private int x;
    private int y;
    private int xIndex;
    private int yIndex;
    private GameObject entity;
    private AnimationPlayer animation;
    private AnimationBitmap warn;
    private boolean hasAnimation = false;
    private boolean warnState = false;
    private int warnTimer = 0;
    private boolean isFriendly;

    public Tile(int _x, int _y, int _xIndex, int _yIndex, AnimationBitmap _warn)
    {
        x = _x;
        y = _y;
        xIndex = _xIndex;
        yIndex = _yIndex;
        warn = _warn;
        warn.init();
        isFriendly = false;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public GameObject getEntity() {
        return entity;
    }

    public void setEntity(GameObject entity) {
        this.entity = entity;
    }

    public int getxIndex() {
        return xIndex;
    }

    public void setxIndex(int xIndex) {
        this.xIndex = xIndex;
    }

    public int getyIndex() {
        return yIndex;
    }

    public void setyIndex(int yIndex) {
        this.yIndex = yIndex;
    }

    public boolean hasAnimation()
    {
        return hasAnimation;
    }

    public void setAnimation(AnimationPlayer newAnimation, boolean _isEnemy)
    {
        if(animation != null) {
            clearAnimation();
        }
        animation = newAnimation;
        hasAnimation = true;
        isFriendly = _isEnemy;
    }

    public void clearAnimation()
    {
        animation = null;
        hasAnimation = false;
        isFriendly = false;
    }

    public void setWarn(int time)
    {
        warnState = true;
        if(time > warnTimer)
            warnTimer = time;
    }

    public boolean getWarn()
    {
        return warnState;
    }

    public Bitmap updateWarn()
    {
        warnTimer--;
        if(warnTimer <= 0) warnState = false;
        return warn.getFrames()[0];
    }

    public AnimationPlayer getAnimation()
    {
        if(animation != null && animation.isFinished())
        {
            clearAnimation();
            android.util.Log.i("animation", "animation cleared from tile");
            return null;
        }
        return animation;
    }

    public boolean isFriendly() {
        return isFriendly;
    }
}
