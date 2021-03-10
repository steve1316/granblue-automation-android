package com.steve1316.granblueautomation_android.utils

import android.app.*
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.steve1316.granblueautomation_android.R

/**
 * Contains the utility functions for creating a Notification.
 *
 * Source is from https://github.com/mtsahakis/MediaProjectionDemo where the Java code was converted to Kotlin and additional logic was added to
 * suit this application's purposes.
 */
class NotificationUtils {
	companion object {
		private const val TAG: String = "GAA_NotificationUtils"
		
		private lateinit var notificationManager: NotificationManager
		private const val NOTIFICATION_ID: Int = 1337
		private const val CHANNEL_ID: String = "GAA_NotificationChannel_ID"
		private const val CHANNEL_NAME: String = "GAA_NotificationChannel"
		private const val CHANNEL_DESCRIPTION: String = "This is the Notification Channel for GAA's MediaProjection Service."
		
		/**
		 * Creates the NotificationChannel and the Notification object.
		 *
		 * @param context The application context.
		 * @return Pair object containing the Notification object and its ID string.
		 */
		fun getNewNotification(context: Context): Pair<Notification, Int> {
			// Create the NotificationChannel.
			createNewNotificationChannel(context)
			
			// Create the Notification.
			val newNotification = createNewNotification(context)
			
			// Get the NotificationManager and then send the new Notification to it.
			notificationManager.notify(NOTIFICATION_ID, newNotification)
			
			return Pair(newNotification, NOTIFICATION_ID)
		}
		
		/**
		 * Create a new NotificationChannel for Granblue Automation.
		 *
		 * https://developer.android.com/training/notify-user/channels
		 *
		 * @param context The application context.
		 */
		private fun createNewNotificationChannel(context: Context) {
			// Create the NotificationChannel.
			val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
			mChannel.description = CHANNEL_DESCRIPTION
			
			// Register the channel with the system; you can't change the importance or other notification behaviors after this.
			notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(mChannel)
			
			Log.d(TAG, "New Notification Channel successfully created for GAA.")
		}
		
		/**
		 * Create a new Notification.
		 *
		 * @param context The application context.
		 * @return A new Notification object.
		 */
		private fun createNewNotification(context: Context) : Notification {
			// Create a STOP Intent for the MediaProjection service.
			val stopIntent = Intent(context, StopServiceReceiver::class.java)
			
			// Create a PendingIntent in order to add a action button to stop the MediaProjection service in the notification.
			val stopPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), stopIntent, PendingIntent
				.FLAG_CANCEL_CURRENT)
			
			return NotificationCompat.Builder(context, CHANNEL_ID).apply {
				setSmallIcon(R.drawable.ic_baseline_control_camera_24)
				setContentTitle("Granblue Automation Android")
				setContentText("Bot process is currently inactive")
				addAction(R.drawable.ic_baseline_stop_circle_24, context.getString(R.string.accessibility_service_action), stopPendingIntent)
				priority = NotificationManager.IMPORTANCE_HIGH
				setCategory(Notification.CATEGORY_SERVICE)
				setOngoing(true)
				setShowWhen(true)
			}.build()
		}
	}
}