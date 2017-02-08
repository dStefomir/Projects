package trac.portableexpensesdiary.basecomponents;

import android.content.Context;
import android.util.AttributeSet;

public class StaticAntiMonkeyGridView extends AntiMonkeyGridView {

  public StaticAntiMonkeyGridView(Context context, AttributeSet attrs) {
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
