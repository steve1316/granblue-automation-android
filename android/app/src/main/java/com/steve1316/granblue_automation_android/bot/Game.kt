package com.steve1316.granblue_automation_android.bot

import android.content.Context
import android.content.res.Resources
import com.steve1316.automation_library.data.SharedData
import com.steve1316.automation_library.utils.DiscordUtils
import com.steve1316.automation_library.utils.MessageLog
import com.steve1316.automation_library.utils.MyAccessibilityService
import com.steve1316.granblue_automation_android.MainActivity.loggerTag
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
class Game(private val myContext: Context) {
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
	private lateinit var riseOfTheBeasts: ROTB
	private lateinit var guildWars: GuildWars
	private lateinit var provingGrounds: ProvingGrounds
	private lateinit var xenoClash: XenoClash
	private lateinit var arcarum: Arcarum
	private lateinit var arcarumSandbox: ArcarumSandbox
	private lateinit var generic: Generic

	val configData: ConfigData = ConfigData(myContext)
	val imageUtils: CustomImageUtils = CustomImageUtils(myContext, this)
	lateinit var gestureUtils: MyAccessibilityService
	var twitterRoomFinder: TwitterRoomFinder = TwitterRoomFinder(myContext, configData)
	val combatMode: CombatMode = CombatMode(this, configData.debugMode)

	init {
		when (configData.farmingMode) {
			"Quest" -> {
				quest = Quest(this, configData.mapName, configData.missionName)
			}
			"Special" -> {
				special = Special(this, configData.mapName, configData.missionName)
			}
			"Coop" -> {
				coop = Coop(this, configData.missionName)
			}
			"Raid" -> {
				raid = Raid(this)
			}
			"Event", "Event (Token Drawboxes)" -> {
				event = Event(this, configData.missionName)
			}
			"Dread Barrage" -> {
				dreadBarrage = DreadBarrage(this, configData.missionName)
			}
			"Rise of the Beasts" -> {
				riseOfTheBeasts = ROTB(this, configData.missionName)
			}
			"Guild Wars" -> {
				guildWars = GuildWars(this, configData.missionName)
			}
			"Proving Grounds" -> {
				provingGrounds = ProvingGrounds(this, configData.missionName)
			}
			"Xeno Clash" -> {
				xenoClash = XenoClash(this, configData.missionName)
			}
			"Arcarum" -> {
				arcarum = Arcarum(this, configData.missionName)
			}
			"Arcarum Sandbox" -> {
				arcarumSandbox = ArcarumSandbox(this)
			}
			"Generic" -> {
				generic = Generic(this)
			}
		}
	}

	/**
	 * Go back to the Home screen by tapping the "Home" button.
	 *
	 * @param confirmLocationCheck Whether or not the bot should confirm that it has arrived at the Home screen.
	 * @param testMode Flag to test and get a valid scale for device compatibility.
	 */
	fun goBackHome(confirmLocationCheck: Boolean = false, testMode: Boolean = false) {
		if (!imageUtils.confirmLocation("home", bypassGeneralAdjustment = true)) {
			MessageLog.printToLog("[INFO] Moving back to the Home screen...", tag)

			if (!findAndClickButton("home", bypassGeneralAdjustment = true)) {
				if (!testMode) {
					throw Exception("HOME button is not found. Stopping bot to prevent cascade of errors. Please readjust your confidences/scales.")
				} else {
					MessageLog.printToLog("\n[DEBUG] Failed to find the HOME button. Now beginning test to find a valid scale for this device...", tag)
					imageUtils.findButton("home", testMode = true)
					return
				}
			}

			// Wait a few seconds for the page to load and to prevent the bot from prematurely scrolling all the way to the bottom.
			wait(4.0)

			MessageLog.printToLog("\n[INFO] Screen Width: ${SharedData.displayWidth}, Screen Height: ${SharedData.displayHeight}, Screen DPI: ${SharedData.displayDPI}", tag)

			// Check for any misc popups.
			findAndClickButton("close")

			if (confirmLocationCheck) {
				wait(2.0)

				if (!imageUtils.confirmLocation("home", bypassGeneralAdjustment = true)) {
					findAndClickButton("reload")
					wait(4.0)
					if (!imageUtils.confirmLocation("home", bypassGeneralAdjustment = true)) {
						throw Exception("Failed to head back to the Home screen after clicking on the Home button.")
					}
				}
			}
		} else {
			MessageLog.printToLog("[INFO] Bot is already at the Home screen.", tag)
		}
	}

	/**
	 * Wait the specified seconds to account for ping or loading.
	 *
	 * @param seconds umber of seconds for the execution to wait for. Defaults to 3.0.
	 */
	fun wait(seconds: Double = 3.0) {
		if (configData.reduceDelaySeconds > 0.0) {
			if (seconds - configData.reduceDelaySeconds < 0.0) {
				runBlocking {
					delay((seconds * 1000).toLong())
				}
			} else {
				runBlocking {
					delay(((seconds - configData.reduceDelaySeconds) * 1000).toLong())
				}
			}
		} else {
			runBlocking {
				delay((seconds * 1000).toLong())
			}
		}
	}

	/**
	 * Find and click button
	 *
	 * @param buttonName Name of the button image file in the /assets/buttons/ folder.
	 * @param tries Number of tries to find the specified button. Defaults to 0 which will use ImageUtil's default.
	 * @param customConfidence Use a custom confidence for the template matching. Defaults to the device default.
	 * @param suppressError Whether or not to suppress saving error messages to the log in failing to find the button.
	 * @param bypassGeneralAdjustment Bypass using the general adjustment for the number of tries. Defaults to True.
	 * @return True if the button was found and clicked. False otherwise.
	 */
	fun findAndClickButton(buttonName: String, tries: Int = 0, customConfidence: Double = configData.confidence, suppressError: Boolean = false, bypassGeneralAdjustment: Boolean = true): Boolean {
		if (configData.debugMode) {
			MessageLog.printToLog("[DEBUG] Now attempting to find and click the \"$buttonName\" button.", tag)
		}

		var tempLocation: Point?
		var newButtonName = buttonName

		if (tries == 0) {
			if (buttonName.lowercase() == "quest") {
				tempLocation = imageUtils.findButton("quest_blue", customConfidence = customConfidence, suppressError = suppressError)
				newButtonName = "quest_blue"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("quest_red", customConfidence = customConfidence, suppressError = suppressError)
					newButtonName = "quest_red"
				}

			} else if (buttonName.lowercase() == "raid") {
				tempLocation = imageUtils.findButton("raid_flat", customConfidence = customConfidence, suppressError = suppressError)
				newButtonName = "raid_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("raid_bouncing", customConfidence = customConfidence, suppressError = suppressError)
					newButtonName = "raid_bouncing"
				}

			} else if (buttonName.lowercase() == "coop_start") {
				tempLocation = imageUtils.findButton("coop_start_flat", customConfidence = customConfidence, suppressError = suppressError)
				newButtonName = "coop_start_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("coop_start_faded", customConfidence = customConfidence, suppressError = suppressError)
					newButtonName = "coop_start_faded"
				}

			} else if (buttonName.lowercase() == "event_special_quest") {
				tempLocation = imageUtils.findButton("event_special_quest_flat", customConfidence = customConfidence, suppressError = suppressError)
				newButtonName = "event_special_quest_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("event_special_quest_bouncing", customConfidence = customConfidence, suppressError = suppressError)
					newButtonName = "event_special_quest_bouncing"
				}

			} else if (buttonName.lowercase() == "world") {
				tempLocation = imageUtils.findButton("world", customConfidence = customConfidence, suppressError = suppressError)
				newButtonName = "world"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("world2", customConfidence = customConfidence, suppressError = suppressError)
					newButtonName = "world2"
				}
			} else {
				tempLocation = imageUtils.findButton(buttonName, customConfidence = customConfidence, suppressError = suppressError)
			}
		} else {
			if (buttonName.lowercase() == "quest") {
				tempLocation = imageUtils.findButton("quest_blue", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
				newButtonName = "quest_blue"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton(
						"quest_red", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment =
						bypassGeneralAdjustment
					)
					newButtonName = "quest_red"
				}

			} else if (buttonName.lowercase() == "raid") {
				tempLocation = imageUtils.findButton("raid_flat", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
				newButtonName = "raid_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton(
						"raid_bouncing", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment =
						bypassGeneralAdjustment
					)
					newButtonName = "raid_bouncing"
				}

			} else if (buttonName.lowercase() == "coop_start") {
				tempLocation = imageUtils.findButton(
					"coop_start_flat", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment =
					bypassGeneralAdjustment
				)
				newButtonName = "coop_start_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton(
						"coop_start_faded", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment =
						bypassGeneralAdjustment
					)
					newButtonName = "coop_start_faded"
				}

			} else if (buttonName.lowercase() == "event_special_quest") {
				tempLocation = imageUtils.findButton(
					"event_special_quest_flat", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment =
					bypassGeneralAdjustment
				)
				newButtonName = "event_special_quest_flat"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton(
						"event_special_quest_bouncing", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment =
						bypassGeneralAdjustment
					)
					newButtonName = "event_special_quest_bouncing"
				}

			} else if (buttonName.lowercase() == "world") {
				tempLocation = imageUtils.findButton("world", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
				newButtonName = "world"

				if (tempLocation == null) {
					tempLocation = imageUtils.findButton("world2", tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
					newButtonName = "world2"
				}
			} else {
				tempLocation = imageUtils.findButton(buttonName, tries = tries, customConfidence = customConfidence, suppressError = suppressError, bypassGeneralAdjustment = bypassGeneralAdjustment)
			}
		}

		return if (tempLocation != null) {
			if (configData.enableDelayTap) {
				val newDelay: Double = ((configData.delayTapMilliseconds - 100)..(configData.delayTapMilliseconds + 100)).random().toDouble() / 1000
				if (configData.debugMode) MessageLog.printToLog("[DEBUG] Adding an additional delay of ${newDelay}s...", tag)
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
		if (imageUtils.confirmLocation("captcha", bypassGeneralAdjustment = true)) {
			throw (Exception("[CAPTCHA] CAPTCHA has been detected! Stopping the bot now."))
		} else {
			MessageLog.printToLog("\n[CAPTCHA] CAPTCHA not detected.", tag)
		}
	}

	/**
	 * Execute a delay after every run completed based on user settings from config.yaml.
	 */
	private fun delayBetweenRuns() {
		if (configData.enableDelayBetweenRuns) {
			MessageLog.printToLog("\n[INFO] Now waiting for ${configData.delayBetweenRuns} seconds as the resting period. Please do not navigate from the current screen.", tag)

			wait(configData.delayBetweenRuns.toDouble())
		} else if (configData.enableRandomizedDelayBetweenRuns) {
			val newSeconds = Random().nextInt(configData.delayBetweenRunsUpperBound - configData.delayBetweenRunsLowerBound) + configData.delayBetweenRunsLowerBound
			MessageLog.printToLog(
				"\n[INFO] Given the bounds of (${configData.delayBetweenRunsLowerBound}, ${configData.delayBetweenRunsUpperBound}), bot will now wait for $newSeconds seconds as a resting " +
						"period. Please do not navigate from the current screen.", tag
			)

			wait(newSeconds.toDouble())
		}

		MessageLog.printToLog("\n[INFO] Resting period complete.", tag)
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
		val unformattedSummonList = optionalSummonList.ifEmpty {
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

		MessageLog.printToLog("Summon list: $newSummonList", tag)
		MessageLog.printToLog("Summon Element list: $summonElementList", tag)

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
				MessageLog.printToLog("[INFO] Bypassing procedure to reset Summons. Reloading page and selecting the very first one now...", tag)

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
		MessageLog.printToLog("[INFO] Resetting Summons...", tag)

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
					MessageLog.printToLog("[SUCCESS] Summons have now been refreshed.", tag)
				}
			}
		} else {
			MessageLog.printToLog("[WARNING] Failed to reset Summons as the 'Gameplay Extras' button is not visible.", tag)
		}
	}

	/**
	 * Selects the specified Group and Party. It will then start the mission.
	 *
	 * @param optionalGroupNumber Overrides the Group Number. Defaults to the one selected for Farming Mode.
	 * @param optionalPartyNumber Overrides the Party Number. Defaults to the one selected for Farming Mode.
	 * @param tries Number of tries to select a Set before failing. Defaults to 3.
	 * @param bypassFirstRun Determines if the bot should reselect the party in subsequent runs. Defaults to False.
	 * @return True if the mission was successfully started. False otherwise.
	 */
	fun selectPartyAndStartMission(optionalGroupNumber: Int = 0, optionalPartyNumber: Int = 0, tries: Int = 3, bypassFirstRun: Boolean = false): Boolean {
		if (partySelectionFirstRun || bypassFirstRun) {
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
			if (configData.farmingMode == "Arcarum Sandbox") {
				while (setLocation == null) {
					setLocation = imageUtils.findButton("party_set_extra", tries = 10)
					if (setLocation == null) {
						numberOfTries -= 1

						if (numberOfTries <= 0) {
							throw (Resources.NotFoundException("Could not find Set Extra."))
						}
					}
				}
			} else {
				if (configData.farmingMode == "Raid" && imageUtils.findButton("party_set_extra", tries = 3) != null) {
					MessageLog.printToLog("[INFO] Skipping Set Selection due to Raid only allowing parties from the Extra category.", tag)
				} else if (selectedGroupNumber < 8) {
					while (setLocation == null) {
						setLocation = imageUtils.findButton("party_set_a", customConfidence = 0.90)
						if (setLocation == null) {
							numberOfTries -= 1
							if (tries <= 0) {
								throw Exception("Could not find Set A")
							}

							// See if the user had Set B active instead of Set A if matching failed.
							findAndClickButton("party_set_b", customConfidence = 0.90)
						}
					}
				} else {
					while (setLocation == null) {
						setLocation = imageUtils.findButton("party_set_b", customConfidence = 0.90)
						if (setLocation == null) {
							numberOfTries -= 1
							if (tries <= 0) {
								throw Exception("Could not find Set B")
							}

							// See if the user had Set A active instead of Set A if matching failed.
							findAndClickButton("party_set_a", customConfidence = 0.90)
						}
					}
				}
			}

			if (setLocation == null) {
				throw (NullPointerException("The set location was set to null for party selection."))
			}

			// Select the Group.
			var equation: Double = if (!imageUtils.isTablet) {
				if (selectedGroupNumber == 1) {
					if (imageUtils.is720p) {
						537.0
					} else {
						787.0
					}
				} else {
					if (imageUtils.is720p) {
						537.0 - (93 * (selectedGroupNumber - 1))
					} else {
						787.0 - (140 * (selectedGroupNumber - 1))
					}
				}
			} else {
				if (!imageUtils.isTabletLandscape) {
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
				if (imageUtils.is720p) {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 80.0, "template_group")
				} else {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 140.0, "template_group")
				}
			} else {
				if (!imageUtils.isTabletLandscape) {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 90.0, "template_group")
				} else {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 70.0, "template_group")
				}
			}

			wait(2.0)

			// Select the Party.
			equation = if (!imageUtils.isTablet) {
				if (selectedPartyNumber == 1) {
					if (imageUtils.is720p) {
						468.0
					} else {
						690.0
					}
				} else {
					if (imageUtils.is720p) {
						468.0 - (85 * (selectedPartyNumber - 1))
					} else {
						690.0 - (130 * (selectedPartyNumber - 1))
					}
				}
			} else {
				if (!imageUtils.isTabletLandscape) {
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
				if (imageUtils.is720p) {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 490.0, "template_party")
				} else {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 740.0, "template_party")
				}
			} else {
				if (!imageUtils.isTabletLandscape) {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 540.0, "template_party")
				} else {
					gestureUtils.tap(setLocation.x - equation, setLocation.y + 425.0, "template_party")
				}
			}

			wait(2.0)

			partySelectionFirstRun = false
		}

		MessageLog.printToLog("[SUCCESS] Selected Group and Party successfully.", tag)

		// Start the mission by clicking "OK".
		findAndClickButton("ok")
		wait(1.0)

		// Detect if a "This raid battle has already ended" popup appeared.
		if (configData.farmingMode == "Raid" && findAndClickButton("ok")) {
			MessageLog.printToLog("[WARNING] Raid unfortunately just ended. Backing out now...", tag)
			wait(3.0)
			return false
		}

		wait(3.0)
		return true
	}

	/**
	 * Check if the user has enabled the auto-restore option for AP.
	 *
	 */
	fun checkAP() {
		wait(3.0)

		if (imageUtils.confirmLocation("not_enough_ap", tries = 2)) {
			throw Exception("AP auto-restore check failed. Please enable the auto-restore option in the in-game settings according to the GitHub instructions.")
		} else {
			MessageLog.printToLog("\n[INFO] AP auto-restore check passed. Continuing to Party Selection...", tag)
		}
	}

	/**
	 * Check if the user has enabled the auto-restore option for EP.
	 *
	 */
	fun checkEP() {
		wait(3.0)

		if (configData.farmingMode == "Raid" && imageUtils.confirmLocation("not_enough_ep", tries = 2)) {
			throw Exception("AP auto-restore check failed. Please enable the auto-restore option in the in-game settings according to the GitHub instructions.")
		} else {
			MessageLog.printToLog("\n[INFO] AP auto-restore check passed. Continuing to Party Selection...", tag)
		}
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
	 * @param isDefender Skip the incrementation of runs attempted if this was a Defender. Defaults to False.
	 */
	fun collectLoot(
		isCompleted: Boolean, isPendingBattle: Boolean = false, isEventNightmare: Boolean = false, skipInfo: Boolean = false, skipPopupCheck: Boolean = false, isDefender: Boolean =
			false
	) {
		var amountGained = 0

		// Close all popups until the bot reaches the Loot Collected screen.
		if (!skipPopupCheck) {
			var lootCollectionTries = 30
			while (!imageUtils.confirmLocation("loot_collected", tries = 1, disableAdjustment = true)) {
				lootCollectionTries -= 1
				if (lootCollectionTries <= 0) {
					throw Exception("Unable to progress in the Loot Collection process.")
				}

				findAndClickButton("ok", tries = 1, suppressError = true)
				findAndClickButton("close", tries = 1, suppressError = true)
				findAndClickButton("cancel", tries = 1, suppressError = true)
				findAndClickButton("new_extended_mastery_level", tries = 1, suppressError = true)

				if (imageUtils.confirmLocation("no_loot", tries = 1, suppressError = true, disableAdjustment = true)) {
					return
				}

				if (configData.debugMode) {
					MessageLog.printToLog("[DEBUG] Have not detected the Loot Collection screen yet...", tag)
				}
			}
		}

		// Now that the bot is at the Loot Collected screen, detect any user-specified items.
		if (isCompleted && !isPendingBattle && !isEventNightmare && !isDefender) {
			MessageLog.printToLog("\n[INFO] Detecting if any user-specified loot dropped this run...", tag)
			amountGained = if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(configData.itemName)) {
				imageUtils.findFarmedItems(configData.itemName)
			} else {
				1
			}

			amountOfRuns += 1
			itemAmountFarmed += amountGained
		} else if (isPendingBattle) {
			MessageLog.printToLog("\n[INFO] Detecting if any user-specified loot dropped this Pending Battle...", tag)
			amountGained = if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(configData.itemName)) {
				imageUtils.findFarmedItems(configData.itemName)
			} else {
				0
			}

			itemAmountFarmed += amountGained
		}

		// If there were item drops detected and the user opt in to sending their result to Granblue Automation Statistics, then have the frontend send the API request.
		if (amountGained != 0 && configData.enableOptInAPI) {
			if (isPendingBattle) {
				sendApiResult(amountGained, 0L)
			} else {
				sendApiResult(amountGained, configData.combatElapsedTime)
			}
		}

		if (isCompleted && !isPendingBattle && !isEventNightmare && !skipInfo && !isDefender) {
			if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(configData.itemName)) {
				MessageLog.printToLog("\n********************", tag)
				MessageLog.printToLog("********************", tag)
				MessageLog.printToLog("[INFO] Farming Mode: ${configData.farmingMode}", tag)
				MessageLog.printToLog("[INFO] Mission: ${configData.missionName}", tag)
				MessageLog.printToLog("[INFO] Summons: ${configData.summonList}", tag)
				MessageLog.printToLog("[INFO] # of ${configData.itemName} gained this run: $amountGained", tag)
				MessageLog.printToLog("[INFO] # of ${configData.itemName} gained in total: ${itemAmountFarmed}/${configData.itemAmount}", tag)
				MessageLog.printToLog("[INFO] # of runs completed: $amountOfRuns", tag)
				MessageLog.printToLog("********************", tag)
				MessageLog.printToLog("********************", tag)

				// Construct the message for the Discord private DM.
				if (amountGained > 0) {
					val discordString = if (itemAmountFarmed >= configData.itemAmount) {
						"> ${amountGained}x __${configData.itemName}__ gained this run: **[${itemAmountFarmed - amountGained} / ${configData.itemAmount}]** -> " +
								"**[${itemAmountFarmed} / ${configData.itemAmount}]** :white_check_mark:"
					} else {
						"> ${amountGained}x __${configData.itemName}__ gained this run: **[${itemAmountFarmed - amountGained} / ${configData.itemAmount}]** -> " +
								"**[${itemAmountFarmed} / ${configData.itemAmount}]**"
					}

					DiscordUtils.queue.add(discordString)
				}
			} else {
				MessageLog.printToLog("\n********************", tag)
				MessageLog.printToLog("********************", tag)
				MessageLog.printToLog("[INFO] Farming Mode: ${configData.farmingMode}", tag)
				MessageLog.printToLog("[INFO] Mission: ${configData.missionName}", tag)
				MessageLog.printToLog("[INFO] Summons: ${configData.summonList}", tag)
				MessageLog.printToLog("[INFO] # of runs completed: $amountOfRuns / ${configData.itemAmount}", tag)
				MessageLog.printToLog("********************", tag)
				MessageLog.printToLog("********************", tag)

				// Construct the message for the Discord private DM.
				val discordString = if (amountOfRuns >= configData.itemAmount) {
					"> Runs completed for __${configData.missionName}__: **[${amountOfRuns - 1} / ${configData.itemAmount}]** -> **[${amountOfRuns} / ${configData.itemAmount}]** :white_check_mark:"
				} else {
					"> Runs completed for __${configData.missionName}__: **[${amountOfRuns - 1} / ${configData.itemAmount}]** -> **[${amountOfRuns} / ${configData.itemAmount}]**"
				}

				DiscordUtils.queue.add(discordString)
			}
		} else if (isPendingBattle && amountGained > 0 && !skipInfo) {
			if (!listOf("EXP", "Angel Halo Weapons", "Repeated Runs").contains(configData.itemName)) {
				MessageLog.printToLog("\n********************", tag)
				MessageLog.printToLog("********************", tag)
				MessageLog.printToLog("[INFO] Farming Mode: ${configData.farmingMode}", tag)
				MessageLog.printToLog("[INFO] Mission: ${configData.missionName}", tag)
				MessageLog.printToLog("[INFO] Summons: ${configData.summonList}", tag)
				MessageLog.printToLog("[INFO] # of ${configData.itemName} gained from this Pending Battle: $amountGained", tag)
				MessageLog.printToLog("[INFO] # of ${configData.itemName} gained in total: ${itemAmountFarmed}/${configData.itemAmount}", tag)
				MessageLog.printToLog("[INFO] # of runs completed: $amountOfRuns", tag)
				MessageLog.printToLog("********************", tag)
				MessageLog.printToLog("********************", tag)

				// Construct the message for the Discord private DM.
				val discordString = if (itemAmountFarmed >= configData.itemAmount) {
					"> ${amountGained}x __${configData.itemName}__ gained from this Pending Battle: **[${itemAmountFarmed - amountGained} / ${configData.itemAmount}]** -> " +
							"**[${itemAmountFarmed} / ${configData.itemAmount}]** :white_check_mark:"
				} else {
					"> ${amountGained}x __${configData.itemName}__ gained from this Pending Battle: **[${itemAmountFarmed - amountGained} / ${configData.itemAmount}]** -> " +
							"**[${itemAmountFarmed} / ${configData.itemAmount}]**"
				}

				DiscordUtils.queue.add(discordString)
			}
		} else if (isDefender) {
			configData.engagedDefenderBattle = false
			configData.numberOfDefeatedDefenders += 1
			MessageLog.printToLog("\n********************", tag)
			MessageLog.printToLog("********************", tag)
			MessageLog.printToLog("[INFO] Farming Mode: ${configData.farmingMode}", tag)
			MessageLog.printToLog("[INFO] Mission: ${configData.missionName}", tag)
			MessageLog.printToLog("[INFO] Summons: ${configData.summonList}", tag)
			MessageLog.printToLog("[INFO] Amount of Defenders defeated: ${configData.numberOfDefeatedDefenders}/${configData.numberOfDefenders}", tag)
			MessageLog.printToLog("********************", tag)
			MessageLog.printToLog("********************", tag)
		}

		return
	}

	/**
	 * Detect any popups and attempt to close them all with the final destination being the Summon Selection screen.
	 *
	 * @return True if there was a Nightmare mission detected or some other popup appeared that requires the navigation process to be restarted.
	 */
	fun checkForPopups(): Boolean {
		MessageLog.printToLog("\n[INFO] Now beginning process to check for popups...", tag)

		var checkPopupTries = 30
		while (!imageUtils.confirmLocation("select_a_summon", tries = 1, suppressError = true)) {
			checkPopupTries -= 1
			if (checkPopupTries <= 0) {
				throw Exception("Failed to progress in the Check for Popups process...")
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

			if (imageUtils.findButton("bottom_of_summon_selection", tries = 1, suppressError = true) != null) {
				MessageLog.printToLog("[INFO] Detected bottom of Summon Selection screen. Reloading now to continue with process to check for popups...", tag)
				findAndClickButton("reload")
			}

			if (configData.debugMode) {
				MessageLog.printToLog("[DEBUG] Have not detected the Support Summon Selection screen yet...", tag)
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
			MessageLog.printToLog("[INFO] Clearing this Pending Battle...", tag)
			wait(2.0)

			if (imageUtils.confirmLocation("no_loot", disableAdjustment = true)) {
				MessageLog.printToLog("[INFO] No loot can be collected. Backing out...", tag)

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
		} else {
			MessageLog.printToLog("[INFO] No more Pending Battles left to claim.", tag)
		}

		return false
	}

	/**
	 * Check and collect any pending rewards and free up slots for the bot to join more Raids.
	 *
	 * @return True if Pending Battles were detected. False otherwise.
	 */
	fun checkPendingBattles(): Boolean {
		MessageLog.printToLog("\n[INFO] Starting process of checking for Pending Battles...", tag)

		if (configData.enablePendingBattleAdjustment) {
			wait(configData.adjustBeforePendingBattle.toDouble())
		} else {
			wait(1.0)
		}

		// Check for the "Check your Pending Battles" popup when navigating to the Quest screen or attempting to join a raid when there are 6
		// Pending Battles or check if the "Play Again" button is covered by the "Pending Battles" button for any other Farming Mode.
		if (imageUtils.confirmLocation("check_your_pending_battles", tries = 2, bypassGeneralAdjustment = true) ||
			imageUtils.confirmLocation("pending_battles", tries = 2, bypassGeneralAdjustment = true) ||
			findAndClickButton("quest_results_pending_battles", tries = 2, bypassGeneralAdjustment = true)
		) {
			MessageLog.printToLog("[INFO] Found Pending Battles that need collecting from.", tag)
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

			MessageLog.printToLog("[INFO] Pending Battles have been cleared.", tag)
			return true
		}

		MessageLog.printToLog("[INFO] No Pending Battles needed to be cleared.", tag)
		return false
	}

	/**
	 * Prints a formatted message as a way to send the event back to the frontend in order to have it send the result to the database.
	 *
	 * @param amount Amount of items detected for this run.
	 * @param elapsedTime Elapsed time for Combat Mode from start to finish.
	 */
	private fun sendApiResult(amount: Int, elapsedTime: Long) {
		var formattedElapsedTime = elapsedTime.toString()
		if (elapsedTime != 0L) {
			formattedElapsedTime = String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(elapsedTime),
				TimeUnit.MILLISECONDS.toMinutes(elapsedTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(elapsedTime)),
				TimeUnit.MILLISECONDS.toSeconds(elapsedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedTime))
			)
		}

		MessageLog.printToLog("\nSending API request to Granblue Automation Statistics...", tag)
		MessageLog.printToLog("API-RESULT|${configData.itemName}|${amount}|${formattedElapsedTime}", tag)
	}

	/**
	 * Start Farming Mode with the provided parameters from the user's choices in the settings.
	 *
	 * @return True if Farming Mode completed successfully. False otherwise.
	 */
	fun start(): Boolean {
		MessageLog.printToLog("[INFO] Device dimensions: ${SharedData.displayHeight}x${SharedData.displayWidth}\n", tag)

		// Throw an Exception if the user selected Coop or Arcarum that reset Summons and the user started the bot without selecting new Summons.
		if (configData.farmingMode != "Coop" && configData.farmingMode != "Arcarum" && configData.summonList[0] == "") {
			throw Exception("You have no summons selected for this Farming Mode.")
		}

		if (configData.enableTestForHomeScreen) {
			goBackHome(confirmLocationCheck = true, testMode = true)
			return true
		}

		// Double check if the AccessibilityService is alive or not. Do not continue if it is dead.
		gestureUtils = if (MyAccessibilityService.checkStatus(myContext)) {
			MyAccessibilityService.getInstance()
		} else {
			throw UninitializedPropertyAccessException(
				"Accessibility Service appears to be dead. Restart it by turning it on and off in the Accessibility Settings until you see a success popup and try again."
			)
		}

		if (configData.itemName != "EXP") {
			MessageLog.printToLog("\n####################", tag)
			MessageLog.printToLog("####################", tag)
			MessageLog.printToLog("[FARM] Starting Farming Mode for ${configData.farmingMode}.", tag)
			MessageLog.printToLog("[FARM] Farming ${configData.itemAmount}x ${configData.itemName} at ${configData.missionName}.", tag)
			MessageLog.printToLog("####################", tag)
			MessageLog.printToLog("####################", tag)
		} else {
			MessageLog.printToLog("\n####################", tag)
			MessageLog.printToLog("####################", tag)
			MessageLog.printToLog("[FARM] Starting Farming Mode for ${configData.farmingMode}.", tag)
			MessageLog.printToLog("[FARM] Doing ${configData.itemAmount}x runs for ${configData.itemName} at ${configData.missionName}.", tag)
			MessageLog.printToLog("####################", tag)
			MessageLog.printToLog("####################", tag)
		}

		if (configData.farmingMode == "Raid") {
			twitterRoomFinder.connect()
		}

		var firstRun = true
		while (itemAmountFarmed < configData.itemAmount) {
			when (configData.farmingMode) {
				"Quest" -> {
					quest.start(firstRun)
				}
				"Special" -> {
					special.start(firstRun)
				}
				"Coop" -> {
					coop.start(firstRun)
				}
				"Raid" -> {
					raid.start()
				}
				"Event", "Event (Token Drawboxes)" -> {
					event.start(firstRun)
				}
				"Rise of the Beasts" -> {
					riseOfTheBeasts.start(firstRun)
				}
				"Guild Wars" -> {
					guildWars.start(firstRun)
				}
				"Dread Barrage" -> {
					dreadBarrage.start(firstRun)
				}
				"Proving Grounds" -> {
					provingGrounds.start(firstRun)
				}
				"Xeno Clash" -> {
					xenoClash.start(firstRun)
				}
				"Arcarum" -> {
					arcarum.start()
				}
				"Arcarum Sandbox" -> {
					arcarumSandbox.start()
				}
				"Generic" -> {
					generic.start()
				}
			}

			if (itemAmountFarmed < configData.itemAmount) {
				// Generate a resting period if the user enabled it.
				delayBetweenRuns()
				firstRun = false
			}
		}

		MessageLog.printToLog("\n********************", tag)
		MessageLog.printToLog("********************", tag)
		MessageLog.printToLog("[INFO] Farming Mode has ended", tag)
		MessageLog.printToLog("********************", tag)
		MessageLog.printToLog("********************", tag)

		MessageLog.printToLog("\nTotal Runtime: ${System.currentTimeMillis() - startTime}ms", tag)

		return true
	}
}