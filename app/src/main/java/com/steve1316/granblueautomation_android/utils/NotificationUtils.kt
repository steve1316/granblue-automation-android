package com.steve1316.granblueautomation_android.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.steve1316.granblueautomation_android.MainActivity
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
		private const val NOTIFICATION_ID: Int = 1
		private const val CHANNEL_ID: String = "BOT_STATUS"
		private const val CHANNEL_NAME: String = "Bot Status"
		private const val CHANNEL_DESCRIPTION: String = "Displays status of GAA, whether it is running or not."
		
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
			val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
			mChannel.description = CHANNEL_DESCRIPTION
			
			// Register the channel with the system; you can't change the importance or other notification behaviors after this.
			notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			notificationManager.createNotificationChannel(mChannel)
		}
		
		/**
		 * Create a new Notification.
		 *
		 * @param context The application context.
		 * @return A new Notification object.
		 */
		private fun createNewNotification(context: Context): Notification {
			// Create a STOP Intent for the MediaProjection service.
			val stopIntent = Intent(context, StopServiceReceiver::class.java)
			
			// Create a PendingIntent in order to add a action button to stop the MediaProjection service in the notification.
			val stopPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)
			
			// Create a PendingIntent to send the user back to the application if they tap the notification itself.
			val contentIntent = Intent(context, MainActivity::class.java)
			val contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)
			
			return NotificationCompat.Builder(context, CHANNEL_ID).apply {
				setSmallIcon(R.drawable.ic_baseline_control_camera_24)
				setContentTitle("Granblue Automation Android")
				setContentText("Bot process is currently inactive")
				setContentIntent(contentPendingIntent)
				addAction(R.drawable.ic_baseline_stop_circle_24, context.getString(R.string.accessibility_service_action), stopPendingIntent)
				priority = NotificationManager.IMPORTANCE_HIGH
				setCategory(Notification.CATEGORY_SERVICE)
				setOngoing(true)
				setShowWhen(true)
			}.build()
		}
		
		/**
		 * Updates the Notification content text.
		 *
		 * @param context The application context.
		 * @param isRunning Boolean for whether or not the bot process is currently running.
		 */
		fun updateNotification(context: Context, isRunning: Boolean) {
			var contentText = "Bot process is currently inactive"
			if (isRunning) {
				contentText = "Bot process is running"
			}
			
			// Create a STOP Intent for the MediaProjection service.
			val stopIntent = Intent(context, StopServiceReceiver::class.java)
			
			// Create a PendingIntent in order to add a action button to stop the MediaProjection service in the notification.
			val stopPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, System.currentTimeMillis().toInt(), stopIntent, PendingIntent.FLAG_CANCEL_CURRENT)
			
			// Create a PendingIntent to send the user back to the application if they tap the notification itself.
			val contentIntent = Intent(context, MainActivity::class.java)
			val contentPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)
			
			val newNotification = NotificationCompat.Builder(context, CHANNEL_ID).apply {
				setSmallIcon(R.drawable.ic_baseline_control_camera_24)
				setContentTitle("Granblue Automation Android")
				setContentText(contentText)
				setContentIntent(contentPendingIntent)
				addAction(R.drawable.ic_baseline_stop_circle_24, context.getString(R.string.accessibility_service_action), stopPendingIntent)
				priority = NotificationManager.IMPORTANCE_HIGH
				setCategory(Notification.CATEGORY_SERVICE)
				setOngoing(true)
				setShowWhen(true)
			}.build()
			
			if (!this::notificationManager.isInitialized) {
				notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
			}
			
			notificationManager.notify(NOTIFICATION_ID, newNotification)
		}
		
		fun createBotStateChangedNotification(context: Context, contentTitle: String, contentText: String) {
			val notificationID = 2
			val channelID = "BOT_STATE_CHANGED"
			val channelName = "Bot State Changed"
			
			// Create the NotificationChannel.
			val mChannel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
			mChannel.description = "This Channel is for notifications that will inform users on bot state changes, like the bot completing its goal successfully or if it encountered an Exception."
			
			// Register the channel with the system; you can't change the importance or other notification behaviors after this.
			notificationManager.createNotificationChannel(mChannel)
			
			val contentIntent = Intent(context, MainActivity::class.java)
			val contentPendingIntent = PendingIntent.getActivity(context, notificationID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT)
			
			val newNotification = NotificationCompat.Builder(context, channelID).apply {
				setSmallIcon(R.drawable.ic_baseline_error_outline_24)
				setContentTitle(contentTitle)
				setContentText(contentText)
				setContentIntent(contentPendingIntent)
				setAutoCancel(true)
				priority = NotificationManager.IMPORTANCE_HIGH
				setCategory(Notification.CATEGORY_SERVICE)
				setOngoing(false)
				setShowWhen(true)
			}.build()
			
			notificationManager.notify(notificationID, newNotification)
		}
	}
}