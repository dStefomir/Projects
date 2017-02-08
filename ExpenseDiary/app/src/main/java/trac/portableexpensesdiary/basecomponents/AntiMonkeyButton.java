package trac.portableexpensesdiary.basecomponents;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;

import mehdi.sakout.fancybuttons.FancyButton;
import trac.portableexpensesdiary.utils.Constants;

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
    }, Constants.BTN_DELAY_IN_MILLISECONDS);

    return super.performClick();
  }
}
