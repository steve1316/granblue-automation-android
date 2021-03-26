package com.steve1316.granblueautomation_android.bot

/**
 * This class handles the Combat Mode and offers helper functions to assist it.
 */
class CombatMode {
	/**
	 * Checks if the Party wiped during Combat Mode. Updates the retreat flag if so.
	 */
	private fun partyWipeCheck() {
		TODO("not yet implemented")
	}
	
	/**
	 * Find the total number of characters ready to Charge Attack.
	 *
	 * @return Total number of image matches found for charge attacks.
	 */
	private fun findChargeAttacks(): Int {
		TODO("not yet implemented")
	}
	
	/**
	 * Checks if there are any dialog popups during Combat Mode from either Lyria or Vyrn and close them.
	 */
	private fun findCombatDialog() {
		TODO("not yet implemented")
	}
	
	/**
	 * Uses the specified healing item during Combat Mode with an optional target if the item requires one.
	 *
	 * @param command The command for the healing item to use.
	 * @param target The character target for the item. This depends on what item it is. Defaults to 0.
	 */
	private fun useCombatHealingItem(command: String, target: Int = 0) {
		TODO("not yet implemented")
	}
	
	/**
	 * Request backup during Combat mode for this Raid.
	 */
	private fun requestBackup() {
		TODO("not yet implemented")
	}
	
	/**
	 * Request backup during Combat mode for this Raid by using the Twitter feature.
	 */
	private fun tweetBackup() {
		TODO("not yet implemented")
	}
	
	/**
	 * Selects the portrait of the specified character during Combat Mode.
	 *
	 * @param characterNumber The character that needs to be selected.
	 */
	private fun selectCharacter(characterNumber: Int) {
		TODO("not yet implemented")
	}
	
	/**
	 * Activate the specified skill for the already selected character.
	 *
	 * @param characterNumber The character whose skill needs to be used.
	 * @param skillNumber The skill that needs to be used.
	 */
	private fun useCharacterSkill(characterNumber: Int, skillNumber: Int) {
		TODO("not yet implemented")
	}
	
	/**
	 * Wait for a maximum of 20 seconds until the bot sees either the "Attack" or the "Next" button before starting a new turn.
	 */
	private fun waitForAttack() {
		TODO("not yet implemented")
	}
	
	/**
	 * Start Combat Mode with the provided combat script.
	 *
	 * @param combatScript ArrayList of all the lines in the text file.
	 * @return True if Combat Mode ended successfully. False otherwise if the Party wiped or backed out without retreating.
	 */
	fun startCombatMode(combatScript: List<String>): Boolean {
		TODO("not yet implemented")
	}
}