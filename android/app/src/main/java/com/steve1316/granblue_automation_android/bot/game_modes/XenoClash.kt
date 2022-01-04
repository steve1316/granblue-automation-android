package com.steve1316.granblue_automation_android.bot.game_modes

import androidx.preference.PreferenceManager
import com.steve1316.granblue_automation_android.bot.Game

class XenoClashException(message: String) : Exception(message)

class XenoClash(private val game: Game, private val missionName: String) {
	private val tag: String = "${com.steve1316.granblue_automation_android.MainActivity.loggerTag}XenoClash"
	
	private val enableXenoClashNightmare: Boolean
	private var xenoClashNightmareSummonList: List<String>
	private var xenoClashNightmareGroupNumber: Int
	private var xenoClashNightmarePartyNumber: Int
	
	init {
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(game.myContext)
		enableXenoClashNightmare = sharedPreferences.getBoolean("enableXenoClashNightmare", false)
		xenoClashNightmareSummonList = sharedPreferences.getStringSet("xenoClashNightmareSummonList", setOf<String>())!!.toList()
		xenoClashNightmareGroupNumber = sharedPreferences.getInt("xenoClashNightmareGroupNumber", 0)
		xenoClashNightmarePartyNumber = sharedPreferences.getInt("xenoClashNightmarePartyNumber", 0)
		
		if (game.itemName == "Repeated Runs" && enableXenoClashNightmare) {
			game.printToLog("\n[XENO.CLASH] Initializing settings for Xeno Clash Nightmare...", tag = tag)
			
			if (xenoClashNightmareSummonList.isEmpty()) {
				game.printToLog("[XENO.CLASH] Summons for Xeno Clash Nightmare will reuse the ones for Farming Mode.", tag = tag)
				xenoClashNightmareSummonList = game.summonList
			}
			
			if (xenoClashNightmareGroupNumber == 0) {
				game.printToLog("[XENO.CLASH] Group Number for Xeno Clash Nightmare will reuse the ones for Farming Mode.", tag = tag)
				xenoClashNightmareGroupNumber = game.groupNumber
			}
			
			if (xenoClashNightmarePartyNumber == 0) {
				game.printToLog("[XENO.CLASH] Party Number for Xeno Clash Nightmare will reuse the ones for Farming Mode.", tag = tag)
				xenoClashNightmarePartyNumber = game.partyNumber
			}
		}
	}
	
	/**
	 * Checks for Xeno Clash Nightmare and if it appeared and the user enabled it in settings, start it.
	 *
	 * @return True if Xeno Clash Nightmare was detected and successfully completed. False otherwise.
	 */
	fun checkForXenoClashNightmare(): Boolean {
		if (enableXenoClashNightmare && game.imageUtils.confirmLocation("limited_time_quests", tries = 1)) {
			// First check if the Nightmare is skippable.
			if (game.findAndClickButton("event_claim_loot", tries = 1)) {
				game.printToLog("\n[XENO] Skippable Xeno Clash Nightmare detected. Claiming it now...", tag = tag)
				game.collectLoot(isCompleted = false, isEventNightmare = true)
				return true
			} else {
				game.printToLog("\n[XENO] Detected Event Nightmare. Starting it now...", tag = tag)
				
				game.printToLog("\n********************************************************************************", tag = tag)
				game.printToLog("********************************************************************************", tag = tag)
				game.printToLog("[XENO] Xeno Clash Nightmare", tag = tag)
				game.printToLog("[XENO] Xeno Clash Nightmare Summons: $xenoClashNightmareSummonList", tag = tag)
				game.printToLog("[XENO] Xeno Clash Nightmare Group Number: $xenoClashNightmareGroupNumber", tag = tag)
				game.printToLog("[XENO] Xeno Clash Nightmare Party Number: $xenoClashNightmarePartyNumber", tag = tag)
				game.printToLog("********************************************************************************", tag = tag)
				game.printToLog("\n********************************************************************************", tag = tag)
				
				// Tap the "Play Next" button to head to the Summon Selection screen.
				game.findAndClickButton("play_next")
				
				game.wait(1.0)
				
				// Select only the first Nightmare.
				val playRoundButtons = game.imageUtils.findAll("play_round_buttons")
				game.gestureUtils.tap(playRoundButtons[0].x, playRoundButtons[0].y, "play_round_buttons")
				
				game.wait(1.0)
				
				// Once the bot is at the Summon Selection screen, select your Summon and Party and start the mission.
				if (game.imageUtils.confirmLocation("select_a_summon")) {
					game.selectSummon(optionalSummonList = xenoClashNightmareSummonList)
					val startCheck: Boolean = game.selectPartyAndStartMission(optionalGroupNumber = xenoClashNightmareGroupNumber, optionalPartyNumber = xenoClashNightmarePartyNumber)
					
					// Once preparations are completed, start Combat Mode.
					if (startCheck && game.combatMode.startCombatMode(game.combatScript)) {
						game.collectLoot(isCompleted = false, isEventNightmare = true)
						return true
					}
				}
			}
		} else if (!enableXenoClashNightmare && game.imageUtils.confirmLocation("limited_time_quests", tries = 1)) {
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
		var bannerLocations = game.imageUtils.findAll("event_banner")
		if (bannerLocations.size == 0) {
			bannerLocations = game.imageUtils.findAll("event_banner_blue")
		}
		game.gestureUtils.tap(bannerLocations[0].x, bannerLocations[0].y, "event_banner")
		
		game.wait(3.0)
		
		if (game.findAndClickButton("xeno_special")) {
			game.wait(1.0)
			
			// Check if there is a Nightmare already available.
			val nightmareIsAvailable: Int = if (game.imageUtils.findButton("event_nightmare", tries = 1) != null) {
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
		if (game.imageUtils.confirmLocation("select_a_summon")) {
			if (game.selectSummon()) {
				// Select the Party.
				game.selectPartyAndStartMission()
				
				game.wait(1.0)
				
				// Now start Combat Mode and detect any item drops.
				if (game.combatMode.startCombatMode(game.combatScript)) {
					runsCompleted = game.collectLoot(isCompleted = true)
				}
			}
		} else {
			throw XenoClashException("Failed to arrive at the Summon Selection screen.")
		}
		
		return runsCompleted
	}
}