package joeshua.robotjack.GameObjects;

import java.util.ArrayList;

import joeshua.robotjack.Globals;
import joeshua.robotjack.Rendering.AnimationBitmap;

/**
 * Created by joeshua on 4/13/2017.
 */

public class Grid {

    //bottom left tile is 0,0
    private Tile[][] grid = new Tile[6][3];
    private Tile[] linearGrid = new Tile[18];
    public static int width = 5;
    public static int height = 2;
    private AnimationBitmap warn;
    int ySpacing = Globals.screenHeight/4;
    int xSpacing = Globals.screenWidth/6;
    int x = 0;
    int y = Globals.screenHeight;

    public Grid()
    {
        warn = new AnimationBitmap("Independent/Warn", .1667f, .25f, true, "Warn");
        int linearPointer = 0;
        for(int i = 0; i  < grid.length; ++i)
        {
            x += xSpacing;
            for(int j = 0; j < grid[i].length; j++)
            {
                grid[i][j] = new Tile(x, y, i, j, warn);
                linearGrid[linearPointer] = grid[i][j];
                linearPointer++;
                y -= ySpacing;
            }
            y = Globals.screenHeight;
        }
    }

    public void placeObject(GameObject object, Tile tile)
    {
        if(object.getMovable())
        {
            Tile oldTile = object.getTile();
            if(oldTile != null)
            {
                oldTile.setEntity(null);
            }

            object.setTile(tile);
            tile.setEntity(object);
        }
    }

    public void placeObjectStatus(GameObject object, Tile tile)
    {
        Tile oldTile = object.getTile();
        if(oldTile != null)
        {
            oldTile.setEntity(null);
        }

        object.setTile(tile);
        tile.setEntity(object);
    }

    public void placeObject(GameObject object, int x, int y)
    {
        placeObject(object, getTile(x, y));
    }

    public Tile getTile(int x, int y)
    {
        if(x < 0 || x > width || y < 0 || y > height) return null;
        else return grid[x][y];
    }

    public Tile getNearestTile(int x, int y, boolean bindLeft)
    {
        if(grid == null) return null;

        int xindex = -1;
        int yindex = -1;

        int i = 0;
        int xCheck = (bindLeft) ? 2 : 5;
        for(i = 0; i <= xCheck; ++i)
        {
            if(x < grid[i][0].getX())
            {
                xindex = i;
                break;
            }
        }
        for(i = height; i >= 0; --i)
        {
            if(y > grid[0][i].getY() - Globals.screenHeight/4)
                yindex = i;
        }

        if(xindex < 0 || yindex < 0) return null;
        else return grid[xindex][yindex];
    }

    public ArrayList<Tile> getEmptyTiles(boolean enemy, int minRange, int maxRange)
    {
        if(enemy && (minRange == -1 || maxRange == -1))
        {
            minRange = 9;
            maxRange = 17;
        }
        else if(!enemy && (minRange == -1 || maxRange == -1))
        {
            minRange = 0;
            maxRange = 8;
        }

        ArrayList<Tile> emptyTiles = new ArrayList<>();

        for(int i = minRange; i <= maxRange; ++i)
        {
            if(linearGrid[i].getEntity() == null)
            {
                emptyTiles.add(linearGrid[i]);
            }
        }

        return emptyTiles;
    }

    public ArrayList<GameObject> scanRow(int row, int colStart, int colEnd, boolean first)
    {
        ArrayList<GameObject> objects = new ArrayList<>();
        if(row < 0 || row  > height || colStart < 0 || colStart > width || colStart > colEnd)
        {
            Globals.error("index out of bounds", "improper index passed to scanRow. row: "
                    + row + "colStart: " + colStart + "colEnd: " + colEnd);
        }
        else
        {
            for( int i = colStart; i <= colEnd; ++i)
            {
                GameObject o = grid[i][row].getEntity();
                if(o != null)
                {
                    objects.add(o);
                    if(first) return objects;
                }
            }
        }
        return objects;
    }

    public ArrayList<Tile> getEmptyTiles(boolean enemy)
    {
        return getEmptyTiles(enemy, -1, -1);
    }

    public ArrayList<GameObject> scanTiles(Tile[] tiles)
    {
        ArrayList<GameObject> objects = new ArrayList<>();
        GameObject o = null;
        for(Tile tile : tiles)
        {
            if(tile != null)
                o = tile.getEntity();
            if(o != null)
            {
                objects.add(o);
            }
        }
        return objects;
    }

    public void clearTile(Tile tile)
    {
        tile.getEntity().setTile(null);
        tile.setEntity(null);
    }

    public void clearTile(GameObject entity)
    {
        entity.getTile().setEntity(null);
        entity.setTile(null);
    }

    public Tile[] getLinearGrid()
    {
        return linearGrid;
    }

}
