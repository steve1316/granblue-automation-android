package com.steve1316.granblueautomation_android.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.FileInputStream

/**
 * Utility functions for image processing via OCR like OpenCV.
 */
class ImageUtils(context: Context) {
    private val TAG: String = "GAA_ImageUtils"
    private var myContext = context
    
    private val matchMethod: Int = Imgproc.TM_CCOEFF_NORMED
    
    companion object {
        private var matchFilePath: String = ""
        lateinit var matchLocation: Point
    
        /**
         * Saves the file path to the saved match image file for debugging purposes.
         *
         * @param filePath File path to where to store the image containing the location of where the match was found.
         */
        fun updateMatchFilePath(filePath: String) {
            matchFilePath = filePath
        }
    }
    
    /**
     * Match between the source Bitmap from /files/temp/ and the template Bitmap from the assets folder.
     *
     * @param sourceBitmap Bitmap from the /files/temp/ folder.
     * @param templateBitmap Bitmap from the assets folder.
     * @param useCannyAlgorithm Check whether or not to use Canny edge detection algorithm. Defaults to false.
     */
    private fun match(sourceBitmap: Bitmap, templateBitmap: Bitmap, useCannyAlgorithm: Boolean = false): Boolean {
        // Create the Mats of both source and template images.
        OpenCVLoader.initDebug()
        val sourceMat = Mat()
        val templateMat = Mat()
        Utils.bitmapToMat(sourceBitmap, sourceMat)
        Utils.bitmapToMat(templateBitmap, templateMat)
    
        // Make the Mats grayscale for the source and the template.
        Imgproc.cvtColor(sourceMat, sourceMat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.cvtColor(templateMat, templateMat, Imgproc.COLOR_BGR2GRAY)
        
        if(useCannyAlgorithm) {
            // Blur the source and template.
            Imgproc.blur(sourceMat, sourceMat, Size(3.0, 3.0))
            Imgproc.blur(templateMat, templateMat, Size(3.0, 3.0))
    
            // Apply Canny edge detection algorithm in both source and template. Generally recommended for threshold2 to be 3 times threshold1.
            Imgproc.Canny(sourceMat, sourceMat, 100.0, 300.0)
            Imgproc.Canny(templateMat, templateMat, 100.0, 300.0)
        }
        
        // Create the result matrix.
        val resultColumns: Int = sourceMat.cols() - templateMat.cols() + 1
        val resultRows: Int = sourceMat.rows() - templateMat.rows() + 1
        val resultMat = Mat(resultRows, resultColumns, CvType.CV_32FC1)
        
        // Now perform the matching and localize the result.
        Imgproc.matchTemplate(sourceMat, templateMat, resultMat, matchMethod)
        val mmr: Core.MinMaxLocResult = Core.minMaxLoc(resultMat)
        
        // Depending on which matching method was used, the algorithms determine which location was the best.
        matchLocation = Point()
        var matchCheck = false
        if((matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) && mmr.minVal <= 0.2) {
            Log.d(TAG, "MATCH FOUND <= 0.2")
            matchLocation = mmr.minLoc
            Log.d(TAG, "Point $matchLocation minVal: ${mmr.minVal}")
            matchCheck = true
        } else if((matchMethod != Imgproc.TM_SQDIFF && matchMethod != Imgproc.TM_SQDIFF_NORMED) && mmr.maxVal >= 0.8) {
            Log.d(TAG, "MATCH FOUND >= 0.8")
            matchLocation = mmr.maxLoc
            Log.d(TAG, "Point $matchLocation maxVal: ${mmr.maxVal}")
            matchCheck = true
        } else {
            Log.d(TAG, "MATCH NOT FOUND")
        }
        
        if(matchCheck) {
            // Draw a rectangle around the supposed best matching location and then save the match into a file in /files/temp/ directory. This is for
            // debugging purposes to see if this algorithm found the match accurately or not.
            if(matchFilePath != "") {
                Imgproc.rectangle(sourceMat, matchLocation, Point(matchLocation.x + templateMat.cols(), matchLocation.y + templateMat.rows()),
                    Scalar(0.0, 128.0, 0.0), 5)
                Imgcodecs.imwrite("$matchFilePath/match.png", sourceMat)
            }
            
            // Center the coordinates so that any tap gesture would be directed at the center of that match location instead of the default
            // position of the top left corner of the match location.
            matchLocation.x += (templateMat.cols() / 2)
            matchLocation.y += (templateMat.rows() / 2)
            
            return true
        } else {
            return false
        }
    }
    
    /**
     * Open the source and template image files and return Bitmaps for them.
     *
     * @param templateName File name of the template image.
     * @param templateFolderName Name of the subfolder in /assets/ that the template image is in.
     * @return A Pair of source and template Bitmaps.
     */
    private fun getBitmaps(templateName: String, templateFolderName: String): Pair<Bitmap?, Bitmap?> {
        var sourceBitmap: Bitmap?
        var templateBitmap: Bitmap?
    
        // Get the Bitmap from the source image file. It is named source.jpg for now.
        FileInputStream(myContext.getExternalFilesDir(null)?.absolutePath + "/temp/source.jpg").use { inputStream ->
            sourceBitmap = BitmapFactory.decodeStream(inputStream)
        }
    
        // Get the Bitmap from the template image file inside the specified folder.
        myContext.assets?.open("$templateFolderName/$templateName.png").use { inputStream ->
            // Get the Bitmap from the template image file and then start matching.
            templateBitmap = BitmapFactory.decodeStream(inputStream)
        }
        
        if(sourceBitmap != null && templateBitmap != null) {
            return Pair(sourceBitmap, templateBitmap)
        } else {
            Log.e(TAG, "One or both of the Bitmaps are null.")
            return Pair(sourceBitmap, templateBitmap)
        }
    }
    
    /**
     * Finds the location of the specified button.
     *
     * @param templateName File name of the template image.
     * @return Point object containing the location of the match or null if not found.
     */
    fun find_button(templateName: String): Point? {
        val folderName = "buttons"
        val (sourceBitmap, templateBitmap) = getBitmaps(templateName, folderName)
        
        if(sourceBitmap != null && templateBitmap != null) {
            val resultFlag: Boolean = match(sourceBitmap, templateBitmap)
            if(!resultFlag) {
                Log.d(TAG, "Button, $templateName, was not found.")
                return null
            }
    
            Log.d(TAG, "Button, $templateName, was found.")
            
            return matchLocation
        } else {
            return null
        }
    }
    
    /**
     * Confirms whether or not the bot is at the specified location.
     *
     * @param templateName File name of the template image.
     * @return True if the current location is at the specified location. False otherwise.
     */
    fun confirm_location(templateName: String): Boolean {
        val folderName = "headers"
        val (sourceBitmap, templateBitmap) = getBitmaps(templateName, folderName)
    
        if(sourceBitmap != null && templateBitmap != null) {
            val resultFlag: Boolean = match(sourceBitmap, templateBitmap)
            if(!resultFlag) {
                Log.d(TAG, "Current location is not at $templateName.")
                return false
            }
        
            Log.d(TAG, "Current location is at $templateName.")
            return true
        } else {
            return false
        }
    }
}