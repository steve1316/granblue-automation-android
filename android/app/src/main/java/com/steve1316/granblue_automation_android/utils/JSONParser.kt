package com.steve1316.granblue_automation_android.utils

import android.content.Context
import com.beust.klaxon.JsonReader
import com.steve1316.granblue_automation_android.data.ConfigData
import com.steve1316.granblue_automation_android.data.ItemData
import com.steve1316.granblue_automation_android.data.MissionData
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

	/**
	 * Construct the ConfigData class associated with the config.json file.
	 */
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

									when (key) {
										"apiKey" -> {
											ConfigData.apiKey = value
										}
										"apiKeySecret" -> {
											ConfigData.apiKeySecret = value
										}
										"accessToken" -> {
											ConfigData.accessToken = value
										}
										"accessTokenSecret" -> {
											ConfigData.accessTokenSecret = value
										}
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
									when (reader.nextString()) {
										"enableDimensionalHalo" -> {
											val value = reader.nextBoolean()
											ConfigData.enableDimensionalHalo = value
										}
										"dimensionalHaloSummonList" -> {
											val value = reader.nextArray() as List<String>
											ConfigData.dimensionalHaloSummonList = value
										}
										"dimensionalHaloGroupNumber" -> {
											val value = reader.nextInt()
											ConfigData.dimensionalHaloGroupNumber = value
										}
										"dimensionalHaloPartyNumber" -> {
											val value = reader.nextInt()
											ConfigData.dimensionalHaloPartyNumber = value
										}
									}
								}
							}
						}
						"event" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									when (reader.nextString()) {
										"enableEventNightmare" -> {
											val value = reader.nextBoolean()
											ConfigData.enableEventNightmare = value
										}
										"eventNightmareSummonList" -> {
											val value = reader.nextArray() as List<String>
											ConfigData.eventNightmareSummonList = value
										}
										"eventNightmareGroupNumber" -> {
											val value = reader.nextInt()
											ConfigData.eventNightmareGroupNumber = value
										}
										"eventNightmarePartyNumber" -> {
											val value = reader.nextInt()
											ConfigData.eventNightmarePartyNumber = value
										}
									}
								}
							}
						}
						"rotb" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									when (reader.nextString()) {
										"enableROTBExtremePlus" -> {
											val value = reader.nextBoolean()
											ConfigData.enableROTBExtremePlus = value
										}
										"rotbExtremePlusSummonList" -> {
											val value = reader.nextArray() as List<String>
											ConfigData.rotbExtremePlusSummonList = value
										}
										"rotbExtremePlusGroupNumber" -> {
											val value = reader.nextInt()
											ConfigData.rotbExtremePlusGroupNumber = value
										}
										"rotbExtremePlusPartyNumber" -> {
											val value = reader.nextInt()
											ConfigData.rotbExtremePlusPartyNumber = value
										}
									}
								}
							}
						}
						"xenoClash" -> {
							reader.beginObject {
								while (reader.hasNext()) {
									when (reader.nextString()) {
										"enableXenoClashNightmare" -> {
											val value = reader.nextBoolean()
											ConfigData.enableXenoClashNightmare = value
										}
										"xenoClashNightmareSummonList" -> {
											val value = reader.nextArray() as List<String>
											ConfigData.xenoClashNightmareSummonList = value
										}
										"xenoClashNightmareGroupNumber" -> {
											val value = reader.nextInt()
											ConfigData.xenoClashNightmareGroupNumber = value
										}
										"xenoClashNightmarePartyNumber" -> {
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
}