package com.steve1316.granblueautomation_android.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DiscordUtils(myContext: Context) {
	private val TAG: String = "GAA_DiscordUtils"
	
	private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)
	private val discordToken: String = sharedPreferences.getString("discordToken", "")!!
	private val userID: String = sharedPreferences.getString("userID", "")!!
	
	companion object {
		val queue: Queue<String> = LinkedList()
	}
	
	suspend fun main(): Unit = coroutineScope {
		val client = Kord(discordToken)
		
		client.on<ReadyEvent> {
			val snowflake = Snowflake(userID)
			val dmChannel = client.getUser(snowflake)?.getDmChannelOrNull()!!
			
			val now = LocalDateTime.now()
			val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
			val formatted = now.format(formatter)
			Log.d(TAG, "Connection to Discord API successful for Granblue Automation Android!")
			queue.add("--------------------\n[${formatted}] Mobile connection to Discord API successful Granblue Automation Android!")
			
			while (true) {
				if (queue.isNotEmpty()) {
					val message = queue.remove()
					dmChannel.createMessage(message)
					
					if (message.contains("Disconnected from Discord API")) {
						break
					}
				}
				
				delay(1000L)
			}
			
			Log.d(TAG, "Shutting down Discord API connection for Granblue Automation Android...")
			client.logout()
		}
		
		client.login()
	}
}