package trac.portableexpensesdiary.smartexpenseregister;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import trac.portableexpensesdiary.R;

public class FocusBoxView extends View {

  private final Paint paint;
  private final int maskColor;
  private final int frameColor;
  private final int cornerColor;

  private Rect box;

  public FocusBoxView(Context context, AttributeSet attrs) {
    super(context, attrs);

    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Resources resources = getResources();

    maskColor = resources.getColor(R.color.focus_box_mask);
    frameColor = resources.getColor(R.color.focus_box_frame);
    cornerColor = resources.getColor(R.color.focus_box_corner);
  }

  private Rect getBox() {
    if (box == null) {

      int width = 470 * 320;
      int height = 800 * 600;

      int left = width / 2;
      int top = height / 2;

      box = new Rect(left, top, left + width, top + height);
    }

    return box;
  }

  @Override public void onDraw(Canvas canvas) {

    Rect frame = getBox();

    int width = canvas.getWidth();
    int height = canvas.getHeight();

    paint.setColor(maskColor);
    canvas.drawRect(0, 0, width, frame.top, paint);
    canvas.drawRect(0, frame.top, frame.left, frame.bottom + 1, paint);
    canvas.drawRect(frame.right + 1, frame.top, width, frame.bottom + 1, paint);
    canvas.drawRect(0, frame.bottom + 1, width, height, paint);

    paint.setAlpha(0);
    paint.setStyle(Paint.Style.FILL);
    paint.setColor(frameColor);
    canvas.drawRect(frame.left, frame.top, frame.right + 1, frame.top + 2, paint);
    canvas.drawRect(frame.left, frame.top + 2, frame.left + 2, frame.bottom - 1, paint);
    canvas.drawRect(frame.right - 1, frame.top, frame.right + 1, frame.bottom - 1, paint);
    canvas.drawRect(frame.left, frame.bottom - 1, frame.right + 1, frame.bottom + 1, paint);

    paint.setColor(cornerColor);
    canvas.drawCircle(frame.left - 32, frame.top - 32, 32, paint);
    canvas.drawCircle(frame.right + 32, frame.top - 32, 32, paint);
    canvas.drawCircle(frame.left - 32, frame.bottom + 32, 32, paint);
    canvas.drawCircle(frame.right + 32, frame.bottom + 32, 32, paint);
  }
}
