import React, { useContext, useEffect, useState } from "react"
import { StyleSheet, View, FlatList, ScrollView, Text, Image } from "react-native"
import { Card, Button, Icon, Divider, ListItem, Avatar } from "react-native-elements"
import { BotStateContext } from "../../context/BotStateContext"
import summonData from "../../data/summons.json"

const TransferList = ({ isNightmare }: { isNightmare: boolean }) => {
    const [leftList, setLeftList] = useState<string[]>([])
    const [rightList, setRightList] = useState<string[]>([])

    const botStateContext = useContext(BotStateContext)

    // Populate the Support Summon List.
    useEffect(() => {
        // Populate the left list.
        var oldLeftList: string[] = leftList

        Object.entries(summonData).forEach((key) => {
            key[1].summons.forEach((summon) => {
                oldLeftList = [...oldLeftList, summon]
            })
        })

        oldLeftList = Array.from(new Set(oldLeftList))

        // Populate the right list.
        var oldRightList: string[] = []
        if (!isNightmare) {
            oldRightList = botStateContext.settings.game.summons
        } else {
            oldRightList = botStateContext.settings.nightmare.nightmareSummons
        }

        // Filter out summons from the left list that are already selected.
        const filteredList = oldLeftList.filter((summon) => !oldRightList.includes(summon))

        setLeftList(filteredList)
        setRightList(oldRightList)
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    const handleChecked = (value: string, isLeftList: boolean) => () => {
        var newRightList: string[] = []
        if (isLeftList) {
            // Handle the left list.
            const index = leftList.indexOf(value)
            const newLeftList = [...leftList]
            newLeftList.splice(index, 1)
            setLeftList(newLeftList)

            // Move the element to the right list.
            newRightList = [...rightList, value]
            setRightList(newRightList)
        } else {
            // Handle the right list
            const index = rightList.indexOf(value)
            newRightList = [...rightList]
            newRightList.splice(index, 1)
            setRightList(newRightList)

            // Move the element to the left list.
            const newLeftList = [...leftList, value]
            setLeftList(newLeftList)
        }

        // Save selected summons to settings.
        if (!isNightmare) {
            botStateContext.setSettings({ ...botStateContext.settings, game: { ...botStateContext.settings.game, summons: newRightList, summonElements: [] } })
        } else {
            botStateContext.setSettings({ ...botStateContext.settings, nightmare: { ...botStateContext.settings.nightmare, nightmareSummons: newRightList, nightmareSummonElements: [] } })
        }
    }

    const customList = (items: string[], isLeftList: boolean) => (
        <Card containerStyle={{ height: "100%" }}>
            <Text>{isLeftList ? "Available Support Summons" : "Selected Support Summons"}</Text>

            <Divider />

            <FlatList
                data={items}
                renderItem={(item) => {
                    return (
                        <Card>
                            <Card.Title>{item.item}</Card.Title>
                            <Image source={require(`../../images/summons/agni.png`)} />
                        </Card>
                    )
                }}
                keyExtractor={(item) => `key-${item}`}
            />
        </Card>
    )

    return (
        <View>
            {customList(leftList, true)}
            {/* <FlatList data={leftList} renderItem={() => customList(leftList, true)} keyExtractor={(item) => item} />
            <FlatList data={rightList} renderItem={() => customList(rightList, true)} keyExtractor={(item) => item} /> */}
        </View>
    )
}

export default TransferList
