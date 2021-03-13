package com.steve1316.granblueautomation_android.bot

import android.util.Log
import java.util.*

/**
 * Provides the utility functions needed for perform navigation for Farming Mode throughout Granblue Fantasy.
 */
class MapSelection(private val game: Game) {
	private val TAG: String = "GAA_MapSelection"
	
	/**
	 * Navigates the bot to the specified map and preps the bot for Summon/Party selection.
	 *
	 * @param farmingMode Mode to look for the specified item and map in.
	 * @param mapName Name of the map to look for the specified mission in.
	 * @param itemName Name of the item to farm.
	 * @param missionName Name of the mission to farm the item in.
	 * @param difficulty Selected difficulty for certain missions.
	 * @return True if the bot reached the Summon Selection screen. False otherwise.
	 */
	fun selectMap(farmingMode: String, mapName: String, itemName: String, missionName: String, difficulty: String): Boolean {
		try {
			var currentLocation = ""
			val formattedMapName = mapName.replace(" ", "_").replace("-", "_")
			
			// Go to the Home screen.
			game.goBackHome(confirmLocationCheck = true)
			
			if(farmingMode.toLowerCase(Locale.ROOT) == "quest") {
				val checkLocation = false
				
				if(game.imageUtils.confirmLocation("map_$formattedMapName", tries=2)) {
					return true
				}
				
			} else if(farmingMode.toLowerCase(Locale.ROOT) == "special") {
				// Go to the Quests screen and then to the Special Quest screen.
				game.findAndClickButton("quest")
				
				if(game.imageUtils.confirmLocation("quest")) {
					game.findAndClickButton("special")
					
					if(game.imageUtils.confirmLocation("special")) {
						Log.d(TAG, "YES I AM HERE")
					}
				}
			}
		} catch(e: Exception) {
			Log.e(TAG, "Encountered an exception in selectMap(): ")
			e.printStackTrace()
		}
		
		return false
	}
	
	/**
	 * Process a Pending Battle.
	 */
	private fun clearPendingBattle() {
		TODO("not yet implemented")
	}
	
	/**
	 * Check and collect any pending rewards and free up slots for the bot to join more Raids.
	 *
	 * @param farmingMode The current farming mode will dictate what logic to follow for Pending Battles.
	 * @return True if Pending Battles were detected. False otherwise.
	 */
	fun checkPendingBattles(farmingMode: String): Boolean {
		TODO("not yet implemented")
	}
	
	/**
	 * Check and updates the number of Raids currently joined.
	 */
	private fun checkJoinedRaids() {
		TODO("not yet implemented")
	}
	
	/**
	 * Navigates the bot to the Backup Requests screen and attempt to join a specified Raid.
	 *
	 * @param missionName Name of the Raid.
	 * @return True if the bot reached the Summon Selection screen after joining a Raid. False otherwise.
	 */
	fun joinRaid(missionName: String): Boolean {
		TODO("not yet implemented")
		
		// TODO: In the case of unable to join using the current room code, simply hit the "Reload" button to clear the text box.
	}
}