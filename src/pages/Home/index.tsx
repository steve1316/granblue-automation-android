import { StyleSheet, View, Text } from "react-native"

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: "#fff",
        alignItems: "center",
        justifyContent: "center",
    },
})

const Home = () => {
    return (
        <View style={styles.container}>
            <Text>Hello Home</Text>
        </View>
    )
}

export default Home
