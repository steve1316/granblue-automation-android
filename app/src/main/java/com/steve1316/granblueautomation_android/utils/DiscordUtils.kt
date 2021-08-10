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

/**
 * This class takes care of notifying users of status updates via Discord private DMs.
 */
class DiscordUtils(myContext: Context) {
	private val TAG: String = "GAA_DiscordUtils"
	
	private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)
	private val discordToken: String = sharedPreferences.getString("discordToken", "")!!
	private val userID: String = sharedPreferences.getString("userID", "")!!
	
	companion object {
		val queue: Queue<String> = LinkedList()
		
		lateinit var client: Kord
		
		suspend fun disconnectClient() {
			client.logout()
		}
	}
	
	suspend fun main(): Unit = coroutineScope {
		// Initialize the client with the Bot Account's token.
		client = Kord(discordToken)
		
		// This listener gets fired when the client is connected to the Discord API.
		client.on<ReadyEvent> {
			// Get the user's private DM channel via their Snowflake.
			val snowflake = Snowflake(userID)
			val dmChannel = client.getUser(snowflake)?.getDmChannelOrNull()!!
			
			Log.d(TAG, "Successful connection to Discord API.")
			queue.add("```diff\n+ Successful connection to Discord API for Granblue Automation Android\n```")
			
			// Loop and send any messages inside the Queue.
			while (true) {
				if (queue.isNotEmpty()) {
					val message = queue.remove()
					dmChannel.createMessage(message)
					
					if (message.contains("Terminated connection to Discord API")) {
						break
					}
				}
			}
			
			Log.d(TAG, "Terminated connection to Discord API.")
			client.logout()
		}
		
		// Login to the Discord API. This will block this Thread but will allow the onReadyEvent listener to continue running.
		client.login()
	}
}