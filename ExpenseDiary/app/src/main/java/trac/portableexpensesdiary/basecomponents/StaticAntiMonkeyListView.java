package trac.portableexpensesdiary.basecomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class StaticAntiMonkeyListView extends ListView {

    public StaticAntiMonkeyListView(Context context) {
        super(context);
    }

    public StaticAntiMonkeyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSpec;

        if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            heightSpec = MeasureSpec.makeMeasureSpec(
                    Short.MAX_VALUE, MeasureSpec.AT_MOST);
        } else {
            heightSpec = heightMeasureSpec;
        }

        super.onMeasure(widthMeasureSpec, heightSpec);
    }
}
