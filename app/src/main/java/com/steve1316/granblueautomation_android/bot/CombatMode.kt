package com.steve1316.granblueautomation_android.bot

import org.opencv.core.Point
import java.util.*

/**
 * This class handles the Combat Mode and offers helper functions to assist it.
 */
class CombatMode(private val game: Game) {
	private val TAG: String = "GAA_CombatMode"
	
	private var retreatCheckFlag = false
	private var attackButtonLocation: Point? = null
	
	private var healingItemCommands = listOf("usegreenpotion.target(1)", "usegreenpotion.target(2)", "usegreenpotion.target(3)",
		"usegreenpotion.target(4)", "usebluepotion", "usefullelixir", "usesupportpotion", "useclarityherb.target(1)", "useclarityherb.target(2)",
		"useclarityherb.target(3)", "useclarityherb.target(4)", "userevivalpotion")
	
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
	 */
	private fun useCombatHealingItem(command: String) {
		var target = 0
		
		// Grab the healing command
		val healingItemCommandList = command.split(".")
		val healingItemCommand = healingItemCommandList[0]
		healingItemCommandList.drop(1)
		
		// Parse the target if the user is using a Green Potion or a Clarity Herb.
		if((healingItemCommand == "usegreenpotion" || healingItemCommand == "useclarityherb") && healingItemCommandList[0]
				.contains("target")) {
			when (healingItemCommandList[0]) {
				"target(1)" -> {
					target = 1
				}
				"target(2)" -> {
					target = 2
				}
				"target(3)" -> {
					target = 3
				}
				"target(4)" -> {
					target = 4
				}
			}
		}
		
		// Open up the "Use Item" popup.
		game.findAndClickButton("heal")
		
		// Format the item name.
		val formattedCommand = command.toLowerCase(Locale.ROOT).replace(" ", "_")
		
		// Tap the specified item.
		if(formattedCommand == "usebluepotion" || formattedCommand == "usesupportpotion") {
			// Blue and Support Potions share the same image but they are at different positions on the screen.
			val potionLocations = game.imageUtils.findAll(formattedCommand)
			if(formattedCommand == "usebluepotion") {
				game.gestureUtils.tap(potionLocations[0].x, potionLocations[0].y)
			} else {
				game.gestureUtils.tap(potionLocations[1].x, potionLocations[1].y)
			}
		} else {
			game.findAndClickButton(formattedCommand)
		}
		
		// After the initial popup vanishes to reveal a new popup, either select a Character target or tap the confirmation button.
		// TODO: Flesh out ImageUtils' waitVanish().
		if(game.imageUtils.waitVanish("tap_the_item_to_use", timeout = 5)) {
			when (formattedCommand) {
				"usegreenpotion" -> {
					game.printToLog("[COMBAT] Using Green Potion on Character $target.", MESSAGE_TAG = TAG)
					selectCharacter(target)
				}
				"usebluepotion" -> {
					game.printToLog("[COMBAT] Using Blue Potion on the whole Party.", MESSAGE_TAG = TAG)
					game.findAndClickButton("use")
				}
				"usefullelixir" -> {
					game.printToLog("[COMBAT] Using Full Elixir to revive and gain Full Charge.", MESSAGE_TAG = TAG)
					game.findAndClickButton("ok")
				}
				"usesupportpotion" -> {
					game.printToLog("[COMBAT] Using Support Potion on the whole Party.", MESSAGE_TAG = TAG)
					game.findAndClickButton("ok")
				}
				"useclaritypotion" -> {
					game.printToLog("[COMBAT] Using Clarity Herb on Character $target.", MESSAGE_TAG = TAG)
					selectCharacter(target)
				}
				"userevivalpotion" -> {
					game.printToLog("[COMBAT] Using Revival Potion to revive the whole Party.", MESSAGE_TAG = TAG)
					game.findAndClickButton("ok")
				}
			}
			
			// Wait for the healing animation to finish.
			game.wait(1.0)
			
			if(!game.imageUtils.confirmLocation("use_item", tries = 1)) {
				game.printToLog("[SUCCESS] Successfully used healing item.", MESSAGE_TAG = TAG)
			} else {
				game.printToLog("[WARNING] Was not able to use the healing item. Canceling it now.", MESSAGE_TAG = TAG)
			}
		} else {
			game.printToLog("[WARNING] Failed to tap on the item. Either it does not exist for this particular Mission or you ran out.",
				MESSAGE_TAG = TAG)
			game.findAndClickButton("cancel")
		}
	}
	
	/**
	 * Request backup during Combat mode for this Raid.
	 */
	private fun requestBackup() {
		game.printToLog("[COMBAT] Now requesting Backup for this Raid.", MESSAGE_TAG = TAG)
		
		// Scroll the screen down a little bit to have the "Request Backup" button visible on all screen sizes. Then tap the button.
		game.gestureUtils.swipe(500f, 1000f, 500f, 400f)
		game.findAndClickButton("request_backup")
		
		game.wait(1.0)
		
		// Find the location of the "Cancel" button and tap the "Request Backup" button to the right of it. This is to ensure that the bot always
		// taps the button no matter the appearance of the "Request Backup" button, which changes frequently.
		val cancelButtonLocation = game.imageUtils.findButton("cancel")
		if(cancelButtonLocation != null) {
			game.gestureUtils.tap(cancelButtonLocation.x + 500, cancelButtonLocation.y)
		}
		
		game.wait(1.0)
		
		// If requesting backup was successful, close the popup.
		if(game.imageUtils.confirmLocation("request_backup_success", tries = 1)) {
			game.printToLog("[COMBAT] Successfully requested Backup.", MESSAGE_TAG = TAG)
			game.findAndClickButton("ok")
		} else {
			game.printToLog("[COMBAT] Unable to request Backup. Possibly because it is still on cooldown.", MESSAGE_TAG = TAG)
			game.findAndClickButton("cancel")
		}
		
		// Now scroll back up to reset the view.
		game.gestureUtils.swipe(500f, 400f, 500f, 1000f)
	}
	
	/**
	 * Request backup during Combat mode for this Raid by using the Twitter feature.
	 */
	private fun tweetBackup() {
		game.printToLog("[COMBAT] Now requesting Backup for this Raid via Twitter.", MESSAGE_TAG = TAG)
		
		// Scroll the screen down a little bit to have the "Request Backup" button visible on all screen sizes. Then tap the button.
		game.gestureUtils.swipe(500f, 1000f, 500f, 400f)
		game.findAndClickButton("request_backup")
		
		game.wait(1.0)
		
		// Now tap the "Tweet" button.
		game.findAndClickButton("request_backup_tweet")
		game.wait(1.0)
		game.findAndClickButton("ok")
		
		game.wait(1.0)
		
		// If requesting backup was successful, close the popup.
		if(game.imageUtils.confirmLocation("request_backup_tweet_success", tries = 1)) {
			game.printToLog("[COMBAT] Successfully requested Backup via Twitter.", MESSAGE_TAG = TAG)
			game.findAndClickButton("ok")
		} else {
			game.printToLog("[COMBAT] Unable to request Backup via Twitter. Possibly because it is still on cooldown.", MESSAGE_TAG = TAG)
			game.findAndClickButton("cancel")
		}
		
		// Now scroll back up to reset the view.
		game.gestureUtils.swipe(500f, 400f, 500f, 1000f)
	}
	
	/**
	 * Selects the portrait of the specified character during Combat Mode.
	 *
	 * @param characterNumber The character that needs to be selected.
	 */
	private fun selectCharacter(characterNumber: Int) {
		val x = when (characterNumber) {
			1 -> {
				attackButtonLocation!!.x - 715.0
			}
			2 -> {
				attackButtonLocation!!.x - 545.0
			}
			3 -> {
				attackButtonLocation!!.x - 375.0
			}
			4 -> {
				attackButtonLocation!!.x - 205.0
			}
			else -> {
				game.printToLog("[WARNING] Invalid command received for selectCharacter()", MESSAGE_TAG = TAG)
				return
			}
		}
		
		val y = attackButtonLocation!!.y + 290.0
		
		// Double tap the Character portrait to avoid any popups caused by other Raid participants.
		game.gestureUtils.tap(x, y, ignoreWait = true)
		game.gestureUtils.tap(x, y)
	}
	
	/**
	 * Activate the specified skill for the already selected character.
	 *
	 * @param characterNumber The character whose skill needs to be used.
	 * @param skillNumber The skill that needs to be used.
	 */
	private fun useCharacterSkill(characterNumber: Int, skillNumber: Int) {
		val x = when (characterNumber) {
			1 -> {
				game.printToLog("[COMBAT] Character $characterNumber uses Skill 1.", MESSAGE_TAG = TAG)
					attackButtonLocation!!.x - 485.0
			}
			2 -> {
				game.printToLog("[COMBAT] Character $characterNumber uses Skill 2.", MESSAGE_TAG = TAG)
					attackButtonLocation!!.x - 295.0
			}
			3 -> {
				game.printToLog("[COMBAT] Character $characterNumber uses Skill 3.", MESSAGE_TAG = TAG)
					attackButtonLocation!!.x - 105.0
			}
			else -> {
				game.printToLog("[COMBAT] Character $characterNumber uses Skill 4.", MESSAGE_TAG = TAG)
					attackButtonLocation!!.x + 85.0
			}
		}
		
		val y = attackButtonLocation.y + 395.0
		
		// Double tap the Skill to avoid any popups caused by other Raid participants.
		game.gestureUtils.tap(x, y, ignoreWait = true)
		game.gestureUtils.tap(x, y)
	}
	
	/**
	 * Wait for a maximum of 20 seconds until the bot sees either the "Attack" or the "Next" button before starting a new turn.
	 */
	private fun waitForAttack() {
		var tries = 10
		
		while((!retreatCheckFlag && game.imageUtils.findButton("attack", tries = 1, suppressError = true) == null) ||
			(!retreatCheckFlag && game.imageUtils.findButton("next", tries = 1, suppressError = true) == null)) {
			// Stagger the checks for dialog popups during Combat Mode.
			if(tries % 2 == 0) {
				findCombatDialog()
			}
			
			game.wait(1.0)
			
			tries -= 1
			if(tries <= 0 || game.imageUtils.findButton("attack", tries = 1, suppressError = true) != null || game.imageUtils.findButton("next",
					tries = 1, suppressError = true) != null) {
				break
			}
			
			// Check if the Party wiped after attacking.
			partyWipeCheck()
			
			game.wait(1.0)
		}
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