package com.steve1316.granblue_automation_android.utils

import android.os.StrictMode
import com.steve1316.granblue_automation_android.MainActivity
import com.steve1316.granblue_automation_android.bot.Game
import twitter4j.Twitter
import twitter4j.v1.Query
import twitter4j.v1.Status
import twitter4j.v1.TwitterV1

/**
 * Provides the functions needed to perform Twitter API-related tasks such as searching tweets for room codes.
 */
class TwitterRoomFinder(private val game: Game, private val test: Boolean = false) {
	private val alreadyVisitedRoomCodes: ArrayList<String> = arrayListOf()

	private val listOfRaids = mapOf(
		// Omega Raids
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

		// Tier 1 Summon Raids
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

		// Tier 2 Summon Raids
		"Lvl 100 Athena" to "Lv100 アテナ",
		"Lvl 100 Grani" to "Lv100 グラニ",
		"Lvl 100 Baal" to "Lv100 バアル",
		"Lvl 100 Garuda" to "Lv100 ガルーダ",
		"Lvl 100 Odin" to "Lv100 オーディン",
		"Lvl 100 Lich" to "Lv100 リッチ",

		// Primarch Raids
		"Lvl 100 Michael" to "Lv100 ミカエル",
		"Lvl 100 Gabriel" to "Lv100 ガブリエル",
		"Lvl 100 Uriel" to "Lv100 ウリエル",
		"Lvl 100 Raphael" to "Lv100 ラファエル",
		"The Four Primarchs" to "四大天司ＨＬ",

		// Nightmare Raids
		"Lvl 100 Proto Bahamut" to "Lv100 プロトバハムート",
		"Lvl 100 Grand Order" to "Lv100 ジ・オーダー・グランデ",

		// Rise of the Beasts Raids
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

		// Impossible Raids
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
		"Lvl 150 Proto Bahamut" to "Lv150 プロトバハムート",
		"Lvl 150 Ultimate Bahamut" to "Lv150 アルティメットバハムート",
		"Lvl 200 Ultimate Bahamut" to "Lv200 アルティメットバハムート",
		"Lvl 200 Grand Order" to "Lv200 ジ・オーダー・グランデ",
		"Lvl 200 Akasha" to "Lv200 アーカーシャ",
		"Lvl 150 Lucilius" to "Lv150 ルシファー",
		"Lvl 250 Lucilius" to "Lv250 ルシファー",
		"Lvl 200 Lindwurm" to "Lv200 リンドヴルム",
		"Lvl 275 Diaspora" to "Lv275 ディアスポラ",

		// Malice Raids
		"Lvl 150 Tiamat Malice" to "Lv150 ティアマト・マリス",
		"Lvl 150 Leviathan Malice" to "Lv150 リヴァイアサン・マリス",
		"Lvl 150 Phronesis" to "Lv150 フロネシス",
		"Lvl 150 Luminiera Malice" to "Lv150 シュヴァリエ・マリス",
		"Lvl 150 Anima-Animus Core" to "Lv150 アニマ・アニムス・コア",

		// Six Dragon Raids
		"Lvl 200 Wilnas" to "Lv200 ウィルナス",
		"Lvl 200 Wamdus" to "Lv200 ワムデュス",
		"Lvl 200 Galleon" to "Lv200 ガレヲン",
		"Lvl 200 Ewiyar" to "Lv200 イーウィヤ",
		"Lvl 200 Lu Woh" to "Lv200 ル・オー",
		"Lvl 200 Fediel" to "Lv200 フェディエル",
		"Lvl 250 Beelzebub" to "Lv250 ベルゼバブ",

		// Xeno Clash Raids
		"Lvl 100 Xeno Ifrit" to "Lv100 ゼノ・イフリート",
		"Lvl 100 Xeno Cocytus" to "Lv100 ゼノ・コキュートス",
		"Lvl 100 Xeno Vohu Manah" to "Lv100 ゼノ・ウォフマナフ",
		"Lvl 100 Xeno Sagittarius" to "Lv100 ゼノ・サジタリウス",
		"Lvl 100 Xeno Corow" to "Lv100 ゼノ・コロゥ",
		"Lvl 100 Xeno Diablo" to "Lv100 ゼノ・ディアボロス",

		// Ennead Raids
		"Lvl 120 Osiris" to "Lv120 オシリス",
		"Lvl 120 Horus" to "Lv120 ホルス",
		"Lvl 120 Horus" to "Lv120 ホルス",
		"Lvl 120 Bennu" to "Lv120 ベンヌ",
		"Lvl 120 Atum" to "Lv120 アトゥム",
		"Lvl 120 Tefnut" to "Lv120 テフヌト",
		"Lvl 120 Ra" to "Lv120 ラー",
	)

	// For Twitter API v1.1
	private lateinit var oldTwitterClient: TwitterV1
	private val oldTwitterClientTweets: ArrayList<Status> = arrayListOf()

	private val twitterClientIDs: ArrayList<String> = arrayListOf()

	companion object {
		private const val tag: String = "${MainActivity.loggerTag}TwitterRoomFinder"
	}

	/**
	 * Connect to Twitter API V1.1
	 *
	 */
	fun connect() {
		if (!test) {
			game.printToLog("\n[TWITTER] Authenticating provided consumer keys and access tokens with the Twitter API V1.1...", tag)
			val result = testConnection()
			if (result == "Test successfully completed.") {
				game.printToLog("[TWITTER] Successfully connected to the Twitter API V1.1.", tag)
				game.printToLog("\n[TWITTER] Now ready for manual searching of tweets for ${game.configData.missionName}.", tag)
			} else {
				throw Exception(result)
			}
		}
	}

	/**
	 * Test connection to the API using the consumer keys/tokens for V1.1.
	 *
	 * @return Either a success message or an error message depending on the connection to the API.
	 */
	fun testConnection(): String {
		// Allow Network IO to be run on the main thread without throwing the NetworkOnMainThreadException.
		val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
		StrictMode.setThreadPolicy(policy)

		val size = try {
			// Create the Twitter client object to use the Twitter API V1.1.
			oldTwitterClient = Twitter.newBuilder().apply {
				oAuthConsumer(game.configData.twitterAPIKey, game.configData.twitterAPIKeySecret)
				oAuthAccessToken(game.configData.twitterAccessToken, game.configData.twitterAccessTokenSecret)
			}.build().v1()

			val queryResult = oldTwitterClient.search().search(Query.of("Hello World"))
			queryResult.count
		} catch (e: Exception) {
			game.printToLog("[ERROR] Cannot connect to Twitter API v1.1 due to keys and access tokens being incorrect.", tag, isError = true)
			return "[ERROR] Cannot connect to Twitter API v1.1 due to keys and access tokens being incorrect."
		}

		return if (size > 0) {
			"Test successfully completed."
		} else {
			"[ERROR] Connection was successful but test search came up empty."
		}
	}

	/**
	 * Start collected tweets containing room codes from EN and JP players. For use with Twitter API v1.1 only.
	 *
	 * @param count Number of IDs to be found.
	 */
	private fun findMostRecent(count: Int = 10) {
		game.printToLog("\n[TWITTER] Now finding the $count most recent tweets for ${game.configData.missionName} using Twitter API v1.1.", tag)

		val queryEN = "+(:Battle ID) AND +(${game.configData.missionName})"
		val queryJP = "+(:参戦ID) AND +(${listOfRaids[game.configData.missionName]})"

		// Search JP tweets first and filter for tweets that the bot has not processed yet.
		val tweetListJP = oldTwitterClient.search().search(Query.of(queryJP)).tweets

		tweetListJP.forEach { tweet ->
			if (!twitterClientIDs.contains(tweet.id.toString()) && oldTwitterClientTweets.size < count) {
				oldTwitterClientTweets.add(tweet)
				twitterClientIDs.add(tweet.id.toString())
			}
		}

		// Search EN tweets only if the filtered JP tweets was less than the desired amount.
		if (oldTwitterClientTweets.size < count) {
			val tweetListEN = oldTwitterClient.search().search(Query.of(queryEN)).tweets

			tweetListEN.forEach { tweet ->
				if (!twitterClientIDs.contains(tweet.id.toString()) && oldTwitterClientTweets.size < count) {
					oldTwitterClientTweets.add(tweet)
					twitterClientIDs.add(tweet.id.toString())
				}
			}
		}
	}

	/**
	 * Clean the tweets and parse out the room codes from them.
	 *
	 * @return A single room code that has not been visited.
	 */
	fun getRoomCode(): String {
		findMostRecent()

		if (oldTwitterClientTweets.size == 0) {
			game.printToLog("[TWITTER] There are no recent or detected tweets available for the given raid.", tag)
			return ""
		}

		game.printToLog("[TWITTER] Now cleaning up the tweets and parsing for room codes...", tag)

		while (oldTwitterClientTweets.size > 0) {
			val tweetText: String = oldTwitterClientTweets.removeFirst().text

			// Split up the text by whitespaces.
			val splitText = tweetText.split(" ")
			var index = 0

			// For each split element, parse the Room Code and save it.
			if (tweetText.contains(game.configData.missionName) || tweetText.contains(listOfRaids[game.configData.missionName]!!)) {
				splitText.forEach { text ->
					if (text.contains(":Battle") || text.contains(":参戦ID")) {
						val roomCode = splitText[index - 1]

						if (!alreadyVisitedRoomCodes.contains(roomCode)) {
							alreadyVisitedRoomCodes.add(roomCode)
							return roomCode
						} else {
							game.printToLog("[TWITTER] Already visited $roomCode before in this session. Skipping this code...", tag)
						}
					}

					index += 1
				}
			} else {
				game.printToLog("[TWITTER] Skipping tweet as it is for a different raid.", tag)
			}
		}

		return ""
	}
}