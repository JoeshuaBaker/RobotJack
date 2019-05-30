package joeshua.robotjack.Managers.Abilities;

import android.graphics.Bitmap;
import android.graphics.Paint;

import joeshua.robotjack.Globals;
import joeshua.robotjack.Rendering.AnimationBitmap;

/**
 * Created by joeshua on 4/21/2017.
 */

public class AbilityIcon {
    private AnimationBitmap icon;
    private Bitmap damageType;
    private String name;
    private String tooltip;
    private String damage;
    private Ability source;

    public AbilityIcon(AnimationBitmap _icon, Ability.DamageType _damageType, String _name, String _tooltip, String _damage, Ability _source)
    {
        icon = _icon;
        //find damage type here.
        name = _name;
        tooltip = _tooltip;
        damage = _damage;
        source = _source;
    }

    public AnimationBitmap getIcon() {
        return icon;
    }

    public Bitmap getDamageType() {
        return damageType;
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return tooltip;
    }

    public String getDamage() {
        return damage;
    }

    public Ability getSource() {
        return source;
    }

    public String[] getTooltipFormatted(Paint paint)
    {
        final int NUM_LINES = 4;
        String[] lines = new String[NUM_LINES];
        for(int j = 0; j < NUM_LINES; ++j)
        {
            lines[j] = "";
        }


        int index = 0;
        String current = new String();
        String word = new String();
        char letter;

        for(int i = 0; i < NUM_LINES; ++i)
        {
            while(paint.measureText(current) < Globals.screenWidth*.20f)
            {
                if(index < tooltip.length())
                {
                    letter = tooltip.charAt(index);

                    if(letter == ' ')
                    {
                        if(paint.measureText(current + word) < Globals.screenWidth*.20f)
                        {
                            current += word + " ";
                            word = "";
                        }
                        else
                        {
                            break;
                        }
                    }
                    else
                    {
                        word += letter;
                    }
                    index++;
                }
                else
                {
                    lines[i] = current + word;
                    return lines;
                }
            }
            lines[i] = current;
            current = "";
        }
        return lines;
    }

    public void setTooltip(String newTip)
    {
        tooltip = newTip;
    }
}