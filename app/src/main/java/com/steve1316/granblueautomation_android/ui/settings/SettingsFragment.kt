package com.steve1316.granblueautomation_android.ui.settings

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import androidx.preference.*
import com.sksamuel.hoplite.ConfigLoader
import com.steve1316.granblueautomation_android.R
import com.steve1316.granblueautomation_android.data.ConfigData
import com.steve1316.granblueautomation_android.data.ItemData
import com.steve1316.granblueautomation_android.data.MissionData
import java.io.*
import kotlin.collections.ArrayList

class SettingsFragment : PreferenceFragmentCompat() {
	private val TAG: String = "GAA_SettingsFragment"
	private val OPEN_FILE_PERMISSION = 1001
	
	private lateinit var sharedPreferences: SharedPreferences
	
	private lateinit var builder: AlertDialog.Builder
	private lateinit var summonListItems: Array<String>
	private lateinit var summonListCheckedItems: BooleanArray
	private var userSelectedSummonList: ArrayList<Int> = arrayListOf()
	
	private val missionsForQuest: Map<String, ArrayList<String>> = MissionData.mapsForQuest
	private val missionsForSpecial: Map<String, ArrayList<String>> = MissionData.mapsForSpecial
	private val missionsForRaid: Map<String, ArrayList<String>> = MissionData.mapsForRaid
	private val missionsForEvent: Map<String, ArrayList<String>> = MissionData.mapsForEvent
	private val missionsForEventTokenDrawboxes: Map<String, ArrayList<String>> = MissionData.mapsForEventTokenDrawboxes
	private val missionsForGuildWars: Map<String, ArrayList<String>> = MissionData.mapsForGuildWars
	
	private val itemsForQuest: Map<String, ArrayList<String>> = ItemData.itemsForQuest
	private val itemsForSpecial: Map<String, ArrayList<String>> = ItemData.itemsForSpecial
	private val itemsForCoop: Map<String, ArrayList<String>> = ItemData.itemsForCoop
	private val itemsForRaid: Map<String, ArrayList<String>> = ItemData.itemsForRaid
	private val itemsForEvent: Map<String, ArrayList<String>> = ItemData.itemsForEvent
	private val itemsForEventTokenDrawboxes: Map<String, ArrayList<String>> = ItemData.itemsForEventTokenDrawboxes
	private val itemsForGuildWars: Map<String, ArrayList<String>> = ItemData.itemsForGuildWars
	
	companion object {
		/**
		 * Get a String value from the SharedPreferences using the provided key.
		 *
		 * @param context The context for the application.
		 * @param key The name of the preference to retrieve.
		 * @return The value that is associated with the key.
		 */
		fun getStringSharedPreference(context: Context, key: String): String {
			val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
			return sharedPreferences.getString(key, "")!!
		}
		
		/**
		 * Get a Int value from the SharedPreferences using the provided key.
		 *
		 * @param context The context for the application.
		 * @param key The name of the preference to retrieve.
		 * @return The value that is associated with the key.
		 */
		fun getIntSharedPreference(context: Context, key: String): Int {
			val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
			return sharedPreferences.getInt(key, 1)
		}
		
		/**
		 * Get a Boolean value from the SharedPreferences using the provided key.
		 *
		 * @param context The context for the application.
		 * @param key The name of the preference to retrieve.
		 * @return The value that is associated with the key.
		 */
		fun getBooleanSharedPreference(context: Context, key: String): Boolean {
			val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
			return sharedPreferences.getBoolean(key, false)
		}
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
                    
                    if (farmingModePicker.value == "Coop") {
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
                    
                    // Build the Summon Selection AlertDialog.
                    val farmingMode = sharedPreferences.getString("farmingMode", "")
                    if (farmingMode != "Coop") {
                        val summonPicker: Preference = findPreference("summonPicker")!!
                        summonPicker.isEnabled = true
                        createSummonDialog()
                    }
                    
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
			}
		}
	}
	
	override fun onResume() {
		super.onResume()
		
		// Makes sure that OnSharedPreferenceChangeListener works properly and avoids the situation where the app suddenly stops triggering the
		// listener.
		preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
	}
	
	override fun onPause() {
		super.onPause()
		preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener)
	}
	
	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		
		if (requestCode == OPEN_FILE_PERMISSION && resultCode == RESULT_OK) {
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
								"\nIf none is selected, it will default to Full/Semi Auto.\n" +
								"\nnCombat Script Selected: $name"
						
						// Now save the file name in the shared preferences.
						sharedPreferences.edit {
							putString("combatScriptName", name)
							apply()
						}
						
						Log.d(TAG, "Combat Script loaded: $uri")
					}
				}
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
			startActivityForResult(intent, OPEN_FILE_PERMISSION)
			
			true
		}
		
		// Get the SharedPreferences.
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
		
		// Grab the preferences from the previous time the user used the app.
		val farmingModePreferences = sharedPreferences.getString("farmingMode", "")
		val missionPreferences = sharedPreferences.getString("missionName", "")
		val itemPreferences = sharedPreferences.getString("itemName", "")
		val itemAmountPreferences = sharedPreferences.getInt("itemAmount", 1)
		val summonPreferences = sharedPreferences.getString("summon", "")?.split("|")
		val groupPreferences = sharedPreferences.getInt("groupNumber", 1)
		val partyPreferences = sharedPreferences.getInt("partyNumber", 1)
		
		// Get references to the Preferences.
		val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
		val missionPicker: ListPreference = findPreference("missionPicker")!!
		val itemPicker: ListPreference = findPreference("itemPicker")!!
		val itemAmountPicker: SeekBarPreference = findPreference("itemAmountPicker")!!
		val groupPicker: ListPreference = findPreference("groupPicker")!!
		val partyPicker: ListPreference = findPreference("partyPicker")!!
		
		// Now set the following values from the shared preferences. Work downwards through the Preferences and make the next ones enabled to
		// direct user's attention as they go through the settings down the page.
		if (farmingModePreferences != null && farmingModePreferences.isNotEmpty()) {
			farmingModePicker.value = farmingModePreferences
			
			// Build the AlertDialog for Summons or disable it if its Coop.
			if (farmingModePreferences != "Coop") {
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
		
		if (missionPreferences != null && missionPreferences.isNotEmpty()) {
			// Populate the Mission picker.
			populateMissionListPreference()
			missionPicker.value = missionPreferences
			missionPicker.isEnabled = true
			
			// Populate and enable the Item picker as the next step for the user.
			populateItemListPreference()
			itemPicker.isEnabled = true
		}
		
		if (itemPreferences != null && itemPreferences.isNotEmpty()) {
			// Populate the Item picker.
			populateItemListPreference()
			itemPicker.value = itemPreferences
			itemPicker.isEnabled = true
			
			// Set the value for the Item Amount picker and enable it.
			itemAmountPicker.value = itemAmountPreferences
			itemAmountPicker.isEnabled = true
			
			// Now reveal the Combat Mode PreferenceCategory that houses the Combat Script, Summons, and Group/Party Preference pickers.
			val combatModePreferenceCategory: PreferenceCategory = findPreference("combatModeTitle")!!
			combatModePreferenceCategory.isEnabled = true
		}
		
		if (summonPreferences != null && summonPreferences.isNotEmpty() && summonPreferences[0] != "") {
			groupPicker.value = "Group $groupPreferences"
			partyPicker.value = "Party $partyPreferences"
		}
		
		// Save the Twitter API keys and tokens to SharedPreferences.
		try {
			val file = File(context?.getExternalFilesDir(null), "config.yaml")
			if (file.exists() && !sharedPreferences.contains("apiKey")) {
				val config = ConfigLoader().loadConfigOrThrow<ConfigData>(file)
				sharedPreferences.edit {
					putString("apiKey", config.twitter.apiKey)
					putString("apiKeySecret", config.twitter.apiKeySecret)
					putString("accessToken", config.twitter.accessToken)
					putString("accessTokenSecret", config.twitter.accessTokenSecret)
					commit()
				}
				
				Log.d(TAG, "Saved Twitter API credentials to SharedPreferences from config.")
			}
		} catch (e: Exception) {
			Log.e(TAG, "Encountered error while saving Twitter API credentials to SharedPreferences from config: $e")
			Log.e(TAG, "Clearing any existing Twitter API credentials from SharedPreferences...")
			
			sharedPreferences.edit {
				remove("apiKey")
				remove("apiKeySecret")
				remove("accessToken")
				remove("accessTokenSecret")
				commit()
			}
		}
		
		Log.d(TAG, "Preferences created")
	}
	
	/**
	 * Populate the entries in the Mission Selection ListPreference based on the selected Farming Mode.
	 */
	private fun populateMissionListPreference() {
		val newEntries = mutableListOf<CharSequence>()
		
		val farmingMode = sharedPreferences.getString("farmingMode", "")
		val missionPicker: ListPreference = findPreference("missionPicker")!!
		
		// Get the item entries based on the selected Farming Mode.
		when (farmingMode) {
            "Quest" -> {
                missionsForQuest.forEach { (_, value) ->
                    value.forEach {
                        newEntries.add(it)
                    }
                }
            }
            "Special" -> {
                missionsForSpecial.forEach { (_, value) ->
                    value.forEach {
                        newEntries.add(it)
                    }
                }
            }
            "Coop" -> {
                itemsForCoop.forEach { (key, _) ->
                    newEntries.add(key)
                }
            }
            "Raid" -> {
                missionsForRaid.forEach { (_, value) ->
                    value.forEach {
                        newEntries.add(it)
                    }
                }
            }
            "Event" -> {
                missionsForEvent.forEach { (_, value) ->
                    value.forEach {
                        newEntries.add(it)
                    }
                }
            }
            "Event (Token Drawboxes)" -> {
                missionsForEventTokenDrawboxes.forEach { (_, value) ->
                    value.forEach {
                        newEntries.add(it)
                    }
                }
            }
            "Guild Wars" -> {
                missionsForGuildWars.forEach { (_, value) ->
                    value.forEach {
                        newEntries.add(it)
                    }
                }
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
		
		val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
		val missionName = sharedPreferences.getString("missionName", "")!!
		val itemPicker: ListPreference = findPreference("itemPicker")!!
		
		// Get the item entries based on the selected Mission.
		when (farmingModePicker.value) {
            "Quest" -> {
                missionsForQuest.forEach { (missionKey, missionValue) ->
                    // Save the Map that the Mission takes place on.
                    if (missionValue.contains(missionName)) {
                        sharedPreferences.edit().apply {
                            putString("mapName", missionKey)
                            apply()
                        }
                    }
                }
                
                itemsForQuest.forEach { (key, value) ->
                    if (key == missionName) {
                        value.forEach {
                            newEntries.add(it)
                        }
                    }
                }
            }
            "Special" -> {
                missionsForSpecial.forEach { (missionKey, missionValue) ->
                    // Save the Map that the Mission takes place on.
                    if (missionValue.contains(missionName)) {
                        sharedPreferences.edit().apply {
                            putString("mapName", missionKey)
                            apply()
                        }
                    }
                }
                
                // Remove any detected difficulty prefix.
                val formattedMissionName = if (missionName.startsWith("N ", true) || missionName.startsWith("H ", true) ||
                    missionName.startsWith("VH ", true) || missionName.startsWith("EX ", true)
                ) {
                    val missionNameList = missionName.split(" ")
                    missionNameList.subList(1, missionNameList.size).joinToString(" ")
                } else {
                    missionName
                }
                
                itemsForSpecial.forEach { (key, value) ->
                    if (key == formattedMissionName) {
                        value.forEach {
                            newEntries.add(it)
                        }
                    }
                }
            }
            "Coop" -> {
                itemsForCoop.forEach { (key, value) ->
                    if (key == missionName) {
                        value.forEach {
                            newEntries.add(it)
                        }
                    }
                }
            }
            "Raid" -> {
                itemsForRaid.forEach { (key, value) ->
                    if (key == missionName) {
                        value.forEach {
                            newEntries.add(it)
                        }
                    }
                }
            }
            "Event" -> {
                itemsForEvent.forEach { (key, value) ->
                    if (key == missionName) {
                        value.forEach {
                            newEntries.add(it)
                        }
                    }
                }
            }
            "Event (Token Drawboxes)" -> {
                itemsForEventTokenDrawboxes.forEach { (key, value) ->
                    if (key == missionName) {
                        value.forEach {
                            newEntries.add(it)
                        }
                    }
                }
            }
            "Guild Wars" -> {
                itemsForGuildWars.forEach { (key, value) ->
                    if (key == missionName) {
                        value.forEach {
                            newEntries.add(it)
                        }
                    }
                }
            }
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
				
				// Note: putStringSet does not support ordering or duplicate values. If you need ordering/duplicate values, either
				// concatenate the values together as a String separated by a delimiter or think of another way.
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
					
					sharedPreferences.edit {
						putInt("groupNumber", 1)
						putInt("partyNumber", 1)
						apply()
					}
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