package joeshua.robotjack;

import joeshua.robotjack.GameObjects.Jack;
import joeshua.robotjack.GameObjects.Grid;
import joeshua.robotjack.Managers.*;
import joeshua.robotjack.Rendering.AnimationLibrary;

/**
 * Created by joeshua on 4/11/2017.
 */

public class Globals {

    public static int screenWidth;
    public static int screenHeight;
    public static Jack player;
    public static Grid grid;
    public static Input input;
    public static AnimationLibrary animationLibrary;
    public static Deck deck;
    /*
    put static class members here, like input booleans and the player and a refrence to the grid
     */

    public static void initActivityGlobals(AnimationLibrary _animationLibrary)
    {
        animationLibrary = _animationLibrary;
    }

    public static void initDependentGlobals(int _screenWidth, int _screenHeight)
    {
        screenWidth = _screenWidth;
        screenHeight = _screenHeight;
    }

    public static void initGlobals(Jack _player, Grid _grid, Input _input, Deck _deck)
    {
        player = _player;
        grid = _grid;
        input = _input;
        deck = _deck;
    }

    public static void error(String label, String description)
    {
        android.util.Log.e(label, description);
    }
}
