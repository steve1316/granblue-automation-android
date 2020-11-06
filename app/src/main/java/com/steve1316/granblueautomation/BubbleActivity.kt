package com.steve1316.granblueautomation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.drawable.toBitmap

/**
 * BubbleActivity - Uses NotificationUtility class to create a Bubble notification to be shown to the user.
 */
class BubbleActivity : AppCompatActivity() {
    private lateinit var notificationUtility: NotificationUtility

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floating)

        val bitmap = ContextCompat.getDrawable(this, R.drawable.ic_settings_24)?.toBitmap()
        val icon = IconCompat.createWithAdaptiveBitmap(bitmap)

        // Initialize NotificationUtility and create the Bubble and Person objects.
        notificationUtility = NotificationUtility(this)
        val bubble = notificationUtility.createBubble(icon)
        val person = notificationUtility.createPerson("Bot")

        // Create the notification/Bubble.
        val notification = notificationUtility.createNotification(
            "Tap Me to Get Started",
            R.drawable.ic_android_icon,
            bitmap,
            "Bot",
            bubbleData = bubble,
            personObject = person
        )

        // Show the notification/Bubble to the user.
        notificationUtility.showNotification(notification)

        Log.d("[DEBUG]", "Bubble Activity successfully started.")
    }
}