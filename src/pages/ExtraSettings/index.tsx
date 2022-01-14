import React, { useState, useEffect, useContext } from "react"
import { StyleSheet, View, ScrollView, Dimensions, Modal, TouchableOpacity } from "react-native"
import { Divider, Input, Text } from "react-native-elements"
import Checkbox from "../../components/Checkbox"
import BouncyCheckbox from "react-native-bouncy-checkbox"
import TitleDivider from "../../components/TitleDivider"
import { Slider, RangeSlider } from "@sharcoux/slider"
import NumericInput from "react-native-numeric-input"
import { Picker } from "@react-native-picker/picker"
import RNFS from "react-native-fs"
import DocumentPicker from "react-native-document-picker"
import { BotStateContext } from "../../context/BotStateContext"
import { CombatScript } from "../Settings"
import CustomButton from "../../components/CustomButton"
import TransferList from "../../components/TransferList"
import { MessageLogContext } from "../../context/MessageLogContext"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "center",
        margin: 10,
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

const ExtraSettings = () => {
    const [modalOpen, setModalOpen] = useState<boolean>(false)
    const [firstTime, setFirstTime] = useState<boolean>(true)

    // Twitter Settings
    const [twitterAPIKey, setTwitterAPIKey] = useState<string>("")
    const [twitterAPIKeySecret, setTwitterAPIKeySecret] = useState<string>("")
    const [twitterAccessToken, setTwitterAccessToken] = useState<string>("")
    const [twitterAccessTokenSecret, setTwitterAccessTokenSecret] = useState<string>("")

    // Discord Settings
    const [enableDiscord, setEnableDiscord] = useState<boolean>(false)
    const [discordToken, setDiscordToken] = useState<string>("")
    const [discordUserID, setDiscordUserID] = useState<string>("")

    // Configuration Settings
    const [debugMode, setDebugMode] = useState<boolean>(false)
    const [enableDelayBetweenRuns, setEnableDelayBetweenRuns] = useState<boolean>(false)
    const [delayBetweenRuns, setDelayBetweenRuns] = useState<number>(5)
    const [randomizedDelayBetweenRuns, setRandomizedDelayBetweenRuns] = useState<[number, number]>([5, 15])
    const [enableRandomizedDelayBetweenRuns, setEnableRandomizedDelayBetweenRuns] = useState<boolean>(false)
    const [enableAutoExitRaid, setEnableAutoExitRaid] = useState<boolean>(false)
    const [autoExitRaidMinutes, setAutoExitRaidsMinutes] = useState<number>(1)
    const [enableNoTimeout, setEnableNoTimeout] = useState<boolean>(false)
    const [enableDelayTap, setEnableDelayTap] = useState<boolean>(false)
    const [delayTapMilliseconds, setDelayTapMilliseconds] = useState<number>(1000)

    // Nightmare Settings
    const [enableCustomNightmareSettings, setEnableCustomNightmareSettings] = useState<boolean>(false)
    const [nightmareCombatScript, setNightmareCombatScript] = useState<CombatScript>({ name: "", script: [] })
    const [nightmareGroupNumber, setNightmareGroupNumber] = useState<number>(1)
    const [nightmarePartyNumber, setNightmarePartyNumber] = useState<number>(1)

    // Device Settings
    const [confidence, setConfidence] = useState<number>(80)
    const [confidenceAll, setConfidenceAll] = useState<number>(80)
    const [customScale, setCustomScale] = useState<number>(1.0)
    const [enableTestForHomeScreen, setEnableTestForHomeScreen] = useState<boolean>(false)

    const bsc = useContext(BotStateContext)
    const mlc = useContext(MessageLogContext)

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    // Callbacks

    // Load state from context to local.
    useEffect(() => {
        setTwitterAPIKey(bsc.settings.twitter.twitterAPIKey)
        setTwitterAPIKeySecret(bsc.settings.twitter.twitterAPIKeySecret)
        setTwitterAccessToken(bsc.settings.twitter.twitterAccessToken)
        setTwitterAccessTokenSecret(bsc.settings.twitter.twitterAccessTokenSecret)
        setEnableDiscord(bsc.settings.discord.enableDiscordNotifications)
        setDiscordToken(bsc.settings.discord.discordToken)
        setDiscordUserID(bsc.settings.discord.discordUserID)
        setDebugMode(bsc.settings.game.debugMode)
        setEnableDelayBetweenRuns(bsc.settings.configuration.enableDelayBetweenRuns)
        setDelayBetweenRuns(bsc.settings.configuration.delayBetweenRuns)
        setEnableRandomizedDelayBetweenRuns(bsc.settings.configuration.enableRandomizedDelayBetweenRuns)
        setRandomizedDelayBetweenRuns([bsc.settings.configuration.delayBetweenRunsLowerBound, bsc.settings.configuration.delayBetweenRunsUpperBound])
        setEnableAutoExitRaid(bsc.settings.raid.enableAutoExitRaid)
        setAutoExitRaidsMinutes(bsc.settings.raid.timeAllowedUntilAutoExitRaid)
        setEnableNoTimeout(bsc.settings.raid.enableNoTimeout)
        setEnableDelayTap(bsc.settings.android.enableDelayTap)
        setDelayTapMilliseconds(bsc.settings.android.delayTapMilliseconds)
        setEnableCustomNightmareSettings(bsc.settings.nightmare.enableCustomNightmareSettings)
        setNightmareCombatScript({ name: bsc.settings.nightmare.nightmareCombatScriptName, script: bsc.settings.nightmare.nightmareCombatScript })
        setNightmareGroupNumber(bsc.settings.nightmare.nightmareGroupNumber)
        setNightmarePartyNumber(bsc.settings.nightmare.nightmarePartyNumber)
        setConfidence(bsc.settings.android.confidence)
        setConfidenceAll(bsc.settings.android.confidenceAll)
        setCustomScale(bsc.settings.android.customScale)
        setEnableTestForHomeScreen(bsc.settings.android.enableTestForHomeScreen)
        setFirstTime(false)
    }, [])

    // Save settings to context state.
    useEffect(() => {
        if (!firstTime) {
            mlc.setMessageLog([])
            mlc.setAsyncMessages([])

            bsc.setSettings({
                ...bsc.settings,
                game: {
                    ...bsc.settings.game,
                    debugMode: debugMode,
                },
                twitter: {
                    ...bsc.settings.twitter,
                    twitterAPIKey: twitterAPIKey,
                    twitterAPIKeySecret: twitterAPIKeySecret,
                    twitterAccessToken: twitterAccessToken,
                    twitterAccessTokenSecret: twitterAccessTokenSecret,
                },
                discord: {
                    ...bsc.settings.discord,
                    discordToken: discordToken,
                    discordUserID: discordUserID,
                },
                configuration: {
                    ...bsc.settings.configuration,
                    enableDelayBetweenRuns: enableDelayBetweenRuns,
                    delayBetweenRuns: delayBetweenRuns,
                    enableRandomizedDelayBetweenRuns: enableRandomizedDelayBetweenRuns,
                    delayBetweenRunsLowerBound: randomizedDelayBetweenRuns[0],
                    delayBetweenRunsUpperBound: randomizedDelayBetweenRuns[1],
                },
                raid: {
                    ...bsc.settings.raid,
                    enableAutoExitRaid: enableAutoExitRaid,
                    timeAllowedUntilAutoExitRaid: autoExitRaidMinutes,
                    enableNoTimeout: enableNoTimeout,
                },
                nightmare: {
                    ...bsc.settings.nightmare,
                    enableCustomNightmareSettings: enableCustomNightmareSettings,
                    nightmareCombatScriptName: nightmareCombatScript.name,
                    nightmareCombatScript: nightmareCombatScript.script,
                    nightmareGroupNumber: nightmareGroupNumber,
                    nightmarePartyNumber: nightmarePartyNumber,
                },
                android: {
                    ...bsc.settings.android,
                    enableDelayTap: enableDelayTap,
                    delayTapMilliseconds: delayTapMilliseconds,
                    confidence: confidence,
                    confidenceAll: confidenceAll,
                    customScale: customScale,
                    enableTestForHomeScreen: enableTestForHomeScreen,
                },
            })
        }
    }, [
        twitterAPIKey,
        twitterAPIKeySecret,
        twitterAccessToken,
        twitterAccessTokenSecret,
        discordToken,
        discordUserID,
        enableDelayBetweenRuns,
        delayBetweenRuns,
        enableRandomizedDelayBetweenRuns,
        randomizedDelayBetweenRuns,
        enableAutoExitRaid,
        autoExitRaidMinutes,
        enableNoTimeout,
        enableCustomNightmareSettings,
        nightmareCombatScript,
        nightmareGroupNumber,
        nightmarePartyNumber,
        enableDelayTap,
        delayTapMilliseconds,
        confidence,
        confidenceAll,
        customScale,
        enableTestForHomeScreen,
    ])

    const renderNightmareSettings = () => {
        if (
            bsc.settings.nightmare.enableNightmare &&
            (bsc.settings.game.farmingMode === "Special" ||
                bsc.settings.game.farmingMode === "Event" ||
                bsc.settings.game.farmingMode === "Event (Token Drawboxes)" ||
                bsc.settings.game.farmingMode === "Xeno Clash" ||
                bsc.settings.game.farmingMode === "Rise of the Beasts")
        ) {
            var title: string = ""
            if (bsc.settings.game.farmingMode === "Special") {
                title = "Dimensional Halo"
            } else if (bsc.settings.game.farmingMode === "Rise of the Beasts") {
                title = "Extreme+"
            } else {
                title = "Nightmare"
            }

            return (
                <View>
                    <TitleDivider title={`${title} Settings`} hasIcon={true} iconName="sword-cross" iconColor="#000" />

                    <Text style={{ marginBottom: 10, fontSize: 12, opacity: 0.7 }}>If none of these settings are changed, then the bot will reuse the settings for the Farming Mode.</Text>

                    <Checkbox
                        text={`Enable Custom Settings for ${title}`}
                        subtitle={`Enable customizing individual settings for ${title}`}
                        state={enableCustomNightmareSettings}
                        updateState={setEnableCustomNightmareSettings}
                    />

                    <CustomButton
                        title="Select Nightmare Combat Script"
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

                                        bsc.setSettings({
                                            ...bsc.settings,
                                            nightmare: { ...bsc.settings.nightmare, nightmareCombatScriptName: pickerResult.name, nightmareCombatScript: newCombatScript },
                                        })
                                    })
                                }
                            } catch (e) {
                                console.warn(e)
                                bsc.setSettings({ ...bsc.settings, nightmare: { ...bsc.settings.nightmare, nightmareCombatScriptName: "", nightmareCombatScript: [] } })
                            }
                        }}
                    />

                    <Divider />

                    <View>
                        <CustomButton title="Select Nightmare Support Summon(s)" width={"100%"} onPress={() => setModalOpen(true)} />
                        <Modal transparent={true} animationType="fade" statusBarTranslucent={true} visible={modalOpen} onRequestClose={() => setModalOpen(false)}>
                            <View style={styles.modal}>
                                <TouchableOpacity style={styles.outsideModal} onPress={() => setModalOpen(false)} />
                                <View style={styles.componentContainer}>
                                    <TransferList isNightmare={true} />
                                </View>
                            </View>
                        </Modal>
                    </View>

                    <View style={{ flexDirection: "row", justifyContent: "space-between" }}>
                        <View style={{ width: Dimensions.get("window").width * 0.3 }}>
                            <Text>Group #:</Text>
                            <Picker selectedValue={nightmareGroupNumber} onValueChange={(value) => setNightmareGroupNumber(value)} mode="dropdown">
                                {[...Array(7 - 1 + 1).keys()]
                                    .map((x) => x + 1)
                                    .map((value) => {
                                        return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                                    })}
                            </Picker>
                        </View>
                        <View style={{ width: Dimensions.get("window").width * 0.3 }}>
                            <Text>Party #:</Text>
                            <Picker selectedValue={nightmarePartyNumber} onValueChange={(value) => setNightmarePartyNumber(value)} mode="dropdown">
                                {[...Array(6 - 1 + 1).keys()]
                                    .map((x) => x + 1)
                                    .map((value) => {
                                        return <Picker.Item key={`key-${value}`} label={`${value}`} value={value} />
                                    })}
                            </Picker>
                        </View>
                    </View>
                </View>
            )
        } else {
            return null
        }
    }

    return (
        <View style={styles.root}>
            <ScrollView>
                {renderNightmareSettings()}

                <TitleDivider
                    title="Twitter Settings"
                    subtitle="Please visit the wiki on the GitHub page for instructions on how to get these keys and tokens."
                    hasIcon={true}
                    iconName="twitter"
                    iconColor="#1da1f2"
                />
                <Input label="Twitter API Key" multiline containerStyle={{ marginLeft: -10 }} value={twitterAPIKey} onChangeText={(value: string) => setTwitterAPIKey(value)} />
                <Input label="Twitter API Key Secret" multiline containerStyle={{ marginLeft: -10 }} value={twitterAPIKeySecret} onChangeText={(value: string) => setTwitterAPIKeySecret(value)} />
                <Input label="Twitter Access Token" multiline containerStyle={{ marginLeft: -10 }} value={twitterAccessToken} onChangeText={(value: string) => setTwitterAccessToken(value)} />
                <Input
                    label="Twitter Access Token Secret"
                    multiline
                    containerStyle={{ marginLeft: -10 }}
                    value={twitterAccessTokenSecret}
                    onChangeText={(value: string) => setTwitterAccessTokenSecret(value)}
                />

                <TitleDivider
                    title="Discord Settings"
                    subtitle={`Please visit the wiki on the GitHub page for instructions on how to get the token and user ID.\n\nNote: This does not work on emulators currently.`}
                    hasIcon={true}
                    iconName="discord"
                    iconColor="#7289d9"
                />
                <Checkbox
                    text="Enable Discord Notifications"
                    subtitle="Enable notifications of loot drops and errors encountered by the bot via Discord DMs."
                    state={enableDiscord}
                    updateState={setEnableDiscord}
                />
                {enableDiscord ? (
                    <View>
                        <Input label="Discord Token" multiline containerStyle={{ marginLeft: -10 }} value={discordToken} onChangeText={(value: string) => setDiscordToken(value)} />
                        <Input label="Discord User ID" multiline containerStyle={{ marginLeft: -10 }} value={discordUserID} onChangeText={(value: string) => setDiscordUserID(value)} />
                    </View>
                ) : null}

                <TitleDivider title="Configuration Settings" hasIcon={true} iconName="tune" />
                <Checkbox
                    text="Enable Debug Mode"
                    subtitle={`Enables debugging messages to show up in the log.\n\nIt will also enable saving screenshots to internal storage for debugging purposes. As such, it will increase average image processing time by ~500ms per operation.`}
                    state={debugMode}
                    updateState={setDebugMode}
                />
                <Checkbox
                    text="Enable Auto Exit Raid"
                    subtitle="Enables backing out of a Raid without retreating while under Semi/Full Auto after a certain period of time has passed."
                    state={enableAutoExitRaid}
                    updateState={setEnableAutoExitRaid}
                />
                {enableAutoExitRaid ? (
                    <View>
                        <Text style={{ marginBottom: 10 }}>Max Time Allowed for Semi/Full Auto: {autoExitRaidMinutes} minutes</Text>
                        <NumericInput
                            type="plus-minus"
                            leftButtonBackgroundColor="#eb5056"
                            rightButtonBackgroundColor="#EA3788"
                            rounded
                            valueType="integer"
                            minValue={1}
                            maxValue={15}
                            value={autoExitRaidMinutes}
                            onChange={(value) => setAutoExitRaidsMinutes(value)}
                            containerStyle={{ marginBottom: 10, alignSelf: "center" }}
                            totalWidth={Dimensions.get("screen").width * 0.9}
                            totalHeight={50}
                        />
                    </View>
                ) : null}
                <Checkbox text="Enable No Timeout" subtitle="Enable no timeouts when attempting to farm Raids that appear infrequently." state={enableNoTimeout} updateState={setEnableNoTimeout} />

                {!enableRandomizedDelayBetweenRuns ? (
                    <View>
                        <BouncyCheckbox
                            size={30}
                            fillColor={"red"}
                            unfillColor={"white"}
                            text="Enable Delay Between Runs"
                            iconStyle={{ borderColor: "red" }}
                            textStyle={{
                                textDecorationLine: "none",
                                color: "#000",
                            }}
                            style={{ marginVertical: 10, marginLeft: 2 }}
                            isChecked={enableDelayBetweenRuns}
                            onPress={(checked) => {
                                if (checked && enableRandomizedDelayBetweenRuns) {
                                    setEnableRandomizedDelayBetweenRuns(false)
                                }

                                setEnableDelayBetweenRuns(checked)
                            }}
                        />
                        <Text style={{ marginBottom: 5, marginLeft: 2, fontSize: 12, opacity: 0.7 }}>Enable delay in seconds between runs to serve as a resting period.</Text>
                    </View>
                ) : null}

                {enableDelayBetweenRuns ? (
                    <View>
                        <Text style={{ marginBottom: 10 }}>Delay: {delayBetweenRuns}</Text>
                        <Slider
                            value={delayBetweenRuns}
                            minimumValue={5}
                            maximumValue={60}
                            step={1}
                            onSlidingComplete={(value) => setDelayBetweenRuns(value)}
                            minimumTrackTintColor="black"
                            maximumTrackTintColor="gray"
                            thumbTintColor="red"
                            thumbSize={20}
                            trackHeight={10}
                            style={{ width: "95%", alignSelf: "center", marginBottom: 10 }}
                        />
                    </View>
                ) : null}

                {!enableDelayBetweenRuns ? (
                    <View>
                        <BouncyCheckbox
                            size={30}
                            fillColor={"red"}
                            unfillColor={"white"}
                            text={`Enable Randomized Delay Between\nRuns`}
                            iconStyle={{ borderColor: "red" }}
                            textStyle={{
                                textDecorationLine: "none",
                                color: "#000",
                            }}
                            style={{ marginVertical: 10, marginLeft: 2 }}
                            isChecked={enableRandomizedDelayBetweenRuns}
                            onPress={(checked) => {
                                if (checked && enableDelayBetweenRuns) {
                                    setEnableDelayBetweenRuns(false)
                                }

                                setEnableRandomizedDelayBetweenRuns(checked)
                            }}
                        />
                        <Text style={{ marginBottom: 5, marginLeft: 2, fontSize: 12, opacity: 0.7 }}>Enable randomized delay in seconds between runs to serve as a resting period.</Text>
                    </View>
                ) : null}

                {enableRandomizedDelayBetweenRuns ? (
                    <View>
                        <Text style={{ marginBottom: 10 }}>
                            Delay between {randomizedDelayBetweenRuns[0]}-{randomizedDelayBetweenRuns[1]} seconds
                        </Text>
                        <RangeSlider
                            range={randomizedDelayBetweenRuns}
                            minimumValue={5}
                            maximumValue={60}
                            step={1}
                            minimumRange={1}
                            onSlidingComplete={(values) => setRandomizedDelayBetweenRuns(values)}
                            outboundColor="gray"
                            inboundColor="black"
                            thumbTintColor="red"
                            thumbSize={30}
                            trackHeight={10}
                            style={{ width: "95%", alignSelf: "center", marginBottom: 10 }}
                        />
                    </View>
                ) : null}

                <Checkbox
                    text="Enable Additional Delay Before Tap"
                    subtitle="Enables a range of delay before each tap in milliseconds (ms). The base point will be used to create a range from -100ms to +100ms using it to determine the additional delay."
                    state={enableDelayTap}
                    updateState={setEnableDelayTap}
                />
                {enableDelayTap ? (
                    <View>
                        <Text style={{ marginBottom: 10 }}>Set Base Point for Additional Delay: {delayTapMilliseconds} milliseconds</Text>
                        <Slider
                            value={delayTapMilliseconds}
                            minimumValue={100}
                            maximumValue={5000}
                            step={100}
                            onValueChange={(value) => setDelayTapMilliseconds(value)}
                            minimumTrackTintColor="black"
                            maximumTrackTintColor="gray"
                            thumbTintColor="red"
                            thumbSize={20}
                            trackHeight={10}
                            style={{ width: "95%", alignSelf: "center", marginBottom: 10 }}
                        />
                    </View>
                ) : null}

                <TitleDivider
                    title="Device Settings"
                    subtitle={`Adjust and fine-tune settings related to device setups and image processing optimizations.`}
                    hasIcon={true}
                    iconName="tablet-cellphone"
                />
                <Text style={{ marginBottom: 10 }}>Set Confidence Level: {confidence}%</Text>
                <NumericInput
                    type="plus-minus"
                    leftButtonBackgroundColor="#eb5056"
                    rightButtonBackgroundColor="#EA3788"
                    rounded
                    valueType="integer"
                    minValue={1}
                    maxValue={100}
                    value={confidence}
                    onChange={(value) => setConfidence(value)}
                    containerStyle={{ marginBottom: 10, alignSelf: "center" }}
                    totalWidth={Dimensions.get("screen").width * 0.9}
                    totalHeight={50}
                />
                <Text style={{ marginBottom: 10 }}>Set Confidence Level for Multiple Matching: {confidenceAll}%</Text>
                <NumericInput
                    type="plus-minus"
                    leftButtonBackgroundColor="#eb5056"
                    rightButtonBackgroundColor="#EA3788"
                    rounded
                    valueType="integer"
                    minValue={1}
                    maxValue={100}
                    value={confidenceAll}
                    onChange={(value) => setConfidenceAll(value)}
                    containerStyle={{ marginBottom: 10, alignSelf: "center" }}
                    totalWidth={Dimensions.get("screen").width * 0.9}
                    totalHeight={50}
                />
                <Text>Set Custom Scale: {customScale % 1 === 0 ? `${customScale}.0` : customScale}</Text>
                <Text style={{ marginBottom: 10, fontSize: 12, opacity: 0.7 }}>
                    Set the scale at which to resize existing image assets to match what would be shown on your device. Internally supported are 720p, 1080p, 1600p (Portrait) and 2560p (Landscape)
                    mode.
                </Text>
                <NumericInput
                    type="plus-minus"
                    leftButtonBackgroundColor="#eb5056"
                    rightButtonBackgroundColor="#EA3788"
                    rounded
                    valueType="real"
                    minValue={0.1}
                    maxValue={5.0}
                    step={0.1}
                    value={customScale}
                    onChange={(value) => setCustomScale(value)}
                    containerStyle={{ marginBottom: 10, alignSelf: "center" }}
                    totalWidth={Dimensions.get("screen").width * 0.9}
                    totalHeight={50}
                />
                <Checkbox
                    text="Enable Test for Home Screen"
                    subtitle={`Enables test for getting to the Home screen instead of the regular bot process. If the test fails, then it will run a different test to find which scale is appropriate for your device.\n\nUseful for troubleshooting working confidences and scales for device compatibility.`}
                    state={enableTestForHomeScreen}
                    updateState={setEnableTestForHomeScreen}
                />
            </ScrollView>
        </View>
    )
}

export default ExtraSettings
