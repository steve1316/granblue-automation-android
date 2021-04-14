package com.steve1316.granblueautomation_android.bot

import android.content.Context
import android.os.StrictMode
import android.util.Log
import com.steve1316.granblueautomation_android.ui.settings.SettingsFragment
import twitter4j.Query
import twitter4j.Status
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.conf.ConfigurationBuilder

/**
 * Provides the functions needed to perform Twitter API-related tasks such as searching tweets for room codes.
 */
class TwitterRoomFinder(myContext: Context, game: Game) {
	private val TAG: String = "GAA_TwitterRoomFinder"
	
	private lateinit var twitter: Twitter
	
	private val alreadyVisitedRoomCodes: ArrayList<String> = arrayListOf()
	private val alreadyVisitedIDs: ArrayList<Long> = arrayListOf()
	
	init {
		try {
			game.printToLog("\n[INFO] Connecting to Twitter API...", MESSAGE_TAG = TAG)
			
			// Initialize the Twitter object.
			val configurationBuilder: ConfigurationBuilder = ConfigurationBuilder()
				.setOAuthConsumerKey(SettingsFragment.getStringSharedPreference(myContext, "apiKey"))
				.setOAuthConsumerSecret(SettingsFragment.getStringSharedPreference(myContext, "apiKeySecret"))
				.setOAuthAccessToken(SettingsFragment.getStringSharedPreference(myContext, "accessToken"))
				.setOAuthAccessTokenSecret(SettingsFragment.getStringSharedPreference(myContext, "accessTokenSecret"))
			
			twitter = TwitterFactory(configurationBuilder.build()).instance
			
			// Test connection by fetching user's timeline.
			twitter.timelines().homeTimeline
			
			game.printToLog("[SUCCESS] Connection to Twitter API is successful.", MESSAGE_TAG = TAG)
			
			// Allow Network IO to be run on the main thread without throwing the NetworkOnMainThreadException.
			val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
			StrictMode.setThreadPolicy(policy)
			
			game.printToLog("[INFO] Main thread will now allow Network IO to be run on it without throwing NetworkOnMainThreadException.", MESSAGE_TAG = TAG)
		} catch (e: Exception) {
			game.printToLog("[ERROR] Failed to connect to Twitter API: ${e.printStackTrace()}", MESSAGE_TAG = TAG, isError = true)
		}
	}
	
	/**
	 * Start collected tweets containing room codes from EN and JP players.
	 *
	 * @param raidName Name and level of the Raid that appears in tweets containing the room code to it.
	 * @param count Number of most recent tweets to grab. Defaults to 10.
	 * @return ArrayList of the most recent room codes that match the query.
	 */
	fun findMostRecentRoomCodes(raidName: String, count: Int = 10): ArrayList<String> {
		// Construct the queries.
		val queryEN = Query("+(:Battle ID) AND +($raidName)")
		queryEN.count = count / 2
		val queryJP = Query("+(:参戦ID) AND +($raidName)")
		queryJP.count = count / 2
		
		// Retrieve tweets from both EN and JP players.
		val tweetsEN = twitter.search(queryEN).tweets
		val tweetsJP = twitter.search(queryJP).tweets
		
		// Filter out tweets that the bot already visited.
		val tweets: MutableList<Status> = mutableListOf()
		tweetsEN.forEach { tweet ->
			if (!alreadyVisitedIDs.contains(tweet.id)) {
				alreadyVisitedIDs.add(tweet.id)
				tweets.add(tweet)
			}
		}
		tweetsJP.forEach { tweet ->
			if (!alreadyVisitedIDs.contains(tweet.id)) {
				alreadyVisitedIDs.add(tweet.id)
				tweets.add(tweet)
			}
		}
		
		return parseRoomCodes(tweets)
	}
	
	/**
	 * Clean the tweets and parse out the room codes from them.
	 *
	 * @param tweets List of Status objects containing the tweets.
	 * @return ArrayList of room codes.
	 */
	private fun parseRoomCodes(tweets: MutableList<Status>): ArrayList<String> {
		val roomCodes: ArrayList<String> = arrayListOf()
		
		tweets.forEach { tweet ->
			// Split up the text by whitespaces.
			val splitText = tweet.text.split(" ")
			var index = 0
			
			// For each split element, parse the Room Code and save it.
			splitText.forEach { text ->
				if (text.contains(":Battle") || text.contains(":参戦ID")) {
					val roomCode = splitText[index - 1]
					
					if (!alreadyVisitedRoomCodes.contains(roomCode)) {
						alreadyVisitedRoomCodes.add(roomCode)
						roomCodes.add(roomCode)
					}
				}
				
				index += 1
			}
		}
		
		return roomCodes
	}
}