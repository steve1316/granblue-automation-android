package com.steve1316.granblueautomation_android.bot.game_modes

import android.util.Log
import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.bot.Game
import com.steve1316.granblueautomation_android.data.RoomCodeData
import org.opencv.core.Point

class RaidException(message: String) : Exception(message)

class Raid(private val game: Game) {
	private val tag: String = "${MainActivity.loggerTag}Raid"
	
	private var joinRoomButtonLocation: Point = Point()
	private var roomCodeTextBoxLocation: Point = Point()
	private var numberOfRaidsJoined = 0
	private var firstInitialization = true
	
	/**
	 * Check and updates the number of Raids currently joined.
	 */
	private fun checkJoinedRaids() {
		game.wait(1.0)
		
		val joinedLocations = game.imageUtils.findAll("joined")
		numberOfRaidsJoined = joinedLocations.size
		game.printToLog("\n[RAID] There are currently $numberOfRaidsJoined raids joined.", tag = tag)
	}
	
	private fun clearJoinedRaids() {
		// While the user has passed the limit of 3 Raids currently joined, wait and recheck to see if any finish.
		while (numberOfRaidsJoined >= 3) {
			game.printToLog("[RAID] Detected maximum of 3 raids joined. Waiting 30 seconds to see if any finish.", tag = tag)
			game.wait(30.0)
			
			game.goBackHome(confirmLocationCheck = true)
			game.findAndClickButton("quest")
			
			game.wait(1.0)
			
			if (game.checkPendingBattles()) {
				game.findAndClickButton("quest")
				game.wait(1.0)
			}
			
			game.findAndClickButton("raid")
			checkJoinedRaids()
		}
	}
	
	private fun joinRaid() {
		val recoveryTime = 15.0
		var tries = 10
		var joinSuccessful = false
		
		// Save the locations of the "Join Room" button and the "Room Code" text box.
		if (firstInitialization) {
			joinRoomButtonLocation = game.imageUtils.findButton("join_a_room")!!
			roomCodeTextBoxLocation = if (!game.imageUtils.isTablet) {
				if (game.imageUtils.isLowerEnd) {
					Point(joinRoomButtonLocation.x - 200.0, joinRoomButtonLocation.y)
				} else {
					Point(joinRoomButtonLocation.x - 400.0, joinRoomButtonLocation.y)
				}
			} else {
				if (!game.imageUtils.isLandscape) {
					Point(joinRoomButtonLocation.x - 300.0, joinRoomButtonLocation.y)
				} else {
					Point(joinRoomButtonLocation.x - 250.0, joinRoomButtonLocation.y)
				}
			}
		}
		
		// Loop and try to join a Raid from the parsed list of room codes. If none of the codes worked, wait before trying again.
		while (tries > 0) {
			var roomCodeTries = 10
			while (roomCodeTries > 0) {
				val roomCode = game.twitterRoomFinder.getRoomCode()
				
				if (roomCode != "") {
					Log.d(tag, "Room code: $roomCode")
					
					// Set the room code.
					RoomCodeData.roomCode = roomCode
					
					// Select the "Room Code" text box. The AccessibilityService should pick up that the textbox is a EditText and will paste the
					// room code into it.
					game.gestureUtils.tap(roomCodeTextBoxLocation.x, roomCodeTextBoxLocation.y, "template_room_code_textbox", longPress = true)
					
					// Wait several seconds to allow enough time for MyAccessibilityService to paste the code.
					game.wait(3.0)
					
					// Now tap the "Join Room" button.
					game.gestureUtils.tap(joinRoomButtonLocation.x, joinRoomButtonLocation.y, "join_a_room")
					
					if (!game.findAndClickButton("ok")) {
						// Check for EP.
						game.checkEP()
						
						game.printToLog("[SUCCESS] Joining $roomCode was successful.", tag = tag)
						numberOfRaidsJoined += 1
						joinSuccessful = true
						break
					} else if (!game.checkPendingBattles()) {
						// Clear the text box by reloading the page.
						game.printToLog("[WARNING] $roomCode already ended or invalid.", tag = tag)
						game.findAndClickButton("reload")
						firstInitialization = false
					} else {
						// Move from the Home screen back to the Backup Requests screen after clearing out all the Pending Battles.
						game.findAndClickButton("quest")
						game.findAndClickButton("raid")
						
						game.wait(2.0)
						
						checkJoinedRaids()
						game.findAndClickButton("enter_id")
					}
				}
				
				roomCodeTries -= 1
				game.wait(1.0)
			}
			
			if (joinSuccessful) {
				break
			}
			
			tries -= 1
			game.printToLog("[WARNING] Could not find any valid room codes. \nWaiting $recoveryTime seconds and then trying again with $tries tries left before exiting.", tag = tag)
			game.wait(recoveryTime)
		}
	}
	
	/**
	 * Navigates to the specified mission.
	 */
	private fun navigate() {
		game.printToLog("\n[RAID] Now beginning process to navigate to the mission: ${game.missionName}...", tag = tag)
		
		// Go to the Home screen and then to the Quests screen.
		game.goBackHome(confirmLocationCheck = true)
		game.findAndClickButton("quest")
		
		// Check for the "You retreated from the raid battle" popup.
		if (game.imageUtils.confirmLocation("you_retreated_from_the_raid_battle", tries = 1)) {
			game.findAndClickButton("ok")
		}
		
		if (game.checkPendingBattles()) {
			game.findAndClickButton("quest")
			game.wait(1.0)
		}
		
		// Now go to the Backup Requests screen.
		game.findAndClickButton("raid")
		
		game.wait(2.0)
		
		if (game.imageUtils.confirmLocation("raid")) {
			// Check for any joined Raids.
			checkJoinedRaids()
			clearJoinedRaids()
			
			// Move to the "Enter ID" section of the Backup Requests screen.
			game.printToLog("[RAID] Moving to the \"Enter ID\" section of the Backup Requests screen...", tag = tag)
			if (game.findAndClickButton("enter_id")) {
				joinRaid()
			}
		} else {
			throw RaidException("Failed to reach the Backup Requests screen.")
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
				navigate()
			}
		}
		
		// Check for EP.
		game.checkEP()
		
		// Check if the bot is at the Summon Selection screen.
		if (game.imageUtils.confirmLocation("select_a_summon")) {
			if (game.selectSummon()) {
				// Select the Party.
				game.selectPartyAndStartMission()
				
				game.wait(1.0)
				
				// Handle the rare case where joining the Raid after selecting the Summon and Party led the bot to the Quest Results screen with no loot to collect.
				if (game.imageUtils.confirmLocation("no_loot", tries = 1)) {
					game.printToLog("\n[RAID] Seems that the Raid just ended. Moving back to the Home screen and joining another Raid...", tag = tag)
				} else {
					// Now start Combat Mode and detect any item drops.
					if (game.combatMode.startCombatMode(game.combatScript)) {
						numberOfItemsDropped = game.collectLoot()
					}
				}
			}
		} else {
			throw RaidException("Failed to arrive at the Summon Selection screen.")
		}
		
		return numberOfItemsDropped
	}
}