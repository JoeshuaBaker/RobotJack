package joeshua.robotjack;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

/**
 * Created by joeshua on 4/11/2017.
 */

public class GameView extends SurfaceView implements Runnable {
    volatile boolean playing;
    private Thread gameThread = null;
    private long previousFrameTime;
    private long frameTime;
    private long sleepTime;

    public GameView(Context context)
    {
        super(context);
        frameTime = System.currentTimeMillis();
        previousFrameTime = System.currentTimeMillis();
    }

    @Override
    public void run(){
        while(playing)
        {
            update();
            draw();
            control();
        }
    }
/*

    Function prototype: onTouchEvent will be on the gameView. For inputs on the right, there will
    be a motion tracker system. Inputs on the left will simply snap you to the closest grid space.
    Inputs on the left above the play area will simply be discarded.
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent)
    {
        switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:

                break;

            case MotionEvent.ACTION_DOWN:

                break;
        }
        return true;
    }
*/
    private void update()
    {
        /*
        gameobjects.move();


         */
    }

    private void draw()
    {

    }

    private void control()
    {
        //measure time between end of last frame and this one, sleep for the difference in time
        //between 17 (perfect execution) and that delta T, sleep for that much time to retain 60 fps
        frameTime = System.currentTimeMillis();
        sleepTime = 17 - (frameTime - previousFrameTime);
        if(sleepTime < 0) sleepTime = 0;

        try{
            gameThread.sleep(sleepTime);
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        previousFrameTime = System.currentTimeMillis();
    }

    //This will halt our game loop and manage the thread
    public void pause()
    {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e)
        {

        }
    }

    //This will restart our game loop and restart the thread
    public void resume()
    {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }
}
