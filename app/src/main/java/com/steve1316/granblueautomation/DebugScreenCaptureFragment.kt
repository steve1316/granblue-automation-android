package com.steve1316.granblueautomation

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.steve1316.granblueautomation.ImageUtils.Screenshot
import java.lang.Exception
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

/**
 * DebugScreenCaptureFragment - Takes care of testing screenshots and performing text detection.
 */
class DebugScreenCaptureFragment : Fragment() {
    private lateinit var main: View
    private lateinit var imageView: ImageView
    private lateinit var bitMapImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment and then attach IDs to the following objects.
        val view = inflater.inflate(R.layout.fragment_debug_screen_capture, container, false)
        main = view.findViewById(R.id.main)
        imageView = view.findViewById(R.id.imageView)
        val screenShotButton: Button = view.findViewById(R.id.screenshot_button)

        // When the screenshot button is clicked, set the imageView to the created Bitmap.
        screenShotButton.setOnClickListener {
            bitMapImage = Screenshot.takeScreenshotOfRootView(imageView)
            imageView.setImageBitmap(bitMapImage)

            // Darken the background a little to emphasize the new cropped screenshot.
            main.setBackgroundColor(Color.parseColor("#999999"))

            // Start Text Detection on the new screenshot.
            startTextDetection()
        }

        return view
    }

    /**
     * Use Google's ML Kit for Text Detection on the bitmap screenshot.
     */
    private fun startTextDetection() {
        try {
            Log.d("[DEBUG]", "Now starting MLKit text detection...")

            // Convert Bitmap to InputImage and then initialize the TextRecognizer object.
            val image = InputImage.fromBitmap(bitMapImage, 0)
            val recognizer = TextRecognition.getClient()

            // Now process the InputImage and report back what texts were detected.
            recognizer.process(image).addOnSuccessListener { visionText ->
                for (block in visionText.textBlocks) {
                    Toast.makeText(activity, "Text detected - " + block.text, Toast.LENGTH_SHORT)
                        .show()
                    Log.d("[DEBUG]", block.text)
                }
            }.addOnFailureListener { e ->
                Log.e("[ERROR]", "Failed to detect any text for this InputImage - $e")
            }
        } catch (error: Exception) {
            Log.e("[ERROR]", "$error")
        }
    }
}