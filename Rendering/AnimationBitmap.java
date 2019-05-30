package joeshua.robotjack.Rendering;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import joeshua.robotjack.GameActivity;
import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.Globals;

/**
 * Created by joeshua on 4/13/2017.
 */

public class AnimationBitmap {


    //get program context, get asset manager, put all assets for an animation in a subdirectory
    //Format the filenames as defaultAnimation/<This Animation>/<Name>_<Frame>_<Linger>
    //call assetManager.list(), store it to a string array
    //create a bitmapFactory object,
    // call bitmapFactory.decodeStream(assetManager.open(myStringArray[i]));x
    // either create 2 arrays of the same size, one for bitmaps, the other for linger time.
    // index will represent the frame
    private int frameCount;
    private Bitmap[] frames;
    private int[] linger;

    //variables with getters
    private float width;
    private float height;
    private String tag;

    private AssetManager assets;
    private String subdirectory;
    public boolean init = false;

    public AnimationBitmap(String _subdirectory, float _width, float _height, boolean _loops, String _tag) {

        width = _width;
        height = _height;
        tag = _tag;

        assets = GameActivity.getContext().getAssets();
        subdirectory = _subdirectory;

        //android.util.Log.i("bitmap init ms", "" + (System.currentTimeMillis() - initTime));
    }

    public void init()
    {
        if(!init)
        {
            boolean initFlag = initArrays(subdirectory, assets);

            if(!initFlag) {
                frameCount = -1;
                android.util.Log.e("Missing Directory", "Directory lookup for AnimationBitmap failed");
                return;
            }

            init = true;
        }
    }

    private boolean initArrays(String subdirectory, AssetManager assets) {
        String[] assetIDs;
        BitmapFactory bitmapFactory = new BitmapFactory();

        try {
            //we start by generating a list of strings of all the files in the provided subdirectory
            assetIDs = assets.list(subdirectory);

            //using that list's length, we can determine how many frames are in this animation.
            frameCount = assetIDs.length;
            frames = new Bitmap[frameCount];
            linger = new int[frameCount];
            String temp; //holder string for snipping purposes

            String dimensions;
            dimensions = assetIDs[0];
            int i = 0;
            int index = 0;

            if(dimensions.length() >= 3 && dimensions.substring(dimensions.length() - 4).equalsIgnoreCase(".txt"))
            {
                width = Float.parseFloat("." + dimensions.split("_")[0]);
                height = Float.parseFloat("." + dimensions.split("_")[1]);
                i = 1;
            }

            for ( ; i < assetIDs.length; ++i) {
                //for each element in this list, we open the file as an InputStream and decode that into a bitmap
                int frameNum = Integer.parseInt(assetIDs[i].split("_")[1]) - 1;
                android.util.Log.i("assetIDs", assetIDs[i]);
                frames[frameNum] = Bitmap.createScaledBitmap(bitmapFactory.decodeStream(assets.open(subdirectory + "/" + assetIDs[i])), Math.round(Globals.screenWidth*width), Math.round(Globals.screenHeight*height), false);
                //to set the amount of linger, we break down the filename into its composite parts
                //the string after the second underscore is how many frames we should linger for.
                temp = assetIDs[i].split("_")[2];
                temp = temp.substring(0, temp.length() - 4);
                linger[frameNum] = Integer.parseInt(temp);
                index++;
            }
            return true;
        } catch (IOException e) {
            android.util.Log.e("IOException:", "From AnimationBitmap file directory lookup. Subdirectory passed:" + subdirectory + Log.getStackTraceString(e));
            return false;
        }
    }

    //getters

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getTag() {
        return tag;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public Bitmap[] getFrames() {
        return frames;
    }

    public int[] getLinger() {
        return linger;
    }

    public String toString() {

        String composite = "Frame count: " + frameCount + ", Frames.length: " + frames.length + ", tag: " + tag;
        return composite;
    }

    public String lingerData() {
        String composite = "Linger times:";
        for(int i = 0; i < linger.length; ++i)
        {
            composite += ", " + linger[i];
        }
        return composite;
    }
}