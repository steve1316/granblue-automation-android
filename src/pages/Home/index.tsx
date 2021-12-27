import { StyleSheet, View } from "react-native"
import CustomButton from "../../components/CustomButton"

import MessageLog from "../../components/MessageLog"

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
    return (
        <View style={styles.root}>
            <CustomButton title="Start" width={200} borderRadius={20} />
            <MessageLog />
        </View>
    )
}

export default Home
