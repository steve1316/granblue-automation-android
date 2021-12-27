import { useState } from "react"
import { StyleSheet, View, ScrollView } from "react-native"
import BouncyCheckbox from "react-native-bouncy-checkbox"
import { Input, Text } from "react-native-elements"
import TitleDivider from "../../components/TitleDivider"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "center",
        marginHorizontal: 10,
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

    return (
        <View style={styles.root}>
            <ScrollView>
                <BouncyCheckbox
                    size={30}
                    fillColor="red"
                    unfillColor="white"
                    text={"Enable Debug Mode"}
                    iconStyle={{ borderColor: "red" }}
                    textStyle={{
                        textDecorationLine: "none",
                    }}
                    style={{ marginVertical: 10 }}
                    isChecked={debugMode}
                    onPress={(isChecked: boolean) => {
                        setDebugMode(isChecked)
                    }}
                />

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
                    subtitle="Please visit the wiki on the GitHub page for instructions on how to get the token and user ID."
                    hasIcon={true}
                    iconName="discord"
                    iconColor="#7289d9"
                />
                <Input label="Discord Token" multiline value={discordToken} onChangeText={(value: string) => setDiscordToken(value)} />
                <Input label="Discord User ID" multiline value={discordUserID} onChangeText={(value: string) => setDiscordUserID(value)} />
            </ScrollView>
        </View>
    )
}

export default ExtraSettings
