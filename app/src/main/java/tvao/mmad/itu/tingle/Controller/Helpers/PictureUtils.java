package tvao.mmad.itu.tingle.Controller.Helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This class is used to scale and display bitmaps from which photos are loaded into.
 * Images taken are saved in the filesystem and this class is used to load it to the user in a reasonably sized bitmap.
 * We achieve a reasonable size by scaling the bitmap down by hand:
 * The file is scanned to see how big it is,
 * then we figure out how much to scale it by to fit it in a given area,
 * and finally reread the file to create a scaled-down Bitmap object.
 */
public class PictureUtils {

    /**
     *
     * @param path - file path.
     * @param destWidth - downscaled new width
     * @param destHeight - downscaled new height
     * @return scaled bitmap representing reasonably sized photo on phone
     */
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight)
    {
        // Read in dimensions of image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1; // Determines how big each sample should be for each pixel
        // Sample 1 has one final horizontal pixel for each horizontal pixel
        // Sample 2 has one final horizontal pixel for every two horizontal pixels in original file
        // Thus, image has quarter number of pixels of original when sample is 2
        if (srcHeight > destHeight || srcWidth > destWidth)
        {
            if (srcWidth > srcHeight)
            {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else
            {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

}
