package com.steve1316.granblueautomation_android.bot

import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import org.opencv.core.Point
import java.util.*
import kotlin.collections.ArrayList

/**
 * Provides the utility functions needed for perform navigation for Farming Mode throughout Granblue Fantasy.
 */
class MapSelection(private val game: Game) {
	private val TAG: String = "GAA_MapSelection"
	
	/**
	 * Navigates the bot to the specified map and preps the bot for Summon/Party selection.
	 *
	 * @param farmingMode Mode to look for the specified item and map in.
	 * @param mapName Name of the map to look for the specified mission in.
	 * @param missionName Name of the mission to farm the item in.
	 * @param difficulty Selected difficulty for certain missions.
	 * @return True if the bot reached the Summon Selection screen. False otherwise.
	 */
	fun selectMap(farmingMode: String, mapName: String, missionName: String, difficulty: String): Boolean {
		try {
			// Format the map name.
			val formattedMapName = mapName.toLowerCase(Locale.ROOT).replace(" ", "_").replace("-", "_")
			
			// Go to the Home screen.
			game.goBackHome(confirmLocationCheck = true)
			
			if(farmingMode.toLowerCase(Locale.ROOT) == "quest") {
				var checkLocation = false
				var currentLocation = ""
				
				// Check if the bot is already at the island where the mission takes place in. If not, navigate to it.
				if(game.imageUtils.confirmLocation("map_$formattedMapName", tries = 2)) {
					game.printToLog("[INFO] Bot is currently on the correct island for the mission.")
					checkLocation = true
				} else {
					game.printToLog("[INFO] Bot is not on the correct island for the mission. Navigating to the correct island...")
					checkLocation = false
					
					// Determine what island the bot is currently at.
					if(game.imageUtils.confirmLocation("map_port_breeze_archipelago", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Port Breeze Archipelago. Now moving to $mapName...")
						currentLocation = "Port Breeze Archipelago"
					} else if(game.imageUtils.confirmLocation("map_valtz_duchy", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Valtz Duchy. Now moving to $mapName...")
						currentLocation = "Valtz Duchy"
					} else if(game.imageUtils.confirmLocation("map_auguste_isles", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Auguste Isles. Now moving to $mapName...")
						currentLocation = "Auguste Isles"
					} else if(game.imageUtils.confirmLocation("map_lumacie_archipelago", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Lumacie Archipelago. Now moving to $mapName...")
						currentLocation = "Lumacie Archipelago"
					} else if(game.imageUtils.confirmLocation("map_albion_citadel", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Albion Citadel. Now moving to $mapName...")
						currentLocation = "Albion Citadel"
					} else if(game.imageUtils.confirmLocation("map_mist_shrouded_isle", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Mist-Shrouded Isle. Now moving to $mapName...")
						currentLocation = "Mist-Shrouded Isle"
					} else if(game.imageUtils.confirmLocation("map_golonzo_island", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Golonzo Island. Now moving to $mapName...")
						currentLocation = "Golonzo Island"
					} else if(game.imageUtils.confirmLocation("map_amalthea_island", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Amalthea Island. Now moving to $mapName...")
						currentLocation = "Amalthea Island"
					} else if(game.imageUtils.confirmLocation("map_former_capital_mephorash", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Former Capital Mephorash. Now moving to $mapName...")
						currentLocation = "Former Capital Mephorash"
					} else if(game.imageUtils.confirmLocation("map_agastia", tries = 1)) {
						game.printToLog("[INFO] Bot's current location is at Agastia. Now moving to $mapName...")
						currentLocation = "Agastia"
					}
				}
				
				// Go to the Quests screen.
				game.findAndClickButton("quest", suppressError = true)
				
				// If the bot is currently not at the correct island, move to it.
				if(!checkLocation) {
					// Tap the "World" button.
					game.findAndClickButton("world")
					
					// Now on the World screen, tap the specified coordinates of the screen to move to that island. Switch pages if necessary.
					checkMapLocation(mapName, formattedMapName, currentLocation)
					
					// Tap the "Go" button on the popup after tapping the map node.
					game.findAndClickButton("go")
					game.wait(1.0)
				}
				
				// Find the "World" button.
				var worldButtonLocation = game.imageUtils.findButton("world", tries = 2)
				if(worldButtonLocation == null) {
					worldButtonLocation = game.imageUtils.findButton("world2", tries = 2)
				}
				
				if(worldButtonLocation == null) {
					throw Exception("Unable to find the location of the World button.")
				}
				
				// Now that the bot is on the correct island and is at the Quests screen, tap the correct chapter node using the location of the
				// "World" button.
				if(missionName == "Scattered Cargo") {
					game.printToLog("[INFO] Moving to Chapter 1 (115) node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 97, worldButtonLocation.y + 97)
				} else if(missionName == "Lucky Charm Hunt") {
					game.printToLog("[INFO] Moving to Chapter 6 (122) node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 332, worldButtonLocation.y + 16)
				} else if(missionName == "Special Op's Request") {
					game.printToLog("[INFO] Moving to Chapter 8 node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 258, worldButtonLocation.y + 151)
				} else if(missionName == "Threat to the Fisheries") {
					game.printToLog("[INFO] Moving to Chapter 9 node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 216, worldButtonLocation.y + 113)
				} else if(missionName == "The Fruit of Lumacie" || missionName == "Whiff of Danger") {
					game.printToLog("[INFO] Moving to Chapter 13 (39/52) node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 78, worldButtonLocation.y + 92)
				} else if(missionName == "I Challenge You!") {
					game.printToLog("[INFO] Moving to Chapter 17 node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 119, worldButtonLocation.y + 121)
				} else if(missionName == "For Whom the Bell Tolls") {
					game.printToLog("[INFO] Moving to Chapter 22 node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 178, worldButtonLocation.y + 33)
				} else if(missionName == "For Whom the Bell Tolls") {
					game.printToLog("[INFO] Moving to Chapter 22 node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 178, worldButtonLocation.y + 33)
				} else if(missionName == "Golonzo's Battles of Old") {
					game.printToLog("[INFO] Moving to Chapter 25 node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 196, worldButtonLocation.y + 5)
				} else if(missionName == "The Dungeon Diet") {
					game.printToLog("[INFO] Moving to Chapter 30 (44/65) node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 242, worldButtonLocation.y + 24)
				} else if(missionName == "Trust Busting Dustup") {
					game.printToLog("[INFO] Moving to Chapter 36 (123) node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 319, worldButtonLocation.y + 13)
				} else if(missionName == "Erste Kingdom Episode 4") {
					game.printToLog("[INFO] Moving to Chapter 70 node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 253, worldButtonLocation.y + 136)
				} else if(missionName == "Imperial Wanderer's Soul") {
					game.printToLog("[INFO] Moving to Chapter 55 node...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(worldButtonLocation.x + 162, worldButtonLocation.y + 143)
				}
				
				// Now that the correct chapter node has been selected, scroll down the screen as far as possible and then click on the specified
				// mission to start.
				game.printToLog("[INFO] Now bringing up the Summon Selection screen for \"$missionName\"...", MESSAGE_TAG = TAG)
				game.gestureUtils.scroll(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN)
				game.wait(1.0)
				val formattedMissionName = missionName.toLowerCase(Locale.ROOT).replace(" ", "_")
				game.findAndClickButton(formattedMissionName)
				
				// If the mission name is "Erste Kingdom Episode 4", select the "Ch. 70 - Erste Kingdom" option.
				if(missionName == "Erste Kingdom Episode 4") {
					game.findAndClickButton("episode_4")
					game.findAndClickButton("ok")
				}
				
			} else if(farmingMode.toLowerCase(Locale.ROOT) == "special") {
				// Go to the Quests screen and then to the Special Quest screen.
				game.findAndClickButton("quest", suppressError = true)
				
				if(game.imageUtils.confirmLocation("quest")) {
					game.findAndClickButton("special")
					
					// Format the mission name based on the difficulty.
					val formattedMissionName: String
					if(difficulty == "Normal" || difficulty == "Hard") {
						formattedMissionName = missionName.substring(1)
					} else if(difficulty == "Very Hard" || difficulty == "Extreme") {
						formattedMissionName = missionName.substring(3)
					} else {
						formattedMissionName = missionName
					}
					
					if(game.imageUtils.confirmLocation("special")) {
						if(mapName != "Campaign-Exclusive Quest" && mapName != "Basic Treasure Quests" && mapName != "Shiny Slime Search!" &&
							mapName != "Six Dragon Trial") {
							// Scroll the screen down if the selected mission is located on the bottom half of the page.
							game.gestureUtils.scroll(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_DOWN)
							game.wait(1.0)
						}
						
						// Find the specified mission popup.
						val missionLocation = game.imageUtils.findButton(formattedMapName) ?: throw Exception("Could not find the " +
								"$formattedMapName button.")
						
						game.printToLog("[INFO] Navigating to $mapName...", MESSAGE_TAG = TAG)
						
						// Tap the mission's "Select" button.
						missionLocation.x += 405
						missionLocation.y += 175
						game.gestureUtils.tap(missionLocation.x, missionLocation.y)
						game.wait(1.0)
						
						if(mapName == "Basic Treasure Quests") {
							// Open up "Basic Treasure Quests" sub-missions popup.
							var roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
							
							if(formattedMissionName == "Scarlet Trial")  {
								game.printToLog("[INFO] Opening up Scarlet Trial mission popup...", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y)
							} else if(formattedMissionName == "Cerulean Trial")  {
								game.printToLog("[INFO] Opening up Cerulean Trial mission popup...", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y)
							} else if(formattedMissionName == "Violet Trial")  {
								game.printToLog("[INFO] Opening up Violet Trial mission popup...", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y)
							}
							
							game.wait(1.0)
							
							// Now that the mission's sub-missions popup is open, select the specified difficulty.
							game.printToLog("[INFO] Now selecting $difficulty difficulty...")
							roundPlayButtonLocations = game.imageUtils.findAll("play_round_button")
							
							if(difficulty == "Normal") {
								game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y)
							} else if(difficulty == "Hard") {
								game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y)
							} else if(difficulty == "Very Hard") {
								game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y)
							}
							
						} else if(mapName == "Shiny Slime Search!" || mapName == "Six Dragon Trial" || mapName == "Angel Halo") {
							// Open up the mission's difficulty selection popup and then select its difficulty.
							game.printToLog("[INFO] Now selecting $difficulty $mapName...", MESSAGE_TAG = TAG)
							val roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
							
							if(difficulty == "Normal") {
								game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y)
							} else if(difficulty == "Hard") {
								game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y)
							} else if(difficulty == "Very Hard"){
								game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y)
							}
							
						} else if(mapName == "Elemental Treasure Quests") {
							game.printToLog("[INFO] Now selecting $missionName...", MESSAGE_TAG = TAG)
							val roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
							
							if(formattedMissionName == "The Hellfire Trial") {
								game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y)
							} else if(formattedMissionName == "The Deluge Trial") {
								game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y)
							} else if(formattedMissionName == "The Wasteland Trial") {
								game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y)
							} else if(formattedMissionName == "The Typhoon Trial") {
								game.gestureUtils.tap(roundPlayButtonLocations[3].x, roundPlayButtonLocations[3].y)
							} else if(formattedMissionName == "The Aurora Trial") {
								game.gestureUtils.tap(roundPlayButtonLocations[4].x, roundPlayButtonLocations[4].y)
							} else if(formattedMissionName == "The Oblivion Trial") {
								game.gestureUtils.tap(roundPlayButtonLocations[5].x, roundPlayButtonLocations[5].y)
							}
							
						} else if(mapName == "Showdowns") {
							game.printToLog("[INFO] Opening up $formattedMissionName mission popup...", MESSAGE_TAG = TAG)
							var roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
							
							if(formattedMissionName == "Ifrit Showdown") {
								game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y)
							} else if(formattedMissionName == "Cocytus Showdown") {
								game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y)
							} else if(formattedMissionName == "Vohu Manah Showdown") {
								game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y)
							} else if(formattedMissionName == "Sagittarius Showdown") {
								game.gestureUtils.tap(roundPlayButtonLocations[3].x, roundPlayButtonLocations[3].y)
							} else if(formattedMissionName == "Corow Showdown") {
								game.gestureUtils.tap(roundPlayButtonLocations[4].x, roundPlayButtonLocations[4].y)
							} else if(formattedMissionName == "Diablo Showdown") {
								game.gestureUtils.tap(roundPlayButtonLocations[5].x, roundPlayButtonLocations[5].y)
							}
							
							game.wait(1.0)
							
							// Now select the difficulty.
							game.printToLog("[INFO] Now selecting $difficulty difficulty...", MESSAGE_TAG = TAG)
							roundPlayButtonLocations = game.imageUtils.findAll("play_round_button")
							
							if(difficulty == "Hard") {
								game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y)
							} else if(difficulty == "Very Hard") {
								game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y)
							} else if(difficulty == "Extreme") {
								game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y)
							}
							
						} else if(mapName == "Campaign-Exclusive Quest") {
							game.printToLog("[INFO] Selecting Campaign-Exclusive Quest...", MESSAGE_TAG = TAG)
							
							// There is only 1 "Play" button for this time-limited quest.
							game.findAndClickButton("play_round_button")
						}
					}
				}
			}
			
			// At this point, the bot has already selected the mission and thus it should now check if it needs any AP.
			// TODO: Make sure to complete checkAP().
			// game.checkAP()
			
			// Finally, double-check to see if the bot is at the Summon Selection screen.
			if(farmingMode.toLowerCase(Locale.ROOT) != "coop") {
				game.printToLog("[INFO] Now checking if the bot is currently at the Summon Selection screen...", MESSAGE_TAG = TAG)
				if(game.imageUtils.confirmLocation("select_summon")) {
					game.printToLog("[SUCCESS] Bot arrived at the Summon Selection screen after selecting the mission.", MESSAGE_TAG = TAG)
					return true
				} else {
					game.printToLog("[WARNING] Bot did not arrive at the Summon Selection screen after selecting the mission.",
						MESSAGE_TAG = TAG)
					return false
				}
			} else {
				game.printToLog("[INFO] Now checking if the bot is currently at the Coop Party Selection screen...", MESSAGE_TAG = TAG)
				if(game.imageUtils.confirmLocation("coop_without_support_summon")) {
					game.printToLog("[SUCCESS] Bot arrived at the Party Selection screen after selecting the Coop mission.",
						MESSAGE_TAG = TAG)
					return true
				} else {
					game.printToLog("[WARNING] Bot did not arrive at the Party Selection screen after selecting the Coop mission.",
						MESSAGE_TAG = TAG)
					return false
				}
			}
			
		} catch(e: Exception) {
			Log.e(TAG, "Encountered an exception in selectMap(): ")
			e.printStackTrace()
		}
		
		return false
	}
	
	/**
	 * Process a Pending Battle.
	 */
	private fun clearPendingBattle() {
		TODO("not yet implemented")
	}
	
	/**
	 * Check and collect any pending rewards and free up slots for the bot to join more Raids.
	 *
	 * @param farmingMode The current farming mode will dictate what logic to follow for Pending Battles.
	 * @return True if Pending Battles were detected. False otherwise.
	 */
	fun checkPendingBattles(farmingMode: String): Boolean {
		TODO("not yet implemented")
	}
	
	/**
	 * Check and updates the number of Raids currently joined.
	 */
	private fun checkJoinedRaids() {
		TODO("not yet implemented")
	}
	
	/**
	 * Navigates the bot to the Backup Requests screen and attempt to join a specified Raid.
	 *
	 * @param missionName Name of the Raid.
	 * @return True if the bot reached the Summon Selection screen after joining a Raid. False otherwise.
	 */
	fun joinRaid(missionName: String): Boolean {
		TODO("not yet implemented")
		
		// TODO: In the case of unable to join using the current room code, simply hit the "Reload" button to clear the text box.
	}
}