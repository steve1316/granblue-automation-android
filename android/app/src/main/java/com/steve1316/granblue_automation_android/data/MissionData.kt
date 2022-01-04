package com.steve1316.granblue_automation_android.data

/**
 * This class simply holds the Missions and associated Maps if applicable that are supported by this application.
 */
class MissionData {
	companion object {
		var missions: MutableMap<String, MutableMap<String, ArrayList<String>>> = mutableMapOf()
	}
}