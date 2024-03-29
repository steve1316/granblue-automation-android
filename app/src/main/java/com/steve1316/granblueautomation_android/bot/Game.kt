package com.steve1316.granblueautomation_android.bot

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import androidx.preference.PreferenceManager
import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.bot.game_modes.*
import com.steve1316.granblueautomation_android.data.SummonData
import com.steve1316.granblueautomation_android.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.opencv.core.Point
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Main driver for bot activity and navigation for the web browser game, Granblue Fantasy.
 */
class Game(val myContext: Context) {
	private val tag: String = "${MainActivity.loggerTag}Game"
	
	private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)
	
	// Grab all necessary information from SharedPreferences.
	var farmingMode: String = sharedPreferences.getString("farmingMode", "")!!
	private var mapName: String = sharedPreferences.getString("mapName", "")!!
	var missionName: String = sharedPreferences.getString("missionName", "")!!
	var difficulty: String = ""
	var itemName: String = sharedPreferences.getString("itemName", "")!!
	var itemAmount: Int = sharedPreferences.getInt("itemAmount", 1)
	var itemAmountFarmed: Int = 0
	private var amountOfRuns: Int = 0
	var combatScript: List<String> = sharedPreferences.getString("combatScript", "")!!.split("|")
	var summonList: List<String> = sharedPreferences.getString("summon", "")!!.split("|")
	var groupNumber: Int = sharedPreferences.getInt("groupNumber", 1)
	var partyNumber: Int = sharedPreferences.getInt("partyNumber", 1)
	private var enableDelayBetweenRuns: Boolean = sharedPreferences.getBoolean("enableDelayBetweenRuns", false)
	private var delayBetweenRuns: Int = sharedPreferences.getInt("delayBetweenRuns", 1)
	private var enableRandomizedDelayBetweenRuns: Boolean = sharedPreferences.getBoolean("enableRandomizedDelayBetweenRuns", false)
	private var randomizedDelayBetweenRuns: Int = sharedPreferences.getInt("randomizedDelayBetweenRuns", 1)
	private var enableSkipAutoRestore: Boolean = sharedPreferences.getBoolean("enabledSkipAutoRestore", true)
	var debugMode: Boolean = sharedPreferences.getBoolean("debugMode", false)
	
	val imageUtils: ImageUtils = ImageUtils(myContext, this)
	val gestureUtils: MyAccessibilityService = MyAccessibilityService.getInstance()
	var twitterRoomFinder: TwitterRoomFinder = TwitterRoomFinder(myContext, this)
	val combatMode: CombatMode = CombatMode(this, debugMode)
	
	private lateinit var quest: Quest
	private lateinit var special: Special
	private lateinit var coop: Coop
	private lateinit var raid: Raid
	private lateinit var event: Event
	private lateinit var dreadBarrage: DreadBarrage
	private lateinit var riseOfTheBeasts: RiseOfTheBeasts
	private lateinit var guildWars: GuildWars
	private lateinit var provingGrounds: ProvingGrounds
	private lateinit var xenoClash: XenoClash
	private lateinit var arcarum: Arcarum
	
	private val startTime: Long = System.currentTimeMillis()
	
	private var partySelectionFirstRun: Boolean = true
	
	init {
		if (farmingMode == "Quest") {
			quest = Quest(this, mapName, missionName)
		} else if (farmingMode == "Special") {
			special = Special(this, mapName, missionName)
		} else if (farmingMode == "Coop") {
			coop = Coop(this, missionName)
		} else if (farmingMode == "Raid") {
			raid = Raid(this)
		} else if (farmingMode == "Event" || farmingMode == "Event (Token Drawboxes)") {
			event = Event(this, missionName)
		} else if (farmingMode == "Dread Barrage") {
			dreadBarrage = DreadBarrage(this, missionName)
		} else if (farmingMode == "Rise of the Beasts") {
			riseOfTheBeasts = RiseOfTheBeasts(this, missionName)
		} else if (farmingMode == "Guild Wars") {
			guildWars = GuildWars(this, missionName)
		} else if (farmingMode == "Proving Grounds") {
			provingGrounds = ProvingGrounds(this, missionName)
		} else if (farmingMode == "Xeno Clash") {
			xenoClash = XenoClash(this, missionName)
		} else if (farmingMode == "Arcarum") {
			arcarum = Arcarum(this, missionName)
		}
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
		
		return String.format(
			"%02d:%02d:%02d",
			TimeUnit.MILLISECONDS.toHours(elapsedMillis),
			TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(elapsedMillis)),
			TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMillis))
		)
	}
	
	/**
	 * Print the specified message to debug console and then saves the message to the log.
	 *
	 * @param message Message to be saved.
	 * @param tag Tag to distinguish between messages for where they came from. Defaults to Game's tag.
	 * @param isError Flag to determine whether to display log message in console as debug or error.
	 */
	fun printToLog(message: String, tag: String = this.tag, isError: Boolean = false) {
		if (!isError) {
			Log.d(tag, message)
		} else {
			Log.e(tag, message)
		}
		
		// Remove the newline prefix if needed and place it where it should be.
		if (message.startsWith("\n")) {
			val newMessage = message.removePrefix("\n")
			MessageLog.messageLog.add("\n" + printTime() + " " + newMessage)
		} else {
			MessageLog.messageLog.add(printTime() + " " + message)
		}
	}
	
	/**
	 * Go back to the Home screen by tapping the "Home" button.
	 *
	 * @param confirmLocationCheck Whether or not the bot should confirm that it has arrived at the Home screen.
	 * @param testMode Flag to test and get a valid scale for device compatibility.
	 */
	fun goBackHome(confirmLocationCheck: Boolean = false, testMode: Boolean = false) {
		if (!imageUtils.confirmLocation("home")) {
			printToLog("[INFO] Moving back to the Home screen...")
			
			if (!findAndClickButton("home")) {
				if (!testMode) {
					throw Exception("HOME button is not found. Stopping bot to prevent cascade of errors. Please readjust your confidences/scales.")
				} else {
					printToLog("\n[DEBUG] Failed to find the HOME button. Now beginning test to find a valid scale for this device...")
					imageUtils.findButton("home", testMode = true)
					return
				}
			}
		} else {
			printToLog("[INFO] Bot is already at the Home screen.")
		}
		
		printToLog("\n[INFO] Screen Width: ${MediaProjectionService.displayWidth}, Screen Height: ${MediaProjectionService.displayHeight}, Screen DPI: ${MediaProjectionService.displayDPI}")
		
		// Check for any misc popups.
		findAndClickButton("close")
		
		if (confirmLocationCheck) {
			wait(2.0)
			
			if (!imageUtils.confirmLocation("home")) {
				throw Exception("Failed to head back to the Home screen after clicking on the Home button.")
			}
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
		if (debugMode) {
			printToLog("[DEBUG] Now attempting to find and click the \"$buttonName\" button.")
		}
		
		var tempLocation: Point?
		var newButtonName = buttonName
		
		if (buttonName.lowercase() == "quest") {
			tempLocation = imageUtils.findButton("quest_blue", suppressError = suppressError)
			newButtonName = "quest_blue"
			
			if (tempLocation == null) {
				tempLocation = imageUtils.findButton("quest_red", suppressError = suppressError)
				newButtonName = "quest_red"
			}
			
		} else if (buttonName.lowercase() == "raid") {
			tempLocation = imageUtils.findButton("raid_flat", tries = tries, suppressError = suppressError)
			newButtonName = "raid_flat"
			
			if (tempLocation == null) {
				tempLocation = imageUtils.findButton("raid_bouncing", tries = tries, suppressError = suppressError)
				newButtonName = "raid_bouncing"
			}
			
		} else if (buttonName.lowercase() == "coop_start") {
			tempLocation = imageUtils.findButton("coop_start_flat", tries = tries, suppressError = suppressError)
			newButtonName = "coop_start_flat"
			
			if (tempLocation == null) {
				tempLocation = imageUtils.findButton("coop_start_faded", tries = tries, suppressError = suppressError)
				newButtonName = "coop_start_faded"
			}
			
		} else if (buttonName.lowercase() == "event_special_quest") {
			tempLocation = imageUtils.findButton("event_special_quest_flat", tries = tries, suppressError = suppressError)
			newButtonName = "event_special_quest_flat"
			
			if (tempLocation == null) {
				tempLocation = imageUtils.findButton("event_special_quest_bouncing", tries = tries, suppressError = suppressError)
				newButtonName = "event_special_quest_bouncing"
			}
			
		} else if (buttonName.lowercase() == "world") {
			tempLocation = imageUtils.findButton("world", tries = tries, suppressError = suppressError)
			newButtonName = "world"
			
			if (tempLocation == null) {
				tempLocation = imageUtils.findButton("world2", tries = tries, suppressError = suppressError)
				newButtonName = "world2"
			}
			
		} else {
			tempLocation = imageUtils.findButton(buttonName, tries = tries, suppressError = suppressError)
		}
		
		return if (tempLocation != null) {
			gestureUtils.tap(tempLocation.x, tempLocation.y, newButtonName)
		} else {
			false
		}
	}
	
	/**
	 * Checks for CAPTCHA right after selecting a Summon. If detected, alert the user and stop the bot.
	 */
	fun checkForCAPTCHA() {
		if (imageUtils.confirmLocation("captcha", tries = 2)) {
			throw(Exception("[CAPTCHA] CAPTCHA has been detected! Stopping the bot now."))
		} else {
			printToLog("\n[CAPTCHA] CAPTCHA not detected.")
		}
	}
	
	/**
	 * Execute a delay after every run completed based on user settings from config.yaml.
	 */
	private fun delayBetweenRuns() {
		if (enableDelayBetweenRuns) {
			// Check if the provided delay is valid.
			if (delayBetweenRuns < 0) {
				printToLog("\n[INFO] Provided delay in seconds for the resting period is not valid. Defaulting to 15 seconds.")
				delayBetweenRuns = 15
			}
			
			printToLog("\n[INFO] Now waiting for $delayBetweenRuns seconds as the resting period. Please do not navigate from the current screen.")
			
			wait(delayBetweenRuns.toDouble())
		} else if (!enableDelayBetweenRuns && enableRandomizedDelayBetweenRuns) {
			// Check if the lower and upper bounds are valid.
			if (delayBetweenRuns < 0 || delayBetweenRuns > randomizedDelayBetweenRuns) {
				printToLog("\n[INFO] Provided lower bound delay in seconds for the resting period is not valid. Defaulting to 15 seconds.")
				delayBetweenRuns = 15
			}
			
			if (randomizedDelayBetweenRuns < 0 || randomizedDelayBetweenRuns < delayBetweenRuns) {
				printToLog("\n[INFO] Provided upper bound delay in seconds for the resting period is not valid. Defaulting to 60 seconds.")
				randomizedDelayBetweenRuns = 60
			}
			
			val newSeconds = Random().nextInt(randomizedDelayBetweenRuns - delayBetweenRuns) + delayBetweenRuns
			printToLog(
				"\n[INFO] Given the bounds of ($delayBetweenRuns, $randomizedDelayBetweenRuns), bot will now wait for $newSeconds seconds as a resting period. Please do not navigate from the " +
						"current screen."
			)
			
			wait(newSeconds.toDouble())
		}
		
		printToLog("\n[INFO] Resting period complete.")
	}
	
	/**
	 * Find and select the specified Summon based on the current index on the Summon Selection screen. It will then check for CAPTCHA right
	 * afterwards.
	 *
	 * @param optionalSummonList Overrides the Summon list used. Defaults to the ones selected for Farming Mode.
	 * @return True if the Summon was found and selected. False otherwise.
	 */
	fun selectSummon(optionalSummonList: List<String> = arrayListOf()): Boolean {
		// Format the Summon strings.
		val newSummonList = mutableListOf<String>()
		val unformattedSummonList = if (optionalSummonList.isNotEmpty()) {
			optionalSummonList
		} else {
			summonList
		}
		
		unformattedSummonList.forEach {
			val newSummonName = it.lowercase().replace(" ", "_")
			newSummonList.add(newSummonName)
		}
		
		// Set up the list of Summon elements.
		val summonElementList = arrayListOf<String>()
		unformattedSummonList.forEach {
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
		
		return if (summonLocation != null) {
			// Select the Summon.
			gestureUtils.tap(summonLocation.x, summonLocation.y, "template_summon")
			
			// Check for CAPTCHA.
			checkForCAPTCHA()
			
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
		
		val listOfSteps: ArrayList<String> = arrayListOf(
			"gameplay_extras", "trial_battles", "trial_battles_old_lignoid", "play_round_button",
			"choose_a_summon", "ok", "close", "menu", "retreat", "retreat_confirmation", "next"
		)
		
		listOfSteps.forEach {
			if (it == "trial_battles_old_lignoid") {
				// Make sure to confirm that the bot arrived at the Trial Battles screen.
				wait(2.0)
				imageUtils.confirmLocation("trial_battles")
			}
			
			if (it == "close") {
				// Wait a few seconds and then confirm its location.
				wait(5.0)
				imageUtils.confirmLocation("trial_battles_description")
			}
			
			var imageLocation: Point? = imageUtils.findButton(it, tries = 2)
			
			while ((it == "gameplay_extras" || it == "trial_battles") && imageLocation == null) {
				// Keep swiping the screen down until the bot finds the specified button.
				imageLocation = imageUtils.findButton(it, tries = 1)
				if (imageLocation == null) {
					gestureUtils.scroll()
					wait(1.0)
				}
			}
			
			if (it == "choose_a_summon" && imageLocation != null) {
				gestureUtils.tap(imageLocation.x, imageLocation.y + 400, "template_summon")
			} else if (it != "choose_a_summon" && imageLocation != null) {
				gestureUtils.tap(imageLocation.x, imageLocation.y, it)
			}
			
			wait(2.0)
		}
	}
	
	/**
	 * Selects the specified Group and Party. It will then start the mission.
	 *
	 * @param optionalGroupNumber Overrides the Group Number. Defaults to the one selected for Farming Mode.
	 * @param optionalPartyNumber Overrides the Party Number. Defaults to the one selected for Farming Mode.
	 * @param tries Number of tries to select a Set before failing. Defaults to 3.
	 * @return True if the mission was successfully started. False otherwise.
	 */
	fun selectPartyAndStartMission(optionalGroupNumber: Int = 0, optionalPartyNumber: Int = 0, tries: Int = 3): Boolean {
		if (partySelectionFirstRun) {
			var setLocation: Point? = null
			var numberOfTries = tries
			
			val selectedGroupNumber = if (optionalGroupNumber == 0) {
				groupNumber
			} else {
				optionalGroupNumber
			}
			
			val selectedPartyNumber = if (optionalPartyNumber == 0) {
				partyNumber
			} else {
				optionalPartyNumber
			}
			
			// Search for the location of the "Set" button based on the Group number.
			while (setLocation == null) {
				setLocation = if (selectedGroupNumber < 8) {
					imageUtils.findButton("party_set_a", tries = 1)
				} else {
					imageUtils.findButton("party_set_b", tries = 1)
				}
				
				if (setLocation == null) {
					numberOfTries -= 1
					
					if (numberOfTries <= 0) {
						if (selectedGroupNumber < 8) {
							throw(Resources.NotFoundException("Could not find Set A."))
						} else {
							throw(Resources.NotFoundException("Could not find Set B."))
						}
					}
					
					// Switch over and search for the other Set.
					setLocation = if (selectedGroupNumber < 8) {
						imageUtils.findButton("party_set_b", tries = 1)
					} else {
						imageUtils.findButton("party_set_a", tries = 1)
					}
				}
			}
			
			// Select the Group.
			var equation: Double = if (!imageUtils.isTablet) {
				if (selectedGroupNumber == 1) {
					if (imageUtils.isLowerEnd) {
						537.0
					} else {
						787.0
					}
				} else {
					if (imageUtils.isLowerEnd) {
						537.0 - (93 * (selectedGroupNumber - 1))
					} else {
						787.0 - (140 * (selectedGroupNumber - 1))
					}
				}
			} else {
				if (!imageUtils.isLandscape) {
					if (selectedGroupNumber == 1) {
						588.0
					} else {
						588.0 - (100 * (selectedGroupNumber - 1))
					}
				} else {
					if (selectedGroupNumber == 1) {
						467.0
					} else {
						467.0 - (80 * (selectedGroupNumber - 1))
					}
				}
			}
			
			if (!imageUtils.isTablet) {
				if (imageUtils.isLowerEnd) {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 80.0, "template_group")
				} else {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 140.0, "template_group")
				}
			} else {
				if (!imageUtils.isLandscape) {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 90.0, "template_group")
				} else {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 70.0, "template_group")
				}
			}
			
			wait(1.0)
			
			// Select the Party.
			equation = if (!imageUtils.isTablet) {
				if (selectedPartyNumber == 1) {
					if (imageUtils.isLowerEnd) {
						468.0
					} else {
						690.0
					}
				} else {
					if (imageUtils.isLowerEnd) {
						468.0 - (85 * (selectedPartyNumber - 1))
					} else {
						690.0 - (130 * (selectedPartyNumber - 1))
					}
				}
			} else {
				if (!imageUtils.isLandscape) {
					if (selectedPartyNumber == 1) {
						516.0
					} else {
						516.0 - (100 * (selectedPartyNumber - 1))
					}
				} else {
					if (selectedPartyNumber == 1) {
						408.0
					} else {
						408.0 - (75 * (selectedPartyNumber - 1))
					}
				}
			}
			
			if (!imageUtils.isTablet) {
				if (imageUtils.isLowerEnd) {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 490.0, "template_party")
				} else {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 740.0, "template_party")
				}
			} else {
				if (!imageUtils.isLandscape) {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 540.0, "template_party")
				} else {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 425.0, "template_party")
				}
			}
			
			wait(1.0)
			
			partySelectionFirstRun = false
		}
		
		printToLog("[SUCCESS] Selected Group and Party successfully.")
		
		// Start the mission by clicking "OK".
		findAndClickButton("ok")
		wait(2.0)
		
		// Detect if a "This raid battle has already ended" popup appeared.
		if (farmingMode == "Raid" && findAndClickButton("ok")) {
			printToLog("[WARNING] Raid unfortunately just ended. Backing out now...")
			return false
		}
		
		return true
	}
	
	/**
	 * Checks if the user has available AP. If not, then it will refill it.
	 *
	 * @param useFullElixir Will use Full Elixir instead of Half Elixir. Defaults to false.
	 * @param tries Number of tries to try to refill AP. Defaults to 3.
	 */
	fun checkAP(useFullElixir: Boolean = false, tries: Int = 3) {
		if (!enableSkipAutoRestore) {
			var numberOfTries = tries
			
			wait(2.0)
			
			if (!imageUtils.confirmLocation("auto_ap_recovered", tries = 1) && !imageUtils.confirmLocation("auto_ap_recovered2", tries = 1)) {
				while ((farmingMode != "Coop" && !imageUtils.confirmLocation("select_a_summon", tries = 1)) ||
					(farmingMode == "Coop" && !imageUtils.confirmLocation("coop_without_support_summon", tries = 1))
				) {
					if (imageUtils.confirmLocation("not_enough_ap", tries = 1)) {
						val useLocations = imageUtils.findAll("use")
						if (!useFullElixir) {
							printToLog("[INFO] AP ran out! Using Half Elixir...")
							gestureUtils.tap(useLocations[0].x, useLocations[0].y, "use")
						} else {
							printToLog("[INFO] AP ran out! Using Full Elixir...")
							gestureUtils.tap(useLocations[1].x, useLocations[1].y, "use")
						}
						
						wait(1.0)
						
						// Press the "OK" button to confirm the item usage.
						findAndClickButton("ok")
					} else {
						numberOfTries -= 1
						if (numberOfTries <= 0) {
							break
						}
					}
				}
			} else {
				findAndClickButton("ok")
			}
			
			printToLog("[INFO] AP is available.")
		}
		
		printToLog("[INFO] AP was auto-restored.")
	}
	
	/**
	 * Checks if the user has available EP. If not, then it will refill it.
	 *
	 * @param useSoulBalm Will use Soul Balm instead of Soul Berry. Defaults to false.
	 * @param tries Number of tries to try to refill AP. Defaults to 3.
	 */
	fun checkEP(useSoulBalm: Boolean = false, tries: Int = 3) {
		if (!enableSkipAutoRestore) {
			var numberOfTries = tries
			
			wait(2.0)
			
			if (!imageUtils.confirmLocation("auto_ep_recovered", tries = 1)) {
				while (farmingMode == "Raid" && !imageUtils.confirmLocation("select_a_summon", tries = 1)) {
					if (imageUtils.confirmLocation("not_enough_ep", tries = 1)) {
						val useLocations = imageUtils.findAll("use")
						if (!useSoulBalm) {
							printToLog("[INFO] EP ran out! Using Soul Berry...")
							gestureUtils.tap(useLocations[0].x, useLocations[0].y, "use")
						} else {
							printToLog("[INFO] EP ran out! Using Soul Balm...")
							gestureUtils.tap(useLocations[1].x, useLocations[1].y, "use")
						}
						
						wait(1.0)
						
						// Press the "OK" button to confirm the item usage.
						findAndClickButton("ok")
					}
					
					numberOfTries -= 1
					if (numberOfTries <= 0) {
						break
					}
				}
			} else {
				findAndClickButton("ok")
			}
			
			printToLog("[INFO] EP is available.")
		}
		
		printToLog("[INFO] EP was auto-restored.")
	}
	
	/**
	 * Detect any dropped loot from the Loot Collected screen while clicking away any dialog popups.
	 *
	 * @param isPendingBattle Skip the incrementation of runs attempted if this was a Pending Battle. Defaults to false.
	 * @param isEventNightmare Skip the incrementation of runs attempted if this was a Event Nightmare. Defaults to false.
	 * @param skipInfo Skip printing the information of the run. Defaults to False.
	 * @return Number of specified items dropped.
	 */
	fun collectLoot(isPendingBattle: Boolean = false, isEventNightmare: Boolean = false, skipInfo: Boolean = false): Int {
		var amountGained = 0
		
		// Close all popups until the bot reaches the Loot Collected screen.
		var tries = 5
		while (!imageUtils.confirmLocation("loot_collected", tries = 1)) {
			findAndClickButton("close", tries = 1, suppressError = true)
			findAndClickButton("cancel", tries = 1, suppressError = true)
			findAndClickButton("ok", tries = 1, suppressError = true)
			findAndClickButton("new_extended_mastery_level", tries = 1, suppressError = true)
			
			tries -= 1
			if (tries <= 0) {
				break
			}
		}
		
		// Now that the bot is at the Loot Collected screen, detect any user-specified items.
		if (!isPendingBattle && !isEventNightmare) {
			printToLog("\n[INFO] Detecting if any user-specified loot dropped this run...")
			amountGained = if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(itemName)) {
				imageUtils.findFarmedItems(itemName)
			} else {
				1
			}
			
			// Only increment number of runs for Proving Grounds when the bot acquires the Completion Rewards.
			// Currently for Proving Grounds, completing 2 battles per difficulty nets you the Completion Rewards.
			if (farmingMode == "Proving Grounds") {
				if (itemAmountFarmed != 0 && itemAmountFarmed % 2 == 0) {
					itemAmountFarmed = 0
					amountOfRuns += 1
				}
			} else {
				amountOfRuns += 1
			}
		} else if (isPendingBattle) {
			printToLog("\n[INFO] Detecting if any user-specified loot dropped this Pending Battle...")
			amountGained = if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(itemName)) {
				imageUtils.findFarmedItems(itemName)
			} else {
				1
			}
			
			itemAmountFarmed += amountGained
		}
		
		if (!isPendingBattle && !isEventNightmare && !skipInfo) {
			if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(itemName)) {
				printToLog("\n************************************************************")
				printToLog("************************************************************")
				printToLog("[INFO] Farming Mode: $farmingMode")
				printToLog("[INFO] Mission: $missionName")
				printToLog("[INFO] Summons: $summonList")
				printToLog("[INFO] # of $itemName gained this run: $amountGained")
				printToLog("[INFO] # of $itemName gained in total: ${itemAmountFarmed + amountGained}/$itemAmount")
				printToLog("[INFO] # of runs completed: $amountOfRuns")
				printToLog("************************************************************")
				printToLog("************************************************************")
				
				// Construct the message for the Discord private DM.
				if (amountGained > 0) {
					val discordString = if (itemAmountFarmed >= itemAmount) {
						"> ${amountGained}x __${itemName}__ gained this run: **[$itemAmountFarmed / $itemAmount]** -> **[${itemAmountFarmed + amountGained} / $itemAmount]** :white_check_mark:"
					} else {
						"> ${amountGained}x __${itemName}__ gained this run: **[$itemAmountFarmed / $itemAmount]** -> **[${itemAmountFarmed + amountGained} / $itemAmount]**"
					}
					
					DiscordUtils.queue.add(discordString)
				}
			} else {
				printToLog("\n************************************************************")
				printToLog("************************************************************")
				printToLog("[INFO] Farming Mode: $farmingMode")
				printToLog("[INFO] Mission: $missionName")
				printToLog("[INFO] Summons: $summonList")
				printToLog("[INFO] # of runs completed: $amountOfRuns / $itemAmount")
				printToLog("************************************************************")
				printToLog("************************************************************")
				
				// Construct the message for the Discord private DM.
				val discordString = if (amountOfRuns >= itemAmount) {
					"> Runs completed for __${missionName}__: **[${amountOfRuns - 1} / $itemAmount]** -> **[$amountOfRuns / $itemAmount]** :white_check_mark:"
				} else {
					"> Runs completed for __${missionName}__: **[${amountOfRuns - 1} / $itemAmount]** -> **[$amountOfRuns / $itemAmount]**"
				}
				
				DiscordUtils.queue.add(discordString)
			}
		} else if (isPendingBattle && amountGained > 0 && !skipInfo) {
			if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(itemName)) {
				printToLog("\n************************************************************")
				printToLog("************************************************************")
				printToLog("[INFO] Farming Mode: $farmingMode")
				printToLog("[INFO] Mission: $missionName")
				printToLog("[INFO] Summons: $summonList")
				printToLog("[INFO] # of $itemName gained from this Pending Battle: $amountGained")
				printToLog("[INFO] # of $itemName gained in total: ${itemAmountFarmed + amountGained}/$itemAmount")
				printToLog("[INFO] # of runs completed: $amountOfRuns")
				printToLog("************************************************************")
				printToLog("************************************************************")
				
				// Construct the message for the Discord private DM.
				if (amountGained > 0) {
					val discordString = if (itemAmountFarmed >= itemAmount) {
						"> ${amountGained}x __${itemName}__ gained from this Pending Battle: **[$itemAmountFarmed / $itemAmount]** -> **[${itemAmountFarmed + amountGained} / $itemAmount]** :white_check_mark:"
					} else {
						"> ${amountGained}x __${itemName}__ gained from this Pending Battle: **[$itemAmountFarmed / $itemAmount]** -> **[${itemAmountFarmed + amountGained} / $itemAmount]**"
					}
					
					DiscordUtils.queue.add(discordString)
				}
			}
		}
		
		return amountGained
	}
	
	/**
	 * Detect any popups and attempt to close them all with the final destination being the Summon Selection screen.
	 *
	 * @return True if there was a Nightmare mission detected or some other popup appeared that requires the navigation process to be restarted.
	 */
	fun checkForPopups(): Boolean {
		printToLog("\n[INFO] Now beginning process to check for popups...")
		
		var tries = 5
		while (tries > 0 && !imageUtils.confirmLocation("select_a_summon")) {
			if (imageUtils.confirmLocation("auto_ap_recovered", tries = 1) || imageUtils.confirmLocation("auto_ap_recovered2", tries = 1)) {
				break
			}
			
			// Break out of the loop if the bot detected the "Not Enough AP" popup.
			if (imageUtils.confirmLocation("not_enough_ap", tries = 1)) {
				break
			}
			
			if (farmingMode == "Rise of the Beasts" && imageUtils.confirmLocation("rotb_proud_solo_quest", tries = 1)) {
				// Scroll down the screen a little bit because the popup itself is too long.
				gestureUtils.scroll()
			}
			
			// Check for certain popups for certain Farming Modes.
			if ((farmingMode == "Rise of the Beasts" && riseOfTheBeasts.checkROTBExtremePlus()) ||
				(farmingMode == "Special" && missionName == "VH Angel Halo" && itemName == "Angel Halo Weapons" && special.checkDimensionalHalo()) ||
				(farmingMode == "Event" || farmingMode == "Event (Token Drawboxes)") && event.checkEventNightmare() ||
				(farmingMode == "Xeno Clash" && xenoClash.checkForXenoClashNightmare())
			) {
				return true
			}
			
			// If the bot tried to repeat a Extreme/Impossible difficulty Event Raid and it lacked the treasures to host it, go back to the Mission again.
			if ((farmingMode == "Event (Token Drawboxes)" || farmingMode == "Guild Wars") && imageUtils.confirmLocation("not_enough_treasure", tries = 1)) {
				findAndClickButton("ok")
				return true
			}
			
			// Attempt to close the popup by clicking on any detected "Close" and "Cancel" buttons.
			if (!findAndClickButton("close", tries = 1, suppressError = true)) {
				findAndClickButton("cancel", tries = 1, suppressError = true)
			}
			
			// Attempt to scroll up to counteract bug where entering the Summon Selection screen led to being brought to the bottom of the page.
			gestureUtils.scroll(scrollDown = false)
			
			wait(1.0)
			tries -= 1
		}
		
		return false
	}
	
	/**
	 * Detects any "Friend Request" popups and close them.
	 */
	private fun checkFriendRequest() {
		if (imageUtils.confirmLocation("friend_request")) {
			findAndClickButton("cancel")
		}
	}
	
	/**
	 * Detects Skyscope popup and close it.
	 */
	fun checkSkyscope() {
		if (imageUtils.confirmLocation("skyscope")) {
			findAndClickButton("close")
			wait(1.0)
		}
	}
	
	/**
	 * Process a Pending Battle.
	 *
	 * @return Return True if a Pending Battle was successfully processed. Otherwise, return False.
	 */
	private fun clearPendingBattle(): Boolean {
		if (findAndClickButton("tap_here_to_see_rewards")) {
			wait(1.0)
			
			if (imageUtils.confirmLocation("no_loot", tries = 1)) {
				printToLog("[INFO] No loot can be collected. Backing out...")
				
				// Navigate back to the Quests screen.
				findAndClickButton("quests")
				
				return true
			} else {
				// Start loot detection if there it is available.
				if (farmingMode == "Raid") {
					collectLoot()
				} else {
					collectLoot(isPendingBattle = true)
				}
				
				findAndClickButton("close", tries = 1)
				findAndClickButton("ok", tries = 1)
				
				return true
			}
		}
		
		return false
	}
	
	/**
	 * Check and collect any pending rewards and free up slots for the bot to join more Raids.
	 *
	 * @return True if Pending Battles were detected. False otherwise.
	 */
	fun checkPendingBattles(): Boolean {
		printToLog("\n[INFO] Starting process of checking for Pending Battles...")
		wait(1.0)
		
		// Check for the "Check your Pending Battles" popup when navigating to the Quest screen or attempting to join a raid when there are 6
		// Pending Battles or check if the "Play Again" button is covered by the "Pending Battles" button for any other Farming Mode.
		if (imageUtils.confirmLocation("check_your_pending_battles", tries = 1) ||
			imageUtils.confirmLocation("pending_battles", tries = 1) ||
			findAndClickButton("quest_results_pending_battles", tries = 1)
		) {
			printToLog("[INFO] Found Pending Battles that need collecting from.")
			
			findAndClickButton("ok", tries = 1)
			
			wait(1.0)
			
			if (imageUtils.confirmLocation("pending_battles", tries = 1)) {
				// Process the current Pending Battle.
				while (clearPendingBattle()) {
					// While on the Loot Collected screen, if there are more Pending Battles then head back to the Pending Battles screen.
					if (findAndClickButton("quest_results_pending_battles", tries = 1)) {
						wait(1.0)
						checkSkyscope()
						checkFriendRequest()
						wait(1.0)
					} else {
						// When there are no more Pending Battles, go back to the Home screen.
						findAndClickButton("home")
						checkSkyscope()
						break
					}
				}
			}
			
			printToLog("[INFO] Pending Battles have been cleared.")
			return true
		}
		
		printToLog("[INFO] No Pending Battles needed to be cleared.")
		return false
	}
	
	/**
	 * Start Farming Mode with the provided parameters from the user's choices in the settings.
	 *
	 * @return True if Farming Mode completed successfully. False otherwise.
	 */
	fun startFarmingMode(): Boolean {
		// Throw an Exception if the user selected Coop or Arcarum that reset Summons and the user started the bot without selecting new Summons.
		if (farmingMode != "Coop" && farmingMode != "Arcarum" && summonList[0] == "") {
			throw Exception("You have no summons selected for this Farming Mode.")
		}
		
		if (itemName != "EXP") {
			printToLog("\n############################################################")
			printToLog("############################################################")
			printToLog("[FARM] Starting Farming Mode for $farmingMode.")
			printToLog("[FARM] Farming ${itemAmount}x $itemName at $missionName.")
			printToLog("############################################################")
			printToLog("############################################################")
		} else {
			printToLog("\n############################################################")
			printToLog("############################################################")
			printToLog("[FARM] Starting Farming Mode for $farmingMode.")
			printToLog("[FARM] Doing ${itemAmount}x runs for $itemName at $missionName.")
			printToLog("############################################################")
			printToLog("############################################################")
		}
		
		// If the user did not select a combat script, use the default Full Auto combat script.
		if (combatScript.isEmpty() || combatScript[0] == "") {
			printToLog("\n[INFO] User did not provide their own combat script. Defaulting to Full Auto combat script.")
			
			combatScript = listOf(
				"Turn 1:",
				"enableFullAuto",
				"end"
			)
		}
		
		var firstRun = true
		while (itemAmountFarmed < itemAmount) {
			if (farmingMode == "Quest") {
				itemAmountFarmed += quest.start(firstRun)
			} else if (farmingMode == "Special") {
				itemAmountFarmed += special.start(firstRun)
			} else if (farmingMode == "Coop") {
				itemAmountFarmed += coop.start(firstRun)
			} else if (farmingMode == "Raid") {
				itemAmountFarmed += raid.start(firstRun)
			} else if (farmingMode == "Event" || farmingMode == "Event (Token Drawboxes)") {
				itemAmountFarmed += event.start(firstRun)
			} else if (farmingMode == "Rise of the Beasts") {
				itemAmountFarmed += riseOfTheBeasts.start(firstRun)
			} else if (farmingMode == "Guild Wars") {
				itemAmountFarmed += guildWars.start(firstRun)
			} else if (farmingMode == "Dread Barrage") {
				itemAmountFarmed += dreadBarrage.start(firstRun)
			} else if (farmingMode == "Proving Grounds") {
				itemAmountFarmed += provingGrounds.start(firstRun)
			} else if (farmingMode == "Xeno Clash") {
				itemAmountFarmed += xenoClash.start(firstRun)
			} else if (farmingMode == "Arcarum") {
				itemAmountFarmed += arcarum.start()
			}
			
			if (itemAmountFarmed < itemAmount) {
				// Generate a resting period if the user enabled it.
				delayBetweenRuns()
				firstRun = false
			}
		}
		
		printToLog("\n************************************************************")
		printToLog("************************************************************")
		printToLog("[INFO] Farming Mode has ended")
		printToLog("************************************************************")
		printToLog("************************************************************")
		
		return true
	}
}