import DeviceInfo from "react-native-device-info"
import React, { useContext } from "react"
import { MessageLogContext } from "../../context/MessageLogContext"
import { ScrollView, StyleSheet, Text, View } from "react-native"

const styles = StyleSheet.create({
    logInnerContainer: {
        height: "90%",
        width: "100%",
        backgroundColor: "#2f2f2f",
        borderStyle: "solid",
        borderRadius: 25,
        marginBottom: 10,
        elevation: 10,
    },
    logText: {
        color: "white",
        margin: 20,
        fontSize: 8,
    },
})

const MessageLog = () => {
    const mlc = useContext(MessageLogContext)

    const introMessage = `****************************************\nWelcome to ${DeviceInfo.getApplicationName()} v${DeviceInfo.getVersion()}\n****************************************\nInstructions\n----------------\nNote: The START button is disabled until the following steps are followed through.\n
    1. Have your game window's Bottom Menu visible. Set the game window size set to the second "notch". 
    2. Go to the Settings Page of the bot and fill out the sections.
    3. You can now head back to the Home Page of the bot and click START.\n****************************************\n`

    return (
        <View style={styles.logInnerContainer}>
            <ScrollView>
                <Text style={styles.logText}>{introMessage + mlc.messageLog.join("\r")}</Text>
            </ScrollView>
        </View>
    )
}

export default MessageLog
