package com.steve1316.granblue_automation_android.bot.game_modes

import com.steve1316.automation_library.data.SharedData
import com.steve1316.automation_library.utils.MessageLog
import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.bot.Game


class GuildWars(private val game: Game, private val missionName: String) {
	private val tag: String = "${loggerTag}GuildWars"

	private class GuildWarsException(message: String) : Exception(message)

	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)

		// Go to the first banner that is usually the current Event by tapping on the "Menu" button.
		game.findAndClickButton("home_menu")
		game.wait(2.0)

		if (SharedData.displayHeight == 1920) {
			MessageLog.printToLog("[GUILD.WARS] Screen too small. Moving the screen down in order to see all of the event banners.", tag)
			game.gestureUtils.swipe(100f, 1000f, 100f, 700f)
			game.wait(0.5)
		}

		var bannerLocations = game.imageUtils.findAll("event_banner")
		if (bannerLocations.size == 0) {
			bannerLocations = game.imageUtils.findAll("event_banner_blue")
		}

		if (game.configData.guildWarsEnableNewPosition) {
			if (game.configData.guildWarsNewPosition > bannerLocations.size - 1) {
				throw GuildWarsException("Value set for New Position was found to be invalid compared to the actual number of events found in the Home Menu.")
			}
			game.gestureUtils.tap(bannerLocations[game.configData.guildWarsNewPosition].x, bannerLocations[game.configData.guildWarsNewPosition].y, "event_banner")
		} else game.gestureUtils.tap(bannerLocations[0].x, bannerLocations[0].y, "event_banner")

		game.wait(3.0)

		if (game.imageUtils.confirmLocation("guild_wars")) {
			// Scroll down the screen a bit.
			game.gestureUtils.swipe(500f, 1000f, 500f, 500f)

			game.wait(2.0)

			val raidBattleLocations = game.imageUtils.findAll("event_raid_battle")

			// Remove the difficulty prefix from the mission name.
			var difficulty = ""
			when (missionName) {
				"Very Hard" -> {
					difficulty = "Very Hard"
				}
				"Extreme" -> {
					difficulty = "Extreme"
				}
				"Extreme+" -> {
					difficulty = "Extreme+"
				}
				"NM90" -> {
					difficulty = "NM90"
				}
				"NM95" -> {
					difficulty = "NM95"
				}
				"NM100" -> {
					difficulty = "NM100"
				}
				"NM150" -> {
					difficulty = "NM150"
				}
			}

			// Perform different navigation actions based on whether the user wants to farm meat or to farm Nightmares.
			if (difficulty == "Very Hard" || difficulty == "Extreme" || difficulty == "Extreme+") {
				MessageLog.printToLog("\n[GUILD.WARS] Now proceeding to farm meat.", tag)

				// Click on the banner to farm meat. Take care of the situation where NM150 is available so it pushes the meat banner down by 1.
				if (raidBattleLocations.size < 3) {
					game.gestureUtils.tap(raidBattleLocations[1].x, raidBattleLocations[1].y, "event_raid_battle")
				} else {
					game.gestureUtils.tap(raidBattleLocations[2].x, raidBattleLocations[2].y, "event_raid_battle")
				}

				game.wait(3.0)

				if (game.imageUtils.confirmLocation("guild_wars_meat")) {
					// Now tap on the specified Mission to start.
					var tries = 10
					MessageLog.printToLog("[GUILD.WARS] Now hosting $difficulty now...", tag)
					val locations = game.imageUtils.findAll("ap_30")

					when (difficulty) {
						"Very Hard" -> {
							game.gestureUtils.tap(locations[0].x, locations[0].y, "ap_30")
						}
						"Extreme" -> {
							game.gestureUtils.tap(locations[1].x, locations[1].y, "ap_30")
						}
						"Extreme+" -> {
							game.gestureUtils.tap(locations[2].x, locations[2].y, "ap_30")
						}
					}

					while (!game.imageUtils.waitVanish("ap_30", timeout = 10)) {
						when (difficulty) {
							"Very Hard" -> {
								game.gestureUtils.tap(locations[0].x, locations[0].y, "ap_30")
							}
							"Extreme" -> {
								game.gestureUtils.tap(locations[1].x, locations[1].y, "ap_30")
							}
							"Extreme+" -> {
								game.gestureUtils.tap(locations[2].x, locations[2].y, "ap_30")
							}
						}

						game.wait(3.0)
						tries -= 1
						if (tries <= 0) {
							if (difficulty == "Extreme+") {
								throw GuildWarsException("You did not unlock Extreme+ yet!")
							} else {
								throw GuildWarsException("There appears to be a deadzone issue that the bot failed 10 times to resolve. Please refresh the page and try again.")
							}
						}
					}
				} else {
					throw GuildWarsException("Failed to open component to host Meat raids in the Guild Wars page.")
				}
			} else {
				MessageLog.printToLog("\n[GUILD.WARS] Now proceeding to farm Nightmares.", tag)

				var startCheckForNM150 = false

				// Click on the banner to farm Nightmares.
				if (difficulty != "NM150") {
					if (raidBattleLocations.size < 3) {
						game.gestureUtils.tap(raidBattleLocations[0].x, raidBattleLocations[0].y, "event_raid_battle")
					} else {
						game.gestureUtils.tap(raidBattleLocations[1].x, raidBattleLocations[1].y, "event_raid_battle")
					}
				} else {
					MessageLog.printToLog("Hosting NM150 now.", tag)
					if (raidBattleLocations.size >= 3) {
						game.gestureUtils.tap(raidBattleLocations[0].x, raidBattleLocations[0].y, "event_raid_battle")

						if (game.imageUtils.confirmLocation("guild_wars_nightmare") && game.findAndClickButton("start")) {
							startCheckForNM150 = true
						}
					}
				}

				if (difficulty != "NM150" && game.imageUtils.confirmLocation("guild_wars_nightmare")) {
					// If today is the first day of Guild Wars, only NM90 is available.
					when {
						game.imageUtils.confirmLocation("guild_wars_nightmare_first_day") -> {
							MessageLog.printToLog("[GUILD.WARS] Today is the first day so hosting NM90.", tag)
							game.findAndClickButton("ok")
						}

						// Now click on the specified Mission to start.
						difficulty == "NM90" -> {
							MessageLog.printToLog("Hosting NM90 now.", tag)
							game.findAndClickButton("guild_wars_nightmare_90")
						}
						difficulty == "NM95" -> {
							MessageLog.printToLog("Hosting NM95 now.", tag)
							game.findAndClickButton("guild_wars_nightmare_95")
						}
						difficulty == "NM100" -> {
							MessageLog.printToLog("Hosting NM100 now.", tag)
							game.findAndClickButton("guild_wars_nightmare_100")
						}
					}
				} else if (!startCheckForNM150) {
					// If there is not enough meat to host, host Extreme+ instead.
					MessageLog.printToLog("[WARNING] User lacks the meat to host the Nightmare. Farming Extreme+ instead.", tag)
					if (difficulty != "NM150") {
						game.findAndClickButton("close")
					} else {
						game.findAndClickButton("cancel")
					}

					MessageLog.printToLog("[GUILD.WARS] Hosting Extreme+ now.", tag)

					// Click on the banner to farm meat.
					if (raidBattleLocations.size < 3) {
						game.gestureUtils.tap(raidBattleLocations[1].x, raidBattleLocations[1].y, "event_raid_battle")
					} else {
						game.gestureUtils.tap(raidBattleLocations[2].x, raidBattleLocations[2].y, "event_raid_battle")
					}

					if (game.imageUtils.confirmLocation("guild_wars_meat")) {
						MessageLog.printToLog("Hosting Extreme+ now.", tag)
						val meatLocation = game.imageUtils.findButton("guild_wars_meat_very_hard")!!
						game.gestureUtils.tap(meatLocation.x + 300.0, meatLocation.y, "guild_wars_meat_very_hard")
					} else {
						throw GuildWarsException("Failed to open component to host Meat raids in the Guild Wars page due to running out of host materials.")
					}
				}
			}
		} else {
			throw GuildWarsException("Failed to arrive at Guild Wars page.")
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

				// Now start Combat Mode and detect any item drops.
				if (game.combatMode.startCombatMode()) {
					game.collectLoot(isCompleted = true)
				}
			}
		} else {
			throw GuildWarsException("Failed to arrive at the Summon Selection screen.")
		}

		return
	}
}