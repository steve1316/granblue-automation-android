package com.steve1316.granblueautomation_android.ui.settings

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.core.content.edit
import androidx.preference.*
import com.sksamuel.hoplite.ConfigLoader
import com.steve1316.granblueautomation_android.MainActivity
import com.steve1316.granblueautomation_android.R
import com.steve1316.granblueautomation_android.data.ConfigData
import com.steve1316.granblueautomation_android.data.ItemData
import com.steve1316.granblueautomation_android.data.MissionData
import java.io.File

class SettingsFragment : PreferenceFragmentCompat() {
	private val loggerTag: String = "${MainActivity.loggerTag}_SettingsFragment"
	
	private lateinit var sharedPreferences: SharedPreferences
	
	private lateinit var builder: AlertDialog.Builder
	private lateinit var summonListItems: Array<String>
	private lateinit var summonListCheckedItems: BooleanArray
	private var userSelectedSummonList: ArrayList<Int> = arrayListOf()
	
	private val editTextListener = EditTextPreference.OnBindEditTextListener { editText ->
		editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
	}
	
	// This listener is triggered whenever the user changes a Preference setting in the Settings Page.
	private val onSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
		if (key != null) {
			when (key) {
				"farmingModePicker" -> {
					// Fill out the new entries and values for Missions based on the newly chosen Farming Mode.
					val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
					val missionPicker: ListPreference = findPreference("missionPicker")!!
					
					sharedPreferences.edit {
						putString("farmingMode", farmingModePicker.value)
						commit()
					}
					
					if (farmingModePicker.value == "Coop" || farmingModePicker.value == "Arcarum") {
						val summonPicker: Preference = findPreference("summonPicker")!!
						summonPicker.title = "Select Summon(s)"
						summonPicker.summary = "Select the Summon(s) in order from highest to lowest priority for Combat Mode."
						summonPicker.isEnabled = false
						
						// Go through every checked item and set them to false.
						if (this::summonListCheckedItems.isInitialized) {
							for (i in summonListCheckedItems.indices) {
								summonListCheckedItems[i] = false
							}
						}
						
						// After that, clear the list of user-selected summons and the one in SharedPreferences.
						userSelectedSummonList.clear()
						sharedPreferences.edit {
							remove("summon")
							commit()
						}
					} else {
						val summonPicker: Preference = findPreference("summonPicker")!!
						summonPicker.title = "Select Summon(s)*"
						
						// Build the Summon Selection AlertDialog.
						summonPicker.isEnabled = true
						createSummonDialog()
					}
					
					// Populate the Mission picker with the missions associated with the newly chosen Farming Mode.
					populateMissionListPreference()
					
					// Now reset the values of the Mission and Item pickers to indicate that a new Farming Mode means a new Mission which in turn will
					// affect what the user wants to choose next.
					resetMissionSharedPreference()
					resetItemSharedPreference()
					
					// Disable the following Preferences.
					val itemPicker: ListPreference = findPreference("itemPicker")!!
					val itemAmountPicker: SeekBarPreference = findPreference("itemAmountPicker")!!
					val combatModePreferenceCategory: PreferenceCategory = findPreference("combatModeTitle")!!
					itemPicker.isEnabled = false
					itemAmountPicker.isEnabled = false
					combatModePreferenceCategory.isEnabled = false
					
					// Finally, enable the Mission picker.
					missionPicker.isEnabled = true
				}
				"missionPicker" -> {
					val missionPicker: ListPreference = findPreference("missionPicker")!!
					val itemPicker: ListPreference = findPreference("itemPicker")!!
					
					sharedPreferences.edit {
						putString("missionName", missionPicker.value)
						commit()
					}
					
					// Populate the Item list based on the Mission selected. Save the Map for the Mission if possible.
					populateItemListPreference()
					
					// Now reset the value of the Item picker.
					resetItemSharedPreference()
					
					// Disable the following Preferences.
					val combatModePreferenceCategory: PreferenceCategory = findPreference("combatModeTitle")!!
					combatModePreferenceCategory.isEnabled = false
					
					// Finally, enable the Item picker.
					itemPicker.isEnabled = true
				}
				"itemPicker" -> {
					val itemPicker: ListPreference = findPreference("itemPicker")!!
					
					sharedPreferences.edit {
						putString("itemName", itemPicker.value)
						commit()
					}
					
					// Enable the Item Amount picker.
					val itemAmountPicker: SeekBarPreference = findPreference("itemAmountPicker")!!
					itemAmountPicker.isEnabled = true
					
					// Finally, enable the Combat Mode PreferenceCategory.
					val combatModePreferenceCategory: PreferenceCategory = findPreference("combatModeTitle")!!
					combatModePreferenceCategory.isEnabled = true
				}
				"itemAmountPicker" -> {
					val itemAmountPicker: SeekBarPreference = findPreference("itemAmountPicker")!!
					sharedPreferences.edit {
						putInt("itemAmount", itemAmountPicker.value)
						commit()
					}
				}
				"groupPicker" -> {
					val groupPicker: ListPreference = findPreference("groupPicker")!!
					sharedPreferences.edit {
						putInt("groupNumber", groupPicker.value.last().toString().toInt())
						commit()
					}
				}
				"partyPicker" -> {
					val partyPicker: ListPreference = findPreference("partyPicker")!!
					sharedPreferences.edit {
						putInt("partyNumber", partyPicker.value.last().toString().toInt())
						commit()
					}
				}
				"enableAutoExitCombat" -> {
					val enableAutoExitCombat: CheckBoxPreference = findPreference("enableAutoExitCombat")!!
					val autoExitCombatMinutes: SeekBarPreference = findPreference("autoExitCombatMinutes")!!
					autoExitCombatMinutes.isVisible = enableAutoExitCombat.isChecked
					sharedPreferences.edit {
						putBoolean("enableAutoExitCombat", enableAutoExitCombat.isChecked)
					}
				}
				"autoExitCombatMinutes" -> {
					val autoExitCombatMinutes: SeekBarPreference = findPreference("autoExitCombatMinutes")!!
					sharedPreferences.edit {
						putInt("autoExitCombatMinutes", autoExitCombatMinutes.value)
					}
				}
				"delayBetweenRunsSwitch" -> {
					val delayBetweenRunsSwitch: SwitchPreference = findPreference("delayBetweenRunsSwitch")!!
					val delayBetweenRunsSeekBar: SeekBarPreference = findPreference("delayBetweenRunsSeekBar")!!
					val randomizedDelayBetweenRunsSwitch: SwitchPreference = findPreference("randomizedDelayBetweenRunsSwitch")!!
					val randomizedDelayBetweenRunsSeekBar: SeekBarPreference = findPreference("randomizedDelayBetweenRunsSeekBar")!!
					
					if (delayBetweenRunsSwitch.isChecked) {
						// Disable the randomized delay between runs settings.
						randomizedDelayBetweenRunsSwitch.isChecked = false
						randomizedDelayBetweenRunsSeekBar.isVisible = false
						
						delayBetweenRunsSeekBar.title = if (randomizedDelayBetweenRunsSeekBar.isVisible) {
							"Set Lower Bound for Delay in Seconds"
						} else {
							"Set Delay In Seconds"
						}
						
						delayBetweenRunsSeekBar.isVisible = true
					} else {
						delayBetweenRunsSeekBar.isVisible = false
					}
					
					sharedPreferences.edit {
						putBoolean("enableDelayBetweenRuns", delayBetweenRunsSwitch.isChecked)
						putBoolean("enableRandomizedDelayBetweenRuns", false)
						commit()
					}
				}
				"randomizedDelayBetweenRunsSwitch" -> {
					// Reveal the Randomized Delay Between Runs SeekBar and then constrain its minimum value to the current value of the singular Delay Between Runs SeekBar.
					val randomizedDelayBetweenRunsSwitch: SwitchPreference = findPreference("randomizedDelayBetweenRunsSwitch")!!
					val randomizedDelayBetweenRunsSeekBar: SeekBarPreference = findPreference("randomizedDelayBetweenRunsSeekBar")!!
					val delayBetweenRunsSeekBar: SeekBarPreference = findPreference("delayBetweenRunsSeekBar")!!
					
					if (randomizedDelayBetweenRunsSwitch.isChecked) {
						randomizedDelayBetweenRunsSeekBar.isVisible = true
						if (randomizedDelayBetweenRunsSeekBar.value < delayBetweenRunsSeekBar.value) {
							// Makes sure that the value of the randomized SeekBar is not lower as it is the upper limit.
							randomizedDelayBetweenRunsSeekBar.value = delayBetweenRunsSeekBar.value
						}
						randomizedDelayBetweenRunsSeekBar.min = delayBetweenRunsSeekBar.value
						
						// Disable the delay between runs settings.
						val delayBetweenRunsSwitch: SwitchPreference = findPreference("delayBetweenRunsSwitch")!!
						delayBetweenRunsSwitch.isChecked = false
						delayBetweenRunsSeekBar.title = if (randomizedDelayBetweenRunsSeekBar.isVisible) {
							"Set Lower Bound for Delay in Seconds"
						} else {
							"Set Delay In Seconds"
						}
						
						delayBetweenRunsSeekBar.isVisible = true
					} else {
						delayBetweenRunsSeekBar.isVisible = false
						randomizedDelayBetweenRunsSeekBar.isVisible = false
					}
					
					sharedPreferences.edit {
						putBoolean("enableDelayBetweenRuns", false)
						putBoolean("enableRandomizedDelayBetweenRuns", randomizedDelayBetweenRunsSwitch.isChecked)
						commit()
					}
				}
				"delayBetweenRunsSeekBar" -> {
					val delayBetweenRunsSeekBar: SeekBarPreference = findPreference("delayBetweenRunsSeekBar")!!
					val randomizedDelayBetweenRunsSeekBar: SeekBarPreference = findPreference("randomizedDelayBetweenRunsSeekBar")!!
					
					if (randomizedDelayBetweenRunsSeekBar.value < delayBetweenRunsSeekBar.value) {
						// Makes sure that the value of the randomized SeekBar is not lower as it is the upper limit.
						randomizedDelayBetweenRunsSeekBar.value = delayBetweenRunsSeekBar.value
					}
					randomizedDelayBetweenRunsSeekBar.min = delayBetweenRunsSeekBar.value
					
					sharedPreferences.edit {
						putInt("delayBetweenRuns", delayBetweenRunsSeekBar.value.toString().toInt())
						commit()
					}
				}
				"randomizedDelayBetweenRunsSeekBar" -> {
					val randomizedDelayBetweenRunsSeekBar: SeekBarPreference = findPreference("randomizedDelayBetweenRunsSeekBar")!!
					
					sharedPreferences.edit {
						putInt("randomizedDelayBetweenRuns", randomizedDelayBetweenRunsSeekBar.value.toString().toInt())
						commit()
					}
				}
				"confidenceSeekBar" -> {
					val confidenceSeekBar: SeekBarPreference = findPreference("confidenceSeekBar")!!
					sharedPreferences.edit {
						putInt("confidence", confidenceSeekBar.value)
					}
				}
				"confidenceAllSeekBar" -> {
					val confidenceAllSeekBar: SeekBarPreference = findPreference("confidenceAllSeekBar")!!
					sharedPreferences.edit {
						putInt("confidenceAll", confidenceAllSeekBar.value)
					}
				}
				"customScale" -> {
					val customScaleEditText: EditTextPreference = findPreference("customScale")!!
					sharedPreferences.edit {
						if (customScaleEditText.text != "1" && customScaleEditText.text != "1.0" && customScaleEditText.text != "" && customScaleEditText.text != "0" && customScaleEditText.text != "0.0") {
							customScaleEditText.summary =
								"Set the scale at which to resize existing image assets to match what would be shown on your device. Internally supported are 720p, 1080p, 1600p (Portrait) and 2560p (Landscape) in width.\n\nScale: ${customScaleEditText.text.toDouble()}"
							putString("customScale", customScaleEditText.text)
						} else {
							customScaleEditText.summary =
								"Set the scale at which to resize existing image assets to match what would be shown on your device. Internally supported are 720p, 1080p, 1600p (Portrait) and 2560p (Landscape) in width.\n\nScale: 1.0 (Default)"
							putString("customScale", "1.0")
						}
					}
				}
				"enableDiscord" -> {
					val enableDiscordCheckBox: CheckBoxPreference = findPreference("enableDiscord")!!
					sharedPreferences.edit {
						putBoolean("enableDiscord", enableDiscordCheckBox.isChecked)
						commit()
					}
				}
				"enableSkipAutoRestore" -> {
					val enableSkipAutoRestoreCheckBox: CheckBoxPreference = findPreference("enableSkipAutoRestore")!!
					sharedPreferences.edit {
						putBoolean("enableSkipAutoRestore", enableSkipAutoRestoreCheckBox.isChecked)
						commit()
					}
				}
				"debugModeCheckBox" -> {
					val debugModeCheckBox: CheckBoxPreference = findPreference("debugModeCheckBox")!!
					sharedPreferences.edit {
						putBoolean("debugMode", debugModeCheckBox.isChecked)
						commit()
					}
				}
				"enableHomeTestCheckBox" -> {
					val enableHomeTestCheckBox: CheckBoxPreference = findPreference("enableHomeTestCheckBox")!!
					sharedPreferences.edit {
						putBoolean("enableHomeTest", enableHomeTestCheckBox.isChecked)
						commit()
					}
				}
			}
		}
	}
	
	override fun onResume() {
		super.onResume()
		
		// Makes sure that OnSharedPreferenceChangeListener works properly and avoids the situation where the app suddenly stops triggering the listener.
		preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
	}
	
	override fun onPause() {
		super.onPause()
		preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		
		if (requestCode == 1001 && resultCode == RESULT_OK) {
			// The data contains the URI to the combat script file that the user selected.
			if (data != null) {
				val uri: Uri? = data.data
				
				if (uri != null) {
					// Open up a InputStream to the combat script.
					val inputStream = context?.contentResolver?.openInputStream(uri)
					
					// Start reading line by line and adding it to the ArrayList. It also makes sure to trim whitespaces and indents and ignores
					// lines that are comments.
					val commandList: ArrayList<String> = arrayListOf()
					inputStream?.bufferedReader()?.forEachLine {
						if (it.isNotEmpty() && it[0] != '/' && it[0] != '#') {
							commandList.add(it.trim(' ').trimIndent())
						}
					}
					
					// Now concatenate the commands together separated by a delimiter in order to keep the order when putting them into
					// SharedPreferences.
					val newCommandList = commandList.joinToString("|")
					
					Log.d(loggerTag, "Combat Script: $newCommandList")
					
					// Now save the ArrayList of combat script commands into SharedPreferences.
					sharedPreferences.edit {
						putString("combatScript", newCommandList)
						commit()
					}
					
					// Grab the file name from the URI and then update combat script category title.
					val path = data.data?.path
					val indexOfName = path?.lastIndexOf('/')
					if (indexOfName != null && indexOfName != -1) {
						val name = path.substring(indexOfName + 1)
						val filePicker: Preference = findPreference("filePicker")!!
						filePicker.summary = "Select the combat script in .txt format that will be used for Combat Mode.\n" +
								"\nCombat Script Selected: $name"
						
						Log.d(loggerTag, "Combat Script loaded: $uri")
					}
				}
			}
		} else {
			// Clear the saved combat script.
			Log.d(loggerTag, "User canceled the file picker intent. Clearing saved combat script information now...")
			
			val filePicker: Preference = findPreference("filePicker")!!
			filePicker.summary = "Select the combat script in .txt format that will be used for Combat Mode.\n" +
					"\n\nIf none is selected, it will default to Full/Semi Auto.\n" +
					"\n\nCombat Script Selected: none"
			
			sharedPreferences.edit {
				remove("combatScript")
				remove("combatScriptName")
			}
		}
	}
	
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		// Display the layout using the preferences xml.
		setPreferencesFromResource(R.xml.preferences, rootKey)
		
		// Open the File Manager so the user can select their combat script.
		val filePicker: Preference? = findPreference("filePicker")
		filePicker?.setOnPreferenceClickListener {
			val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
			intent.addCategory(Intent.CATEGORY_OPENABLE)
			intent.type = "text/plain"
			startActivityForResult(intent, 1001)
			
			true
		}
		
		// Get the SharedPreferences.
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
		
		// Grab the saved preferences from the previous time the user used the app.
		val farmingMode: String = sharedPreferences.getString("farmingMode", "")!!
		val missionName: String = sharedPreferences.getString("missionName", "")!!
		val itemName: String = sharedPreferences.getString("itemName", "")!!
		val itemAmount: Int = sharedPreferences.getInt("itemAmount", 1)
		val summon: List<String> = sharedPreferences.getString("summon", "")!!.split("|")
		val combatScriptName: String = sharedPreferences.getString("combatScriptName", "")!!
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
		
		// Get references to the Preference components.
		val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
		val missionPicker: ListPreference = findPreference("missionPicker")!!
		val itemPicker: ListPreference = findPreference("itemPicker")!!
		val itemAmountPicker: SeekBarPreference = findPreference("itemAmountPicker")!!
		val groupPicker: ListPreference = findPreference("groupPicker")!!
		val partyPicker: ListPreference = findPreference("partyPicker")!!
		val enableAutoExitCombatPreference: CheckBoxPreference = findPreference("enableAutoExitCombat")!!
		val autoExitCombatMinutesPreference: SeekBarPreference = findPreference("autoExitCombatMinutes")!!
		val delayBetweenRunsSwitch: SwitchPreference = findPreference("delayBetweenRunsSwitch")!!
		val delayBetweenRunsSeekBar: SeekBarPreference = findPreference("delayBetweenRunsSeekBar")!!
		val randomizedDelayBetweenRunsSwitch: SwitchPreference = findPreference("randomizedDelayBetweenRunsSwitch")!!
		val randomizedDelayBetweenRunsSeekBar: SeekBarPreference = findPreference("randomizedDelayBetweenRunsSeekBar")!!
		val confidenceSeekBar: SeekBarPreference = findPreference("confidenceSeekBar")!!
		val confidenceAllSeekBar: SeekBarPreference = findPreference("confidenceAllSeekBar")!!
		val customScaleEditText: EditTextPreference = findPreference("customScale")!!
		val enableDiscordCheckBox: CheckBoxPreference = findPreference("enableDiscord")!!
		val enableSkipAutoRestoreCheckBox: CheckBoxPreference = findPreference("enableSkipAutoRestore")!!
		val debugModeCheckBox: CheckBoxPreference = findPreference("debugModeCheckBox")!!
		val enableHomeTestCheckBox: CheckBoxPreference = findPreference("enableHomeTestCheckBox")!!
		
		customScaleEditText.setOnBindEditTextListener(editTextListener)
		
		// Now set the following values from the shared preferences. Work downwards through the Preferences and make the next ones enabled to direct user's attention as they go through the settings
		// down the page.
		
		////////////////////
		// Farming Mode Settings
		////////////////////
		if (farmingMode.isNotEmpty()) {
			farmingModePicker.value = farmingMode
			
			// Build the AlertDialog for Summons or disable it if its Coop.
			if (farmingMode != "Coop" && farmingMode != "Arcarum") {
				createSummonDialog()
			} else {
				val summonPicker: Preference = findPreference("summonPicker")!!
				summonPicker.title = "Select Summon(s)"
				summonPicker.isEnabled = false
				summonPicker.summary = "Select the Summon(s) in order from highest to lowest priority for Combat Mode."
			}
			
			// Populate and enable the Mission picker as the next step for the user.
			populateMissionListPreference()
			missionPicker.isEnabled = true
		}
		
		if (missionName.isNotEmpty()) {
			// Populate the Mission picker.
			populateMissionListPreference()
			missionPicker.value = missionName
			missionPicker.isEnabled = true
			
			// Populate and enable the Item picker as the next step for the user.
			populateItemListPreference()
			itemPicker.isEnabled = true
		}
		
		if (itemName.isNotEmpty()) {
			// Populate the Item picker.
			populateItemListPreference()
			itemPicker.value = itemName
			itemPicker.isEnabled = true
			
			// Set the value for the Item Amount picker and enable it.
			itemAmountPicker.value = itemAmount
			itemAmountPicker.isEnabled = true
			
			// Now reveal the Combat Mode PreferenceCategory that houses the Combat Script, Summons, and Group/Party Preference pickers.
			val combatModePreferenceCategory: PreferenceCategory = findPreference("combatModeTitle")!!
			combatModePreferenceCategory.isEnabled = true
		}
		
		////////////////////
		// Combat Mode Settings
		////////////////////
		if (combatScriptName != "") {
			filePicker?.summary = "Select the combat script in .txt format that will be used for Combat Mode.\n\nCombat Script Selected: $combatScriptName"
		}
		
		if (summon.isNotEmpty() && summon[0] != "") {
			groupPicker.value = "Group $groupNumber"
			partyPicker.value = "Party $partyNumber"
		}
		
		enableAutoExitCombatPreference.isChecked = enableAutoExitCombat
		autoExitCombatMinutesPreference.isVisible = enableAutoExitCombat
		autoExitCombatMinutesPreference.value = autoExitCombatMinutes
		
		////////////////////
		// Delay Settings
		////////////////////
		delayBetweenRunsSwitch.isChecked = enableDelayBetweenRuns
		delayBetweenRunsSeekBar.isVisible = (delayBetweenRunsSwitch.isChecked || randomizedDelayBetweenRunsSwitch.isChecked)
		delayBetweenRunsSeekBar.value = delayBetweenRuns
		
		randomizedDelayBetweenRunsSwitch.isChecked = enableRandomizedDelayBetweenRuns
		randomizedDelayBetweenRunsSeekBar.isVisible = randomizedDelayBetweenRunsSwitch.isChecked
		randomizedDelayBetweenRunsSeekBar.value = randomizedDelayBetweenRuns
		randomizedDelayBetweenRunsSeekBar.min = delayBetweenRunsSeekBar.value
		delayBetweenRunsSeekBar.title = if (randomizedDelayBetweenRunsSeekBar.isVisible) {
			"Set Lower Bound for Delay in Seconds"
		} else {
			"Set Delay In Seconds"
		}
		
		////////////////////
		// Misc Settings
		////////////////////
		confidenceSeekBar.value = confidence
		confidenceAllSeekBar.value = confidenceAll
		if (customScale == 1.0) {
			customScaleEditText.summary =
				"Set the scale at which to resize existing image assets to match what would be shown on your device. Internally supported are 720p, 1080p, 1600p (Portrait) and 2560p (Landscape) in width.\n\nScale: 1.0 (Default)"
		} else {
			customScaleEditText.summary =
				"Set the scale at which to resize existing image assets to match what would be shown on your device. Internally supported are 720p, 1080p, 1600p (Portrait) and 2560p (Landscape) in width.\n\nScale: $customScale"
		}
		customScaleEditText.text = customScale.toString()
		enableDiscordCheckBox.isChecked = enableDiscord
		enableSkipAutoRestoreCheckBox.isChecked = enableSkipAutoRestore
		debugModeCheckBox.isChecked = debugMode
		enableHomeTestCheckBox.isChecked = enableHomeTest
		
		// Save the Twitter API keys and tokens and every other settings in the config.yaml to SharedPreferences.
		try {
			val file = File(context?.getExternalFilesDir(null), "config.yaml")
			if (file.exists()) {
				val config = ConfigLoader().loadConfigOrThrow<ConfigData>(file)
				
				if (config.twitter.apiKey != sharedPreferences.getString("apiKey", "")) {
					sharedPreferences.edit {
						putString("apiKey", config.twitter.apiKey)
						putString("apiKeySecret", config.twitter.apiKeySecret)
						putString("accessToken", config.twitter.accessToken)
						putString("accessTokenSecret", config.twitter.accessTokenSecret)
						commit()
					}
					
					Log.d(TAG, "Saved Twitter API credentials to SharedPreferences from config.")
				}
				
				sharedPreferences.edit {
					putBoolean("enableEventNightmare", config.event.enableEventNightmare)
					putStringSet("eventNightmareSummonList", config.event.eventNightmareSummonList.toMutableSet())
					putInt("eventNightmareGroupNumber", config.event.eventNightmareGroupNumber)
					putInt("eventNightmarePartyNumber", config.event.eventNightmarePartyNumber)
					
					putBoolean("enableDimensionalHalo", config.dimensionalHalo.enableDimensionalHalo)
					putStringSet("dimensionalHaloSummonList", config.dimensionalHalo.dimensionalHaloSummonList.toMutableSet())
					putInt("dimensionalHaloGroupNumber", config.dimensionalHalo.dimensionalHaloGroupNumber)
					putInt("dimensionalHaloPartyNumber", config.dimensionalHalo.dimensionalHaloPartyNumber)
					
					putBoolean("enableROTBExtremePlus", config.rotb.enableROTBExtremePlus)
					putStringSet("rotbExtremePlusSummonList", config.rotb.rotbExtremePlusSummonList.toMutableSet())
					putInt("rotbExtremePlusGroupNumber", config.rotb.rotbExtremePlusGroupNumber)
					putInt("rotbExtremePlusPartyNumber", config.rotb.rotbExtremePlusPartyNumber)
					
					putBoolean("enableXenoClashNightmare", config.rotb.enableROTBExtremePlus)
					putStringSet("xenoClashNightmareSummonList", config.rotb.rotbExtremePlusSummonList.toMutableSet())
					putInt("xenoClashNightmareGroupNumber", config.rotb.rotbExtremePlusGroupNumber)
					putInt("xenoClashNightmarePartyNumber", config.rotb.rotbExtremePlusPartyNumber)
				}
				
				Log.d(TAG, "Saved config.ini settings to SharedPreferences.")
			}
		} catch (e: Exception) {
			Log.e(TAG, "Encountered error while saving Twitter API credentials to SharedPreferences from config: ${e.stackTraceToString()}")
			Log.e(TAG, "Clearing any existing Twitter API credentials from SharedPreferences...")
			
			sharedPreferences.edit {
				remove("apiKey")
				remove("apiKeySecret")
				remove("accessToken")
				remove("accessTokenSecret")
				commit()
			}
		}
		
		Log.d(loggerTag, "Preferences created successfully.")
	}
	
	/**
	 * Populate the entries in the Mission Selection ListPreference based on the selected Farming Mode.
	 */
	private fun populateMissionListPreference() {
		val newEntries = mutableListOf<CharSequence>()
		
		val farmingMode = sharedPreferences.getString("farmingMode", "")
		val missionPicker: ListPreference = findPreference("missionPicker")!!
		
		// Get the item entries based on the selected Farming Mode.
		MissionData.missions[farmingMode]?.forEach { (_, missionArrayList) ->
			missionArrayList.forEach {
				newEntries.add(it)
			}
		}
		
		missionPicker.entries = newEntries.toTypedArray()
		missionPicker.entryValues = newEntries.toTypedArray()
	}
	
	/**
	 * Populate the entries in the Item Selection ListPreference based on the selected Mission.
	 */
	private fun populateItemListPreference() {
		val newEntries = mutableListOf<CharSequence>()
		
		val farmingMode = sharedPreferences.getString("farmingMode", "")!!
		val missionName = sharedPreferences.getString("missionName", "")!!
		val itemPicker: ListPreference = findPreference("itemPicker")!!
		
		// Remove any detected difficulty prefix.
		val formattedMissionName = if (farmingMode == "Special") {
			if (missionName.startsWith("N ", true) || missionName.startsWith("H ", true) ||
				missionName.startsWith("VH ", true) || missionName.startsWith("EX ", true)
			) {
				val missionNameList = missionName.split(" ")
				missionNameList.subList(1, missionNameList.size).joinToString(" ")
			} else {
				missionName
			}
		} else {
			missionName
		}
		
		// Get the item entries based on the selected Farming Mode.
		ItemData.items[farmingMode]?.get(formattedMissionName)?.forEach { item ->
			// Save the Map that the Mission takes place on.
			if (farmingMode == "Quest" || farmingMode == "Special") {
				MissionData.missions[farmingMode]?.forEach { (map, missions) ->
					missions.forEach { mission ->
						if (mission.contains(formattedMissionName)) {
							sharedPreferences.edit {
								putString("mapName", map)
								apply()
							}
						}
					}
				}
			}
			
			newEntries.add(item)
		}
		
		itemPicker.entries = newEntries.toTypedArray()
		itemPicker.entryValues = newEntries.toTypedArray()
	}
	
	/**
	 * Builds and displays the AlertDialog for selecting summons for Combat Mode.
	 */
	private fun createSummonDialog() {
		val summonPicker: Preference = findPreference("summonPicker")!!
		val summonPreferences = sharedPreferences.getString("summon", "")!!.split("|")
		
		// Update the Preference's summary to reflect the order of summons selected if the user did it before.
		if (summonPreferences.toList().isEmpty() || summonPreferences.toList()[0] == "") {
			summonPicker.summary = "Select the Summon(s) in order from highest to lowest priority for Combat Mode."
		} else {
			summonPicker.summary = "${summonPreferences.toList()}"
		}
		
		summonPicker.setOnPreferenceClickListener {
			// Create the AlertDialog that pops up after clicking on this Preference.
			builder = AlertDialog.Builder(context)
			builder.setTitle("Select Summon(s)")
			
			// Grab the array of supported summons.
			summonListItems = resources.getStringArray(R.array.summon_list)
			
			// Populate the list for supported summons if this is the first time.
			if (summonPreferences.isEmpty()) {
				summonListCheckedItems = BooleanArray(summonListItems.size)
				var index = 0
				summonListItems.forEach { _ ->
					summonListCheckedItems[index] = false
					index++
				}
			} else {
				summonListCheckedItems = BooleanArray(summonListItems.size)
				var index = 0
				summonListItems.forEach {
					// Populate the checked items BooleanArray with true or false depending on what the user selected before.
					summonListCheckedItems[index] = summonPreferences.contains(it)
					index++
				}
			}
			
			// Set the selectable items for this AlertDialog.
			builder.setMultiChoiceItems(summonListItems, summonListCheckedItems) { _, position, isChecked ->
				if (isChecked) {
					userSelectedSummonList.add(position)
				} else {
					userSelectedSummonList.remove(position)
				}
			}
			
			// Set the AlertDialog's PositiveButton.
			builder.setPositiveButton("OK") { _, _ ->
				// Grab the summons using the acquired indexes. This will put them in order from the user's highest to lowest priority.
				val values: ArrayList<String> = arrayListOf()
				userSelectedSummonList.forEach {
					values.add(summonListItems[it])
				}
				
				// Join the elements together into a String with the "|" delimiter in order to keep its order when storing into SharedPreferences.
				val newValues = values.joinToString("|")
				
				// Note: putStringSet does not support ordering or duplicate values. If you need ordering/duplicate values, either concatenate the values together as a String separated by a
				// delimiter or think of another way.
				sharedPreferences.edit {
					putString("summon", newValues)
					apply()
				}
				
				// Recreate the AlertDialog again to update it with the newly selected items.
				createSummonDialog()
				
				if (values.toList().isEmpty()) {
					summonPicker.summary = "Select the Summon(s) in order from highest to lowest priority for Combat Mode."
				} else {
					// Display the list of ordered summons and display the Group/Party Preference pickers as well.
					summonPicker.summary = "${values.toList()}"
				}
			}
			
			// Set the AlertDialog's NegativeButton.
			builder.setNegativeButton("Dismiss") { dialog, _ -> dialog?.dismiss() }
			
			// Set the AlertDialog's NeutralButton.
			builder.setNeutralButton("Clear all") { _, _ ->
				// Go through every checked item and set them to false.
				for (i in summonListCheckedItems.indices) {
					summonListCheckedItems[i] = false
				}
				
				// After that, clear the list of user-selected summons and the one in SharedPreferences.
				userSelectedSummonList.clear()
				sharedPreferences.edit {
					remove("summon")
					apply()
				}
				
				// Recreate the AlertDialog again to update it with the newly selected items and reset its summary.
				createSummonDialog()
				summonPicker.summary = "Select the Summon(s) in order from highest to lowest priority for Combat Mode."
			}
			
			// Finally, show the AlertDialog to the user.
			builder.create().show()
			
			true
		}
	}
	
	/**
	 * Reset the mission stored in the SharedPreferences.
	 */
	private fun resetMissionSharedPreference() {
		val missionPicker: ListPreference = findPreference("missionPicker")!!
		missionPicker.value = null
		sharedPreferences.edit {
			remove("mapName")
			remove("missionName")
			commit()
		}
	}
	
	/**
	 * Reset the item stored in the SharedPreferences.
	 */
	private fun resetItemSharedPreference() {
		val itemPicker: ListPreference = findPreference("itemPicker")!!
		itemPicker.value = null
		sharedPreferences.edit {
			remove("itemName")
			commit()
		}
	}
}