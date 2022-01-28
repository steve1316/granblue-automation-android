package com.steve1316.granblue_automation_android.bot

import android.content.Context
import android.content.res.Resources
import android.util.Log
import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import com.steve1316.granblue_automation_android.StartModule
import com.steve1316.granblue_automation_android.bot.game_modes.*
import com.steve1316.granblue_automation_android.data.ConfigData
import com.steve1316.granblue_automation_android.data.SummonData
import com.steve1316.granblue_automation_android.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.opencv.core.Point
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Main driver for bot activity and navigation for the web browser game, Granblue Fantasy.
 */
class Game(myContext: Context) {
	private val tag: String = "${loggerTag}Game"

	var itemAmountFarmed: Int = 0
	private var amountOfRuns: Int = 0
	private val startTime: Long = System.currentTimeMillis()
	private var partySelectionFirstRun: Boolean = true

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
	private lateinit var generic: Generic

	val configData: ConfigData = ConfigData(myContext)
	val imageUtils: ImageUtils = ImageUtils(myContext, this)
	val gestureUtils: MyAccessibilityService = MyAccessibilityService.getInstance()
	var twitterRoomFinder: TwitterRoomFinder = TwitterRoomFinder(this)
	val combatMode: CombatMode = CombatMode(this, configData.debugMode)

	init {
		if (configData.farmingMode == "Quest") {
			quest = Quest(this, configData.mapName, configData.missionName)
		} else if (configData.farmingMode == "Special") {
			special = Special(this, configData.mapName, configData.missionName)
		} else if (configData.farmingMode == "Coop") {
			coop = Coop(this, configData.missionName)
		} else if (configData.farmingMode == "Raid") {
			raid = Raid(this)
		} else if (configData.farmingMode == "Event" || configData.farmingMode == "Event (Token Drawboxes)") {
			event = Event(this, configData.missionName)
		} else if (configData.farmingMode == "Dread Barrage") {
			dreadBarrage = DreadBarrage(this, configData.missionName)
		} else if (configData.farmingMode == "Rise of the Beasts") {
			riseOfTheBeasts = RiseOfTheBeasts(this, configData.missionName)
		} else if (configData.farmingMode == "Guild Wars") {
			guildWars = GuildWars(this, configData.missionName)
		} else if (configData.farmingMode == "Proving Grounds") {
			provingGrounds = ProvingGrounds(this, configData.missionName)
		} else if (configData.farmingMode == "Xeno Clash") {
			xenoClash = XenoClash(this, configData.missionName)
		} else if (configData.farmingMode == "Arcarum") {
			arcarum = Arcarum(this, configData.missionName)
		} else if (configData.farmingMode == "Generic") {
			generic = Generic(this)
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
		val newMessage = if (message.startsWith("\n")) {
			"\n" + printTime() + " " + message.removePrefix("\n")
		} else {
			printTime() + " " + message
		}

		MessageLog.messageLog.add(newMessage)

		// Send the message to the frontend.
		StartModule.sendEvent("MessageLog", newMessage)
	}

	/**
	 * Go back to the Home screen by tapping the "Home" button.
	 *
	 * @param confirmLocationCheck Whether or not the bot should confirm that it has arrived at the Home screen.
	 * @param testMode Flag to test and get a valid scale for device compatibility.
	 */
	fun goBackHome(confirmLocationCheck: Boolean = false, testMode: Boolean = false) {
		if ((configData.enableCalibrationAdjustment && !imageUtils.confirmLocation("home", tries = configData.adjustCalibration, bypassGeneralAdjustment = true)) ||
			(!configData.enableCalibrationAdjustment && !imageUtils.confirmLocation("home"))
		) {
			printToLog("[INFO] Moving back to the Home screen...")

			if ((configData.enableCalibrationAdjustment && !findAndClickButton("home", tries = configData.adjustCalibration, bypassGeneralAdjustment = true)) ||
				(!configData.enableCalibrationAdjustment && !findAndClickButton("home"))
			) {
				if (!testMode) {
					throw Exception("HOME button is not found. Stopping bot to prevent cascade of errors. Please readjust your confidences/scales.")
				} else {
					printToLog("\n[DEBUG] Failed to find the HOME button. Now beginning test to find a valid scale for this device...")
					imageUtils.findButton("home", testMode = true)
					return
				}
			}

			// Wait a few seconds for the page to load and to prevent the bot from prematurely scrolling all the way to the bottom.
			wait(4.0)

			printToLog("\n[INFO] Screen Width: ${MediaProjectionService.displayWidth}, Screen Height: ${MediaProjectionService.displayHeight}, Screen DPI: ${MediaProjectionService.displayDPI}")

			// Check for any misc popups.
			findAndClickButton("close")

			if (confirmLocationCheck) {
				wait(2.0)

				if ((configData.enableCalibrationAdjustment && !imageUtils.confirmLocation("home", tries = configData.adjustCalibration, bypassGeneralAdjustment = true)) ||
					(!configData.enableCalibrationAdjustment && !imageUtils.confirmLocation("home"))
				) {
					findAndClickButton("reload")
					wait(4.0)
					if ((configData.enableCalibrationAdjustment && !imageUtils.confirmLocation("home", tries = configData.adjustCalibration, bypassGeneralAdjustment = true)) ||
						(!configData.enableCalibrationAdjustment && !imageUtils.confirmLocation("home"))
					) {
						throw Exception("Failed to head back to the Home screen after clicking on the Home button.")
					}
				}
			}
		} else {
			printToLog("[INFO] Bot is already at the Home screen.")
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
	 * @param tries Number of tries to find the specified button. Defaults to 0 which will use ImageUtil's default.
	 * @param suppressError Whether or not to suppress saving error messages to the log in failing to find the button.
	 * @param bypassGeneralAdjustment Bypass using the general adjustment for the number of tries. Defaults to True.
	 * @return True if the button was found and clicked. False otherwise.
	 */
	fun findAndClickButton(buttonName: String, tries: Int = 0, suppressError: Boolean = false, bypassGeneralAdjustment: Boolean = true): Boolean {
		if (configData.debugMode) {
			printToLog("[DEBUG] Now attempting to find and click the \"$buttonName\" button.")
		}

		var tempLocation: Point?
		var newButtonName = buttonName

		if (tries == 0) {
			if (buttonName.lowercase() == "quest") {
				tempLocation = imageUtils.findButton("quest_blue", suppressError = suppressError)
				newButtonName = "quest_blue"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("quest_red", suppressError = suppressError)
					newButtonName = "quest_red"
				}

			} else if (buttonName.lowercase() == "raid") {
				tempLocation = imageUtils.findButton("raid_flat", suppressError = suppressError)
				newButtonName = "raid_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("raid_bouncing", suppressError = suppressError)
					newButtonName = "raid_bouncing"
				}

			} else if (buttonName.lowercase() == "coop_start") {
				tempLocation = imageUtils.findButton("coop_start_flat", suppressError = suppressError)
				newButtonName = "coop_start_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("coop_start_faded", suppressError = suppressError)
					newButtonName = "coop_start_faded"
				}

			} else if (buttonName.lowercase() == "event_special_quest") {
				tempLocation = imageUtils.findButton("event_special_quest_flat", suppressError = suppressError)
				newButtonName = "event_special_quest_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("event_special_quest_bouncing", suppressError = suppressError)
					newButtonName = "event_special_quest_bouncing"
				}

			} else if (buttonName.lowercase() == "world") {
				tempLocation = imageUtils.findButton("world", suppressError = suppressError)
				newButtonName = "world"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("world2", suppressError = suppressError)
					newButtonName = "world2"
				}
			} else {
				tempLocation = imageUtils.findButton(buttonName, suppressError = suppressError)
			}
		} else {
			if (buttonName.lowercase() == "quest") {
				tempLocation = imageUtils.findButton("quest_blue", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
				newButtonName = "quest_blue"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("quest_red", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
					newButtonName = "quest_red"
				}

			} else if (buttonName.lowercase() == "raid") {
				tempLocation = imageUtils.findButton("raid_flat", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
				newButtonName = "raid_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("raid_bouncing", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
					newButtonName = "raid_bouncing"
				}

			} else if (buttonName.lowercase() == "coop_start") {
				tempLocation = imageUtils.findButton("coop_start_flat", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
				newButtonName = "coop_start_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("coop_start_faded", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
					newButtonName = "coop_start_faded"
				}

			} else if (buttonName.lowercase() == "event_special_quest") {
				tempLocation = imageUtils.findButton("event_special_quest_flat", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
				newButtonName = "event_special_quest_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("event_special_quest_bouncing", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
					newButtonName = "event_special_quest_bouncing"
				}

			} else if (buttonName.lowercase() == "world") {
				tempLocation = imageUtils.findButton("world", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
				newButtonName = "world"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("world2", tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
					newButtonName = "world2"
				}
			} else {
				tempLocation = imageUtils.findButton(buttonName, tries = tries, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
			}
		}

		return if (tempLocation != null) {
			if (configData.enableDelayTap) {
				val newDelay: Double = ((configData.delayTapMilliseconds - 100)..(configData.delayTapMilliseconds + 100)).random().toDouble() / 1000
				if (configData.debugMode) printToLog("[DEBUG] Adding an additional delay of ${newDelay}s...")
				wait(newDelay)
			}

			gestureUtils.tap(tempLocation.x, tempLocation.y, newButtonName)
		} else {
			false
		}
	}

	/**
	 * Checks for CAPTCHA right after selecting a Summon. If detected, alert the user and stop the bot.
	 */
	fun checkForCAPTCHA() {
		if ((configData.enableCaptchaAdjustment && imageUtils.confirmLocation("captcha", tries = configData.adjustCaptcha, bypassGeneralAdjustment = true)) ||
			(!configData.enableCaptchaAdjustment && imageUtils.confirmLocation("captcha"))
		) {
			throw(Exception("[CAPTCHA] CAPTCHA has been detected! Stopping the bot now."))
		} else {
			printToLog("\n[CAPTCHA] CAPTCHA not detected.")
		}
	}

	/**
	 * Execute a delay after every run completed based on user settings from config.yaml.
	 */
	private fun delayBetweenRuns() {
		if (configData.enableDelayBetweenRuns) {
			printToLog("\n[INFO] Now waiting for ${configData.delayBetweenRuns} seconds as the resting period. Please do not navigate from the current screen.")

			wait(configData.delayBetweenRuns.toDouble())
		} else if (!configData.enableDelayBetweenRuns && configData.enableRandomizedDelayBetweenRuns) {
			val newSeconds = Random().nextInt(configData.delayBetweenRunsUpperBound - configData.delayBetweenRunsLowerBound) + configData.delayBetweenRunsLowerBound
			printToLog(
				"\n[INFO] Given the bounds of (${configData.delayBetweenRunsLowerBound}, ${configData.delayBetweenRunsUpperBound}), bot will now wait for $newSeconds seconds as a resting " +
						"period. Please do not navigate from the current screen."
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
			configData.summonList
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

		return when {
			summonLocation != null -> {
				// Select the Summon.
				gestureUtils.tap(summonLocation.x, summonLocation.y, "template_summon")

				// Check for CAPTCHA.
				checkForCAPTCHA()

				true
			}
			configData.enableBypassResetSummon -> {
				printToLog("[INFO] Bypassing procedure to reset Summons. Reloading page and selecting the very first one now...")

				findAndClickButton("reload")
				wait(3.0)

				// Now select the first Summon.
				val chooseASummonLocation = imageUtils.findButton("choose_a_summon")!!
				gestureUtils.tap(chooseASummonLocation.x, chooseASummonLocation.y + 400, "template_summon")

				true
			}
			else -> {
				// Reset Summons if not found.
				resetSummons()

				false
			}
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

		if (findAndClickButton("gameplay_extras")) {
			// If the bot cannot find the "Trial Battles" button, keep scrolling down until it does. It should not take more than 2 loops to see it for any reasonable screen size.
			while (!findAndClickButton("trial_battles")) {
				gestureUtils.swipe(500f, 1000f, 500f, 400f)
			}

			if (imageUtils.confirmLocation("trial_battles")) {
				// Press the "Old Lignoid" button.
				findAndClickButton("trial_battles_old_lignoid")

				// Press any detected "Play" button.
				findAndClickButton("play_round_button")

				// Now select the first Summon.
				val chooseASummonLocation = imageUtils.findButton("choose_a_summon")!!
				gestureUtils.tap(chooseASummonLocation.x, chooseASummonLocation.y + 400, "template_summon")

				// Now start the Old Lignoid Trial Battle right away and then wait a few seconds.
				wait(3.0)

				// Retreat from this Trial Battle.
				findAndClickButton("menu", tries = 30)
				findAndClickButton("retreat", tries = 30)
				findAndClickButton("retreat_confirmation", tries = 30)
				goBackHome()

				if (imageUtils.confirmLocation("home")) {
					printToLog("[SUCCESS] Summons have now been refreshed.")
				}
			}
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
			wait(1.0)

			var setLocation: Point? = null
			var numberOfTries = tries

			val selectedGroupNumber = if (optionalGroupNumber == 0) {
				configData.groupNumber
			} else {
				optionalGroupNumber
			}

			val selectedPartyNumber = if (optionalPartyNumber == 0) {
				configData.partyNumber
			} else {
				optionalPartyNumber
			}

			// Search for the location of the "Set" button based on the Group number.
			while (setLocation == null) {
				setLocation = if (selectedGroupNumber < 8) {
					imageUtils.findButton("party_set_a", tries = 10)
				} else {
					imageUtils.findButton("party_set_b", tries = 10)
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
						imageUtils.findButton("party_set_b", tries = 10)
					} else {
						imageUtils.findButton("party_set_a", tries = 10)
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

			wait(2.0)

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

			wait(2.0)

			partySelectionFirstRun = false
		}

		printToLog("[SUCCESS] Selected Group and Party successfully.")

		// Start the mission by clicking "OK".
		findAndClickButton("ok")
		wait(1.0)

		// Detect if a "This raid battle has already ended" popup appeared.
		if (configData.farmingMode == "Raid" && findAndClickButton("ok")) {
			printToLog("[WARNING] Raid unfortunately just ended. Backing out now...")
			wait(3.0)
			return false
		}

		wait(3.0)
		return true
	}

	/**
	 * Checks if the user has available AP. If not, then it will refill it.
	 *
	 * @param useFullElixir Will use Full Elixir instead of Half Elixir. Defaults to false.
	 * @param tries Number of tries to try to refill AP. Defaults to 3.
	 */
	fun checkAP(useFullElixir: Boolean = false, tries: Int = 3) {
		if (!configData.enableAutoRestore) {
			var numberOfTries = tries

			wait(2.0)

			if (!imageUtils.confirmLocation("auto_ap_recovered", tries = 1) && !imageUtils.confirmLocation("auto_ap_recovered2", tries = 1)) {
				while ((configData.farmingMode != "Coop" && !imageUtils.confirmLocation("select_a_summon", tries = 1)) ||
					(configData.farmingMode == "Coop" && !imageUtils.confirmLocation("coop_without_support_summon", tries = 1))
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
		if (!configData.enableAutoRestore) {
			var numberOfTries = tries

			wait(2.0)

			if (!imageUtils.confirmLocation("auto_ep_recovered", tries = 1)) {
				while (configData.farmingMode == "Raid" && !imageUtils.confirmLocation("select_a_summon", tries = 1)) {
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
	 * @param isCompleted Allows incrementing of number of runs completed. This is for Farming Modes who have multi-part sections to them to prevent unnecessary incrementing of runs when it wasn't
	 * finished with 1 yet.
	 * @param isPendingBattle Skip the incrementation of runs attempted if this was a Pending Battle. Defaults to false.
	 * @param isEventNightmare Skip the incrementation of runs attempted if this was a Event Nightmare. Defaults to false.
	 * @param skipInfo Skip printing the information of the run. Defaults to false.
	 * @param skipPopupCheck Skip checking for popups to get to the Loot Collected screen. Defaults to false
	 * @return Number of specified items dropped.
	 */
	fun collectLoot(isCompleted: Boolean, isPendingBattle: Boolean = false, isEventNightmare: Boolean = false, skipInfo: Boolean = false, skipPopupCheck: Boolean = false): Int {
		var amountGained = 0

		// Close all popups until the bot reaches the Loot Collected screen.
		if (!skipPopupCheck) {
			while (!imageUtils.confirmLocation("loot_collected", tries = 1)) {
				findAndClickButton("ok", tries = 1, suppressError = true)
				findAndClickButton("close", tries = 1, suppressError = true)
				findAndClickButton("cancel", tries = 1, suppressError = true)
				findAndClickButton("new_extended_mastery_level", tries = 1, suppressError = true)

				if (imageUtils.confirmLocation("no_loot", tries = 1)) {
					return 0
				}

				if (configData.debugMode) {
					printToLog("[DEBUG] Have not detected the Loot Collection screen yet...")
				}
			}
		}

		// Now that the bot is at the Loot Collected screen, detect any user-specified items.
		if (isCompleted && !isPendingBattle && !isEventNightmare) {
			printToLog("\n[INFO] Detecting if any user-specified loot dropped this run...")
			amountGained = if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(configData.itemName)) {
				imageUtils.findFarmedItems(configData.itemName)
			} else {
				1
			}

			amountOfRuns += 1
		} else if (isPendingBattle) {
			printToLog("\n[INFO] Detecting if any user-specified loot dropped this Pending Battle...")
			amountGained = if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(configData.itemName)) {
				imageUtils.findFarmedItems(configData.itemName)
			} else {
				1
			}

			itemAmountFarmed += amountGained
		}

		if (isCompleted && !isPendingBattle && !isEventNightmare && !skipInfo) {
			if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(configData.itemName)) {
				printToLog("\n********************")
				printToLog("********************")
				printToLog("[INFO] Farming Mode: ${configData.farmingMode}")
				printToLog("[INFO] Mission: ${configData.missionName}")
				printToLog("[INFO] Summons: ${configData.summonList}")
				printToLog("[INFO] # of ${configData.itemName} gained this run: $amountGained")
				printToLog("[INFO] # of ${configData.itemName} gained in total: ${itemAmountFarmed + amountGained}/${configData.itemAmount}")
				printToLog("[INFO] # of runs completed: $amountOfRuns")
				printToLog("********************")
				printToLog("********************")

				// Construct the message for the Discord private DM.
				if (amountGained > 0) {
					val discordString = if (itemAmountFarmed >= configData.itemAmount) {
						"> ${amountGained}x __${configData.itemName}__ gained this run: **[$itemAmountFarmed / ${configData.itemAmount}]** -> **[${itemAmountFarmed + amountGained} / " +
								"${configData.itemAmount}]** :white_check_mark:"
					} else {
						"> ${amountGained}x __${configData.itemName}__ gained this run: **[$itemAmountFarmed / ${configData.itemAmount}]** -> **[${itemAmountFarmed + amountGained} / " +
								"${configData.itemAmount}]**"
					}

					DiscordUtils.queue.add(discordString)
				}
			} else {
				printToLog("\n********************")
				printToLog("********************")
				printToLog("[INFO] Farming Mode: ${configData.farmingMode}")
				printToLog("[INFO] Mission: ${configData.missionName}")
				printToLog("[INFO] Summons: ${configData.summonList}")
				printToLog("[INFO] # of runs completed: $amountOfRuns / ${configData.itemAmount}")
				printToLog("********************")
				printToLog("********************")

				// Construct the message for the Discord private DM.
				val discordString = if (amountOfRuns >= configData.itemAmount) {
					"> Runs completed for __${configData.missionName}__: **[${amountOfRuns - 1} / ${configData.itemAmount}]** -> **[$amountOfRuns / ${configData.itemAmount}]** :white_check_mark:"
				} else {
					"> Runs completed for __${configData.missionName}__: **[${amountOfRuns - 1} / ${configData.itemAmount}]** -> **[$amountOfRuns / ${configData.itemAmount}]**"
				}

				DiscordUtils.queue.add(discordString)
			}
		} else if (isPendingBattle && amountGained > 0 && !skipInfo) {
			if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(configData.itemName)) {
				printToLog("\n********************")
				printToLog("********************")
				printToLog("[INFO] Farming Mode: ${configData.farmingMode}")
				printToLog("[INFO] Mission: ${configData.missionName}")
				printToLog("[INFO] Summons: ${configData.summonList}")
				printToLog("[INFO] # of ${configData.itemName} gained from this Pending Battle: $amountGained")
				printToLog("[INFO] # of ${configData.itemName} gained in total: ${itemAmountFarmed + amountGained}/${configData.itemAmount}")
				printToLog("[INFO] # of runs completed: $amountOfRuns")
				printToLog("********************")
				printToLog("********************")

				// Construct the message for the Discord private DM.
				if (amountGained > 0) {
					val discordString = if (itemAmountFarmed >= configData.itemAmount) {
						"> ${amountGained}x __${configData.itemName}__ gained from this Pending Battle: **[$itemAmountFarmed / ${configData.itemAmount}]** -> **[${itemAmountFarmed + amountGained} /" +
								" ${configData.itemAmount}]** :white_check_mark:"
					} else {
						"> ${amountGained}x __${configData.itemName}__ gained from this Pending Battle: **[$itemAmountFarmed / ${configData.itemAmount}]** -> **[${itemAmountFarmed + amountGained} /" +
								" ${configData.itemAmount}]**"
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

		while (!imageUtils.confirmLocation("select_a_summon", tries = 1, suppressError = true)) {
			if (!configData.enableAutoRestore && (imageUtils.confirmLocation("auto_ap_recovered", tries = 1) || imageUtils.confirmLocation("auto_ap_recovered2", tries = 1))) {
				break
			}

			// Break out of the loop if the bot detected the "Not Enough AP" popup.
			if (!configData.enableAutoRestore && imageUtils.confirmLocation("not_enough_ap", tries = 1)) {
				break
			}

			if (configData.farmingMode == "Rise of the Beasts" && imageUtils.confirmLocation("rotb_proud_solo_quest", tries = 1)) {
				// Scroll down the screen a little bit because the popup itself is too long.
				gestureUtils.scroll()
			}

			// Check for certain popups for certain Farming Modes.
			if ((configData.farmingMode == "Rise of the Beasts" && riseOfTheBeasts.checkROTBExtremePlus()) ||
				(configData.farmingMode == "Special" && configData.missionName == "VH Angel Halo" && configData.itemName == "Angel Halo Weapons" && special.checkDimensionalHalo()) ||
				(configData.farmingMode == "Event" || configData.farmingMode == "Event (Token Drawboxes)") && event.checkEventNightmare() ||
				(configData.farmingMode == "Xeno Clash" && xenoClash.checkForXenoClashNightmare())
			) {
				return true
			}

			// If the bot tried to repeat a Extreme/Impossible difficulty Event Raid and it lacked the treasures to host it, go back to the Mission again.
			if ((configData.farmingMode == "Event (Token Drawboxes)" || configData.farmingMode == "Guild Wars") && imageUtils.confirmLocation("not_enough_treasure", tries = 1)) {
				findAndClickButton("ok")
				return true
			}

			// Attempt to close the popup by clicking on any detected "Close" and "Cancel" buttons.
			if (!findAndClickButton("close", tries = 1, suppressError = true)) {
				findAndClickButton("cancel", tries = 1, suppressError = true)
			}

			if (configData.debugMode) {
				printToLog("[DEBUG] Have not detected the Support Summon Selection screen yet...")
			}
		}

		return false
	}

	/**
	 * Detects any "Friend Request" popups and close them.
	 */
	fun checkFriendRequest() {
		if (imageUtils.confirmLocation("friend_request")) {
			findAndClickButton("cancel")
			wait(2.0)
		}
	}

	/**
	 * Detects Skyscope popup and close it.
	 */
	fun checkSkyscope() {
		if (imageUtils.confirmLocation("skyscope")) {
			findAndClickButton("close")
			wait(2.0)
		}
	}

	/**
	 * Process a Pending Battle.
	 *
	 * @return Return True if a Pending Battle was successfully processed. Otherwise, return False.
	 */
	private fun clearPendingBattle(): Boolean {
		if (findAndClickButton("tap_here_to_see_rewards", tries = 10)) {
			printToLog("[INFO] Clearing this Pending Battle...")
			wait(2.0)

			if (imageUtils.confirmLocation("no_loot")) {
				printToLog("[INFO] No loot can be collected. Backing out...")

				// Navigate back to the Quests screen.
				findAndClickButton("quests")

				return true
			} else {
				// Start loot detection if there it is available.
				if (configData.farmingMode == "Raid") {
					collectLoot(isCompleted = true)
				} else {
					collectLoot(isCompleted = false, isPendingBattle = true)
				}

				findAndClickButton("close", suppressError = true)
				findAndClickButton("ok", suppressError = true)

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

		if (configData.enablePendingBattleAdjustment) {
			wait(configData.adjustBeforePendingBattle.toDouble())
		} else {
			wait(1.0)
		}

		// Check for the "Check your Pending Battles" popup when navigating to the Quest screen or attempting to join a raid when there are 6
		// Pending Battles or check if the "Play Again" button is covered by the "Pending Battles" button for any other Farming Mode.
		if ((configData.enablePendingBattleAdjustment &&
					(imageUtils.confirmLocation("check_your_pending_battles", tries = configData.adjustPendingBattle, bypassGeneralAdjustment = true) ||
							imageUtils.confirmLocation("pending_battles", tries = configData.adjustPendingBattle, bypassGeneralAdjustment = true) ||
							findAndClickButton("quest_results_pending_battles", tries = configData.adjustPendingBattle, bypassGeneralAdjustment = true))) ||
			(!configData.enablePendingBattleAdjustment &&
					(imageUtils.confirmLocation("check_your_pending_battles", tries = 2) ||
							imageUtils.confirmLocation("pending_battles", tries = 2) ||
							findAndClickButton("quest_results_pending_battles", tries = 2)))
		) {
			printToLog("[INFO] Found Pending Battles that need collecting from.")
			findAndClickButton("ok")

			wait(3.0)

			if (imageUtils.confirmLocation("pending_battles")) {
				// Process the current Pending Battle.
				while (clearPendingBattle()) {
					// While on the Loot Collected screen, if there are more Pending Battles then head back to the Pending Battles screen.
					if (findAndClickButton("quest_results_pending_battles")) {
						wait(1.0)
						checkSkyscope()
					} else {
						// When there are no more Pending Battles, go back to the Home screen.
						findAndClickButton("home")
						wait(1.0)
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
		if (configData.farmingMode != "Coop" && configData.farmingMode != "Arcarum" && configData.summonList[0] == "") {
			throw Exception("You have no summons selected for this Farming Mode.")
		}

		if (configData.itemName != "EXP") {
			printToLog("\n####################")
			printToLog("####################")
			printToLog("[FARM] Starting Farming Mode for ${configData.farmingMode}.")
			printToLog("[FARM] Farming ${configData.itemAmount}x ${configData.itemName} at ${configData.missionName}.")
			printToLog("####################")
			printToLog("####################")
		} else {
			printToLog("\n####################")
			printToLog("####################")
			printToLog("[FARM] Starting Farming Mode for ${configData.farmingMode}.")
			printToLog("[FARM] Doing ${configData.itemAmount}x runs for ${configData.itemName} at ${configData.missionName}.")
			printToLog("####################")
			printToLog("####################")
		}

		var firstRun = true
		while (itemAmountFarmed < configData.itemAmount) {
			if (configData.farmingMode == "Quest") {
				itemAmountFarmed += quest.start(firstRun)
			} else if (configData.farmingMode == "Special") {
				itemAmountFarmed += special.start(firstRun)
			} else if (configData.farmingMode == "Coop") {
				itemAmountFarmed += coop.start(firstRun)
			} else if (configData.farmingMode == "Raid") {
				itemAmountFarmed += raid.start(firstRun)
			} else if (configData.farmingMode == "Event" || configData.farmingMode == "Event (Token Drawboxes)") {
				itemAmountFarmed += event.start(firstRun)
			} else if (configData.farmingMode == "Rise of the Beasts") {
				itemAmountFarmed += riseOfTheBeasts.start(firstRun)
			} else if (configData.farmingMode == "Guild Wars") {
				itemAmountFarmed += guildWars.start(firstRun)
			} else if (configData.farmingMode == "Dread Barrage") {
				itemAmountFarmed += dreadBarrage.start(firstRun)
			} else if (configData.farmingMode == "Proving Grounds") {
				itemAmountFarmed += provingGrounds.start(firstRun)
			} else if (configData.farmingMode == "Xeno Clash") {
				itemAmountFarmed += xenoClash.start(firstRun)
			} else if (configData.farmingMode == "Arcarum") {
				itemAmountFarmed += arcarum.start()
			} else if (configData.farmingMode == "Generic") {
				itemAmountFarmed += generic.start()
			}

			if (itemAmountFarmed < configData.itemAmount) {
				// Generate a resting period if the user enabled it.
				delayBetweenRuns()
				firstRun = false
			}
		}

		printToLog("\n********************")
		printToLog("********************")
		printToLog("[INFO] Farming Mode has ended")
		printToLog("********************")
		printToLog("********************")

		return true
	}
}