import React, { useContext, useEffect, useState } from "react"
import { View, FlatList, Text, Image, ImageSourcePropType } from "react-native"
import { Card } from "react-native-elements"
import { BotStateContext } from "../../context/BotStateContext"

interface Summon {
    label: string
    uri: ImageSourcePropType
}

const TransferList = ({ isNightmare }: { isNightmare: boolean }) => {
    const [leftList, setLeftList] = useState<Summon[]>([])
    const [rightList, setRightList] = useState<Summon[]>([])

    const botStateContext = useContext(BotStateContext)

    // Require statements are created statically ahead of time for app bundling to work.
    const summonData: Summon[] = [
        {
            label: "Colossus Omega",
            uri: require("../../images/summons/colossus_omega.png"),
        },
        {
            label: "Shiva",
            uri: require("../../images/summons/shiva.png"),
        },
        {
            label: "Agni",
            uri: require("../../images/summons/agni.png"),
        },
        {
            label: "Leviathan Omega",
            uri: require("../../images/summons/leviathan_omega.png"),
        },
        {
            label: "Europa",
            uri: require("../../images/summons/europa.png"),
        },
        {
            label: "Bonito",
            uri: require("../../images/summons/bonito.png"),
        },
        {
            label: "Varuna",
            uri: require("../../images/summons/varuna.png"),
        },
        {
            label: "Yggdrasil Omega",
            uri: require("../../images/summons/yggdrasil_omega.png"),
        },
        {
            label: "Godsworn Alexiel",
            uri: require("../../images/summons/godsworn_alexiel.png"),
        },
        {
            label: "Titan",
            uri: require("../../images/summons/titan.png"),
        },
        {
            label: "Tiamat Omega",
            uri: require("../../images/summons/tiamat_omega.png"),
        },
        {
            label: "Grimnir",
            uri: require("../../images/summons/grimnir.png"),
        },
        {
            label: "Zephyrus",
            uri: require("../../images/summons/zephyrus.png"),
        },
        {
            label: "Luminiera Omega",
            uri: require("../../images/summons/luminiera_omega.png"),
        },
        {
            label: "Lucifer",
            uri: require("../../images/summons/lucifer.png"),
        },
        {
            label: "Zeus",
            uri: require("../../images/summons/zeus.png"),
        },
        {
            label: "Celeste Omega",
            uri: require("../../images/summons/celeste_omega.png"),
        },
        {
            label: "Bahamut",
            uri: require("../../images/summons/bahamut.png"),
        },
        {
            label: "Hades",
            uri: require("../../images/summons/hades.png"),
        },
        {
            label: "Kaguya",
            uri: require("../../images/summons/kaguya.png"),
        },
        {
            label: "White Rabbit",
            uri: require("../../images/summons/white_rabbit.png"),
        },
        {
            label: "Black Rabbit",
            uri: require("../../images/summons/black_rabbit.png"),
        },
        {
            label: "Huanglong",
            uri: require("../../images/summons/huanglong.png"),
        },
        {
            label: "Qilin",
            uri: require("../../images/summons/qilin.png"),
        },
        {
            label: "Nobiyo",
            uri: require("../../images/summons/nobiyo.png"),
        },
    ]

    // Populate the Support Summon List.
    useEffect(() => {
        // Populate the left list.
        let oldLeftList: Summon[] = []

        Object.entries(summonData).forEach((key) => {
            oldLeftList = [...oldLeftList, key[1]]
        })

        oldLeftList = Array.from(new Set(oldLeftList))

        // Populate the right list.
        let oldRightList: Summon[] = []
        if (!isNightmare) {
            botStateContext.settings.game.summons.forEach((summon) => {
                summonData.map((item) => {
                    if (item.label === summon) {
                        oldRightList.push({ label: summon, uri: item.uri })
                    }
                })
            })
        } else {
            botStateContext.settings.nightmare.nightmareSummons.forEach((nightmareSummon) => {
                summonData.map((item) => {
                    if (item.label === nightmareSummon) {
                        oldRightList.push({ label: nightmareSummon, uri: item.uri })
                    }
                })
            })
        }

        // Filter out summons from the left list that are already selected.
        const filteredList = oldLeftList.filter((leftSummon) =>
            oldRightList.every((rightSummon) => {
                if (leftSummon.label === rightSummon.label) {
                    return false
                } else {
                    return true
                }
            })
        )

        setLeftList(filteredList)
        setRightList(oldRightList)
    }, [])

    const handleChecked = (value: string, isLeftList: boolean) => () => {
        var newRightList: Summon[] = []
        if (isLeftList) {
            // Handle the left list.
            const index = leftList.findIndex((summon) => summon.label === value)
            const newLeftList = [...leftList]
            newLeftList.splice(index, 1)
            setLeftList(newLeftList)

            // Move the element to the right list.
            newRightList = [...rightList, { label: value, uri: leftList[index].uri }]
            setRightList(newRightList)
        } else {
            // Handle the right list
            const index = rightList.findIndex((summon) => summon.label === value)
            newRightList = [...rightList]
            newRightList.splice(index, 1)
            setRightList(newRightList)

            // Get the index of the summon from the original untouched list.
            const newIndex = summonData.findIndex((summon) => summon.label === value)

            // Move the element to the left list.
            const newLeftList = [...leftList, { label: value, uri: summonData[newIndex].uri }]
            setLeftList(newLeftList)
        }

        // Save selected summons to settings.
        if (!isNightmare) {
            const newSummons: string[] = []
            newRightList.forEach((summon) => newSummons.push(summon.label))
            botStateContext.setSettings({ ...botStateContext.settings, game: { ...botStateContext.settings.game, summons: newSummons, summonElements: [] } })
        } else {
            const newSummons: string[] = []
            newRightList.forEach((summon) => newSummons.push(summon.label))
            botStateContext.setSettings({ ...botStateContext.settings, nightmare: { ...botStateContext.settings.nightmare, nightmareSummons: newSummons, nightmareSummonElements: [] } })
        }
    }

    const customList = (items: Summon[], isLeftList: boolean) => (
        <Card containerStyle={{ flex: 1 }}>
            <Text>{isLeftList ? "Available Support Summons" : "Selected Support Summons"}</Text>

            <FlatList
                data={items}
                style={{ height: "95%" }}
                contentContainerStyle={{ paddingBottom: 20 }}
                renderItem={(item) => {
                    return (
                        <View onTouchEnd={handleChecked(item.item.label, isLeftList)}>
                            <Card containerStyle={{ alignItems: "center" }}>
                                <Card.Title>{item.item.label}</Card.Title>
                                <Image source={item.item.uri} />
                            </Card>
                        </View>
                    )
                }}
                keyExtractor={(item) => `key-${item.label}`}
            />
        </Card>
    )

    return (
        <View style={{ flex: 1, flexDirection: "column" }}>
            {customList(leftList, true)}
            {customList(rightList, false)}
        </View>
    )
}

export default TransferList
