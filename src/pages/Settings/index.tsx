import { useContext, useEffect, useState } from "react"
import { StyleSheet, View, Text, ScrollView, Dimensions, Modal, TouchableOpacity } from "react-native"
import DropDownPicker from "react-native-dropdown-picker"
import { Divider } from "react-native-elements"
import { Button } from "react-native-elements/dist/buttons/Button"
import DocumentPicker, { DirectoryPickerResponse, DocumentPickerResponse } from "react-native-document-picker"
import data from "../../data/data.json"
import { BotStateContext } from "../../context/BotStateContext"
import { Picker } from "@react-native-picker/picker"
import TransferList from "../../components/TransferList"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        justifyContent: "center",
    },
    picker: {
        marginVertical: 20,
    },
    dropdown: {
        marginTop: 20,
    },
})

interface Item {
    label: string
    value: string
}

const Settings = () => {
    const [open, setOpen] = useState<boolean>(false)
    const [open2, setOpen2] = useState<boolean>(false)
    const [open3, setOpen3] = useState<boolean>(false)
    const [modalOpen, setModalOpen] = useState<boolean>(false)

    const [itemList, setItemList] = useState<Item[]>([])
    const [missionList, setMissionList] = useState<Item[]>([])

    const [farmingMode, setFarmingMode] = useState<string>("")
    const [item, setItem] = useState<string>("")
    const [mission, setMission] = useState<string>("")
    const [itemAmount, setItemAmount] = useState<number>(1)

    const [result, setResult] = useState<Array<DocumentPickerResponse> | DirectoryPickerResponse | undefined | null>()

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

    const closeAllPickers = () => {
        setOpen(false)
        setOpen2(false)
    }

    // useEffect(() => {
    //     console.log(JSON.stringify(result, null, 2))
    // }, [result])

    //////////////////////////////////////////////////
    // Callbacks to save current settings.
    useEffect(() => {
        // Reset selected Item and Mission.
        bsc.setSettings({
            ...bsc.settings,
            game: { ...bsc.settings.game, farmingMode: farmingMode, item: "", mission: "", map: "" },
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

        // Reset Item and Mission in local state.
        setItem("")
        setMission("")
    }, [farmingMode])

    useEffect(() => {
        bsc.setSettings({
            ...bsc.settings,
            game: { ...bsc.settings.game, farmingMode: farmingMode, item: item, mission: mission, map: "" },
        })
    }, [item, mission])

    useEffect(() => {
        console.log("Item populate callback is called")
        populateItemList()

        // Reset Mission in local state.
        setMission("")
    }, [farmingMode])

    useEffect(() => {
        console.log("Mission populate callback is called")
        populateMissionList()
    }, [item])

    const populateItemList = () => {
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

        const filteredNewItemList = newItemList.filter((v, i, a) => a.findIndex((t) => t.label === v.label) === i)
        setItemList(filteredNewItemList)
    }

    const populateMissionList = () => {
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
                if (obj[1].items.indexOf(item) !== -1) {
                    newMissionList = newMissionList.concat({ label: obj[0], value: obj[0] })
                }
            })
        } else {
            Object.entries(data["Coop"]).forEach((obj) => {
                if (obj[1].items.indexOf(item) !== -1) {
                    newMissionList = newMissionList.concat({ label: obj[0], value: obj[0] })
                }
            })
        }

        const filteredNewMissionList = Array.from(new Set(newMissionList))
        setMissionList(filteredNewMissionList)
    }

    return (
        <View style={styles.root}>
            <ScrollView nestedScrollEnabled={true}>
                <View style={{ height: 1000, marginHorizontal: 20 }}>
                    {/* <Button
                        title="Select Combat Script"
                        buttonStyle={{
                            backgroundColor: "rgba(78, 116, 289, 1)",
                            borderRadius: 3,
                        }}
                        containerStyle={{
                            width: 100,
                            marginHorizontal: 50,
                            marginVertical: 10,
                        }}
                        raised
                        onPress={async () => {
                            try {
                                const pickerResult = await DocumentPicker.pickSingle({
                                    presentationStyle: "fullScreen",
                                })
                                setResult([pickerResult])
                            } catch (e) {
                                console.error(e)
                            }
                        }}
                    /> */}

                    <Divider />

                    <DropDownPicker
                        listMode="SCROLLVIEW"
                        style={styles.picker}
                        dropDownContainerStyle={styles.dropdown}
                        placeholder="Select Farming Mode"
                        searchTextInputStyle={{ fontStyle: "italic" }}
                        items={farmingModes}
                        open={open}
                        setOpen={(flag) => {
                            closeAllPickers()
                            setOpen(flag)
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
                            open={open2}
                            setOpen={(flag) => {
                                closeAllPickers()
                                setOpen2(flag)
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
                            open={open3}
                            setOpen={(flag) => {
                                closeAllPickers()
                                setOpen3(flag)
                            }}
                            value={mission}
                            setValue={setMission}
                            zIndex={9997}
                        />
                    ) : null}

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

                    <Button
                        title="Select Summons"
                        buttonStyle={{
                            backgroundColor: "rgba(78, 116, 289, 1)",
                            borderRadius: 3,
                        }}
                        containerStyle={{
                            alignSelf: "center",
                        }}
                        raised
                        onPress={() => setModalOpen(true)}
                    />
                    <Modal transparent={true} animationType="fade" statusBarTranslucent={true} visible={modalOpen} onRequestClose={() => setModalOpen(false)}>
                        <View
                            style={{
                                flex: 1,
                                flexDirection: "column",
                                justifyContent: "center",
                                alignItems: "center",
                                backgroundColor: "rgba(80,80,80,0.3)",
                            }}
                        >
                            <TouchableOpacity style={{ position: "absolute", height: "100%", width: "100%" }} onPress={() => setModalOpen(false)} />
                            <View
                                style={{
                                    width: Dimensions.get("window").width * 0.7,
                                    height: Dimensions.get("window").height * 0.9,
                                }}
                            >
                                <TransferList isNightmare={false} />
                            </View>
                        </View>
                    </Modal>
                </View>
            </ScrollView>
        </View>
    )
}

export default Settings
