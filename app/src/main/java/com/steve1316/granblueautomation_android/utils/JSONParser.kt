package com.steve1316.granblueautomation_android.utils

import android.content.Context
import com.beust.klaxon.JsonReader
import com.steve1316.granblueautomation_android.data.ConfigData
import com.steve1316.granblueautomation_android.data.ItemData
import com.steve1316.granblueautomation_android.data.MissionData
import java.io.File
import java.io.StringReader

class JSONParser(private val myContext: Context) {
	/**
	 * Construct the data classes associated with the provided JSON data files.
	 */
	fun constructDataClasses() {
		// Construct the data class for items and missions.
		val fileList = arrayListOf("items.json", "missions.json")
		while (fileList.size > 0) {
			val fileName = fileList[0]
			fileList.removeAt(0)
			val objectString = myContext.assets.open("data/$fileName").bufferedReader().use { it.readText() }
			
			JsonReader(StringReader(objectString)).use { reader ->
				reader.beginObject {
					while (reader.hasNext()) {
						// Grab the name.
						val name = reader.nextName()
						
						val contents = mutableMapOf<String, ArrayList<String>>()
						reader.beginObject {
							while (reader.hasNext()) {
								// Grab the event name.
								val eventName = reader.nextName()
								contents.putIfAbsent(eventName, arrayListOf())
								
								reader.beginArray {
									// Grab all of the event option rewards for this event and add them to the map.
									while (reader.hasNext()) {
										val optionReward = reader.nextString()
										contents[eventName]?.add(optionReward)
									}
								}
							}
						}
						
						// Finally, put into the MutableMap the key value pair depending on the current category.
						if (fileName == "items.json") {
							ItemData.items[name] = contents
						} else {
							MissionData.missions[name] = contents
						}
					}
				}
			}
		}
	}
	
	fun constructConfigClass() {
		// Now construct the data class for config.
		val objectString = File(myContext.getExternalFilesDir(null), "config.json").bufferedReader().use { it.readText() }
		JsonReader(StringReader(objectString)).use { reader ->
			reader.beginObject {
				while (reader.hasNext()) {
					// Grab setting category name.
					when (reader.nextName()) {
						"discord" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									val key = reader.nextString()
									val value = reader.nextString()
									
									if (key == "discordToken") {
										ConfigData.discordToken = value
									} else if (key == "userID") {
										ConfigData.userID = value
									}
								}
							}
						}
						"twitter" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									val key = reader.nextString()
									val value = reader.nextString()
									
									if (key == "apiKey") {
										ConfigData.apiKey = value
									} else if (key == "apiKeySecret") {
										ConfigData.apiKeySecret = value
									} else if (key == "accessToken") {
										ConfigData.accessToken = value
									} else if (key == "accessTokenSecret") {
										ConfigData.accessTokenSecret = value
									}
								}
							}
						}
						"refill" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									val key = reader.nextString()
									val value = reader.nextBoolean()
									
									if (key == "fullElixir") {
										ConfigData.fullElixir = value
									} else if (key == "soulBalm") {
										ConfigData.soulBalm = value
									}
								}
							}
						}
						"dimensionalHalo" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									val key = reader.nextString()
									
									if (key == "enableDimensionalHalo") {
										val value = reader.nextBoolean()
										ConfigData.enableDimensionalHalo = value
									} else if (key == "dimensionalHaloSummonList") {
										val value = reader.nextArray() as List<String>
										ConfigData.dimensionalHaloSummonList = value
									} else if (key == "dimensionalHaloGroupNumber") {
										val value = reader.nextInt()
										ConfigData.dimensionalHaloGroupNumber = value
									} else if (key == "dimensionalHaloPartyNumber") {
										val value = reader.nextInt()
										ConfigData.dimensionalHaloPartyNumber = value
									}
								}
							}
						}
						"event" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									val key = reader.nextString()
									
									if (key == "enableEventNightmare") {
										val value = reader.nextBoolean()
										ConfigData.enableEventNightmare = value
									} else if (key == "eventNightmareSummonList") {
										val value = reader.nextArray() as List<String>
										ConfigData.eventNightmareSummonList = value
									} else if (key == "eventNightmareGroupNumber") {
										val value = reader.nextInt()
										ConfigData.eventNightmareGroupNumber = value
									} else if (key == "eventNightmarePartyNumber") {
										val value = reader.nextInt()
										ConfigData.eventNightmarePartyNumber = value
									}
								}
							}
						}
						"rotb" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									val key = reader.nextString()
									
									if (key == "enableROTBExtremePlus") {
										val value = reader.nextBoolean()
										ConfigData.enableROTBExtremePlus = value
									} else if (key == "rotbExtremePlusSummonList") {
										val value = reader.nextArray() as List<String>
										ConfigData.rotbExtremePlusSummonList = value
									} else if (key == "rotbExtremePlusGroupNumber") {
										val value = reader.nextInt()
										ConfigData.rotbExtremePlusGroupNumber = value
									} else if (key == "rotbExtremePlusPartyNumber") {
										val value = reader.nextInt()
										ConfigData.rotbExtremePlusPartyNumber = value
									}
								}
							}
						}
						"xenoClash" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									val key = reader.nextString()
									
									if (key == "enableXenoClashNightmare") {
										val value = reader.nextBoolean()
										ConfigData.enableXenoClashNightmare = value
									} else if (key == "xenoClashNightmareSummonList") {
										val value = reader.nextArray() as List<String>
										ConfigData.xenoClashNightmareSummonList = value
									} else if (key == "xenoClashNightmareGroupNumber") {
										val value = reader.nextInt()
										ConfigData.xenoClashNightmareGroupNumber = value
									} else if (key == "xenoClashNightmarePartyNumber") {
										val value = reader.nextInt()
										ConfigData.xenoClashNightmarePartyNumber = value
									}
								}
							}
						}
					}
				}
			}
		}
	}
}