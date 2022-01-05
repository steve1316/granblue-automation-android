package com.steve1316.granblue_automation_android.bot.game_modes

import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game

class GenericException(message: String) : Exception(message)

class Generic(private val game: Game) {
	private val tag: String = "${loggerTag}Generic"

	/**
	 * Starts the process of completing a generic setup that supports the 'Play Again' logic.
	 *
	 * @return Number of runs completed.
	 */
	fun start(): Int {
		var runsCompleted = 0

		game.printToLog("\n[GENERIC] Now checking for run eligibility...", tag = tag)

		// Bot can start either at the Combat screen with the "Attack" button visible or the Loot Collection screen with the "Play Again" button visible.
		when {
			game.imageUtils.findButton("attack", tries = 5) != null -> {
				game.printToLog("\n[GENERIC] Bot is at the Combat screen. Starting Combat Mode now...", tag = tag)
				if (game.combatMode.startCombatMode(game.combatScript)) {
					runsCompleted = game.collectLoot(isCompleted = true)
				}
			}
			else -> {
				game.printToLog("\n[GENERIC] Bot is not at the Combat screen. Checking for the Loot Collection screen now...", tag = tag)

				// Press the "Play Again" button if necessary, otherwise start Combat Mode.
				if (game.findAndClickButton("play_again")) {
					game.checkForPopups()
				} else {
					throw GenericException(
						"Failed to detect the 'Play Again' button. Bot can start either at the Combat screen with the 'Attack' button visible or the Loot Collection screen with " +
								"the 'Play Again' button visible..."
					)
				}

				// Check for AP.
				game.checkAP()

				// Check if the bot is at the Summon Selection screen.
				if (game.imageUtils.confirmLocation("select_a_summon")) {
					if (game.selectSummon()) {
						// Do not select party and just commence the mission.
						if (game.findAndClickButton("ok", tries = 10)) {
							game.wait(1.0)

							// Now start Combat Mode and detect any item drops.
							if (game.combatMode.startCombatMode(game.combatScript)) {
								runsCompleted = game.collectLoot(isCompleted = true)
							}
						} else {
							throw GenericException("Failed to skip party selection.")
						}
					}
				} else {
					throw EventException("Failed to arrive at the Summon Selection screen.")
				}
			}
		}

		return runsCompleted
	}
}