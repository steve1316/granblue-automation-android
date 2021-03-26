package com.steve1316.granblueautomation_android.bot

import org.opencv.core.Point
import java.util.*

/**
 * This class handles the Combat Mode and offers helper functions to assist it.
 */
class CombatMode(private val game: Game) {
	private val TAG: String = "GAA_CombatMode"
	
	private var retreatCheckFlag = false
	private lateinit var attackButtonLocation: Point
	
	/**
	 * Checks if the Party wiped during Combat Mode. Updates the retreat flag if so.
	 */
	private fun partyWipeCheck() {
		game.printToLog("[INFO] Checking to see if Party wiped.", MESSAGE_TAG = TAG)
		
		val partyWipeIndicatorLocation = game.imageUtils.findButton("party_wipe_indicator", tries = 1, suppressError = true)
		if(partyWipeIndicatorLocation != null) {
			// Tap on the blue indicator to get rid of the overlay.
			game.wait(2.0)
			game.gestureUtils.tap(partyWipeIndicatorLocation.x, partyWipeIndicatorLocation.y)
			
			if(game.farmingMode != "Raid" && game.farmingMode != "Dread Barrage" && game.imageUtils.confirmLocation("continue")) {
				// Close the popup that asks if you want to use a Full Elixir. Then tap the red "Retreat" button.
				game.printToLog("[WARNING] Party has wiped during Combat Mode. Retreating now...", MESSAGE_TAG = TAG)
				
				game.findAndClickButton("cancel")
				game.wait(1.0)
				game.findAndClickButton("retreat_confirmation")
				
				retreatCheckFlag = true
			} else if((game.farmingMode == "Raid" || game.farmingMode == "Dread Barrage") && game.imageUtils.confirmLocation("salute_participants")) {
				// Head back to the Home screen.
				game.goBackHome(confirmLocationCheck = true)
				
				retreatCheckFlag = true
			} else if(game.farmingMode == "Coop" && game.imageUtils.confirmLocation("salute_participants")) {
				// Salute the participants.
				game.printToLog("[WARNING] Party has wiped during Coop Combat Mode. Leaving the Coop Room...", MESSAGE_TAG = TAG)
				
				game.findAndClickButton("salute")
				game.wait(1.0)
				game.findAndClickButton("ok")
				
				// Then cancel the popup that asks if you want to use a Full Elixir and then tap the "Leave" button.
				game.findAndClickButton("cancel")
				game.wait(1.0)
				game.findAndClickButton("leave")
				
				retreatCheckFlag = true
			}
		} else {
			game.printToLog("[INFO] Party has not wiped.", MESSAGE_TAG = TAG)
		}
	}
	
	/**
	 * Find the total number of characters ready to Charge Attack.
	 *
	 * @return Total number of image matches found for charge attacks.
	 */
	private fun findChargeAttacks(): Int {
		return game.imageUtils.findAll("full_charge").size
	}
	
	/**
	 * Checks if there are any dialog popups during Combat Mode from either Lyria or Vyrn and close them.
	 */
	private fun findCombatDialog() {
		// Check for Lyria dialog popup first.
		var combatDialogLocation = game.imageUtils.findDialog("dialog_lyria", tries = 1)
		if(combatDialogLocation != null) {
			game.gestureUtils.tap(combatDialogLocation.x, combatDialogLocation.y)
			return
		}
		
		// Then check for Vyrn dialog popup next.
		combatDialogLocation = game.imageUtils.findDialog("dialog_vyrn", tries = 1)
		if(combatDialogLocation != null) {
			game.gestureUtils.tap(combatDialogLocation.x, combatDialogLocation.y)
			return
		}
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
	 * Wait for a maximum of 20 seconds until the bot sees either the "Attack" or the "Next" button before starting a new turn.
	 */
	private fun waitForAttack() {
		TODO("not yet implemented")
	}
	
	/**
	 * Start Combat Mode with the provided combat script.
	 *
	 * @param combatScript ArrayList of all the lines in the text file.
	 * @return True if Combat Mode ended successfully. False otherwise if the Party wiped or backed out without retreating.
	 */
	fun startCombatMode(combatScript: List<String>): Boolean {
		TODO("not yet implemented")
	}
}