package com.steve1316.granblueautomation_android.data

/**
 * This is structured in such a way that Hoplite can parse the config.yaml file and create the following data classes from it.
 */
data class ConfigData(
	val twitter: TwitterData,
	val refill: RefillData,
	val dimensionalHalo: DimensionalHaloData,
	val event: EventNightmareData,
	val rotb: RiseOfTheBeastsData,
	val dreadBarrage: DreadBarrageData
)

/**
 * Consumer keys and access tokens required to use the Twitter API.
 */
data class TwitterData(val apiKey: String, val apiKeySecret: String, val accessToken: String, val accessTokenSecret: String)

/**
 * Determine whether or not the user wants to use Full Elixir/Soul Balm to refill.
 */
data class RefillData(val fullElixir: Boolean = false, val soulBalm: Boolean = false)

/**
 * Settings needed to do Dimensional Halo.
 */
data class DimensionalHaloData(
	val enableDimensionalHalo: Boolean = false,
	val dimensionalHaloCombatScript: String = "",
	val dimensionalHaloSummonList: List<String> = listOf(),
	val dimensionalHaloGroupNumber: Int = 0,
	val dimensionalHaloPartyNumber: Int = 0
)

/**
 * Settings needed to do Event Nightmare.
 */
data class EventNightmareData(
	val enableEventNightmare: Boolean = false,
	val eventNightmareCombatScript: String = "",
	val eventNightmareSummonList: List<String> = listOf(),
	val eventNightmareGroupNumber: Int = 0,
	val eventNightmarePartyNumber: Int = 0
)

/**
 * Settings needed to do Rise of the Beasts Extreme+.
 */
data class RiseOfTheBeastsData(
	val enableROTBExtremePlus: Boolean = false,
	val rotbExtremePlusCombatScript: String = "",
	val rotbExtremePlusSummonList: List<String> = listOf(),
	val rotbExtremePlusGroupNumber: Int = 0,
	val rotbExtremePlusPartyNumber: Int = 0
)

/**
 * Settings needed to do Dread Barrage Unparalleled Foes.
 */
data class DreadBarrageData(
	val enableUnparalleledFoe: Boolean = false,
	val enableUnparalleledFoeLevel95: Boolean = false,
	val enableUnparalleledFoeLevel175: Boolean = false,
	val unparalleledFoeCombatScript: String = "",
	val unparalleledFoeSummonList: List<String> = listOf(),
	val unparalleledFoeGroupNumber: Int = 0,
	val unparalleledFoePartyNumber: Int = 0
)