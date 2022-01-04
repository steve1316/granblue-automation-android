package com.steve1316.granblue_automation_android.data

/**
 * This class simply holds the values of the items that are supported by this application.
 */
class ItemData {
	companion object {
		var items: MutableMap<String, MutableMap<String, ArrayList<String>>> = mutableMapOf()
	}
}