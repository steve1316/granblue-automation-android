package com.steve1316.granblue_automation_android.utils

import android.util.Log
import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game
import java.util.*
import org.javacord.api.DiscordApiBuilder

import org.javacord.api.DiscordApi
import org.javacord.api.entity.channel.PrivateChannel
import org.javacord.api.entity.user.User
import org.javacord.api.entity.user.UserStatus


/**
 * This class takes care of notifying users of status updates via Discord private DMs.
 */
class DiscordUtils(val game: Game) {
	private val tag: String = "${loggerTag}DiscordUtils"

	companion object {
		val queue: Queue<String> = LinkedList()
		lateinit var client: DiscordApi
		lateinit var privateChannel: PrivateChannel

		fun disconnectClient() {
			if (this::client.isInitialized && client.status == UserStatus.ONLINE) {
				client.disconnect()
			}
		}
	}

	private fun sendMessage(message: String) {
		privateChannel.sendMessage(message).join()
	}

	fun main() {
		try {
			Log.d(tag, "Starting Discord process now...")

			client = DiscordApiBuilder().setToken(game.configData.discordToken).login().join()
			val user: User = client.getUserById(game.configData.discordUserID).join()
			privateChannel = user.openPrivateChannel().join()

			Log.d(tag, "Successfully fetched reference to user and their private channel.")

			queue.add("```diff\n+ Successful connection to Discord API for Granblue Automation Android\n```")

			// Loop and send any messages inside the Queue.
			while (true) {
				if (queue.isNotEmpty()) {
					val message = queue.remove()
					sendMessage(message)

					if (message.contains("Terminated connection to Discord API")) {
						break
					}
				}
			}

			Log.d(tag, "Terminated connection to Discord API.")
			disconnectClient()
		} catch (e: Exception) {
			Log.e(tag, "Failed to initialize JDA client: ${e.stackTraceToString()}")
			disconnectClient()
		}
	}
}