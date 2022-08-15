package com.steve1316.granblue_automation_android.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager
import com.steve1316.granblue_automation_android.MainActivity

class ConfigData(myContext: Context) {
	private val tag = "${MainActivity.loggerTag}ConfigData"

	// Game
	val farmingMode: String
	val mapName: String
	val missionName: String
	val itemName: String
	val itemAmount: Int
	val combatScriptName: String
	val combatScript: List<String>
	val summonList: List<String>
	val groupNumber: Int
	val partyNumber: Int
	val debugMode: Boolean
	var combatElapsedTime: Long = 0L

	// Twitter
	val twitterAPIKey: String
	val twitterAPIKeySecret: String
	val twitterAccessToken: String
	val twitterAccessTokenSecret: String

	// Discord
	val enableDiscordNotifications: Boolean
	val discordToken: String
	val discordUserID: String

	// API
	val enableOptInAPI: Boolean

	// Configuration
	val enableDelayBetweenRuns: Boolean
	val delayBetweenRuns: Int
	val enableRandomizedDelayBetweenRuns: Boolean
	val delayBetweenRunsLowerBound: Int
	val delayBetweenRunsUpperBound: Int
	val enableRefreshDuringCombat: Boolean
	val enableAutoQuickSummon: Boolean
	val enableBypassResetSummon: Boolean

	// Nightmare
	val enableNightmare: Boolean
	val nightmareCombatScriptName: String
	val nightmareCombatScript: List<String>
	val nightmareSummons: List<String>
	val nightmareGroupNumber: Int
	val nightmarePartyNumber: Int

	// Event
	val enableLocationIncrementByOne: Boolean
	val enableSelectBottomCategory: Boolean

	// Raid
	val enableAutoExitRaid: Boolean
	val timeAllowedUntilAutoExitRaid: Long
	val enableNoTimeout: Boolean

	// Arcarum
	val enableStopOnArcarumBoss: Boolean

	// Sandbox
	val enableDefender: Boolean
	val enableCustomDefenderSettings: Boolean
	val numberOfDefenders: Int
	var numberOfDefeatedDefenders: Int
	var engagedDefenderBattle: Boolean
	val defenderCombatScriptName: String
	val defenderCombatScript: List<String>
	val defenderGroupNumber: Int
	val defenderPartyNumber: Int

	// Generic
	val enableForceReload: Boolean

	// Xeno Clash
	val selectTopOption: Boolean

	// Adjustment
	val enableCalibrationAdjustment: Boolean
	val adjustCalibration: Int
	val enableGeneralAdjustment: Boolean
	val adjustButtonSearchGeneral: Int
	val adjustHeaderSearchGeneral: Int
	val enablePendingBattleAdjustment: Boolean
	val adjustBeforePendingBattle: Int
	val adjustPendingBattle: Int
	val enableCaptchaAdjustment: Boolean
	val adjustCaptcha: Int
	val enableSupportSummonSelectionScreenAdjustment: Boolean
	val adjustSupportSummonSelectionScreen: Int
	val enableCombatModeAdjustment: Boolean
	val adjustCombatStart: Int
	val adjustDialog: Int
	val adjustSkillUsage: Int
	val adjustSummonUsage: Int
	val adjustWaitingForReload: Int
	val adjustWaitingForAttack: Int
	val adjustCheckForNoLootScreen: Int
	val adjustCheckForBattleConcludedPopup: Int
	val adjustCheckForExpGainedPopup: Int
	val adjustCheckForLootCollectionScreen: Int
	val enableArcarumAdjustment: Boolean
	val adjustArcarumAction: Int
	val adjustArcarumStageEffect: Int

	// Android
	val enableDelayTap: Boolean
	val delayTapMilliseconds: Int
	val confidence: Double
	val confidenceAll: Double
	val customScale: Double
	val enableTestForHomeScreen: Boolean

	init {
		Log.d(tag, "Loading settings from SharedPreferences to memory...")

		val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)

		// Game
		farmingMode = sharedPreferences.getString("farmingMode", "")!!
		mapName = sharedPreferences.getString("map", "")!!
		missionName = sharedPreferences.getString("mission", "")!!
		itemName = sharedPreferences.getString("item", "")!!
		itemAmount = sharedPreferences.getInt("itemAmount", 1)
		combatScriptName = sharedPreferences.getString("combatScriptName", "")!!
		combatScript = if (sharedPreferences.getString("combatScript", "")!! == "") {
			listOf()
		} else {
			sharedPreferences.getString("combatScript", "")!!.split("|")
		}
		summonList = sharedPreferences.getString("summons", "")!!.split("|")
		groupNumber = sharedPreferences.getInt("groupNumber", 1)
		partyNumber = sharedPreferences.getInt("partyNumber", 1)
		debugMode = sharedPreferences.getBoolean("debugMode", false)

		// Twitter
		twitterAPIKey = sharedPreferences.getString("twitterAPIKey", "")!!
		twitterAPIKeySecret = sharedPreferences.getString("twitterAPIKeySecret", "")!!
		twitterAccessToken = sharedPreferences.getString("twitterAccessToken", "")!!
		twitterAccessTokenSecret = sharedPreferences.getString("twitterAccessTokenSecret", "")!!

		// Discord
		enableDiscordNotifications = sharedPreferences.getBoolean("enableDiscordNotifications", false)
		discordToken = sharedPreferences.getString("discordToken", "")!!
		discordUserID = sharedPreferences.getString("discordUserID", "")!!

		// API
		enableOptInAPI = sharedPreferences.getBoolean("enableOptInAPI", false)

		// Configuration
		enableDelayBetweenRuns = sharedPreferences.getBoolean("enableDelayBetweenRuns", false)
		delayBetweenRuns = sharedPreferences.getInt("delayBetweenRuns", 5)
		enableRandomizedDelayBetweenRuns = sharedPreferences.getBoolean("enableRandomizedDelayBetweenRuns", false)
		delayBetweenRunsLowerBound = sharedPreferences.getInt("delayBetweenRunsLowerBound", 5)
		delayBetweenRunsUpperBound = sharedPreferences.getInt("delayBetweenRunsUpperBound", 15)
		enableRefreshDuringCombat = sharedPreferences.getBoolean("enableRefreshDuringCombat", true)
		enableAutoQuickSummon = sharedPreferences.getBoolean("enableAutoQuickSummon", false)
		enableBypassResetSummon = sharedPreferences.getBoolean("enableBypassResetSummon", false)

		// Nightmare
		enableNightmare = sharedPreferences.getBoolean("enableNightmare", false)
		if (sharedPreferences.getBoolean("enableCustomNightmareSettings", false)) {
			Log.d(tag, "[NIGHTMARE] Settings initializing...")

			nightmareCombatScriptName = sharedPreferences.getString("nightmareCombatScriptName", "")!!

			nightmareCombatScript = if (sharedPreferences.getString("nightmareCombatScript", "")!!.split("|").isNotEmpty()) {
				sharedPreferences.getString("nightmareCombatScript", "")!!.split("|")
			} else {
				combatScript
			}

			nightmareSummons = if (sharedPreferences.getString("nightmareSummons", "")!!.split("|").isNotEmpty()) {
				sharedPreferences.getString("nightmareSummons", "")!!.split("|")
			} else {
				summonList
			}

			nightmareGroupNumber = sharedPreferences.getInt("nightmareGroupNumber", 1)
			nightmarePartyNumber = sharedPreferences.getInt("nightmarePartyNumber", 1)

			Log.d(tag, "[NIGHTMARE] Settings initialized.")
		} else {
			Log.d(tag, "[NIGHTMARE] Reusing settings from Farming Mode for $farmingMode Nightmare.")
			nightmareCombatScriptName = sharedPreferences.getString("nightmareCombatScriptName", "")!!
			nightmareCombatScript = sharedPreferences.getString("nightmareCombatScript", "")!!.split("|")
			nightmareSummons = sharedPreferences.getString("nightmareSummons", "")!!.split("|")
			nightmareGroupNumber = sharedPreferences.getInt("nightmareGroupNumber", 1)
			nightmarePartyNumber = sharedPreferences.getInt("nightmarePartyNumber", 1)
		}

		// Event
		enableLocationIncrementByOne = sharedPreferences.getBoolean("enableLocationIncrementByOne", false)
		enableSelectBottomCategory = sharedPreferences.getBoolean("selectBottomCategory", false)

		// Raid
		enableAutoExitRaid = sharedPreferences.getBoolean("enableAutoExitRaid", false)
		timeAllowedUntilAutoExitRaid = sharedPreferences.getInt("timeAllowedUntilAutoExitRaid", 1).toLong() * 60L * 1000L
		enableNoTimeout = sharedPreferences.getBoolean("enableNoTimeout", false)

		// Arcarum
		enableStopOnArcarumBoss = sharedPreferences.getBoolean("enableStopOnArcarumBoss", true)

		// Sandbox
		enableDefender = sharedPreferences.getBoolean("enableDefender", false)
		enableCustomDefenderSettings = sharedPreferences.getBoolean("enableCustomDefenderSettings", false)
		numberOfDefenders = sharedPreferences.getInt("numberOfDefenders", 1)
		numberOfDefeatedDefenders = 0
		engagedDefenderBattle = false
		defenderCombatScriptName = sharedPreferences.getString("defenderCombatScriptName", "")!!
		defenderCombatScript = sharedPreferences.getString("defenderCombatScript", "")!!.split("|")
		defenderGroupNumber = sharedPreferences.getInt("defenderGroupNumber", 1)
		defenderPartyNumber = sharedPreferences.getInt("defenderPartyNumber", 1)

		// Generic
		enableForceReload = sharedPreferences.getBoolean("enableForceReload", false)

		// Xeno Clash
		selectTopOption = sharedPreferences.getBoolean("selectTopOption", false)

		// Adjustment
		enableCalibrationAdjustment = sharedPreferences.getBoolean("enableCalibrationAdjustment", false)
		adjustCalibration = sharedPreferences.getInt("adjustCalibration", 5)
		enableGeneralAdjustment = sharedPreferences.getBoolean("enableGeneralAdjustment", false)
		adjustButtonSearchGeneral = sharedPreferences.getInt("adjustButtonSearchGeneral", 5)
		adjustHeaderSearchGeneral = sharedPreferences.getInt("adjustHeaderSearchGeneral", 5)
		enablePendingBattleAdjustment = sharedPreferences.getBoolean("enablePendingBattleAdjustment", false)
		adjustBeforePendingBattle = sharedPreferences.getInt("adjustBeforePendingBattle", 1)
		adjustPendingBattle = sharedPreferences.getInt("adjustPendingBattle", 2)
		enableCaptchaAdjustment = sharedPreferences.getBoolean("enableCaptchaAdjustment", false)
		adjustCaptcha = sharedPreferences.getInt("adjustCaptcha", 5)
		enableSupportSummonSelectionScreenAdjustment = sharedPreferences.getBoolean("enableSupportSummonSelectionScreenAdjustment", false)
		adjustSupportSummonSelectionScreen = sharedPreferences.getInt("adjustSupportSummonSelectionScreen", 30)
		enableCombatModeAdjustment = sharedPreferences.getBoolean("enableCombatModeAdjustment", false)
		adjustCombatStart = sharedPreferences.getInt("adjustCombatStart", 50)
		adjustDialog = sharedPreferences.getInt("adjustDialog", 2)
		adjustSkillUsage = sharedPreferences.getInt("adjustSkillUsage", 5)
		adjustSummonUsage = sharedPreferences.getInt("adjustSummonUsage", 5)
		adjustWaitingForReload = sharedPreferences.getInt("adjustWaitingForReload", 3)
		adjustWaitingForAttack = sharedPreferences.getInt("adjustWaitingForAttack", 100)
		adjustCheckForNoLootScreen = sharedPreferences.getInt("adjustCheckForNoLootScreen", 1)
		adjustCheckForBattleConcludedPopup = sharedPreferences.getInt("adjustCheckForBattleConcludedPopup", 1)
		adjustCheckForExpGainedPopup = sharedPreferences.getInt("adjustCheckForExpGainedPopup", 1)
		adjustCheckForLootCollectionScreen = sharedPreferences.getInt("adjustCheckForLootCollectionScreen", 1)
		enableArcarumAdjustment = sharedPreferences.getBoolean("enableArcarumAdjustment", false)
		adjustArcarumAction = sharedPreferences.getInt("adjustArcarumAction", 3)
		adjustArcarumStageEffect = sharedPreferences.getInt("adjustArcarumStageEffect", 10)

		// Android
		enableDelayTap = sharedPreferences.getBoolean("enableDelayTap", false)
		delayTapMilliseconds = sharedPreferences.getInt("delayTapMilliseconds", 1000)
		confidence = sharedPreferences.getFloat("confidence", 0.8f).toDouble() / 100.0
		confidenceAll = sharedPreferences.getFloat("confidenceAll", 0.8f).toDouble() / 100.0
		customScale = sharedPreferences.getFloat("customScale", 1.0f).toDouble()
		enableTestForHomeScreen = sharedPreferences.getBoolean("enableTestForHomeScreen", false)

		Log.d(tag, "Successfully loaded settings from SharedPreferences to memory.")
	}
}