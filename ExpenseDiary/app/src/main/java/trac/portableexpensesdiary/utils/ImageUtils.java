package trac.portableexpensesdiary.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import trac.portableexpensesdiary.R;

public class ImageUtils {

    public static void performCrop(
            Activity activity,
            Uri picUri,
            boolean isRect) {

        if (!isRect) {
            CropImage.activity(picUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setActivityTitle(activity.getString(R.string.crop_title))
                    .setAutoZoomEnabled(false)
                    .setOutputCompressQuality(20)
                    .setAspectRatio(1, 1)
                    .setFixAspectRatio(true)
                    .start(activity);
        } else {
            CropImage.activity(picUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setActivityTitle(activity.getString(R.string.crop_title))
                    .setAutoZoomEnabled(false)
                    .setOutputCompressQuality(20)
                    .start(activity);
        }
    }

    public static byte[] drawableToBlob(Drawable drawable) {
        Bitmap bitmap =
                ((BitmapDrawable) drawable).getBitmap();

        return bitmapToBlob(bitmap);
    }

    public static Drawable blobToDrawable(
            Resources resources,
            byte[] holder) {

        return new BitmapDrawable(
                resources,
                ImageUtils.blobToBitmap(holder)
        );
    }

    public static byte[] defaultDrawableToBlob(Drawable drawable) {
        Bitmap bitmap =
                ((BitmapDrawable) drawable).getBitmap();

        return defaultBitmapToBlob(bitmap);
    }

    public static byte[] bitmapToBlob(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();

        bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                20,
                byteArrayOutputStream
        );

        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] defaultBitmapToBlob(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();

        bitmap.compress(
                Bitmap.CompressFormat.PNG,
                100,
                byteArrayOutputStream
        );

        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap blobToBitmap(byte[] holder) {

        return BitmapFactory.decodeByteArray(
                holder,
                0,
                holder.length
        );
    }

    public static Uri takePhoto(
            Activity activity,
            Uri imageUri) {

        Uri currentImageUri = imageUri;

        if (PermissionUtils.areAllPermissionsGranted(activity)) {
            try {
                ContentValues values = new ContentValues();
                values.put(
                        MediaStore.Images.Media.TITLE,
                        "New Picture"
                );
                values.put(
                        MediaStore.Images.Media.DESCRIPTION,
                        "ExpenseTracking Category Picture"
                );

                currentImageUri = activity.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                );

                Intent intent =
                        new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        currentImageUri
                );

                activity.startActivityForResult(
                        intent,
                        Constants.CAMERA_CAPTURE
                );

            } catch (ActivityNotFoundException anfe) {

                new SweetAlertDialog(
                        activity,
                        SweetAlertDialog.ERROR_TYPE
                )
                        .setTitleText(activity.getString(R.string.error_title))
                        .setContentText(activity.getString(R.string.device_camera_err))
                        .setConfirmText(activity.getString(R.string.ok_btn))
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {

                                sDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        }

        return currentImageUri;
    }

    public static void selectPhoto(Activity activity) {
        final String intentType = "image/*";

        Intent intent = new Intent();
        intent.setType(intentType);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(
                Intent.createChooser(
                        intent,
                        activity.getString(R.string.select_image_hint)
                ),
                Constants.SELECT_IMAGE
        );
    }
}