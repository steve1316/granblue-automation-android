package com.steve1316.granblueautomation

import android.content.Intent
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference

/**
 * HomeFragment - Help users be redirected to the fragments/activities that they want to navigate to.
 */
class HomeFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Inflate the preferences to the view.
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Attach onClick listeners to travel to the appropriate fragments/activities for each preference.
        findPreference<Preference>("nav_start")?.let {
            it.setOnPreferenceClickListener {
                // Start the Bubble Activity.
                requireActivity().run {
                    startActivity(Intent(this, BubbleActivity::class.java))
                    finish()
                }

                true
            }
        }
        findPreference<Preference>("nav_raid_planner")?.let {
            it.setOnPreferenceClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToRaidPlannerFragment()
                findNavController().navigate(action)
                true
            }
        }
        findPreference<Preference>("nav_settings")?.let {
            it.setOnPreferenceClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToSettingsFragment()
                findNavController().navigate(action)
                true
            }
        }
        findPreference<Preference>("nav_debug")?.let {
            it.setOnPreferenceClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDebugScreenCaptureFragment()
                findNavController().navigate(action)
                true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
