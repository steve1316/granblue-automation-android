package com.steve1316.granblueautomation_android.bot

import org.opencv.core.Point
import java.util.*

/**
 * This class handles the Combat Mode and offers helper functions to assist it.
 */
class CombatMode(private val game: Game, private val debugMode: Boolean = false) {
	private val TAG: String = "GAA_CombatMode"
	
	private var healingItemCommands = listOf(
		"usegreenpotion.target(1)",
		"usegreenpotion.target(2)",
		"usegreenpotion.target(3)",
		"usegreenpotion.target(4)",
		"usebluepotion",
		"usefullelixir",
		"usesupportpotion",
		"useclarityherb.target(1)",
		"useclarityherb.target(2)",
		"useclarityherb.target(3)",
		"useclarityherb.target(4)",
		"userevivalpotion"
	)
	
	private var retreatCheckFlag = false
	private var attackButtonLocation: Point? = null
	
	/**
	 * Checks if the Party wiped during Combat Mode. Updates the retreat flag if so.
	 */
	private fun partyWipeCheck() {
		if (debugMode) {
			game.printToLog("[INFO] Checking to see if Party wiped.", MESSAGE_TAG = TAG)
		}
		
		val partyWipeIndicatorLocation = game.imageUtils.findButton("party_wipe_indicator", tries = 1, suppressError = true)
		if (partyWipeIndicatorLocation != null) {
			// Tap on the blue indicator to get rid of the overlay.
			game.gestureUtils.tap(partyWipeIndicatorLocation.x, partyWipeIndicatorLocation.y, "party_wipe_indicator")
			
			if (game.farmingMode != "Raid" && game.farmingMode != "Dread Barrage" && game.imageUtils.confirmLocation("continue")) {
				game.printToLog("[WARNING] Party has wiped during Combat Mode. Retreating now...", MESSAGE_TAG = TAG)
				
				// Close the popup that asks if you want to use a Full Elixir. Then tap the red "Retreat" button.
				game.findAndClickButton("cancel")
				game.wait(1.0)
				game.findAndClickButton("retreat_confirmation")
				retreatCheckFlag = true
			} else if ((game.farmingMode == "Raid" || game.farmingMode == "Dread Barrage")) {
				game.printToLog("[WARNING] Party has wiped during Combat Mode. Backing out now without retreating...", MESSAGE_TAG = TAG)
				
				// Head back to the Home screen.
				game.goBackHome(confirmLocationCheck = true)
				retreatCheckFlag = true
			} else if (game.farmingMode == "Coop" && game.imageUtils.confirmLocation("salute_participants")) {
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
		} else if (debugMode) {
			game.printToLog("[INFO] Party has not wiped.", MESSAGE_TAG = TAG)
		}
	}
	
	/**
	 * Checks if there are any dialog popups during Combat Mode from either Lyria or Vyrn and close them.
	 */
	private fun findCombatDialog() {
		// Check for Lyria dialog popup first.
		var combatDialogLocation = game.imageUtils.findDialog("dialog_lyria", tries = 1)
		if (combatDialogLocation != null) {
			game.gestureUtils.tap(combatDialogLocation.x, combatDialogLocation.y, "template_dialog")
			return
		}
		
		// Then check for Vyrn dialog popup next.
		combatDialogLocation = game.imageUtils.findDialog("dialog_vyrn", tries = 1)
		if (combatDialogLocation != null) {
			game.gestureUtils.tap(combatDialogLocation.x, combatDialogLocation.y, "template_dialog")
			return
		}
	}
	
	/**
	 * Uses the specified healing item during Combat Mode with an optional target if the item requires one.
	 *
	 * @param command The command for the healing item to use.
	 */
	private fun useCombatHealingItem(command: String) {
		if (debugMode) {
			game.printToLog("\n[DEBUG] Using item: $command", MESSAGE_TAG = TAG)
		}
		
		var target = 0
		
		// Grab the healing command.
		val healingItemCommandList = command.split(".")
		val healingItemCommand = healingItemCommandList[0]
		healingItemCommandList.drop(1)
		
		// Parse the target if the user is using a Green Potion or a Clarity Herb.
		if ((healingItemCommand == "usegreenpotion" || healingItemCommand == "useclarityherb") && healingItemCommandList[0].contains("target")) {
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
		if (formattedCommand == "usebluepotion" || formattedCommand == "usesupportpotion") {
			// Blue and Support Potions share the same image but they are at different positions on the screen.
			val potionLocations = game.imageUtils.findAll(formattedCommand)
			if (formattedCommand == "usebluepotion") {
				game.gestureUtils.tap(potionLocations[0].x, potionLocations[0].y, formattedCommand)
			} else {
				game.gestureUtils.tap(potionLocations[1].x, potionLocations[1].y, formattedCommand)
			}
		} else {
			game.findAndClickButton(formattedCommand)
		}
		
		// After the initial popup vanishes to reveal a new popup, either select a Character target or tap the confirmation button.
		if (game.imageUtils.waitVanish("tap_the_item_to_use", timeout = 5)) {
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
				"useclarityherb" -> {
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
			
			if (!game.imageUtils.confirmLocation("use_item", tries = 1)) {
				game.printToLog("[SUCCESS] Successfully used healing item.", MESSAGE_TAG = TAG)
			} else {
				game.printToLog("[WARNING] Was not able to use the healing item. Canceling it now.", MESSAGE_TAG = TAG)
			}
		} else {
			game.printToLog("[WARNING] Failed to tap on the item. Either it does not exist for this particular Mission or you ran out.", MESSAGE_TAG = TAG)
			game.findAndClickButton("cancel")
		}
	}
	
	/**
	 * Request backup during Combat mode for this Raid.
	 */
	private fun requestBackup() {
		game.printToLog("\n[COMBAT] Now requesting Backup for this Raid.", MESSAGE_TAG = TAG)
		
		// Scroll the screen down a little bit to have the "Request Backup" button visible on all screen sizes. Then tap the button.
		game.gestureUtils.swipe(500f, 1000f, 500f, 400f)
		game.findAndClickButton("request_backup")
		
		game.wait(1.0)
		
		// Find the location of the "Cancel" button and tap the "Request Backup" button to the right of it. This is to ensure that the bot always
		// taps the button no matter the appearance of the "Request Backup" button, which changes frequently.
		val cancelButtonLocation = game.imageUtils.findButton("cancel")
		if (cancelButtonLocation != null) {
			game.gestureUtils.tap(cancelButtonLocation.x + 500, cancelButtonLocation.y, "cancel")
		}
		
		game.wait(1.0)
		
		// If requesting backup was successful, close the popup.
		if (game.imageUtils.confirmLocation("request_backup_success", tries = 1)) {
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
		game.printToLog("\n[COMBAT] Now requesting Backup for this Raid via Twitter.", MESSAGE_TAG = TAG)
		
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
		if (game.imageUtils.confirmLocation("request_backup_tweet_success", tries = 1)) {
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
		game.gestureUtils.tap(x, y, "template_character", ignoreWait = true)
		game.gestureUtils.tap(x, y, "template_character")
	}
	
	/**
	 * Activate the specified Skill for the already selected Character.
	 *
	 * @param characterNumber The Character whose Skill needs to be used.
	 * @param skillCommandList The commands to be executed.
	 */
	private fun useCharacterSkill(characterNumber: Int, skillCommandList: List<String>) {
		while (skillCommandList.isNotEmpty()) {
			val x = when (skillCommandList[0]) {
				"useskill(1)" -> {
					game.printToLog("[COMBAT] Character $characterNumber uses Skill 1.", MESSAGE_TAG = TAG)
					attackButtonLocation!!.x - 485.0
				}
				"useskill(2)" -> {
					game.printToLog("[COMBAT] Character $characterNumber uses Skill 2.", MESSAGE_TAG = TAG)
					attackButtonLocation!!.x - 295.0
				}
				"useskill(3)" -> {
					game.printToLog("[COMBAT] Character $characterNumber uses Skill 3.", MESSAGE_TAG = TAG)
					attackButtonLocation!!.x - 105.0
				}
				"useskill(4)" -> {
					game.printToLog("[COMBAT] Character $characterNumber uses Skill 4.", MESSAGE_TAG = TAG)
					attackButtonLocation!!.x + 85.0
				}
				else -> {
					game.printToLog("[WARNING] Invalid command received for using the Character's Skill.", MESSAGE_TAG = TAG)
					return
				}
			}
			
			skillCommandList.drop(1)
			
			val y = attackButtonLocation!!.y + 395.0
			
			// Double tap the Skill to avoid any popups caused by other Raid participants.
			game.gestureUtils.tap(x, y, "template_skill", ignoreWait = true)
			game.gestureUtils.tap(x, y, "template_skill")
			
			// Check if the Skill requires a target.
			if (game.imageUtils.confirmLocation("use_skill", tries = 1, suppressError = true)) {
				val selectCharacterLocation = game.imageUtils.findButton("select_a_character")
				
				if (selectCharacterLocation != null) {
					game.printToLog("[COMBAT] Skill is awaiting a target.", MESSAGE_TAG = TAG)
					
					if (skillCommandList.isNotEmpty()) {
						// Select the targeted Character.
						when (skillCommandList[0]) {
							"target(1)" -> {
								game.printToLog("[COMBAT] Targeting Character 1 for Skill.", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(selectCharacterLocation.x - 195.0, selectCharacterLocation.y + 195.0, "template_target")
							}
							"target(2)" -> {
								game.printToLog("[COMBAT] Targeting Character 2 for Skill.", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(selectCharacterLocation.x + 5.0, selectCharacterLocation.y + 195.0, "template_target")
							}
							"target(3)" -> {
								game.printToLog("[COMBAT] Targeting Character 3 for Skill.", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(selectCharacterLocation.x - 210.0, selectCharacterLocation.y + 195.0, "template_target")
							}
							"target(4)" -> {
								game.printToLog("[COMBAT] Targeting Character 4 for Skill.", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(selectCharacterLocation.x - 195.0, selectCharacterLocation.y + 570.0, "template_target")
							}
							"target(5)" -> {
								game.printToLog("[COMBAT] Targeting Character 5 for Skill.", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(selectCharacterLocation.x + 5.0, selectCharacterLocation.y + 570.0, "template_target")
							}
							"target(6)" -> {
								game.printToLog("[COMBAT] Targeting Character 6 for Skill.", MESSAGE_TAG = TAG)
								game.gestureUtils.tap(selectCharacterLocation.x - 210.0, selectCharacterLocation.y + 570.0, "template_target")
							}
							else -> {
								game.printToLog("[WARNING] Invalid command received for Skill targeting.", MESSAGE_TAG = TAG)
								game.findAndClickButton("cancel")
							}
						}
						
						skillCommandList.drop(1)
					}
				} else if (game.imageUtils.confirmLocation("skill_unusable", tries = 1)) {
					game.printToLog("[COMBAT] Character is currently skill-sealed. Unable to execute command.", MESSAGE_TAG = TAG)
					game.findAndClickButton("cancel")
				}
			}
		}
		
		// Once all commands for the selected Character have been processed, tap the "Back" button to return.
		game.findAndClickButton("back")
	}
	
	/**
	 * Activate the specified Summon.
	 *
	 * @param summonCommand The command to be executed.
	 */
	private fun useSummon(summonCommand: String) {
		for (j in 1..6) {
			if (summonCommand == "summon($j)") {
				// Bring up the available Summons.
				game.printToLog("[COMBAT] Invoking Summon $j.", MESSAGE_TAG = TAG)
				game.findAndClickButton("summon")
				
				// Now tap on the specified Summon.
				when (j) {
					1 -> {
						game.gestureUtils.tap(attackButtonLocation!!.x - 715.0, attackButtonLocation!!.y + 300.0, "summon")
					}
					2 -> {
						game.gestureUtils.tap(attackButtonLocation!!.x - 545.0, attackButtonLocation!!.y + 300.0, "summon")
					}
					3 -> {
						game.gestureUtils.tap(attackButtonLocation!!.x - 375.0, attackButtonLocation!!.y + 300.0, "summon")
					}
					4 -> {
						game.gestureUtils.tap(attackButtonLocation!!.x - 205.0, attackButtonLocation!!.y + 300.0, "summon")
					}
					5 -> {
						game.gestureUtils.tap(attackButtonLocation!!.x - 35.0, attackButtonLocation!!.y + 300.0, "summon")
					}
					6 -> {
						game.gestureUtils.tap(attackButtonLocation!!.x + 135.0, attackButtonLocation!!.y + 300.0, "summon")
					}
				}
				
				if (game.imageUtils.confirmLocation("summon_details")) {
					val okButtonLocation = game.imageUtils.findButton("ok")
					
					if (okButtonLocation != null) {
						game.gestureUtils.tap(okButtonLocation.x, okButtonLocation.y, "ok")
						
						// Now wait for the Summon animation to complete.
						game.wait(7.0)
					} else {
						game.printToLog("[COMBAT] Summon $j cannot be invoked due to current restrictions.", MESSAGE_TAG = TAG)
						game.findAndClickButton("cancel")
						
						// Tap the "Back" button to return.
						game.findAndClickButton("back")
					}
				}
			}
		}
	}
	
	/**
	 * Wait for a maximum of 20 seconds until the bot sees either the "Attack" or the "Next" button before starting a new turn.
	 */
	private fun waitForAttack() {
		var tries = 10
		
		while ((!retreatCheckFlag && game.imageUtils.findButton("attack", tries = 1, suppressError = true) == null) ||
			(!retreatCheckFlag && game.imageUtils.findButton("next", tries = 1, suppressError = true) == null)) {
			// Stagger the checks for dialog popups during Combat Mode.
			if (tries % 2 == 0) {
				findCombatDialog()
			}
			
			game.wait(1.0)
			
			tries -= 1
			if (tries <= 0 || game.imageUtils.findButton("attack", tries = 1, suppressError = true) != null ||
				game.imageUtils.findButton("next", tries = 1, suppressError = true) != null) {
				break
			}
			
			// Check if the Party wiped after attacking.
			partyWipeCheck()
			
			game.wait(1.0)
		}
	}
	
	/**
	 * Determine whether or not to reload after an Attack.
	 */
	private fun reloadAfterAttack() {
		val rotbQuests = arrayListOf("VH Zhuque", "VH Xuanwu", "VH Baihu", "VH Qinglong")
		val provingGroundsQuests = arrayListOf("Extreme", "Extreme+")
		
		// If the "Cancel" button vanishes, that means the attack is in-progress. Now reload the page and wait for either the attack to finish or Battle ended.
		if ((game.farmingMode != "Quest" && game.farmingMode != "Special") && (game.farmingMode != "Event" && game.missionName != "EX Event Quest") &&
			(game.farmingMode != "Event (Token Drawboxes)" && game.missionName != "EX Event Quest") && (game.farmingMode != "Rise of the Beasts" && rotbQuests.contains(game.missionName)) &&
			(game.farmingMode != "Proving Grounds" && provingGroundsQuests.contains(game.missionName)) && (game.farmingMode != "Xeno Clash" && game.missionName != "Xeno Clash Extreme")) {
			game.findAndClickButton("reload")
		}
	}
	
	/**
	 * Start Combat Mode with the provided combat script.
	 *
	 * @param combatScript ArrayList of all the lines in the text file.
	 * @return True if Combat Mode ended successfully. False otherwise if the Party wiped or backed out without retreating.
	 */
	fun startCombatMode(combatScript: List<String>): Boolean {
		game.printToLog("\n################################################################################", MESSAGE_TAG = TAG)
		game.printToLog("################################################################################", MESSAGE_TAG = TAG)
		game.printToLog("[COMBAT] Starting Combat Mode.", MESSAGE_TAG = TAG)
		game.printToLog("################################################################################", MESSAGE_TAG = TAG)
		game.printToLog("################################################################################", MESSAGE_TAG = TAG)
		
		val commandList = combatScript.toMutableList()
		
		// Current Turn number for script execution.
		var turnNumber = 1
		
		// Reset the Retreat, Semi Auto, and Full Auto flags.
		retreatCheckFlag = false
		var semiAutoCheckFlag = false
		var fullAutoCheckFlag = false
		
		attackButtonLocation = game.imageUtils.findButton("attack", tries = 10)
		if (attackButtonLocation == null) {
			game.printToLog("\n[ERROR] Cannot find Attack button. Raid must have just ended.", MESSAGE_TAG = TAG, isError = true)
			return false
		}
		
		// The following is the primary loop workflow for Combat Mode.
		while (combatScript.isNotEmpty() && !retreatCheckFlag && !semiAutoCheckFlag && !fullAutoCheckFlag) {
			// The commands are already preprocessed to remove all comments back in SettingsFragment.
			var command = commandList.removeAt(0).toLowerCase(Locale.ROOT)
			
			game.printToLog("[COMBAT] Reading command: $command", MESSAGE_TAG = TAG)
			
			if (command.contains("turn")) {
				// Parse the Turn's number.
				val commandTurnNumber: Int = (command.split(":")[0].split(" ")[1]).toInt()
				
				// If the command is a "Turn #:" and it is currently not the correct Turn, attack until the Turn numbers match.
				if (!retreatCheckFlag && turnNumber != commandTurnNumber) {
					game.printToLog("[COMBAT] Attacking until the bot reaches Turn $commandTurnNumber", MESSAGE_TAG = TAG)
					
					while (turnNumber != commandTurnNumber) {
						game.printToLog("[COMBAT] Starting Turn $turnNumber.", MESSAGE_TAG = TAG)
						
						// Clear any detected dialog popups that might obstruct the "Attack" button.
						findCombatDialog()
						
						if (game.imageUtils.findButton("attack", tries = 1, suppressError = true) != null) {
							game.printToLog("[COMBAT] Ending Turn $turnNumber")
							
							// Tap the "Attack" button.
							game.gestureUtils.tap(attackButtonLocation!!.x, attackButtonLocation!!.y, "attack")
							
							// Wait until the "Cancel" button vanishes from the screen.
							if (game.imageUtils.findButton("combat_cancel", suppressError = true) != null) {
								while (!game.imageUtils.waitVanish("combat_cancel", timeout = 5, suppressError = true)) {
									if (debugMode) {
										game.printToLog("[DEBUG] The \"Cancel\" button has not vanished from the screen yet.", MESSAGE_TAG = TAG)
									}
									
									game.wait(1.0)
								}
							}
							
							reloadAfterAttack()
							waitForAttack()
							
							game.printToLog("[COMBAT] Turn $turnNumber has ended.", MESSAGE_TAG = TAG)
							
							turnNumber += 1
						}
						
						if (game.findAndClickButton("next", tries = 1, suppressError = true)) {
							game.wait(3.0)
						}
						
						// Check if the Battle has ended.
						if (retreatCheckFlag || game.imageUtils.confirmLocation("exp_gained", tries = 1) || game.imageUtils.confirmLocation("no_loot", tries = 1) ||
							game.imageUtils.confirmLocation("battle_concluded", tries = 1, suppressError = true)) {
							game.printToLog("\n[COMBAT] Battle concluded suddenly.", MESSAGE_TAG = TAG)
							game.printToLog("\n################################################################################", MESSAGE_TAG = TAG)
							game.printToLog("################################################################################", MESSAGE_TAG = TAG)
							game.printToLog("[COMBAT] Ending Combat Mode.", MESSAGE_TAG = TAG)
							game.printToLog("################################################################################", MESSAGE_TAG = TAG)
							game.printToLog("################################################################################", MESSAGE_TAG = TAG)
							return false
						}
					}
				}
				
				if (!retreatCheckFlag && turnNumber == commandTurnNumber) {
					game.printToLog("[COMBAT] Starting Turn $turnNumber.", MESSAGE_TAG = TAG)
					
					// Clear any detected dialog popups that might obstruct the "Attack" button.
					findCombatDialog()
					
					// Proceed to process each command inside this Turn block until the "end" command is reached.
					while (commandList.isNotEmpty() && command != "end" && command != "exit") {
						command = commandList.removeAt(0).toLowerCase(Locale.ROOT)
						
						game.printToLog("[COMBAT] Reading command: $command", MESSAGE_TAG = TAG)
						
						if (command == "end") {
							break
						} else if (command == "exit") {
							// End Combat Mode by heading back to the Home screen without retreating.
							game.printToLog("\n[COMBAT] Leaving this Raid without retreating.", MESSAGE_TAG = TAG)
							game.printToLog("\n################################################################################", MESSAGE_TAG = TAG)
							game.printToLog("################################################################################", MESSAGE_TAG = TAG)
							game.printToLog("[COMBAT] Ending Combat Mode.", MESSAGE_TAG = TAG)
							game.printToLog("################################################################################", MESSAGE_TAG = TAG)
							game.printToLog("################################################################################", MESSAGE_TAG = TAG)
							game.goBackHome(confirmLocationCheck = true)
							return false
						}
						
						// Determine which Character to take action.
						val characterSelected = when {
							command.contains("character1") -> {
								1
							}
							command.contains("character2") -> {
								2
							}
							command.contains("character3") -> {
								3
							}
							command.contains("character4") -> {
								4
							}
							else -> {
								0
							}
						}
						
						// Execute Skill commands here.
						if (characterSelected != 0) {
							// Select the specified Character.
							selectCharacter(characterSelected)
							
							// Now execute each Skill command starting from left to right for this Character.
							val skillCommandList: List<String> = command.split(".")
							skillCommandList.drop(1)
							
							useCharacterSkill(characterSelected, skillCommandList)
							
							if (game.imageUtils.confirmLocation("battle_concluded", tries = 1, suppressError = true)) {
								game.printToLog("\n[COMBAT] Battle concluded suddenly.", MESSAGE_TAG = TAG)
								game.printToLog("\n################################################################################", MESSAGE_TAG = TAG)
								game.printToLog("################################################################################", MESSAGE_TAG = TAG)
								game.printToLog("[COMBAT] Ending Combat Mode.", MESSAGE_TAG = TAG)
								game.printToLog("################################################################################", MESSAGE_TAG = TAG)
								game.printToLog("################################################################################", MESSAGE_TAG = TAG)
								game.findAndClickButton("reload")
								return true
							}
						}
						
						// Handle any other supported command.
						else if (command == "requestbackup") {
							requestBackup()
						} else if (command == "tweetbackup") {
							tweetBackup()
						} else if (healingItemCommands.contains(command)) {
							useCombatHealingItem(command)
						} else if (command.contains("summon")) {
							useSummon(command)
						} else if (command == "enablesemiauto") {
							game.printToLog("[COMBAT] Enabling Semi Auto.", MESSAGE_TAG = TAG)
							semiAutoCheckFlag = true
							break
						} else if (command == "enablefullauto") {
							game.printToLog("[COMBAT] Enabling Full Auto.", MESSAGE_TAG = TAG)
							fullAutoCheckFlag = game.findAndClickButton("full_auto", tries = 3)
							
							// If the bot failed to find and click the "Full Auto" button, fallback to the "Semi Auto" button.
							if (!fullAutoCheckFlag) {
								game.printToLog("[COMBAT] Failed to find the \"Full Auto\" button. Falling back to Semi Auto.", MESSAGE_TAG = TAG)
								semiAutoCheckFlag = true
							}
							
							break
						}
						
						if (game.findAndClickButton("next", tries = 1, suppressError = true)) {
							break
						}
					}
				}
				
				// Handle certain commands that could be present outside of a Turn block.
				if (!semiAutoCheckFlag && !fullAutoCheckFlag && command == "enablesemiauto") {
					game.printToLog("[COMBAT] Enabling Semi Auto.", MESSAGE_TAG = TAG)
					semiAutoCheckFlag = true
					break
				} else if (!semiAutoCheckFlag && !fullAutoCheckFlag && command == "enablefullauto") {
					game.printToLog("[COMBAT] Enabling Full Auto.", MESSAGE_TAG = TAG)
					fullAutoCheckFlag = game.findAndClickButton("full_auto")
					
					// If the bot failed to find and click the "Full Auto" button, fallback to the "Semi Auto" button.
					if (!fullAutoCheckFlag) {
						game.printToLog("[COMBAT] Failed to find the \"Full Auto\" button. Falling back to Semi Auto.", MESSAGE_TAG = TAG)
						semiAutoCheckFlag = true
					}
					
					break
				} else if (!semiAutoCheckFlag && !fullAutoCheckFlag && command == "end") {
					// Tap the "Attack" button once every command inside the Turn Block has been processed.
					game.printToLog("[COMBAT] Ending Turn $turnNumber")
					game.findAndClickButton("attack", tries = 10)
					
					// Wait until the "Cancel" button vanishes from the screen.
					if (game.imageUtils.findButton("combat_cancel", suppressError = true) != null) {
						while (!game.imageUtils.waitVanish("combat_cancel", timeout = 5, suppressError = true)) {
							if (debugMode) {
								game.printToLog("[DEBUG] The \"Cancel\" button has not vanished from the screen yet.", MESSAGE_TAG = TAG)
							}
							
							game.wait(1.0)
						}
					}
					
					reloadAfterAttack()
					waitForAttack()
					
					game.printToLog("[COMBAT] Turn $turnNumber has ended.", MESSAGE_TAG = TAG)
					
					turnNumber += 1
					
					if (game.findAndClickButton("next", tries = 1, suppressError = true)) {
						game.wait(3.0)
					}
				}
			}
		}
		
		// When the bot arrives here, all the commands in the combat script has been processed.
		game.printToLog("[COMBAT] Bot has processed the entire combat script. Automatically attacking until the battle ends or Party wipes.", MESSAGE_TAG = TAG)
		
		// Until the bot reaches the Quest Results screen, keep pressing the "Attack" and "Next" buttons if not Semi Auto or Full Auto.
		while (!retreatCheckFlag && !semiAutoCheckFlag && !fullAutoCheckFlag && !game.imageUtils.confirmLocation("exp_gained", tries = 1, suppressError = true) &&
			!game.imageUtils.confirmLocation("no_loot", tries = 1, suppressError = true)) {
			// Clear any detected dialog popups that might obstruct the "Attack" button.
			findCombatDialog()
			
			// Tap the "Attack" button once every command inside the Turn Block has been processed.
			game.printToLog("[COMBAT] Ending Turn $turnNumber")
			game.findAndClickButton("attack", tries = 10)
			
			// Wait until the "Cancel" button vanishes from the screen.
			if (game.imageUtils.findButton("combat_cancel", suppressError = true) != null) {
				while (!game.imageUtils.waitVanish("combat_cancel", timeout = 5, suppressError = true)) {
					if (debugMode) {
						game.printToLog("[DEBUG] The \"Cancel\" button has not vanished from the screen yet.", MESSAGE_TAG = TAG)
					}
					
					game.wait(1.0)
				}
			}
			
			reloadAfterAttack()
			waitForAttack()
			
			game.printToLog("[COMBAT] Turn $turnNumber has ended.", MESSAGE_TAG = TAG)
			
			turnNumber += 1
			
			if (game.findAndClickButton("next", tries = 1, suppressError = true)) {
				game.wait(3.0)
			}
		}
		
		// Double check to see if Semi Auto is turned on. Note that the "Semi Auto" button only appears while the Party is attacking.
		if (!retreatCheckFlag && semiAutoCheckFlag && !fullAutoCheckFlag) {
			game.printToLog("[COMBAT] Double checking to see if Semi Auto is enabled.", MESSAGE_TAG = TAG)
			
			val enabledSemiAutoButtonLocation = game.imageUtils.findButton("semi_auto_enabled")
			if (enabledSemiAutoButtonLocation == null) {
				// Have the Party attack and then attempt to see if the "Semi Auto" button becomes visible.
				game.findAndClickButton("attack")
				
				game.wait(2.0)
				
				semiAutoCheckFlag = game.findAndClickButton("semi_auto", tries = 5)
				
				// If the bot still cannot find the "Semi Auto" button, that probably means that the user has the "Full Auto" button on the screen instead.
				if (!semiAutoCheckFlag) {
					game.printToLog("[COMBAT] Failed to enable Semi Auto. Falling back to Full Auto.", MESSAGE_TAG = TAG)
					fullAutoCheckFlag = true
					
					// Enable Full Auto.
					game.findAndClickButton("full_auto")
				} else {
					game.printToLog("[COMBAT] Semi Auto is now enabled.", MESSAGE_TAG = TAG)
				}
			}
		}
		
		var sleepPreventionTimer = 0
		
		// Primary loop workflow for Semi Auto. The bot will progress the Quest/Raid until it ends or the Party wipes.
		while (!retreatCheckFlag && !fullAutoCheckFlag && semiAutoCheckFlag && !game.imageUtils.confirmLocation("exp_gained", tries = 1, suppressError = true) &&
			!game.imageUtils.confirmLocation("no_loot", tries = 1, suppressError = true)) {
			if (game.imageUtils.confirmLocation("battle_concluded", tries = 1, suppressError = true)) {
				game.printToLog("\n[COMBAT] Battle concluded suddenly.", MESSAGE_TAG = TAG)
				game.printToLog("\n################################################################################", MESSAGE_TAG = TAG)
				game.printToLog("################################################################################", MESSAGE_TAG = TAG)
				game.printToLog("[COMBAT] Ending Combat Mode.", MESSAGE_TAG = TAG)
				game.printToLog("################################################################################", MESSAGE_TAG = TAG)
				game.printToLog("################################################################################", MESSAGE_TAG = TAG)
				game.findAndClickButton("reload")
				return true
			}
			
			// The Android device would lock itself and go to sleep if there has been no inputs. Thus, some occasional swiping is required.
			if (sleepPreventionTimer != 0 && sleepPreventionTimer % 60 == 0) {
				game.printToLog("\n[COMBAT] Swiping screen to prevent Android device going to sleep due to inactivity.", MESSAGE_TAG = TAG)
				game.gestureUtils.swipe(500f, 1000f, 500f, 900f, 100L)
				game.gestureUtils.swipe(500f, 900f, 500f, 1000f, 100L)
			}
			
			partyWipeCheck()
			game.wait(1.0)
			
			sleepPreventionTimer += 1
		}
		
		// Primary loop workflow for Full Auto. The bot will progress the Quest/Raid until it ends or the Party wipes.
		while (!retreatCheckFlag && fullAutoCheckFlag && !semiAutoCheckFlag && !game.imageUtils.confirmLocation("exp_gained", tries = 1, suppressError = true) &&
			!game.imageUtils.confirmLocation("no_loot", tries = 1, suppressError = true)) {
			if (game.imageUtils.confirmLocation("battle_concluded", tries = 1, suppressError = true)) {
				game.printToLog("\n[COMBAT] Battle concluded suddenly.", MESSAGE_TAG = TAG)
				game.printToLog("\n################################################################################", MESSAGE_TAG = TAG)
				game.printToLog("################################################################################", MESSAGE_TAG = TAG)
				game.printToLog("[COMBAT] Ending Combat Mode.", MESSAGE_TAG = TAG)
				game.printToLog("################################################################################", MESSAGE_TAG = TAG)
				game.printToLog("################################################################################", MESSAGE_TAG = TAG)
				game.findAndClickButton("reload")
				return true
			}
			
			// The Android device would lock itself and go to sleep if there has been no inputs. Thus, some occasional swiping is required.
			if (sleepPreventionTimer != 0 && sleepPreventionTimer % 60 == 0) {
				game.printToLog("\n[COMBAT] Swiping screen to prevent Android device going to sleep due to inactivity.", MESSAGE_TAG = TAG)
				game.gestureUtils.swipe(500f, 1000f, 500f, 900f, 100L)
				game.gestureUtils.swipe(500f, 900f, 500f, 1000f, 100L)
			}
			
			partyWipeCheck()
			game.wait(1.0)
			
			sleepPreventionTimer += 1
		}
		
		game.printToLog("\n################################################################################", MESSAGE_TAG = TAG)
		game.printToLog("################################################################################", MESSAGE_TAG = TAG)
		game.printToLog("[COMBAT] Ending Combat Mode.", MESSAGE_TAG = TAG)
		game.printToLog("################################################################################", MESSAGE_TAG = TAG)
		game.printToLog("################################################################################", MESSAGE_TAG = TAG)
		
		return if (!retreatCheckFlag) {
			game.printToLog("\n[INFO] Bot has reached the Quest Results screen.", MESSAGE_TAG = TAG)
			true
		} else {
			false
		}
	}
}