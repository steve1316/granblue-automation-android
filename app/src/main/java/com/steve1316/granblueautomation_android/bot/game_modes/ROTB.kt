package com.steve1316.granblueautomation_android.bot.game_modes

import androidx.preference.PreferenceManager
import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.bot.Game

class RiseOfTheBeastsException(message: String): Exception(message)

class RiseOfTheBeasts(private val game: Game, private val missionName: String) {
	private val tag: String = "${MainActivity.loggerTag}_RiseOfTheBeasts"
	
	private val enableROTBExtremePlus: Boolean
	private var rotbExtremePlusSummonList: List<String>
	private var rotbExtremePlusGroupNumber: Int
	private var rotbExtremePlusPartyNumber: Int
	private var rotbExtremePlusAmount = 0
	
	init {
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(game.myContext)
		enableROTBExtremePlus = sharedPreferences.getBoolean("enableROTBExtremePlus", false)
		rotbExtremePlusSummonList = sharedPreferences.getStringSet("rotbExtremePlusSummonList", setOf<String>())!!.toList()
		rotbExtremePlusGroupNumber = sharedPreferences.getInt("rotbExtremePlusGroupNumber", 0)
		rotbExtremePlusPartyNumber = sharedPreferences.getInt("rotbExtremePlusPartyNumber", 0)
		
		if (game.farmingMode == "Rise of the Beasts" && game.itemName == "Repeated Runs" && enableROTBExtremePlus) {
			game.printToLog("\n[INFO] Initializing settings for Rise of the Beasts Extreme+...", tag = tag)
			
			if (rotbExtremePlusSummonList.isEmpty()) {
				game.printToLog("[INFO] Summons for Rise of the Beasts Extreme+ will reuse the ones for Farming Mode.", tag = tag)
				rotbExtremePlusSummonList = game.summonList
			}
			
			if (rotbExtremePlusGroupNumber == 0) {
				game.printToLog("[INFO] Group Number for Rise of the Beasts Extreme+ will reuse the ones for Farming Mode.", tag = tag)
				rotbExtremePlusGroupNumber = game.groupNumber
			}
			
			if (rotbExtremePlusPartyNumber == 0) {
				game.printToLog("[INFO] Party Number for Rise of the Beasts Extreme+ will reuse the ones for Farming Mode.", tag = tag)
				rotbExtremePlusPartyNumber = game.partyNumber
			}
		}
	}
	
	/**
	 * Checks for Extreme Plus during Rise of the Beasts and if it appeared and the user enabled it in settings, start it.
	 *
	 * @return True if Extreme Plus was detected and successfully completed. False otherwise.
	 */
	fun checkROTBExtremePlus(): Boolean {
		if (enableROTBExtremePlus && game.imageUtils.confirmLocation("rotb_extreme_plus", tries = 1)) {
			game.printToLog("\n[ROTB] Detected Extreme+. Starting it now...", tag = tag)
			rotbExtremePlusAmount += 1
			
			game.printToLog("\n********************************************************************************", tag = tag)
			game.printToLog("********************************************************************************", tag = tag)
			game.printToLog("[ROTB] Rise of the Beasts Extreme+", tag = tag)
			game.printToLog("[ROTB] Rise of the Beasts Extreme+ Summons: $rotbExtremePlusSummonList", tag = tag)
			game.printToLog("[ROTB] Rise of the Beasts Extreme+ Group Number: $rotbExtremePlusGroupNumber", tag = tag)
			game.printToLog("[ROTB] Rise of the Beasts Extreme+ Party Number: $rotbExtremePlusPartyNumber", tag = tag)
			game.printToLog("[ROTB] Amount of Rise of the Beasts Extreme+ encountered: $rotbExtremePlusAmount", tag = tag)
			game.printToLog("********************************************************************************", tag = tag)
			game.printToLog("\n********************************************************************************", tag = tag)
			
			// Tap the "Play Next" button to head to the Summon Selection screen.
			game.findAndClickButton("play_next")
			
			game.wait(1.0)
			
			// Once the bot is at the Summon Selection screen, select your Summon and Party and start the mission.
			if (game.imageUtils.confirmLocation("select_a_summon")) {
				game.selectSummon(optionalSummonList = rotbExtremePlusSummonList)
				val startCheck: Boolean = game.selectPartyAndStartMission(optionalGroupNumber = rotbExtremePlusGroupNumber, optionalPartyNumber = rotbExtremePlusPartyNumber)
				
				// Once preparations are completed, start Combat Mode.
				if (startCheck && game.combatMode.startCombatMode(game.combatScript)) {
					game.collectLoot()
					return true
				}
			}
		} else if (!enableROTBExtremePlus && game.imageUtils.confirmLocation("rotb_extreme_plus", tries = 1)) {
			game.printToLog("\n[ROTB] Rise of the Beasts Extreme+ detected but user opted to not run it. Moving on...", tag = tag)
			game.findAndClickButton("close")
		} else {
			game.printToLog("\n[ROTB] No Rise of the Beasts Extreme+ detected. Moving on...", tag = tag)
		}
		
		return false
	}
	
	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)
		
		game.printToLog("\n[INFO] Now navigating to Rise of the Beasts...", tag = tag)
		
		// Go to the first banner that is usually the current Event by tapping on the "Menu" button.
		game.findAndClickButton("home_menu")
		var bannerLocations = game.imageUtils.findAll("event_banner")
		if (bannerLocations.size == 0) {
			bannerLocations = game.imageUtils.findAll("event_banner_blue")
		}
		game.gestureUtils.tap(bannerLocations[0].x, bannerLocations[0].y, "event_banner")
		
		game.wait(3.0)
		
		if (game.imageUtils.confirmLocation("rotb")) {
			// Remove the difficulty prefix from the mission name.
			var difficulty = ""
			var formattedMissionName = ""
			when {
				missionName.contains("N ") -> {
					difficulty = "Normal"
					formattedMissionName = missionName.substring(2)
				}
				missionName.contains("H ") -> {
					difficulty = "Hard"
					formattedMissionName = missionName.substring(2)
				}
				missionName.contains("VH ") -> {
					difficulty = "Very Hard"
					formattedMissionName = missionName.substring(3)
				}
				missionName.contains("EX ") -> {
					difficulty = "Extreme"
					formattedMissionName = missionName.substring(3)
				}
			}
			
			// Only Raids are marked with Extreme difficulty.
			if (difficulty == "Extreme") {
				game.printToLog("[INFO] Now hosting $formattedMissionName Raid...", tag = tag)
				
				// Tap on the Raid banner.
				game.findAndClickButton("rotb_extreme")
				
				if (game.imageUtils.confirmLocation("rotb_battle_the_beasts")) {
					when (formattedMissionName) {
						"Zhuque" -> {
							game.printToLog("[INFO] Now starting EX Zhuque Raid...", tag = tag)
							game.findAndClickButton("rotb_raid_zhuque")
						}
						"Xuanwu" -> {
							game.printToLog("[INFO] Now starting EX Xuanwu Raid...", tag = tag)
							game.findAndClickButton("rotb_raid_xuanwu")
						}
						"Baihu" -> {
							game.printToLog("[INFO] Now starting EX Baihu Raid...", tag = tag)
							game.findAndClickButton("rotb_raid_baihu")
						}
						"Qinglong" -> {
							game.printToLog("[INFO] Now starting EX Qinglong Raid...", tag = tag)
							game.findAndClickButton("rotb_raid_qinglong")
						}
					}
				}
			} else if (missionName == "Lvl 100 Shenxian") {
				// Tap on Shenxian to host.
				game.printToLog("[INFO] Now hosting Shenxian Raid...", tag = tag)
				game.findAndClickButton("rotb_shenxian_host")
				
				if (!game.imageUtils.waitVanish("rotb_shenxian_host", timeout = 10)) {
					game.printToLog("[INFO] There are no more Shenxian hosts left. Alerting user...", tag = tag)
					throw(IllegalStateException("There are no more Shenxian hosts left."))
				}
			} else {
				game.printToLog("[INFO] Now hosting $formattedMissionName Quest...", tag = tag)
				
				// Scroll the screen to make way for smaller screens.
				game.gestureUtils.scroll()
				
				game.wait(1.0)
				
				// Find all instances of the "Select" button on the screen and tap on the first instance.
				val selectButtonLocations = game.imageUtils.findAll("select")
				game.gestureUtils.tap(selectButtonLocations[0].x, selectButtonLocations[0].y, "select")
				
				if (game.imageUtils.confirmLocation("rotb_rising_beasts_showdown")) {
					// Find all the round "Play" buttons.
					var roundPlayButtonLocations = game.imageUtils.findAll("play_round_button")
					
					when (formattedMissionName) {
						"Zhuque" -> {
							game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y, "play_round_button")
						}
						"Xuanwu" -> {
							game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y, "play_round_button")
						}
						"Baihu" -> {
							game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
						}
						"Qinglong" -> {
							game.gestureUtils.tap(roundPlayButtonLocations[3].x, roundPlayButtonLocations[3].y, "play_round_button")
						}
					}
					
					game.wait(3.0)
					
					// Find all the round "Play" buttons again.
					roundPlayButtonLocations = game.imageUtils.findAll("play_round_button")
					
					// Only Very Hard difficulty will be supported for farming efficiency.
					game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
				}
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
		var numberOfItemsDropped = 0
		
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
		if (game.imageUtils.confirmLocation("select_a_summon")) {
			if (game.selectSummon()) {
				// Select the Party.
				game.selectPartyAndStartMission()
				
				game.wait(1.0)
				
				// Now start Combat Mode and detect any item drops.
				if (game.combatMode.startCombatMode(game.combatScript)) {
					numberOfItemsDropped = game.collectLoot()
				}
			}
		} else {
			throw RiseOfTheBeastsException("Failed to arrive at the Summon Selection screen.")
		}
		
		return numberOfItemsDropped
	}
}