package com.steve1316.granblueautomation_android.bot

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.steve1316.granblueautomation_android.MyAccessibilityService
import com.steve1316.granblueautomation_android.ui.settings.SettingsFragment
import com.steve1316.granblueautomation_android.utils.ImageUtils
import com.steve1316.granblueautomation_android.utils.MediaProjectionService
import com.steve1316.granblueautomation_android.data.SummonData
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.opencv.core.Point
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

/**
 * Main driver for bot activity and navigation for the web browser game, Granblue Fantasy.
 *
 * TODO: Make sure that in the constructor that you read in all of the preferences that the user set in the settings for Farming Mode.
 */
class Game(myContext: Context) {
	private val TAG: String = "GAA_Game"
	val imageUtils: ImageUtils = ImageUtils(myContext, this)
	val gestureUtils: MyAccessibilityService = MyAccessibilityService.getInstance()
	private val mapSelection: MapSelection = MapSelection(this)
	private val combatMode: CombatMode = CombatMode()
	
	companion object {
		private val startTime: Long = System.currentTimeMillis()
		private var messageLog: ArrayList<String> = arrayListOf()
		
		private var farmingMode: String = ""
		private var mapName: String = ""
		private var missionName: String = ""
		private var itemName: String = ""
		private var itemAmount: Int = 0
		private var itemAmountFarmed: Int = 0
		private var combatScriptName: String = ""
		private var combatScript: List<String> = arrayListOf()
		private var summonList: List<String> = arrayListOf()
		private var groupNumber: Int = 0
		private var partyNumber: Int = 0
		
		private var coopFirstRun: Boolean = true
	}
	
	/**
	 * Returns a formatted string of the elapsed time since the bot started as HH:MM:SS format.
	 *
	 * Source is from https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format/9027379
	 *
	 * @return String of HH:MM:SS format of the elapsed time.
	 */
	private fun printTime(): String {
		val elapsedMillis: Long = System.currentTimeMillis() - startTime
		
		return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(elapsedMillis), TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(elapsedMillis)), TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) - TimeUnit
			.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)))
	}
	
	/**
	 * Print the specified message to debug console and then saves the message to the log.
	 *
	 * @param message Message to be saved.
	 * @param MESSAGE_TAG TAG to distinguish between messages for where they came from. Defaults to Game's TAG.
	 */
	fun printToLog(message: String, MESSAGE_TAG: String = TAG) {
		Log.d(MESSAGE_TAG, message)
		messageLog.add(printTime() + " " + message)
	}
	
	/**
	 * Go back to the Home screen by tapping the "Home" button.
	 *
	 * @param confirmLocationCheck Whether or not the bot should confirm that it has arrived at the Home screen.
	 * @param displayInfoCheck Whether or not it should print display info into the log.
	 */
	fun goBackHome(confirmLocationCheck: Boolean = false, displayInfoCheck: Boolean = false) {
		if(!imageUtils.confirmLocation("home")) {
			printToLog("[INFO] Moving back to the Home screen...")
			findAndClickButton("home")
		} else {
			printToLog("[INFO] Bot is already at the Home screen.")
		}
		
		if(displayInfoCheck) {
			printToLog("Screen Width: ${MediaProjectionService.displayWidth}, Screen Height: ${MediaProjectionService.displayHeight}, " +
					"Screen DPI: ${MediaProjectionService.displayDPI}")
		}
		
		if(confirmLocationCheck) {
			imageUtils.confirmLocation("home")
		}
	}
	
	/**
	 * Wait the specified seconds to account for ping or loading.
	 *
	 * @param seconds Number of seconds to pause execution.
	 */
	fun wait(seconds: Double) {
		runBlocking {
			delay((seconds * 1000).toLong())
		}
	}
	
	/**
	 * Find and click button
	 *
	 * @param buttonName Name of the button image file in the /assets/buttons/ folder.
	 * @param tries Number of tries to find the specified button.
	 * @param suppressError Whether or not to suppress saving error messages to the log in failing to find the button.
	 * @return True if the button was found and clicked. False otherwise.
	 */
	fun findAndClickButton(buttonName: String, tries: Int = 2, suppressError: Boolean = false): Boolean {
		Log.d(TAG, "[DEBUG] Now attempting to find and click the ${buttonName.toUpperCase(Locale.ROOT)} button.")
		var tempLocation: Point?
		
		if(buttonName.toLowerCase(Locale.ROOT) == "quest") {
			tempLocation = imageUtils.findButton("quest_blue", tries = 1, suppressError = suppressError)
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("quest_red", tries = 1, suppressError = suppressError)
			}
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("quest_blue_strike_time", tries = 1, suppressError = suppressError)
			}
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("quest_red_strike_time", tries = 1, suppressError = suppressError)
			}
		} else if(buttonName.toLowerCase(Locale.ROOT) == "raid") {
			tempLocation = imageUtils.findButton("raid_flat", tries = tries, suppressError = suppressError)
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("raid_bouncing", tries = tries, suppressError = suppressError)
			}
		} else if(buttonName.toLowerCase(Locale.ROOT) == "coop") {
			tempLocation = imageUtils.findButton("coop_start_flat", tries = tries, suppressError = suppressError)
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("coop_start_faded", tries = tries, suppressError = suppressError)
			}
		} else if(buttonName.toLowerCase(Locale.ROOT) == "event_special_quest") {
			tempLocation = imageUtils.findButton("event_special_quest_flat", tries = tries, suppressError = suppressError)
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("event_special_quest_bouncing", tries = tries, suppressError = suppressError)
			}
		} else if(buttonName.toLowerCase(Locale.ROOT) == "world") {
			tempLocation = imageUtils.findButton("world", tries = tries, suppressError = suppressError)
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("world2", tries = tries, suppressError = suppressError)
			}
		} else {
			tempLocation = imageUtils.findButton(buttonName, tries = tries, suppressError = suppressError)
		}
		
		return if(tempLocation != null) {
			gestureUtils.tap(tempLocation.x, tempLocation.y)
		} else {
			false
		}
	}
	
	/**
	 * Checks for CAPTCHA right after selecting a Summon. If detected, alert the user and stop the bot.
	 */
	fun checkForCAPTCHA() {
		TODO("not yet implemented")
	}
	
	/**
	 * Find and select the specified Summon based on the current index on the Summon Selection screen. It will then check for CAPTCHA right
	 * afterwards.
	 *
	 * @return True if the Summon was found and selected. False otherwise.
	 */
	private fun selectSummon(): Boolean {
		// Format the Summon strings.
		val newSummonList = mutableListOf<String>()
		summonList.forEach {
			val newSummonName = it.toLowerCase(Locale.ROOT).replace(" ", "_")
			Log.d(TAG, newSummonName)
			newSummonList.add(newSummonName)
		}
		
		// Set up the list of Summon elements.
		val summonElementList = arrayListOf<String>()
		summonList.forEach {
			when {
				SummonData.fireSummons.contains(it) -> {
					summonElementList.add("fire")
				}
				SummonData.waterSummons.contains(it) -> {
					summonElementList.add("water")
				}
				SummonData.earthSummons.contains(it) -> {
					summonElementList.add("earth")
				}
				SummonData.windSummons.contains(it) -> {
					summonElementList.add("wind")
				}
				SummonData.lightSummons.contains(it) -> {
					summonElementList.add("light")
				}
				SummonData.darkSummons.contains(it) -> {
					summonElementList.add("dark")
				}
				SummonData.miscSummons.contains(it) -> {
					summonElementList.add("misc")
				}
			}
		}
		
		printToLog("Summon list: $newSummonList")
		printToLog("Summon Element list: $summonElementList")
		
		// Find the location of one of the Summons.
		val summonLocation = imageUtils.findSummon(newSummonList, summonElementList)
		
		return if(summonLocation != null) {
			// Select the Summon.
			gestureUtils.tap(summonLocation.x, summonLocation.y)
			
			// Check for CAPTCHA.
			//checkForCAPTCHA()
			
			true
		} else {
			// Reset Summons if not found.
			resetSummons()
			
			false
		}
	}
	
	/**
	 * Reset the available Summons by starting and then retreating from an Old Lignoid Trial Battle.
	 */
	private fun resetSummons() {
		printToLog("[INFO] Resetting Summons...")
		
		// Go back Home.
		goBackHome(confirmLocationCheck = true)
		
		// Scroll the screen down to attempt to see the "Gameplay Extras" button.
		gestureUtils.swipe(500f, 1000f, 500f, 400f)
		
		try {
			val listOfSteps: ArrayList<String> = arrayListOf("gameplay_extras", "trial_battles", "trial_battles_old_lignoid", "play_round_button",
				"choose_a_summon", "ok", "close", "menu", "retreat", "retreat_confirmation", "next")
			
			listOfSteps.forEach {
				if(it == "trial_battles_old_lignoid") {
					// Make sure to confirm that the bot arrived at the Trial Battles screen.
					wait(2.0)
					imageUtils.confirmLocation("trial_battles")
				}
				
				if(it == "close") {
					// Wait a few seconds and then confirm its location.
					wait(5.0)
					imageUtils.confirmLocation("trial_battles_description")
				}
				
				var imageLocation: Point? = imageUtils.findButton(it)
				
				while((it == "gameplay_extras" || it == "trial_battles") && imageLocation == null) {
					// Keep swiping the screen down until the bot finds the specified button.
					gestureUtils.swipe(500f, 1500f, 500f, 500f)
					wait(1.0)
					imageLocation = imageUtils.findButton(it, tries = 1)
				}
				
				if(it == "choose_a_summon" && imageLocation != null) {
					gestureUtils.tap(imageLocation.x, imageLocation.y + 400)
				} else if(it != "choose_a_summon" && imageLocation != null) {
					gestureUtils.tap(imageLocation.x, imageLocation.y)
				}
				
				wait(1.0)
			}
		} catch(e: Exception) {
			printToLog("[ERROR] Bot encountered exception while resetting Summons: ${e.printStackTrace()}")
		}
	}
	
	/**
	 * Selects the specified Group and Party. It will then start the mission.
	 *
	 * @param tries Number of tries to select a Set before failing. Defaults to 3.
	 * @return True if the mission was successfully started. False otherwise.
	 */
	private fun selectPartyAndStartMission(tries: Int = 3): Boolean {
		var setLocation: Point? = null
		var numberOfTries = tries
		
		// Search for the location of the "Set" button based on the Group number.
		try {
			while(setLocation == null) {
				setLocation = if(groupNumber < 8) {
					imageUtils.findButton("party_set_a", tries = 1)
				} else {
					imageUtils.findButton("party_set_b", tries = 1)
				}
				
				if(setLocation == null) {
					numberOfTries -= 1
					
					if(numberOfTries <= 0) {
						if(groupNumber < 8) {
							throw(Resources.NotFoundException("Could not find Set A."))
						} else {
							throw(Resources.NotFoundException("Could not find Set B."))
						}
					}
					
					// Switch over and search for the other Set.
					setLocation = if(groupNumber < 8) {
						imageUtils.findButton("party_set_b", tries = 1)
					} else {
						imageUtils.findButton("party_set_a", tries = 1)
					}
				}
			}
		} catch(e: Exception) {
			printToLog("[ERROR] Bot encountered exception while selecting A or B Set: ${e.printStackTrace()}")
		}
		
		if(setLocation != null) {
			// Select the Group.
			var equation: Double = if(groupNumber == 1) {
				787.0
			} else {
				787.0 - (140 * (groupNumber - 1))
			}
			
			gestureUtils.tap(setLocation.x - equation, setLocation.y + 140.0)
			wait(1.0)
			
			// Select the Party.
			equation = if(partyNumber == 1) {
				690.0
			} else {
				690.0 - (130 * (partyNumber - 1))
			}
			
			gestureUtils.tap(setLocation.x - equation, setLocation.y + 740.0)
			wait(1.0)
		
			printToLog("[SUCCESS] Selected Group and Party successfully.")
			
			// Start the mission by clicking "OK".
			findAndClickButton("ok")
			wait(2.0)
			
			// Detect if a "This raid battle has already ended" popup appeared.
			if(imageUtils.confirmLocation("raid_just_ended_home_redirect", tries = 1)) {
				printToLog("[WARNING] Raid unfortunately just ended. Backing out now...")
				
				// TODO: Determine whether or not the bot should head back home.
				
				return false
			}
			
			return true
		}
		
		return false
	}
	
	/**
	 * Checks if the user has available AP. If not, then it will refill it.
	 *
	 * @param useFullElixir Will use Full Elixir instead of Half Elixir. Defaults to false.
	 */
	fun checkAP(useFullElixir: Boolean = false) {
		while((farmingMode != "Coop" && !imageUtils.confirmLocation("select_summon", tries = 1)) ||
			(farmingMode == "Coop" && !imageUtils.confirmLocation("coop_without_support_summon", tries = 1))) {
			if(imageUtils.confirmLocation("not_enough_ap", tries = 1)) {
				if(!useFullElixir) {
					printToLog("[INFO] AP ran out! Using Half Elixir...")
					val location = imageUtils.findButton("refill_half_ap")!!
					gestureUtils.tap(location.x, location.y + 370)
				} else {
					printToLog("[INFO] AP ran out! Using Full Elixir...")
					val location = imageUtils.findButton("refill_full_ap")!!
					gestureUtils.tap(location.x, location.y + 370)
				}
				
				wait(1.0)
				
				// Press the "OK" button to confirm the item usage.
				findAndClickButton("ok")
			}
		}
		
		printToLog("[INFO] AP is available.")
	}
	
	/**
	 * Checks if the user has available EP. If not, then it will refill it.
	 *
	 * @param useSoulBalm Will use Soul Balm instead of Soul Berry. Defaults to false.
	 */
	fun checkEP(useSoulBalm: Boolean = false) {
		if(farmingMode == "Raid" && imageUtils.confirmLocation("not_enough_ep", tries = 1)) {
			if(!useSoulBalm) {
				printToLog("[INFO] EP ran out! Using Soul Berry...")
				val location = imageUtils.findButton("refill_half_ep")!!
				gestureUtils.tap(location.x, location.y + 370)
			} else {
				printToLog("[INFO] EP ran out! Using Soul Balm...")
				val location = imageUtils.findButton("refill_full_ep")!!
				gestureUtils.tap(location.x, location.y + 370)
			}
			
			wait(1.0)
			
			// Press the "OK" button to confirm the item usage.
			findAndClickButton("ok")
		}
		
		printToLog("[INFO] EP is available.")
	}
	
	/**
	 * Detect any dropped loot from the Loot Collected screen while clicking away any dialog popups.
	 *
	 * @param isPendingBattle Skip the incrementation of runs attempted if this was a Pending Battle. Defaults to false.
	 * @param isEventNightmare Skip the incrementation of runs attempted if this was a Event Nightmare. Defaults to false.
	 */
	fun collectLoot(isPendingBattle: Boolean = false, isEventNightmare: Boolean = false) {
		TODO("not yet implemented")
	}
	
	/**
	 * Detects any "Friend Request" popups and close them.
	 */
	fun checkFriendRequest() {
		if(imageUtils.confirmLocation("friend_request", tries = 1)) {
			printToLog("[INFO] Detected \"Friend Request\" popup. Closing it now...")
			findAndClickButton("cancel")
		}
	}
	
	/**
	 * Checks for Extreme Plus during Rise of the Beasts and if it appeared and the user enabled it in settings, start it.
	 *
	 * @return True if Extreme Plus was detected and successfully completed. False otherwise.
	 */
	private fun checkROTBExtremePlus(): Boolean {
		TODO("not yet implemented")
	}
	
	/**
	 * Checks for Event Nightmare and if it appeared and the user enabled it in settings, start it.
	 *
	 * @return True if Event Nightmare was detected and successfully completed. False otherwise.
	 */
	private fun checkEventNightmare(): Boolean {
		TODO("not yet implemented")
	}
	
	/**
	 * Checks for Dimensional Halo and if it appeared and the user enabled it in settings, start it.
	 *
	 * @return True if Dimensional Halo was detected and successfully completed. False otherwise.
	 */
	private fun checkDimensionalHalo(): Boolean {
		TODO("not yet implemented")
	}
	
	/**
	 * Start Farming Mode with the provided parameters from the user's choices in the settings.
	 *
	 * @param context: The context for the application.
	 */
	fun startFarmingMode(context: Context) {
		// Grab all necessary information from SharedPreferences.
		farmingMode = SettingsFragment.getStringSharedPreference(context, "farmingMode")
		mapName = SettingsFragment.getStringSharedPreference(context, "mapName")
		missionName = SettingsFragment.getStringSharedPreference(context, "missionName")
		itemName = SettingsFragment.getStringSharedPreference(context, "itemName")
		itemAmount = SettingsFragment.getIntSharedPreference(context, "itemAmount")
		combatScriptName = SettingsFragment.getStringSharedPreference(context, "combatScriptName")
		combatScript = SettingsFragment.getStringSharedPreference(context, "combatScript").split("|")
		summonList = SettingsFragment.getStringSharedPreference(context, "summon").split("|")
		groupNumber = SettingsFragment.getIntSharedPreference(context, "groupNumber")
		partyNumber = SettingsFragment.getIntSharedPreference(context, "partyNumber")
		
		if(itemName != "EXP") {
			printToLog("################################################################################")
			printToLog("################################################################################")
			printToLog("[FARM] Starting Farming Mode for $farmingMode.")
			printToLog("[FARM] Farming ${itemAmount}x $itemName at $missionName.")
			printToLog("################################################################################")
			printToLog("################################################################################")
		} else {
			printToLog("################################################################################")
			printToLog("################################################################################")
			printToLog("[FARM] Starting Farming Mode for $farmingMode.")
			printToLog("[FARM] Doing ${itemAmount}x runs for $itemName at $missionName.")
			printToLog("################################################################################")
			printToLog("################################################################################")
		}
		
		// Parse the difficulty for the chosen Mission.
		var difficulty = ""
		if(farmingMode == "Special" || farmingMode == "Event" || farmingMode == "Event (Token Drawboxes)" || farmingMode == "Rise of the Beasts") {
			when {
				missionName.indexOf("N ") == 0 -> {
					difficulty = "Normal"
				}
				missionName.indexOf("H ") == 0 -> {
					difficulty = "Hard"
				}
				missionName.indexOf("VH ") == 0 -> {
					difficulty = "Very Hard"
				}
				missionName.indexOf("EX ") == 0 -> {
					difficulty = "Extreme"
				}
				missionName.indexOf("IM ") == 0 -> {
					difficulty = "Impossible"
				}
			}
		} else if(farmingMode == "Dread Barrage") {
			when {
				missionName.indexOf("1 Star") == 0 -> {
					difficulty = "1 Star"
				}
				missionName.indexOf("2 Star") == 0 -> {
					difficulty = "2 Star"
				}
				missionName.indexOf("3 Star") == 0 -> {
					difficulty = "3 Star"
				}
				missionName.indexOf("4 Star") == 0 -> {
					difficulty = "4 Star"
				}
				missionName.indexOf("5 Star") == 0 -> {
					difficulty = "5 Star"
				}
			}
		}
		
		// TODO: Perform advanced settings setup for Dimensional Halo, Event Nightmare, ROTB Extreme+, and Dread Barrage Unparalleled Foes.
		
		var startCheckFlag = false
		var summonCheckFlag = false
		
		// Primary workflow loop for Farming Mode.
		while(itemAmountFarmed < itemAmount) {
			printToLog("[INFO] Now selecting the Mission...")
			
			mapSelection.selectMap(farmingMode, mapName, missionName, difficulty)
			
			// Loop and attempt to select a Summon. Reset Summons if necessary.
			while(!summonCheckFlag && farmingMode != "Coop") {
				summonCheckFlag = selectSummon()
				
				// If the return came back as false, that means the Summons were reset.
				if(!summonCheckFlag) {
					if(farmingMode != "Raid") {
						printToLog("[INFO] Selecting Mission again after resetting Summons.")
						mapSelection.selectMap(farmingMode, mapName, missionName, difficulty)
					} else {
						printToLog("[INFO] Joining Raids again after resetting Summons.")
						mapSelection.joinRaid(missionName)
					}
				}
			}
			
			// Perform Party Selection and then start the Mission. Do not perform Party Selection for Coop after the initial setup as starting the
			// Coop Mission again reuses the same Party.
			if(farmingMode != "Coop") {
				startCheckFlag = selectPartyAndStartMission()
			} else {
				if(coopFirstRun) {
					startCheckFlag = selectPartyAndStartMission()
					coopFirstRun = false
					
					// Click the "Start" button to start the Coop Mission.
					findAndClickButton("ccop_start")
				}
				
				printToLog("[INFO] Starting Coop Mission.")
			}
			
			if(startCheckFlag && farmingMode != "Raid") {
				wait(3.0)
				
				// Check for "Items Picked Up" popup that appears after starting a Quest Mission.
				if(farmingMode == "Quest" && imageUtils.confirmLocation("items_picked_up", tries = 1)) {
					printToLog("[INFO] Detected \"Items Picked Up\" popup. Closing it now...")
					findAndClickButton("ok")
				}
				
				// Finally, start Combat Mode. If it ended successfully, detect loot and do it again if necessary.
				if(combatMode.startCombatMode(combatScript)) {
					// TODO: Flesh out the collectLoot().
					//collectLoot()
					
					if(itemAmountFarmed < itemAmount) {
						if(farmingMode != "Coop") {
							if(!findAndClickButton("play_again")) {
								// Clear away any Pending Battles.
								mapSelection.checkPendingBattles(farmingMode)
								
								// Now that Pending Battles have been cleared away, select the Mission again.
								mapSelection.selectMap(farmingMode, mapName, missionName, difficulty)
							}
						} else {
							// Head back to the Coop Room.
							findAndClickButton("coop_room")
							
							wait(1.0)
							
							// Check for "Daily Missions" popup for Coop.
							if(imageUtils.confirmLocation("coop_daily_missions", tries = 1)) {
								findAndClickButton("close")
							}
							
							// Now start the Coop Mission again.
							findAndClickButton("coop_start")
						}
						
						// For every Farming Mode other than Coop, continuously close all popups until the bot reaches the Summon Selection screen.
						while(!imageUtils.confirmLocation("select_summon", tries = 1)) {
							if(farmingMode == "Dread Barrage" && imageUtils.confirmLocation("dread_barrage_unparalleled_foe", tries = 1)) {
								// Find all the locations of the "AP 0" texts underneath each Unparalleled Foe.
								// val ap0Locations = imageUtils.findAll("ap_0")
								
								// Start the Unparalleled Foe that the user specified for in the Settings.
								// TODO: Flesh out this section.
							} else if(farmingMode == "Rise of the Beasts" && imageUtils.confirmLocation("proud_solo_quest", tries = 1)) {
								// Scroll the screen down a little bit to see the "Close" button for the "Proud Solo Quest" popup for ROTB.
								gestureUtils.swipe(500f, 1000f, 500f, 400f)
							} else if(farmingMode == "Rise of the Beasts" && checkROTBExtremePlus()) {
								// Make sure that the bot goes back to the Home screen when completing an Extreme+.
								mapSelection.selectMap(farmingMode, mapName, missionName, difficulty)
							} else if((farmingMode == "Event" || farmingMode == "Event (Token Drawboxes)") && checkEventNightmare()) {
								// Make sure that the bot goes back to the Home screen when completing an Event Nightmare.
								mapSelection.selectMap(farmingMode, mapName, missionName, difficulty)
							} else if(farmingMode == "Event (Token Drawboxes)" && imageUtils.confirmLocation("not_enough_treasure", tries = 1)) {
								// Host a Very Hard Raid if the bot lacked the Treasures to host an Extreme/Impossible Raid.
								printToLog("[INFO] Bot ran out of Treasures to host this Mission! Falling back to Very Hard Raid.")
								findAndClickButton("ok")
								mapSelection.selectMap(farmingMode, mapName, missionName, difficulty)
							} else if(farmingMode == "Special" && checkDimensionalHalo()) {
								// Make sure that the bot goes back to the Home screen when completing an Dimensional Halo.
								mapSelection.selectMap(farmingMode, mapName, missionName, difficulty)
							}
							
							else {
								findAndClickButton("cancel", tries = 1, suppressError = true)
								wait(1.0)
								findAndClickButton("close", tries = 1, suppressError = true)
							}
							
							wait(1.0)
							
							// Check for available AP.
							checkAP()
							
							wait(1.0)
						}
					}
				} else {
					// Restart the Mission if the Party wiped or exited prematurely during Combat Mode.
					printToLog("[INFO] Restarting the Mission due to retreating...")
					mapSelection.selectMap(farmingMode, mapName, missionName, difficulty)
				}
			} else if(startCheckFlag && farmingMode == "Raid") {
				// Cover the occasional case where joining the Raid after selecting the Summon and Party led to the Quest Results screen with no
				// loot to collect.
				if(imageUtils.confirmLocation("no_loot", tries = 1)) {
					printToLog("[INFO] Seems that the Raid just ended. Moving back to the Home screen and joining another Raid...")
					goBackHome(confirmLocationCheck = true)
					summonCheckFlag = false
				} else {
					// At this point, the Summon and Party have already been selected and the Mission has started. Start Combat Mode.
					if(combatMode.startCombatMode(combatScript)) {
						// TODO: Flesh out the collectLoot().
						//collectLoot()
						
						if(itemAmountFarmed < itemAmount) {
							// Clear away any Pending Battles.
							mapSelection.checkPendingBattles(farmingMode)
							
							// Now join a new Raid.
							mapSelection.joinRaid(missionName)
						}
					}
				}
			} else if(!startCheckFlag && farmingMode == "Raid") {
				// If the bot reaches here, that means that the Raid ended before the bot could start the Mission after selecting the Summon and
				// Party.
				printToLog("[INFO] Seems that the Raid ended before the bot was able to join. Now looking for another Raid to join...")
				mapSelection.joinRaid(missionName)
				summonCheckFlag = false
			}
		}
	}
}