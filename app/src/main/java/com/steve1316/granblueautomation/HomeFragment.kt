package com.steve1316.granblueautomation

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference

class HomeFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Inflate the preferences to the view.
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Attach onClick listeners to travel to the appropriate fragments for each preference.
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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
