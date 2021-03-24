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
import com.steve1316.granblueautomation_android.R
import com.steve1316.granblueautomation_android.utils.ItemData

class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG: String = "GAA_SettingsFragment"
    private val OPEN_FILE_PERMISSION = 1001
    
    private lateinit var sharedPreferences: SharedPreferences
    
    private lateinit var builder: AlertDialog.Builder
    private lateinit var summonListItems: Array<String>
    private lateinit var summonListCheckedItems: BooleanArray
    private var userSelectedSummonList: ArrayList<Int> = arrayListOf()
    
    private val itemsForQuest: Map<String, ArrayList<String>> = ItemData.itemsForQuest
    private val itemsForSpecial: Map<String, ArrayList<String>> = ItemData.itemsForSpecial
    private val itemsForCoop: Map<String, ArrayList<String>> = ItemData.itemsForCoop
    private val itemsForRaid: Map<String, ArrayList<String>> = ItemData.itemsForRaid
    
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
    }
    
    // This listener is triggered whenever the user changes a Preference setting in the Settings Page.
    private val onSharedPreferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if(key != null) {
            val newEntries = mutableListOf<CharSequence>()
            val newEntryValues = mutableListOf<CharSequence>()
            
            if(key == "farmingModePicker") {
                // Fill out the new entries and values for Missions based on the newly chosen Farming Mode.
                val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
                if(farmingModePicker.value == "Quest") {
                    itemsForQuest.forEach { (key, _) ->
                        newEntries.add(key)
                        newEntryValues.add(key)
                    }
                } else if(farmingModePicker.value == "Special") {
                    itemsForSpecial.forEach { (key, _) ->
                        newEntries.add(key)
                        newEntryValues.add(key)
                    }
                } else if(farmingModePicker.value == "Coop") {
                    itemsForCoop.forEach { (key, _) ->
                        newEntries.add(key)
                        newEntryValues.add(key)
                    }
                } else if(farmingModePicker.value == "Raid") {
                    itemsForRaid.forEach { (key, _) ->
                        newEntries.add(key)
                        newEntryValues.add(key)
                    }
                }
                
                sharedPreferences.edit {
                    putString("farmingMode", farmingModePicker.value)
                    commit()
                }
                
                // Populate the Mission picker with the missions associated with the newly chosen Farming Mode.
                val missionPicker: ListPreference = findPreference("missionPicker")!!
                missionPicker.entries = newEntries.toTypedArray()
                missionPicker.entryValues = newEntryValues.toTypedArray()
                
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
            } else if(key == "missionPicker") {
                val missionPicker: ListPreference = findPreference("missionPicker")!!
                val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
                
                // Fill out the new entries and values for Items based on the newly chosen Mission.
                if(farmingModePicker.value == "Quest") {
                    itemsForQuest.forEach { (key, value) ->
                        if(key == missionPicker.value) {
                            value.forEach {
                                newEntries.add(it)
                                newEntryValues.add(it)
                            }
                        }
                    }
                } else if(farmingModePicker.value == "Special") {
                    itemsForSpecial.forEach { (key, value) ->
                        if(key == missionPicker.value) {
                            value.forEach {
                                newEntries.add(it)
                                newEntryValues.add(it)
                            }
                        }
                    }
                } else if(farmingModePicker.value == "Coop") {
                    itemsForCoop.forEach { (key, value) ->
                        if(key == missionPicker.value) {
                            value.forEach {
                                newEntries.add(it)
                                newEntryValues.add(it)
                            }
                        }
                    }
                } else if(farmingModePicker.value == "Raid") {
                    itemsForRaid.forEach { (key, value) ->
                        if(key == missionPicker.value) {
                            value.forEach {
                                newEntries.add(it)
                                newEntryValues.add(it)
                            }
                        }
                    }
                }
                
                sharedPreferences.edit {
                    putString("mission", missionPicker.value)
                    commit()
                }
                
                // Populate the Item picker with the items associated with the newly chosen Mission.
                val itemPicker: ListPreference = findPreference("itemPicker")!!
                itemPicker.entries = newEntries.toTypedArray()
                itemPicker.entryValues = newEntryValues.toTypedArray()
                
                // Now reset the value of the Item picker.
                resetItemSharedPreference()
    
                // Disable the following Preferences.
                val combatModePreferenceCategory: PreferenceCategory = findPreference("combatModeTitle")!!
                combatModePreferenceCategory.isEnabled = false
                
                // Finally, enable both the Item picker and the Item Amount picker.
                itemPicker.isEnabled = true
                val itemAmountPicker: SeekBarPreference = findPreference("itemAmountPicker")!!
                itemAmountPicker.isEnabled = true
            } else if(key == "itemPicker") {
                val itemPicker: ListPreference = findPreference("itemPicker")!!
                sharedPreferences.edit {
                    putString("item", itemPicker.value)
                    commit()
                }
                
                // Build the Summon Selection AlertDialog.
                createSummonDialog()
                
                // Finally, enable the Combat Mode PreferenceCategory.
                val combatModePreferenceCategory: PreferenceCategory = findPreference("combatModeTitle")!!
                combatModePreferenceCategory.isEnabled = true
            } else if(key == "itemAmountPicker") {
                val itemAmountPicker: SeekBarPreference = findPreference("itemAmountPicker")!!
                sharedPreferences.edit {
                    putInt("itemAmount", itemAmountPicker.value)
                    commit()
                }
            } else if(key == "groupPicker") {
                val groupPicker: ListPreference = findPreference("groupPicker")!!
                sharedPreferences.edit {
                    putInt("groupNumber", groupPicker.value.last().toString().toInt())
                    commit()
                }
            } else if(key == "partyPicker") {
                val partyPicker: ListPreference = findPreference("partyPicker")!!
                sharedPreferences.edit {
                    putInt("partyNumber", partyPicker.value.last().toString().toInt())
                    commit()
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
        
        if(requestCode == OPEN_FILE_PERMISSION && resultCode == RESULT_OK) {
            // The data contains the URI to the combat script file that the user selected.
            if(data != null) {
                val uri: Uri? = data.data
                
                if(uri != null) {
                    // Open up a InputStream to the combat script.
                    val inputStream = context?.contentResolver?.openInputStream(uri)
                    
                    // Start reading line by line and adding it to the ArrayList. It also makes sure to trim whitespaces and indents and ignores
                    // lines that are comments.
                    val commandList: ArrayList<String> = arrayListOf()
                    inputStream?.bufferedReader()?.forEachLine {
                        if(it.isNotEmpty() && it[0] != '/' && it[0] != '#') {
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
                    if(indexOfName != null && indexOfName != -1) {
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
        val combatScriptNamePreferences = sharedPreferences.getString("combatScriptName", "")
        val farmingModePreferences = sharedPreferences.getString("farmingMode", "")
        val missionPreferences = sharedPreferences.getString("mission", "")
        val itemPreferences = sharedPreferences.getString("item", "")
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
        if(farmingModePreferences != null && farmingModePreferences.isNotEmpty()) {
            farmingModePicker.value = farmingModePreferences
            missionPicker.isEnabled = true
        }
        
        if(missionPreferences != null && missionPreferences.isNotEmpty()) {
            // Populate the Mission picker.
            populateMissionListPreference()
            missionPicker.value = missionPreferences
            missionPicker.isEnabled = true
            
            // Enable the Item picker as the next step for the user.
            itemPicker.isEnabled = true
        }
        
        if(itemPreferences != null && itemPreferences.isNotEmpty()) {
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
            createSummonDialog()
        }
        
        if(summonPreferences != null && summonPreferences.isNotEmpty() && summonPreferences[0] != "") {
            groupPicker.value = "Group $groupPreferences"
            partyPicker.value = "Party $partyPreferences"
        }
        
        Log.d(TAG, "Preferences created")
    }
    
    /**
     * Populate the entries in the Mission Selection ListPreference based on the selected Farming Mode.
     */
    private fun populateMissionListPreference() {
        val newEntries = mutableListOf<CharSequence>()
        val newEntryValues = mutableListOf<CharSequence>()
    
        val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
        val missionPicker: ListPreference = findPreference("missionPicker")!!
    
        // Get the item entries based on the selected Farming Mode.
        if(farmingModePicker.value == "Quest") {
            itemsForQuest.forEach { (key, _) ->
                newEntries.add(key)
                newEntryValues.add(key)
            }
        } else if(farmingModePicker.value == "Special") {
            itemsForSpecial.forEach { (key, _) ->
                newEntries.add(key)
                newEntryValues.add(key)
            }
        } else if(farmingModePicker.value == "Coop") {
            itemsForCoop.forEach { (key, _) ->
                newEntries.add(key)
                newEntryValues.add(key)
            }
        } else if(farmingModePicker.value == "Raid") {
            itemsForRaid.forEach { (key, _) ->
                newEntries.add(key)
                newEntryValues.add(key)
            }
        }
    
        missionPicker.entries = newEntries.toTypedArray()
        missionPicker.entryValues = newEntryValues.toTypedArray()
    }
    
    /**
     * Populate the entries in the Item Selection ListPreference based on the selected Mission.
     */
    private fun populateItemListPreference() {
        val newEntries = mutableListOf<CharSequence>()
        val newEntryValues = mutableListOf<CharSequence>()
    
        val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
        val missionPicker: ListPreference = findPreference("missionPicker")!!
        val itemPicker: ListPreference = findPreference("itemPicker")!!
    
        // Get the item entries based on the selected Mission.
        if(farmingModePicker.value == "Quest") {
            itemsForQuest.forEach { (key, value) ->
                if(key == missionPicker.value) {
                    value.forEach {
                        newEntries.add(it)
                        newEntryValues.add(it)
                    }
                }
            }
        } else if(farmingModePicker.value == "Special") {
            itemsForSpecial.forEach { (key, value) ->
                if(key == missionPicker.value) {
                    value.forEach {
                        newEntries.add(it)
                        newEntryValues.add(it)
                    }
                }
            }
        } else if(farmingModePicker.value == "Coop") {
            itemsForCoop.forEach { (key, value) ->
                if(key == missionPicker.value) {
                    value.forEach {
                        newEntries.add(it)
                        newEntryValues.add(it)
                    }
                }
            }
        } else if(farmingModePicker.value == "Raid") {
            itemsForRaid.forEach { (key, value) ->
                if(key == missionPicker.value) {
                    value.forEach {
                        newEntries.add(it)
                        newEntryValues.add(it)
                    }
                }
            }
        }
    
        itemPicker.entries = newEntries.toTypedArray()
        itemPicker.entryValues = newEntryValues.toTypedArray()
    }
    
    /**
     * Builds and displays the AlertDialog for selecting summons for Combat Mode.
     */
    private fun createSummonDialog() {
        val summonPicker: Preference = findPreference("summonPicker")!!
        val summonPreferences = sharedPreferences.getString("summon", "")!!.split("|")
        
        // Update the Preference's summary to reflect the order of summons selected if the user did it before.
        if(summonPreferences.toList().isEmpty() || summonPreferences.toList()[0] == "") {
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
            if(summonPreferences.isEmpty()) {
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
            remove("mission")
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
            remove("item")
            commit()
        }
    }
    
    // TODO: Implement options for the user to choose item to farm, amount of it, mission, Summons, combat script, etc.
}