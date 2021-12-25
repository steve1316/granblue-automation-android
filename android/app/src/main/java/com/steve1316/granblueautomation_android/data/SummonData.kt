package com.steve1316.granblueautomation_android.data

/**
 * This class simply holds the selectable Summons that are supported by this application.
 */
class SummonData {
	companion object {
		val fireSummons: ArrayList<String> = arrayListOf("Colossus Omega", "Shiva", "Agni")
		val waterSummons: ArrayList<String> = arrayListOf("Leviathan Omega", "Europa", "Varuna", "Bonito")
		val earthSummons: ArrayList<String> = arrayListOf("Yggdrasil Omega", "Godsworn Alexiel", "Titan")
		val windSummons: ArrayList<String> = arrayListOf("Tiamat Omega", "Grimnir", "Zephyrus")
		val lightSummons: ArrayList<String> = arrayListOf("Luminiera Omega", "Lucifer", "Zeus")
		val darkSummons: ArrayList<String> = arrayListOf("Celeste Omega", "Bahamut", "Hades")
		val miscSummons: ArrayList<String> = arrayListOf("Huanglong", "Qilin", "Kaguya", "Nobiyo", "White Rabbit", "Black Rabbit")
	}
}