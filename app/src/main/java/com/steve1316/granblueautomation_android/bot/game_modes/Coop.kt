package com.steve1316.granblueautomation_android.bot.game_modes

import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.bot.Game

class CoopException(message: String) : Exception(message)

class Coop(private val game: Game, private val missionName: String) {
	private val tag: String = "${MainActivity.loggerTag}Coop"
	
	// The 2nd element of the list of EX1 missions is designated "empty" because it is used to navigate properly to "Lost in the Dark" from "Corridor of Puzzles".
	private val listForCoopEX1 = arrayListOf("EX1-1 Corridor of Puzzles", "empty", "EX1-3 Lost in the Dark")
	private val listForCoopEX2 = arrayListOf("EX2-2 Time of Judgement", "EX2-3 Time of Revelation", "EX2-4 Time of Eminence")
	private val listForCoopEX3 = arrayListOf("EX3-2 Rule of the Tundra", "EX3-3 Rule of the Plains", "EX3-4 Rule of the Twilight")
	private val listForCoopEX4 = arrayListOf("EX4-2 Amidst the Waves", "EX4-3 Amidst the Petals", "EX4-4 Amidst Severe Cliffs", "EX4-5 Amidst the Flames")
	
	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		game.printToLog("\n[COOP] Now beginning process to navigate to the mission: $missionName...", tag = tag)
		
		// Go to the Home screen.
		game.goBackHome(confirmLocationCheck = true)
		
		// Tap the "Menu" button at the top right corner of the Home screen and go to Coop.
		game.findAndClickButton("home_menu")
		game.findAndClickButton("coop")
		
		game.wait(3.0)
		
		if (game.imageUtils.confirmLocation("coop")) {
			// Scroll the screen down a little bit.
			game.gestureUtils.swipe(500f, 1000f, 500f, 400f)
			
			game.wait(2.0)
			
			// Find all occurrences of the "Host Quest" button.
			val hostButtonLocations = game.imageUtils.findAll("coop_host_quest")
			
			// Select the difficulty of the mission that it is under.
			if (missionName == "H3-1 In a Dusk Dream") {
				// Check if Hard difficulty is already selected. If not, select it.
				if (!game.findAndClickButton("coop_hard_selected")) {
					game.findAndClickButton("coop_hard")
				}
				
				game.wait(3.0)
				
				game.printToLog("[COOP] Hard difficulty for Coop is now selected.", tag = tag)
				
				// Select the category, "Save the Oceans", which should be the 3rd category.
				game.printToLog("[COOP] Now navigating to \"In a Dusk Dream\" for Hard difficulty...", tag = tag)
				game.gestureUtils.tap(hostButtonLocations[2].x, hostButtonLocations[2].y, "coop_host_quest")
				
				if (game.imageUtils.confirmLocation("coop_save_the_oceans")) {
					val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
					game.gestureUtils.tap(hostRoundButtonLocations[0].x, hostRoundButtonLocations[0].y, "coop_host_quest_circle")
				}
			} else {
				// Check if Extra difficulty is already selected. If not, select it.
				if (!game.findAndClickButton("coop_extra_selected")) {
					game.findAndClickButton("coop_extra")
				}
				
				game.wait(3.0)
				
				game.printToLog("[COOP] Extra difficulty for Coop is now selected.", tag = tag)
				
				// Select the category for the specified EX mission. For EX2 to EX4, skip past the first missions of each.
				if (listForCoopEX1.contains(missionName)) {
					game.printToLog("[COOP] Now navigating to \"$missionName\" for EX1...", tag = tag)
					
					game.gestureUtils.tap(hostButtonLocations[0].x, hostButtonLocations[0].y, "coop_host_quest")
					if (game.imageUtils.confirmLocation("coop_ex1")) {
						game.printToLog("[COOP] Now selecting \"$missionName\"...", tag = tag)
						
						val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
						game.gestureUtils.tap(
							hostRoundButtonLocations[listForCoopEX1.indexOf(missionName)].x,
							hostRoundButtonLocations[listForCoopEX1.indexOf(missionName)].y,
							"coop_host_quest_circle"
						)
					}
				} else if (listForCoopEX2.contains(missionName)) {
					game.printToLog("[COOP] Now navigating to \"$missionName\" for EX2...", tag = tag)
					
					game.gestureUtils.tap(hostButtonLocations[1].x, hostButtonLocations[1].y, "coop_host_quest")
					if (game.imageUtils.confirmLocation("coop_ex2")) {
						game.printToLog("[COOP] Now selecting \"$missionName\"...", tag = tag)
						
						val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
						game.gestureUtils.tap(
							hostRoundButtonLocations[listForCoopEX2.indexOf(missionName) + 1].x,
							hostRoundButtonLocations[listForCoopEX2.indexOf(missionName) + 1].y,
							"coop_host_quest_circle"
						)
					}
				} else if (listForCoopEX3.contains(missionName)) {
					game.printToLog("[COOP] Now navigating to \"$missionName\" for EX3...", tag = tag)
					
					game.gestureUtils.tap(hostButtonLocations[2].x, hostButtonLocations[2].y, "coop_host_quest")
					if (game.imageUtils.confirmLocation("coop_ex3")) {
						game.printToLog("[COOP] Now selecting \"$missionName\"...", tag = tag)
						
						val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
						game.gestureUtils.tap(
							hostRoundButtonLocations[listForCoopEX3.indexOf(missionName) + 1].x,
							hostRoundButtonLocations[listForCoopEX3.indexOf(missionName) + 1].y,
							"coop_host_quest_circle"
						)
					}
				} else if (listForCoopEX4.contains(missionName)) {
					game.printToLog("[COOP] Now navigating to \"$missionName\" for EX4...", tag = tag)
					
					game.gestureUtils.tap(hostButtonLocations[3].x, hostButtonLocations[3].y, "coop_host_quest")
					if (game.imageUtils.confirmLocation("coop_ex4")) {
						game.printToLog("[COOP] Now selecting \"$missionName\"...", tag = tag)
						
						val hostRoundButtonLocations = game.imageUtils.findAll("coop_host_quest_circle")
						game.gestureUtils.tap(
							hostRoundButtonLocations[listForCoopEX4.indexOf(missionName) + 1].x,
							hostRoundButtonLocations[listForCoopEX4.indexOf(missionName) + 1].y,
							"coop_host_quest_circle"
						)
					}
				}
			}
			
			game.wait(3.0)
			
			// After selecting the mission, create a new Coop Room.
			game.printToLog("\n[COOP] Now opening up a new Coop room...", tag = tag)
			game.findAndClickButton("coop_post_to_crew_chat")
			
			// Scroll the screen down to see the "OK" button in case of small screens.
			game.gestureUtils.scroll()
			game.findAndClickButton("coop_ok")
			
			game.wait(3.0)
			
			// Just in case, check for the "You retreated from the raid battle" popup.
			if (game.imageUtils.confirmLocation("you_retreated_from_the_raid_battle", tries = 1)) {
				game.findAndClickButton("ok")
			}
			
			// Scroll the screen down to see the "Select Party" button in case of small screens and then tap the button.
			game.gestureUtils.swipe(500f, 1000f, 500f, 700f)
			game.findAndClickButton("coop_select_party")
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
			else -> {
				// Head back to the Coop Room.
				game.findAndClickButton("coop_room")
				
				game.wait(1.0)
				
				if (game.imageUtils.confirmLocation("coop_daily_missions", tries = 1)) {
					game.printToLog("\n[COOP] Coop room has closed due to time running out.", tag = tag)
					return -1
				}
			}
		}
		
		// Check for AP.
		game.checkAP()

		game.wait(3.0)
		
		if (firstRun && game.imageUtils.confirmLocation("coop_without_support_summon", tries = 10)) {
			// Select the Party.
			game.selectPartyAndStartMission()
			
			// Now tap the "Start" button to start the Coop mission.
			game.findAndClickButton("coop_start")
			
			game.wait(1.0)
			
			// Now start Combat Mode and detect any item drops.
			if (game.combatMode.startCombatMode(game.combatScript)) {
				numberOfItemsDropped = game.collectLoot(isCompleted = true)
			}
		} else if (!firstRun) {
			game.printToLog("\n[COOP] Starting Coop mission again.", tag = tag)
			
			// Now start Combat Mode and detect any item drops.
			if (game.combatMode.startCombatMode(game.combatScript)) {
				numberOfItemsDropped = game.collectLoot(isCompleted = true)
			}
		} else {
			throw CoopException("Failed to arrive at the Summon Selection screen.")
		}
		
		return numberOfItemsDropped
	}
}