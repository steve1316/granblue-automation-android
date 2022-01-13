package com.steve1316.granblue_automation_android.data

/**
 * This class is needed to share the room code to MyAccessibilityService as the system clipboard is severely restricted in Android 10+ such that
 * the clipboard is practically unusable for the purposes of this application.
 */
class RoomCodeData {
	companion object {
		var roomCode = ""
	}
}