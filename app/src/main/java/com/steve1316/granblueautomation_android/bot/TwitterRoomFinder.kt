package com.steve1316.granblueautomation_android.bot

import android.content.Context
import android.os.StrictMode
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
	
	private val listOfRaids = mapOf(
		"Lvl 50 Tiamat Omega" to "Lv50 ティアマト・マグナ",
		"Lvl 100 Tiamat Omega Ayr" to "Lv100 ティアマト・マグナ＝エア",
		"Lvl 70 Colossus Omega" to "Lv70 コロッサス・マグナ",
		"Lvl 100 Colossus Omega" to "Lv100 コロッサス・マグナ",
		"Lvl 60 Leviathan Omega" to "Lv60 リヴァイアサン・マグナ",
		"Lvl 100 Leviathan Omega" to "Lv100 リヴァイアサン・マグナ",
		"Lvl 60 Yggdrasil Omega" to "Lv60 ユグドラシル・マグナ",
		"Lvl 100 Yggdrasil Omega" to "Lv100 ユグドラシル・マグナ",
		"Lvl 75 Luminiera Omega" to "Lv75 シュヴァリエ・マグナ",
		"Lvl 100 Luminiera Omega" to "Lv100 シュヴァリエ・マグナ",
		"Lvl 75 Celeste Omega" to "Lv75 セレスト・マグナ",
		"Lvl 100 Celeste Omega" to "Lv100 セレスト・マグナ",
		
		"Lvl 100 Twin Elements" to "Lv100 フラム＝グラス",
		"Lvl 120 Twin Elements" to "Lv120 フラム＝グラス",
		"Lvl 100 Macula Marius" to "Lv100 マキュラ・マリウス",
		"Lvl 120 Macula Marius" to "Lv120 マキュラ・マリウス",
		"Lvl 100 Medusa" to "Lv100 メドゥーサ",
		"Lvl 120 Medusa" to "Lv120 メドゥーサ",
		"Lvl 100 Nezha" to "Lv100 ナタク",
		"Lvl 120 Nezha" to "Lv120 ナタク",
		"Lvl 100 Apollo" to "Lv100 アポロン",
		"Lvl 120 Apollo" to "Lv120 アポロン",
		"Lvl 100 Dark Angel Olivia" to "Lv100 Dエンジェル・オリヴィエ",
		"Lvl 120 Dark Angel Olivia" to "Lv120 Dエンジェル・オリヴィエ",
		
		"Lvl 100 Athena" to "Lv100 アテナ",
		"Lvl 100 Grani" to "Lv100 グラニ",
		"Lvl 100 Baal" to "Lv100 バアル",
		"Lvl 100 Garuda" to "Lv100 ガルーダ",
		"Lvl 100 Odin" to "Lv100 オーディン",
		"Lvl 100 Lich" to "Lv100 リッチ",
		
		"Lvl 100 Michael" to "Lv100 ミカエル",
		"Lvl 100 Gabriel" to "Lv100 ガブリエル",
		"Lvl 100 Uriel" to "Lv100 ウリエル",
		"Lvl 100 Raphael" to "Lv100 ラファエル",
		"The Four Primarchs" to "四大天司ＨＬ",
		
		"Lvl 100 Proto Bahamut" to "Lv100 プロトバハムート",
		"Lvl 150 Proto Bahamut" to "Lv150 プロトバハムート",
		"Lvl 150 Ultimate Bahamut" to "Lv150 アルティメットバハムート",
		"Lvl 200 Ultimate Bahamut" to "Lv200 アルティメットバハムート",
		"Lvl 100 Grand Order" to "Lv100 ジ・オーダー・グランデ",
		"Lvl 200 Grand Order" to "Lv200 ジ・オーダー・グランデ",
		
		"Lvl 60 Zhuque" to "Lv60 朱雀",
		"Lvl 90 Agni" to "Lv90 アグニス",
		"Lvl 60 Xuanwu" to "Lv60 玄武",
		"Lvl 90 Neptune" to "Lv90 ネプチューン",
		"Lvl 60 Baihu" to "Lv60 白虎",
		"Lvl 90 Titan" to "Lv90 ティターン",
		"Lvl 60 Qinglong" to "Lv60 青竜",
		"Lvl 90 Zephyrus" to "Lv90 ゼピュロス",
		"Lvl 100 Huanglong" to "Lv100 黄龍",
		"Lvl 100 Qilin" to "Lv100 黒麒麟",
		"Huanglong & Qilin (Impossible)" to "黄龍・黒麒麟HL",
		"Lvl 100 Shenxian" to "Lv100 四象瑞神",
		
		"Lvl 110 Rose Queen" to "Lv110 ローズクイーン",
		"Lvl 120 Shiva" to "Lv120 シヴァ",
		"Lvl 120 Europa" to "Lv120 エウロペ",
		"Lvl 120 Godsworn Alexiel" to "Lv120 ゴッドガード・ブローディア",
		"Lvl 120 Grimnir" to "Lv120 グリームニル",
		"Lvl 120 Metatron" to "Lv120 メタトロン",
		"Lvl 120 Avatar" to "Lv120 アバター",
		"Lvl 120 Prometheus" to "Lv120 プロメテウス",
		"Lvl 120 Ca Ong" to "Lv120 カー・オン",
		"Lvl 120 Gilgamesh" to "Lv120 ギルガメッシュ",
		"Lvl 120 Morrigna" to "Lv120 バイヴカハ",
		"Lvl 120 Hector" to "Lv120 ヘクトル",
		"Lvl 120 Anubis" to "Lv120 アヌビス",
		"Lvl 200 Akasha" to "Lv200 アーカーシャ",
		"Lvl 150 Lucilius" to "Lv150 ルシファー",
		"Lvl 250 Lucilius" to "Lv250 ルシファー",
		
		"Lvl 150 Tiamat Malice" to "Lv150 ティアマト・マリス",
		"Lvl 150 Leviathan Malice" to "Lv150 リヴァイアサン・マリス",
		"Lvl 150 Phronesis" to "Lv150 フロネシス",
		"Lvl 150 Luminiera Malice" to "Lv150 シュヴァリエ・マリス",
		
		"Lvl 200 Wilnas" to "Lv200 ウィルナス",
		"Lvl 200 Wamdus" to "Lv200 ワムデュス",
		"Lvl 200 Galleon" to "Lv200 ガレヲン",
		"Lvl 200 Ewiyar" to "Lv200 イーウィヤ",
		"Lvl 200 Lu Woh" to "Lv200 ル・オー",
		"Lvl 200 Fediel" to "Lv200 フェディエル",
		"Lvl 250 Beelzebub" to "Lv250 ベルゼバブ",
		
		"Lvl 100 Xeno Ifrit" to "Lv100 ゼノ・イフリート",
		"Lvl 100 Xeno Cocytus" to "Lv100 ゼノ・コキュートス",
		"Lvl 100 Xeno Vohu Manah" to "Lv100 ゼノ・ウォフマナフ",
		"Lvl 100 Xeno Sagittarius" to "Lv100 ゼノ・サジタリウス",
		"Lvl 100 Xeno Corow" to "Lv100 ゼノ・コロゥ",
		"Lvl 100 Xeno Diablo" to "Lv100 ゼノ・ディアボロス"
	)
	
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
		// Construct the query for EN tweets.
		val queryEN = Query("+(:Battle ID) AND +($raidName)")
		queryEN.count = count / 2
		
		// Grab the Japanese name of the Raid and construct the query for JP tweets.
		val jpRaidName = listOfRaids[raidName]!!
		val queryJP = Query("+(:参戦ID) AND +($jpRaidName)")
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