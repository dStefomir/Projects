package com.securegroup.githubapp.basecomponents;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import com.securegroup.githubapp.utils.Constants;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Button which takes care of bug situations like opening multiple instances of another components
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */
public class AntiMonkeyButton extends FancyButton {

    public AntiMonkeyButton(Context context) {
        super(context);
    }

    public AntiMonkeyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override public boolean performClick() {
        this.setEnabled(false);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override public void run() {
                AntiMonkeyButton.this.setEnabled(true);
            }
        }, Constants.ANTI_MONKEY_BUTTON_DELAY);

        return super.performClick();
    }
}
