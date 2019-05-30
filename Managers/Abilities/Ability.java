package joeshua.robotjack.Managers.Abilities;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Rendering.AnimationBitmap;
import joeshua.robotjack.Rendering.AnimationPlayer;

/**
 * Created by joeshua on 4/21/2017.
 */

public class Ability {

    protected enum DamageType {
        Mechanical,
        Energy,
        Explosive;
    }

    private AbilityIcon icon;
    protected AnimationPlayer animations;
    private GameObject source;
    private boolean friendly;
    private boolean shuffled;
    private LinkedList<Tile[]> tiles;
    private LinkedList<Instruction> instructions;
    private DamageType damageType;
    private int damage;
    private ArrayList<GameObject> targets;
    private int sourceY;
    protected int MAX_COOLDOWN;
    private int cooldown;
    public Tile sourceTile;

    public Ability()
    {
        MAX_COOLDOWN = 0;
        cooldown = 0;
    }

    public void setup(boolean _isFriendly, GameObject _source)
    {
        //define in subclasses
        friendly = _isFriendly;
        source = _source;
        sourceY = _source.getTile().getyIndex();
    }

    public void update()
    {
        if(cooldown > 0)
        {
            --cooldown;
        }
    }

    @Override
    public Ability clone()
    {
        return new Ability();
    }

    public void setSource(GameObject source) {
        this.source = source;
    }

    public GameObject getSource() {
        return source;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    public LinkedList<Tile[]> getTiles() {
        return tiles;
    }

    public void setTiles(LinkedList<Tile[]> tiles) {
        this.tiles = tiles;
    }

    public Tile[] getFirstTile() {return tiles.getFirst();}

    public Tile[] removeFirstTile()
    {
        return tiles.removeFirst();
    }

    public void addTileFirst(Tile[] newTile)
    {
        if(tiles != null)
        {
            tiles.addFirst(newTile);
        }
    }

    public LinkedList<Instruction> getInstructions() {
        return instructions;
    }

    public Instruction removeFirstInstruction()
    {
        return instructions.removeFirst();
    }

    public Instruction getFirstInstruction()
    {
        return instructions.getFirst();
    }

    public Instruction peekFirst()
    {
        return instructions.peekFirst();
    }

    public void setInstructions(LinkedList<Instruction> instructions) {
        this.instructions = instructions;
    }

    public AbilityIcon getIcon() {
        return icon;
    }

    public void setIcon(AbilityIcon icon) {
        this.icon = icon;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public ArrayList<GameObject> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<GameObject> targets) {
        this.targets = targets;
    }

    public void clearTargets()
    {
        targets.clear();
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public AnimationPlayer getAnimations() { return animations;}

    public int getMAX_COOLDOWN() {
        return MAX_COOLDOWN;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public boolean isShuffled() {
        return shuffled;
    }

    public void setShuffled(boolean shuffled) {
        this.shuffled = shuffled;
    }

    public int getSourceY() {
        return sourceY;
    }
}
