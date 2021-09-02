package com.steve1316.granblueautomation_android.bot.game_modes

import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.bot.Game

/**
 * Provides the navigation and any necessary utility functions to handle the Arcarum game mode.
 */
class Arcarum(
	private val game: Game,
	private val map: String,
	private val groupNumber: Int,
	private val partyNumber: Int,
	private val numberOfRuns: Int = 1,
	private val combatScript: List<String> = arrayListOf()
) {
	private val TAG: String = "${MainActivity.loggerTag}_Arcarum"
	
	private var firstRun: Boolean = true
	
	/**
	 * Navigates to the specified Arcarum expedition.
	 *
	 * @return True if the bot was able to start/resume the expedition. False otherwise.
	 */
	private fun navigate(): Boolean {
		if (firstRun) {
			game.printToLog("\n[ARCARUM] Now beginning navigation to $map.", MESSAGE_TAG = TAG)
			game.goBackHome()
			game.wait(2.0)
			
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
		} else {
			game.wait(4.0)
		}
		
		// Now make sure that the Extreme difficulty is selected.
		game.wait(1.0)
		
		// Confirm the completion popup if it shows up.
		if (game.imageUtils.confirmLocation("arcarum_expedition", tries = 1)) {
			game.findAndClickButton("ok")
		}
		
		game.findAndClickButton("arcarum_extreme")
		
		// Finally, navigate to the specified map to start it.
		game.printToLog("[ARCARUM] Now starting the specified expedition: $map", MESSAGE_TAG = TAG)
		val formattedMapName: String = map.lowercase().replace(" ", "_")
		if (!game.findAndClickButton("arcarum_${formattedMapName}", tries = 5)) {
			// Resume the expedition if it is already in-progress.
			game.findAndClickButton("arcarum_exploring")
		} else if (game.imageUtils.confirmLocation("arcarum_departure_check", tries = 3)) {
			game.printToLog("[ARCARUM] Now using 1 Arcarum ticket to start this expedition...", MESSAGE_TAG = TAG)
			val resultCheck = game.findAndClickButton("start_expedition")
			game.wait(6.0)
			return resultCheck
		} else if (game.findAndClickButton("resume")) {
			game.wait(3.0)
			return true
		} else {
			throw(IllegalStateException("Failed to encounter the Departure Check to confirm starting the expedition."))
		}
		
		return false
	}
	
	/**
	 * Chooses the next action to take for the current Arcarum expedition.
	 *
	 * @return The action to take next.
	 */
	private fun chooseAction(): String {
		// Determine what action to take.
		game.printToLog("\n[ARCARUM] Now determining what action to take...", MESSAGE_TAG = TAG)
		
		// Wait a second in case the "Do or Die" animation plays.
		game.wait(1.0)
		
		var tries = 3
		while (tries > 0) {
			if (checkForBoss()) {
				return "Boss Detected"
			}
			
			// Prioritise any enemies/chests/thorns that are available on the current node.
			if (game.findAndClickButton("arcarum_action", tries = 1)) {
				game.wait(2.0)
				
				game.checkForCAPTCHA()
				
				return when {
					game.imageUtils.confirmLocation("arcarum_party_selection", tries = 1) -> {
						"Combat"
					}
					game.findAndClickButton("ok", tries = 1) -> {
						"Claimed Treasure/Keythorn"
					}
					else -> {
						"Claimed Spirethorn"
					}
				}
			}
			
			if (game.imageUtils.confirmLocation("arcarum_treasure", tries = 1)) {
				game.findAndClickButton("ok")
				return "Claimed Treasure"
			}
			
			// Next, determine if there is a available node to move to. Any bound monsters should have been destroyed by now.
			if (game.findAndClickButton("arcarum_node", tries = 1)) {
				game.wait(1.0)
				return "Navigating"
			}
			
			// If all else fails, attempt to navigate to a node that is occupied by mob(s).
			if (game.findAndClickButton("arcarum_mob", tries = 1) || game.findAndClickButton("arcarum_red_mob", tries = 1)) {
				game.wait(1.0)
				return "Navigating"
			}
			
			tries -= 1
		}
		
		return "Next Area"
	}
	
	/**
	 * Checks for the existence of 3-3, 6-3 or 9-3 boss.
	 *
	 * @return Flag on whether or not a Boss was detected.
	 */
	private fun checkForBoss(): Boolean {
		game.printToLog("\n[ARCARUM] Checking if boss is available...")
		
		return game.imageUtils.findButton("arcarum_boss", tries = 1) != null || game.imageUtils.findButton("arcarum_boss2", tries = 1) != null
	}
	
	/**
	 * Starts the process of completing Arcarum expeditions.
	 *
	 * @return True if the number of completed runs has been reached. False otherwise.
	 */
	fun start(): Boolean {
		var runsCompleted = 0
		while (runsCompleted < numberOfRuns) {
			navigate()
			
			while (true) {
				val action = chooseAction()
				game.printToLog("[ARCARUM] Action to take will be: $action", MESSAGE_TAG = TAG)
				
				if (action == "Combat") {
					// Start Combat Mode.
					if (game.selectPartyAndStartMission(groupNumber, partyNumber)) {
						if (game.imageUtils.confirmLocation("elemental_damage", tries = 1)) {
							throw(IllegalStateException("Encountered an important mob for Arcarum and the selected party does not conform to the enemy's weakness. Perhaps you would like to do this battle yourself?"))
						} else if (game.imageUtils.confirmLocation("arcarum_restriction", tries = 1)) {
							throw(IllegalStateException("Encountered a party restriction for Arcarum. Perhaps you would like to complete this section by yourself?"))
						}
						
						game.wait(3.0)
						if (game.combatMode.startCombatMode(combatScript)) {
							game.collectLoot(skipInfo = true)
							game.findAndClickButton("expedition")
						}
					}
				} else if (action == "Navigating") {
					// Move to the next available node.
					game.findAndClickButton("move")
				} else if (action == "Next Area") {
					// Either navigate to the next area or confirm the expedition's conclusion.
					if (game.findAndClickButton("arcarum_next_stage")) {
						game.findAndClickButton("ok")
						game.printToLog("[ARCARUM] Moving to the next area...", MESSAGE_TAG = TAG)
					} else if (game.findAndClickButton("arcarum_checkpoint")) {
						game.findAndClickButton("arcarum")
						game.printToLog("[ARCARUM] Expedition is complete", MESSAGE_TAG = TAG)
						runsCompleted += 1
						break
					}
				} else if (action == "Boss Detected") {
					game.printToLog("[ARCARUM] Boss has been detected. Stopping the bot.")
					return true
				}
				
				game.wait(1.0)
			}
		}
		
		return true
	}
}