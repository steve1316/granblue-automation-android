package com.steve1316.granblue_automation_android.bot.game_modes

import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game

class ArcarumSandboxException(message: String) : Exception(message)

/**
 * Provides the navigation and any necessary utility functions to handle the Arcarum Replicard Sandbox game mode.
 */
class ArcarumSandbox(private val game: Game) {
	private val tag: String = "${loggerTag}ArcarumSandbox"

	private var firstRun: Boolean = true

	interface MissionInterface {
		val section: Int
		val x: Int
		val y: Int
	}

	class Mission(override val section: Int, override val x: Int, override val y: Int) : MissionInterface

	private val missionData: Map<String, Mission> = mapOf(
		// Zone Eletio
		"Slithering Seductress" to Mission(0, 760, 465),
		"Living Lightning Rod" to Mission(0, 145, 445),
		"Eletion Drake" to Mission(0, 250, 775),
		"Paradoxical Gate" to Mission(1, 695, 465),
		"Blazing Everwing" to Mission(1, 400, 430),
		"Death Seer" to Mission(1, 125, 605),
		"Hundred-Armed Hulk" to Mission(2, 680, 415),
		"Terror Trifecta" to Mission(2, 475, 585),
		"Rageborn One" to Mission(2, 640, 780),

		// Zone Faym
		"Trident Grandmaster" to Mission(0, 790, 475),
		"Hoarfrost Icequeen" to Mission(0, 470, 610),
		"Oceanic Archon" to Mission(0, 210, 770),
		"Farsea Predator" to Mission(1, 785, 475),
		"Faymian Fortress" to Mission(1, 465, 610),
		"Draconic Simulacrum" to Mission(1, 155, 475),
		"Azureflame Dragon" to Mission(2, 770, 485),
		"Eyes of Sorrow" to Mission(2, 715, 780),
		"Mad Shearwielder" to Mission(2, 140, 490),

		// Zone Goliath
		"Avatar of Avarice" to Mission(0, 640, 785),
		"Temptation's Guide" to Mission(0, 485, 385),
		"World's Veil" to Mission(0, 385, 605),
		"Goliath Keeper" to Mission(1, 800, 755),
		"Bloodstained Barbarian" to Mission(1, 590, 465),
		"Frenzied Howler" to Mission(1, 110, 425),
		"Goliath Vanguard" to Mission(1, 150, 770),
		"Vestige of Truth" to Mission(2, 845, 470),
		"Writhing Despair" to Mission(2, 565, 585),

		// Zone Harbinger
		"Vengeful Demigod" to Mission(0, 545, 410),
		"Dirgesinger" to Mission(0, 780, 525),
		"Wildwind Conjurer/Fullthunder Conjurer" to Mission(0, 485, 700),
		"Harbinger Simurgh" to Mission(0, 265, 585),
		"Harbinger Hardwood" to Mission(1, 830, 720),
		"Demanding Stormgod" to Mission(1, 610, 565),
		"Harbinger Tyrant" to Mission(2, 845, 455),
		"Phantasmagoric Aberration" to Mission(2, 525, 710),
		"Dimensional Riftwalker" to Mission(2, 260, 555)
	)

	/**
	 * Navigates to the specified Arcarum Replicard Sandbox mission inside the current Zone.
	 *
	 * @param skipToAction True if the mission is already selected. Defaults to False.
	 */
	private fun navigateToMission(skipToAction: Boolean = false) {
		game.printToLog("[ARCARUM.SANDBOX] Now beginning navigation to ${game.configData.missionName} inside ${game.configData.mapName}...", tag = tag)

		if (!skipToAction) {
			val section = missionData[game.configData.missionName]!!.section
			val x = missionData[game.configData.missionName]!!.x
			val y = missionData[game.configData.missionName]!!.y

			// Shift the Zone over to the right based on the section that the mission is located at.
			if (section == 1) {
				game.findAndClickButton("arcarum_sandbox_right_arrow")
			} else if (section == 2) {
				game.findAndClickButton("arcarum_sandbox_right_arrow")
				game.wait(1.0)
				game.findAndClickButton("arcarum_sandbox_right_arrow")
			}

			game.wait(1.0)

			// Now press the specified node that has the mission offset by the coordinates associated with it based off of the Home Menu button location.
			val homeLocation = game.imageUtils.findButton("home_menu")!!
			game.gestureUtils.tap(homeLocation.x - x, homeLocation.y + y, "arcarum_node")
		}

		game.wait(1.0)

		// If there is no Defender, then the first action is the mission itself. Else, it is the second action.
		val actionLocations = game.imageUtils.findAll("arcarum_sandbox_action")
		if (actionLocations.size == 1) {
			game.gestureUtils.tap(actionLocations[0].x, actionLocations[0].y, "arcarum_sandbox_action")
		} else {
			game.gestureUtils.tap(actionLocations[1].x, actionLocations[1].y, "arcarum_sandbox_action")
		}
	}

	/**
	 * Resets the position of the bot to be at the left-most edge of the map.
	 *
	 */
	private fun resetPosition() {
		game.printToLog("[ARCARUM.SANDBOX] Now determining if bot is starting all the way at the left edge of the Zone...", tag = tag)

		while (game.findAndClickButton("arcarum_sandbox_left_arrow", tries = 1, suppressError = true)) {
			game.wait(1.0)
		}

		game.printToLog("[ARCARUM.SANDBOX] Left edge of the Zone has been reached.", tag = tag)
	}

	/**
	 * Navigates to the specified Arcarum Replicard Sandbox Zone.
	 *
	 */
	private fun navigateToZone() {
		if (firstRun) {
			game.printToLog("\n[ARCARUM.SANDBOX] Now beginning navigation to ${game.configData.mapName}...", tag = tag)
			game.goBackHome()

			// Scroll up in case of the rare cases where refreshing the page lead to being loaded in on the bottom of the Home page.
			val tempFix: Boolean = game.imageUtils.findButton("home_menu", tries = 1) == null

			// Navigate to the Arcarum banner.
			var tries = 5
			while (tries > 0) {
				if (!game.findAndClickButton("arcarum_banner", tries = 1)) {
					if (tempFix) {
						game.gestureUtils.scroll(scrollDown = false)
					} else {
						game.gestureUtils.scroll()
					}

					game.wait(1.0)

					tries -= 1
					if (tries <= 0) {
						throw(IllegalStateException("Failed to navigate to Arcarum from the Home screen."))
					}
				} else {
					break
				}
			}

			firstRun = false
			game.wait(1.0)
		} else {
			game.wait(4.0)
		}

		// If the bot is not at Replicard Sandbox and instead is at regular Arcarum, navigate to Replicard Sandbox by clicking on its banner.
		if (!game.imageUtils.confirmLocation("arcarum_sandbox")) {
			game.gestureUtils.scroll()
			game.wait(1.0)
			game.findAndClickButton("arcarum_sandbox_banner")
		}

		game.wait(2.0)

		// Move to the Zone that the user's mission is at.
		when (game.configData.mapName) {
			"Zone Eletio" -> {
				game.findAndClickButton("arcarum_sandbox_zone_eletio")
			}
			"Zone Faym" -> {
				game.findAndClickButton("arcarum_sandbox_zone_faym")
			}
			"Zone Goliath" -> {
				game.findAndClickButton("arcarum_sandbox_zone_goliath")
			}
			"Zone Harbinger" -> {
				game.findAndClickButton("arcarum_sandbox_zone_harbinger")
			}
			else -> {
				throw ArcarumSandboxException("Invalid map name provided for Arcarum Replicard Sandbox navigation.")
			}
		}

		game.wait(2.0)

		// Now that the Zone is on screen, have the bot move all the way to the left side of the map.
		resetPosition()

		// Finally, select the mission.
		navigateToMission()
	}

	/**
	 * Refills AAP if necessary.
	 *
	 */
	private fun refillAAP() {
		if (game.imageUtils.confirmLocation("aap")) {
			game.printToLog("\n[ARCARUM.SANDBOX] Bot ran out of AAP. Refilling now...", tag = tag)

			val useLocations = game.imageUtils.findAll("use")
			game.gestureUtils.tap(useLocations[0].x, useLocations[0].y, "use")

			game.wait(1.0)
			game.findAndClickButton("ok")
			game.wait(1.0)

			game.printToLog("[ARCARUM.SANDBOX] AAP is now refilled.", tag = tag)
		}
	}

	/**
	 * Starts the process of completing Arcarum expeditions.
	 *
	 */
	fun start() {
		// Start the navigation process.
		if (firstRun) {
			navigateToZone()
		} else if (!game.findAndClickButton("play_again")) {
			if (game.checkPendingBattles()) {
				firstRun = true
				navigateToZone()
			} else {
				// If the bot cannot find the "Play Again" button, click the Expedition button.
				game.findAndClickButton("expedition")

				// Wait out the animations that play, whether it be Treasure or Defender spawning.
				game.wait(5.0)

				// Click away the Treasure popup if it shows up.
				game.findAndClickButton("ok", suppressError = true)

				// Start the mission again.
				game.wait(3.0)
				navigateToMission(skipToAction = true)
			}
		}

		// Refill AAP if needed.
		refillAAP()

		game.wait(3.0)

		if (game.selectPartyAndStartMission()) {
			if (game.combatMode.startCombatMode()) {
				game.collectLoot(isCompleted = true)
			}
		}
	}
}