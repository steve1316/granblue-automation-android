package com.steve1316.granblue_automation_android.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.StrictMode
import android.util.Log
import androidx.preference.PreferenceManager
import com.steve1316.granblue_automation_android.MainActivity
import com.steve1316.granblue_automation_android.bot.Game
import twitter4j.*
import twitter4j.conf.ConfigurationBuilder

/**
 * Listener class for the Streaming API.
 */
class MyListener(private val game: Game) : StatusListener {
	private val tag: String = "${MainActivity.loggerTag}MyListener"

	val tweets: ArrayDeque<Status> = ArrayDeque()

	override fun onStatus(status: Status?) {
		if (status != null) {
			if (game.configData.debugMode) {
				Log.d(tag, "[DEBUG] Stream found: ${status.text}")
			}

			tweets.addFirst(status)
		}
	}

	override fun onException(ex: java.lang.Exception?) {
		return
	}

	override fun onDeletionNotice(statusDeletionNotice: StatusDeletionNotice?) {
		return
	}

	override fun onTrackLimitationNotice(numberOfLimitedStatuses: Int) {
		return
	}

	override fun onScrubGeo(userId: Long, upToStatusId: Long) {
		return
	}

	override fun onStallWarning(warning: StallWarning?) {
		return
	}
}

/**
 * Provides the functions needed to perform Twitter API-related tasks such as searching tweets for room codes.
 */
class TwitterRoomFinder(myContext: Context, private val game: Game) {
	private lateinit var twitter: Twitter
	private val listener = MyListener(game)

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
		"Lvl 120 Osiris" to "Lv120 オシリス"
	)

	companion object {
		private const val tag: String = "${MainActivity.loggerTag}TwitterRoomFinder"
		private var twitterStream: TwitterStream? = null

		fun disconnect() {
			twitterStream?.shutdown()
			Log.d(tag, "[TWITTER] Stream API disconnected.")
		}
	}

	init {
		if (game.configData.farmingMode == "Raid") {
			game.printToLog("\n[INFO] Connecting to Twitter API...", tag = tag)

			// Allow Network IO to be run on the main thread without throwing the NetworkOnMainThreadException.
			val policy: StrictMode.ThreadPolicy = StrictMode.ThreadPolicy.Builder().permitAll().build()
			StrictMode.setThreadPolicy(policy)

			game.printToLog("[INFO] Main thread will now allow Network IO to be run on it without throwing NetworkOnMainThreadException.", tag = tag)

			// Initialize the Twitter object.
			val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(myContext)
			val configurationBuilder: ConfigurationBuilder = ConfigurationBuilder()
				.setOAuthConsumerKey(sharedPreferences.getString("apiKey", ""))
				.setOAuthConsumerSecret(sharedPreferences.getString("apiKeySecret", ""))
				.setOAuthAccessToken(sharedPreferences.getString("accessToken", ""))
				.setOAuthAccessTokenSecret(sharedPreferences.getString("accessTokenSecret", ""))

			val configurationStreamBuilder: ConfigurationBuilder = ConfigurationBuilder()
				.setOAuthConsumerKey(sharedPreferences.getString("apiKey", ""))
				.setOAuthConsumerSecret(sharedPreferences.getString("apiKeySecret", ""))
				.setOAuthAccessToken(sharedPreferences.getString("accessToken", ""))
				.setOAuthAccessTokenSecret(sharedPreferences.getString("accessTokenSecret", ""))

			try {
				// Create the listener and stream objects.
				twitterStream = TwitterStreamFactory(configurationStreamBuilder.build()).instance
				twitterStream?.addListener(listener)

				// Test connection by fetching user's timeline.
				twitter = TwitterFactory(configurationBuilder.build()).instance
				twitter.timelines().homeTimeline

				game.printToLog("[SUCCESS] Connection to Twitter API is successful.", tag = tag)

				findMostRecent(game.configData.missionName)
			} catch (e: Exception) {
				game.printToLog("[ERROR] Failed to connect to Twitter API: ${e.stackTraceToString()}", tag = tag, isError = true)
			}
		}
	}

	/**
	 * Start collected tweets containing room codes from EN and JP players.
	 *
	 * @param raidName Name and level of the Raid that appears in tweets containing the room code to it.
	 */
	private fun findMostRecent(raidName: String) {
		// Grab the Japanese name of the Raid.
		val jpRaidName = listOfRaids[raidName]!!

		// Construct the combined query for both EN and JP tweets.
		val queryCombined = FilterQuery("$raidName,$jpRaidName")

		// Start listening to the Stream API.
		twitterStream?.filter(queryCombined)
	}

	/**
	 * Clean the tweets and parse out the room codes from them.
	 *
	 * @return A single room code that has not been visited.
	 */
	fun getRoomCode(): String {
		if (listener.tweets.isEmpty()) {
			game.printToLog("[TWITTER] There are no recent or detected tweets available for the given raid.", tag = tag)
			return ""
		}

		game.printToLog("[TWITTER] Now cleaning up the tweets and parsing for room codes...", tag = tag)

		while (listener.tweets.isNotEmpty()) {
			val tweet = listener.tweets.removeFirst()

			// Split up the text by whitespaces.
			val splitText = tweet.text.split(" ")
			var index = 0

			// For each split element, parse the Room Code and save it.
			splitText.forEach { text ->
				if (text.contains(":Battle") || text.contains(":参戦ID")) {
					val roomCode = splitText[index - 1]

					if (!alreadyVisitedRoomCodes.contains(roomCode)) {
						alreadyVisitedRoomCodes.add(roomCode)
						return roomCode
					} else {
						game.printToLog("[TWITTER] Already visited $roomCode before in this session. Skipping this code...", tag = tag)
					}
				}

				index += 1
			}
		}

		return ""
	}
}