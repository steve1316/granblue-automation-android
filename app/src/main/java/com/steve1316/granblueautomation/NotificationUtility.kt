package com.steve1316.granblueautomation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat

/**
 * NotificationUtility - Hosts the functionality necessary to create a Bubble notification.
 */
class NotificationUtility(private val context: Context) {
    private val channelID = "101"

    /**
     * This will return a BubbleMeta object to be used by Notification.
     *
     * @param icon IconCompat object that will be the icon for the Bubble.
     *
     * @return A BubbleMeta object.
     */
    fun createBubble(icon: IconCompat): NotificationCompat.BubbleMetadata {
        val target = Intent(context, BubbleActivity::class.java)
        val bubbleIntent = PendingIntent.getActivity(context, 0, target, 0)

        return NotificationCompat.BubbleMetadata.Builder()
            .setIcon(icon)
            .apply {
                setAutoExpandBubble(false)
                setSuppressNotification(true)
            }
            .setDesiredHeight(300)
            .setIntent(bubbleIntent)
            .build()
    }

    /**
     * This will return a Notification object based on the BubbleMeta data.
     *
     * @param message String of the message when first showing the notification.
     * @param smallIcon Integer of the resource ID to the Bubble's icon.
     * @param largeIcon Bitmap of the icon to be shown in the ticker and notification.
     * @param personName String of the person's name.
     * @param personObject Person object.
     * @param bubbleData BubbleMeta object.
     *
     * @return A Notification object.
     */
    fun createNotification(
        message: String,
        smallIcon: Int,
        largeIcon: Bitmap?,
        personName: String,
        personObject: Person,
        bubbleData: NotificationCompat.BubbleMetadata
    ):
            Notification? {
        return NotificationCompat.Builder(context, channelID)
            .setContentTitle(context.getString(R.string.app_name))
            .setLargeIcon(largeIcon)
            .setSmallIcon(smallIcon)
            .setCategory(Notification.CATEGORY_MESSAGE)
            .setStyle(
                NotificationCompat.MessagingStyle(personObject)
                    .setGroupConversation(false)
                    .addMessage(message, System.currentTimeMillis(), personObject)
            )
            .addPerson(personName)
            .setShowWhen(true)
            .setBubbleMetadata(bubbleData)
            .build()
    }

    /**
     * Return a Person object using the given information.
     *
     * @param personName String of the person's name.
     *
     * @return A Person object.
     */
    fun createPerson(personName: String): Person {
        return Person.Builder()
            .setBot(true)
            .setName(personName)
            .setImportant(true)
            .build()
    }

    /**
     * Show the notification to the user (if the user enabled Bubble developer feature on Android 10, this notification will show as a Bubble).
     *
     * @param notification Notification object
     *
     * @return Nothing
     */
    fun showNotification(notification: Notification?) {
        notification?.let {
            NotificationManagerCompat.from(context).apply {
                val channel = NotificationChannel(
                    channelID,
                    "TestChannel",
                    NotificationManager.IMPORTANCE_HIGH
                )
                createNotificationChannel(channel)
                notify(100, notification)
            }
        }
    }
}
