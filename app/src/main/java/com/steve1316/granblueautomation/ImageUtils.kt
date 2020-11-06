package com.steve1316.granblueautomation

import android.graphics.Bitmap
import android.view.View

/**
 * ImageUtils - Take a screenshot of the screen. Can be adjusted to crop at different (x,y) coordinates to fit a particular model.
 */
class ImageUtils {
    @Suppress("DEPRECATION")
    companion object Screenshot {
        fun takeScreenshotOfRootView(v: View): Bitmap {
            return takeScreenshot(v.rootView)
        }

        /**
         * This will take a screenshot of the current screen using specified (x,y) coordinates, width, and height.
         *
         * @param view View object currently on the screen.
         *
         * @return Bitmap screenshot
         */
        private fun takeScreenshot(view: View): Bitmap {
            // TODO: The logic below has been deprecated since API 28 due to performance loss. Update the logic below using Canvas and draw().
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache(true)

            // TODO: Accommodate for different screen sizes. Currently only for Samsung S10+ screen size.

            // Take a screenshot of the screen based on the given (x,y) coordinates, width, and height of the image.
            //val bitMapImage = Bitmap.createBitmap(view.drawingCache, 500, 0, 500, 500)
            val bitMapImage = Bitmap.createBitmap(view.drawingCache, 0, 0, 1080, 225)

            view.isDrawingCacheEnabled = false

            return bitMapImage
        }
    }
}