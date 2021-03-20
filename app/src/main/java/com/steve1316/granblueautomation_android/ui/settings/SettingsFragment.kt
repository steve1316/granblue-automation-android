package com.steve1316.granblueautomation_android.ui.settings

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.content.edit
import androidx.preference.*
import com.steve1316.granblueautomation_android.R

class SettingsFragment : PreferenceFragmentCompat() {
    private val TAG: String = "GAA_SettingsFragment"
    private val OPEN_FILE_PERMISSION = 1001
    
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesEditor: SharedPreferences.Editor
    
    private lateinit var preferenceCategoryCombatScript: PreferenceCategory
    
    private val itemsForQuest = mapOf(
        "Scattered Cargo" to arrayListOf("Satin Feather", "Zephyr Feather", "Flying Sprout"),
        "Lucky Charm Hunt" to arrayListOf("Fine Sand Bottle", "Untamed Flame", "Blistering Ore"),
        "Special Op's Request" to arrayListOf("Fine Sand Bottle", "Untamed Flame", "Blistering Ore"),
        "Threat to the Fisheries" to arrayListOf("Fresh Water Jug", "Soothing Splash", "Glowing Coral"),
        "The Fruit of Lumacie" to arrayListOf("Rough Stone", "Swirling Amber", "Coarse Alluvium"),
        "I Challenge You!" to arrayListOf("Falcon Feather", "Spring Water Jug", "Vermilion Stone"),
        "For Whom the Bell Tolls" to arrayListOf("Slimy Shroom", "Hollow Soul", "Lacrimosa"),
        "Golonzo's Battles of Old" to arrayListOf("Wheat Stalk", "Iron Cluster", "Olea Plant"),
        "The Dungeon Diet" to arrayListOf("Indigo Fruit", "Foreboding Clover", "Blood Amber"),
        "Trust Busting Dustup" to arrayListOf("Sand Brick", "Native Reed", "Antique Cloth"),
        "Imperial Wanderer's Soul" to arrayListOf("Prosperity Flame", "Explosive Material", "Steel Liquid")
    )
    
    private val itemsForSpecial = mapOf(
        "Scarlet Trial" to arrayListOf(
            "Fire Orb", "Water Orb", "Earth Orb", "Wind Orb", "Light Orb", "Dark Orb", "Inferno Orb", "Frost Orb",
            "Rumbling Orb", "Cyclone Orb", "Shining Orb", "Abysm Orb"
        ),
        "Cerulean Trial" to arrayListOf(
            "Red Tome", "Blue Tome", "Brown Tome", "Green Tome", "White Tome", "Black Tome", "Hellfire Scroll",
            "Flood Scroll", "Thunder Scroll", "Gale Scroll", "Skylight Scroll", "Chasm Scroll", "Infernal Whorl", "Tidal Whorl", "Seismic Whorl",
            "Tempest Whorl", "Radiant Whorl", "Umbral Whorl"
        ),
        "Violet Trial" to arrayListOf("Prism Chip", "Flawed Prism", "Flawless Prism", "Rainbow Prism"),
        "Shiny Slime Search!" to arrayListOf("EXP"),
        "Six Dragon Trials" to arrayListOf(
            "Red Dragon Scale", "Blue Dragon Scale", "Brown Dragon Scale", "Green Dragon Scale", "White Dragon Scale",
            "Black Dragon Scale"
        ),
        "Ifrit Showdown" to arrayListOf("Jasper Scale", "Scorching Peak", "Infernal Garnet", "Ifrit Anima", "Ifrit Omega Anima"),
        "Cocytus Showdown" to arrayListOf("Mourning Stone", "Crystal Spirit", "Frozen Hell Prism", "Cocytus Anima", "Cocytus Omega Anima"),
        "Vohu Manah Showdown" to arrayListOf(
            "Scrutiny Stone", "Luminous Judgment", "Evil Judge Crystal", "Vohu Manah Anima",
            "Vohu Manah Omega Anima"
        ),
        "Sagittarius Showdown" to arrayListOf(
            "Sagittarius Arrowhead", "Sagittarius Rune", "Horseman's Plate", "Sagittarius Anima",
            "Sagittarius Omega Anima"
        ),
        "Corow Showdown" to arrayListOf("Solar Ring", "Sunlight Quartz", "Halo Light Quartz", "Corow Anima", "Corow Omega Anima"),
        "Diablo Showdown" to arrayListOf("Twilight Cloth Strip", "Shadow Silver", "Phantom Demon Jewel", "Diablo Anima", "Diablo Omega Anima"),
        "Extreme Trials" to arrayListOf("Hellfire Fragment", "Deluge Fragment", "Wasteland Fragment", "Typhoon Fragment"),
        "Angel Halo" to arrayListOf("Angel Halo Weapons")
    )
    
    private val itemsForCoop = mapOf(
        "H3-1 In a Dusk Dream" to arrayListOf("EXP"),
        "EX1-1 Corridor of Puzzles" to arrayListOf("Warrior Creed", "Mage Creed"),
        "EX1-3 Lost in the Dark" to arrayListOf("Warrior Creed", "Mage Creed"),
        "EX2-2 Time of Judgement" to arrayListOf(
            "Evil Judge Crystal", "Pilgrim Distinction", "Mage Distinction", "Alchemist Distinction",
            "Monk's Distinction", "Keraunos Replica", "Faust Replica"
        ),
        "EX2-3 Time of Revelation" to arrayListOf(
            "Infernal Garnet", "Gladiator Distinction", "Fencer Distinction", "Dual Wielder Distinction",
            "Forester's Distinction", "Avenger Replica", "Hellion Gauntlet Replica"
        ),
        "EX2-4 Time of Eminence" to arrayListOf(
            "Halo Light Quartz", "Bandit Distinction", "Troubadour Distinction", "Mystic Distinction",
            "Shredder Distinction", "Nirvana Replica", "Romulus Spear Replica", "Murakumo Replica"
        ),
        "EX3-2 Rule of the Tundra" to arrayListOf(
            "Frozen Hell Prism", "Guardian Distinction", "Combatant Distinction", "Sword Master Distinction",
            "Dragoon's Distinction", "Skofnung Replica", "Langeleik Replica", "Kapilavastu Replica"
        ),
        "EX3-3 Rule of the Plains" to arrayListOf(
            "Horseman's Plate", "Sharpshooter Distinction", "Cavalryman Distinction", "Gunslinger Distinction",
            "Oliver Replica", "Rosenbogen Replica", "Misericorde Replica"
        ),
        "EX3-4 Rule of the Twilight" to arrayListOf(
            "Phantom Demon Jewel", "Samurai Distinction", "Ninja Distinction", "Assassin Distinction",
            "Ipetam Replica", "Proximo Replica", "Nebuchad Replica", "Muramasa Replica"
        ),
        "EX4-2 Amidst the Waves" to arrayListOf(
            "Pilgrim Distinction", "Mage Distinction", "Alchemist Distinction", "Mystic Distinction",
            "Monk's Distinction", "Oliver Replica", "Langeleik Replica", "Romulus Spear Replica", "Proximo Replica", "Kapilavastu Replica"
        ),
        "EX4-3 Amidst the Petals" to arrayListOf(
            "Sharpshooter Distinction", "Samurai Distinction", "Ninja Distinction", "Gunslinger Distinction",
            "Assassin Distinction", "Longstrider's Distinction", "Langeleik Replica", "Misericorde Replica", "Faust Replica"
        ),
        "EX4-4 Amidst Severe Cliffs" to arrayListOf(
            "Gladiator Distinction", "Fencer Distinction", "Combatant Distinction", "Sword Master Distinction",
            "Aschallon Replica", "Hellion Gauntlet Replica", "Muramasa Replica", "Practice Drum"
        ),
        "EX4-5 Amidst the Flames" to arrayListOf(
            "Guardian Distinction", "Bandit Distinction", "Troubadour Distinction", "Cavalryman Distinction",
            "Dragoon's Distinction", "Ipetam Replica", "Murakumo Replica", "Nebuchad Replica"
        )
    )
    
    private val itemsForRaid = mapOf(
        "Tiamat Omega" to arrayListOf(
            "Tiamat Omega", "Tiamat Anima", "Tiamat Omega Anima", "Tiamat Amood Omega", "Tiamat Bolt Omega",
            "Tiamat Gauntlet Omega", "Tiamat Glaive Omega"
        ),
        "Colossus Omega" to arrayListOf(
            "Colossus Omega", "Colossus Anima", "Colossus Omega Anima", "Colossus Blade Omega", "Colossus Cane Omega",
            "Colossus Carbine Omega", "Colossus Fist Omega"
        ),
        "Leviathan Omega" to arrayListOf(
            "Leviathan Omega", "Leviathan Anima", "Leviathan Omega Anima", "Leviathan Bow Omega", "Leviathan Gaze Omega",
            "Leviathan Scepter Omega", "Leviathan Spear Omega"
        ),
        "Yggdrasil Omega" to arrayListOf(
            "Yggdrasil Omega", "Yggdrasil Anima", "Yggdrasil Omega Anima", "Yggdrasil Bow Omega",
            "Yggdrasil Crystal Blade Omega", "Yggdrasil Dagger Omega", "Yggdrasil Dewbranch Omega"
        ),
        "Luminiera Omega" to arrayListOf(
            "Luminiera Omega", "Luminiera Anima", "Luminiera Omega Anima", "Luminiera Bhuj Omega", "Luminiera Bolt Omega",
            "Luminiera Harp Omega", "Luminiera Sword Omega"
        ),
        "Celeste Omega" to arrayListOf(
            "Celeste Omega", "Celeste Anima", "Celeste Omega Anima", "Celeste Harp Omega", "Celeste Claw Omega",
            "Celeste Horn Omega", "Celeste Zaghnal Omega"
        ),
        "Shiva" to arrayListOf("Shiva Anima", "Shiva Omega Anima", "Hand of Brahman", "Scimitar of Brahman", "Trident of Brahman", "Nilakantha"),
        "Europa" to arrayListOf("Europa Anima", "Europa Omega Anima", "Tyros Bow", "Tyros Scepter", "Tyros Zither", "Spirit of Mana"),
        "Godsworn Alexiel" to arrayListOf(
            "Alexiel Anima", "Alexiel Omega Anima", "Nibelung Horn", "Nibelung Klinge", "Nibelung Messer",
            "Godsworn Edge"
        ),
        "Grimnir" to arrayListOf(
            "Grimnir Anima", "Grimnir Omega Anima", "Last Storm Blade", "Last Storm Harp", "Last Storm Lance",
            "Coruscant Crozier"
        ),
        "Metatron" to arrayListOf(
            "Metatron Anima", "Metatron Omega Anima", "Mittron's Treasured Blade", "Mittron's Gauntlet", "Mittron's Bow",
            "Pillar of Flame"
        ),
        "Avatar" to arrayListOf("Avatar Anima", "Avatar Omega Anima", "Abyss Striker", "Abyss Spine", "Abyss Gaze", "Zechariah"),
        "Twin Elements" to arrayListOf("Twin Elements Anima", "Twin Elements Omega Anima", "Ancient Ecke Sachs", "Ecke Sachs"),
        "Macula Marius" to arrayListOf("Macula Marius Anima", "Macula Marius Omega Anima", "Ancient Auberon", "Auberon"),
        "Medusa" to arrayListOf("Medusa Anima", "Medusa Omega Anima", "Ancient Perseus", "Perseus"),
        "Nezha" to arrayListOf("Nezha Anima", "Nezha Omega Anima", "Ancient Nalakuvara", "Nalakuvara"),
        "Apollo" to arrayListOf("Apollo Anima", "Apollo Omega Anima", "Ancient Bow of Artemis", "Bow of Artemis"),
        "Dark Angel Olivia" to arrayListOf("Dark Angel Olivia Anima", "Dark Angel Olivia Omega Anima", "Ancient Cortana", "Cortana"),
        "Athena" to arrayListOf("Athena Anima", "Athena Omega Anima", "Erichthonius", "Sword of Pallas"),
        "Grani" to arrayListOf("Grani Anima", "Grani Omega Anima", "Bow of Sigurd", "Wilhelm"),
        "Baal" to arrayListOf("Baal Anima", "Baal Omega Anima", "Solomon's Axe", "Spymur's Vision"),
        "Garuda" to arrayListOf("Garuda Anima", "Garuda Omega Anima", "Plume of Suparna", "Indra's Edge"),
        "Odin" to arrayListOf("Odin Anima", "Odin Omega Anima", "Gungnir", "Sleipnir Shoe"),
        "Lich" to arrayListOf("Lich Anima", "Lich Omega Anima", "Obscuritas", "Phantasmas"),
        "Prometheus" to arrayListOf("Prometheus Anima", "Fire of Prometheus", "Chains of Caucasus"),
        "Ca Ong" to arrayListOf("Ca Ong Anima", "Keeper of Hallowed Ground", "Savior of Hallowed Ground"),
        "Gilgamesh" to arrayListOf("Gilgamesh Anima", "All-Might Spear", "All-Might Battle-Axe"),
        "Morrigna" to arrayListOf("Morrigna Anima", "Le Fay", "Unius"),
        "Hector" to arrayListOf("Hector Anima", "Bow of Iliad", "Adamantine Gauntlet"),
        "Anubis" to arrayListOf("Anubis Anima", "Hermanubis", "Scales of Dominion"),
        "Tiamat Malice" to arrayListOf("Tiamat Malice Anima", "Hatsoiiłhał", "Majestas"),
        "Leviathan Malice" to arrayListOf("Leviathan Malice Anima", "Kaladanda", "Kris of Hypnos"),
        "Phronesis" to arrayListOf("Phronesis Anima", "Dark Thrasher", "Master Bamboo Sword"),
        "Grand Order" to arrayListOf("Azure Feather", "Heavenly Horn"),
        "Proto Bahamut" to arrayListOf("Horn of Bahamut", "Champion Merit", "Primeval Horn"),
        "Rose Queen" to arrayListOf("Rose Petal")
    )
    
    // This listener is triggered when the user changes a Preference setting in the Settings Page.
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        if(key != null && (key == "farmingModePicker" || key == "missionPicker" || key == "itemPicker")) {
            // Key is associated with one of the ListPreference pickers.
    
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
                
                // Now save the value into SharedPreferences.
                sharedPreferences.edit {
                    putString("farmingMode", farmingModePicker.value)
                    commit()
                }
    
                // Populate the Mission picker with the missions associated with the newly chosen Farming Mode.
                val picker: ListPreference = findPreference("missionPicker")!!
                picker.entries = newEntries.toTypedArray()
                picker.entryValues = newEntryValues.toTypedArray()
    
                // Now reveal the Mission picker and reset its value.
                picker.isVisible = true
                picker.value = null
                
                // Finally, hide and reset every other Preference setting as the Farming Mode that has now been changed will dictate the contents
                // of every other Preference setting after it.
                val itemPicker: ListPreference = findPreference("itemPicker")!!
                itemPicker.value = null
                itemPicker.isVisible = false
            } else if(key == "missionPicker") {
                // Fill out the new entries and values for Items based on the newly chosen Mission.
                val missionPicker: ListPreference = findPreference("missionPicker")!!
                val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
                
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
    
                // Now save the value into SharedPreferences.
                sharedPreferences.edit {
                    putString("mission", missionPicker.value)
                    commit()
                }
    
                // Populate the Item picker with the items associated with the newly chosen Mission.
                val picker: ListPreference = findPreference("itemPicker")!!
                picker.entries = newEntries.toTypedArray()
                picker.entryValues = newEntryValues.toTypedArray()
                
                // Reveal the Item picker.
                picker.isVisible = true
            } else if(key == "itemPicker") {
                val itemPicker: ListPreference = findPreference("itemPicker")!!
                
                // Now save the value into SharedPreferences.
                sharedPreferences.edit {
                    putString("item", itemPicker.value)
                    commit()
                }
            }
        }
    }
    
    private fun populateMissionListPreference() {
        val newEntries = mutableListOf<CharSequence>()
        val newEntryValues = mutableListOf<CharSequence>()
    
        val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
        val missionPicker: ListPreference = findPreference("missionPicker")!!
        
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
    
    private fun populateItemListPreference() {
        val newEntries = mutableListOf<CharSequence>()
        val newEntryValues = mutableListOf<CharSequence>()
    
        val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
        val missionPicker: ListPreference = findPreference("missionPicker")!!
        val itemPicker: ListPreference = findPreference("itemPicker")!!
    
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
    
    override fun onResume() {
        super.onResume()
        
        // Makes sure that OnSharedPreferenceChangeListener works properly and avoids the situation where the app suddenly stops triggering the
        // listener.
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }
    
    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
    
    @SuppressLint("CommitPrefEdits")
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
    
        // Get the SharedPreferences and its Editor object.
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferencesEditor = sharedPreferences.edit()
    
        preferenceCategoryCombatScript = findPreference("combatScriptTitle")!!
        
        Log.d(TAG, "Preferences created")
    
        // Grab the preferences from the previous time the user used the app.
        val combatScript = sharedPreferences.getString("combatScript", "")
        val farmingMode = sharedPreferences.getString("farmingMode", "")
        val mission = sharedPreferences.getString("mission", "")
        val item = sharedPreferences.getString("item", "")

        // Get references to the Preferences.
        val farmingModePicker: ListPreference = findPreference("farmingModePicker")!!
        val missionPicker: ListPreference = findPreference("missionPicker")!!
        val itemPicker: ListPreference = findPreference("itemPicker")!!
        
        Log.d(TAG, "combat script: $combatScript")
        Log.d(TAG, "farming mode: $farmingMode")
        Log.d(TAG, "mission: $mission")
        Log.d(TAG, "item: $item")
        

        // Now set the values of the preferences from the shared preferences.
        if(combatScript != null && combatScript.isNotEmpty()) {
            val preferenceCategory = findPreference<PreferenceCategory>("combatScriptTitle")
            preferenceCategory?.title = "Combat Script: Selected $combatScript"
        }

        if(farmingMode != null && farmingMode.isNotEmpty()) {
            Log.d(TAG, "Farming Mode: $farmingMode")
            farmingModePicker.value = farmingMode

            // Reveal the Mission picker.
            missionPicker.isVisible = true
        }

        if(mission != null && mission.isNotEmpty()) {
            Log.d(TAG, "Mission: $mission")
            
            // Populate the Mission picker.
            populateMissionListPreference()
            
            missionPicker.value = mission
            missionPicker.isVisible = true

            // Reveal the Item picker.
            itemPicker.isVisible = true
        }

        if(item != null && item.isNotEmpty()) {
            Log.d(TAG, "Item: $item")
            
            // Populate the Item picker.
            populateItemListPreference()
            
            itemPicker.value = item
            itemPicker.isVisible = true
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