package com.steve1316.granblue_automation_android.utils

import android.content.Context
import android.graphics.Bitmap
import com.steve1316.automation_library.utils.ImageUtils
import com.steve1316.automation_library.utils.MessageLog
import com.steve1316.granblue_automation_android.bot.Game
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Utility functions for image processing via CV like OpenCV.
 */
class CustomImageUtils(context: Context, private val game: Game) : ImageUtils(context) {
	private val tag: String = "${com.steve1316.granblue_automation_android.MainActivity.loggerTag}ImageUtils"

	// Used for skipping selecting the Summon Element every time on repeated runs.
	private var summonSelectionFirstRun: Boolean = true
	private var summonSelectionSameElement: Boolean = true

	init {
		setTemplateSubfolderPath("buttons/")
	}

	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////

	/**
	 * Verify whether the template name is able to be adjusted and return its adjustment.
	 *
	 * @param templateName File name of the template image.
	 * @return The specific adjustment to the specified template or 0 to use the default number of tries.
	 */
	private fun determineAdjustment(templateName: String): Int {
		val calibrationList = arrayListOf("home")
		val pendingBattlesList = arrayListOf("check_your_pending_battles", "pending_battles", "quest_results_pending_battles")
		val captchaList = arrayListOf("captcha")
		val supportSummonSelectionList = arrayListOf("select_a_summon", "coop_without_support_summon", "proving_grounds_summon_selection")
		val beforeCombatStartList = arrayListOf("attack")
		val dialogList = arrayListOf("dialog_lyria", "dialog_vyrn")
		val skillUsageList = arrayListOf("use_skill", "skill_unusable")
		val summonUsageList = arrayListOf("summon_details", "quick_summon1", "quick_summon2", "quick_summon_not_ready")
		val noLootScreenList = arrayListOf("no_loot")
		val battleConcludedPopupList = arrayListOf("battle_concluded")
		val expGainedPopupList = arrayListOf("exp_gained")
		val lootCollectionScreenList = arrayListOf("loot_collected")
		val arcarumList = arrayListOf(
			"arcarum_party_selection", "arcarum_treasure", "arcarum_node", "arcarum_mob", "arcarum_red_mob", "arcarum_silver_chest", "arcarum_gold_chest", "arcarum_boss", "arcarum_boss2"
		)
		val arcarumStageEffectList = arrayListOf("arcarum_stage_effect_active")

		return when {
			game.configData.enableCalibrationAdjustment && calibrationList.contains(templateName) -> {
				game.configData.adjustCalibration
			}
			game.configData.enablePendingBattleAdjustment && pendingBattlesList.contains(templateName) -> {
				game.configData.adjustPendingBattle
			}
			game.configData.enableCaptchaAdjustment && captchaList.contains(templateName) -> {
				game.configData.adjustCaptcha
			}
			game.configData.enableSupportSummonSelectionScreenAdjustment && supportSummonSelectionList.contains(templateName) -> {
				game.configData.adjustSupportSummonSelectionScreen
			}
			game.configData.enableCombatModeAdjustment && beforeCombatStartList.contains(templateName) -> {
				game.configData.adjustCombatStart
			}
			game.configData.enableCombatModeAdjustment && dialogList.contains(templateName) -> {
				game.configData.adjustDialog
			}
			game.configData.enableCombatModeAdjustment && skillUsageList.contains(templateName) -> {
				game.configData.adjustSkillUsage
			}
			game.configData.enableCombatModeAdjustment && summonUsageList.contains(templateName) -> {
				game.configData.adjustSummonUsage
			}
			game.configData.enableCombatModeAdjustment && noLootScreenList.contains(templateName) -> {
				game.configData.adjustCheckForNoLootScreen
			}
			game.configData.enableCombatModeAdjustment && battleConcludedPopupList.contains(templateName) -> {
				game.configData.adjustCheckForBattleConcludedPopup
			}
			game.configData.enableCombatModeAdjustment && expGainedPopupList.contains(templateName) -> {
				game.configData.adjustCheckForExpGainedPopup
			}
			game.configData.enableCombatModeAdjustment && lootCollectionScreenList.contains(templateName) -> {
				game.configData.adjustCheckForLootCollectionScreen
			}
			game.configData.enableArcarumAdjustment && arcarumList.contains(templateName) -> {
				game.configData.adjustArcarumAction
			}
			game.configData.enableArcarumAdjustment && arcarumStageEffectList.contains(templateName) -> {
				game.configData.adjustArcarumStageEffect
			}
			else -> {
				0
			}
		}
	}

	/**
	 * Finds the location of the specified image from the /images/ folder inside assets.
	 *
	 * @param templateName File name of the template image.
	 * @param tries Number of tries before failing. Note that this gets overridden if the templateName is one of the adjustments. Defaults to 5.
	 * @param region Specify the region consisting of (x, y, width, height) of the source screenshot to template match. Defaults to (0, 0, 0, 0) which is equivalent to searching the full image.
	 * @param customConfidence Use a custom confidence for the template matching. Defaults to 0.80.
	 * @param suppressError Whether or not to suppress saving error messages to the log. Defaults to false.
	 * @param disableAdjustment Disable the usage of adjustment to tries. Defaults to False.
	 * @param bypassGeneralAdjustment Bypass using the general adjustment for the number of tries. Defaults to False.
	 * @param testMode Flag to test and get a valid scale for device compatibility.
	 * @return Point object containing the location of the match or null if not found.
	 */
	fun findButton(
		templateName: String, tries: Int = 5, region: IntArray = intArrayOf(0, 0, 0, 0), customConfidence: Double = confidence, suppressError: Boolean = false,
		disableAdjustment: Boolean = false, bypassGeneralAdjustment: Boolean = false, testMode: Boolean = false
	): Point? {
		val folderName = "buttons"
		var numberOfTries = determineAdjustment(templateName)
		numberOfTries = if (numberOfTries == 0 && !disableAdjustment) {
			if (game.configData.enableGeneralAdjustment && !bypassGeneralAdjustment && tries == 5) {
				game.configData.adjustButtonSearchGeneral
			} else {
				tries
			}
		} else {
			tries
		}

		if (debugMode) {
			MessageLog.printToLog("\n[DEBUG] Starting process to find the ${templateName.uppercase()} button image...", tag = tag)
		}

		// If Test Mode is enabled, prepare for it by setting initial scale.
		if (testMode) {
			numberOfTries = 80
			customScale = 0.20
		}

		var (sourceBitmap, templateBitmap) = getBitmaps(templateName, folderName)

		while (numberOfTries > 0) {
			if (sourceBitmap != null && templateBitmap != null) {
				val resultFlag: Boolean = match(sourceBitmap, templateBitmap, region, customConfidence = customConfidence, useSingleScale = true)
				if (!resultFlag) {
					if (testMode) {
						// Increment scale by 0.01 until a match is found if Test Mode is enabled.
						customScale += 0.01
						customScale = decimalFormat.format(customScale).replace(",", ".").toDouble()
					}

					numberOfTries -= 1
					if (numberOfTries <= 0) {
						if (!suppressError) {
							MessageLog.printToLog("[WARNING] Failed to find the ${templateName.uppercase()} button.", tag = tag)
						}

						break
					}

					if (!testMode) {
						game.wait(0.1)
					}

					sourceBitmap = getSourceScreenshot()
				} else {
					if (testMode) {
						// Create a range of scales for user recommendation.
						val scale0: Double = decimalFormat.format(customScale).replace(",", ".").toDouble()
						val scale1: Double = decimalFormat.format(scale0 + 0.01).replace(",", ".").toDouble()
						val scale2: Double = decimalFormat.format(scale0 + 0.02).replace(",", ".").toDouble()
						val scale3: Double = decimalFormat.format(scale0 + 0.03).replace(",", ".").toDouble()
						val scale4: Double = decimalFormat.format(scale0 + 0.04).replace(",", ".").toDouble()

						MessageLog.printToLog(
							"[SUCCESS] Found the ${templateName.uppercase()} at $matchLocation with scale $scale0.\n\nRecommended to use scale $scale1, $scale2, $scale3 or $scale4.",
							tag = tag
						)
					} else {
						if (game.configData.debugMode) {
							MessageLog.printToLog("[DEBUG] Found the ${templateName.uppercase()} at $matchLocation.", tag = tag)
						}
					}

					return matchLocation
				}
			}
		}

		return null
	}

	/**
	 * Confirms whether or not the bot is at the specified location from the /headers/ folder inside assets.
	 *
	 * @param templateName File name of the template image.
	 * @param tries Number of tries before failing. Note that this gets overridden if the templateName is one of the adjustments. Defaults to 5.
	 * @param region Specify the region consisting of (x, y, width, height) of the source screenshot to template match. Defaults to (0, 0, 0, 0) which is equivalent to searching the full image.
	 * @param customConfidence customConfidence Use a custom confidence for the template matching. Defaults to 0.80.
	 * @param suppressError Whether or not to suppress saving error messages to the log.
	 * @param disableAdjustment Disable the usage of adjustment to tries. Defaults to False.
	 * @param bypassGeneralAdjustment Bypass using the general adjustment for the number of tries. Defaults to False.
	 * @return True if the current location is at the specified location. False otherwise.
	 */
	fun confirmLocation(
		templateName: String, tries: Int = 5, region: IntArray = intArrayOf(0, 0, 0, 0), customConfidence: Double = confidence, suppressError: Boolean = false,
		disableAdjustment: Boolean = false, bypassGeneralAdjustment: Boolean = false
	): Boolean {
		val folderName = "headers"
		var numberOfTries = determineAdjustment(templateName)
		numberOfTries = if (numberOfTries == 0 && !disableAdjustment) {
			if (game.configData.enableGeneralAdjustment && !bypassGeneralAdjustment && tries == 5) {
				game.configData.adjustHeaderSearchGeneral
			} else {
				tries
			}
		} else {
			tries
		}

		if (debugMode) {
			MessageLog.printToLog("\n[DEBUG] Starting process to find the ${templateName.uppercase()} header image...", tag = tag)
		}

		var (sourceBitmap, templateBitmap) = getBitmaps(templateName + "_header", folderName)

		while (numberOfTries > 0) {
			if (sourceBitmap != null && templateBitmap != null) {
				val resultFlag: Boolean = match(sourceBitmap, templateBitmap, region, customConfidence = customConfidence)
				if (!resultFlag) {
					numberOfTries -= 1
					if (numberOfTries <= 0) {
						break
					}

					// Attempt to fix the issue where the Summon Selection page loaded in at the bottom of the view.
					if (templateName == "select_a_summon") {
						game.wait(0.5)
						if (game.imageUtils.findButton("bottom_of_summon_selection") != null) {
							game.findAndClickButton("reload")
						}
					}

					game.wait(0.1)
					sourceBitmap = getSourceScreenshot()
				} else {
					if (game.configData.debugMode) {
						MessageLog.printToLog("[DEBUG] Current location confirmed to be at ${templateName.uppercase()}.", tag = tag)
					}

					return true
				}
			} else {
				break
			}
		}

		if (!suppressError) {
			MessageLog.printToLog("[WARNING] Failed to confirm the bot location at ${templateName.uppercase()}.", tag = tag)
		}

		return false
	}

	/**
	 * Finds the location of the specified Summon.
	 *
	 * @param summonList List of selected Summons sorted from greatest to least priority.
	 * @param summonElementList List of Summon Elements that correspond to the summonList.
	 * @param suppressError Suppresses template matching error if True. Defaults to False.
	 * @return Point object containing the location of the match or null if not found.
	 */
	fun findSummon(summonList: List<String>, summonElementList: List<String>, suppressError: Boolean = false): Point? {
		val folderName = "summons"

		if (debugMode) {
			MessageLog.printToLog("[DEBUG] Received the following list of Summons to search for: $summonList", tag = tag)
			MessageLog.printToLog("[DEBUG] Received the following list of Summon Elements: $summonElementList", tag = tag)
		}

		var lastSummonElement = ""
		var summonIndex: Int
		var summonElementIndex = 0

		// Make sure that the bot is at the Summon Selection screen.
		var tries = 10
		while (!confirmLocation("select_a_summon")) {
			game.findAndClickButton("reload")
			tries -= 1
			if (tries <= 0 && !confirmLocation("select_a_summon", tries = 1)) {
				throw Exception("Could not reach the Summon Selection screen.")
			}
		}

		// Determine if all the summon elements are the same or not. This will influence whether or not the bot needs to change elements in repeated runs.
		for (element in summonElementList) {
			if (element != summonElementList[0]) {
				summonSelectionSameElement = false
			}
		}

		// Make the first summon element category active for first run.
		if (summonSelectionFirstRun) {
			val currentSummonElement: String = summonElementList[summonElementIndex]
			game.findAndClickButton("summon_$currentSummonElement")
			lastSummonElement = currentSummonElement
			summonSelectionFirstRun = false
		}

		tries = 30
		while (true) {
			// Reset the summon index.
			summonIndex = 0
			while (summonIndex < summonList.size) {
				// Switch over to a different element for this summon index if it is different.
				if (!summonSelectionSameElement) {
					val currentSummonElement: String = summonElementList[summonElementIndex]
					if (currentSummonElement != lastSummonElement) {
						if (!game.findAndClickButton("summon_$currentSummonElement")) {
							throw Exception("Unable to switch summon element categories from ${lastSummonElement.uppercase()} to ${currentSummonElement.uppercase()}.")
						}
					}
				}

				val summonName = summonList[summonIndex]
				val (sourceBitmap, templateBitmap) = getBitmaps(summonName, folderName)

				val resultFlag: Boolean = match(sourceBitmap!!, templateBitmap!!, customConfidence = 0.7)

				if (resultFlag) {
					if (game.configData.debugMode) {
						MessageLog.printToLog("[SUCCESS] Found ${summonList[summonIndex].uppercase()} Summon at $matchLocation.", tag = tag)
					}

					return matchLocation
				} else {
					if (!suppressError) {
						MessageLog.printToLog("[WARNING] Could not locate ${summonList[summonIndex].uppercase()} Summon.", tag = tag)
					}

					if (summonSelectionSameElement) {
						summonIndex += 1
					} else {
						// Keep searching for the same summon until the bot reaches the bottom of the page. Then reset the page and move to the next summon's element.
						if (findButton("bottom_of_summon_selection", tries = 1) != null) {
							summonIndex += 1
							summonElementIndex += 1

							// If the bot cycled through the list of summon elements without find a match, reset Summons.
							if (!summonSelectionSameElement && summonElementIndex >= summonElementList.size) {
								MessageLog.printToLog("[WARNING] Bot has gone through the entire summon list without finding a match. Resetting Summons now...", tag = tag)
								return null
							}

							MessageLog.printToLog("[INFO] Bot has reached the bottom of the page. Moving on to the next summon's element...", tag = tag)
							if (!game.findAndClickButton("reload")) {
								game.gestureUtils.scroll(scrollDown = false)
							}


							game.wait(2.0)
						} else {
							// If matching failed and the bottom of the page has not been reached, scroll the screen down to see more Summons and try again.
							game.gestureUtils.swipe(500f, 1000f, 500f, 400f)
						}
					}

					tries -= 1
				}
			}

			// Perform check here to prevent infinite loop for rare cases.
			if (tries <= 0) {
				MessageLog.printToLog("[WARNING] Summon Selection process was not able to find any valid summons. Resetting Summons now...", tag = tag)
				return null
			}

			// If the bot reached the bottom of the page, reset Summons.
			if (findButton("bottom_of_summon_selection", tries = 1) != null) {
				MessageLog.printToLog("[WARNING] Bot has reached the bottom of the page and found no suitable Summons. Resetting Summons now...", tag = tag)
				return null
			}

			// If matching failed and the bottom of the page has not been reached, scroll the screen down to see more Summons and try again.
			game.gestureUtils.swipe(500f, 1000f, 500f, 400f)
			game.wait(1.0)
		}
	}

	/**
	 * Finds all occurrences of the specified image in the buttons folder. Has an optional parameter to specify looking in the items folder instead.
	 *
	 * @param templateName File name of the template image.
	 * @param isItem Whether or not the user wants to search for items instead of buttons.
	 * @param region Specify the region consisting of (x, y, width, height) of the source screenshot to template match. Defaults to (0, 0, 0, 0) which is equivalent to searching the full image.
	 * @param customConfidence Accuracy threshold for matching all. Defaults to the device default.
	 * @return An ArrayList of Point objects containing all the occurrences of the specified image or null if not found.
	 */
	fun findAll(templateName: String, isItem: Boolean = false, region: IntArray = intArrayOf(0, 0, 0, 0), customConfidence: Double = confidenceAll): ArrayList<Point> {
		val folderName = if (!isItem) {
			"buttons"
		} else {
			"items"
		}

		if (debugMode) {
			MessageLog.printToLog("\n[DEBUG] Starting process to find all ${templateName.uppercase()} images...", tag = tag)
		}

		val (sourceBitmap, templateBitmap) = getBitmaps(templateName, folderName)

		// Clear the ArrayList first before attempting to find all matches.
		matchLocations.clear()

		if (sourceBitmap != null && templateBitmap != null) {
			matchAll(sourceBitmap, templateBitmap, region = region, customConfidence = customConfidence)
		}

		// Sort the match locations by ascending x and y coordinates.
		matchLocations.sortBy { it.x }
		matchLocations.sortBy { it.y }

		if (debugMode) {
			MessageLog.printToLog("[DEBUG] Found match locations for $templateName: $matchLocations.", tag = tag)
		}

		return matchLocations
	}

	/**
	 * Finds all occurrences of the specified item image and sums all of the number amounts together.
	 *
	 * @param templateName File name of the template image.
	 * @return Sum of all the item's amounts.
	 */
	fun findFarmedItems(templateName: String): Int {
		MessageLog.printToLog("[INFO] Now detecting item rewards.", tag = tag)

		// Reset the total item amount.
		var totalItemAmount = 0

		// Get the locations of all of the specified item.
		val itemLocations: ArrayList<Point> = findAll(templateName, isItem = true)

		// Grab a reference to the source bitmap.
		val sourceBitmap = getSourceScreenshot()

		for (itemLocation in itemLocations) {
			// Crop the source bitmap to hold only the item amount.
			val croppedItemAmountBitmap = Bitmap.createBitmap(sourceBitmap, (itemLocation.x + 50).toInt(), (itemLocation.y).toInt() - 10, 35, 50)
			val cvImage = Mat()
			Utils.bitmapToMat(croppedItemAmountBitmap, cvImage)

			// Grayscale the cropped image.
			val grayImage = Mat()
			Imgproc.cvtColor(cvImage, grayImage, Imgproc.COLOR_RGB2GRAY)

			// Thresh the grayscale cropped image to make black and white.
			val resultBitmap: Bitmap = croppedItemAmountBitmap
			val bwImage = Mat()
			Imgproc.threshold(grayImage, bwImage, 130.0, 255.0, Imgproc.THRESH_BINARY)
			Utils.matToBitmap(bwImage, resultBitmap)

			// Use the Tesseract client geared towards digits to set the image to scan.
			tessDigitsBaseAPI.setImage(resultBitmap)

			totalItemAmount += try {
				// Finally, detect text on the cropped region.
				val text = tessDigitsBaseAPI.utF8Text

				try {
					if (debugMode) {
						MessageLog.printToLog("[DEBUG] Detected item amount: ${text.toInt()}", tag = tag)
					}
					text.toInt()
				} catch (_: Exception) {
					MessageLog.printToLog("[ERROR] Failed to convert $text to integer. Defaulting to 1.", tag, isError = true)
					1
				}
			} catch (e: Exception) {
				MessageLog.printToLog("[ERROR] Cannot perform OCR: ${e.stackTraceToString()}. Defaulting to 1.", tag, isError = true)
				1
			}

			// Stop Tesseract operations.
			tessDigitsBaseAPI.stop()
		}

		return totalItemAmount
	}
}