package com.steve1316.granblue_automation_android.bot.game_modes

import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game

class QuestException(message: String) : Exception(message)

class Quest(private val game: Game, private val mapName: String, private val missionName: String) {
	private val tag: String = "${loggerTag}Quest"

	private val phantagrandePage1Islands = listOf("Zinkenstill", "Port Breeze Archipelago", "Valtz Duchy", "Auguste Isles", "Lumacie Archipelago", "Albion Citadel")
	private val phantagrandePage2Islands = listOf("Mist-Shrouded Isle", "Golonzo Island", "Amalthea Island", "Former Capital Mephorash", "Agastia")
	private val nalhegrandePage1Islands = listOf("Merkmal Island", "Groz Island", "Kluger Island", "The Edgelands")
	private val nalhegrandePage2Islands = listOf("Bestia Island", "Reiche Island", "Starke Island")
	private val oarlyegrandePage1Islands = listOf("New Utopia")

	/**
	 * Exit out of the current skydom onto the world map.
	 *
	 * @param currentSkydom Name of the skydom that the bot is currently at.
	 */
	private fun exitSkydom(currentSkydom: String) {
		if (currentSkydom.contains("Phantagrande")) {
			// Attempt to move to the right-most section of the skydom.
			game.findAndClickButton("world_right_arrow", suppressError = true)

			if (!game.findAndClickButton("world_skydom")) {
				throw QuestException("Failed to move out of the Phantagrande Skydom.")
			}
		} else if (currentSkydom.contains("Nalhegrande")) {
			// Attempt to move to the left-most section of the skydom.
			game.findAndClickButton("world_left_arrow", suppressError = true)

			if (!game.findAndClickButton("world_skydom")) {
				throw QuestException("Failed to move out of the Nalhegrande Skydom.")
			}
		} else if (currentSkydom.contains("Oarlyegrande")) {
			if (!game.findAndClickButton("world_skydom")) {
				throw QuestException("Failed to move out of the Oarlyegrande Skydom.")
			}
		}

		game.wait(3.0)
	}

	/**
	 * Enter a skydom from the world map.
	 *
	 * @param newSkydom Name of the skydom that the bot will be moving to.
	 */
	private fun enterSkydom(newSkydom: String) {
		if (newSkydom.contains("Phantagrande")) {
			if (!game.findAndClickButton("skydom_phantagrande")) {
				throw QuestException("Failed to move into Phantagrande Skydom")
			}
		} else if (newSkydom.contains("Nalhegrande")) {
			if (!game.findAndClickButton("skydom_nalhegrande")) {
				throw QuestException("Failed to move into Nalhegrande Skydom")
			}
		} else if (newSkydom.contains("Oarlyegrande")) {
			if (!game.findAndClickButton("skydom_oarlyegrande")) {
				throw QuestException("Failed to move into Oarlyegrande Skydom")
			}
		}

		game.wait(3.0)
	}

	/**
	 * Navigates the bot to the specified island inside the Phantagrande Skydom.
	 *
	 * @param mapName Name of the expected Island inside the Phantagrande Skydom.
	 * @param currentIsland Name of the Island inside the Phantagrande Skydom that the bot is currently at.
	 */
	private fun navigateToPhantagrandeSkydomIsland(mapName: String, currentIsland: String) {
		game.printToLog("\n[QUEST] Beginning process to navigate to the island inside the Phantagrande Skydom: $mapName...", tag = tag)

		if (phantagrandePage1Islands.contains(mapName)) {
			// Switch pages if bot is on Page 2.
			if (phantagrandePage2Islands.contains(currentIsland) && !game.findAndClickButton("world_left_arrow")) {
				throw QuestException("Failed to move to Page 1 of Phantagrande Skydom.")
			}

			// Move to the expected Island.
			if (!game.findAndClickButton(mapName.lowercase().replace(" ", "_").replace("-", "_"))) {
				// If the name of the island is obscured, like by the "Next" text indicating that the user's next quest is there, fallback to the manual method.
				val arrowLocation = game.imageUtils.findButton("world_right_arrow") ?: throw QuestException("Unable to find the location of the right arrow for the World.")

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
					else -> {
						throw QuestException("Unexpected map name when trying to navigate in Phantagrande Skydom Page 1: $mapName")
					}
				}
			}
		} else if (phantagrandePage2Islands.contains(mapName)) {
			if (phantagrandePage1Islands.contains(currentIsland) && !game.findAndClickButton("world_right_arrow")) {
				throw QuestException("Failed to move to Page 2 of Phantagrande Skydom.")
			}

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
					else -> {
						throw QuestException("Unexpected map name when trying to navigate in Phantagrande Skydom Page 2: $mapName")
					}
				}
			}
		}

		// Press "Go" on the popup after pressing on the island node.
		if (!game.findAndClickButton("go")) {
			throw QuestException("Failed to enter $mapName as the Go button is missing.")
		}
	}

	/**
	 * Navigates the bot to the specified island inside the Nalhegrande Skydom.
	 *
	 * @param mapName Name of the expected Island inside the Nalhegrande Skydom.
	 * @param currentIsland Name of the Island inside the Nalhegrande Skydom that the bot is currently at.
	 */
	private fun navigateToNalhegrandeSkydomIsland(mapName: String, currentIsland: String) {
		game.printToLog("\n[QUEST] Beginning process to navigate to the island inside the Nalhegrande Skydom: $mapName...", tag = tag)

		if (nalhegrandePage1Islands.contains(mapName)) {
			// Switch pages if bot is on Page 2.
			if (nalhegrandePage2Islands.contains(currentIsland) && !game.findAndClickButton("world_left_arrow")) {
				throw QuestException("Failed to move to Page 1 of Nalhegrande Skydom.")
			}

			// Move to the expected Island.
			if (!game.findAndClickButton(mapName.lowercase().replace(" ", "_").replace("-", "_"))) {
				val arrowLocation = game.imageUtils.findButton("world_right_arrow") ?: throw Exception("Unable to find the location of the right arrow for the World.")

				when (mapName) {
					"Merkmal Island" -> {
						game.gestureUtils.tap(arrowLocation.x - 790, arrowLocation.y + 475, "world_right_arrow")
					}
					"Groz Island" -> {
						game.gestureUtils.tap(arrowLocation.x - 215, arrowLocation.y + 200, "world_right_arrow")
					}
					"Kluger Island" -> {
						game.gestureUtils.tap(arrowLocation.x - 695, arrowLocation.y + 55, "world_right_arrow")
					}
					"The Edgelands" -> {
						game.gestureUtils.tap(arrowLocation.x - 540, arrowLocation.y + 340, "world_right_arrow")
					}
					else -> {
						throw QuestException("Unexpected map name when trying to navigate in Nalhegrande Skydom Page 1: $mapName")
					}
				}
			}
		} else if (nalhegrandePage2Islands.contains(mapName)) {
			// Switch pages if bot is on Page 1.
			if (nalhegrandePage1Islands.contains(currentIsland) && !game.findAndClickButton("world_right_arrow")) {
				throw QuestException("Failed to move to Page 2 of Nalhegrande Skydom.")
			}

			// Move to the expected Island.
			if (!game.findAndClickButton(mapName.lowercase().replace(" ", "_").replace("-", "_"))) {
				val arrowLocation = game.imageUtils.findButton("world_left_arrow") ?: throw Exception("Unable to find the location of the left arrow for the World.")

				when (mapName) {
					"Bestia Island" -> {
						game.gestureUtils.tap(arrowLocation.x + 285, arrowLocation.y + 510, "world_left_arrow")
					}
					"Reiche Island" -> {
						game.gestureUtils.tap(arrowLocation.x + 715, arrowLocation.y + 120, "world_left_arrow")
					}
					"Starke Island" -> {
						game.gestureUtils.tap(arrowLocation.x + 385, arrowLocation.y - 215, "world_left_arrow")
					}
					else -> {
						throw QuestException("Unexpected map name when trying to navigate in Nalhegrande Skydom Page 2: $mapName")
					}
				}
			}
		}

		// Press "Go" on the popup after pressing on the island node.
		if (!game.findAndClickButton("go")) {
			throw QuestException("Failed to enter $mapName as the Go button is missing.")
		}
	}

	/**
	 * Navigates the bot to the specified island inside the Oarlyegrande Skydom.
	 *
	 * @param mapName Name of the expected Island inside the Oarlyegrande Skydom.
	 */
	private fun navigateToOarlyegrandeSkydomIsland(mapName: String) {
		game.printToLog("\n[QUEST] Beginning process to navigate to the island inside the Oarlyegrande Skydom: $mapName...", tag = tag)

		if (oarlyegrandePage1Islands.contains(mapName)) {
			// Move to the expected Island.
			if (!game.findAndClickButton(mapName.lowercase().replace(" ", "_").replace("-", "_"))) {
				// If the name of the island is obscured, like by the "Next" text indicating that the user's next quest is there, fallback to the manual method.
				val skydomLocation = game.imageUtils.findButton("world_skydom") ?: throw Exception("Unable to find the location of the Skydom button for the World.")

				when (mapName) {
					"New Utopia" -> {
						game.gestureUtils.tap(skydomLocation.x - 445, skydomLocation.y + 405, "world_skydom")
					}
					else -> {
						throw QuestException("Unexpected map name when trying to navigate in Oarlyegrande Skydom: $mapName")
					}
				}
			}
		}

		// Press "Go" on the popup after pressing on the island node.
		if (!game.findAndClickButton("go_oarlyegrande")) {
			throw QuestException("Failed to enter $mapName as the Go button is missing.")
		}
	}

	/**
	 * Selects the required episode using the mission's name.
	 *
	 */
	private fun selectEpisode() {
		if (missionName.contains("Episode")) {
			when {
				missionName.contains("Episode 1") -> {
					game.findAndClickButton("episode_1")
				}
				missionName.contains("Episode 2") -> {
					game.findAndClickButton("episode_2")
				}
				missionName.contains("Episode 3") -> {
					game.findAndClickButton("episode_3")
				}
				missionName.contains("Episode 4") -> {
					game.findAndClickButton("episode_4")
				}
			}

			game.findAndClickButton("ok")
		}
	}

	/**
	 * Selects the Phantagrande chapter node for the mission.
	 *
	 */
	private fun selectPhantagrandeChapterNode() {
		// Grab the location of the "World" button.
		var worldButtonLocation = game.imageUtils.findButton("world", tries = 5)
		if (worldButtonLocation == null) {
			worldButtonLocation = game.imageUtils.findButton("world2", tries = 5)!!
		}

		if (missionName == "Scattered Cargo") {
			game.printToLog("\n[QUEST] Moving to Chapter 1 (115) node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 6 (122) node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 8 node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 9 node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 13 (39/52) node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 17 node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 22 node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 25 node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 30 (44/65) node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 36 (123) node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 70 node...", tag = tag)
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
			game.printToLog("\n[QUEST] Moving to Chapter 55 node...", tag = tag)
			if (!game.imageUtils.isTablet) {
				game.gestureUtils.tap(worldButtonLocation.x + 350, worldButtonLocation.y + 320, "template_node")
			} else {
				if (!game.imageUtils.isLandscape) {
					game.gestureUtils.tap(worldButtonLocation.x + 260, worldButtonLocation.y + 245, "template_node")
				} else {
					game.gestureUtils.tap(worldButtonLocation.x + 200, worldButtonLocation.y + 195, "template_node")
				}
			}
		} else if (missionName == "Rocket Raid") {
			game.printToLog("\n[QUEST] Moving to Chapter 59 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 155, worldButtonLocation.y + 185, "template_node")
		}

		else {
			throw QuestException("Selected mission of $missionName does not exist.")
		}
	}

	/**
	 * Selects the Nalhegrande chapter node for the mission.
	 *
	 */
	private fun selectNalhegrandeChapterNode() {
		// Grab the location of the "World" button.
		var worldButtonLocation = game.imageUtils.findButton("world", tries = 5)
		if (worldButtonLocation == null) {
			worldButtonLocation = game.imageUtils.findButton("world2", tries = 5)!!
		}

		if (missionName == "Stocking Up for Winter") {
			game.printToLog("\n[QUEST] Moving to Chapter 80 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 35, worldButtonLocation.y + 150, "template_node")
		} else if (missionName == "The Mysterious Room") {
			game.printToLog("\n[QUEST] Moving to Chapter 81 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 460, worldButtonLocation.y + 105, "template_node")
		} else if (missionName == "The Right of Might" || missionName == "Idelva Kingdom Episode 4") {
			game.printToLog("\n[QUEST] Moving to Chapter 124 node...", tag = tag)
			game.findAndClickButton("arcarum_sandbox_right_arrow", tries = 1, suppressError = true)
			game.gestureUtils.tap(worldButtonLocation.x + 540, worldButtonLocation.y + 110, "template_node")
		} else if (missionName == "Pholia the Maiden Episode 1" || missionName == "Pholia the Maiden Episode 3") {
			game.printToLog("\n[QUEST] Moving to Chapter 85 node...", tag = tag)
			game.findAndClickButton("arcarum_sandbox_right_arrow", tries = 1, suppressError = true)
			game.gestureUtils.tap(worldButtonLocation.x + 370, worldButtonLocation.y + 290, "template_node")
		} else if (missionName == "Teachings of the Sage Episode 2") {
			game.printToLog("\n[QUEST] Moving to Chapter 89 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 755, worldButtonLocation.y + 150, "template_node")
		} else if (missionName == "Isle of Primals Episode 3") {
			game.printToLog("\n[QUEST] Moving to Chapter 129 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 500, worldButtonLocation.y + 305, "template_node")
		} else if (missionName == "Deception's Inception Episode 4") {
			game.printToLog("\n[QUEST] Moving to Chapter 100 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 645, worldButtonLocation.y + 155, "template_node")
		} else if (missionName == "Be ALl That You Can Be") {
			game.printToLog("\n[QUEST] Moving to Chapter 102 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 30, worldButtonLocation.y + 180, "template_node")
		} else if (missionName == "Once Lost, Once Found") {
			game.printToLog("\n[QUEST] Moving to Chapter 108 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 665, worldButtonLocation.y + 160, "template_node")
		} else if (missionName == "A Girl Named Mika Episode 2") {
			game.printToLog("\n[QUEST] Moving to Chapter 113 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 405, worldButtonLocation.y + 150, "template_node")
		} else {
			throw QuestException("Selected mission of $missionName does not exist.")
		}
	}

	/**
	 * Selects the Oarlyegrande chapter node for the mission.
	 *
	 */
	private fun selectOarlyegrandeChapterNode() {
		// Grab the location of the "World" button.
		var worldButtonLocation = game.imageUtils.findButton("world", tries = 5)
		if (worldButtonLocation == null) {
			worldButtonLocation = game.imageUtils.findButton("world2", tries = 5)!!
		}

		if (missionName == "House of Happiness") {
			game.printToLog("\n[QUEST] Moving to Chapter 132 node...", tag = tag)
			game.gestureUtils.tap(worldButtonLocation.x + 350, worldButtonLocation.y + 300, "template_node")
		} else {
			throw QuestException("Selected mission of $missionName does not exist.")
		}
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
		val currentSkydom: String
		var currentIsland = ""

		// Determine target skydom.
		val targetSkydom: String = if (phantagrandePage1Islands.contains(mapName) || phantagrandePage2Islands.contains(mapName)) {
			"Phantagrande Skydom"
		} else if (nalhegrandePage1Islands.contains(mapName) || nalhegrandePage2Islands.contains(mapName)) {
			"Nalhegrande Skydom"
		} else if (oarlyegrandePage1Islands.contains(mapName)) {
			"Oarlyegrande Skydom"
		} else {
			throw QuestException("Invalid Skydom associated with map in settings.")
		}

		// Check which island the bot is at.
		if (game.imageUtils.confirmLocation("map_$formattedMapName", tries = 1)) {
			game.printToLog("[QUEST] Bot is currently on the correct island.", tag = tag)
			checkLocation = true
			currentSkydom = targetSkydom
		} else {
			game.printToLog("[QUEST] Bot is currently not on the current island.", tag = tag)
			checkLocation = false

			val locationList = phantagrandePage1Islands + phantagrandePage2Islands + nalhegrandePage1Islands + nalhegrandePage2Islands + oarlyegrandePage1Islands

			// Determine current island.
			var locationIndex = 0
			while (locationIndex < locationList.size) {
				val tempMapLocation: String = locationList[locationIndex]

				val tempFormattedMapLocation = tempMapLocation.lowercase().replace(" ", "").replace("-", "_")
				if (game.imageUtils.confirmLocation("map_$tempFormattedMapLocation", tries = 1)) {
					game.printToLog("[QUEST] Bot's current location is at $tempMapLocation. Now moving to $mapName...", tag = tag)
					currentIsland = tempMapLocation
					break
				}

				locationIndex += 1
			}

			// Now determine current skydom.
			currentSkydom = if (phantagrandePage1Islands.contains(currentIsland) || phantagrandePage2Islands.contains(currentIsland)) {
				"Phantagrande Skydom"
			} else if (nalhegrandePage1Islands.contains(currentIsland) || nalhegrandePage2Islands.contains(currentIsland)) {
				"Nalhegrande Skydom"
			} else if (oarlyegrandePage1Islands.contains(currentIsland)) {
				"Oarlyegrande Skydom"
			} else {
				throw QuestException("Current island does not fit into any of the skydoms defined.")
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

				game.wait(3.0)

				// If current skydom is different from the target skydom, move to the target skydom.
				if (currentSkydom != targetSkydom) {
					exitSkydom(currentSkydom)
					enterSkydom(targetSkydom)
				}
			}

			// From the World page, move to the target island and then select the target chapter node.
			if (phantagrandePage1Islands.contains(currentIsland) || phantagrandePage2Islands.contains(currentIsland)) {
				navigateToPhantagrandeSkydomIsland(mapName, currentIsland)
				selectPhantagrandeChapterNode()
			} else if (nalhegrandePage1Islands.contains(currentIsland) || nalhegrandePage2Islands.contains(currentIsland)) {
				navigateToNalhegrandeSkydomIsland(mapName, currentIsland)
				selectNalhegrandeChapterNode()
			} else if (oarlyegrandePage1Islands.contains(currentIsland)) {
				navigateToOarlyegrandeSkydomIsland(mapName)
				selectOarlyegrandeChapterNode()
			}

			// After being on the correct chapter node, scroll down the screen as far as possible and then click the mission to start.
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

			// Apply special navigation for Episode missions.
			selectEpisode()
		} else {
			throw QuestException("Failed to arrive at the Quest page.")
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

				game.wait(1.0)

				// Close the "Items Picked Up" popup.
				if (game.imageUtils.confirmLocation("items_picked_up")) {
					game.findAndClickButton("ok")
				}

				// Now start Combat Mode and detect any item drops.
				if (game.combatMode.startCombatMode()) {
					game.collectLoot(isCompleted = true)
				}
			}
		} else {
			throw QuestException("Failed to arrive at the Summon Selection screen.")
		}

		return
	}
}