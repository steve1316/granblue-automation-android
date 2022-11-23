import Checkbox from "../../components/Checkbox"
import CustomButton from "../../components/CustomButton"
import data from "../../data/data.json"
import DocumentPicker from "react-native-document-picker"
import Ionicons from "react-native-vector-icons/Ionicons"
import React, { useContext, useEffect, useState } from "react"
import RNFS from "react-native-fs"
import SnackBar from "rn-snackbar-component"
import TransferList from "../../components/TransferList"
import { BotStateContext } from "../../context/BotStateContext"
import { Dimensions, Modal, ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native"
import { Divider } from "react-native-elements"
import { Picker } from "@react-native-picker/picker"
import CustomDropdown from "../../components/CustomDropdown"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "center",
        margin: 10,
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

export interface CombatScript {
    name: string
    script: string[]
}

const Settings = () => {
    const [modalOpen, setModalOpen] = useState<boolean>(false)
    const [snackbarOpen, setSnackbarOpen] = useState<boolean>(false)

    const [itemList, setItemList] = useState<string[]>([])
    const [missionList, setMissionList] = useState<string[]>([])

    const bsc = useContext(BotStateContext)

    const farmingModes = [
        "Quest",
        "Special",
        "Coop",
        "Raid",
        "Event",
        "Event (Token Drawboxes)",
        "Rise of the Beasts",
        "Guild Wars",
        "Dread Barrage",
        "Proving Grounds",
        "Xeno Clash",
        "Arcarum",
        "Arcarum Sandbox",
        "Generic",
    ]

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Callbacks

    // Populate the item list after selecting the Farming Mode.
    useEffect(() => {
        let newItemList: string[] = []
        let fullItemList: string[] = []

        if (
            bsc.settings.game.farmingMode !== "" &&
            (bsc.settings.game.farmingMode === "Quest" ||
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
                bsc.settings.game.farmingMode === "Generic")
        ) {
            if (bsc.settings.game.mission !== "") {
                // Filter items based on the mission selected.
                Object.entries(data[bsc.settings.game.farmingMode]).forEach((missionObj) => {
                    if (missionObj[0] === bsc.settings.game.mission) {
                        newItemList = newItemList.concat(missionObj[1].items)
                    }
                })
            } else {
                // Display all items.
                Object.values(data[bsc.settings.game.farmingMode]).forEach((tempObj) => {
                    console.log
                    fullItemList = fullItemList.concat(tempObj.items)
                })
            }
        }

        if (newItemList !== itemList) {
            if (newItemList.length > 0) {
                const filteredNewItemList = Array.from(new Set(newItemList))
                setItemList(filteredNewItemList)
            } else {
                const filteredFullItemList = Array.from(new Set(fullItemList))
                setItemList(filteredFullItemList)
            }
        }
    }, [bsc.settings.game.farmingMode, bsc.settings.game.mission])

    // Populate the mission list after selecting the item.
    useEffect(() => {
        let newMissionList: string[] = []
        let fullMissionList: string[] = []

        if (
            bsc.settings.game.farmingMode !== "" &&
            (bsc.settings.game.farmingMode === "Quest" ||
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
                bsc.settings.game.farmingMode === "Generic")
        ) {
            Object.entries(data[bsc.settings.game.farmingMode]).forEach((obj) => {
                if (obj[1].items.indexOf(bsc.settings.game.item) !== -1) {
                    newMissionList = newMissionList.concat(obj[0])
                } else {
                    fullMissionList = fullMissionList.concat(obj[0])
                }
            })
        }

        if (newMissionList !== missionList) {
            if (newMissionList.length > 0) {
                const filteredNewMissionList = Array.from(new Set(newMissionList))
                setMissionList(filteredNewMissionList)
            } else {
                const filteredFullMissionList = Array.from(new Set(fullMissionList))
                setMissionList(filteredFullMissionList)
            }
        }
    }, [bsc.settings.game.item])

    // Fetch the map that corresponds to the selected mission if applicable. Not for Coop.
    useEffect(() => {
        if (
            bsc.settings.game.farmingMode !== "" &&
            (bsc.settings.game.farmingMode === "Quest" ||
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
                bsc.settings.game.farmingMode === "Generic")
        ) {
            Object.entries(data[bsc.settings.game.farmingMode]).every((obj) => {
                if (obj[0] === bsc.settings.game.mission) {
                    bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, map: obj[1].map } })
                    return false
                } else {
                    return true
                }
            })
        }
    }, [bsc.settings.game.mission])

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
                <CustomDropdown
                    placeholder="Select Farming Mode"
                    data={farmingModes}
                    value={bsc.settings.game.farmingMode}
                    onSelect={(value) =>
                        // In addition, also reset selected Item and Mission.
                        bsc.setSettings({
                            ...bsc.settings,
                            game: { ...bsc.settings.game, farmingMode: value, item: "", mission: "", map: "" },
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
                    }
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
            <CustomDropdown
                placeholder="Select Item"
                data={itemList}
                search={itemList.length > 10}
                value={bsc.settings.game.item}
                onSelect={(value) => bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, item: value } })}
            />
        )
    }

    const renderMissionSetting = () => {
        return (
            <CustomDropdown
                placeholder="Select Mission"
                data={missionList}
                value={bsc.settings.game.mission}
                onSelect={(value) => bsc.setSettings({ ...bsc.settings, game: { ...bsc.settings.game, mission: value } })}
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
