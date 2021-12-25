import { StyleSheet, View, Pressable, Alert, Text } from "react-native"
import MessageLog from "../../components/MessageLog"

const styles = StyleSheet.create({
    container: {
        height: "100%",
        flex: 1,
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        marginHorizontal: 10,
    },
    button: {
        alignItems: "center",
        justifyContent: "center",
        paddingVertical: 12,
        paddingHorizontal: 32,
        borderRadius: 4,
        elevation: 5,
        backgroundColor: "rgb(63, 167, 252)",
        marginVertical: 5,
    },
    buttonText: {
        fontSize: 16,
        lineHeight: 21,
        fontWeight: "bold",
        letterSpacing: 0.25,
        color: "white",
    },
})

const Home = () => {
    return (
        <View style={styles.container}>
            <Pressable style={styles.button} onPress={() => Alert.alert("Pressed")}>
                <Text style={styles.buttonText}>Start</Text>
            </Pressable>

            <MessageLog />
        </View>
    )
}

export default Home
