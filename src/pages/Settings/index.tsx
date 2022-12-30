import Checkbox from "../../components/Checkbox"
import CustomButton from "../../components/CustomButton"
import data from "../../data/data.json"
import DocumentPicker from "react-native-document-picker"
import DropDownPicker, { ValueType } from "react-native-dropdown-picker"
import Ionicons from "react-native-vector-icons/Ionicons"
import React, { useContext, useEffect, useState } from "react"
import RNFS from "react-native-fs"
import SnackBar from "rn-snackbar-component"
import TransferList from "../../components/TransferList"
import { BotStateContext } from "../../context/BotStateContext"
import { Dimensions, Modal, ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native"
import { Divider } from "react-native-elements"
import { Picker } from "@react-native-picker/picker"

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

    // Certain individual states are necessary as react-native-dropdown-picker requires a setValue state parameter for DropDownPicker.
    const [farmingMode, setFarmingMode] = useState<ValueType>("")
    const [item, setItem] = useState<ValueType>("")
    const [mission, setMission] = useState<ValueType>("")

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
        { label: "Arcarum Sandbox", value: "Arcarum Sandbox" },
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

    // Load some specific states from context to local.
    useEffect(() => {
        setFarmingMode(bsc.settings.game.farmingMode)
        setItem(bsc.settings.game.item)
        setMission(bsc.settings.game.mission)
        setFirstTime(false)
    }, [])

    useEffect(() => {
        if (!firstTime) {
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
                sandbox: {
                    ...bsc.settings.sandbox,
                    enableDefender: false,
                    enableCustomDefenderSettings: false,
                    numberOfDefenders: 1,
                    defenderCombatScriptName: "",
                    defenderCombatScript: [],
                    defenderGroupNumber: 1,
                    defenderPartyNumber: 1,
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
                    item: item.toString(),
                    mission: mission.toString(),
                },
            })
        }
    }, [item, mission])

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
            farmingMode === "Arcarum Sandbox" ||
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
            bsc.settings.game.farmingMode === "Arcarum Sandbox" ||
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
            bsc.settings.game.farmingMode === "Arcarum Sandbox" ||
            bsc.settings.game.farmingMode === "Generic"
        ) {
            Object.entries(data[bsc.settings.game.farmingMode]).every((obj) => {
                if (obj[0] === mission) {
                    bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, mission: mission, map: obj[1].map } })
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

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Rendering

    const renderCombatScriptSetting = () => {
        return (
            <View>
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
                            if (!e.message.includes("Can't perform a React")) {
                                console.warn(e)
                            }
                            bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, combatScriptName: "", combatScript: [] } })
                        }
                    }}
                />

                <Text style={{ marginBottom: 10, fontSize: 12, opacity: 0.7, color: "black" }}>
                    Combat Scripts need to be located in the root of the storage (/storage/emulated/0/), otherwise the app would not be able to open it due to Android SDK changes. To deselect,
                    cancel/back out of the document picker. If no combat script is selected, Full/Semi Auto is used by default.
                </Text>
            </View>
        )
    }

    const renderFarmingModeSetting = () => {
        return (
            <View>
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

                {bsc.settings.game.farmingMode === "Generic" ? (
                    <View>
                        <Divider style={{ marginBottom: 10 }} />

                        <Text style={{ marginBottom: 10, fontSize: 12, opacity: 0.7, color: "black" }}>
                            {`Selecting this will repeat the current mission on the screen until it finishes the required number of runs. Note that Generic does not provide any navigation.\n\nIt is required that the bot starts on either the Combat screen with the "Attack" button visible, the Loot Collection screen with the "Play Again" button visible, or the Coop Room screen with the "Start" button visible and party already selected.`}
                        </Text>

                        <Divider />
                    </View>
                ) : null}

                {bsc.settings.game.farmingMode === "Event" ? (
                    <Checkbox
                        text={`Enable Incrementation of Location\nby 1`}
                        subtitle="Enable this if the event has its N/H missions at the very top so the bot can correctly select the correct quest. Or in otherwords, enable this if the Event tab in the Special page has 3 'Select' buttons instead of 2."
                        isChecked={bsc.settings.event.enableLocationIncrementByOne}
                        onPress={() => bsc.setSettings({ ...bsc.settings, event: { ...bsc.settings.event, enableLocationIncrementByOne: !bsc.settings.event.enableLocationIncrementByOne } })}
                    />
                ) : null}

                {bsc.settings.game.farmingMode === "Arcarum Sandbox" ? (
                    <Checkbox
                        text="Enable Defender settings"
                        subtitle="Enable additional settings to show up in the Extra Settings page."
                        isChecked={bsc.settings.sandbox.enableDefender}
                        onPress={() => bsc.setSettings({ ...bsc.settings, sandbox: { ...bsc.settings.sandbox, enableDefender: !bsc.settings.arcarum.enableStopOnArcarumBoss } })}
                    />
                ) : null}

                {bsc.settings.game.farmingMode === "Arcarum" ? (
                    <Checkbox
                        text="Enable Stop on Arcarum Boss"
                        subtitle="Enable this option to have the bot stop upon encountering a Arcarum Boss (3-3, 6-3, 9-9)."
                        isChecked={bsc.settings.arcarum.enableStopOnArcarumBoss}
                        onPress={() => bsc.setSettings({ ...bsc.settings, arcarum: { ...bsc.settings.arcarum, enableStopOnArcarumBoss: !bsc.settings.arcarum.enableStopOnArcarumBoss } })}
                    />
                ) : null}

                {bsc.settings.game.farmingMode === "Xeno Clash" ? (
                    <Checkbox
                        text="Enable Selection of Bottom Option"
                        subtitle="Enabling this will select the bottom Xeno Clash option. By default, it selects the top option."
                        isChecked={bsc.settings.xenoClash.selectTopOption}
                        onPress={() => bsc.setSettings({ ...bsc.settings, xenoClash: { ...bsc.settings.xenoClash, selectTopOption: !bsc.settings.xenoClash.selectTopOption } })}
                    />
                ) : null}

                {bsc.settings.game.farmingMode === "Generic" ? (
                    <Checkbox
                        text="Enable Forcing Reload after Attack"
                        subtitle="Enable this option to force Generic Farming Mode to reload after an attack. This does not take into account whether or not the current battle supports reloading after an attack."
                        isChecked={bsc.settings.generic.enableForceReload}
                        onPress={() => bsc.setSettings({ ...bsc.settings, generic: { ...bsc.settings.generic, enableForceReload: !bsc.settings.generic.enableForceReload } })}
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
                        isChecked={bsc.settings.nightmare.enableNightmare}
                        onPress={() => bsc.setSettings({ ...bsc.settings, nightmare: { ...bsc.settings.nightmare, enableNightmare: !bsc.settings.nightmare.enableNightmare } })}
                    />
                ) : null}

                {bsc.settings.game.farmingMode === "Event (Token Drawboxes)" ? (
                    <Checkbox
                        text="Enable Selecting the Bottom Category"
                        subtitle="In the event of the raids being split between 2 categories, the bot selects the top category by default. Enable this to select the bottom category instead."
                        isChecked={bsc.settings.event.selectBottomCategory}
                        onPress={() => bsc.setSettings({ ...bsc.settings, event: { ...bsc.settings.event, selectBottomCategory: !bsc.settings.event.selectBottomCategory } })}
                    />
                ) : null}
            </View>
        )
    }

    const renderItemSetting = () => {
        return (
            <DropDownPicker
                listMode={itemList.length > 10 ? "MODAL" : "SCROLLVIEW"}
                modalProps={{
                    animationType: "slide",
                }}
                style={[styles.picker, { backgroundColor: item !== "" ? "azure" : "pink" }]}
                dropDownContainerStyle={styles.dropdown}
                placeholder="Select Item"
                searchTextInputStyle={{ fontStyle: "italic" }}
                searchable={itemList.length > 10}
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
        )
    }

    const renderMissionSetting = () => {
        return (
            <DropDownPicker
                listMode="SCROLLVIEW"
                style={[styles.picker, { backgroundColor: mission !== "" ? "azure" : "pink" }]}
                dropDownContainerStyle={styles.dropdown}
                placeholder="Select Mission"
                searchTextInputStyle={{ fontStyle: "italic" }}
                dropDownDirection="BOTTOM"
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
        )
    }

    const renderItemAmountSetting = () => {
        return (
            <View>
                <Text style={{ color: "#000" }}>Item Amount:</Text>
                <Picker
                    selectedValue={bsc.settings.game.itemAmount}
                    onValueChange={(value) => bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, itemAmount: value } })}
                    mode="dropdown"
                    style={{ color: "#000" }}
                    dropdownIconColor={"#000"}
                >
                    {[...Array(999 - 1 + 1).keys()]
                        .map((x) => x + 1)
                        .map((value) => {
                            return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                        })}
                </Picker>
            </View>
        )
    }

    const renderSummonSetting = () => {
        return (
            <View>
                {bsc.settings.game.farmingMode !== "Coop" && bsc.settings.game.farmingMode !== "Arcarum" && bsc.settings.game.farmingMode !== "Arcarum Sandbox" ? (
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
            </View>
        )
    }

    const renderGroupPartySettings = () => {
        return (
            <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
                <View style={{ width: Dimensions.get("window").width * 0.3 }}>
                    <Text style={{ color: "#000" }}>Group #:</Text>
                    <Picker
                        selectedValue={bsc.settings.game.groupNumber}
                        onValueChange={(value) => bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, groupNumber: value } })}
                        mode="dropdown"
                        style={{ color: "#000" }}
                        dropdownIconColor={"#000"}
                    >
                        {[...Array(7 - 1 + 1).keys()]
                            .map((x) => x + 1)
                            .map((value) => {
                                return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                            })}
                    </Picker>
                </View>
                <View style={{ width: Dimensions.get("window").width * 0.3 }}>
                    <Text style={{ color: "#000" }}>Party #:</Text>
                    <Picker
                        selectedValue={bsc.settings.game.partyNumber}
                        onValueChange={(value) => bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, partyNumber: value } })}
                        mode="dropdown"
                        style={{ color: "#000" }}
                        dropdownIconColor={"#000"}
                    >
                        {[...Array(6 - 1 + 1).keys()]
                            .map((x) => x + 1)
                            .map((value) => {
                                return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                            })}
                    </Picker>
                </View>
            </View>
        )
    }

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

            <ScrollView nestedScrollEnabled={true} contentContainerStyle={{ flexGrow: 1 }}>
                <View style={{ marginHorizontal: 20 }}>
                    {renderCombatScriptSetting()}

                    <Divider />

                    {renderFarmingModeSetting()}

                    {renderItemSetting()}

                    {renderMissionSetting()}

                    {renderItemAmountSetting()}

                    {renderSummonSetting()}

                    {renderGroupPartySettings()}
                </View>
            </ScrollView>
        </View>
    )
}

export default Settings
