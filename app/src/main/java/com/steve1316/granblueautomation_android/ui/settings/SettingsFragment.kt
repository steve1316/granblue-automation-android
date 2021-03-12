package com.steve1316.granblueautomation_android.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.steve1316.granblueautomation_android.R

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)

        settingsViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        return root
    }
    
    // TODO: Implement options for the user to choose item to farm, amount of it, mission, Summons, combat script, etc.
}