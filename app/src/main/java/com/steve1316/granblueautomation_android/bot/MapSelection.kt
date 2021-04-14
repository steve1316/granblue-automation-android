package com.steve1316.granblueautomation_android.bot

import android.util.Log
import com.steve1316.granblueautomation_android.data.RoomCodeData
import org.opencv.core.Point
import java.util.*

/**
 * Provides the utility functions needed for perform navigation for Farming Mode throughout Granblue Fantasy.
 */
class MapSelection(private val game: Game, private val twitterRoomFinder: TwitterRoomFinder?) {
	private val TAG: String = "GAA_MapSelection"
	
	private var numberOfRaidsJoined: Int = 0
	
	/**
	 * Helper function to assist selectMap() in navigating to the correct island for Quest Farming Mode.
	 *
	 * @param islandName Name of the island.
	 * @param formattedIslandName Formatted version of the islandName string.
	 * @param currentLocation The name of the island that the bot is currently at.
	 * @return True if the bot arrived at the correct island. False otherwise.
	 */
	private fun checkMapLocation(islandName: String, formattedIslandName: String, currentLocation: String): Boolean {
		// Phantagrande Skydom Page 1
		if (islandName == "Port Breeze Archipelago" || islandName == "Valtz Duchy" || islandName == "Auguste Isles" || islandName == "Lumacie Archipelago" ||
			islandName == "Albion Citadel" || islandName == "Zinkenstill"
		) {
			if (currentLocation == "Mist-Shrouded Isle" || currentLocation == "Golonzo Island" || currentLocation == "Amalthea Island"
				|| currentLocation == "Former Capital Mephorash" || currentLocation == "Agastia"
			) {
				game.findAndClickButton("world_left_arrow")
			}
			
			// Use a manual way to tap on the correct island if image matching for the island name failed.
			if (!game.findAndClickButton(formattedIslandName, tries = 1)) {
				val arrowLocation = game.imageUtils.findButton("world_right_arrow")
					?: throw Exception("Unable to find the location of the right arrow for the World.")
				
				when (islandName) {
					"Port Breeze Archipelago" -> {
						game.gestureUtils.tap(arrowLocation.x - 717, arrowLocation.y - 289, "world_right_arrow")
					}
					"Valtz Duchy" -> {
						game.gestureUtils.tap(arrowLocation.x - 344, arrowLocation.y - 118, "world_right_arrow")
					}
					"Auguste Isles" -> {
						game.gestureUtils.tap(arrowLocation.x - 840, arrowLocation.y + 54, "world_right_arrow")
					}
					"Lumacie Archipelago" -> {
						game.gestureUtils.tap(arrowLocation.x - 177, arrowLocation.y + 159, "world_right_arrow")
					}
					"Albion Citadel" -> {
						game.gestureUtils.tap(arrowLocation.x - 589, arrowLocation.y + 344, "world_right_arrow")
					}
				}
			}
			
			return true
		}
		
		// Phantagrande Skydom Page 2
		else if (islandName == "Mist-Shrouded Isle" || islandName == "Golonzo Island" || islandName == "Amalthea Island" || islandName == "Former Capital " +
			"Mephorash" || islandName == "Agastia"
		) {
			if (currentLocation == "Port Breeze Archipelago" || currentLocation == "Valtz Duchy" || currentLocation == "Auguste Isles" ||
				currentLocation == "Lumacie Archipelago" || currentLocation == "Albion Citadel" || currentLocation == "Zinkenstill"
			) {
				game.findAndClickButton("world_right_arrow")
			}
			
			// Use a manual way to tap on the correct island if image matching for the island name failed.
			if (!game.findAndClickButton(formattedIslandName, tries = 1)) {
				val arrowLocation = game.imageUtils.findButton("world_left_arrow")
					?: throw Exception("Unable to find the location of the left arrow for the World.")
				
				when (islandName) {
					"Mist-Shrouded Isle" -> {
						game.gestureUtils.tap(arrowLocation.x + 379, arrowLocation.y + 342, "world_left_arrow")
					}
					"Golonzo Island" -> {
						game.gestureUtils.tap(arrowLocation.x + 820, arrowLocation.y + 255, "world_left_arrow")
					}
					"Amalthea Island" -> {
						game.gestureUtils.tap(arrowLocation.x + 288, arrowLocation.y + 34, "world_left_arrow")
					}
					"Former Capital Mephorash" -> {
						game.gestureUtils.tap(arrowLocation.x + 802, arrowLocation.y - 43, "world_left_arrow")
					}
					"Agastia" -> {
						game.gestureUtils.tap(arrowLocation.x + 440, arrowLocation.y - 267, "world_left_arrow")
					}
				}
			}
			
			return true
		}
		
		return false
	}
	
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
		// Format the map name.
		val formattedMapName = mapName.toLowerCase(Locale.ROOT).replace(" ", "_").replace("-", "_")
		
		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)
		
		if (farmingMode.toLowerCase(Locale.ROOT) == "quest") {
			val checkLocation: Boolean
			var currentLocation = ""
			
			// Check if the bot is already at the island where the mission takes place in. If not, navigate to it.
			if (game.imageUtils.confirmLocation("map_$formattedMapName", tries = 1)) {
				game.printToLog("[INFO] Bot is currently on the correct island for the mission.", MESSAGE_TAG = TAG)
				checkLocation = true
			} else {
				game.printToLog("[INFO] Bot is not on the correct island for the mission. Navigating to the correct island...")
				checkLocation = false
				
				// Determine what island the bot is currently at.
				when {
					game.imageUtils.confirmLocation("map_zinkenstill", tries = 1) -> {
						game.printToLog(
							"[INFO] Bot's current location is at Zinkenstill. Now moving to $mapName...",
							MESSAGE_TAG = TAG
						)
						currentLocation = "Zinkenstill"
					}
					game.imageUtils.confirmLocation("map_port_breeze_archipelago", tries = 1) -> {
						game.printToLog(
							"[INFO] Bot's current location is at Port Breeze Archipelago. Now moving to $mapName...",
							MESSAGE_TAG = TAG
						)
						currentLocation = "Port Breeze Archipelago"
					}
					game.imageUtils.confirmLocation("map_valtz_duchy", tries = 1) -> {
						game.printToLog("[INFO] Bot's current location is at Valtz Duchy. Now moving to $mapName...", MESSAGE_TAG = TAG)
						currentLocation = "Valtz Duchy"
					}
					game.imageUtils.confirmLocation("map_auguste_isles", tries = 1) -> {
						game.printToLog("[INFO] Bot's current location is at Auguste Isles. Now moving to $mapName...", MESSAGE_TAG = TAG)
						currentLocation = "Auguste Isles"
					}
					game.imageUtils.confirmLocation("map_lumacie_archipelago", tries = 1) -> {
						game.printToLog(
							"[INFO] Bot's current location is at Lumacie Archipelago. Now moving to $mapName...",
							MESSAGE_TAG = TAG
						)
						currentLocation = "Lumacie Archipelago"
					}
					game.imageUtils.confirmLocation("map_albion_citadel", tries = 1) -> {
						game.printToLog("[INFO] Bot's current location is at Albion Citadel. Now moving to $mapName...", MESSAGE_TAG = TAG)
						currentLocation = "Albion Citadel"
					}
					game.imageUtils.confirmLocation("map_mist_shrouded_isle", tries = 1) -> {
						game.printToLog(
							"[INFO] Bot's current location is at Mist-Shrouded Isle. Now moving to $mapName...",
							MESSAGE_TAG = TAG
						)
						currentLocation = "Mist-Shrouded Isle"
					}
					game.imageUtils.confirmLocation("map_golonzo_island", tries = 1) -> {
						game.printToLog("[INFO] Bot's current location is at Golonzo Island. Now moving to $mapName...", MESSAGE_TAG = TAG)
						currentLocation = "Golonzo Island"
					}
					game.imageUtils.confirmLocation("map_amalthea_island", tries = 1) -> {
						game.printToLog("[INFO] Bot's current location is at Amalthea Island. Now moving to $mapName...", MESSAGE_TAG = TAG)
						currentLocation = "Amalthea Island"
					}
					game.imageUtils.confirmLocation("map_former_capital_mephorash", tries = 1) -> {
						game.printToLog(
							"[INFO] Bot's current location is at Former Capital Mephorash. Now moving to $mapName...",
							MESSAGE_TAG = TAG
						)
						currentLocation = "Former Capital Mephorash"
					}
					game.imageUtils.confirmLocation("map_agastia", tries = 1) -> {
						game.printToLog("[INFO] Bot's current location is at Agastia. Now moving to $mapName...", MESSAGE_TAG = TAG)
						currentLocation = "Agastia"
					}
				}
			}
			
			// Go to the Quests screen.
			game.findAndClickButton("quest", suppressError = true)
			
			// If the bot is currently not at the correct island, move to it.
			if (!checkLocation) {
				// Tap the "World" button.
				game.findAndClickButton("world")
				
				// Now on the World screen, tap the specified coordinates of the screen to move to that island. Switch pages if necessary.
				checkMapLocation(mapName, formattedMapName, currentLocation)
				
				// Tap the "Go" button on the popup after tapping the map node.
				game.findAndClickButton("go")
			}
			
			// Find the "World" button.
			var worldButtonLocation = game.imageUtils.findButton("world", tries = 2)
			if (worldButtonLocation == null) {
				worldButtonLocation = game.imageUtils.findButton("world2", tries = 2)
			}
			
			if (worldButtonLocation == null) {
				throw Exception("Unable to find the location of the World button.")
			}
			
			// Now that the bot is on the correct island and is at the Quests screen, tap the correct chapter node using the location of the
			// "World" button.
			if (missionName == "Scattered Cargo") {
				game.printToLog("[INFO] Moving to Chapter 1 (115) node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 227, worldButtonLocation.y + 213, "template_node")
			} else if (missionName == "Lucky Charm Hunt") {
				game.printToLog("[INFO] Moving to Chapter 6 (122) node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 757, worldButtonLocation.y + 43, "template_node")
			} else if (missionName == "Special Op's Request") {
				game.printToLog("[INFO] Moving to Chapter 8 node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 577, worldButtonLocation.y + 343, "template_node")
			} else if (missionName == "Threat to the Fisheries") {
				game.printToLog("[INFO] Moving to Chapter 9 node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 497, worldButtonLocation.y + 258, "template_node")
			} else if (missionName == "The Fruit of Lumacie" || missionName == "Whiff of Danger") {
				game.printToLog("[INFO] Moving to Chapter 13 (39/52) node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 197, worldButtonLocation.y + 208, "template_node")
			} else if (missionName == "I Challenge You!") {
				game.printToLog("[INFO] Moving to Chapter 17 node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 262, worldButtonLocation.y + 268, "template_node")
			} else if (missionName == "For Whom the Bell Tolls") {
				game.printToLog("[INFO] Moving to Chapter 22 node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 417, worldButtonLocation.y + 78, "template_node")
			} else if (missionName == "Golonzo's Battles of Old") {
				game.printToLog("[INFO] Moving to Chapter 25 node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 457, worldButtonLocation.y + 18, "template_node")
			} else if (missionName == "The Dungeon Diet") {
				game.printToLog("[INFO] Moving to Chapter 30 (44/65) node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 557, worldButtonLocation.y + 48, "template_node")
			} else if (missionName == "Trust Busting Dustup") {
				game.printToLog("[INFO] Moving to Chapter 36 (123) node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 714, worldButtonLocation.y + 30, "template_node")
			} else if (missionName == "Erste Kingdom Episode 4") {
				game.printToLog("[INFO] Moving to Chapter 70 node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 587, worldButtonLocation.y + 318, "template_node")
			} else if (missionName == "Imperial Wanderer's Soul") {
				game.printToLog("[INFO] Moving to Chapter 55 node...", MESSAGE_TAG = TAG)
				game.gestureUtils.tap(worldButtonLocation.x + 350, worldButtonLocation.y + 320, "template_node")
			}
			
			// Now that the correct chapter node has been selected, scroll down the screen.
			game.printToLog("[INFO] Now bringing up the Summon Selection screen for \"$missionName\"...", MESSAGE_TAG = TAG)
			game.gestureUtils.scroll()
			
			game.wait(2.0)
			
			// Now tap on the mission node to start.
			val formattedMissionName = missionName.toLowerCase(Locale.ROOT).replace(" ", "_")
			if (!game.findAndClickButton(formattedMissionName)) {
				// If the bot failed to find and click on the mission node the first time, scroll down the screen again.
				game.gestureUtils.scroll()
				
				game.wait(2.0)
				
				game.findAndClickButton(formattedMissionName)
			}
			
			// If the mission name is "Erste Kingdom Episode 4", select the "Ch. 70 - Erste Kingdom" option.
			if (missionName == "Erste Kingdom Episode 4") {
				game.findAndClickButton("episode_4")
				game.findAndClickButton("ok")
			}
			
		} else if (farmingMode.toLowerCase(Locale.ROOT) == "coop") {
			// Tap the "Menu" button at the top right corner of the Home screen and go to Coop.
			game.findAndClickButton("home_menu")
			game.findAndClickButton("coop")
			
			game.wait(1.0)
			
			if (game.imageUtils.confirmLocation("coop")) {
				// Scroll the screen down a little bit.
				game.gestureUtils.swipe(500f, 1000f, 500f, 400f)
				
				game.wait(1.0)
				
				// Find all occurrences of the "Host Quest" button.
				val hostButtonLocations = game.imageUtils.findAll("coop_host_quest")
				
				if (missionName == "H3-1 In a Dusk Dream") {
					// Check if the difficulty is already selected. If not, select it.
					if (!game.findAndClickButton("coop_hard_selected", tries = 1)) {
						game.findAndClickButton("coop_hard", tries = 1)
					}
					
					game.wait(1.0)
					
					game.printToLog("[INFO] Hard difficulty for Coop is now selected.", MESSAGE_TAG = TAG)
					
					// Select the category, "Save the Oceans", which should be the 3rd category.
					game.printToLog("[INFO] Now navigating to \"In a Dusk Dream\" for Hard difficulty...", MESSAGE_TAG = TAG)
					game.gestureUtils.tap(hostButtonLocations[2].x, hostButtonLocations[2].y, "coop_host_quest")
					if (game.imageUtils.confirmLocation("coop_save_the_oceans")) {
						val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
						game.gestureUtils.tap(hostRoundButtonLocations[0].x, hostRoundButtonLocations[0].y, "coop_host_quest_circle")
					}
				} else {
					// Check if the difficulty is already selected. If not, select it.
					if (!game.findAndClickButton("coop_extra_selected", tries = 1)) {
						game.findAndClickButton("coop_extra", tries = 1)
					}
					
					game.printToLog("[INFO] Extra difficulty for Coop is now selected.", MESSAGE_TAG = TAG)
					
					val listForCoopEX1 = arrayListOf("EX1-1 Corridor of Puzzles", "empty", "EX1-3 Lost in the Dark")
					val listForCoopEX2 = arrayListOf("EX2-2 Time of Judgement", "EX2-3 Time of Revelation", "EX2-4 Time of Eminence")
					val listForCoopEX3 = arrayListOf("EX3-2 Rule of the Tundra", "EX3-3 Rule of the Plains", "EX3-4 Rule of the Twilight")
					val listForCoopEX4 = arrayListOf(
						"EX4-2 Amidst the Waves", "EX4-3 Amidst the Petals", "EX4-4 Amidst Severe Cliffs",
						"EX4-5 Amidst the Flames"
					)
					
					// Select the category for the specified EX mission. For EX2 to EX4, skip past the first missions of each.
					if (listForCoopEX1.contains(missionName)) {
						game.printToLog("[INFO] Now navigating to \"$missionName\" for EX1...", MESSAGE_TAG = TAG)
						
						game.gestureUtils.tap(hostButtonLocations[0].x, hostButtonLocations[0].y, "coop_host_quest")
						if (game.imageUtils.confirmLocation("coop_ex1")) {
							game.printToLog("[INFO] Now selecting \"$missionName\"...", MESSAGE_TAG = TAG)
							
							val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
							game.gestureUtils.tap(
								hostRoundButtonLocations[listForCoopEX1.indexOf(missionName)].x,
								hostRoundButtonLocations[listForCoopEX1.indexOf(missionName)].y,
								"coop_host_quest_circle"
							)
						}
					} else if (listForCoopEX2.contains(missionName)) {
						game.printToLog("[INFO] Now navigating to \"$missionName\" for EX2...", MESSAGE_TAG = TAG)
						
						game.gestureUtils.tap(hostButtonLocations[1].x, hostButtonLocations[1].y, "coop_host_quest")
						if (game.imageUtils.confirmLocation("coop_ex2")) {
							game.printToLog("[INFO] Now selecting \"$missionName\"...", MESSAGE_TAG = TAG)
							
							val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
							game.gestureUtils.tap(
								hostRoundButtonLocations[listForCoopEX2.indexOf(missionName) + 1].x,
								hostRoundButtonLocations[listForCoopEX2.indexOf(missionName) + 1].y,
								"coop_host_quest_circle"
							)
						}
					} else if (listForCoopEX3.contains(missionName)) {
						game.printToLog("[INFO] Now navigating to \"$missionName\" for EX3...", MESSAGE_TAG = TAG)
						
						game.gestureUtils.tap(hostButtonLocations[2].x, hostButtonLocations[2].y, "coop_host_quest")
						if (game.imageUtils.confirmLocation("coop_ex3")) {
							game.printToLog("[INFO] Now selecting \"$missionName\"...", MESSAGE_TAG = TAG)
							
							val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
							game.gestureUtils.tap(
								hostRoundButtonLocations[listForCoopEX3.indexOf(missionName) + 1].x,
								hostRoundButtonLocations[listForCoopEX3.indexOf(missionName) + 1].y,
								"coop_host_quest_circle"
							)
						}
					} else if (listForCoopEX4.contains(missionName)) {
						game.printToLog("[INFO] Now navigating to \"$missionName\" for EX4...", MESSAGE_TAG = TAG)
						
						game.gestureUtils.tap(hostButtonLocations[3].x, hostButtonLocations[3].y, "coop_host_quest")
						if (game.imageUtils.confirmLocation("coop_ex4")) {
							game.printToLog("[INFO] Now selecting \"$missionName\"...", MESSAGE_TAG = TAG)
							
							val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
							game.gestureUtils.tap(
								hostRoundButtonLocations[listForCoopEX4.indexOf(missionName) + 1].x,
								hostRoundButtonLocations[listForCoopEX4.indexOf(missionName) + 1].y,
								"coop_host_quest_circle"
							)
						}
					}
				}
				
				// After selecting the mission, create a new Coop Room.
				game.printToLog("[INFO] Now opening up a new Coop room...", MESSAGE_TAG = TAG)
				game.findAndClickButton("coop_post_to_crew_chat")
				
				// Scroll the screen down to see the "OK" button in case of small screens.
				game.gestureUtils.scroll()
				game.findAndClickButton("coop_ok")
				
				// Just in case, check for the "You retreated from the raid battle" popup.
				game.wait(1.0)
				if (game.imageUtils.confirmLocation("you_retreated_from_the_raid_battle", tries = 1)) {
					game.findAndClickButton("ok")
				}
				
				// Scroll the screen down to see the "Select Party" button in case of small screens and then tap the button.
				game.gestureUtils.swipe(500f, 1000f, 500f, 700f)
				game.findAndClickButton("coop_select_party")
			}
			
		} else if (farmingMode.toLowerCase(Locale.ROOT) == "special") {
			// Go to the Quests screen and then to the Special Quest screen.
			game.findAndClickButton("quest", suppressError = true)
			
			if (game.imageUtils.confirmLocation("quest")) {
				game.findAndClickButton("special")
				
				// Format the mission name based on the difficulty.
				val formattedMissionName: String = if (difficulty == "Normal" || difficulty == "Hard") {
					missionName.substring(2)
				} else if (difficulty == "Very Hard" || difficulty == "Extreme") {
					missionName.substring(3)
				} else {
					missionName
				}
				
				if (game.imageUtils.confirmLocation("special")) {
					if (mapName != "Campaign-Exclusive Quest" && mapName != "Basic Treasure Quests" && mapName != "Shiny Slime Search!" &&
						mapName != "Six Dragon Trial"
					) {
						// Scroll the screen down if the selected mission is located on the bottom half of the page.
						game.gestureUtils.scroll()
						
						game.wait(1.0)
					}
					
					// Find the specified mission popup.
					val missionLocation = game.imageUtils.findButton(formattedMapName) ?: throw Exception(
						"Could not find the " +
								"$formattedMapName button."
					)
					
					game.printToLog("[INFO] Navigating to $mapName...", MESSAGE_TAG = TAG)
					
					// Tap the mission's "Select" button.
					missionLocation.x += 405
					missionLocation.y += 175
					game.gestureUtils.tap(missionLocation.x, missionLocation.y, formattedMapName)
					
					if (mapName == "Basic Treasure Quests") {
						// Open up "Basic Treasure Quests" sub-missions popup.
						var roundPlayButtonLocations: ArrayList<Point> = game.imageUtils.findAll("play_round_button")
						
						when (formattedMissionName) {
							"Scarlet Trial" -> {
								game.printToLog("[INFO] Opening up Scarlet Trial mission popup...", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(roundPlayButtonLocations[0].x, roundPlayButtonLocations[0].y, "play_round_button")
							}
							"Cerulean Trial" -> {
								game.printToLog("[INFO] Opening up Cerulean Trial mission popup...", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(roundPlayButtonLocations[1].x, roundPlayButtonLocations[1].y, "play_round_button")
							}
							"Violet Trial" -> {
								game.printToLog("[INFO] Opening up Violet Trial mission popup...", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(roundPlayButtonLocations[2].x, roundPlayButtonLocations[2].y, "play_round_button")
							}
						}
						
						// Now that the mission's sub-missions popup is open, select the specified difficulty.
						game.printToLog("[INFO] Now selecting $difficulty difficulty...")
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
						game.printToLog("[INFO] Now selecting $difficulty $mapName...", MESSAGE_TAG = TAG)
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
						game.printToLog("[INFO] Now selecting $missionName...", MESSAGE_TAG = TAG)
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
						game.printToLog("[INFO] Opening up $formattedMissionName mission popup...", MESSAGE_TAG = TAG)
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
						game.printToLog("[INFO] Now selecting $difficulty difficulty...", MESSAGE_TAG = TAG)
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
						game.printToLog("[INFO] Selecting Campaign-Exclusive Quest...", MESSAGE_TAG = TAG)
						
						// There is only 1 "Play" button for this time-limited quest.
						game.findAndClickButton("play_round_button")
					}
				}
			}
			
		} else if (farmingMode.toLowerCase(Locale.ROOT) == "event" || farmingMode.toLowerCase(Locale.ROOT) == "event (token drawboxes)") {
			// Go to the first banner that is usually the current Event by tapping on the "Menu" button.
			game.findAndClickButton("home_menu")
			var bannerLocations = game.imageUtils.findAll("event_banner")
			if (bannerLocations.size == 0) {
				bannerLocations = game.imageUtils.findAll("event_banner_blue")
			}
			game.gestureUtils.tap(bannerLocations[0].x, bannerLocations[0].y, "event_banner")
			
			// Check if there is a "Daily Missions" popup and close it.
			game.wait(1.0)
			if (game.imageUtils.confirmLocation("event_daily_missions", tries = 1)) {
				game.printToLog("[INFO] Detected \"Daily Missions\" popup. Closing it...", MESSAGE_TAG = TAG)
				game.findAndClickButton("cancel")
			}
			
			// Remove the difficulty prefix from the mission name.
			val formattedMissionName: String = if (difficulty == "Normal" || difficulty == "Hard") {
				missionName.substring(2)
			} else if (difficulty == "Very Hard" || difficulty == "Extreme" || difficulty == "Impossible") {
				missionName.substring(3)
			} else {
				missionName
			}
			
			if (farmingMode.toLowerCase(Locale.ROOT) == "event") {
				game.findAndClickButton("event_special_quest")
				
				if (game.imageUtils.confirmLocation("special")) {
					// Check if there is a Nightmare already available.
					val nightmareIsAvailable: Int = if (game.imageUtils.findButton("event_nightmare", tries = 1) != null) {
						1
					} else {
						0
					}
					
					// Find the locations of all the "Select" buttons.
					val selectButtonLocations = game.imageUtils.findAll("select")
					
					// Open up Event Quests or Event Raids. Offset by 1 if there is a Nightmare available.
					if (formattedMissionName == "event quest") {
						game.printToLog("[INFO] Now hosting Event Quest...", MESSAGE_TAG = TAG)
						game.gestureUtils.tap(
							selectButtonLocations[0 + nightmareIsAvailable].x, selectButtonLocations[0 +
									nightmareIsAvailable].y,
							"select"
						)
					} else if (farmingMode.toLowerCase(Locale.ROOT) == "event raid") {
						game.printToLog("[INFO] Now hosting Event Raid...", MESSAGE_TAG = TAG)
						game.gestureUtils.tap(
							selectButtonLocations[1 + nightmareIsAvailable].x, selectButtonLocations[1 +
									nightmareIsAvailable].y,
							"select"
						)
					}
					
					game.wait(1.0)
					
					// Find the locations of all round "Play" buttons.
					val playRoundButtonLocations = game.imageUtils.findAll("play_round_button")
					
					// Now select the chosen difficulty.
					if (difficulty == "Very Hard") {
						game.gestureUtils.tap(playRoundButtonLocations[0].x, playRoundButtonLocations[0].y, "play_round_button")
					} else if (difficulty == "Extreme") {
						game.gestureUtils.tap(playRoundButtonLocations[1].x, playRoundButtonLocations[1].y, "play_round_button")
					}
				}
			} else {
				// Scroll down the screen a little bit for this UI layout that has Token Drawboxes.
				game.gestureUtils.swipe(500f, 1000f, 500f, 700f)
				
				if (formattedMissionName.toLowerCase(Locale.ROOT) == "event raid") {
					// Bring up the "Raid Battle" popup. Scroll the screen down a bit in case of small screen size.
					game.printToLog("[INFO] Now hosting Event Raid...", MESSAGE_TAG = TAG)
					game.findAndClickButton("event_raid_battle")
					game.gestureUtils.swipe(500f, 1000f, 500f, 700f)
					
					// Now select the chosen difficulty.
					when (difficulty) {
						"Very Hard" -> {
							game.findAndClickButton("event_raid_very_hard")
						}
						"Extreme" -> {
							game.findAndClickButton("event_raid_extreme")
						}
						"Impossible" -> {
							game.findAndClickButton("event_raid_impossible")
						}
					}
					
					// If the user does not have enough Treasures to host a Extreme or Impossible Raid, host a Very Hard Raid instead.
					if (difficulty == "Extreme" && !game.imageUtils.waitVanish("event_raid_extreme", timeout = 3)) {
						game.printToLog(
							"[INFO] Not enough treasures to host Extreme Raid. Hosting Very Hard Raid instead...",
							MESSAGE_TAG = TAG
						)
						game.findAndClickButton("event_very_hard_raid")
					} else if (difficulty == "Impossible" && !game.imageUtils.waitVanish("event_raid_impossible", timeout = 3)) {
						game.printToLog(
							"[INFO] Not enough treasures to host Impossible Raid. Hosting Very Hard Raid instead...",
							MESSAGE_TAG = TAG
						)
						game.findAndClickButton("event_very_hard_raid")
					}
				} else if (formattedMissionName.toLowerCase(Locale.ROOT) == "event quest") {
					game.printToLog("[INFO] Now hosting Event Quest...", MESSAGE_TAG = TAG)
					game.findAndClickButton("event_quests")
					
					game.wait(1.0)
					
					// Find the locations of all round "Play" buttons.
					val playRoundButtonLocations = game.imageUtils.findAll("play_round_button")
					
					// Now select the chosen difficulty.
					when (difficulty) {
						"Normal" -> {
							game.gestureUtils.tap(playRoundButtonLocations[0].x, playRoundButtonLocations[0].y, "play_round_button")
						}
						"Hard" -> {
							game.gestureUtils.tap(playRoundButtonLocations[1].x, playRoundButtonLocations[1].y, "play_round_button")
						}
						"Very Hard" -> {
							game.gestureUtils.tap(playRoundButtonLocations[2].x, playRoundButtonLocations[2].y, "play_round_button")
						}
						"Extreme" -> {
							game.gestureUtils.tap(playRoundButtonLocations[3].x, playRoundButtonLocations[3].y, "play_round_button")
						}
					}
				}
			}
			
		} else if (farmingMode.toLowerCase(Locale.ROOT) == "dread barrage") {
			// Scroll the screen down a little bit and then click on the Dread Barrage banner.
			game.printToLog("[INFO] Now navigating to Dread Barrage...", MESSAGE_TAG = TAG)
			game.gestureUtils.swipe(500f, 1000f, 500f, 700f)
			game.findAndClickButton("dread_barrage")
			
			game.wait(2.0)
			
			if (game.imageUtils.confirmLocation("dread_barrage")) {
				// Check if there is already a hosted Dread Barrage mission.
				if (game.imageUtils.confirmLocation("resume_quests", tries = 1)) {
					game.printToLog("[WARNING] Detected that there is already a hosted Dread Barrage mission.", MESSAGE_TAG = TAG)
					var expiryTimeInSeconds = 0
					
					while (game.imageUtils.confirmLocation("resume_quests", tries = 1)) {
						// The bot will wait for a total of 1 hour and 30 miuntes for either the Raid's timer to expire or for anyone else in
						// the room to clear it.
						game.printToLog(
							"[WARNING] The bot will now either wait for the expiry time of 1 hour and 30 minutes or for " +
									"someone else in the room to clear it.", MESSAGE_TAG = TAG
						)
						game.printToLog(
							"[WARNING] The bot will now refresh the page every 30 seconds to check if it is still there " +
									"before proceeding.", MESSAGE_TAG = TAG
						)
						game.printToLog(
							"User can either wait it out, revive and fight it to completion, or retreat from the mission " +
									"manually.", MESSAGE_TAG = TAG
						)
						
						game.wait(30.0)
						
						game.findAndClickButton("reload")
						game.wait(2.0)
						
						expiryTimeInSeconds += 30
						if (expiryTimeInSeconds >= 5400) {
							break
						}
					}
					
					game.printToLog(
						"[SUCCESS] Hosted Dread Barrage mission is now gone either because of timeout or someone else in " +
								"the room killed it. Moving on...", MESSAGE_TAG = TAG
					)
				}
				
				// Find the locations of all the "Play" buttons at the top of the window.
				val dreadBarragePlayButtonLocations = game.imageUtils.findAll("dread_barrage_play")
				
				// Now select the chosen difficulty.
				when (difficulty) {
					"1 Star" -> {
						game.printToLog("[INFO] Now starting 1 Star Dread Barrage Raid...", MESSAGE_TAG = TAG)
						game.gestureUtils.tap(dreadBarragePlayButtonLocations[0].x, dreadBarragePlayButtonLocations[0].y, "dread_barrage_play")
					}
					"2 Star" -> {
						game.printToLog("[INFO] Now starting 2 Star Dread Barrage Raid...", MESSAGE_TAG = TAG)
						game.gestureUtils.tap(dreadBarragePlayButtonLocations[1].x, dreadBarragePlayButtonLocations[1].y, "dread_barrage_play")
					}
					"3 Star" -> {
						game.printToLog("[INFO] Now starting 3 Star Dread Barrage Raid...", MESSAGE_TAG = TAG)
						game.gestureUtils.tap(dreadBarragePlayButtonLocations[2].x, dreadBarragePlayButtonLocations[2].y, "dread_barrage_play")
					}
					"4 Star" -> {
						game.printToLog("[INFO] Now starting 4 Star Dread Barrage Raid...", MESSAGE_TAG = TAG)
						game.gestureUtils.tap(dreadBarragePlayButtonLocations[3].x, dreadBarragePlayButtonLocations[3].y, "dread_barrage_play")
					}
					"5 Star" -> {
						game.printToLog("[INFO] Now starting 5 Star Dread Barrage Raid...", MESSAGE_TAG = TAG)
						game.gestureUtils.tap(dreadBarragePlayButtonLocations[4].x, dreadBarragePlayButtonLocations[4].y, "dread_barrage_play")
					}
				}
			}
		} else if (farmingMode.toLowerCase(Locale.ROOT) == "guild wars") {
			game.printToLog("[INFO] Navigating to Guild Wars now.", MESSAGE_TAG = TAG)
			
			// Go to the Guild Wars banner by tapping on the "Menu" button.
			game.findAndClickButton("home_menu")
			var bannerLocations = game.imageUtils.findAll("event_banner")
			if (bannerLocations.size == 0) {
				bannerLocations = game.imageUtils.findAll("event_banner_blue")
			}
			game.gestureUtils.tap(bannerLocations[0].x, bannerLocations[0].y, "event_banner")
			
			game.wait(1.0)
			
			if (game.imageUtils.confirmLocation("guild_wars")) {
				// Scroll down the screen a bit.
				game.gestureUtils.swipe(500f, 1000f, 500f, 700f)
				
				game.wait(1.0)
				
				if (difficulty == "Very Hard" || difficulty == "Extreme" || difficulty == "Extreme+") {
					game.printToLog("[INFO] Now proceeding to farm meat.", MESSAGE_TAG = TAG)
					
					// Click on the banner to farm meat.
					game.findAndClickButton("guild_wars_meat")
					
					if (game.imageUtils.confirmLocation("guild_wars_meat")) {
						// Now click on the specified Mission to start.
						val meatLocation = game.imageUtils.findButton("guild_wars_meat_very_hard")!!
						if (difficulty == "Very Hard") {
							game.printToLog("Hosting Very Hard now.", MESSAGE_TAG = TAG)
							game.gestureUtils.tap(meatLocation.x, meatLocation.y, "guild_wars_meat_very_hard")
						} else if (difficulty == "Extreme") {
							game.printToLog("Hosting Extreme now.", MESSAGE_TAG = TAG)
							game.gestureUtils.tap(meatLocation.x + 230.0, meatLocation.y, "guild_wars_meat_very_hard")
						} else if (difficulty == "Extreme+") {
							game.printToLog("Hosting Extreme+ now.", MESSAGE_TAG = TAG)
							game.gestureUtils.tap(meatLocation.x + 620.0, meatLocation.y, "guild_wars_meat_very_hard")
						}
					}
				} else {
					game.printToLog("[INFO] Now proceeding to farm Nightmares.", MESSAGE_TAG = TAG)
					
					var startCheckForNM150 = false
					
					// Click on the banner to farm Nightmares.
					if (difficulty != "NM150") {
						game.findAndClickButton("guild_wars_nightmare")
					} else {
						game.printToLog("Hosting NM150 now.", MESSAGE_TAG = TAG)
						game.findAndClickButton("guild_wars_nightmare_150")
						
						if (game.imageUtils.confirmLocation("guild_wars_nightmare") && game.findAndClickButton("start")) {
							startCheckForNM150 = true
						}
					}
					
					if (difficulty != "NM150" && game.imageUtils.confirmLocation("guild_wars_nightmare")) {
						// If today is the first day of Guild Wars, only NM90 is available.
						when {
							game.imageUtils.confirmLocation("guild_wars_nightmare_first_day", tries = 1) -> {
								game.printToLog("[INFO] Today is the first day so hosting NM90.", MESSAGE_TAG = TAG)
								game.findAndClickButton("ok")
							}
							
							// Now click on the specified Mission to start.
							difficulty == "NM90" -> {
								game.printToLog("Hosting NM90 now.", MESSAGE_TAG = TAG)
								game.findAndClickButton("guild_wars_nightmare_90")
							}
							difficulty == "NM95" -> {
								game.printToLog("Hosting NM95 now.", MESSAGE_TAG = TAG)
								game.findAndClickButton("guild_wars_nightmare_95")
							}
							difficulty == "NM100" -> {
								game.printToLog("Hosting NM100 now.", MESSAGE_TAG = TAG)
								game.findAndClickButton("guild_wars_nightmare_100")
							}
						}
					} else if (!startCheckForNM150) {
						// If there is not enough meat to host, host Extreme+ instead.
						game.printToLog("[WARNING] User lacks the meat to host the Nightmare. Farming Extreme+ instead.", MESSAGE_TAG = TAG)
						if (difficulty != "NM150") {
							game.findAndClickButton("close")
						} else {
							game.findAndClickButton("cancel")
						}
						
						game.printToLog("[INFO] Hosting Extreme+ now.", MESSAGE_TAG = TAG)
						
						// Click on the banner to farm meat.
						game.findAndClickButton("guild_wars_meat")
						
						if (game.imageUtils.confirmLocation("guild_wars_meat")) {
							game.printToLog("Hosting Extreme+ now.", MESSAGE_TAG = TAG)
							val meatLocation = game.imageUtils.findButton("guild_wars_meat_very_hard")!!
							game.gestureUtils.tap(meatLocation.x + 300.0, meatLocation.y, "guild_wars_meat_very_hard")
						}
					}
				}
			}
		}
		
		game.wait(1.0)
		
		// At this point, the bot has already selected the mission and thus it should now check if it needs any AP.
		game.checkAP()
		
		// Finally, double-check to see if the bot is at the Summon Selection screen.
		if (farmingMode.toLowerCase(Locale.ROOT) != "coop") {
			game.printToLog("[INFO] Now checking if the bot is currently at the Summon Selection screen...", MESSAGE_TAG = TAG)
			return if (game.imageUtils.confirmLocation("select_summon")) {
				game.printToLog("[SUCCESS] Bot arrived at the Summon Selection screen after selecting the mission.", MESSAGE_TAG = TAG)
				true
			} else {
				game.printToLog(
					"[WARNING] Bot did not arrive at the Summon Selection screen after selecting the mission.",
					MESSAGE_TAG = TAG
				)
				false
			}
		} else {
			game.printToLog("[INFO] Now checking if the bot is currently at the Coop Party Selection screen...", MESSAGE_TAG = TAG)
			return if (game.imageUtils.confirmLocation("coop_without_support_summon")) {
				game.printToLog(
					"[SUCCESS] Bot arrived at the Party Selection screen after selecting the Coop mission.",
					MESSAGE_TAG = TAG
				)
				true
			} else {
				game.printToLog(
					"[WARNING] Bot did not arrive at the Party Selection screen after selecting the Coop mission.",
					MESSAGE_TAG = TAG
				)
				false
			}
		}
	}
	
	/**
	 * Process a Pending Battle.
	 */
	private fun clearPendingBattle() {
		game.findAndClickButton("tap_here_to_see_rewards")
		game.wait(1.0)
		
		if (game.imageUtils.confirmLocation("no_loot", tries = 1)) {
			game.printToLog("[INFO] No loot can be collected. Backing out...")
			
			// Navigate back to the Quests screen.
			game.findAndClickButton("quests")
			
			if (numberOfRaidsJoined > 0) {
				numberOfRaidsJoined -= 1
			}
		} else {
			// Start loot detection if there it is available.
			game.collectLoot(isPendingBattle = true)
			
			if (numberOfRaidsJoined > 0) {
				numberOfRaidsJoined -= 1
			}
		}
	}
	
	/**
	 * Check and collect any pending rewards and free up slots for the bot to join more Raids.
	 *
	 * @param farmingMode The current farming mode will dictate what logic to follow for Pending Battles.
	 * @return True if Pending Battles were detected. False otherwise.
	 */
	fun checkPendingBattles(farmingMode: String): Boolean {
		game.wait(1.0)
		
		// Check for the "Check your Pending Battles" popup when navigating to the Quest screen or attempting to join a raid when there are 6
		// Pending Battles or check if the "Play Again" button is covered by the "Pending Battles" button for any other Farming Mode.
		if ((farmingMode.toLowerCase(Locale.ROOT) == "raid" && game.imageUtils.confirmLocation(
				"check_your_pending_battles", tries = 1,
				suppressError = true
			)) ||
			(farmingMode.toLowerCase(Locale.ROOT) != "raid" && game.imageUtils.confirmLocation(
				"quest_results_pending_battles", tries = 1,
				suppressError = true
			))
		) {
			game.printToLog("[INFO] Found Pending Battles that need collecting from.", MESSAGE_TAG = TAG)
			
			if (farmingMode.toLowerCase(Locale.ROOT) == "raid") {
				game.findAndClickButton("ok")
			} else {
				game.findAndClickButton("quest_results_pending_battles")
			}
			
			game.wait(1.0)
			
			if (game.imageUtils.confirmLocation("pending_battles", tries = 1)) {
				while (game.imageUtils.findButton("tap_here_to_see_rewards", tries = 1) != null) {
					clearPendingBattle()
					
					// While on the Loot Collected screen, if there are more Pending Battles then head back to the Pending Battles screen.
					if (game.findAndClickButton("quest_results_pending_battles", tries = 1)) {
						game.wait(1.0)
						
						// TODO: Close the Skyscope mission popup.
						
						game.checkFriendRequest()
						game.wait(1.0)
					} else {
						// When there are no more Pending Battles, go back to the Quests screen.
						game.findAndClickButton("quests")
						
						// TODO: Close the Skyscope mission popup.
						
						break
					}
				}
			}
			
			game.printToLog("[INFO] No Pending Battles needed to be cleared.", MESSAGE_TAG = TAG)
			return true
		}
		
		return false
	}
	
	/**
	 * Check and updates the number of Raids currently joined.
	 */
	private fun checkJoinedRaids() {
		game.wait(1.0)
		
		val joinedLocations = game.imageUtils.findAll("joined")
		if (joinedLocations.size != 0) {
			numberOfRaidsJoined = joinedLocations.size
		}
	}
	
	/**
	 * Navigates the bot to the Backup Requests screen and attempt to join a specified Raid.
	 *
	 * @param missionName Name of the Raid.
	 * @return True if the bot reached the Summon Selection screen after joining a Raid. False otherwise.
	 */
	fun joinRaid(missionName: String): Boolean {
		// Go to the Home screen and then to the Quests screen.
		game.goBackHome(confirmLocationCheck = true)
		game.findAndClickButton("quest")
		
		game.wait(1.0)
		
		// Check for the "You retreated from the raid battle" popup.
		if (game.imageUtils.confirmLocation("you_retreated_from_the_raid_battle", tries = 1)) {
			game.findAndClickButton("ok")
		}
		
		if (game.imageUtils.confirmLocation("quest")) {
			checkPendingBattles("raid")
			
			game.wait(1.0)
			
			// Now go to the Backup Requests screen.
			game.findAndClickButton("raid")
			
			game.wait(1.0)
			
			Log.d(TAG, "Looking for $missionName.")
			
			// Loop and try to join a Raid from the parsed list of room codes. If none of the codes worked, wait 60 seconds before trying again.
			var firstRun = true
			var tries = 10
			while (tries > 0) {
				// Check for any joined Raids.
				checkJoinedRaids()
				
				// While the user has passed the limit of 3 Raids currently joined, wait and recheck to see if any finish.
				while (numberOfRaidsJoined >= 3) {
					game.printToLog("[INFO] Detected maximum of 3 raids joined. Waiting 60 seconds to see if any finish.", MESSAGE_TAG = TAG)
					game.goBackHome(confirmLocationCheck = true)
					
					game.wait(60.0)
					
					game.findAndClickButton("quest")
					game.wait(1.0)
					game.findAndClickButton("raid")
					
					checkPendingBattles("Raid")
				}
				
				// Move to the "Enter ID" section of the Backup Requests screen.
				game.printToLog("[INFO] Moving to the \"Enter ID\" section of the Backup Requests screen...", MESSAGE_TAG = TAG)
				game.findAndClickButton("enter_id")
				
				// Save the locations of the "Join Room" button and the "Room Code" text box.
				var joinRoomButtonLocation: Point? = Point()
				var roomCodeTextBoxLocation: Point? = Point()
				if (firstRun) {
					joinRoomButtonLocation = game.imageUtils.findButton("join_a_room")!!
					roomCodeTextBoxLocation = Point(joinRoomButtonLocation.x - 410.0, joinRoomButtonLocation.y)
				}
				
				var roomCodes: ArrayList<String> = arrayListOf()
				// Get recent room codes for the specified Raid.
				if (twitterRoomFinder != null) {
					roomCodes = twitterRoomFinder.findMostRecentRoomCodes(missionName)
				}
				
				roomCodes.forEach { roomCode ->
					Log.d(TAG, "Room code: $roomCode")
					
					// Set the room code.
					RoomCodeData.roomCode = roomCode
					
					// Select the "Room Code" text box. The AccessibilityService should pick up that the textbox is a EditText and will paste the
					// room code into it.
					game.gestureUtils.tap(roomCodeTextBoxLocation?.x!!, roomCodeTextBoxLocation.y, "template_room_code_textbox", longPress = true)
					
					// Wait several seconds to allow enough time for MyAccessibilityService to paste the code.
					game.wait(3.0)
					
					// Now tap the "Join Room" button.
					game.gestureUtils.tap(joinRoomButtonLocation?.x!!, joinRoomButtonLocation.y, "join_a_room")
					
					if (!checkPendingBattles("raid") && !game.imageUtils.confirmLocation("raid_already_ended", tries = 1)
						&& !game.imageUtils.confirmLocation("already_taking_part", tries = 1)
						&& !game.imageUtils.confirmLocation("invalid_code", tries = 1)
					) {
						// Check for EP.
						game.checkEP()
						
						game.printToLog("[SUCCESS] Joining {room_code} was successful.", MESSAGE_TAG = TAG)
						numberOfRaidsJoined += 1
						
						return game.imageUtils.confirmLocation("select_summon")
					} else {
						// Clear the text box by reloading the page.
						game.printToLog("[WARNING] $roomCode already ended or invalid.", MESSAGE_TAG = TAG)
						game.findAndClickButton("reload")
						firstRun = false
						
						game.wait(1.0)
						game.findAndClickButton("enter_id")
					}
				}
				
				tries -= 1
				game.printToLog(
					"[WARNING] Could not find any valid room codes. \\nWaiting 60 seconds and then trying again with $tries " +
							"tries left before exiting.", MESSAGE_TAG = TAG
				)
				game.wait(60.0)
			}
		}
		
		return false
	}
}