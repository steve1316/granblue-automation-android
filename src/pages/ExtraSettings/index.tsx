import { useState, useEffect } from "react"
import { StyleSheet, View, ScrollView } from "react-native"
import { Input, Text } from "react-native-elements"
import Checkbox from "../../components/Checkbox"
import BouncyCheckbox from "react-native-bouncy-checkbox"
import TitleDivider from "../../components/TitleDivider"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "center",
        margin: 10,
    },
})

const ExtraSettings = () => {
    const [debugMode, setDebugMode] = useState<boolean>(false)
    const [twitterAPIKey, setTwitterAPIKey] = useState<string>("")
    const [twitterAPIKeySecret, setTwitterAPIKeySecret] = useState<string>("")
    const [twitterAccessToken, setTwitterAccessToken] = useState<string>("")
    const [twitterAccessTokenSecret, setTwitterAccessTokenSecret] = useState<string>("")
    const [discordToken, setDiscordToken] = useState<string>("")
    const [discordUserID, setDiscordUserID] = useState<string>("")
    const [enableDelayBetweenRuns, setEnableDelayBetweenRuns] = useState<boolean>(false)
    const [enableRandomizedDelayBetweenRuns, setEnableRandomizedDelayBetweenRuns] = useState<boolean>(false)

    return (
        <View style={styles.root}>
            <ScrollView>
                <TitleDivider
                    title="Twitter Settings"
                    subtitle="Please visit the wiki on the GitHub page for instructions on how to get these keys and tokens."
                    hasIcon={true}
                    iconName="twitter"
                    iconColor="#1da1f2"
                />
                <Input label="Twitter API Key" multiline value={twitterAPIKey} onChangeText={(value: string) => setTwitterAPIKey(value)} />
                <Input label="Twitter API Key Secret" multiline value={twitterAPIKeySecret} onChangeText={(value: string) => setTwitterAPIKeySecret(value)} />
                <Input label="Twitter Access Token" multiline value={twitterAccessToken} onChangeText={(value: string) => setTwitterAccessToken(value)} />
                <Input label="Twitter Access Token Secret" multiline value={twitterAccessTokenSecret} onChangeText={(value: string) => setTwitterAccessTokenSecret(value)} />

                <TitleDivider
                    title="Discord Settings"
                    subtitle={`Please visit the wiki on the GitHub page for instructions on how to get the token and user ID.\n\nNote: This does not work on emulators.`}
                    hasIcon={true}
                    iconName="discord"
                    iconColor="#7289d9"
                />
                <Input label="Discord Token" multiline containerStyle={{ marginLeft: -10 }} value={discordToken} onChangeText={(value: string) => setDiscordToken(value)} />
                <Input label="Discord User ID" multiline containerStyle={{ marginLeft: -10 }} value={discordUserID} onChangeText={(value: string) => setDiscordUserID(value)} />

                <TitleDivider title="Configuration Settings" hasIcon={true} iconName="tune" />
                <Checkbox text="Enable Debug Mode" subtitle="Enables debugging messages to show up in the log." state={debugMode} updateState={setDebugMode} />

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
                        <Text style={{ marginBottom: 5 }}>Enable delay in seconds between runs to serve as a resting period.</Text>
                    </View>
                ) : null}

                {!enableDelayBetweenRuns ? (
                    <View>
                        <BouncyCheckbox
                            size={30}
                            fillColor={"red"}
                            unfillColor={"white"}
                            text="Enable Randomized Delay Between Runs"
                            iconStyle={{ borderColor: "red" }}
                            textStyle={{
                                textDecorationLine: "none",
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
                        <Text style={{ marginBottom: 5 }}>Enable randomized delay in seconds between runs to serve as a resting period.</Text>
                    </View>
                ) : null}
            </ScrollView>
        </View>
    )
}

export default ExtraSettings
