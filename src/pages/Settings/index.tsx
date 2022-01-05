import React, { useContext, useEffect, useState } from "react"
import { StyleSheet, View, Text, ScrollView, Dimensions, TouchableOpacity, Modal } from "react-native"
import DropDownPicker, { ValueType } from "react-native-dropdown-picker"
import { Divider } from "react-native-elements"
import DocumentPicker from "react-native-document-picker"
import RNFS from "react-native-fs"
import data from "../../data/data.json"
import { BotStateContext } from "../../context/BotStateContext"
import { Picker } from "@react-native-picker/picker"
import CustomButton from "../../components/CustomButton"
import TransferList from "../../components/TransferList"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "center",
        marginVertical: 10,
    },
    picker: {
        marginVertical: 10,
    },
    dropdown: {
        marginTop: 20,
    },
    modal: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "rgba(80,80,80,0.3)",
    },
    outsideModal: {
        position: "absolute",
        height: "100%",
        width: "100%",
    },
    componentContainer: {
        width: Dimensions.get("window").width * 0.7,
        height: Dimensions.get("window").height * 0.9,
    },
})

interface Item {
    label: string
    value: string
}

interface CombatScript {
    name: string
    script: string[]
}

const Settings = () => {
    const [isFarmingModePickerOpen, setIsFarmingModePickerOpen] = useState<boolean>(false)
    const [isItemPickerOpen, setIsItemPickerOpen] = useState<boolean>(false)
    const [isMissionPickerOpen, setIsMissionPickerOpen] = useState<boolean>(false)
    const [modalOpen, setModalOpen] = useState<boolean>(false)

    const [itemList, setItemList] = useState<Item[]>([])
    const [missionList, setMissionList] = useState<Item[]>([])

    const [farmingMode, setFarmingMode] = useState<ValueType | null>("")
    const [item, setItem] = useState<ValueType | null>("")
    const [mission, setMission] = useState<ValueType | null>("")
    const [map, setMap] = useState<string>("")
    const [itemAmount, setItemAmount] = useState<number>(1)
    const [groupNumber, setGroupNumber] = useState<number>(1)
    const [partyNumber, setPartyNumber] = useState<number>(1)
    const [combatScript, setCombatScript] = useState<CombatScript>({ name: "", script: [] })

    const bsc = useContext(BotStateContext)

    const farmingModes = [
        { label: "Quest", value: "Quest" },
        { label: "Special", value: "Special" },
        { label: "Coop", value: "Coop" },
        { label: "Raid", value: "Raid" },
        { label: "Event", value: "Event" },
        { label: "Event (Token Drawboxes)", value: "Event (Token Drawboxes)" },
        { label: "Rise of the Beasts", value: "Rise of the Beasts" },
        { label: "Guild Wars", value: "Guild Wars" },
        { label: "Dread Barrage", value: "Dread Barrage" },
        { label: "Proving Grounds", value: "Proving Grounds" },
        { label: "Xeno Clash", value: "Xeno Clash" },
        { label: "Arcarum", value: "Arcarum" },
        { label: "Generic", value: "Generic" },
    ]

    // Manually close all pickers as react-native-dropdown-picker does not handle that automatically.
    const closeAllPickers = () => {
        setIsFarmingModePickerOpen(false)
        setIsItemPickerOpen(false)
        setIsMissionPickerOpen(false)
    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Callbacks to save current settings.

    useEffect(() => {
        if (farmingMode) {
            // Reset selected Item and Mission and save the farming mode.
            bsc.setSettings({
                ...bsc.settings,
                game: { ...bsc.settings.game, farmingMode: farmingMode.toString(), item: "", mission: "", map: "" },
                nightmare: {
                    ...bsc.settings.nightmare,
                    enableNightmare: false,
                    enableCustomNightmareSettings: false,
                    nightmareCombatScriptName: "",
                    nightmareCombatScript: [],
                    nightmareSummons: [],
                    nightmareSummonElements: [],
                    nightmareGroupNumber: 1,
                    nightmarePartyNumber: 1,
                },
            })
        }

        // Reset Item and Mission in local state.
        setItem("")
        setMission("")
    }, [farmingMode])

    // Fetch the map that corresponds to the selected mission if applicable. Not for Coop.
    useEffect(() => {
        if (
            farmingMode === "Quest" ||
            farmingMode === "Special" ||
            farmingMode === "Raid" ||
            farmingMode === "Event" ||
            farmingMode === "Event (Token Drawboxes)" ||
            farmingMode === "Rise of the Beasts" ||
            farmingMode === "Guild Wars" ||
            farmingMode === "Dread Barrage" ||
            farmingMode === "Proving Grounds" ||
            farmingMode === "Xeno Clash" ||
            farmingMode === "Arcarum" ||
            farmingMode === "Generic"
        ) {
            Object.entries(data[farmingMode]).every((obj) => {
                if (obj[0] === mission) {
                    setMap(obj[1].map)
                    return false
                } else {
                    return true
                }
            })
        }
    }, [mission])

    // Save every other setting.
    useEffect(() => {
        if (farmingMode && item && mission) {
            bsc.setSettings({
                ...bsc.settings,
                game: {
                    ...bsc.settings.game,
                    farmingMode: farmingMode.toString(),
                    item: item.toString(),
                    mission: mission.toString(),
                    map: map,
                    itemAmount: itemAmount,
                    groupNumber: groupNumber,
                    partyNumber: partyNumber,
                    combatScriptName: combatScript.name,
                    combatScript: combatScript.script,
                },
            })
        }
    }, [item, mission, map, itemAmount, groupNumber, partyNumber, combatScript])

    // Populates the item list based on farming mode.
    useEffect(() => {
        let newItemList: Item[] = []
        if (
            farmingMode === "Quest" ||
            farmingMode === "Special" ||
            farmingMode === "Coop" ||
            farmingMode === "Raid" ||
            farmingMode === "Event" ||
            farmingMode === "Event (Token Drawboxes)" ||
            farmingMode === "Rise of the Beasts" ||
            farmingMode === "Guild Wars" ||
            farmingMode === "Dread Barrage" ||
            farmingMode === "Proving Grounds" ||
            farmingMode === "Xeno Clash" ||
            farmingMode === "Arcarum" ||
            farmingMode === "Generic"
        ) {
            Object.values(data[farmingMode]).forEach((tempItems) => {
                tempItems.items.forEach((item) => {
                    newItemList = newItemList.concat({ label: item, value: item })
                })
            })
        }

        // Remove any duplicates.
        const filteredNewItemList = newItemList.filter((v, i, a) => a.findIndex((t) => t.label === v.label) === i)
        setItemList(filteredNewItemList)

        setMission("") // Reset Mission in local state.
    }, [farmingMode])

    // Populate the mission list based on item.
    useEffect(() => {
        if (item) {
            let newMissionList: Item[] = []
            if (
                farmingMode === "Quest" ||
                farmingMode === "Special" ||
                farmingMode === "Coop" ||
                farmingMode === "Raid" ||
                farmingMode === "Event" ||
                farmingMode === "Event (Token Drawboxes)" ||
                farmingMode === "Rise of the Beasts" ||
                farmingMode === "Guild Wars" ||
                farmingMode === "Dread Barrage" ||
                farmingMode === "Proving Grounds" ||
                farmingMode === "Xeno Clash" ||
                farmingMode === "Arcarum" ||
                farmingMode === "Generic"
            ) {
                Object.entries(data[farmingMode]).forEach((obj) => {
                    if (obj[1].items.indexOf(item.toString()) !== -1) {
                        newMissionList = newMissionList.concat({ label: obj[0], value: obj[0] })
                    }
                })
            }

            const filteredNewMissionList = Array.from(new Set(newMissionList))
            setMissionList(filteredNewMissionList)
        }
    }, [item])

    return (
        <View style={styles.root}>
            <ScrollView nestedScrollEnabled={true}>
                <View style={{ height: Dimensions.get("screen").height * 0.5, marginHorizontal: 20 }}>
                    <CustomButton
                        title="Select Combat Script"
                        width={200}
                        borderRadius={20}
                        onPress={async () => {
                            try {
                                const pickerResult = await DocumentPicker.pickSingle({
                                    type: "text/plain",
                                })

                                const uri = pickerResult.uri
                                if (uri.startsWith("content://")) {
                                    // Convert content uri to file uri.
                                    // Source: https://stackoverflow.com/a/62677483
                                    const uriComponents = uri.split("/")
                                    const fileNameAndExtension = uriComponents[uriComponents.length - 1]
                                    const destPath = `${RNFS.TemporaryDirectoryPath}/${fileNameAndExtension}`
                                    await RNFS.copyFile(uri, destPath)

                                    // Now read the file using the newly converted file uri.
                                    await RNFS.readFile("file://" + destPath).then((data) => {
                                        console.log("Read combat script: ", data)

                                        const newCombatScript: string[] = data
                                            .replace(/\r\n/g, "\n") // Replace LF with CRLF.
                                            .replace(/[\r\n]/g, "\n")
                                            .replace("\t", "") // Replace tab characters.
                                            .replace(/\t/g, "")
                                            .split("\n")

                                        setCombatScript({ name: pickerResult.name, script: newCombatScript })
                                    })
                                }
                            } catch (e) {
                                console.warn(e)
                                setCombatScript({ name: "", script: [] })
                            }
                        }}
                    />

                    <Divider />

                    <DropDownPicker
                        listMode="SCROLLVIEW"
                        style={styles.picker}
                        dropDownContainerStyle={styles.dropdown}
                        placeholder="Select Farming Mode"
                        searchTextInputStyle={{ fontStyle: "italic" }}
                        items={farmingModes}
                        open={isFarmingModePickerOpen}
                        setOpen={(flag) => {
                            closeAllPickers()
                            setIsFarmingModePickerOpen(flag)
                        }}
                        value={farmingMode}
                        setValue={setFarmingMode}
                        zIndex={9999}
                    />

                    {farmingMode !== "" ? (
                        <DropDownPicker
                            listMode="MODAL"
                            modalProps={{
                                animationType: "slide",
                            }}
                            style={styles.picker}
                            dropDownContainerStyle={styles.dropdown}
                            placeholder="Select Item"
                            searchTextInputStyle={{ fontStyle: "italic" }}
                            searchable={true}
                            items={itemList}
                            open={isItemPickerOpen}
                            setOpen={(flag) => {
                                closeAllPickers()
                                setIsItemPickerOpen(flag)
                            }}
                            value={item}
                            setValue={setItem}
                            zIndex={9998}
                        />
                    ) : null}

                    {item !== "" ? (
                        <DropDownPicker
                            listMode="SCROLLVIEW"
                            style={styles.picker}
                            dropDownContainerStyle={styles.dropdown}
                            placeholder="Select Mission"
                            searchTextInputStyle={{ fontStyle: "italic" }}
                            items={missionList}
                            open={isMissionPickerOpen}
                            setOpen={(flag) => {
                                closeAllPickers()
                                setIsMissionPickerOpen(flag)
                            }}
                            value={mission}
                            setValue={setMission}
                            zIndex={9997}
                        />
                    ) : null}

                    {mission !== "" ? (
                        <View>
                            <Text>Item Amount:</Text>
                            <Picker selectedValue={itemAmount} onValueChange={(value) => setItemAmount(value)} mode="dropdown">
                                {[...Array(999 - 1 + 1).keys()]
                                    .map((x) => x + 1)
                                    .map((value) => {
                                        return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                                    })}
                            </Picker>
                        </View>
                    ) : null}

                    {mission !== "" && farmingMode !== "Coop" && farmingMode !== "Arcarum" ? (
                        <View style={{ zIndex: 9996 }}>
                            <CustomButton title="Select Summons" width={"100%"} onPress={() => setModalOpen(true)} />
                            <Modal transparent={true} animationType="fade" statusBarTranslucent={true} visible={modalOpen} onRequestClose={() => setModalOpen(false)}>
                                <View style={styles.modal}>
                                    <TouchableOpacity style={styles.outsideModal} onPress={() => setModalOpen(false)} />
                                    <View style={styles.componentContainer}>
                                        <TransferList isNightmare={false} />
                                    </View>
                                </View>
                            </Modal>
                        </View>
                    ) : null}

                    {mission !== "" ? (
                        <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
                            <View style={{ width: Dimensions.get("window").width * 0.3 }}>
                                <Text>Group #:</Text>
                                <Picker selectedValue={groupNumber} onValueChange={(value) => setGroupNumber(value)} mode="dropdown">
                                    {[...Array(7 - 1 + 1).keys()]
                                        .map((x) => x + 1)
                                        .map((value) => {
                                            return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                                        })}
                                </Picker>
                            </View>
                            <View style={{ width: Dimensions.get("window").width * 0.3 }}>
                                <Text>Party #:</Text>
                                <Picker selectedValue={partyNumber} onValueChange={(value) => setPartyNumber(value)} mode="dropdown">
                                    {[...Array(6 - 1 + 1).keys()]
                                        .map((x) => x + 1)
                                        .map((value) => {
                                            return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                                        })}
                                </Picker>
                            </View>
                        </View>
                    ) : null}
                </View>
            </ScrollView>
        </View>
    )
}

export default Settings
