package trac.portableexpensesdiary.smartexpenseregister;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.View;

import trac.portableexpensesdiary.R;
import trac.portableexpensesdiary.basecomponents.BaseActivity;

public class CameraActivity extends BaseActivity
    implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback,
    Camera.ShutterCallback {

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_camera);
  }

  @Override public void onPictureTaken(byte[] data, Camera camera) {

  }

  @Override public void onShutter() {

  }

  @Override public void surfaceCreated(SurfaceHolder holder) {

  }

  @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

  }

  @Override public void surfaceDestroyed(SurfaceHolder holder) {

  }

  @Override public void onClick(View v) {

  }
}
