package com.steve1316.granblue_automation_android.bot.game_modes

import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game

class DreadBarrageException(message: String) : Exception(message)

class DreadBarrage(private val game: Game, private val missionName: String) {
	private val tag: String = "${loggerTag}DreadBarrage"

	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)

		// Scroll the screen down a little bit and then click on the Dread Barrage banner.
		game.printToLog("\n[DREAD.BARRAGE] Now navigating to Dread Barrage...", tag = tag)
		game.gestureUtils.swipe(500f, 1000f, 500f, 700f)
		game.findAndClickButton("dread_barrage")

		game.wait(3.0)

		if (game.imageUtils.confirmLocation("dread_barrage")) {
			// Check if there is already a hosted Dread Barrage mission.
			if (game.imageUtils.confirmLocation("resume_quests")) {
				game.printToLog("\n[WARNING] Detected that there is already a hosted Dread Barrage mission.", tag = tag)
				var expiryTimeInSeconds = 0

				while (game.imageUtils.confirmLocation("resume_quests", tries = 1)) {
					// The bot will wait for a total of 1 hour and 30 minutes for either the Raid's timer to expire or for anyone else in the room to clear it.
					game.printToLog("\n[WARNING] The bot will now either wait for the expiry time of 1 hour and 30 minutes or for someone else in the room to clear it.", tag = tag)
					game.printToLog("[WARNING] The bot will now refresh the page every 30 seconds to check if it is still there before proceeding.", tag = tag)
					game.printToLog("User can either wait it out, revive and fight it to completion, or retreat from the mission manually.", tag = tag)

					game.wait(30.0)

					game.findAndClickButton("reload")
					game.wait(2.0)

					expiryTimeInSeconds += 30
					if (expiryTimeInSeconds >= 5400) {
						break
					}
				}

				game.printToLog("\n[SUCCESS] Hosted Dread Barrage mission is now gone either because of timeout or someone else in the room killed it. Moving on...", tag = tag)
			}

			// Find the locations of all the "Play" buttons at the top of the window.
			val dreadBarragePlayButtonLocations = game.imageUtils.findAll("dread_barrage_play")

			var difficulty = ""
			when {
				missionName.contains("1 Star") -> {
					difficulty = "1 Star"
				}
				missionName.contains("2 Star") -> {
					difficulty = "2 Star"
				}
				missionName.contains("3 Star") -> {
					difficulty = "3 Star"
				}
				missionName.contains("4 Star") -> {
					difficulty = "4 Star"
				}
				missionName.contains("5 Star") -> {
					difficulty = "5 Star"
				}
			}

			// Now select the chosen difficulty.
			when (difficulty) {
				"1 Star" -> {
					game.printToLog("[DREAD.BARRAGE] Now starting 1 Star Dread Barrage Raid...", tag = tag)
					game.gestureUtils.tap(dreadBarragePlayButtonLocations[0].x, dreadBarragePlayButtonLocations[0].y, "dread_barrage_play")
				}
				"2 Star" -> {
					game.printToLog("[DREAD.BARRAGE] Now starting 2 Star Dread Barrage Raid...", tag = tag)
					game.gestureUtils.tap(dreadBarragePlayButtonLocations[1].x, dreadBarragePlayButtonLocations[1].y, "dread_barrage_play")
				}
				"3 Star" -> {
					game.printToLog("[DREAD.BARRAGE] Now starting 3 Star Dread Barrage Raid...", tag = tag)
					game.gestureUtils.tap(dreadBarragePlayButtonLocations[2].x, dreadBarragePlayButtonLocations[2].y, "dread_barrage_play")
				}
				"4 Star" -> {
					game.printToLog("[DREAD.BARRAGE] Now starting 4 Star Dread Barrage Raid...", tag = tag)
					game.gestureUtils.tap(dreadBarragePlayButtonLocations[3].x, dreadBarragePlayButtonLocations[3].y, "dread_barrage_play")
				}
				"5 Star" -> {
					game.printToLog("[DREAD.BARRAGE] Now starting 5 Star Dread Barrage Raid...", tag = tag)
					game.gestureUtils.tap(dreadBarragePlayButtonLocations[4].x, dreadBarragePlayButtonLocations[4].y, "dread_barrage_play")
				}
			}

			game.wait(2.0)
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
				game.checkForPopups()
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
			throw DreadBarrageException("Failed to arrive at the Summon Selection screen.")
		}

		return
	}
}