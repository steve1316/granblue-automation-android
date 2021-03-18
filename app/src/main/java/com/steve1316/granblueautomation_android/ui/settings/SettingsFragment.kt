package com.steve1316.granblueautomation_android.ui.settings

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.steve1316.granblueautomation_android.R

class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG: String = "GAA_SettingsFragment"
    private val OPEN_FILE_PERMISSION = 1001
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    
    private lateinit var preferenceCategoryCombatScript: PreferenceCategory
    
    @SuppressLint("CommitPrefEdits", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get the SharedPreferences and its Editor object.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferencesEditor = sharedPreferences.edit()
    
        preferenceCategoryCombatScript = findPreference("combatScriptTitle")!!
        
        // Grab the combat script preference and update the title of its category.
        val combatScript = sharedPreferences.getString("combatScript", "")
        if(combatScript != null && combatScript.isNotEmpty()) {
            val preferenceCategory = findPreference<PreferenceCategory>("combatScriptTitle")
            preferenceCategory?.title = "Combat Script: Selected $combatScript"
        }
        
        // Grab the farming mode preference.
        val farmingMode = sharedPreferences.getString("farmingMode", "")
        if(farmingMode != null && farmingMode.isNotEmpty()) {
            val farmingModePreference = findPreference<Preference>("farmingMode")
            farmingModePreference?.setDefaultValue(farmingMode)
        }
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    
        val filePicker: Preference? = findPreference("filePicker")
    
        // Open the File Manager so the user can select their combat script.
        filePicker?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "text/plain"
            startActivityForResult(intent, OPEN_FILE_PERMISSION)
            
            true
        }
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
                    
                    // Start reading line by line and adding it to the ArrayList. It also makes sure to trim whitespaces and indents.
                    val list: ArrayList<String> = arrayListOf()
                    inputStream?.bufferedReader()?.forEachLine {
                        if(it.isNotEmpty() && it[0] != '/' && it[0] != '#') {
                            list.add(it.trim(' ').trimIndent())
                        }
                    }
                    
                    // Grab the file name from the URI and then update combat script category title.
                    val path = data.data?.path
                    val indexOfName = path?.lastIndexOf('/')
                    if(indexOfName != null && indexOfName != -1) {
                        val name = path.substring(indexOfName + 1)
                        preferenceCategoryCombatScript.title = "Combat Script: Selected $name"
    
                        // Now save the file name in the shared preferences.
                        sharedPreferencesEditor.putString("combatScript", name)
                        sharedPreferencesEditor.apply()
                        
                        Log.d(TAG, "Combat Script loaded: $uri")
                    }
                }
            }
        }
    }
    
    // TODO: Implement options for the user to choose item to farm, amount of it, mission, Summons, combat script, etc.
}