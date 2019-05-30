package joeshua.robotjack.Managers.Abilities;

/**
 * Created by joeshua on 4/23/2017.
 */

public enum InstructionType
{
    Wait,           //Do Nothing for the set amount of delay.
    Warn,           //Warn the current target squares by flashing yellow
    Listen,         //Checks each frame if a gameobject is within the target. If it is, continue on to the next instruction.
    ScanRow,        //Get all targetable gameObjects on a specific row. Defaults to row source is on.
    ScanRowFirst,   //Get first targetable gameObject on a specific row. Same properties as ^
    ScanCol,        //Get all targetable gameObjects on a specific column. Defaults to col source is on.
    ScanTile,     //Get all targetable gameObjects over the current prescripted target
    NextTile,     //Move target array pointer to the next target
    Damage,         //call ability.damage() on all scanned gameObjects
    InflictStatus, //call ability.inflictStatus() on all scanned gameObjects
    AddAnimationTile,    //gives tiles under tile pointer a reference to this animation object.
    AddAnimationSourceTile,
    FinishAnimation,
    PlayAnimation, //calls playAnimationByTag based on the predefined tag.
    HealSource,     //heals the source for damage done, or for placement if specified
    BasicAttackCD,  //lower's jack's focus ability cd by 1 if something was hit.
    GetPlayerTile,
    And;            //execute the next n instructions simultaneously. Defaults to 2.


}
