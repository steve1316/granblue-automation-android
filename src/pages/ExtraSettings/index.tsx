import { StyleSheet, View, Text } from "react-native"

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#fff",
        alignItems: "center",
        justifyContent: "center",
    },
})

const ExtraSettings = () => {
    return (
        <View style={styles.container}>
            <Text>Hello Extra Settings</Text>
        </View>
    )
}

export default ExtraSettings
