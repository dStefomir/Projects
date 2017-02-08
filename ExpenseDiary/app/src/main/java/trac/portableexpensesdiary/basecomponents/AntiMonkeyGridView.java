package trac.portableexpensesdiary.basecomponents;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import trac.portableexpensesdiary.utils.Constants;

public class AntiMonkeyGridView extends GridView {

    public AntiMonkeyGridView(Context context) {
        super(context);
    }

    public AntiMonkeyGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        this.setEnabled(false);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                AntiMonkeyGridView.this.setEnabled(true);
            }
        }, Constants.BTN_DELAY_IN_MILLISECONDS);

        return super.performItemClick(view, position, id);
    }
}
