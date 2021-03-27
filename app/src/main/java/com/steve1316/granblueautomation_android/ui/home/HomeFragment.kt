package com.steve1316.granblueautomation_android.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.steve1316.granblueautomation_android.MyAccessibilityService
import com.steve1316.granblueautomation_android.R
import com.steve1316.granblueautomation_android.ui.settings.SettingsFragment
import com.steve1316.granblueautomation_android.utils.MediaProjectionService

class HomeFragment : Fragment() {
    private val TAG: String = "GAA_HomeFragment"
    private val SCREENSHOT_PERMISSION_REQUEST_CODE: Int = 100
    private var firstBoot = false
    
    private lateinit var myContext: Context
    private lateinit var homeFragmentView: View
    private lateinit var startButton: Button
    
    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        myContext = requireContext()

        homeFragmentView = inflater.inflate(R.layout.fragment_home, container, false)
        
        // Start or stop the MediaProjection service via this button.
        startButton = homeFragmentView.findViewById(R.id.start_button)
        startButton.setOnClickListener {
            val readyCheck = startReadyCheck()
            if(readyCheck && !MediaProjectionService.isRunning) {
                startProjection()
                startButton.text = getString(R.string.bot_stop)
                
                // This is needed because onResume() is immediately called right after accepting the MediaProjection and it has not been properly
                // initialized yet so it would cause the button's text to revert back to "Start".
                firstBoot = true
            } else if(MediaProjectionService.isRunning){
                stopProjection()
                startButton.text = getString(R.string.bot_start)
            }
        }
        
        // Update the TextView here based on the information of the SharedPreferences. Required preferences to check for are Farming Mode, Mission,
        // Item, and Summons. The rest are already given default values if the user never set them. Except Combat Script as if it is not set by the
        // user, set it for them to be Full/Semi Auto by default.
        val settingsStatusTextView: TextView = homeFragmentView.findViewById(R.id.settings_status)
        
        var combatScriptName = SettingsFragment.getStringSharedPreference(myContext, "combatScriptName")
        var farmingMode = SettingsFragment.getStringSharedPreference(myContext, "farmingMode")
        var mission = SettingsFragment.getStringSharedPreference(myContext, "missionName")
        var missionName = SettingsFragment.getStringSharedPreference(myContext, "missionName")
        var itemName = SettingsFragment.getStringSharedPreference(myContext, "itemName")
        val itemAmount = SettingsFragment.getIntSharedPreference(myContext, "itemAmount")
        var summon = SettingsFragment.getStringSharedPreference(myContext, "summon").split("|")
        val groupNumber = SettingsFragment.getIntSharedPreference(myContext, "groupNumber")
        val partyNumber = SettingsFragment.getIntSharedPreference(myContext, "partyNumber")
    
        startButton.isEnabled = (farmingMode != "" && missionName != "" && itemName != "" && summon.isNotEmpty() && summon[0] != "")
        
        if(combatScriptName == "") {
            combatScriptName = ""
        }
        
        if(farmingMode == "") {
            farmingMode = "Missing"
        }
        
        if(missionName == "") {
            missionName = "Missing"
        }
        
        if(itemName == "") {
            itemName = "Missing"
        }
        
        if(summon[0] == "") {
            summon = listOf("Requires at least 1 Summon")
        }
        
        settingsStatusTextView.text = "Farming Mode: $farmingMode\n" +
                "\nMission: $mission\n" +
                "\nMission: $missionName\n" +
                "\nItem: $itemName\n" +
                "\nItem Amount: $itemAmount\n" +
                "\nCombat Script: $combatScriptName\n" +
                "\nSummon: $summon\n" +
                "\nGroup: $groupNumber\n" +
                "\nParty: $partyNumber"

        return homeFragmentView
    }
    
    override fun onResume() {
        super.onResume()
    
        // Update the button's text depending on if the MediaProjection service is running.
        if(!firstBoot) {
            if(MediaProjectionService.isRunning) {
                startButton.text = getString(R.string.bot_stop)
            } else {
                startButton.text = getString(R.string.bot_start)
            }
        }
    
        // Setting this false here will ensure that stopping the MediaProjection Service outside of this application will update this button's text.
        firstBoot = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == SCREENSHOT_PERMISSION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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
        if(!checkForOverlayPermission() || !checkForAccessibilityPermission() || !checkForAccessibilityAlive()) {
            return false
        }
        
        return true
    }

    /**
     * Starts the MediaProjection Service.
     */
    private fun startProjection() {
        val mediaProjectionManager = context?.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), SCREENSHOT_PERMISSION_REQUEST_CODE)
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
        if(!Settings.canDrawOverlays(requireContext())) {
            Log.d(TAG, "Application is missing overlay permission.")
            
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
    
        Log.d(TAG, "Application has permission to draw overlay.")
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
        
        if(prefString != null && prefString.isNotEmpty()) {
            // Check the string of enabled accessibility services to see if this application's accessibility service is there.
            val enabled = prefString.contains(myContext.packageName.toString() + "/" + MyAccessibilityService::class.java.name)
    
            return if(enabled) {
                Log.d(TAG, "This application's Accessibility Service is currently turned on.")
                true
            } else {
                Log.e(TAG, "This application's Accessibility Service is currently turned off.")
                false
            }
        }
        
        // Moves the user to the Accessibility Settings if the service is not detected.
        AlertDialog.Builder(myContext).apply {
            setTitle(R.string.accessibility_disabled)
            setMessage(R.string.accessibility_disabled_message)
            setPositiveButton(R.string.go_to_settings) { _, _ ->
                Log.d(TAG, "Accessibility Service is not detected. Moving user to Accessibility Settings.")
                val accessibilitySettingsIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                myContext.startActivity(accessibilitySettingsIntent)
            }
            setNegativeButton(android.R.string.cancel, null)
            show()
        }
        
        return false
    }
    
    private fun checkForAccessibilityAlive(): Boolean {
        if(MyAccessibilityService.isInstanceInitialized()) {
            return true
        } else {
            // Moves the user to the Accessibility Settings if the service is not detected.
            AlertDialog.Builder(myContext).apply {
                setTitle(R.string.accessibility_uninitialized)
                setMessage(R.string.accessibility_uninitialized_message)
                setPositiveButton(R.string.go_to_settings) { _, _ ->
                    Log.d(TAG, "Accessibility Service is uninitialized. Moving user to Accessibility Settings.")
                    val accessibilitySettingsIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    myContext.startActivity(accessibilitySettingsIntent)
                }
                setNegativeButton(android.R.string.cancel, null)
                show()
            }
            
            return false
        }
    }
}
