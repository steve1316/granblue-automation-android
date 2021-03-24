package com.steve1316.granblueautomation_android.bot

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.steve1316.granblueautomation_android.MyAccessibilityService
import com.steve1316.granblueautomation_android.ui.settings.SettingsFragment
import com.steve1316.granblueautomation_android.utils.ImageUtils
import com.steve1316.granblueautomation_android.utils.MediaProjectionService
import com.steve1316.granblueautomation_android.utils.SummonData
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
	var imageUtils = ImageUtils(myContext, this)
	val gestureUtils = MyAccessibilityService.getInstance()
	
	companion object {
		val startTime: Long = System.currentTimeMillis()
		var messageLog: ArrayList<String> = arrayListOf()
		
		var farmingMode: String = ""
		var missionName: String = ""
		var itemName: String = ""
		var itemAmount: String = ""
		var combatScriptName: String = ""
		var combatScript: List<String> = arrayListOf()
		var summonList: List<String> = arrayListOf()
		var groupNumber: Int = 0
		var partyNumber: Int = 0
	}
	
	/**
	 * Returns a formatted string of the elapsed time since the bot started as HH:MM:SS format.
	 *
	 * Source is from https://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format/9027379
	 *
	 * @return String of HH:MM:SS format of the elapsed time.
	 */
	fun printTime(): String {
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
		
		if(tempLocation != null) {
			return gestureUtils.tap(tempLocation.x, tempLocation.y)
		} else {
			return false
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
			if(SummonData.fireSummons.contains(it)) {
				summonElementList.add("fire")
			} else if(SummonData.waterSummons.contains(it)) {
				summonElementList.add("water")
			} else if(SummonData.earthSummons.contains(it)) {
				summonElementList.add("earth")
			} else if(SummonData.windSummons.contains(it)) {
				summonElementList.add("wind")
			} else if(SummonData.lightSummons.contains(it)) {
				summonElementList.add("light")
			} else if(SummonData.darkSummons.contains(it)) {
				summonElementList.add("dark")
			} else if(SummonData.miscSummons.contains(it)) {
				summonElementList.add("misc")
			}
		}
		
		printToLog("Summon list: $newSummonList")
		printToLog("Summon Element list: $summonElementList")
		
		// Find the location of one of the Summons.
		val summonLocation = imageUtils.findSummon(newSummonList, summonElementList)
		
		if(summonLocation != null) {
			// Select the Summon.
			gestureUtils.tap(summonLocation.x, summonLocation.y)
			
			// Check for CAPTCHA.
			//checkForCAPTCHA()
			
			return true
		} else {
			// Reset Summons if not found.
			resetSummons()
			
			return false
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
				if(groupNumber < 8) {
					setLocation = imageUtils.findButton("party_set_a", tries = 1)
				} else {
					setLocation = imageUtils.findButton("party_set_b", tries = 1)
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
					if(groupNumber < 8) {
						setLocation = imageUtils.findButton("party_set_b", tries = 1)
					} else {
						setLocation = imageUtils.findButton("party_set_a", tries = 1)
					}
				}
			}
		} catch(e: Exception) {
			printToLog("[ERROR] Bot encountered exception while selecting A or B Set: ${e.printStackTrace()}")
		}
		
		if(setLocation != null) {
			// Select the Group.
			var equation: Double
			if(groupNumber == 1) {
				equation = 787.0
			} else {
				equation = 787.0 - (140 * (groupNumber - 1))
			}
			
			gestureUtils.tap(setLocation.x - equation, setLocation.y + 140.0)
			wait(1.0)
			
			// Select the Party.
			if(partyNumber == 1) {
				equation = 690.0
			} else {
				equation = 690.0 - (130 * (partyNumber - 1))
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
		missionName = SettingsFragment.getStringSharedPreference(context, "missionName")
		itemName = SettingsFragment.getStringSharedPreference(context, "itemName")
		itemAmount = SettingsFragment.getStringSharedPreference(context, "itemAmount")
		combatScriptName = SettingsFragment.getStringSharedPreference(context, "combatScriptName")
		combatScript = SettingsFragment.getStringSharedPreference(context, "combatScript").split("|")
		summonList = SettingsFragment.getStringSharedPreference(context, "summon").split("|")
		groupNumber = SettingsFragment.getIntSharedPreference(context, "groupNumber")
		partyNumber = SettingsFragment.getIntSharedPreference(context, "partyNumber")

		selectSummon()
		selectPartyAndStartMission()
	}
}