package trac.portableexpensesdiary.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.raizlabs.android.dbflow.data.Blob;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;
import trac.portableexpensesdiary.R;

public class AsyncThumbnailImageLoaderTask extends AsyncTask<Blob, Void, Bitmap> {

    private final WeakReference<CircleImageView> imageViewReference;

    public AsyncThumbnailImageLoaderTask(CircleImageView imageView) {
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        imageViewReference.get().setImageResource(
                R.drawable.ic_panorama_fish_eye_white_48dp
        );
    }

    @Override
    protected Bitmap doInBackground(Blob... params) {
        Blob data = params[0];

        return ThumbnailUtils.extractThumbnail(
                ImageUtils.blobToBitmap(
                        data.getBlob()
                ),
                Constants.COMPRESSED_IMAGE_WIDTH,
                Constants.COMPRESSED_IMAGE_HEIGHT
        );
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        ImageView imageView = imageViewReference.get();

        if (imageView != null && bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
