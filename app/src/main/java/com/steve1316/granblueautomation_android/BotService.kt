package com.steve1316.granblueautomation_android

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.ImageButton
import com.steve1316.granblueautomation_android.utils.MediaProjectionService
import kotlin.math.roundToInt

/**
 * This Service will begin workflow automation based on the chosen preference settings.
 */
class BotService: Service() {
	private lateinit var myContext: Context
	private lateinit var overlayView: View
	
	companion object {
		private const val TAG: String = "GAA_BotService"
		private lateinit var windowManager: WindowManager
		private val overlayLayoutParams = WindowManager.LayoutParams().apply {
			type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
			flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			format = PixelFormat.TRANSLUCENT
			width = WindowManager.LayoutParams.WRAP_CONTENT
			height = WindowManager.LayoutParams.WRAP_CONTENT
			windowAnimations = android.R.style.Animation_Toast
		}
	}
	
	@SuppressLint("ClickableViewAccessibility", "InflateParams")
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		myContext = this
		
		overlayView = LayoutInflater.from(this).inflate(R.layout.bot_actions, null)
		windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
		windowManager.addView(overlayView, overlayLayoutParams)
		
		// This button is able to be moved around the screen and clicking it will start/stop the game automation.
		val overlayButton = overlayView.findViewById<ImageButton>(R.id.bot_actions_start_button)
		overlayButton.setOnTouchListener(object: View.OnTouchListener {
			private var initialX: Int = 0
			private var initialY: Int = 0
			private var initialTouchX: Float = 0F
			private var initialTouchY: Float = 0F

			override fun onTouch(v: View?, event: MotionEvent?): Boolean {
				val action = event?.action

				if(action == MotionEvent.ACTION_DOWN) {
					// Get the initial position.
					initialX = overlayLayoutParams.x
					initialY = overlayLayoutParams.y

					// Now get the new position.
					initialTouchX = event.rawX
					initialTouchY = event.rawY

					return false
				} else if(action == MotionEvent.ACTION_UP) {
					val elapsedTime: Long = event.eventTime - event.downTime
					if(elapsedTime < 100L) {
						// Take a screenshot now and save it.
						MediaProjectionService.takeScreenshotNow = true
						
						// Returning true here freezes the animation of the click on the button.
						return false
					}
				} else if(action == MotionEvent.ACTION_MOVE) {
					val xDiff = (event.rawX - initialTouchX).roundToInt()
					val yDiff = (event.rawY - initialTouchY).roundToInt()

					// Calculate the X and Y coordinates of the view.
					overlayLayoutParams.x = initialX + xDiff
					overlayLayoutParams.y = initialY + yDiff

					// Now update the layout.
					windowManager.updateViewLayout(overlayView, overlayLayoutParams)
					return false
				}
				
				return false
			}
		})
		
		Log.d(TAG, "Bot Service for GAA is now running.")
		return super.onStartCommand(intent, flags, startId)
	}
	
	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onDestroy() {
		super.onDestroy()

		windowManager.removeView(overlayView)
	}
}