package com.steve1316.granblue_automation_android.bot.game_modes

import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game

class ProvingGroundsException(message: String) : Exception(message)

class ProvingGrounds(private val game: Game, private val missionName: String) {
	private val tag: String = "${loggerTag}ProvingGrounds"

	private var firstTime: Boolean = true

	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		game.printToLog("\n[PROVING.GROUNDS] Now beginning process to navigate to the mission: $missionName...", tag = tag)

		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)

		// Go to the first banner that is usually the current Event by tapping on the "Menu" button.
		game.findAndClickButton("home_menu")
		var bannerLocations = game.imageUtils.findAll("event_banner")
		if (bannerLocations.size == 0) {
			bannerLocations = game.imageUtils.findAll("event_banner_blue")
		}
		game.gestureUtils.tap(bannerLocations[0].x, bannerLocations[0].y, "event_banner")

		game.wait(3.0)

		// Select the difficulty.
		if (game.imageUtils.confirmLocation("proving_grounds")) {
			if (game.findAndClickButton("proving_grounds_missions")) {
				val playRoundButtonLocations = game.imageUtils.findAll("play_round_button")

				when (missionName) {
					"Extreme" -> {
						game.gestureUtils.tap(playRoundButtonLocations[1].x, playRoundButtonLocations[1].y, "play_round_button")
					}
					"Extreme+" -> {
						game.gestureUtils.tap(playRoundButtonLocations[2].x, playRoundButtonLocations[2].y, "play_round_button")
					}
				}

				// After the difficulty has been selected, tap "Play" to land the bot at the Proving Grounds Summon Selection screen.
				game.findAndClickButton("play")
			}
		}
	}

	/**
	 * Starts the process to complete a run for this Farming Mode and returns the number of items detected.
	 *
	 * @param firstRun Flag that determines whether or not to run the navigation process again. Should be False if the Farming Mode supports the "Play Again" feature for repeated runs.
	 * @return Number of items detected.
	 */
	fun start(firstRun: Boolean): Int {
		var runsCompleted = 0

		// Start the navigation process.
		when {
			firstRun && firstTime -> {
				navigate()
			}
			firstTime && game.findAndClickButton("play_again") -> {
				game.printToLog("\n[PROVING.GROUNDS] Starting Proving Grounds Mission again...", tag = tag)
			}
		}

		// Check for AP.
		game.checkAP()

		// Check if the bot is at the Summon Selection screen.
		if ((firstRun || firstTime) && game.imageUtils.confirmLocation("proving_grounds_summon_selection")) {
			if (game.selectSummon()) {
				game.wait(1.0)

				// No need to select a Party. Just click "OK" to start the mission and confirming the selected summon.
				game.findAndClickButton("ok")

				game.wait(1.0)

				game.printToLog("\n[PROVING.GROUNDS] Now starting Mission for Proving Grounds...", tag = tag)
				game.findAndClickButton("proving_grounds_start")

				// Now start Combat Mode and detect any item drops.
				if (game.combatMode.startCombatMode(game.combatScript)) {
					game.collectLoot(isCompleted = false)

					// Tap the "Next Battle" button if there are any battles left.
					if (game.findAndClickButton("proving_grounds_next_battle")) {
						game.printToLog("\n[PROVING.GROUNDS] Moving onto the next battle for Proving Grounds...", tag = tag)
						game.findAndClickButton("ok")
						firstTime = false
					}
				}
			}
		} else if (!firstRun && !firstTime) {
			// No need to select a Summon again as it is reused.
			if (game.combatMode.startCombatMode(game.combatScript)) {
				game.collectLoot(isCompleted = false)

				// Tap the "Next Battle" button if there are any battles left.
				if (game.findAndClickButton("proving_grounds_next_battle")) {
					game.printToLog("\n[PROVING.GROUNDS] Moving onto the next battle for Proving Grounds...", tag = tag)
					game.findAndClickButton("ok")
				} else {
					// Otherwise, all battles for the Mission has been completed. Collect the completion rewards at the end.
					game.printToLog("\n[PROVING.GROUNDS] Proving Grounds Mission has been completed.", tag = tag)
					game.findAndClickButton("event")

					// Check for friend request.
					game.findAndClickButton("cancel", tries = 1, suppressError = true)

					// Check for trophy.
					game.findAndClickButton("close", tries = 1, suppressError = true)

					game.wait(2.0)
					game.findAndClickButton("proving_grounds_open_chest")
					game.findAndClickButton("proving_grounds_open_chest")

					if (game.imageUtils.confirmLocation("proving_grounds_completion_loot")) {
						game.printToLog("\n[PROVING.GROUNDS] Completion rewards has been acquired.", tag = tag)
						runsCompleted = game.collectLoot(isCompleted = true, skipPopupCheck = true)

						// Reset the First Time flag so the bot can select a Summon and select the Mission again.
						if (game.itemAmountFarmed < game.itemAmount) {
							firstTime = true
						}
					} else {
						throw ProvingGroundsException("Failed to detect the Completion Loot screen for completing this Proving Grounds mission.")
					}
				}
			}
		} else {
			throw ProvingGroundsException("Failed to arrive at the Summon Selection screen.")
		}

		return runsCompleted
	}
}