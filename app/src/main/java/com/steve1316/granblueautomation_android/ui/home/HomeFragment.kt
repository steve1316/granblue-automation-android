package com.steve1316.granblueautomation_android.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.github.javiersantos.appupdater.AppUpdater
import com.github.javiersantos.appupdater.enums.UpdateFrom
import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.R
import com.steve1316.granblueautomation_android.data.ConfigData
import com.steve1316.granblueautomation_android.utils.JSONParser
import com.steve1316.granblueautomation_android.utils.MediaProjectionService
import com.steve1316.granblueautomation_android.utils.MessageLog
import com.steve1316.granblueautomation_android.utils.MyAccessibilityService
import java.io.File

class HomeFragment : Fragment() {
	private val loggerTag: String = "${MainActivity.loggerTag}HomeFragment"
	private var firstBoot = false
	private var firstRun = true
	
	private lateinit var myContext: Context
	private lateinit var homeFragmentView: View
	private lateinit var startButton: Button
	
	@SuppressLint("SetTextI18n")
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		myContext = requireContext()
		
		homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false)
		
		val jsonParser = JSONParser(myContext)
		
		// Start or stop the MediaProjection service via this button.
		startButton = homeFragmentView.findViewById(R.id.start_button)
		startButton.setOnClickListener {
			val readyCheck = startReadyCheck()
			if (readyCheck && !MediaProjectionService.isRunning) {
				startProjection()
				startButton.text = getString(R.string.bot_stop)
				
				// This is needed because onResume() is immediately called right after accepting the MediaProjection and it has not been properly
				// initialized yet so it would cause the button's text to revert back to "Start".
				firstBoot = true
			} else if (MediaProjectionService.isRunning) {
				stopProjection()
				startButton.text = getString(R.string.bot_start)
			}
		}
		
		// Check if the application created the config.json file yet and if not, create it.
		val file = File(myContext.getExternalFilesDir(null), "config.json")
		if (!file.exists()) {
			file.createNewFile()
			
			val content = "{\n" +
					"    \"discord\": {\n" +
					"        \"discordToken\": \"\",\n" +
					"        \"userID\": \"\"\n" +
					"    },\n" +
					"    \"twitter\": {\n" +
					"        \"apiKey\": \"\",\n" +
					"        \"apiKeySecret\": \"\",\n" +
					"        \"accessToken\": \"\",\n" +
					"        \"accessTokenSecret\": \"\"\n" +
					"    },\n" +
					"    \"refill\": {\n" +
					"        \"fullElixir\": false,\n" +
					"        \"soulBalm\": false\n" +
					"    },\n" +
					"    \"dimensionalHalo\": {\n" +
					"        \"enableDimensionalHalo\": false,\n" +
					"        \"dimensionalHaloSummonList\": [],\n" +
					"        \"dimensionalHaloGroupNumber\": 0,\n" +
					"        \"dimensionalHaloPartyNumber\": 0\n" +
					"    },\n" +
					"    \"event\": {\n" +
					"        \"enableEventNightmare\": false,\n" +
					"        \"eventNightmareSummonList\": [],\n" +
					"        \"eventNightmareGroupNumber\": 0,\n" +
					"        \"eventNightmarePartyNumber\": 0\n" +
					"    },\n" +
					"    \"rotb\": {\n" +
					"        \"enableROTBExtremePlus\": false,\n" +
					"        \"rotbExtremePlusSummonList\": [],\n" +
					"        \"rotbExtremePlusGroupNumber\": 0,\n" +
					"        \"rotbExtremePlusPartyNumber\": 0\n" +
					"    },\n" +
					"    \"xenoClash\": {\n" +
					"        \"enableXenoClashNightmare\": false,\n" +
					"        \"xenoClashNightmareSummonList\": [],\n" +
					"        \"xenoClashNightmareGroupNumber\": 0,\n" +
					"        \"xenoClashNightmarePartyNumber\": 0\n" +
					"    }\n" +
					"}\n"
			
			file.writeText(content)
			
			Log.d(loggerTag, "Created config.json in internal storage.")
		} else {
			Log.d(loggerTag, "config.json already exists.")
			
			val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
			
			// Save the Twitter API keys and tokens and every other settings in the config.json to SharedPreferences.
			try {
				if (file.exists()) {
					jsonParser.constructConfigClass()
					
					sharedPreferences.edit {
						putString("discordToken", ConfigData.discordToken)
						putString("userID", ConfigData.userID)
						
						putBoolean("enableEventNightmare", ConfigData.enableEventNightmare)
						putStringSet("eventNightmareSummonList", ConfigData.eventNightmareSummonList.toMutableSet())
						putInt("eventNightmareGroupNumber", ConfigData.eventNightmareGroupNumber)
						putInt("eventNightmarePartyNumber", ConfigData.eventNightmarePartyNumber)
						
						putBoolean("enableDimensionalHalo", ConfigData.enableDimensionalHalo)
						putStringSet("dimensionalHaloSummonList", ConfigData.dimensionalHaloSummonList.toMutableSet())
						putInt("dimensionalHaloGroupNumber", ConfigData.dimensionalHaloGroupNumber)
						putInt("dimensionalHaloPartyNumber", ConfigData.dimensionalHaloPartyNumber)
						
						putBoolean("enableROTBExtremePlus", ConfigData.enableROTBExtremePlus)
						putStringSet("rotbExtremePlusSummonList", ConfigData.rotbExtremePlusSummonList.toMutableSet())
						putInt("rotbExtremePlusGroupNumber", ConfigData.rotbExtremePlusGroupNumber)
						putInt("rotbExtremePlusPartyNumber", ConfigData.rotbExtremePlusPartyNumber)
						
						putBoolean("enableXenoClashNightmare", ConfigData.enableXenoClashNightmare)
						putStringSet("xenoClashNightmareSummonList", ConfigData.xenoClashNightmareSummonList.toMutableSet())
						putInt("xenoClashNightmareGroupNumber", ConfigData.xenoClashNightmareGroupNumber)
						putInt("xenoClashNightmarePartyNumber", ConfigData.xenoClashNightmarePartyNumber)
						commit()
					}
					
					Log.d(loggerTag, "Saved config.json settings to SharedPreferences.")
				}
			} catch (e: Exception) {
				Log.e(loggerTag, "Encountered error while saving Twitter API credentials to SharedPreferences from config: ${e.stackTraceToString()}")
				Log.e(loggerTag, "Clearing any existing Twitter API credentials from SharedPreferences...")
				
				sharedPreferences.edit {
					remove("apiKey")
					remove("apiKeySecret")
					remove("accessToken")
					remove("accessTokenSecret")
					commit()
				}
			}
		}
		
		// Update the TextView here based on the information of the SharedPreferences. Required preferences to check for are Farming Mode, Mission,
		// Item, and Summons. The rest are already given default values if the user never set them. Except Combat Script as if it is not set by the
		// user, set it for them to be Full/Semi Auto by default.
		val settingsStatusTextView: TextView = homeFragmentView.findViewById(R.id.settings_status)
		
		val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
		
		var combatScriptName: String = sharedPreferences.getString("combatScriptName", "")!!
		var farmingMode: String = sharedPreferences.getString("farmingMode", "")!!
		val mapName: String = sharedPreferences.getString("mapName", "")!!
		var missionName: String = sharedPreferences.getString("missionName", "")!!
		var itemName: String = sharedPreferences.getString("itemName", "")!!
		val itemAmount: Int = sharedPreferences.getInt("itemAmount", 1)
		var summon = sharedPreferences.getString("summon", "")!!.split("|")
		val groupNumber: Int = sharedPreferences.getInt("groupNumber", 1)
		val partyNumber: Int = sharedPreferences.getInt("partyNumber", 1)
		val enableAutoExitCombat: Boolean = sharedPreferences.getBoolean("enableAutoExitCombat", false)
		val autoExitCombatMinutes: Int = sharedPreferences.getInt("autoExitCombatMinutes", 5)
		val enableDelayBetweenRuns: Boolean = sharedPreferences.getBoolean("enableDelayBetweenRuns", false)
		val enableRandomizedDelayBetweenRuns: Boolean = sharedPreferences.getBoolean("enableRandomizedDelayBetweenRuns", false)
		val delayBetweenRuns: Int = sharedPreferences.getInt("delayBetweenRuns", 1)
		val randomizedDelayBetweenRuns: Int = sharedPreferences.getInt("randomizedDelayBetweenRuns", 1)
		val confidence: Int = sharedPreferences.getInt("confidence", 80)
		val confidenceAll: Int = sharedPreferences.getInt("confidenceAll", 80)
		val customScale: Double = sharedPreferences.getString("customScale", "1.0")!!.toDouble()
		val enableDiscord: Boolean = sharedPreferences.getBoolean("enableDiscord", false)
		val enableSkipAutoRestore: Boolean = sharedPreferences.getBoolean("enableSkipAutoRestore", true)
		val debugMode: Boolean = sharedPreferences.getBoolean("debugMode", false)
		val enableHomeTest: Boolean = sharedPreferences.getBoolean("enableHomeTest", false)
		val enableNoTimeout: Boolean = sharedPreferences.getBoolean("enableNoTimeout", false)
		val enableAdditionalDelayTap: Boolean = sharedPreferences.getBoolean("enableAdditionalDelayTap", false)
		val additionalDelayTapRange: Int = sharedPreferences.getInt("additionalDelayTapRange", 1000)
		
		startButton.isEnabled = (farmingMode != "" && missionName != "" && itemName != "" && ((farmingMode != "Coop" && summon.isNotEmpty() && summon[0] != "") ||
				((farmingMode == "Coop" || farmingMode == "Arcarum") && summon.isNotEmpty() && summon[0] == "")))
		
		if (combatScriptName == "") {
			combatScriptName = "None selected. Using default Semi/Full Auto script."
		}
		
		if (farmingMode == "") {
			farmingMode = "Missing"
		}
		
		val mapString: String = if (mapName != "") {
			"Map: $mapName\n"
		} else {
			""
		}
		
		if (missionName == "") {
			missionName = "Missing"
		}
		
		if (itemName == "") {
			itemName = "Missing"
		}
		
		if (summon[0] == "" && farmingMode != "Coop") {
			summon = listOf("Requires at least 1 Summon")
		}
		
		val autoExitCombatString: String = if (enableAutoExitCombat) {
			"Enabled\nAuto Exit Maximum Time: $autoExitCombatMinutes minutes"
		} else {
			"Disabled"
		}
		
		val enableDelayBetweenRunsString: String = if (enableDelayBetweenRuns) {
			"Enabled"
		} else {
			"Disabled"
		}
		
		val enableRandomizedDelayBetweenRunsString: String = if (enableRandomizedDelayBetweenRuns) {
			"Enabled"
		} else {
			"Disabled"
		}
		
		val delayString: String = when {
			enableDelayBetweenRuns -> {
				"Delay Between Runs: $delayBetweenRuns seconds\n"
			}
			enableRandomizedDelayBetweenRuns -> {
				"Delay Between Runs Lower Bound: $delayBetweenRuns seconds\nDelay Between Runs Upper Bound: $randomizedDelayBetweenRuns seconds\n"
			}
			else -> {
				""
			}
		}
		
		val customScaleString: String = if (customScale == 1.0) {
			"1.0 (Default)"
		} else {
			"$customScale"
		}
		
		val enableDiscordString: String = if (enableDiscord) {
			"Enabled"
		} else {
			"Disabled"
		}
		
		val enableSkipAutoRestoreString: String = if (enableSkipAutoRestore) {
			"Enabled"
		} else {
			"Disabled"
		}
		
		val enableDebugModeString: String = if (debugMode) {
			"Enabled"
		} else {
			"Disabled"
		}
		
		val enableHomeTestString: String = if (enableHomeTest) {
			"Enabled"
		} else {
			"Disabled"
		}
		
		val enableNoTimeoutString: String = if (farmingMode == "Raid" && enableNoTimeout) {
			"Enable No Timeout: Enabled\n"
		} else {
			""
		}
		
		val enableAdditionalDelayTapString: String = if (enableAdditionalDelayTap) {
			"Enabled"
		} else {
			"Disabled"
		}
		
		val additionalDelayTapRangeString: String = if (enableAdditionalDelayTap) {
			"Base Point for Additional Delay: ${additionalDelayTapRange}ms\n"
		} else {
			""
		}
		
		settingsStatusTextView.setTextColor(Color.WHITE)
		settingsStatusTextView.text = "---------- Farming Mode Settings ----------\n" +
				"Mode: $farmingMode\n" +
				enableNoTimeoutString +
				mapString +
				"Mission: $missionName\n" +
				"Item: x$itemAmount $itemName\n" +
				"---------- Combat Mode Settings ----------\n" +
				"Combat Script: $combatScriptName\n" +
				"Summon: $summon\n" +
				"Group: $groupNumber\n" +
				"Party: $partyNumber\n" +
				"Auto Exit Combat: $autoExitCombatString\n" +
				"---------- Delay Settings ----------\n" +
				"Delay Between Runs: $enableDelayBetweenRunsString\n" +
				"Randomized Between Runs: $enableRandomizedDelayBetweenRunsString\n" +
				delayString +
				"---------- Misc Settings ----------\n" +
				"Confidence for Single Image Matching: $confidence%\n" +
				"Confidence for Multiple Image Matching: $confidenceAll%\n" +
				"Scale: $customScaleString\n" +
				"Enable Additional Delay Before Tap: $enableAdditionalDelayTapString\n" +
				additionalDelayTapRangeString +
				"Discord Notifications: $enableDiscordString\n" +
				"Enable Skip checks for AP/EP: $enableSkipAutoRestoreString\n" +
				"Debug Mode: $enableDebugModeString\n" +
				"Enable Test for Home screen: $enableHomeTestString"
		
		// Now construct the data files if this is the first time.
		if (firstRun) {
			jsonParser.constructDataClasses()
			firstRun = false
		}
		
		return homeFragmentView
	}
	
	override fun onResume() {
		super.onResume()
		
		// Update the button's text depending on if the MediaProjection service is running.
		if (!firstBoot) {
			if (MediaProjectionService.isRunning) {
				startButton.text = getString(R.string.bot_stop)
			} else {
				startButton.text = getString(R.string.bot_start)
			}
		}
		
		// Setting this false here will ensure that stopping the MediaProjection Service outside of this application will update this button's text.
		firstBoot = false
		
		// Now update the Message Log inside the ScrollView with the latest logging messages from the bot.
		Log.d(loggerTag, "Now updating the Message Log TextView...")
		val messageLogTextView = homeFragmentView.findViewById<TextView>(R.id.message_log)
		messageLogTextView.text = ""
		var index = 0
		
		// Get local copies of the message log.
		val messageLog = MessageLog.messageLog
		val messageLogSize = MessageLog.messageLog.size
		while (index < messageLogSize) {
			messageLogTextView.append("\n" + messageLog[index])
			index += 1
		}
		
		// Set up the app updater to check for the latest update from GitHub.
		AppUpdater(myContext)
			.setUpdateFrom(UpdateFrom.XML)
			.setUpdateXML("https://raw.githubusercontent.com/steve1316/granblue-automation-android/main/app/update.xml")
			.start()
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			// Start up the MediaProjection service after the user accepts the onscreen prompt.
			myContext.startService(data?.let { MediaProjectionService.getStartIntent(myContext, resultCode, data) })
		}
	}
	
	/**
	 * Checks to see if the application is ready to start.
	 *
	 * @return True if the application has overlay permission and has enabled the Accessibility Service for it. Otherwise, return False.
	 */
	private fun startReadyCheck(): Boolean {
		if (!checkForOverlayPermission() || !checkForAccessibilityPermission()) {
			return false
		}
		
		return true
	}
	
	/**
	 * Starts the MediaProjection Service.
	 */
	private fun startProjection() {
		val mediaProjectionManager = context?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
		startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 100)
	}
	
	/**
	 * Stops the MediaProjection Service.
	 */
	private fun stopProjection() {
		context?.startService(MediaProjectionService.getStopIntent(requireContext()))
	}
	
	/**
	 * Checks if the application has permission to draw overlays. If not, it will direct the user to enable it.
	 *
	 * Source is from https://github.com/Fate-Grand-Automata/FGA/blob/master/app/src/main/java/com/mathewsachin/fategrandautomata/ui/MainFragment.kt
	 *
	 * @return True if it has permission. False otherwise.
	 */
	private fun checkForOverlayPermission(): Boolean {
		if (!Settings.canDrawOverlays(requireContext())) {
			Log.d(loggerTag, "Application is missing overlay permission.")
			
			AlertDialog.Builder(requireContext()).apply {
				setTitle(R.string.overlay_disabled)
				setMessage(R.string.overlay_disabled_message)
				setPositiveButton(R.string.go_to_settings) { _, _ ->
					// Send the user to the Overlay Settings.
					val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${requireContext().packageName}"))
					startActivity(intent)
				}
				setNegativeButton(android.R.string.cancel, null)
			}.show()
			
			return false
		}
		
		Log.d(loggerTag, "Application has permission to draw overlay.")
		return true
	}
	
	/**
	 * Checks if the Accessibility Service for this application is enabled. If not, it will direct the user to enable it.
	 *
	 * Source is from https://stackoverflow.com/questions/18094982/detect-if-my-accessibility-service-is-enabled/18095283#18095283
	 *
	 * @return True if it is enabled. False otherwise.
	 */
	private fun checkForAccessibilityPermission(): Boolean {
		val prefString = Settings.Secure.getString(myContext.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
		
		if (prefString != null && prefString.isNotEmpty()) {
			// Check the string of enabled accessibility services to see if this application's accessibility service is there.
			val enabled = prefString.contains(myContext.packageName.toString() + "/" + MyAccessibilityService::class.java.name)
			
			if (enabled) {
				Log.d(loggerTag, "This application's Accessibility Service is currently turned on.")
				return true
			}
		}
		
		// Moves the user to the Accessibility Settings if the service is not detected.
		AlertDialog.Builder(myContext).apply {
			setTitle(R.string.accessibility_disabled)
			setMessage(R.string.accessibility_disabled_message)
			setPositiveButton(R.string.go_to_settings) { _, _ ->
				Log.d(loggerTag, "Accessibility Service is not detected. Moving user to Accessibility Settings.")
				val accessibilitySettingsIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
				myContext.startActivity(accessibilitySettingsIntent)
			}
			setNegativeButton(android.R.string.cancel, null)
			show()
		}
		
		return false
	}
}
