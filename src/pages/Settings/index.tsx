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
import Checkbox from "../../components/Checkbox"
import SnackBar from "rn-snackbar-component"
import Ionicons from "react-native-vector-icons/Ionicons"
import { MessageLogContext } from "../../context/MessageLogContext"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "center",
        margin: 10,
    },
    picker: {
        marginVertical: 10,
        backgroundColor: "azure",
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

export interface CombatScript {
    name: string
    script: string[]
}

const Settings = () => {
    const [isFarmingModePickerOpen, setIsFarmingModePickerOpen] = useState<boolean>(false)
    const [isItemPickerOpen, setIsItemPickerOpen] = useState<boolean>(false)
    const [isMissionPickerOpen, setIsMissionPickerOpen] = useState<boolean>(false)
    const [modalOpen, setModalOpen] = useState<boolean>(false)
    const [firstTime, setFirstTime] = useState<boolean>(true)
    const [firstTime2, setFirstTime2] = useState<boolean>(true)
    const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false)

    const [itemList, setItemList] = useState<Item[]>([])
    const [missionList, setMissionList] = useState<Item[]>([])

    // Individual states are necessary as react-native-dropdown-picker requires its setValue state parameter to be set for DropDownPicker.
    const [farmingMode, setFarmingMode] = useState<ValueType | null>("")
    const [item, setItem] = useState<ValueType | null>("")
    const [mission, setMission] = useState<ValueType | null>("")
    const [map, setMap] = useState<string>("")
    const [itemAmount, setItemAmount] = useState<number>(1)
    const [groupNumber, setGroupNumber] = useState<number>(1)
    const [partyNumber, setPartyNumber] = useState<number>(1)
    const [combatScript, setCombatScript] = useState<CombatScript>({ name: "", script: [] })
    const [enableNightmare, setEnableNightmare] = useState<boolean>(false)
    const [enableLocationIncrementByOne, setEnableLocationIncrementByOne] = useState<boolean>(false)
    const [enableStopOnArcarumBoss, setEnableStopOnArcarumBoss] = useState<boolean>(true)

    const bsc = useContext(BotStateContext)
    const mlc = useContext(MessageLogContext)

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
    // Callbacks

    // Load state from context to local.
    useEffect(() => {
        setFarmingMode(bsc.settings.game.farmingMode)
        setItem(bsc.settings.game.item)
        setMission(bsc.settings.game.mission)
        setMap(bsc.settings.game.map)
        setItemAmount(bsc.settings.game.itemAmount)
        setGroupNumber(bsc.settings.game.groupNumber)
        setPartyNumber(bsc.settings.game.partyNumber)
        setCombatScript({ name: bsc.settings.game.combatScriptName, script: bsc.settings.game.combatScript })
        setEnableNightmare(bsc.settings.nightmare.enableNightmare)
        setEnableLocationIncrementByOne(bsc.settings.event.enableLocationIncrementByOne)
        setEnableStopOnArcarumBoss(bsc.settings.arcarum.enableStopOnArcarumBoss)
        setFirstTime(false)
    }, [])

    useEffect(() => {
        if (!firstTime) {
            mlc.setMessageLog([])
            mlc.setAsyncMessages([])

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
                event: {
                    ...bsc.settings.event,
                    enableLocationIncrementByOne: false,
                },
                arcarum: {
                    ...bsc.settings.arcarum,
                    enableStopOnArcarumBoss: true,
                },
            })

            setFirstTime2(false)
        }
    }, [farmingMode])

    // Save every other setting.
    useEffect(() => {
        if (!firstTime) {
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
                nightmare: {
                    ...bsc.settings.nightmare,
                    enableNightmare: enableNightmare,
                },
                event: {
                    ...bsc.settings.event,
                    enableLocationIncrementByOne: enableLocationIncrementByOne,
                },
                arcarum: {
                    ...bsc.settings.arcarum,
                    enableStopOnArcarumBoss: enableStopOnArcarumBoss,
                },
            })
        }
    }, [item, mission, map, itemAmount, groupNumber, partyNumber, combatScript, enableNightmare, enableLocationIncrementByOne, enableStopOnArcarumBoss])

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

        if (!firstTime2) {
            setItem("")
        }
    }, [farmingMode])

    // Populate the mission list based on item.
    useEffect(() => {
        let newMissionList: Item[] = []
        if (
            bsc.settings.game.farmingMode === "Quest" ||
            bsc.settings.game.farmingMode === "Special" ||
            bsc.settings.game.farmingMode === "Coop" ||
            bsc.settings.game.farmingMode === "Raid" ||
            bsc.settings.game.farmingMode === "Event" ||
            bsc.settings.game.farmingMode === "Event (Token Drawboxes)" ||
            bsc.settings.game.farmingMode === "Rise of the Beasts" ||
            bsc.settings.game.farmingMode === "Guild Wars" ||
            bsc.settings.game.farmingMode === "Dread Barrage" ||
            bsc.settings.game.farmingMode === "Proving Grounds" ||
            bsc.settings.game.farmingMode === "Xeno Clash" ||
            bsc.settings.game.farmingMode === "Arcarum" ||
            bsc.settings.game.farmingMode === "Generic"
        ) {
            Object.entries(data[bsc.settings.game.farmingMode]).forEach((obj) => {
                if (obj[1].items.indexOf(item.toString()) !== -1) {
                    newMissionList = newMissionList.concat({ label: obj[0], value: obj[0] })
                }
            })
        }

        const filteredNewMissionList = Array.from(new Set(newMissionList))
        setMissionList(filteredNewMissionList)

        if (!firstTime2) {
            setMission("")
        }
    }, [item])

    // Fetch the map that corresponds to the selected mission if applicable. Not for Coop.
    useEffect(() => {
        if (
            bsc.settings.game.farmingMode === "Quest" ||
            bsc.settings.game.farmingMode === "Special" ||
            bsc.settings.game.farmingMode === "Raid" ||
            bsc.settings.game.farmingMode === "Event" ||
            bsc.settings.game.farmingMode === "Event (Token Drawboxes)" ||
            bsc.settings.game.farmingMode === "Rise of the Beasts" ||
            bsc.settings.game.farmingMode === "Guild Wars" ||
            bsc.settings.game.farmingMode === "Dread Barrage" ||
            bsc.settings.game.farmingMode === "Proving Grounds" ||
            bsc.settings.game.farmingMode === "Xeno Clash" ||
            bsc.settings.game.farmingMode === "Arcarum" ||
            bsc.settings.game.farmingMode === "Generic"
        ) {
            Object.entries(data[bsc.settings.game.farmingMode]).every((obj) => {
                if (obj[0] === mission) {
                    setMap(obj[1].map)
                    return false
                } else {
                    return true
                }
            })
        }
    }, [mission])

    useEffect(() => {
        // Manually set this flag to false as the snackbar autohiding does not set this to false automatically.
        setSnackbarOpen(true)
        setTimeout(() => setSnackbarOpen(false), 1500)
    }, [bsc.readyStatus])

    return (
        <View style={styles.root}>
            <SnackBar
                visible={snackbarOpen}
                message={bsc.readyStatus ? "Bot is ready!" : "Bot is not ready!"}
                actionHandler={() => setSnackbarOpen(false)}
                action={<Ionicons name="close" size={30} />}
                autoHidingTime={1500}
                containerStyle={{ backgroundColor: bsc.readyStatus ? "green" : "red", borderRadius: 10 }}
                native={false}
            />

            <ScrollView nestedScrollEnabled={true} contentContainerStyle={{ height: "100%" }}>
                <View style={{ height: Dimensions.get("screen").height * 0.5, marginHorizontal: 20 }}>
                    <CustomButton
                        title={bsc.settings.game.combatScriptName === "" ? "Select Combat Script" : `Selected: ${bsc.settings.game.combatScriptName}`}
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

                                        bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, combatScriptName: pickerResult.name, combatScript: newCombatScript } })
                                    })
                                }
                            } catch (e) {
                                console.warn(e)
                                bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, combatScriptName: "", combatScript: [] } })
                            }
                        }}
                    />

                    <Text style={{ marginBottom: 10, fontSize: 12, opacity: 0.7, color: "black" }}>
                        To deselect, cancel/back out of the document picker. If no combat script is selected, Full/Semi Auto is used by default.
                    </Text>

                    <Divider />

                    <DropDownPicker
                        listMode="SCROLLVIEW"
                        style={[styles.picker, { backgroundColor: bsc.settings.game.farmingMode !== "" ? "azure" : "pink" }]}
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

                    {bsc.settings.game.farmingMode === "Event" ? (
                        <Checkbox
                            text="Enable Incrementation of Location by 1"
                            subtitle="Enable this if the event has its N/H missions at the very top so the bot can correctly select the correct quest. Or in otherwords, enable this if the Event tab in the Special page has 3 'Select' buttons instead of 2."
                            state={enableLocationIncrementByOne}
                            updateState={setEnableLocationIncrementByOne}
                        />
                    ) : null}

                    {bsc.settings.game.farmingMode === "Arcarum" ? (
                        <Checkbox
                            text="Enable Stop on Arcarum Boss"
                            subtitle="Enable this option to have the bot upon encountering a Arcarum Boss (3-3, 6-3, 9-9)."
                            state={enableStopOnArcarumBoss}
                            updateState={setEnableStopOnArcarumBoss}
                        />
                    ) : null}

                    {bsc.settings.game.farmingMode === "Special" ||
                    bsc.settings.game.farmingMode === "Event" ||
                    bsc.settings.game.farmingMode === "Event (Token Drawboxes)" ||
                    bsc.settings.game.farmingMode === "Xeno Clash" ||
                    bsc.settings.game.farmingMode === "Rise of the Beasts" ? (
                        <Checkbox
                            text="Enable Nightmare Settings"
                            subtitle="Enable additional settings to show up in the Extra Settings page."
                            state={enableNightmare}
                            updateState={setEnableNightmare}
                        />
                    ) : null}

                    <DropDownPicker
                        listMode="MODAL"
                        modalProps={{
                            animationType: "slide",
                        }}
                        style={[styles.picker, { backgroundColor: bsc.settings.game.item !== "" ? "azure" : "pink" }]}
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

                    <DropDownPicker
                        listMode="SCROLLVIEW"
                        style={[styles.picker, { backgroundColor: bsc.settings.game.mission !== "" ? "azure" : "pink" }]}
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

                    <View>
                        <Text style={{ color: "#000" }}>Item Amount:</Text>
                        <Picker selectedValue={itemAmount} onValueChange={(value) => setItemAmount(value)} mode="dropdown" style={{ color: "#000" }} dropdownIconColor={"#000"}>
                            {[...Array(999 - 1 + 1).keys()]
                                .map((x) => x + 1)
                                .map((value) => {
                                    return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                                })}
                        </Picker>
                    </View>

                    {bsc.settings.game.farmingMode !== "Coop" && bsc.settings.game.farmingMode !== "Arcarum" ? (
                        <View style={{ zIndex: 9996 }}>
                            {bsc.settings.game.summons.length > 0 ? (
                                <CustomButton title="Select Support Summon(s)" width={"100%"} onPress={() => setModalOpen(true)} />
                            ) : (
                                <CustomButton title="Select Support Summon(s)" backgroundColor="red" width={"100%"} onPress={() => setModalOpen(true)} />
                            )}
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

                    <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
                        <View style={{ width: Dimensions.get("window").width * 0.3 }}>
                            <Text style={{ color: "#000" }}>Group #:</Text>
                            <Picker selectedValue={groupNumber} onValueChange={(value) => setGroupNumber(value)} mode="dropdown" style={{ color: "#000" }} dropdownIconColor={"#000"}>
                                {[...Array(7 - 1 + 1).keys()]
                                    .map((x) => x + 1)
                                    .map((value) => {
                                        return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                                    })}
                            </Picker>
                        </View>
                        <View style={{ width: Dimensions.get("window").width * 0.3 }}>
                            <Text style={{ color: "#000" }}>Party #:</Text>
                            <Picker selectedValue={partyNumber} onValueChange={(value) => setPartyNumber(value)} mode="dropdown" style={{ color: "#000" }} dropdownIconColor={"#000"}>
                                {[...Array(6 - 1 + 1).keys()]
                                    .map((x) => x + 1)
                                    .map((value) => {
                                        return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                                    })}
                            </Picker>
                        </View>
                    </View>
                </View>
            </ScrollView>
        </View>
    )
}

export default Settings
