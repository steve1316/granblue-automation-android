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
import android.widget.Toast
import com.steve1316.granblueautomation_android.bot.Game
import com.steve1316.granblueautomation_android.bot.MapSelection
import com.steve1316.granblueautomation_android.utils.NotificationUtils
import kotlin.concurrent.thread
import kotlin.math.roundToInt

/**
 * This Service will begin workflow automation based on the chosen preference settings.
 */
class BotService: Service() {
	private val TAG: String = "GAA_BotService"
	private lateinit var myContext: Context
	private lateinit var overlayView: View
	private lateinit var overlayButton: ImageButton
	
	companion object {
		private var thread: Thread = Thread()
		private lateinit var windowManager: WindowManager
		private val overlayLayoutParams = WindowManager.LayoutParams().apply {
			type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
			flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			format = PixelFormat.TRANSLUCENT
			width = WindowManager.LayoutParams.WRAP_CONTENT
			height = WindowManager.LayoutParams.WRAP_CONTENT
			windowAnimations = android.R.style.Animation_Toast
		}
		
		var isRunning = false
	}
	
	@SuppressLint("ClickableViewAccessibility", "InflateParams")
	override fun onCreate() {
		super.onCreate()
		
		myContext = this
		
		overlayView = LayoutInflater.from(this).inflate(R.layout.bot_actions, null)
		windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
		windowManager.addView(overlayView, overlayLayoutParams)
		
		// This button is able to be moved around the screen and clicking it will start/stop the game automation.
		overlayButton = overlayView.findViewById(R.id.bot_actions_overlay_button)
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
						// Update both the Notification and the overlay button to reflect the current bot status.
						if(!isRunning) {
							Log.d(TAG, "Bot Service for GAA is now running.")
							Toast.makeText(myContext, "Bot Service for GAA is now running.", Toast.LENGTH_SHORT).show()
							isRunning = true
							NotificationUtils.updateNotification(myContext, isRunning)
							overlayButton.setImageResource(R.drawable.ic_baseline_stop_circle_24)
							
							val game = Game(myContext)
							val mapSelection = MapSelection(game)
							
							thread = thread {
								mapSelection.selectMap("special", "Angel Halo", "VH Angel Halo", "Very Hard")
								//MediaProjectionService.takeScreenshotNow()
							}
						} else {
							thread.interrupt()
							
							Log.d(TAG, "Bot Service for GAA is now stopped.")
							Toast.makeText(myContext, "Bot Service for GAA is now stopped.", Toast.LENGTH_SHORT).show()
							isRunning = false
							NotificationUtils.updateNotification(myContext, isRunning)
							overlayButton.setImageResource(R.drawable.ic_baseline_play_circle_outline_24)
						}

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
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		return super.onStartCommand(intent, flags, startId)
	}
	
	override fun onBind(intent: Intent?): IBinder? {
		return null
	}

	override fun onDestroy() {
		super.onDestroy()
		
		// Remove the overlay View that holds the overlay button.
		windowManager.removeView(overlayView)
	}
}