package com.steve1316.granblueautomation_android.data

/**
 * This class simply holds the values of the items that are supported by this application.
 */
class ItemData {
	companion object {
		val itemsForQuest = mapOf(
			"Scattered Cargo" to arrayListOf("Satin Feather", "Zephyr Feather", "Flying Sprout"),
			"Lucky Charm Hunt" to arrayListOf("Fine Sand Bottle", "Untamed Flame", "Blistering Ore"),
			"Special Op's Request" to arrayListOf("Fine Sand Bottle", "Untamed Flame", "Blistering Ore"),
			"Threat to the Fisheries" to arrayListOf("Fresh Water Jug", "Soothing Splash", "Glowing Coral"),
			"The Fruit of Lumacie" to arrayListOf("Rough Stone", "Swirling Amber", "Coarse Alluvium"),
			"Whiff of Danger" to arrayListOf("Rough Stone", "Swirling Amber", "Coarse Alluvium"),
			"I Challenge You!" to arrayListOf("Falcon Feather", "Spring Water Jug", "Vermilion Stone"),
			"For Whom the Bell Tolls" to arrayListOf("Slimy Shroom", "Hollow Soul", "Lacrimosa"),
			"Golonzo's Battles of Old" to arrayListOf("Wheat Stalk", "Iron Cluster", "Olea Plant"),
			"The Dungeon Diet" to arrayListOf("Indigo Fruit", "Foreboding Clover", "Blood Amber"),
			"Trust Busting Dustup" to arrayListOf("Sand Brick", "Native Reed"),
			"Erste Kingdom Episode 4" to arrayListOf("Antique Cloth"),
			"Imperial Wanderer's Soul" to arrayListOf("Prosperity Flame", "Explosive Material", "Steel Liquid")
		)
		
		val itemsForSpecial = mapOf(
			"Scarlet Trial" to arrayListOf(
				"Fire Orb", "Water Orb", "Earth Orb", "Wind Orb", "Light Orb", "Dark Orb", "Inferno Orb", "Frost Orb",
				"Rumbling Orb", "Cyclone Orb", "Shining Orb", "Abysm Orb"
			),
			"Cerulean Trial" to arrayListOf(
				"Red Tome", "Blue Tome", "Brown Tome", "Green Tome", "White Tome", "Black Tome", "Hellfire Scroll",
				"Flood Scroll", "Thunder Scroll", "Gale Scroll", "Skylight Scroll", "Chasm Scroll", "Infernal Whorl", "Tidal Whorl", "Seismic Whorl",
				"Tempest Whorl", "Radiant Whorl", "Umbral Whorl"
			),
			"Violet Trial" to arrayListOf("Prism Chip", "Flawed Prism", "Flawless Prism", "Rainbow Prism"),
			"Shiny Slime Search!" to arrayListOf("EXP"),
			"Six Dragon Trial" to arrayListOf(
				"Red Dragon Scale", "Blue Dragon Scale", "Brown Dragon Scale", "Green Dragon Scale", "White Dragon Scale",
				"Black Dragon Scale"
			),
			"Ifrit Showdown" to arrayListOf("Jasper Scale", "Scorching Peak", "Infernal Garnet", "Ifrit Anima", "Ifrit Omega Anima"),
			"Cocytus Showdown" to arrayListOf("Mourning Stone", "Crystal Spirit", "Frozen Hell Prism", "Cocytus Anima", "Cocytus Omega Anima"),
			"Vohu Manah Showdown" to arrayListOf(
				"Scrutiny Stone", "Luminous Judgment", "Evil Judge Crystal", "Vohu Manah Anima",
				"Vohu Manah Omega Anima"
			),
			"Sagittarius Showdown" to arrayListOf(
				"Sagittarius Arrowhead", "Sagittarius Rune", "Horseman's Plate", "Sagittarius Anima",
				"Sagittarius Omega Anima"
			),
			"Corow Showdown" to arrayListOf("Solar Ring", "Sunlight Quartz", "Halo Light Quartz", "Corow Anima", "Corow Omega Anima"),
			"Diablo Showdown" to arrayListOf("Twilight Cloth Strip", "Shadow Silver", "Phantom Demon Jewel", "Diablo Anima", "Diablo Omega Anima"),
			"The Hellfire Trial" to arrayListOf("Hellfire Fragment"),
			"The Deluge Trial" to arrayListOf("Deluge Fragment"),
			"The Wasteland Trial" to arrayListOf("Wasteland Fragment"),
			"The Typhoon Trial" to arrayListOf("Typhoon Fragment"),
			"Angel Halo" to arrayListOf("Angel Halo Weapons"),
			"Campaign-Exclusive Quest" to arrayListOf("Repeated Runs")
		)
		
		val itemsForCoop = mapOf(
			"H3-1 In a Dusk Dream" to arrayListOf("EXP"),
			"EX1-1 Corridor of Puzzles" to arrayListOf("Warrior Creed", "Mage Creed"),
			"EX1-3 Lost in the Dark" to arrayListOf("Warrior Creed", "Mage Creed"),
			"EX2-2 Time of Judgement" to arrayListOf(
				"Keraunos Replica", "Faust Replica", "Mage Distinction", "Pilgrim Distinction", "Alchemist Distinction", "Monk's Distinction",
				"Evil Judge Crystal"
			),
			"EX2-3 Time of Revelation" to arrayListOf(
				"Avenger Replica", "Hellion Gauntlet Replica", "Gladiator Distinction", "Fencer Distinction", "Dual Wielder Distinction",
				"Forester's Distinction", "Infernal Garnet"
			),
			"EX2-4 Time of Eminence" to arrayListOf(
				"Nirvana Replica", "Romulus Spear Replica", "Murakumo Replica", "Bandit Distinction", "Troubadour Distinction", "Mystic Distinction",
				"Shredder Distinction", "Contractor Distinction", "Halo Light Quartz"
			),
			"EX3-2 Rule of the Tundra" to arrayListOf(
				"Frozen Hell Prism", "Guardian Distinction", "Combatant Distinction", "Sword Master Distinction",
				"Dragoon's Distinction", "Skofnung Replica", "Langeleik Replica", "Kapilavastu Replica"
			),
			"EX3-3 Rule of the Plains" to arrayListOf(
				"Oliver Replica", "Rosenbogen Replica", "Sharpshooter Distinction", "Gunslinger Distinction", "Cavalryman Distinction",
				"Longstrider's Distinction", "Horseman's Plate"
			),
			"EX3-4 Rule of the Twilight" to arrayListOf(
				"Ipetam Replica", "Muramasa Replica", "Proximo Replica", "Samurai Distinction", "Assassin Distinction", "Ninja Distinction",
				"Dancer Distinction", "Phantom Demon Jewel"
			),
			"EX4-2 Amidst the Waves" to arrayListOf(
				"Oliver Replica",
				"Romulus Spear Replica",
				"Kapilavastu Replica",
				"Mystic Distinction",
				"Pilgrim Distinction",
				"Alchemist Distinction",
				"Mage Distinction",
				"Monk's Distinction"
			),
			"EX4-3 Amidst the Petals" to arrayListOf(
				"Langeleik Replica", "Faust Replica", "Misericorde Replica", "Arabesque Replica", "Ninja Distinction", "Samurai Distinction",
				"Sharpshooter Distinction", "Gunslinger Distinction", "Longstrider's Distinction", "Dancer Distinction"
			),
			"EX4-4 Amidst Severe Cliffs" to arrayListOf(
				"Hellion Gauntlet Replica",
				"Muramasa Replica",
				"Aschallon Replica",
				"Practice Drum",
				"Combatant Distinction",
				"Gladiator Distinction",
				"Sword Master Distinction",
				"Fencer Distinction"
			),
			"EX4-5 Amidst the Flames" to arrayListOf(
				"Ipetam Replica", "Murakumo Replica", "Nebuchad Replica", "Proximo Replica", "Guardian Distinction", "Cavalryman Distinction",
				"Bandit Distinction", "Troubadour Distinction", "Dragoon's Distinction", "Contractor Distinction"
			)
		)
		
		val itemsForRaid = mapOf(
			"Lvl 50 Tiamat Omega" to arrayListOf(
				"Tiamat Omega", "Tiamat Anima", "Tiamat Omega Anima", "Tiamat Amood Omega", "Tiamat Bolt Omega",
				"Tiamat Gauntlet Omega", "Tiamat Glaive Omega"
			),
			"Lvl 100 Tiamat Omega Ayr" to arrayListOf(
				"Tiamat Omega", "Tiamat Anima", "Tiamat Omega Anima", "Tiamat Amood Omega", "Tiamat Bolt Omega",
				"Tiamat Gauntlet Omega", "Tiamat Glaive Omega"
			),
			"Lvl 70 Colossus Omega" to arrayListOf(
				"Colossus Omega", "Colossus Anima", "Colossus Omega Anima", "Colossus Blade Omega", "Colossus Cane Omega",
				"Colossus Carbine Omega", "Colossus Fist Omega"
			),
			"Lvl 100 Colossus Omega" to arrayListOf(
				"Colossus Omega", "Colossus Anima", "Colossus Omega Anima", "Colossus Blade Omega", "Colossus Cane Omega",
				"Colossus Carbine Omega", "Colossus Fist Omega"
			),
			"Lvl 60 Leviathan Omega" to arrayListOf(
				"Leviathan Omega", "Leviathan Anima", "Leviathan Omega Anima", "Leviathan Bow Omega", "Leviathan Gaze Omega",
				"Leviathan Scepter Omega", "Leviathan Spear Omega"
			),
			"Lvl 100 Leviathan Omega" to arrayListOf(
				"Leviathan Omega", "Leviathan Anima", "Leviathan Omega Anima", "Leviathan Bow Omega", "Leviathan Gaze Omega",
				"Leviathan Scepter Omega", "Leviathan Spear Omega"
			),
			"Lvl 60 Yggdrasil Omega" to arrayListOf(
				"Yggdrasil Omega", "Yggdrasil Anima", "Yggdrasil Omega Anima", "Yggdrasil Bow Omega",
				"Yggdrasil Crystal Blade Omega", "Yggdrasil Dagger Omega", "Yggdrasil Dewbranch Omega"
			),
			"Lvl 100 Yggdrasil Omega" to arrayListOf(
				"Yggdrasil Omega", "Yggdrasil Anima", "Yggdrasil Omega Anima", "Yggdrasil Bow Omega",
				"Yggdrasil Crystal Blade Omega", "Yggdrasil Dagger Omega", "Yggdrasil Dewbranch Omega"
			),
			"Lvl 75 Luminiera Omega" to arrayListOf(
				"Luminiera Omega", "Luminiera Anima", "Luminiera Omega Anima", "Luminiera Bhuj Omega", "Luminiera Bolt Omega",
				"Luminiera Harp Omega", "Luminiera Sword Omega"
			),
			"Lvl 100 Luminiera Omega" to arrayListOf(
				"Luminiera Omega", "Luminiera Anima", "Luminiera Omega Anima", "Luminiera Bhuj Omega", "Luminiera Bolt Omega",
				"Luminiera Harp Omega", "Luminiera Sword Omega"
			),
			"Lvl 75 Celeste Omega" to arrayListOf(
				"Celeste Omega", "Celeste Anima", "Celeste Omega Anima", "Celeste Harp Omega", "Celeste Claw Omega",
				"Celeste Horn Omega", "Celeste Zaghnal Omega"
			),
			"Lvl 100 Celeste Omega" to arrayListOf(
				"Celeste Omega", "Celeste Anima", "Celeste Omega Anima", "Celeste Harp Omega", "Celeste Claw Omega",
				"Celeste Horn Omega", "Celeste Zaghnal Omega"
			),
			
			"Lvl 120 Shiva" to arrayListOf(
				"Shiva Anima",
				"Shiva Omega Anima",
				"Hand of Brahman",
				"Scimitar of Brahman",
				"Trident of Brahman",
				"Nilakantha"
			),
			"Lvl 120 Europa" to arrayListOf("Europa Anima", "Europa Omega Anima", "Tyros Bow", "Tyros Scepter", "Tyros Zither", "Spirit of Mana"),
			"Lvl 120 Godsworn Alexiel" to arrayListOf(
				"Alexiel Anima", "Alexiel Omega Anima", "Nibelung Horn", "Nibelung Klinge", "Nibelung Messer",
				"Godsworn Edge"
			),
			"Lvl 120 Grimnir" to arrayListOf(
				"Grimnir Anima", "Grimnir Omega Anima", "Last Storm Blade", "Last Storm Harp", "Last Storm Lance",
				"Coruscant Crozier"
			),
			"Lvl 120 Metatron" to arrayListOf(
				"Metatron Anima", "Metatron Omega Anima", "Mittron's Treasured Blade", "Mittron's Gauntlet", "Mittron's Bow",
				"Pillar of Flame"
			),
			"Lvl 120 Avatar" to arrayListOf("Avatar Anima", "Avatar Omega Anima", "Abyss Striker", "Abyss Spine", "Abyss Gaze", "Zechariah"),
			
			"Lvl 100 Michael" to arrayListOf("Michael Anima"),
			"Lvl 100 Gabriel" to arrayListOf("Gabriel Anima"),
			"Lvl 100 Uriel" to arrayListOf("Uriel Anima"),
			"Lvl 100 Raphael" to arrayListOf("Raphael Anima"),
			"The Four Primarchs" to arrayListOf("Fire Halo", "Water Halo", "Earth Halo", "Wind Halo"),
			
			"Lvl 110 Rose Queen" to arrayListOf("Rose Petal"),
			"Lvl 100 Grand Order" to arrayListOf("Azure Feather", "Heavenly Horn"),
			"Lvl 200 Grand Order" to arrayListOf("Verdant Azurite"),
			"Lvl 100 Proto Bahamut" to arrayListOf("Champion Merit", "Horn of Bahamut"),
			"Lvl 150 Proto Bahamut" to arrayListOf("Primeval Horn"),
			"Lvl 150 Ultimate Bahamut" to arrayListOf(
				"Michael Anima", "Gabriel Anima", "Uriel Anima", "Raphael Anima", "Meteorite Fragment", "Meteorite", "Silver Centrum", "Ultima Unit",
				"Athena Anima", "Athena Omega Anima", "Grani Anima", "Grani Omega Anima", "Baal Anima", "Baal Omega Anima", "Garuda Anima", "Garuda Omega Anima", "Odin Anima", "Odin Omega Anima",
				"Lich Anima", "Lich Omega Anima"),
			"Lvl 200 Ultimate Bahamut" to arrayListOf("Silver Centrum", "Ultima Unit"),
			"Lvl 200 Akasha" to arrayListOf("Hollow Key"),
			"Lvl 150 Lucilius" to arrayListOf("Dark Residue", "Shadow Substance"),
			"Lvl 200 Lindwurm" to arrayListOf("Golden Scale", "Lineage Fragment"),
			"Huanglong & Qilin (Impossible)" to arrayListOf("Huanglong Anima", "Qilin Anima", "Golden Talisman", "Obsidian Talisman"),
			
			"Lvl 100 Twin Elements" to arrayListOf("Twin Elements Anima", "Twin Elements Omega Anima", "Ancient Ecke Sachs", "Ecke Sachs"),
			"Lvl 120 Twin Elements" to arrayListOf("Twin Elements Anima", "Twin Elements Omega Anima", "Ancient Ecke Sachs", "Ecke Sachs"),
			"Lvl 100 Macula Marius" to arrayListOf("Macula Marius Anima", "Macula Marius Omega Anima", "Ancient Auberon", "Auberon"),
			"Lvl 120 Macula Marius" to arrayListOf("Macula Marius Anima", "Macula Marius Omega Anima", "Ancient Auberon", "Auberon"),
			"Lvl 100 Medusa" to arrayListOf("Medusa Anima", "Medusa Omega Anima", "Ancient Perseus", "Perseus"),
			"Lvl 120 Medusa" to arrayListOf("Medusa Anima", "Medusa Omega Anima", "Ancient Perseus", "Perseus"),
			"Lvl 100 Nezha" to arrayListOf("Nezha Anima", "Nezha Omega Anima", "Ancient Nalakuvara", "Nalakuvara"),
			"Lvl 120 Nezha" to arrayListOf("Nezha Anima", "Nezha Omega Anima", "Ancient Nalakuvara", "Nalakuvara"),
			"Lvl 100 Apollo" to arrayListOf("Apollo Anima", "Apollo Omega Anima", "Ancient Bow of Artemis", "Bow of Artemis"),
			"Lvl 120 Apollo" to arrayListOf("Apollo Anima", "Apollo Omega Anima", "Ancient Bow of Artemis", "Bow of Artemis"),
			"Lvl 100 Dark Angel Olivia" to arrayListOf("Dark Angel Olivia Anima", "Dark Angel Olivia Omega Anima", "Ancient Cortana", "Cortana"),
			"Lvl 120 Dark Angel Olivia" to arrayListOf("Dark Angel Olivia Anima", "Dark Angel Olivia Omega Anima", "Ancient Cortana", "Cortana"),
			
			"Lvl 100 Athena" to arrayListOf("Athena Anima", "Athena Omega Anima", "Erichthonius", "Sword of Pallas"),
			"Lvl 100 Grani" to arrayListOf("Grani Anima", "Grani Omega Anima", "Bow of Sigurd", "Wilhelm"),
			"Lvl 100 Baal" to arrayListOf("Baal Anima", "Baal Omega Anima", "Solomon's Axe", "Spymur's Vision"),
			"Lvl 100 Garuda" to arrayListOf("Garuda Anima", "Garuda Omega Anima", "Plume of Suparna", "Indra's Edge"),
			"Lvl 100 Odin" to arrayListOf("Odin Anima", "Odin Omega Anima", "Gungnir", "Sleipnir Shoe"),
			"Lvl 100 Lich" to arrayListOf("Lich Anima", "Lich Omega Anima", "Obscuritas", "Phantasmas"),
			
			"Lvl 120 Prometheus" to arrayListOf("Prometheus Anima", "Fire of Prometheus", "Chains of Caucasus"),
			"Lvl 120 Ca Ong" to arrayListOf("Ca Ong Anima", "Keeper of Hallowed Ground", "Savior of Hallowed Ground"),
			"Lvl 120 Gilgamesh" to arrayListOf("Gilgamesh Anima", "All-Might Spear", "All-Might Battle-Axe"),
			"Lvl 120 Morrigna" to arrayListOf("Morrigna Anima", "Le Fay", "Unius"),
			"Lvl 120 Hector" to arrayListOf("Hector Anima", "Bow of Iliad", "Adamantine Gauntlet"),
			"Lvl 120 Anubis" to arrayListOf("Anubis Anima", "Hermanubis", "Scales of Dominion"),
			
			"Lvl 150 Tiamat Malice" to arrayListOf("Tiamat Malice Anima", "Hatsoiiłhał", "Majestas"),
			"Lvl 150 Leviathan Malice" to arrayListOf("Leviathan Malice Anima", "Kaladanda", "Kris of Hypnos"),
			"Lvl 150 Phronesis" to arrayListOf("Phronesis Anima", "Dark Thrasher", "Master Bamboo Sword"),
			"Lvl 150 Luminiera Malice" to arrayListOf("Luminiera Malice Anima", "Colomba", "Seyfert"),
			
			"Lvl 200 Wilnas" to arrayListOf("Wilnas's Finger"),
			"Lvl 200 Wamdus" to arrayListOf("Wamdus's Cnidocyte"),
			"Lvl 200 Galleon" to arrayListOf("Galleon's Jaw"),
			"Lvl 200 Ewiyar" to arrayListOf("Ewiyar's Beak"),
			"Lvl 200 Lu Woh" to arrayListOf("Lu Woh's Horn"),
			"Lvl 200 Fediel" to arrayListOf("Fediel's Spine"),
			
			"Lvl 100 Xeno Ifrit" to arrayListOf("True Xeno Ifrit Anima", "Infernal Vajra"),
			"Lvl 100 Xeno Cocytus" to arrayListOf("True Xeno Cocytus Anima", "Frozen Hellplume"),
			"Lvl 100 Xeno Vohu Manah" to arrayListOf("True Xeno Vohu Manah Anima", "Sacrosanct Sutra"),
			"Lvl 100 Xeno Sagittarius" to arrayListOf("True Xeno Sagittarius Anima", "Zodiac Arc"),
			"Lvl 100 Xeno Corow" to arrayListOf("True Xeno Corow Anima", "Flame Fanner"),
			"Lvl 100 Xeno Diablo" to arrayListOf("True Xeno Diablo Anima", "Wraithbind Fetter"),
			
			"Lvl 100 Shenxian" to arrayListOf("Shenxian Badge"),
			"Lvl 90 Agni" to arrayListOf("Zhuque Badge"),
			"Lvl 90 Neptune" to arrayListOf("Xuanwu Badge"),
			"Lvl 90 Titan" to arrayListOf("Baihu Badge"),
			"Lvl 90 Zephyrus" to arrayListOf("Qinglong Badge")
		)
		
		val itemsForEvent = mapOf(
			"EX Event Quest" to arrayListOf("Repeated Runs"),
			"VH Event Raid" to arrayListOf("Repeated Runs"),
			"EX Event Raid" to arrayListOf("Repeated Runs")
		)
		
		val itemsForEventTokenDrawboxes = mapOf(
			"EX Event Quest" to arrayListOf("Repeated Runs"),
			"VH Event Raid" to arrayListOf("Repeated Runs"),
			"EX Event Raid" to arrayListOf("Repeated Runs"),
			"IM Event Raid" to arrayListOf("Repeated Runs")
		)
		
		val itemsForROTB = mapOf(
			"VH Zhuque" to arrayListOf("Repeated Runs"),
			"VH Xuanwu" to arrayListOf("Repeated Runs"),
			"VH Baihu" to arrayListOf("Repeated Runs"),
			"VH Qinglong" to arrayListOf("Repeated Runs"),
			"EX Zhuque" to arrayListOf("Repeated Runs"),
			"EX Xuanwu" to arrayListOf("Repeated Runs"),
			"EX Baihu" to arrayListOf("Repeated Runs"),
			"EX Qinglong" to arrayListOf("Repeated Runs"),
			"Lvl 100 Shenxian" to arrayListOf("Repeated Runs")
		)
		
		val itemsForGuildWars = mapOf(
			"Very Hard" to arrayListOf("Repeated Runs"),
			"Extreme" to arrayListOf("Repeated Runs"),
			"Extreme+" to arrayListOf("Repeated Runs"),
			"NM90" to arrayListOf("Repeated Runs"),
			"NM95" to arrayListOf("Repeated Runs"),
			"NM100" to arrayListOf("Repeated Runs"),
			"NM150" to arrayListOf("Repeated Runs")
		)
		
		val itemsForDreadBarrage = mapOf(
			"1 Star" to arrayListOf("Repeated Runs"),
			"2 Star" to arrayListOf("Repeated Runs"),
			"3 Star" to arrayListOf("Repeated Runs"),
			"4 Star" to arrayListOf("Repeated Runs"),
			"5 Star" to arrayListOf("Repeated Runs"),
		)
		
		val itemsForProvingGrounds = mapOf(
			"Extreme" to arrayListOf("Repeated Runs"),
			"Extreme+" to arrayListOf("Repeated Runs"),
		)
		
		val itemsForXenoClash = mapOf(
			"Xeno Clash Extreme" to arrayListOf("Repeated Runs"),
			"Xeno Clash Raid" to arrayListOf("Repeated Runs")
		)
	}
}