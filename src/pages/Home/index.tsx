import CustomButton from "../../components/CustomButton"
import MessageLog from "../../components/MessageLog"
import React, { useContext, useEffect, useState } from "react"
import { BotStateContext } from "../../context/BotStateContext"
import { DeviceEventEmitter, StyleSheet, View } from "react-native"
import { NativeModules } from "react-native" // Import native Java module.

const styles = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        marginHorizontal: 10,
    },
})

const Home = () => {
    const { StartModule } = NativeModules

    const [isRunning, setIsRunning] = useState<boolean>(false)

    const bsc = useContext(BotStateContext)

    useEffect(() => {
        DeviceEventEmitter.addListener("MediaProjectionService", (data) => {
            setIsRunning(data["message"] === "Running")
        })
    }, [])

    return (
        <View style={styles.root}>
            {isRunning ? (
                <CustomButton title="Stop" backgroundColor="red" width={200} borderRadius={20} onPress={() => StartModule.stop()} />
            ) : (
                <CustomButton disabled={!bsc.readyStatus} title={bsc.readyStatus ? "Start" : "Not Ready"} width={200} borderRadius={20} onPress={() => StartModule.start()} />
            )}

            <MessageLog />
        </View>
    )
}

export default Home
