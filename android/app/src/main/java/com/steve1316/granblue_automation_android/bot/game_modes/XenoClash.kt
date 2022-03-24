package com.steve1316.granblue_automation_android.bot.game_modes

import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game

class XenoClashException(message: String) : Exception(message)

class XenoClash(private val game: Game, private val missionName: String) {
	private val tag: String = "${loggerTag}XenoClash"

	/**
	 * Checks for Xeno Clash Nightmare and if it appeared and the user enabled it in settings, start it.
	 *
	 * @return True if Xeno Clash Nightmare was detected and successfully completed. False otherwise.
	 */
	fun checkForXenoClashNightmare(): Boolean {
		if (game.configData.enableNightmare && game.imageUtils.confirmLocation("limited_time_quests", tries = 3)) {
			// First check if the Nightmare is skippable.
			if (game.findAndClickButton("event_claim_loot", tries = 1)) {
				game.printToLog("\n[XENO] Skippable Xeno Clash Nightmare detected. Claiming it now...", tag = tag)
				game.collectLoot(isCompleted = false, isEventNightmare = true)
				return true
			} else {
				game.printToLog("\n[XENO] Detected Event Nightmare. Starting it now...", tag = tag)

				game.printToLog("\n********************", tag = tag)
				game.printToLog("********************", tag = tag)
				game.printToLog("[XENO] Xeno Clash Nightmare", tag = tag)
				game.printToLog("[XENO] Xeno Clash Nightmare Summons: ${game.configData.nightmareSummons}", tag = tag)
				game.printToLog("[XENO] Xeno Clash Nightmare Group Number: ${game.configData.nightmareGroupNumber}", tag = tag)
				game.printToLog("[XENO] Xeno Clash Nightmare Party Number: ${game.configData.nightmarePartyNumber}", tag = tag)
				game.printToLog("********************", tag = tag)
				game.printToLog("\n********************", tag = tag)

				// Tap the "Play Next" button to head to the Summon Selection screen.
				game.findAndClickButton("play_next")

				game.wait(1.0)

				// Select only the first Nightmare.
				val playRoundButtons = game.imageUtils.findAll("play_round_buttons")
				game.gestureUtils.tap(playRoundButtons[0].x, playRoundButtons[0].y, "play_round_buttons")

				game.wait(1.0)

				// Once the bot is at the Summon Selection screen, select your Summon and Party and start the mission.
				if (game.imageUtils.confirmLocation("select_a_summon")) {
					game.selectSummon(optionalSummonList = game.configData.nightmareSummons)
					val startCheck: Boolean = game.selectPartyAndStartMission(
						optionalGroupNumber = game.configData.nightmareGroupNumber, optionalPartyNumber = game.configData.nightmarePartyNumber,
						bypassFirstRun = true
					)

					// Once preparations are completed, start Combat Mode.
					if (startCheck && game.combatMode.startCombatMode(optionalCombatScript = game.configData.nightmareCombatScript)) {
						game.collectLoot(isCompleted = false, isEventNightmare = true)
						return true
					}
				}
			}
		} else if (!game.configData.enableNightmare && game.imageUtils.confirmLocation("limited_time_quests", tries = 3)) {
			// First check if the Nightmare is skippable.
			if (game.findAndClickButton("event_claim_loot", tries = 1)) {
				game.printToLog("\n[XENO] Skippable Xeno Clash Nightmare detected. Claiming it now...", tag = tag)
				game.collectLoot(isCompleted = false, isEventNightmare = true)
				return true
			} else {
				game.printToLog("\n[XENO] Xeno Clash Nightmare detected but user opted to not run it. Moving on...", tag = tag)
				game.findAndClickButton("close")
			}
		} else {
			game.printToLog("\n[XENO] No Xeno Clash Nightmare detected. Moving on...", tag = tag)
		}

		return false
	}

	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)

		game.printToLog("\n[XENO.CLASH] Now navigating to Xeno Clash...", tag = tag)

		// Go to the first banner that is usually the current Event by tapping on the "Menu" button.
		game.findAndClickButton("home_menu")
		game.wait(1.0)
		var bannerLocations = game.imageUtils.findAll("event_banner")
		if (bannerLocations.size == 0) {
			bannerLocations = game.imageUtils.findAll("event_banner_blue")
		}
		game.gestureUtils.tap(bannerLocations[0].x, bannerLocations[0].y, "event_banner")

		game.wait(3.0)

		if (game.findAndClickButton("xeno_special")) {
			game.wait(2.0)

			// Check if there is a Nightmare already available.
			val nightmareIsAvailable: Int = if (game.imageUtils.findButton("event_nightmare") != null) {
				1
			} else {
				0
			}

			// Find the locations of all the "Select" buttons.
			val selectButtonLocations = game.imageUtils.findAll("select")

			// Open up Event Quests or Event Raids. Offset by 1 if there is a Nightmare available.
			if (missionName == "Xeno Clash Extreme") {
				game.printToLog("[XENO.CLASH] Now hosting Xeno Clash Extreme...", tag = tag)
				game.gestureUtils.tap(selectButtonLocations[1 + nightmareIsAvailable].x, selectButtonLocations[1 + nightmareIsAvailable].y, "select")

				game.wait(1.0)

				val playRoundButtonLocations = game.imageUtils.findAll("play_round_button")
				game.gestureUtils.tap(playRoundButtonLocations[0].x, playRoundButtonLocations[0].y, "play_round_button")
			} else if (missionName == "Xeno Clash Raid") {
				game.printToLog("[XENO.CLASH] Now hosting Xeno Clash Raid...", tag = tag)
				game.gestureUtils.tap(selectButtonLocations[2 + nightmareIsAvailable].x, selectButtonLocations[2 + nightmareIsAvailable].y, "select")

				game.wait(1.0)

				game.findAndClickButton("play")
			}
		} else {
			throw XenoClashException("Failed to arrive at the Xeno Special section of the Special page.")
		}
	}

	/**
	 * Starts the process to complete a run for this Farming Mode and returns the number of items detected.
	 *
	 * @param firstRun Flag that determines whether or not to run the navigation process again. Should be False if the Farming Mode supports the "Play Again" feature for repeated runs.
	 */
	fun start(firstRun: Boolean) {
		// Start the navigation process.
		when {
			firstRun -> {
				navigate()
			}
			game.findAndClickButton("play_again") -> {
				if (game.checkForPopups()) {
					navigate()
				}
			}
			else -> {
				// If the bot cannot find the "Play Again" button, check for Pending Battles and then perform navigation again.
				game.checkPendingBattles()
				navigate()
			}
		}

		// Check for AP.
		game.checkAP()

		// Check if the bot is at the Summon Selection screen.
		if (game.imageUtils.confirmLocation("select_a_summon", tries = 30)) {
			if (game.selectSummon()) {
				// Select the Party.
				game.selectPartyAndStartMission()

				// Now start Combat Mode and detect any item drops.
				if (game.combatMode.startCombatMode()) {
					game.collectLoot(isCompleted = true)
				}
			}
		} else {
			throw XenoClashException("Failed to arrive at the Summon Selection screen.")
		}

		return
	}
}