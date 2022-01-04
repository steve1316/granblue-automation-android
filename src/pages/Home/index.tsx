import React from "react"
import { StyleSheet, View } from "react-native"
import CustomButton from "../../components/CustomButton"

import MessageLog from "../../components/MessageLog"

// Import native Java module.
import { NativeModules } from "react-native"

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

    return (
        <View style={styles.root}>
            <CustomButton title="Start" width={200} borderRadius={20} onPress={() => StartModule.start()} />
            <MessageLog />
        </View>
    )
}

export default Home
