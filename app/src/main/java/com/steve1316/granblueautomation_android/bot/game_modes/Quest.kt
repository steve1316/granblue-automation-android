package com.steve1316.granblueautomation_android.bot.game_modes

import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.bot.Game

class QuestException(message: String) : Exception(message)

class Quest(private val game: Game, private val mapName: String, private val missionName: String) {
	private val tag: String = "${MainActivity.loggerTag}_Quest"
	
	private val listPage1 = listOf("Zinkenstill", "Port Breeze Archipelago", "Valtz Duchy", "Auguste Isles", "Lumacie Archipelago", "Albion Citadel")
	private val listPage2 = listOf("Mist-Shrouded Isle", "Golonzo Island", "Amalthea Island", "Former Capital Mephorash", "Agastia")
	
	/**
	 * Helper function to assist selectMap() in navigating to the correct island for Quest Farming Mode.
	 *
	 * @param mapName Name of the island to navigate to.
	 * @param currentLocation The name of the island that the bot is currently at.
	 * @return True if the bot arrived at the correct island. False otherwise.
	 */
	private fun navigateToMap(mapName: String, currentLocation: String): Boolean {
		// Phantagrande Skydom Page 1
		if (listPage1.contains(mapName)) {
			// Switch pages if needed.
			if (listPage2.contains(currentLocation)) {
				game.findAndClickButton("world_left_arrow")
			}
			
			// Use a manual way to tap on the correct island if image matching for the island name failed.
			if (!game.findAndClickButton(mapName.lowercase().replace(" ", "_").replace("-", "_"))) {
				val arrowLocation = game.imageUtils.findButton("world_right_arrow") ?: throw Exception("Unable to find the location of the right arrow for the World.")
				
				when (mapName) {
					"Port Breeze Archipelago" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x - 485, arrowLocation.y - 225, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 717, arrowLocation.y - 289, "world_right_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x - 526, arrowLocation.y - 250, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 420, arrowLocation.y - 190, "world_right_arrow")
							}
						}
					}
					"Valtz Duchy" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x - 230, arrowLocation.y - 110, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 344, arrowLocation.y - 118, "world_right_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x - 250, arrowLocation.y - 122, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 205, arrowLocation.y - 90, "world_right_arrow")
							}
						}
					}
					"Auguste Isles" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x - 560, arrowLocation.y + 5, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 840, arrowLocation.y + 54, "world_right_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x - 620, arrowLocation.y + 6, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 480, arrowLocation.y + 15, "world_right_arrow")
							}
						}
					}
					"Lumacie Archipelago" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x - 130, arrowLocation.y + 75, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 177, arrowLocation.y + 159, "world_right_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x - 140, arrowLocation.y + 88, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 115, arrowLocation.y + 70, "world_right_arrow")
							}
						}
					}
					"Albion Citadel" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x - 400, arrowLocation.y + 200, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 589, arrowLocation.y + 344, "world_right_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x - 435, arrowLocation.y + 200, "world_right_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x - 345, arrowLocation.y + 180, "world_right_arrow")
							}
						}
					}
				}
			}
			
			return true
		}
		
		// Phantagrande Skydom Page 2
		else if (listPage2.contains(mapName)) {
			// Switch pages if needed.
			if (listPage1.contains(currentLocation)) {
				game.findAndClickButton("world_right_arrow")
			}
			
			// Use a manual way to tap on the correct island if image matching for the island name failed.
			if (!game.findAndClickButton(mapName.lowercase().replace(" ", "_").replace("-", "_"))) {
				val arrowLocation = game.imageUtils.findButton("world_left_arrow") ?: throw Exception("Unable to find the location of the left arrow for the World.")
				
				when (mapName) {
					"Mist-Shrouded Isle" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x + 240, arrowLocation.y + 190, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 379, arrowLocation.y + 342, "world_left_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x + 270, arrowLocation.y + 200, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 210, arrowLocation.y + 175, "world_left_arrow")
							}
						}
					}
					"Golonzo Island" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x + 540, arrowLocation.y + 145, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 820, arrowLocation.y + 255, "world_left_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x + 526, arrowLocation.y + 146, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 460, arrowLocation.y + 125, "world_left_arrow")
							}
						}
					}
					"Amalthea Island" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x + 190, arrowLocation.y + 5, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 288, arrowLocation.y + 34, "world_left_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x + 220, arrowLocation.y + 11, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 170, arrowLocation.y + 5, "world_left_arrow")
							}
						}
					}
					"Former Capital Mephorash" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x + 535, arrowLocation.y - 60, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 802, arrowLocation.y - 43, "world_left_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x + 595, arrowLocation.y - 67, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 460, arrowLocation.y - 50, "world_left_arrow")
							}
						}
					}
					"Agastia" -> {
						if (!game.imageUtils.isTablet) {
							if (game.imageUtils.isLowerEnd) {
								game.gestureUtils.tap(arrowLocation.x + 290, arrowLocation.y - 200, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 440, arrowLocation.y - 267, "world_left_arrow")
							}
						} else {
							if (!game.imageUtils.isLandscape) {
								game.gestureUtils.tap(arrowLocation.x + 320, arrowLocation.y - 226, "world_left_arrow")
							} else {
								game.gestureUtils.tap(arrowLocation.x + 250, arrowLocation.y - 175, "world_left_arrow")
							}
						}
					}
				}
			}
			
			return true
		}
		
		return false
	}
	
	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		game.printToLog("\n[QUEST] Now beginning process to navigate to the mission: $missionName...", tag = tag)
		
		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)
		
		// Format the map name.
		val formattedMapName = mapName.lowercase().replace(" ", "_").replace("-", "_")
		
		val checkLocation: Boolean
		var currentLocation = ""
		
		// Check if the bot is already at the island where the mission takes place in. If not, navigate to it.
		if (game.imageUtils.confirmLocation("map_$formattedMapName", tries = 1)) {
			game.printToLog("[QUEST] Bot is currently on the correct island for the mission.", tag = tag)
			checkLocation = true
		} else {
			game.printToLog("[QUEST] Bot is not on the correct island for the mission. Navigating to the correct island...")
			checkLocation = false
			
			// Determine what island the bot is currently at.
			val locationList = listOf(
				"Zinkenstill", "Port Breeze Archipelago", "Valtz Duchy", "Auguste Isles", "Lumacie Archipelago", "Albion Citadel", "Mist-Shrouded Isle",
				"Golonzo Island", "Amalthea Island", "Former Capital Mephorash", "Agastia"
			)
			
			var locationIndex = 0
			while (locationIndex < locationList.size) {
				val tempMapLocation = locationList[locationIndex]
				val tempFormattedMapLocation = tempMapLocation.lowercase().replace(" ", "_").replace("-", "_")
				
				if (game.imageUtils.confirmLocation("map_${tempFormattedMapLocation}", tries = 1)) {
					game.printToLog("\n[QUEST] Bot's current location is at ${tempFormattedMapLocation}. Now moving to ${mapName}...", tag = tag)
					currentLocation = tempMapLocation
					break
				}
				
				locationIndex += 1
			}
		}
		
		// Once the bot has determined where it is, go to the Quest screen.
		game.findAndClickButton("quest", suppressError = true)
		
		game.wait(3.0)
		
		// Check for the "You retreated from the raid battle" popup.
		if (game.imageUtils.confirmLocation("you_retreated_from_the_raid_battle", tries = 1)) {
			game.findAndClickButton("ok")
		}
		
		if (game.imageUtils.confirmLocation("quest")) {
			// If the bot is currently not at the correct island, move to it.
			if (!checkLocation) {
				// Tap the "World" button.
				game.findAndClickButton("world")
				
				// Now on the World screen, tap the specified coordinates of the screen to move to that island. Switch pages if necessary.
				navigateToMap(mapName, currentLocation)
				
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
			
			// Now that the bot is on the correct island, tap on the correct chapter node.
			if (missionName == "Scattered Cargo") {
				game.printToLog("[QUEST] Moving to Chapter 1 (115) node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 227, worldButtonLocation.y + 213, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 155, worldButtonLocation.y + 170, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 115, worldButtonLocation.y + 135, "template_node")
					}
				}
			} else if (missionName == "Lucky Charm Hunt") {
				game.printToLog("[QUEST] Moving to Chapter 6 (122) node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 757, worldButtonLocation.y + 43, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 545, worldButtonLocation.y + 40, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 425, worldButtonLocation.y + 30, "template_node")
					}
				}
			} else if (missionName == "Special Op's Request") {
				game.printToLog("[QUEST] Moving to Chapter 8 node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 577, worldButtonLocation.y + 343, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 420, worldButtonLocation.y + 263, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 330, worldButtonLocation.y + 205, "template_node")
					}
				}
			} else if (missionName == "Threat to the Fisheries") {
				game.printToLog("[QUEST] Moving to Chapter 9 node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 497, worldButtonLocation.y + 258, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 350, worldButtonLocation.y + 200, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 275, worldButtonLocation.y + 160, "template_node")
					}
				}
			} else if (missionName == "The Fruit of Lumacie" || missionName == "Whiff of Danger") {
				game.printToLog("[QUEST] Moving to Chapter 13 (39/52) node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 197, worldButtonLocation.y + 208, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 125, worldButtonLocation.y + 160, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 95, worldButtonLocation.y + 125, "template_node")
					}
				}
			} else if (missionName == "I Challenge You!") {
				game.printToLog("[QUEST] Moving to Chapter 17 node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 262, worldButtonLocation.y + 268, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 180, worldButtonLocation.y + 206, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 140, worldButtonLocation.y + 165, "template_node")
					}
				}
			} else if (missionName == "For Whom the Bell Tolls") {
				game.printToLog("[QUEST] Moving to Chapter 22 node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 417, worldButtonLocation.y + 78, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 295, worldButtonLocation.y + 65, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 230, worldButtonLocation.y + 50, "template_node")
					}
				}
			} else if (missionName == "Golonzo's Battles of Old") {
				game.printToLog("[QUEST] Moving to Chapter 25 node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 457, worldButtonLocation.y + 18, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 325, worldButtonLocation.y + 25, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 255, worldButtonLocation.y + 15, "template_node")
					}
				}
			} else if (missionName == "The Dungeon Diet") {
				game.printToLog("[QUEST] Moving to Chapter 30 (44/65) node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 557, worldButtonLocation.y + 48, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 400, worldButtonLocation.y + 50, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 310, worldButtonLocation.y + 40, "template_node")
					}
				}
			} else if (missionName == "Trust Busting Dustup") {
				game.printToLog("[QUEST] Moving to Chapter 36 (123) node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 714, worldButtonLocation.y + 30, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 530, worldButtonLocation.y + 33, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 415, worldButtonLocation.y + 30, "template_node")
					}
				}
			} else if (missionName == "Erste Kingdom Episode 4") {
				game.printToLog("[QUEST] Moving to Chapter 70 node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 587, worldButtonLocation.y + 318, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 420, worldButtonLocation.y + 235, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 325, worldButtonLocation.y + 185, "template_node")
					}
				}
			} else if (missionName == "Imperial Wanderer's Soul") {
				game.printToLog("[QUEST] Moving to Chapter 55 node...", tag = tag)
				if (!game.imageUtils.isTablet) {
					game.gestureUtils.tap(worldButtonLocation.x + 350, worldButtonLocation.y + 320, "template_node")
				} else {
					if (!game.imageUtils.isLandscape) {
						game.gestureUtils.tap(worldButtonLocation.x + 260, worldButtonLocation.y + 245, "template_node")
					} else {
						game.gestureUtils.tap(worldButtonLocation.x + 200, worldButtonLocation.y + 195, "template_node")
					}
				}
			}
			
			// Now that the correct chapter node has been selected, scroll down the screen.
			game.printToLog("[QUEST] Now bringing up the Summon Selection screen for \"$missionName\"...", tag = tag)
			game.gestureUtils.scroll()
			
			game.wait(2.0)
			
			// Now tap on the mission node to start.
			val formattedMissionName = missionName.lowercase().replace(" ", "_")
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
				
				// Close the "Items Picked Up" popup.
				if (game.imageUtils.confirmLocation("items_picked_up")) {
					game.findAndClickButton("ok")
				}
				
				// Now start Combat Mode and detect any item drops.
				if (game.combatMode.startCombatMode(game.combatScript)) {
					numberOfItemsDropped = game.collectLoot()
				}
			}
		} else {
			throw QuestException("Failed to arrive at the Summon Selection screen.")
		}
		
		return numberOfItemsDropped
	}
}