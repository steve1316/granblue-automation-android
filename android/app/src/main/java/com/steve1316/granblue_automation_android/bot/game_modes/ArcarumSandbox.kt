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

	// The x and y coordinates are the difference between the center of the Menu button at the top-right and the center of the node itself.
	// The section refers to the left most page that the node is located in starting at page 0.
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
		"Eletion Glider" to Mission(2, 140, 560),

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
		"Faymian Gun" to Mission(2, 460, 605),

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
		"Goliath Triune" to Mission(2, 115, 715),

		// Zone Harbinger
		"Vengeful Demigod" to Mission(0, 545, 410),
		"Dirgesinger" to Mission(0, 780, 525),
		"Wildwind Conjurer/Fullthunder Conjurer" to Mission(0, 485, 700),
		"Harbinger Simurgh" to Mission(0, 265, 585),
		"Harbinger Hardwood" to Mission(1, 830, 720),
		"Demanding Stormgod" to Mission(1, 610, 565),
		"Harbinger Stormer" to Mission(1, 415, 355),
		"Harbinger Tyrant" to Mission(2, 845, 455),
		"Phantasmagoric Aberration" to Mission(2, 525, 710),
		"Dimensional Riftwalker" to Mission(2, 260, 555),

		// Zone Invidia
		"Infernal Hellbeast" to Mission(0, 795, 475),
		"Spikeball" to Mission(0, 265, 580),
		"Blushing Groom" to Mission(0, 110, 410),
		"Unworldly Guardian" to Mission(1, 660, 580),
		"Deva of Wisdom" to Mission(1, 415, 295),
		"Sword Aberration" to Mission(1, 395, 505),
		"Athena Militis" to Mission(1, 410, 745),

		// Zone Joculator
		"Glacial Hellbeast" to Mission(0, 110, 425),
		"Giant Sea Plant" to Mission(0, 855, 635),
		"Maiden of the Depths" to Mission(0, 330, 760),
		"Bloody Soothsayer" to Mission(1, 595, 765),
		"Nebulous One" to Mission(1, 70, 635),
		"Dreadful Scourge" to Mission(1, 535, 805),
		"Grani Militis" to Mission(1, 445, 560),

		// Zone Kalendae
		"Bedeviled Plague" to Mission(1, 675, 405),
		"Tainted Hellmaiden" to Mission(1, 230, 760),
		"Watcher from Above" to Mission(1, 45, 480),
		"Scintillant Matter" to Mission(0, 820, 545),
		"Ebony Executioner" to Mission(0, 575, 315),
		"Hellbeast of Doom" to Mission(0, 280, 770),
		"Baal Militis" to Mission(0, 455, 540),

		// Zone Liber
		"Mounted Toxophilite" to Mission(0, 515, 325),
		"Beetle of Damnation" to Mission(0, 520, 780),
		"Ageless Guardian Beast" to Mission(0, 280, 565),
		"Solar Princess" to Mission(1, 750, 605),
		"Drifting Blade Demon" to Mission(1, 510, 335),
		"Simpering Beast" to Mission(1, 505, 760),
		"Garuda Militis" to Mission(1, 95, 545)
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
		} else if (game.configData.enableDefender && game.configData.numberOfDefeatedDefenders < game.configData.numberOfDefenders) {
			game.gestureUtils.tap(actionLocations[0].x, actionLocations[0].y, "arcarum_sandbox_action")
			game.printToLog("\n[ARCARUM.SANDBOX] Found Defender and fighting it...", tag = tag)
			game.configData.engagedDefenderBattle = true
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
			var tries = 30
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
		} else {
			game.wait(4.0)
		}

		// If the bot is not at Replicard Sandbox and instead is at regular Arcarum, navigate to Replicard Sandbox by clicking on its banner.
		if (!game.imageUtils.confirmLocation("arcarum_sandbox")) {
			game.gestureUtils.scroll()
			game.wait(1.0)
			game.findAndClickButton("arcarum_sandbox_banner")
		}

		// Move to the Zone that the user's mission is at.
		val navigationCheck: Boolean = when (game.configData.mapName) {
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
			"Zone Invidia" -> {
				game.findAndClickButton("arcarum_sandbox_zone_invidia")
			}
			"Zone Joculator" -> {
				game.findAndClickButton("arcarum_sandbox_zone_joculator")
			}
			"Zone Kalendae" -> {
				game.findAndClickButton("arcarum_sandbox_zone_kalendae")
			}
			"Zone Liber" -> {
				game.findAndClickButton("arcarum_sandbox_zone_liber")
			}
			else -> {
				throw ArcarumSandboxException("Invalid map name provided for Arcarum Replicard Sandbox navigation.")
			}
		}

		if (!navigationCheck) {
			throw ArcarumSandboxException("Failed to navigate into the Sandbox Zone.")
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
		if (game.imageUtils.confirmLocation("aap", tries = 10)) {
			game.printToLog("\n[ARCARUM.SANDBOX] Bot ran out of AAP. Refilling now...", tag = tag)
			val useLocations = game.imageUtils.findAll("use")
			game.gestureUtils.tap(useLocations[1].x, useLocations[1].y, "use")

			game.wait(1.0)
			game.findAndClickButton("ok")
			game.wait(1.0)

			game.printToLog("[ARCARUM.SANDBOX] AAP is now refilled.", tag = tag)
		}
	}

	/**
	 * Clicks on Play if you are fighting a zone boss.
	 *
	 */
	private fun playZoneBoss() {
		val playButton = game.imageUtils.findButton("play")
		if (playButton != null) {
			game.printToLog("\n[ARCARUM.SANDBOX] Now fighting zone boss...", tag)
			game.gestureUtils.tap(playButton.x, playButton.y, "play")
		}
	}

	/**
	 * Clicks on a gold chest. If it is a mimic, fight it, if not, click ok. Courtesy of KoiKomei.
	 *
	 */
	private fun openGoldChest() {
		val actionLocations = game.imageUtils.findAll("arcarum_sandbox_action")
		game.gestureUtils.tap(actionLocations[0].x, actionLocations[0].y, "arcarum_sandbox_action")
		game.findAndClickButton("ok")
		game.wait(3.0)
		if (!game.findAndClickButton("ok", suppressError = true)) {
			game.gestureUtils.tap(actionLocations[0].x, actionLocations[0].y, "arcarum_sandbox_action")
			game.wait(3.0)
			if (game.selectPartyAndStartMission()) {
				if (game.combatMode.startCombatMode()) {
					game.collectLoot(isCompleted = true)
				}
			}
			game.findAndClickButton("expedition")
		}

		game.wait(2.0)
		resetPosition()
		navigateToMission()
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
				if (game.configData.enableGoldChest && game.findAndClickButton("arcarum_gold_chest")) {
					openGoldChest()
				} else {
					// Start the mission again.
					game.wait(3.0)
					navigateToMission(skipToAction = true)
				}
			}
		}

		// Refill AAP if needed.
		playZoneBoss()
		refillAAP()

		game.wait(3.0)

		if (game.configData.engagedDefenderBattle) {
			if (game.selectPartyAndStartMission(game.configData.defenderGroupNumber, game.configData.defenderPartyNumber, bypassFirstRun = true)) {
				if (game.combatMode.startCombatMode(game.configData.defenderCombatScript)) {
					game.collectLoot(isCompleted = true, isDefender = game.configData.engagedDefenderBattle)
				}
			}
		} else {
			if (game.selectPartyAndStartMission()) {
				if (game.combatMode.startCombatMode()) {
					game.collectLoot(isCompleted = true)
				}
			}
		}
	}
}