package com.steve1316.granblueautomation_android

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.EditText
import android.widget.Toast
import com.steve1316.granblueautomation_android.data.RoomCodeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

/**
 * Contains the Accessibility service that will allow the bot to programmatically perform gestures on the screen.
 */
class MyAccessibilityService : AccessibilityService() {
	private val TAG: String = "GAA_MyAccessibilityService"
	private lateinit var myContext: Context
	
	companion object {
		// Other classes need this static reference to this service as calling dispatchGesture() would not work.
		@SuppressLint("StaticFieldLeak")
		private lateinit var instance: MyAccessibilityService
		
		/**
		 * Returns a static reference to this class.
		 *
		 * @return Static reference to MyAccessibilityService.
		 */
		fun getInstance(): MyAccessibilityService {
			return instance
		}
	}
	
	override fun onServiceConnected() {
		instance = this
		myContext = this
		
		Log.d(TAG, "Accessibility Service for GAA is now running.")
		Toast.makeText(myContext, "Accessibility Service for GAA is now running.", Toast.LENGTH_SHORT).show()
	}
	
	override fun onAccessibilityEvent(event: AccessibilityEvent?) {
		if (event?.source != null && RoomCodeData.roomCode != "" && event.source?.className.toString().contains(EditText::class.java.simpleName)) {
			Log.d(TAG, "Copying ${RoomCodeData.roomCode}")
			
			// Paste the room code.
			val arguments = Bundle()
			arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, RoomCodeData.roomCode)
			event.source.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
			
			Log.d(TAG, "Pasted ${RoomCodeData.roomCode}")
			
			// Now reset the room code to prevent looping of onAccessibilityEvent().
			RoomCodeData.roomCode = ""
		}
		
		return
	}
	
	override fun onInterrupt() {
		return
	}
	
	override fun onDestroy() {
		super.onDestroy()
		
		Log.d(TAG, "Accessibility Service for GAA is now stopped.")
		Toast.makeText(myContext, "Accessibility Service for GAA is now stopped.", Toast.LENGTH_SHORT).show()
	}
	
	/**
	 * Wait the specified seconds to account for ping or loading.
	 *
	 * @param seconds Number of seconds to pause execution.
	 */
	private fun wait(seconds: Double) {
		runBlocking {
			delay((seconds * 1000).toLong())
		}
	}
	
	private fun randomizeTapLocation(x: Double, y: Double, buttonName: String): Pair<Int, Int> {
		// Get the Bitmap from the template image file inside the specified folder.
		val templateBitmap: Bitmap
		myContext.assets?.open("buttons/$buttonName.webp").use { inputStream ->
			// Get the Bitmap from the template image file and then start matching.
			templateBitmap = BitmapFactory.decodeStream(inputStream)
		}
		
		val width = templateBitmap.width
		val height = templateBitmap.height
		
		// Randomize the tapping location.
		val x0: Int = (x - (width / 2)).toInt()
		val x1: Int = (x + (width / 2)).toInt()
		val y0: Int = (y - (height / 2)).toInt()
		val y1: Int = (y + (height / 2)).toInt()
		
		var newX: Int
		var newY: Int
		
		while (true) {
			val newWidth: Int = (10..(width - 10)).random()
			val newHeight: Int = (10..(height - 10)).random()
			
			newX = x0 + newWidth
			newY = y0 + newHeight
			
			// If the new coordinates are within the bounds of the template image, break out of the loop.
			if (newX > x0 || newX < x1 || newY > y0 || newY < y1) {
				break
			}
		}
		
		return Pair(newX, newY)
	}
	
	/**
	 * Creates a tap gesture on the specified point on the screen.
	 *
	 * @param x The x coordinate of the point.
	 * @param y The y coordinate of the point.
	 * @param ignoreWait Whether or not to not wait 0.5 seconds after dispatching the gesture.
	 * @param longPress Whether or not to long press.
	 * @return True if the tap gesture was executed successfully. False otherwise.
	 */
	fun tap(x: Double, y: Double, buttonName: String, ignoreWait: Boolean = false, longPress: Boolean = false): Boolean {
		// Randomize the tapping location.
		val (newX, newY) = randomizeTapLocation(x, y, buttonName)
		
		val tapPath = Path().apply {
			moveTo(newX.toFloat(), newY.toFloat())
		}
		
		val gesture: GestureDescription = if (longPress) {
			GestureDescription.Builder().apply {
				addStroke(GestureDescription.StrokeDescription(tapPath, 0, 1000, true))
			}.build()
		} else {
			GestureDescription.Builder().apply {
				addStroke(GestureDescription.StrokeDescription(tapPath, 0, 1))
			}.build()
		}
		
		val dispatchResult = dispatchGesture(gesture, null, null)
		if (!ignoreWait) {
			wait(0.5)
		}

//        if(dispatchResult) {
//            if(longPress) {
//                Log.d(TAG, "Long pressing x: $x, y: $y")
//            } else {
//                Log.d(TAG, "Tapping x: $x, y: $y")
//            }
//        } else {
//            Log.e(TAG, "Failed to dispatch gesture")
//        }
		
		return dispatchResult
	}
	
	/**
	 * Creates a scroll gesture either scrolling up or down the screen depending on the given action.
	 *
	 * @param action The scrolling action, either ACTION_SCROLL_UP or ACTION_SCROLL_DOWN. Defaults to ACTION_SCROLL_DOWN.
	 * @param duration How long the scroll should take. Defaults to 100L.
	 * @param ignoreWait Whether or not to not wait 0.5 seconds after dispatching the gesture.
	 * @return True if the scroll gesture was executed successfully. False otherwise.
	 */
	fun scroll(
        action: AccessibilityNodeInfo.AccessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN, duration: Long
        = 500L, ignoreWait: Boolean = false
    ): Boolean {
		val scrollPath = Path()
		
		// Get certain portions of the screen's dimensions.
		val displayMetrics = Resources.getSystem().displayMetrics
		val top: Float = (displayMetrics.heightPixels * 0.75).toFloat()
		val middle: Float = (displayMetrics.widthPixels / 2).toFloat()
		val bottom: Float = (displayMetrics.heightPixels * 0.25).toFloat()
		
		when (action) {
            AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_UP -> {
                // Create a Path to scroll the screen up starting from the bottom and swiping to the top.
                scrollPath.apply {
                    moveTo(middle, bottom)
                    lineTo(middle, top)
                }
            }
            AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN -> {
                // Create a Path to scroll the screen down starting from the top and swiping to the bottom.
                scrollPath.apply {
                    moveTo(middle, top)
                    lineTo(middle, bottom)
                }
            }
			else -> {
				Log.e(TAG, "Invalid action received.")
			}
		}
		
		val gesture = GestureDescription.Builder().apply {
			addStroke(GestureDescription.StrokeDescription(scrollPath, 0, duration))
		}.build()
		
		val dispatchResult = dispatchGesture(gesture, null, null)
		if (!ignoreWait) {
			wait(0.5)
		}
		
		if (!dispatchResult) {
			Log.e(TAG, "Failed to dispatch scroll gesture.")
		}
		
		return dispatchResult
	}
	
	/**
	 * Creates a swipe gesture from the old coordinates to the new coordinates on the screen.
	 *
	 * @param oldX The x coordinate of the old position.
	 * @param oldY The y coordinate of the old position.
	 * @param newX The x coordinate of the new position.
	 * @param newY The y coordinate of the new position.
	 * @param duration How long the swipe should take. Defaults to 500L.
	 * @param ignoreWait Whether or not to not wait 0.5 seconds after dispatching the gesture.
	 * @return True if the swipe gesture was executed successfully. False otherwise.
	 */
	fun swipe(oldX: Float, oldY: Float, newX: Float, newY: Float, duration: Long = 500L, ignoreWait: Boolean = false): Boolean {
		// Set up the Path by swiping from the old position coordinates to the new position coordinates.
		val swipePath = Path().apply {
			moveTo(oldX, oldY)
			lineTo(newX, newY)
		}
		
		val gesture = GestureDescription.Builder().apply {
			addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))
		}.build()
		
		val dispatchResult = dispatchGesture(gesture, null, null)
		if (!ignoreWait) {
			wait(0.5)
		}

//        if(dispatchResult) {
//            Log.d(TAG, "Swiping from ($oldX, $oldY) to ($newX, $newY).")
//        } else {
//            Log.e(TAG, "Failed to dispatch swipe gesture.")
//        }
		
		return dispatchResult
	}
}