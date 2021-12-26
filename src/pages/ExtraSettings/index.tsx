import { StyleSheet, View, Text } from "react-native"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        backgroundColor: "#fff",
        alignItems: "center",
        justifyContent: "center",
    },
})

const ExtraSettings = () => {
    return (
        <View style={styles.root}>
            <Text>Hello Extra Settings</Text>
        </View>
    )
}

export default ExtraSettings
