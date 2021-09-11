package com.steve1316.granblueautomation_android.bot.game_modes

import androidx.preference.PreferenceManager
import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.bot.Game
import org.opencv.core.Point
import java.util.*

class SpecialException(message: String) : Exception(message)

class Special(private val game: Game, private val mapName: String, private val missionName: String) {
	private val tag: String = "${MainActivity.loggerTag}_Special"
	
	private val enableDimensionalHalo: Boolean
	private var dimensionalHaloSummonList: List<String>
	private var dimensionalHaloGroupNumber: Int
	private var dimensionalHaloPartyNumber: Int
	private var dimensionalHaloAmount: Int = 0
	
	init {
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(game.myContext)
		enableDimensionalHalo = sharedPreferences.getBoolean("enableDimensionalHalo", false)
		dimensionalHaloSummonList = sharedPreferences.getStringSet("dimensionalHaloSummonList", setOf<String>())!!.toList()
		dimensionalHaloGroupNumber = sharedPreferences.getInt("dimensionalHaloGroupNumber", 0)
		dimensionalHaloPartyNumber = sharedPreferences.getInt("dimensionalHaloPartyNumber", 0)
		
		if (missionName == "VH Angel Halo" && enableDimensionalHalo && (game.itemName == "EXP" || game.itemName == "Angel Halo Weapons")) {
			game.printToLog("\n[SPECIAL] Initializing settings for Dimensional Halo...", tag = tag)
			
			if (dimensionalHaloSummonList.isEmpty()) {
				game.printToLog("[SPECIAL] Summons for Dimensional Halo will reuse the ones for Farming Mode.", tag = tag)
				dimensionalHaloSummonList = game.summonList
			}
			
			if (dimensionalHaloGroupNumber == 0) {
				game.printToLog("[SPECIAL] Group Number for Dimensional Halo will reuse the ones for Farming Mode.", tag = tag)
				dimensionalHaloGroupNumber = game.groupNumber
			}
			
			if (dimensionalHaloPartyNumber == 0) {
				game.printToLog("[SPECIAL] Party Number for Dimensional Halo will reuse the ones for Farming Mode.", tag = tag)
				dimensionalHaloPartyNumber = game.partyNumber
			}
		}
	}
	
	/**
	 * Checks for Dimensional Halo and if it appeared and the user enabled it in settings, start it.
	 *
	 * @return True if Dimensional Halo was detected and successfully completed. False otherwise.
	 */
	fun checkDimensionalHalo(): Boolean {
		if (enableDimensionalHalo && game.imageUtils.confirmLocation("limited_time_quests", tries = 1)) {
			game.printToLog("\n[D.HALO] Detected Dimensional Halo. Starting it now...", tag = tag)
			dimensionalHaloAmount += 1
			
			game.printToLog("\n********************************************************************************", tag = tag)
			game.printToLog("********************************************************************************", tag = tag)
			game.printToLog("[D.HALO] Dimensional Halo", tag = tag)
			game.printToLog("[D.HALO] Dimensional Halo Summons: $dimensionalHaloSummonList", tag = tag)
			game.printToLog("[D.HALO] Dimensional Halo Group Number: $dimensionalHaloGroupNumber", tag = tag)
			game.printToLog("[D.HALO] Dimensional Halo Party Number: $dimensionalHaloPartyNumber", tag = tag)
			game.printToLog("[D.HALO] Amount of Dimensional Halos encountered: $dimensionalHaloAmount", tag = tag)
			game.printToLog("********************************************************************************", tag = tag)
			game.printToLog("\n********************************************************************************", tag = tag)
			
			// Tap the "Play Next" button to head to the Summon Selection screen.
			game.findAndClickButton("play_next")
			
			game.wait(1.0)
			
			// Once the bot is at the Summon Selection screen, select your Summon and Party and start the mission.
			if (game.imageUtils.confirmLocation("select_a_summon")) {
				game.selectSummon(optionalSummonList = dimensionalHaloSummonList)
				val startCheck: Boolean = game.selectPartyAndStartMission(optionalGroupNumber = dimensionalHaloGroupNumber, optionalPartyNumber = dimensionalHaloPartyNumber)
				
				// Once preparations are completed, start Combat Mode.
				if (startCheck && game.combatMode.startCombatMode(game.combatScript)) {
					game.collectLoot()
					return true
				}
			}
		} else if (!enableDimensionalHalo && game.imageUtils.confirmLocation("limited_time_quests", tries = 1)) {
			game.printToLog("\n[D.HALO] Dimensional Halo detected but user opted to not run it. Moving on...", tag = tag)
			game.findAndClickButton("close")
		} else {
			game.printToLog("\n[D.HALO] No Dimensional Halo detected. Moving on...", tag = tag)
		}
		
		return false
	}
	
	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)
		
		// Go to the Quests screen.
		game.findAndClickButton("quest", suppressError = true)
		
		game.wait(3.0)
		
		// Check for the "You retreated from the raid battle" popup.
		if (game.imageUtils.confirmLocation("you_retreated_from_the_raid_battle", tries = 1)) {
			game.findAndClickButton("ok")
		}
		
		if (game.imageUtils.confirmLocation("quest")) {
			// Go to the Special screen.
			game.findAndClickButton("special")
			
			game.wait(3.0)
			
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
			
			if (game.imageUtils.confirmLocation("special")) {
				var tries = 2
				
				// Try to select the specified Special mission for a number of tries.
				while (tries > 0) {
					// Scroll the screen down if its any of the Special Quests that are more towards the bottom of the page to alleviate problems for smaller screens.
					if (mapName != "Campaign-Exclusive Quest" && mapName != "Basic Treasure Quests" && mapName != "Shiny Slime Search!" && mapName != "Six Dragon Trial") {
						// Scroll the screen down if the selected mission is located on the bottom half of the page.
						game.gestureUtils.scroll()
						game.wait(1.0)
					}
					
					// Find the specified mission popup.
					val formattedMapName = mapName.lowercase().replace(" ", "_").replace("-", "_")
					val missionLocation = game.imageUtils.findButton(formattedMapName)
					
					if (missionLocation != null) {
						// Tap the mission's "Select" button.
						game.printToLog("[SPECIAL] Navigating to $mapName...", tag = tag)
						missionLocation.x += if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								200
							} else {
								400
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								290
							} else {
								210
							}
						}
						missionLocation.y += if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								110
							} else {
								175
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								120
							} else {
								95
							}
						}
						game.gestureUtils.tap(missionLocation.x, missionLocation.y, formattedMapName)
						
						game.wait(3.0)
						
						if (mapName == "Basic Treasure Quests") {
							// Open up "Basic Treasure Quests" sub-missions popup.
							var roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
							
							when (formattedMissionName) {
								"Scarlet Trial" -> {
									game.printToLog("[SPECIAL] Opening up Scarlet Trial mission popup...", tag = tag)
									game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y, "play_round_button")
								}
								"Cerulean Trial" -> {
									game.printToLog("[SPECIAL] Opening up Cerulean Trial mission popup...", tag = tag)
									game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y, "play_round_button")
								}
								"Violet Trial" -> {
									game.printToLog("[SPECIAL] Opening up Violet Trial mission popup...", tag = tag)
									game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
								}
							}
							
							// Now that the mission's sub-missions popup is open, select the specified difficulty.
							game.printToLog("[SPECIAL] Now selecting $difficulty difficulty...")
							roundPlayButtonLocations = game.imageUtils.findAll("play_round_button")
							
							when (difficulty) {
								"Normal" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y, "play_round_button")
								}
								"Hard" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y, "play_round_button")
								}
								"Very Hard" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
								}
							}
						} else if (mapName == "Shiny Slime Search!" || mapName == "Six Dragon Trial" || mapName == "Angel Halo") {
							// Open up the mission's difficulty selection popup and then select its difficulty.
							game.printToLog("[SPECIAL] Now selecting $difficulty $mapName...", tag = tag)
							val roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
							
							when (difficulty) {
								"Normal" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y, "play_round_button")
								}
								"Hard" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y, "play_round_button")
								}
								"Very Hard" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
								}
							}
						} else if (mapName == "Elemental Treasure Quests") {
							game.printToLog("[SPECIAL] Now selecting $missionName...", tag = tag)
							val roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
							
							when (formattedMissionName) {
								"The Hellfire Trial" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y, "play_round_button")
								}
								"The Deluge Trial" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y, "play_round_button")
								}
								"The Wasteland Trial" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
								}
								"The Typhoon Trial" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[3].x, roundPlayButtonLocations[3].y, "play_round_button")
								}
								"The Aurora Trial" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[4].x, roundPlayButtonLocations[4].y, "play_round_button")
								}
								"The Oblivion Trial" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[5].x, roundPlayButtonLocations[5].y, "play_round_button")
								}
							}
						} else if (mapName == "Showdowns") {
							game.printToLog("[SPECIAL] Opening up $formattedMissionName mission popup...", tag = tag)
							var roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
							
							when (formattedMissionName) {
								"Ifrit Showdown" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y, "play_round_button")
								}
								"Cocytus Showdown" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y, "play_round_button")
								}
								"Vohu Manah Showdown" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
								}
								"Sagittarius Showdown" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[3].x, roundPlayButtonLocations[3].y, "play_round_button")
								}
								"Corow Showdown" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[4].x, roundPlayButtonLocations[4].y, "play_round_button")
								}
								"Diablo Showdown" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[5].x, roundPlayButtonLocations[5].y, "play_round_button")
								}
							}
							
							// Now select the difficulty.
							game.printToLog("[SPECIAL] Now selecting $difficulty difficulty...", tag = tag)
							roundPlayButtonLocations = game.imageUtils.findAll("play_round_button")
							
							when (difficulty) {
								"Hard" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y, "play_round_button")
								}
								"Very Hard" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y, "play_round_button")
								}
								"Extreme" -> {
									game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
								}
							}
						} else if (mapName == "Campaign-Exclusive Quest") {
							game.printToLog("[SPECIAL] Selecting Campaign-Exclusive Quest...", tag = tag)
							
							// There is only 1 "Play" button for this time-limited quest.
							game.findAndClickButton("play_round_button")
						}
						
						break
					} else {
						// Scroll down the screen more if on a smaller screen and it obscures the targeted mission.
						game.gestureUtils.scroll()
						tries -= 1
					}
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
			throw SpecialException("Failed to arrive at the Summon Selection screen.")
		}
		
		return numberOfItemsDropped
	}
}