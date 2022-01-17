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

class JSONParser() {
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

		val twitterObj = jObj.getJSONObject("twitter")
		sharedPreferences.edit {
			putString("twitterAPIKey", twitterObj.getString("twitterAPIKey"))
			putString("twitterAPIKeySecret", twitterObj.getString("twitterAPIKeySecret"))
			putString("twitterAccessToken", twitterObj.getString("twitterAccessToken"))
			putString("twitterAccessTokenSecret", twitterObj.getString("twitterAccessTokenSecret"))
			commit()
		}

		val discordObj = jObj.getJSONObject("discord")
		sharedPreferences.edit {
			putBoolean("enableDiscordNotifications", discordObj.getBoolean("enableDiscordNotifications"))
			putString("discordToken", discordObj.getString("discordToken"))
			putString("discordUserID", discordObj.getString("discordUserID"))
			commit()
		}

		val configurationObj = jObj.getJSONObject("configuration")
		sharedPreferences.edit {
			putBoolean("enableAutoRestore", configurationObj.getBoolean("enableAutoRestore"))
			putBoolean("enableFullElixir", configurationObj.getBoolean("enableFullElixir"))
			putBoolean("enableSoulBalm", configurationObj.getBoolean("enableSoulBalm"))
			putBoolean("enableDelayBetweenRuns", configurationObj.getBoolean("enableDelayBetweenRuns"))
			putInt("delayBetweenRuns", configurationObj.getInt("delayBetweenRuns"))
			putBoolean("enableRandomizedDelayBetweenRuns", configurationObj.getBoolean("enableRandomizedDelayBetweenRuns"))
			putInt("delayBetweenRunsLowerBound", configurationObj.getInt("delayBetweenRunsLowerBound"))
			putInt("delayBetweenRunsUpperBound", configurationObj.getInt("delayBetweenRunsUpperBound"))
			commit()
		}

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

		val eventObj = jObj.getJSONObject("event")
		sharedPreferences.edit {
			putBoolean("enableLocationIncrementByOne", eventObj.getBoolean("enableLocationIncrementByOne"))
			commit()
		}

		val raidObj = jObj.getJSONObject("raid")
		sharedPreferences.edit {
			putBoolean("enableAutoExitRaid", raidObj.getBoolean("enableAutoExitRaid"))
			putInt("timeAllowedUntilAutoExitRaid", raidObj.getInt("timeAllowedUntilAutoExitRaid"))
			putBoolean("enableNoTimeout", raidObj.getBoolean("enableNoTimeout"))
			commit()
		}

		val arcarumObj = jObj.getJSONObject("arcarum")
		sharedPreferences.edit {
			putBoolean("enableStopOnArcarumBoss", arcarumObj.getBoolean("enableStopOnArcarumBoss"))
			commit()
		}

		val genericObj = jObj.getJSONObject("generic")
		sharedPreferences.edit {
			putBoolean("enableForceReload", genericObj.getBoolean("enableForceReload"))
			commit()
		}

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