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
			queue.add("```diff\n+ Successful connection to Discord API for Granblue Automation Android\n```")
			
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