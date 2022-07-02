package com.steve1316.granblue_automation_android.utils

import android.util.Log
import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.channel.PrivateChannel
import org.javacord.api.entity.user.User
import java.util.*


/**
 * This class takes care of notifying users of status updates via Discord private DMs.
 */
class DiscordUtils(val game: Game) {
	private val tag: String = "${loggerTag}DiscordUtils"

	companion object {
		val queue: Queue<String> = LinkedList()
		lateinit var client: DiscordApi
		var privateChannel: PrivateChannel? = null
	}

	private fun sendMessage(message: String) {
		privateChannel?.sendMessage(message)?.join()
	}

	fun main() {
		Log.d(tag, "Starting Discord process now...")

		try {
			client = DiscordApiBuilder().setToken(game.configData.discordToken).login().join()
		} catch (e: Exception) {
			Log.d(tag, "[DISCORD] Failed to connect to Discord API using provided token.")
			return
		}

		val user: User
		try {
			user = client.getUserById(game.configData.discordUserID).join()
		} catch (e: Exception) {
			Log.d(tag, "[DISCORD] Failed to find user using provided user ID.")
			return
		}

		try {
			privateChannel = user.openPrivateChannel().join()
		} catch (e: Exception) {
			Log.d(tag, "[DISCORD] Failed to open private channel with user.")
			return
		}

		Log.d(tag, "Successfully fetched reference to user and their private channel.")

		queue.add("```diff\n+ Successful connection to Discord API for Granblue Automation Android\n```")

		try {
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
		} catch (e: Exception) {
			Log.e(tag, e.stackTraceToString())
		}
	}
}