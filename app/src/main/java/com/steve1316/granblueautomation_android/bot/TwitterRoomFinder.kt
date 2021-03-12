package com.steve1316.granblueautomation_android.bot

/**
 * Provides the functions needed to perform Twitter API-related tasks such as searching tweets for room codes.
 */
class TwitterRoomFinder {
	// TODO: Get the consumer keys and access tokens from a text file in the onCreate.
	
	/**
	 * Start collected tweets containing room codes starting with JP region and then fallback to EN region if there was not enough collected.
	 *
	 * @param raidName Name and level of the Raid that appears in tweets containing the room code to it.
	 * @param count Number of most recent tweets to grab. Defaults to 10.
	 * @return ArrayList of the most recent tweets that match the query.
	 */
	fun findMostRecentRoomCodes(raidName: String, count: Int = 10): ArrayList<String> {
		// TODO: Connect to the Twitter API here.
		
		TODO("not yet implemented")
	}
	
	/**
	 * Clean the tweets and parse out the room codes from them.
	 *
	 * @param tweets ArrayList of tweets.
	 * @return ArrayList of room codes.
	 */
	private fun parseRoomCodes(tweets: ArrayList<String>): ArrayList<String> {
		TODO("not yet implemented")
	}
}