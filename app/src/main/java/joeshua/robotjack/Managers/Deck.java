package joeshua.robotjack.Managers;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import joeshua.robotjack.GameObjects.GameObject;
import joeshua.robotjack.GameObjects.Jack;
import joeshua.robotjack.GameObjects.Tile;
import joeshua.robotjack.Globals;
import joeshua.robotjack.Managers.Abilities.Ability;
import joeshua.robotjack.Managers.Abilities.BasicAttack;
import joeshua.robotjack.Managers.Abilities.Instruction;
import joeshua.robotjack.Managers.Abilities.InstructionType;
import joeshua.robotjack.Managers.Abilities.Lightning;

/**
 * Created by joeshua on 4/21/2017.
 */

public class Deck {

    private LinkedList<Ability> activeAbilities;
    private LinkedList<Ability> abilityGraveyard;
    private Ability[] hand;
    private ArrayList<Ability> deck;
    private int shootCD = 0;
    private int shootMax = 3;

    public Deck()
    {
        activeAbilities = new LinkedList<Ability>();
        abilityGraveyard = new LinkedList<>();
        hand = new Ability[5];
    }

    public void refreshDeck(ArrayList<Ability> fullDeck)
    {
        ArrayList<Ability> newDeck = new ArrayList<>();
        for(Ability a : fullDeck) {
            newDeck.add(a.clone());
        }
        deck = newDeck;
        Collections.shuffle(deck);
        int handSize = hand.length;
        if(deck.size() > 0)
        {
            while(handSize > 0)
            {
                hand[handSize - 1] = (deck.size() > 0) ? deck.remove(deck.size() - 1) : null;
                handSize--;
            }
        }
    }

    public void update()
    {
        //Handle abilities currently in the queue
        Instruction current;
        for(Ability ability : activeAbilities)
        {
            if(ability.peekFirst() == null)
            {
                abilityGraveyard.add(ability);
                continue;
            }
            else
            {
                current = ability.getFirstInstruction();
            }

            if(!current.isExecuted())
            {
                execute(ability, current);
            }

            if(current.isFinished())
            {
                ability.removeFirstInstruction();
            }
            else
            {
                current.tickDown();
            }
        }

        for(Ability ability : abilityGraveyard)
        {
            activeAbilities.remove(ability);
        }
        abilityGraveyard.clear();

        //Tick down cooldowns for abilities
        for(int i = 0; i < hand.length; ++i)
        {
            if(hand[i] != null && hand[i].getCooldown() > 0)
            {
                hand[i].setCooldown(hand[i].getCooldown() - 1);
            }

            //add code here to swap bitmap to the active one if cooldown became 0
            //code will probably need to be here to update the cooldown bar as well
        }

        //take inputs from player and respond to them
        handleInputs();

    }

    @NonNull
    public void execute(Ability ability, Instruction instruction)
    {
        instruction.setExecuted(true);
        int x;
        int y;
        boolean flag = false;
        ArrayList<GameObject> objects;

        switch(instruction.getType())
        {
            case Wait:
                break;
            case Warn:
                for(Tile tile : ability.getFirstTile())
                {
                    tile.setWarn(instruction.getPlacement());
                }
                break;
            case Listen:
                instruction.setExecuted(false);
                execute(ability, new Instruction(InstructionType.ScanTile, 0));
                if(!ability.getTargets().isEmpty())
                {
                    instruction.tickDown(instruction.getCurrentDelay());
                    instruction.setExecuted(true);
                }
                break;
            case ScanRowFirst:
                flag = true;
            case ScanRow:
                if(instruction.getPlacement() == -99)
                    y = ability.getSourceY();
                else
                    y = instruction.getPlacement();

                if(ability.isFriendly())
                    objects = Globals.grid.scanRow(y, 3, 5, flag);
                else
                    objects = Globals.grid.scanRow(y, 0, 2, flag);

                ability.setTargets(objects);
                break;

            case ScanCol:
                break;
            case ScanTile:
                ability.setTargets(Globals.grid.scanTiles(ability.getFirstTile()));
                break;
            case NextTile:
                ability.removeFirstTile();
                break;
            case Damage:
                objects = ability.getTargets();
                for(GameObject object : objects)
                {
                    object.getHit(ability.getDamage());
                }
                break;
            case InflictStatus:
                objects = ability.getTargets();
                for(GameObject object : objects)
                {
                    object.setStatus(instruction.getStatus(), instruction.getPlacement());
                }
                break;

            case HealSource:
                objects = ability.getTargets();
                if(!objects.isEmpty() && ability.getSource() != null)
                {
                    if(instruction.getPlacement() == -99)
                    {
                        ability.getSource().heal(ability.getDamage());
                    }
                    else
                    {
                        ability.getSource().heal(instruction.getPlacement());
                    }
                }

                break;
            case BasicAttackCD:
                objects = ability.getTargets();
                if(!objects.isEmpty() && hand[2] != null && hand[2].getCooldown() > 0)
                {
                    if(hand[2].getCooldown() - 3 >= 0)
                        hand[2].setCooldown(hand[2].getCooldown() - 3);
                    else
                        hand[2].setCooldown(0);

                }
                break;

            case And:
                LinkedList<Instruction> list = ability.getInstructions();
                int index = list.indexOf(instruction);
                int cycles = (instruction.getPlacement() == -99) ? 2 : instruction.getPlacement();
                Instruction current;
                for(int i = 1; i <= cycles; ++i)
                {
                    //android.util.Log.i("AND size/index/cycle: ", "size: " + list.size() + ", index: " + index + ", cycle: " + i);
                    current = list.get(index + 1);
                    execute(ability, current);
                    list.remove(current);
                }
                break;
            case PlayAnimation:
                ability.getAnimations().playAnimationByTag(instruction.getTag(), false);
                break;

            case AddAnimationTile:
                Tile[] currentTile = ability.getFirstTile();
                for( Tile tile : currentTile)
                {
                    if(tile != null)
                        tile.setAnimation(ability.getAnimations(), ability.isFriendly());
                }
                break;

            case AddAnimationSourceTile:
                if(ability.sourceTile != null)
                {
                    ability.sourceTile.setAnimation(ability.getAnimations(), ability.isFriendly());
                }
                break;

            case FinishAnimation:
                ability.getAnimations().finish();
                break;

            case GetPlayerTile:
                if(Globals.player != null && Globals.player.getTile() != null)
                    ability.addTileFirst(new Tile[] {Globals.player.getTile()});
                break;

            default:
                android.util.Log.e("instruction default", "switch did not work correctly");
            break;
        }
    }

    //remove this after deck system is in
    public void addAbility(Ability ability)
    {
        activeAbilities.add(ability);
    }

    private Ability getFocusAbility()
    {
        return hand[2];
    }

    private void slideLeft()
    {
        Ability temp;
        temp = hand[hand.length - 1];
        hand[hand.length - 1] = hand[0];
        for(int i = 1; i < hand.length - 1; ++i)
        {
            hand[i - 1] = hand[i];
        }
        hand[hand.length - 2] = temp;

        //PSEUDOCODE
        //0, 1, 2, 3, 4,
        //temp = 4, hand[4] = 0
        // step 1: hand[0] = 1 // i = 1
        // step 2: hand[1] = 2 // i = 2
        // step 3: hand[2] = 3; // i = 3
        // post loop: hand[3] = temp, temp = 4
        // result: 1, 2, 3, 4, 0
    }

    private void slideRight()
    {
        Ability temp;
        temp = hand[0];
        hand[0] = hand[hand.length - 1];
        for(int i = hand.length - 1; i > 1; --i)
        {
            hand[i] = hand[i - 1];
        }
        hand[1] = temp;

        //PSEUDOCODE
        //0, 1, 2, 3, 4
        //hand[0] = 4, temp = 0.
        //step 1: hand[4] = 3; i = 4
        //step 2: hand[3] = 2; i = 3
        //step 3: hand[2] = 1; i = 2
        // post loop: hand[1] = 0;
        //result: 4, 0, 1, 2, 3
    }

    private void executeFocus()
    {
        Ability focus = getFocusAbility();
        if(focus != null && focus.getCooldown() <= 0)
        {
            focus.setup(true, Globals.player);
            activeAbilities.add(focus);
            replaceFocus();
            Globals.player.getAnimations()[Jack.AnimationTypes.Arms.getIndex()].playAnimationByTagExclusive("PlayerAbility", false);
        }
    }

    private void replaceFocus()
    {
        Ability focus = getFocusAbility();
        hand[2] = (deck.isEmpty()) ? null : deck.remove(deck.size() - 1);
        if(hand[2] != null && focus != null)
        {
            hand[2].setCooldown(focus.getMAX_COOLDOWN());
        }
    }

    private void handleInputs()
    {

        if(Globals.input.pullInput(0))
        {
            //up swipe
            executeFocus();
        }
        if(Globals.input.pullInput(1))
        {
            //right swipe
            slideRight();
        }
        if(Globals.input.pullInput(2))
        {
            //down swipe
            //executeFocus(true);
        }
        if(Globals.input.pullInput(3))
        {
            //left swipe
            slideLeft();
        }

        if(Globals.input.getHold()[2])
        {
            if(shootCD == 0)
            {
                addAbility(new BasicAttack(Globals.player));
                shootCD = shootMax;
            }
            else
            {
                shootCD--;
            }

            Globals.player.getAnimations()[Jack.AnimationTypes.Arms.getIndex()].playAnimationByTagExclusive("PlayerShoot", false);
        }
        if(Globals.input.pullMoveTrigger())
        {
            Lightning temp;
            //android.util.Log.i("Lightning", "Move trigger pulled as true");
            for(int i = 0; i < hand.length; ++i)
            {
                if(hand[i] instanceof Lightning)
                {
                    temp = (Lightning) hand[i];
                    temp.getCharge();
                    //android.util.Log.i("Lightning", "gained charge");
                }
            }
        }
    }

    public void addHand(Ability newAbility, int index)
    {
        if(index < 5)
        {
            hand[index] = null;
            hand[index] = newAbility;
        }
    }

    public void addDeck(Ability newAbility)
    {
        if(newAbility != null)
        {
            deck.add(newAbility);
        }
    }

    public LinkedList getActiveAbilities()
    {
        return activeAbilities;
    }
    public Ability[] getHand() { return hand; }
}
