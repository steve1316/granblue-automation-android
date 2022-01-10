import React, { useContext } from "react"
import { StyleSheet, View } from "react-native"
import CustomButton from "../../components/CustomButton"

import MessageLog from "../../components/MessageLog"

// Import native Java module.
import { NativeModules } from "react-native"
import { BotStateContext } from "../../context/BotStateContext"

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

    const bsc = useContext(BotStateContext)

    return (
        <View style={styles.root}>
            <CustomButton disabled={!bsc.readyStatus} title={bsc.readyStatus ? "Start" : "Not Ready"} width={200} borderRadius={20} onPress={() => StartModule.start()} />
            <MessageLog />
        </View>
    )
}

export default Home
