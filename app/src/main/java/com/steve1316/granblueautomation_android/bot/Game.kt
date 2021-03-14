package com.steve1316.granblueautomation_android.bot

import android.content.Context
import android.util.Log
import com.steve1316.granblueautomation_android.MyAccessibilityService
import com.steve1316.granblueautomation_android.utils.ImageUtils
import com.steve1316.granblueautomation_android.utils.MediaProjectionService
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
			delay(seconds.toLong() * 1000)
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
			tempLocation = imageUtils.findButton("quest_blue", tries=tries, suppressError=suppressError)
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("quest_red", tries=tries, suppressError=suppressError)
			}
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("quest_blue_strike_time", tries=tries, suppressError=suppressError)
			}
			if(tempLocation == null) {
				tempLocation = imageUtils.findButton("quest_red_strike_time", tries=tries, suppressError=suppressError)
			}
		} else {
			tempLocation = imageUtils.findButton(buttonName, tries=tries, suppressError=suppressError)
		}
		
		var gestureConfirmation = false
		
		if(tempLocation != null) {
			gestureConfirmation = gestureUtils.tap(tempLocation.x, tempLocation.y)
			wait(1.0)
			return gestureConfirmation
		} else {
			return false
		}
	}
	
	/**
	 * Checks if the Party wiped during Combat Mode. Updates the retreat flag if so.
	 */
	private fun partyWipeCheck() {
		TODO("not yet implemented")
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
	 * @param summonList List of selected Summons sorted from greatest to least priority.
	 * @param summonElementList List of Summon Elements that correspond to the summonList.
	 * @return True if the Summon was found and selected. False otherwise.
	 */
	private fun selectSummon(summonList: ArrayList<String>, summonElementList: ArrayList<String>): Boolean {
		TODO("not yet implemented")
	}
	
	/**
	 * Reset the available Summons by starting and then retreating from an Old Lignoid Trial Battle.
	 */
	private fun resetSummons() {
		TODO("not yet implemented")
	}
	
	/**
	 * Selects the specified Group and Party. It will then start the mission.
	 *
	 * @param groupNumber The Group that the specified Party in in.
	 * @param partyNumber The specified Party to start the mission with.
	 * @param tries Number of tries to select a Set before failing. Defaults to 3.
	 * @return True if the mission was successfully started. False otherwise.
	 */
	private fun selectPartyAndStartMission(groupNumber: Int, partyNumber: Int, tries: Int = 3): Boolean {
		TODO("not yet implemented")
	}
	
	/**
	 * Find the total number of characters ready to Charge Attack.
	 *
	 * @return Total number of image matches found for charge attacks.
	 */
	private fun findChargeAttacks(): Int {
		TODO("not yet implemented")
	}
	
	/**
	 * Checks if there are any dialog popups during Combat Mode from either Lyria or Vyrn and close them.
	 */
	private fun findCombatDialog() {
		TODO("not yet implemented")
	}
	
	/**
	 * Checks if the user has available AP. If not, then it will refill it.
	 *
	 * @param useFullElixir Will use Full Elixir instead of Half Elixir. Defaults to false.
	 */
	fun checkAP(useFullElixir: Boolean = false) {
		TODO("not yet implemented")
	}
	
	/**
	 * Checks if the user has available EP. If not, then it will refill it.
	 *
	 * @param useSoulBalm Will use Soul Balm instead of Soul Berry. Defaults to false.
	 */
	fun checkEP(useSoulBalm: Boolean = false) {
		TODO("not yet implemented")
	}
	
	/**
	 * Selects the portrait of the specified character during Combat Mode.
	 *
	 * @param characterNumber The character that needs to be selected.
	 */
	private fun selectCharacter(characterNumber: Int) {
		TODO("not yet implemented")
	}
	
	/**
	 * Activate the specified skill for the already selected character.
	 *
	 * @param characterNumber The character whose skill needs to be used.
	 * @param skillNumber The skill that needs to be used.
	 */
	private fun useCharacterSkill(characterNumber: Int, skillNumber: Int) {
		TODO("not yet implemented")
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
	 * Wait for a maximum of 20 seconds until the bot sees either the "Attack" or the "Next" button before starting a new turn.
	 */
	private fun waitForAttack() {
		TODO("not yet implemented")
	}
	
	/**
	 * Uses the specified healing item during Combat Mode with an optional target if the item requires one.
	 *
	 * @param command The command for the healing item to use.
	 * @param target The character target for the item. This depends on what item it is. Defaults to 0.
	 */
	private fun useCombatHealingItem(command: String, target: Int = 0) {
		TODO("not yet implemented")
	}
	
	/**
	 * Request backup during Combat mode for this Raid.
	 */
	private fun requestBackup() {
		TODO("not yet implemented")
	}
	
	/**
	 * Request backup during Combat mode for this Raid by using the Twitter feature.
	 */
	private fun tweetBackup() {
		TODO("not yet implemented")
	}
	
	/**
	 * Start Combat Mode with the provided combat script.
	 *
	 * @param combatScript ArrayList of all the lines in the text file.
	 * @return True if Combat Mode ended successfully. False otherwise if the Party wiped or backed out without retreating.
	 */
	private fun startCombatMode(combatScript: ArrayList<String>): Boolean {
		TODO("not yet implemented")
	}
	
	/**
	 * Start Farming Mode with the provided parameters from the user's choices in the settings.
	 *
	 * @param itemName
	 * @param itemAmount
	 * @param farmingMode
	 * @param locationName
	 * @param missionName
	 * @param summonList
	 * @param summonElementList
	 * @param groupNumber
	 * @param partyNumber
	 */
	fun startFarmingMode(itemName: String, itemAmount: Int, farmingMode: String, locationName: String, missionName: String, summonList:
	ArrayList<String>, summonElementList: ArrayList<String>, groupNumber: Int, partyNumber: Int) {
		TODO("not yet implemented")
	}
}