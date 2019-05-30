package joeshua.robotjack.Rendering;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Hashtable;

import joeshua.robotjack.Globals;

/**
 * Created by joeshua on 4/21/2017.
 */

public class AnimationPlayer {
    private Hashtable<String, AnimationBitmap> animations;
    private AnimationBitmap currentAnimation;
    private AnimationBitmap defaultAnimation;
    private AnimationPlayer sync;
    private Bitmap[] currentFrames;
    private int[] currentLinger;
    private int framePointer = 0;
    private Bitmap currentFrame;
    private int linger = 0;
    private boolean loops = false;
    private boolean finished = false;
    private final boolean isAbility;
    private boolean reverse = false;

    public AnimationPlayer(String objectTag, String _defaultAnimation)
    {
        animations = Globals.animationLibrary.getAnimations(objectTag);
        defaultAnimation = animations.get(_defaultAnimation);
        playAnimationByTag(_defaultAnimation, true);
        isAbility = false;
    }

    public AnimationPlayer(String objectTag, String _defaultAnimation, boolean _isAbility)
    {
        animations = Globals.animationLibrary.getAnimations(objectTag);
        defaultAnimation = animations.get(_defaultAnimation);
        playAnimationByTag(_defaultAnimation, true);
        isAbility = _isAbility;
    }

    public void playAnimationReverse(String animationTag, boolean loop)
    {
        if(animations.containsKey(animationTag))
        {
            currentAnimation = animations.get(animationTag);
            int startFrame = currentAnimation.getFrameCount() - 1;
            currentFrames = currentAnimation.getFrames();
            currentLinger = currentAnimation.getLinger();
            framePointer = startFrame;
            currentFrame = currentFrames[startFrame];
            linger = currentLinger[startFrame];
            loops = loop;
            finished = false;
            reverse = true;
        }
        else
        {
            Globals.error("string mismatch", "tried to play an animationTag that was not found, animationTag: " + animationTag);
        }
    }

    public void playAnimationByTag(String animationTag, boolean loop)
    {
        playAnimationByTag(animationTag, 0, loop);
    }

    public void playAnimationByTag(String animationTag, int startFrame, boolean loop)
    {
        if(animations.containsKey(animationTag))
        {
            currentAnimation = animations.get(animationTag);
            currentFrames = currentAnimation.getFrames();
            currentLinger = currentAnimation.getLinger();
            framePointer = startFrame;
            currentFrame = currentFrames[startFrame];
            linger = currentLinger[startFrame];
            loops = loop;
            finished = false;
            reverse = false;
        }
        else
        {
            Globals.error("string mismatch", "tried to play an animationTag that was not found, animationTag: " + animationTag);
        }
    }

    public void playAnimationByTagExclusive(String tag, boolean loop)
    {
        if(!getCurrentTag().equalsIgnoreCase(tag))
        {
            playAnimationByTag(tag, loop);
        }
    }

    public void update()
    {
        if(currentAnimation == null)
        {
            Globals.error("null pointer", "currentAnimation in an animationPlayer was null");
        }
        else
        {
            if(!reverse)
            {
                linger--;
                if(linger <= 0)
                {
                    framePointer++;
                    if (framePointer >= currentFrames.length) {
                        if(isAbility) finished = true;
                        if(loops)
                        {
                            framePointer = 0;
                        }
                        else {
                            if(!finished)
                            {
                                if(sync != null)
                                {
                                    playAnimationByTag(defaultAnimation.getTag(), sync.getFramePointer(), true);
                                    linger = sync.getLinger();
                                }
                                else {
                                    playAnimationByTag(defaultAnimation.getTag(), true);
                                }
                            }
                            return;
                        }
                    }
                    currentFrame = currentFrames[framePointer];
                    linger = currentLinger[framePointer];
                }
            }
            else
            {
                linger--;
                if(linger <= 0) {
                    framePointer--;
                    if (framePointer <= 0) {
                        if (isAbility) finished = true;
                        if (loops) {
                            framePointer = currentFrames.length;
                        } else {
                            if (!finished) {
                                if (sync != null) {
                                    playAnimationByTag(defaultAnimation.getTag(), sync.getFramePointer(), true);
                                    linger = sync.getLinger();
                                } else {
                                    playAnimationByTag(defaultAnimation.getTag(), true);
                                }
                            }
                            return;
                        }
                    }
                    currentFrame = currentFrames[framePointer];
                    linger = currentLinger[framePointer];
                }
            }
        }
    }

    public void cleanUp()
    {
        finished = true;
        animations = null;
        currentFrame = null;
        currentFrames = null;
        currentAnimation = null;
        defaultAnimation = null;
        currentLinger = null;
        linger = 0;
        framePointer = 0;
    }

    public int getCurrentLength()
    {
        int output = 0;
        for(int i = 0; i < currentLinger.length; ++i)
        {
            output += currentLinger[i];
        }
        return output;
    }

    public Bitmap getCurrentFrame() {
        return currentFrame;
    }

    public String getCurrentTag() {
        return currentAnimation.getTag();
    }

    public AnimationBitmap getCurrentAnimation()
    {
        return currentAnimation;
    }

    public boolean isFinished() { return finished; }

    public void setDefaultAnimation(String animTag)
    {
        defaultAnimation = animations.get(animTag);
    }

    public void finish()
    {
        finished = true;
    }

    public void setSync(AnimationPlayer newSync) {sync = newSync;}

    public int getFramePointer()
    {
        return framePointer;
    }

    public int getLinger()
    {
        return linger;
    }

    public void jumpTo(int frame)
    {
        framePointer = frame;
        currentFrame = currentFrames[frame];
        linger = currentLinger[frame];
    }
}
