package joeshua.robotjack.Rendering;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;

import joeshua.robotjack.GameActivity;

/**
 * Created by joeshua on 4/21/2017.
 */

public class AnimationLibrary{

    private static Hashtable<String, Hashtable<String, AnimationBitmap>> library = null;
    private  AssetManager assets;
    private String[] objectTags;

    public AnimationLibrary()
    {
        if(AnimationLibrary.library == null)
        {
            load();
        }
        else
        {
            library = AnimationLibrary.library;
        }
    }

    private void load() {
        String[] tagsTemp;
        int i = 0;
        assets = GameActivity.getContext().getAssets();
        String root = "Animations";

        try {
            //List all the folders in the root directory. Populate that list into an Array
            objectTags = assets.list(root);
            for(i = 0; i < objectTags.length; ++i)
            {
                android.util.Log.i("objectTagszzzz:", "" + i + " " + objectTags[i]);
            }

            //create variables needed to dig deeper
            library = new Hashtable<String, Hashtable<String, AnimationBitmap>>(objectTags.length*3);
            String dimensions;
            float width;
            float height;
            Hashtable<String, AnimationBitmap> shelf;


            for(String objectTag : objectTags) {
                //list the subdirectory of this object's different defaultAnimation
                tagsTemp = assets.list(root + "/" + objectTag);

                for(i = 0; i < tagsTemp.length; ++i)
                {
                    android.util.Log.i("Internal tags:", "" + i + " " + tagsTemp[i]);
                }
                //calculate the dimensions based on the textfile at the end of the folder
                dimensions = tagsTemp[0]; //tagsTemp[tagsTemp.length - 1]
                width = Float.parseFloat("." + dimensions.split("_")[0]);
                height = Float.parseFloat("." + dimensions.split("_")[1]);
                android.util.Log.i("width/height", "" + width + "," + height);
                //create a shelf for this object
                shelf = new Hashtable<String, AnimationBitmap>((tagsTemp.length - 1)*3);

                //populate the shelf hashtable with the different animation bitmaps using their tags as pairs
                for(i = 1; i < tagsTemp.length; ++i)
                {
                    shelf.put(tagsTemp[i], new AnimationBitmap(root + "/" + objectTag + "/" + tagsTemp[i], width, height, true, tagsTemp[i]));
                }

                //populate the library with the different objects
                library.put(objectTag, shelf);

            }
        } catch (IOException e)
        {
            android.util.Log.e("IOException", "AnimationLibrary could not pull tags" + Log.getStackTraceString(e));
        }


    }

    public Hashtable<String, AnimationBitmap> getAnimations(String objectTag)
    {
        if(library.containsKey(objectTag)) {
            Hashtable<String, AnimationBitmap> assets = library.get(objectTag);
            for(AnimationBitmap temp : assets.values())
            {
                temp.init();
            }
            return assets;
        }

        else {
            android.util.Log.e("String mismatch", "objectTag passed to getDefaultAnimation was not found. objectTag: " + objectTag);
            return null;
        }

    }

    public Hashtable<String, AnimationBitmap> getUninitAnimations(String objectTag)
    {
        if(library.containsKey(objectTag)) {
            return library.get(objectTag);
        }

        else {
            android.util.Log.e("String mismatch", "objectTag passed to getDefaultAnimation was not found. objectTag: " + objectTag);
            return null;
        }

    }

    public String[] getAnimationTags(String objectTag)
    {
        if(library.containsKey(objectTag))
        {
            Object[] c = library.get(objectTag).values().toArray();
            String[] animationTags = new String[c.length];

            for( int i = 0 ; i < c.length; ++i)
            {
                animationTags[i] = ((AnimationBitmap) c[i]).getTag();
            }

            return animationTags;
        }
        else
        {
            android.util.Log.e("String Mismatch", "Object tag thrown to getAnimationTags was not found in library. objectTag given: " + objectTag);
            return null;
        }
    }
}
