package com.securegroup.githubapp.basecomponents;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

import com.securegroup.githubapp.utils.Constants;

/**
 * ListView which takes care of bug situations like opening multiple instances of another components
 * when click event occurs
 * <p>
 *
 * @author Stefomir Dimitrov </p>
 */

public class AntiMonkeyListView extends ListView {

    /**
     * Constructor
     *
     * @param context mContext of the application
     */
    public AntiMonkeyListView(Context context) {
        super(context);
    }

    /**
     * Constructor
     *
     * @param context mContext of the application
     * @param attrs   attributes of the view described in the layout file or in code
     */
    public AntiMonkeyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean performItemClick(View view, int position, long id) {
        this.setEnabled(false);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                AntiMonkeyListView.this.setEnabled(true);
            }
        }, Constants.ANTI_MONEY_LIST_VIEW_DELAY);

        return super.performItemClick(view, position, id);
    }
}
