package com.steve1316.granblueautomation_android.data

class ConfigData {
	companion object {
		// Token and user ID for use with the Discord API.
		var discordToken: String = ""
		var userID: String = ""
		
		// Consumer keys and access tokens required to use the Twitter API.
		var apiKey: String = ""
		var apiKeySecret: String = ""
		var accessToken: String = ""
		var accessTokenSecret: String = ""
		
		// Determine whether or not the user wants to use Full Elixir/Soul Balm to refill.
		var fullElixir: Boolean = false
		var soulBalm: Boolean = false
		
		// Settings needed to do Dimensional Halo.
		var enableDimensionalHalo: Boolean = false
		var dimensionalHaloSummonList: List<String> = listOf()
		var dimensionalHaloGroupNumber: Int = 0
		var dimensionalHaloPartyNumber: Int = 0
		
		// Settings needed to do Event Nightmare.
		var enableEventNightmare: Boolean = false
		var eventNightmareSummonList: List<String> = listOf()
		var eventNightmareGroupNumber: Int = 0
		var eventNightmarePartyNumber: Int = 0
		
		// Settings needed to do Rise of the Beasts Extreme+.
		var enableROTBExtremePlus: Boolean = false
		var rotbExtremePlusSummonList: List<String> = listOf()
		var rotbExtremePlusGroupNumber: Int = 0
		var rotbExtremePlusPartyNumber: Int = 0
		
		// Settings needed to do Xeno Clash Nightmare.
		var enableXenoClashNightmare: Boolean = false
		var xenoClashNightmareSummonList: List<String> = listOf()
		var xenoClashNightmareGroupNumber: Int = 0
		var xenoClashNightmarePartyNumber: Int = 0
	}
}