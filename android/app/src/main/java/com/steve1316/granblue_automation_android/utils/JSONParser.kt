package com.steve1316.granblue_automation_android.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.steve1316.granblue_automation_android.MainActivity.loggerTag
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class JSONParser {
	private val tag = "${loggerTag}JSONParser"

	/**
	 * Initialize settings into SharedPreferences from the JSON file.
	 *
	 * @param myContext The application context.
	 */
	fun initializeSettings(myContext: Context) {
		Log.d(tag, "Loading settings from JSON file to SharedPreferences...")

		// Grab the JSON object from the file.
		val jString = File(myContext.getExternalFilesDir(null), "settings.json").bufferedReader().use { it.readText() }
		val jObj = JSONObject(jString)

		//////////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////////
		// Manually save all key-value pairs from JSON object to SharedPreferences.

		val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)

		try {
			val gameObj = jObj.getJSONObject("game")
			sharedPreferences.edit {
				putString("combatScriptName", gameObj.getString("combatScriptName"))
				putString("combatScript", toArrayList(gameObj.getJSONArray("combatScript")).joinToString("|"))
				putString("farmingMode", gameObj.getString("farmingMode"))
				putString("item", gameObj.getString("item"))
				putString("mission", gameObj.getString("mission"))
				putString("map", gameObj.getString("map"))
				putInt("itemAmount", gameObj.getInt("itemAmount"))
				putString("summons", toArrayList(gameObj.getJSONArray("summons")).joinToString("|"))
				putString("summonElements", toArrayList(gameObj.getJSONArray("summonElements")).joinToString("|"))
				putInt("groupNumber", gameObj.getInt("groupNumber"))
				putInt("partyNumber", gameObj.getInt("partyNumber"))
				putBoolean("debugMode", gameObj.getBoolean("debugMode"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val twitterObj = jObj.getJSONObject("twitter")
			sharedPreferences.edit {
				putString("twitterAPIKey", twitterObj.getString("twitterAPIKey"))
				putString("twitterAPIKeySecret", twitterObj.getString("twitterAPIKeySecret"))
				putString("twitterAccessToken", twitterObj.getString("twitterAccessToken"))
				putString("twitterAccessTokenSecret", twitterObj.getString("twitterAccessTokenSecret"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val discordObj = jObj.getJSONObject("discord")
			sharedPreferences.edit {
				putBoolean("enableDiscordNotifications", discordObj.getBoolean("enableDiscordNotifications"))
				putString("discordToken", discordObj.getString("discordToken"))
				putString("discordUserID", discordObj.getString("discordUserID"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val apiObj = jObj.getJSONObject("api")
			sharedPreferences.edit {
				putBoolean("enableOptInAPI", apiObj.getBoolean("enableOptInAPI"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val configurationObj = jObj.getJSONObject("configuration")
			sharedPreferences.edit {
				putBoolean("enableFullElixir", configurationObj.getBoolean("enableFullElixir"))
				putBoolean("enableSoulBalm", configurationObj.getBoolean("enableSoulBalm"))
				putBoolean("enableDelayBetweenRuns", configurationObj.getBoolean("enableDelayBetweenRuns"))
				putInt("delayBetweenRuns", configurationObj.getInt("delayBetweenRuns"))
				putBoolean("enableRandomizedDelayBetweenRuns", configurationObj.getBoolean("enableRandomizedDelayBetweenRuns"))
				putInt("delayBetweenRunsLowerBound", configurationObj.getInt("delayBetweenRunsLowerBound"))
				putInt("delayBetweenRunsUpperBound", configurationObj.getInt("delayBetweenRunsUpperBound"))
				putBoolean("enableRefreshDuringCombat", configurationObj.getBoolean("enableRefreshDuringCombat"))
				putBoolean("enableAutoQuickSummon", configurationObj.getBoolean("enableAutoQuickSummon"))
				putBoolean("enableBypassResetSummon", configurationObj.getBoolean("enableBypassResetSummon"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val nightmareObj = jObj.getJSONObject("nightmare")
			sharedPreferences.edit {
				putBoolean("enableNightmare", nightmareObj.getBoolean("enableNightmare"))
				putString("nightmareCombatScriptName", nightmareObj.getString("nightmareCombatScriptName"))
				putString("nightmareCombatScript", toArrayList(nightmareObj.getJSONArray("nightmareCombatScript")).joinToString("|"))
				putString("nightmareSummons", toArrayList(nightmareObj.getJSONArray("nightmareSummons")).joinToString("|"))
				putString("nightmareSummonElements", toArrayList(nightmareObj.getJSONArray("nightmareSummonElements")).joinToString("|"))
				putInt("nightmareGroupNumber", nightmareObj.getInt("nightmareGroupNumber"))
				putInt("nightmarePartyNumber", nightmareObj.getInt("nightmarePartyNumber"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val eventObj = jObj.getJSONObject("event")
			sharedPreferences.edit {
				putBoolean("enableLocationIncrementByOne", eventObj.getBoolean("enableLocationIncrementByOne"))
				putBoolean("selectBottomCategory", eventObj.getBoolean("selectBottomCategory"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val raidObj = jObj.getJSONObject("raid")
			sharedPreferences.edit {
				putBoolean("enableAutoExitRaid", raidObj.getBoolean("enableAutoExitRaid"))
				putInt("timeAllowedUntilAutoExitRaid", raidObj.getInt("timeAllowedUntilAutoExitRaid"))
				putBoolean("enableNoTimeout", raidObj.getBoolean("enableNoTimeout"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val arcarumObj = jObj.getJSONObject("arcarum")
			sharedPreferences.edit {
				putBoolean("enableStopOnArcarumBoss", arcarumObj.getBoolean("enableStopOnArcarumBoss"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val sandboxObj = jObj.getJSONObject("sandbox")
			sharedPreferences.edit {
				putBoolean("enableDefender", sandboxObj.getBoolean("enableDefender"))
				putBoolean("enableCustomDefenderSettings", sandboxObj.getBoolean("enableCustomDefenderSettings"))
				putString("defenderCombatScriptName", sandboxObj.getString("defenderCombatScriptName"))
				putString("defenderCombatScript", toArrayList(sandboxObj.getJSONArray("defenderCombatScript")).joinToString("|"))
				putInt("numberOfDefenders", sandboxObj.getInt("numberOfDefenders"))
				putInt("defenderGroupNumber", sandboxObj.getInt("defenderGroupNumber"))
				putInt("defenderPartyNumber", sandboxObj.getInt("defenderPartyNumber"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val genericObj = jObj.getJSONObject("generic")
			sharedPreferences.edit {
				putBoolean("enableForceReload", genericObj.getBoolean("enableForceReload"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val adjustmentObj = jObj.getJSONObject("adjustment")
			sharedPreferences.edit {
				putBoolean("enableCalibrationAdjustment", adjustmentObj.getBoolean("enableCalibrationAdjustment"))
				putInt("adjustCalibration", adjustmentObj.getInt("adjustCalibration"))
				putBoolean("enableGeneralAdjustment", adjustmentObj.getBoolean("enableGeneralAdjustment"))
				putInt("adjustButtonSearchGeneral", adjustmentObj.getInt("adjustButtonSearchGeneral"))
				putInt("adjustHeaderSearchGeneral", adjustmentObj.getInt("adjustHeaderSearchGeneral"))
				putBoolean("enablePendingBattleAdjustment", adjustmentObj.getBoolean("enablePendingBattleAdjustment"))
				putInt("adjustBeforePendingBattle", adjustmentObj.getInt("adjustBeforePendingBattle"))
				putInt("adjustPendingBattle", adjustmentObj.getInt("adjustPendingBattle"))
				putBoolean("enableCaptchaAdjustment", adjustmentObj.getBoolean("enableCaptchaAdjustment"))
				putInt("adjustCaptcha", adjustmentObj.getInt("adjustCaptcha"))
				putBoolean("enableSupportSummonSelectionScreenAdjustment", adjustmentObj.getBoolean("enableSupportSummonSelectionScreenAdjustment"))
				putInt("adjustSupportSummonSelectionScreen", adjustmentObj.getInt("adjustSupportSummonSelectionScreen"))
				putBoolean("enableCombatModeAdjustment", adjustmentObj.getBoolean("enableCombatModeAdjustment"))
				putInt("adjustCombatStart", adjustmentObj.getInt("adjustCombatStart"))
				putInt("adjustDialog", adjustmentObj.getInt("adjustDialog"))
				putInt("adjustSkillUsage", adjustmentObj.getInt("adjustSkillUsage"))
				putInt("adjustSummonUsage", adjustmentObj.getInt("adjustSummonUsage"))
				putInt("adjustWaitingForReload", adjustmentObj.getInt("adjustWaitingForReload"))
				putInt("adjustWaitingForAttack", adjustmentObj.getInt("adjustWaitingForAttack"))
				putInt("adjustCheckForNoLootScreen", adjustmentObj.getInt("adjustCheckForNoLootScreen"))
				putInt("adjustCheckForBattleConcludedPopup", adjustmentObj.getInt("adjustCheckForBattleConcludedPopup"))
				putInt("adjustCheckForExpGainedPopup", adjustmentObj.getInt("adjustCheckForExpGainedPopup"))
				putInt("adjustCheckForLootCollectionScreen", adjustmentObj.getInt("adjustCheckForLootCollectionScreen"))
				putBoolean("enableArcarumAdjustment", adjustmentObj.getBoolean("enableArcarumAdjustment"))
				putInt("adjustArcarumAction", adjustmentObj.getInt("adjustArcarumAction"))
				putInt("adjustArcarumStageEffect", adjustmentObj.getInt("adjustArcarumStageEffect"))
				commit()
			}
		} catch (e: Exception) {
		}

		try {
			val androidObj = jObj.getJSONObject("android")
			sharedPreferences.edit {
				putBoolean("enableDelayTap", androidObj.getBoolean("enableDelayTap"))
				putInt("delayTapMilliseconds", androidObj.getInt("delayTapMilliseconds"))
				putFloat("confidence", androidObj.getDouble("confidence").toFloat())
				putFloat("confidenceAll", androidObj.getDouble("confidenceAll").toFloat())
				putFloat("customScale", androidObj.getDouble("customScale").toFloat())
				putBoolean("enableTestForHomeScreen", androidObj.getBoolean("enableTestForHomeScreen"))
				commit()
			}
		} catch (e: Exception) {
		}

		Log.d(tag, "Successfully loaded settings into SharedPreferences.")
	}

	/**
	 * Convert JSONArray to ArrayList object.
	 *
	 * @param jsonArray The JSONArray object to be converted.
	 * @return The converted ArrayList object.
	 */
	private fun toArrayList(jsonArray: JSONArray): ArrayList<String> {
		val newArrayList: ArrayList<String> = arrayListOf()

		var i = 0
		while (i < jsonArray.length()) {
			newArrayList.add(jsonArray.get(i) as String)
			i++
		}

		return newArrayList
	}
}