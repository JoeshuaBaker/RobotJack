package joeshua.robotjack.Managers;

import android.graphics.Point;
import android.view.MotionEvent;

import joeshua.robotjack.Globals;

/**
 * Created by joeshua on 4/18/2017.
 */

public class Input {

    //input variables for left half of screen.
    private int leftX;
    private int leftY;
    private boolean leftDown;
    private boolean moveTrigger;

    //input variables for right half of screen
    private boolean rightDown;
    private Point startSwipe;
    private Point currentSwipe;
    private final int SWIPE_DISTANCE_MIN;
    private final int BUFFER;
    private boolean[] inputs;
    private boolean[] hold;

    public Input()
    {
        leftX = -1;
        leftY = -1;
        leftDown = false;
        rightDown = false;
        inputs = new boolean[]{false, false, false, false};
        hold = new boolean[]{false, false, false, false};

        startSwipe = new Point(0, 0);
        currentSwipe = new Point(0, 0);
        SWIPE_DISTANCE_MIN = Globals.screenWidth/10;

        BUFFER = Globals.screenWidth/20;
    }

    public void handleInput(MotionEvent motionEvent)
    {
        int pointerCount = motionEvent.getPointerCount();
        int i = 0;

        int leftCount = 0;
        int rightCount = 0;
        for(i = 0; i < pointerCount; ++i)
        {
            if(motionEvent.getX(i) < Globals.screenWidth/2)
                leftCount++;
            else if(motionEvent.getX(i) >= Globals.screenWidth/2)
                rightCount++;
        }

        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                android.util.Log.i("pointer down triggered", "a");
                pointerCount = motionEvent.getPointerCount();
                for(i = 0; i < pointerCount; ++i)
                {
                    if(motionEvent.getX(i) < Globals.screenWidth/2)
                    {
                        //left down
                        if(!leftDown)
                        {
                            leftDown = true;
                            leftX = (int)motionEvent.getX(i);
                            leftY = (int)motionEvent.getY(i);
                        }
                    }
                    else if(motionEvent.getX(i) >= Globals.screenWidth/2)
                    {
                        //right down
                        android.util.Log.i("right down triggered", "a");
                        if(!rightDown)
                        {
                            rightDown = true;
                            startSwipe.set((int)motionEvent.getX(i), (int)motionEvent.getY(i));
                            currentSwipe.set((int)motionEvent.getX(i), (int)motionEvent.getY(i));
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                pointerCount = motionEvent.getPointerCount();
                android.util.Log.i("pointer up registered", "a");

                MotionEvent.PointerCoords coords = new MotionEvent.PointerCoords();

                i = motionEvent.getActionIndex();
                    motionEvent.getPointerCoords(i, coords);
                    if(coords.x < Globals.screenWidth/2)
                    {
                        //left up
                        leftCount--;
                        if(leftCount <= 0)
                        {
                            leftCount = 0;
                            leftDown = false;
                            leftX = -1;
                            leftY = -1;
                        }
                    }
                    else if(coords.x >= Globals.screenWidth/2)
                    {
                        //right up
                        rightCount--;
                        if(rightCount <= 0)
                        {
                            rightDown = false;
                            clearHold();
                            startSwipe.set(0, 0);
                            currentSwipe.set(0,0);
                        }
                    }
                break;

            case MotionEvent.ACTION_MOVE:
                pointerCount = motionEvent.getPointerCount();
                boolean leftMoved = false;
                for(i = 0; i < pointerCount; ++i)
                {
                    if(motionEvent.getX(i) < Globals.screenWidth/2)
                    {

                        if(motionEvent.getX(i) > Globals.screenWidth/2 - BUFFER &&
                           rightCount == 0 && rightDown)
                        {
                            //right swipe moved off the screen onto the left.
                            //run code for resolving swipe
                            android.util.Log.i("swipe moved to left", "");
                            resolveSwipe();
                            clearHold();
                        }

                        //left move
                        if(!leftMoved)
                        {
                            //only move the first pointer on the left.
                            leftMoved = true;
                            leftDown = true;
                            leftX = (int)motionEvent.getX(i);
                            leftY = (int)motionEvent.getY(i);
                        }

                    }
                    else if(motionEvent.getX(i) >= Globals.screenWidth/2)
                    {
                        //right move
                        if(motionEvent.getX(i) < Globals.screenWidth/2 + BUFFER && leftCount == 0)
                        {
                            //the last pointer could have moved off the left and onto the right
                            leftDown = false;
                            leftX = -1;
                            leftY = -1;
                        }

                        if(rightDown)
                        {
                            currentSwipe.set((int)motionEvent.getX(i), (int)motionEvent.getY(i));
                            if(distance(startSwipe, currentSwipe) > SWIPE_DISTANCE_MIN)
                            {
                                resolveSwipe();
                            }
                        }
                    }
                }
                break;
        }
    }

    public boolean checkRightBuffer()
    {
        //checking to see if a swipe escaped the screen. if so, resolve the swipe early.
        return (currentSwipe.x > Globals.screenWidth - BUFFER ||
                 currentSwipe.y < BUFFER ||
                 currentSwipe.y > Globals.screenHeight - BUFFER);
    }

    public void resolveSwipe()
    {
        //checking the starting point and the ending point to determine swipe direction
        String test;
        if(Math.abs(currentSwipe.y - startSwipe.y) > Math.abs(currentSwipe.x - startSwipe.x))
        {
            //vertical swipe
            if(currentSwipe.y > startSwipe.y)
            {
                //downward swipe
                inputs[2] = true;
                hold[2] = true;
            }
            else
            {
                //upward swipe
                inputs[0] = true;
                hold[0] = true;
            }
        }
        else
        {
            //horizontal swipe
            if(currentSwipe.x > startSwipe.x)
            {
                //rightward swipe
                inputs[1] = true;
                hold[1] = true;
            }
            else
            {
                //leftward swipe
                inputs[3] = true;
                hold[3] = true;
            }
        }

        rightDown = false;
        startSwipe.set(0,0);
        currentSwipe.set(0, 0);
    }

    public int distance(Point p1, Point p2)
    {
        return((int)Math.sqrt((p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y)));
    }
    public boolean[] getInputs() { return inputs; }

    public boolean[] getHold() { return hold;}

    public boolean anyHeld() { return (hold[0] || hold[1] || hold[2] || hold[3]);}

    public void clearHold() {
        hold[0] = hold[1] = hold[2] = hold[3] = false;
    }

    public boolean pullInput(int index) {
        if(index >= inputs.length || index < 0)
        {
            android.util.Log.e("out of bounds", "incorrect index thrown to pullInput."
                                                    + "index passed: " + index);
            return false;
        }
        else
        {
            boolean returnValue = inputs[index];
            inputs[index] = false;
            return returnValue;
        }
    }

    public boolean pullInput(String direction)
    {
        if(direction.equalsIgnoreCase("up"))
        {
            return pullInput(0);
        }
        else if(direction.equalsIgnoreCase("right"))
        {
            return pullInput(1);
        }
        else if(direction.equalsIgnoreCase("down"))
        {
            return pullInput(2);
        }
        else if(direction.equalsIgnoreCase("left"))
        {
            return pullInput(3);
        }
        else
        {
            android.util.Log.e("string mismatch", "tag passed to pullindex does not match."
                                                     + " String passed: " + direction);
            return false;
        }
    }

    public int getLeftX() {
        return leftX;
    }

    public int getLeftY() {
        return leftY;
    }

    public boolean isLeftDown() {
        return leftDown;
    }

    public boolean pullMoveTrigger() {
        boolean temp = moveTrigger;
        moveTrigger = false;
        return temp;
    }

    public void setMoveTrigger(boolean moveTrigger) {
        this.moveTrigger = moveTrigger;
    }
}
