package com.steve1316.granblueautomation_android.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.steve1316.granblueautomation_android.bot.Game
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.util.*
import kotlin.collections.ArrayList

/**
 * Utility functions for image processing via OCR like OpenCV.
 */
class ImageUtils(context: Context, private val game: Game) {
    private val TAG: String = "GAA_ImageUtils"
    private var myContext = context
    
    private val matchMethod: Int = Imgproc.TM_CCOEFF_NORMED
    
    companion object {
        private var matchFilePath: String = ""
        lateinit var matchLocation: Point
        var matchLocations: ArrayList<Point> = arrayListOf()
    
        /**
         * Saves the file path to the saved match image file for debugging purposes.
         *
         * @param filePath File path to where to store the image containing the location of where the match was found.
         */
        private fun updateMatchFilePath(filePath: String) {
            matchFilePath = filePath
        }
    }
    
    init {
        // Set the file path to the /files/temp/ folder.
        val matchFilePath: String = myContext.getExternalFilesDir(null)?.absolutePath + "/temp"
        updateMatchFilePath(matchFilePath)
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
    
    private fun matchAll(sourceBitmap: Bitmap, templateBitmap: Bitmap): ArrayList<Point> {
        // Create the Mats of both source and template images.
        val sourceMat = Mat()
        val templateMat = Mat()
        Utils.bitmapToMat(sourceBitmap, sourceMat)
        Utils.bitmapToMat(templateBitmap, templateMat)
    
        // Make the Mats grayscale for the source and the template.
        Imgproc.cvtColor(sourceMat, sourceMat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.cvtColor(templateMat, templateMat, Imgproc.COLOR_BGR2GRAY)
    
        // Create the result matrix.
        val resultColumns: Int = sourceMat.cols() - templateMat.cols() + 1
        val resultRows: Int = sourceMat.rows() - templateMat.rows() + 1
        val resultMat = Mat(resultRows, resultColumns, CvType.CV_32FC1)
        
        // Loop until all matches are found.
        while(true) {
            // Now perform the matching and localize the result.
            Imgproc.matchTemplate(sourceMat, templateMat, resultMat, matchMethod)
            val mmr: Core.MinMaxLocResult = Core.minMaxLoc(resultMat)
            
            if((matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) && mmr.minVal <= 0.2) {
                Log.d(TAG, "MATCH FOUND <= 0.2")
                val tempMatchLocation: Point = mmr.minLoc
                Log.d(TAG, "Point $tempMatchLocation minVal: ${mmr.minVal}")
                
                // Draw a rectangle around the match and then save it to the specified file.
                Imgproc.rectangle(sourceMat, tempMatchLocation, Point(tempMatchLocation.x + templateMat.cols(), tempMatchLocation.y +
                        templateMat.rows()), Scalar(255.0, 255.0, 255.0), 5)
                Imgcodecs.imwrite("$matchFilePath/matchAll.png", sourceMat)
    
                // Center the location coordinates and then save it to the arrayList.
                tempMatchLocation.x += (templateMat.cols() / 2)
                tempMatchLocation.y += (templateMat.rows() / 2)
                matchLocations.add(tempMatchLocation)
            } else if((matchMethod != Imgproc.TM_SQDIFF && matchMethod != Imgproc.TM_SQDIFF_NORMED) && mmr.maxVal >= 0.8) {
                Log.d(TAG, "MATCH FOUND >= 0.8")
                val tempMatchLocation: Point = mmr.maxLoc
                Log.d(TAG, "Point $tempMatchLocation maxVal: ${mmr.maxVal}")
    
                // Draw a rectangle around the match and then save it to the specified file.
                Imgproc.rectangle(sourceMat, tempMatchLocation, Point(tempMatchLocation.x + templateMat.cols(), tempMatchLocation.y +
                        templateMat.rows()), Scalar(255.0, 255.0, 255.0), 5)
                Imgcodecs.imwrite("$matchFilePath/matchAll.png", sourceMat)
    
                // Center the location coordinates and then save it to the arrayList.
                tempMatchLocation.x += (templateMat.cols() / 2)
                tempMatchLocation.y += (templateMat.rows() / 2)
                matchLocations.add(tempMatchLocation)
            } else {
                break
            }
        }
    
        return matchLocations
    }
    
    /**
     * Open the source and template image files and return Bitmaps for them.
     *
     * @param templateName File name of the template image.
     * @param templateFolderName Name of the subfolder in /assets/ that the template image is in.
     * @return A Pair of source and template Bitmaps.
     */
    private fun getBitmaps(templateName: String, templateFolderName: String): Pair<Bitmap?, Bitmap?> {
        val sourceBitmap = MediaProjectionService.takeScreenshotNow()
        
        var templateBitmap: Bitmap?
    
        // Get the Bitmap from the template image file inside the specified folder.
        myContext.assets?.open("$templateFolderName/$templateName.png").use { inputStream ->
            // Get the Bitmap from the template image file and then start matching.
            templateBitmap = BitmapFactory.decodeStream(inputStream)
        }
        
        if(templateBitmap != null) {
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
     * @param tries Number of tries before failing. Defaults to 3.
     * @param suppressError Whether or not to suppress saving error messages to the log.
     * @return Point object containing the location of the match or null if not found.
     */
    fun findButton(templateName: String, tries: Int = 3, suppressError: Boolean = false): Point? {
        val folderName = "buttons"
        var numberOfTries = tries
        
        while(numberOfTries > 0) {
            val (sourceBitmap, templateBitmap) = getBitmaps(templateName, folderName)
    
            if(sourceBitmap != null && templateBitmap != null) {
                val resultFlag: Boolean = match(sourceBitmap, templateBitmap)
                if (!resultFlag) {
                    numberOfTries -= 1
                    if (numberOfTries <= 0) {
                        if (!suppressError) {
                            game.printToLog("[WARNING] Failed to find the ${templateName.toUpperCase(Locale.ROOT)} button.",
                                MESSAGE_TAG = TAG)
                        }
            
                        return null
                    }
        
                    Log.d(TAG, "Failed to find the ${templateName.toUpperCase(Locale.ROOT)} button. Trying again...")
                    game.wait(1.0)
                } else {
                    game.printToLog("[SUCCESS] Found the ${templateName.toUpperCase(Locale.ROOT)} at $matchLocation.", MESSAGE_TAG = TAG)
                    return matchLocation
                }
            }
        }
        
        return null
    }
    
    /**
     * Confirms whether or not the bot is at the specified location.
     *
     * @param templateName File name of the template image.
     * @param tries Number of tries before failing. Defaults to 3.
     * @param suppressError Whether or not to suppress saving error messages to the log.
     * @return True if the current location is at the specified location. False otherwise.
     */
    fun confirmLocation(templateName: String, tries: Int = 3, suppressError: Boolean = false): Boolean {
        val folderName = "headers"
        var numberOfTries = tries
        while(numberOfTries > 0) {
            val (sourceBitmap, templateBitmap) = getBitmaps(templateName + "_header", folderName)
    
            if(sourceBitmap != null && templateBitmap != null) {
                val resultFlag: Boolean = match(sourceBitmap, templateBitmap)
                if(!resultFlag) {
                    Log.d(TAG, "Current location is not at $templateName.")
                    numberOfTries -= 1
                    if(numberOfTries <= 0) {
                        break
                    }
                    
                    game.wait(1.0)
                } else {
                    game.printToLog("[SUCCESS] Current location confirmed to be at ${templateName.toUpperCase(Locale.ROOT)}.",
                        MESSAGE_TAG = TAG)
                    return true
                }
            } else {
                break
            }
        }
    
        if(!suppressError) {
            game.printToLog("[WARNING] Failed to confirm the bot's location at ${templateName.toUpperCase(Locale.ROOT)}.",
                MESSAGE_TAG = TAG)
        }
        
        return false
    }
    
    /**
     * Finds the location of the specified Summon.
     *
     * @param summonList List of selected Summons sorted from greatest to least priority.
     * @param summonElementList List of Summon Elements that correspond to the summonList.
     * @param suppressError Whether or not to suppress saving error messages to the log.
     * @return Point object containing the location of the match or null if not found.
     */
    fun findSummon(summonList: ArrayList<String>, summonElementList: ArrayList<String>, suppressError: Boolean = false): Point? {
        val folderName = "summons"
        
        game.printToLog("[DEBUG] Received the following list of Summons to search for: $summonList", MESSAGE_TAG = TAG)
        game.printToLog("[DEBUG] Received the following list of Summon Elements: $summonElementList", MESSAGE_TAG = TAG)
    
        var summonIndex = 0
        var summonLocation: Point? = null
        
        while(summonLocation == null && summonIndex <= summonList.size) {
            // Select the Summon Element.
            game.printToLog("[INFO] Now attempting to find ${summonList[summonIndex]}", MESSAGE_TAG = TAG)
            val currentSummonElement = summonElementList[summonIndex]
            game.findAndClickButton("summon_$currentSummonElement")
            
            while(summonLocation == null && summonIndex <= summonList.size) {
                // Go through each Summon detected on the Summon Selection screen and see if they match with the selected Summon.
                val summonName = summonList[summonIndex]
                val (sourceBitmap, templateBitmap) = getBitmaps(summonName, folderName)
                
                if(sourceBitmap != null && templateBitmap != null && match(sourceBitmap, templateBitmap)) {
                    summonLocation = matchLocation
                    break
                } else {
                    game.printToLog("[WARNING] Could not locate ${summonName.toUpperCase(Locale.ROOT)} Summon. Trying again...")
                    
                    // If it reached the bottom of the Summon Selection page, scroll all the way back up.
                    if(findButton("bottom_of_summon_selection", tries = 1) != null) {
                        game.gestureUtils.scroll(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD)
                        summonIndex += 1
                        break
                    }
                }
            }
            
            if(summonLocation == null && (summonIndex + 1) > summonList.size) {
                if(!suppressError) {
                    game.printToLog("[WARNING] Failed to find any of the specified Summons.", MESSAGE_TAG = TAG)
                }
                
                return null
            }
        }
        
        game.printToLog("[SUCCESS] Found ${summonList[summonIndex].toUpperCase(Locale.ROOT)} Summon at $matchLocation.")
        return matchLocation
    }
    
    /**
     * Finds any dialog popups from Lyria/Vyrn during Combat Mode.
     *
     * @param templateName File name of the template image.
     * @param tries Number of tries before failing. Defaults to 3.
     * @param suppressError Whether or not to suppress saving error messages to the log.
     * @return Point object containing the location of the match or null if not found.
     */
    fun findDialog(templateName: String, tries: Int = 3, suppressError: Boolean = false): Point? {
        val folderName = "dialogs"
        var numberOfTries = tries
    
        while(numberOfTries > 0) {
            val (sourceBitmap, templateBitmap) = getBitmaps(templateName, folderName)
        
            if(sourceBitmap != null && templateBitmap != null) {
                val resultFlag: Boolean = match(sourceBitmap, templateBitmap)
                if (!resultFlag) {
                    numberOfTries -= 1
                    if (numberOfTries <= 0) {
                        game.printToLog("[SUCCESS] There are no dialog popups detected from Lyria/Vyrn.", MESSAGE_TAG = TAG)
                    
                        return null
                    }
                
                    if(!suppressError) {
                        Log.d(TAG, "Failed to find the ${templateName.toUpperCase(Locale.ROOT)} dialog. Trying again...")
                    }
                    
                    game.wait(1.0)
                } else {
                    game.printToLog("[SUCCESS] Found the ${templateName.toUpperCase(Locale.ROOT)} at $matchLocation.", MESSAGE_TAG = TAG)
                    return matchLocation
                }
            }
        }
    
        return null
    }
    
    /**
     * Finds all occurrences of the specified image.
     *
     * @param templateName File name of the template image.
     * @return An ArrayList of Point objects containing all the occurrences of the specified image or null if not found.
     */
    fun findAll(templateName: String): ArrayList<Point> {
        val folderName = "buttons"
    
        val (sourceBitmap, templateBitmap) = getBitmaps(templateName, folderName)
    
        // Clear the ArrayList first before attempting to find all matches.
        matchLocations.clear()
    
        if(sourceBitmap != null && templateBitmap != null) {
            matchAll(sourceBitmap, templateBitmap)
        }
        
        return matchLocations
    }
    
    /**
     * Finds all occurrences of the specified item image and sums all of the number amounts together.
     *
     * @param templateName File name of the template image.
     * @return Sum of all the item's amounts.
     */
    fun findFarmedItems(templateName: String): Int {
        TODO("Not yet implemented")
    }
    
    /**
     * Waits for the specified image to vanish from the screen.
     *
     * @param templateName File name of the template image.
     * @param timeout Amount of time to wait before timing out. Default is 5 seconds.
     * @return True if the specified image vanished from the screen. False otherwise.
     */
    fun waitVanish(templateName: String, timeout: Int = 5): Boolean {
        TODO("Not yet implemented")
    }
}