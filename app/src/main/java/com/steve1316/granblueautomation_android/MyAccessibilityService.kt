package com.steve1316.granblueautomation_android

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Path
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast

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
        return
    }
    
    override fun onInterrupt() {
        return
    }
    
    /**
     * Creates a tap gesture on the specified point on the screen.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     * @return True if the tap gesture was executed successfully. False otherwise.
     */
    fun tap(x: Double, y: Double): Boolean {
        val tapPath = Path().apply {
            moveTo(x.toFloat(), y.toFloat())
        }
        
        val gesture = GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(tapPath, 0, 1))
        }.build()
        
        return dispatchGesture(gesture, null, null)
    }
    
    /**
     * Creates a scroll gesture either scrolling up or down the screen depending on the given action.
     *
     * @param action The scrolling action, either ACTION_SCROLL_FORWARD or ACTION_SCROLL_BACKWARD. Defaults to ACTION_SCROLL_FORWARD.
     * @param duration How long the scroll should take. Defaults to 500L.
     * @return True if the scroll gesture was executed successfully. False otherwise.
     */
    fun scroll(action: AccessibilityNodeInfo.AccessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD, duration: Long
    = 500L): Boolean {
        val scrollPath = Path()
        
        // Get certain portions of the screen's dimensions.
        val displayMetrics = Resources.getSystem().displayMetrics
        val top: Float = (displayMetrics.heightPixels * 0.75).toFloat()
        val middle: Float = (displayMetrics.widthPixels / 2).toFloat()
        val bottom: Float = (displayMetrics.heightPixels * 0.25).toFloat()
        
        if (action == AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD) {
            // Create a Path to scroll from top to bottom.
            scrollPath.apply {
                moveTo(middle, top)
                lineTo(middle, bottom)
            }
        } else if (action == AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD) {
            // Create a Path to scroll from bottom to top.
            scrollPath.apply {
                moveTo(middle, bottom)
                lineTo(middle, top)
            }
        } else {
            Log.e(TAG, "Invalid action received.")
        }
        
        val gesture = GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(scrollPath, 0, duration))
        }.build()
        
        return dispatchGesture(gesture, null, null)
    }
    
    /**
     * Creates a swipe gesture from the old coordinates to the new coordinates on the screen.
     *
     * @param oldX The x coordinate of the old position.
     * @param oldY The y coordinate of the old position.
     * @param newX The x coordinate of the new position.
     * @param newY The y coordinate of the new position.
     * @param duration How long the swipe should take. Defaults to 500L.
     * @return True if the swipe gesture was executed successfully. False otherwise.
     */
    fun swipe(oldX: Float, oldY: Float, newX: Float, newY: Float, duration: Long = 500L): Boolean {
        // Set up the Path by swiping from the old position coordinates to the new position coordinates.
        val swipePath = Path().apply {
            moveTo(oldX, oldY)
            lineTo(newX, newY)
        }
    
        val gesture = GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))
        }.build()
        
        return dispatchGesture(gesture, null, null)
    }
}