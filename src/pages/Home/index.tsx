import { StyleSheet, View, Alert } from "react-native"
import { Button } from "react-native-elements/dist/buttons/Button"

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
            <Button
                title="Start"
                buttonStyle={{
                    backgroundColor: "rgba(78, 116, 289, 1)",
                    borderRadius: 3,
                }}
                containerStyle={{
                    width: 100,
                    marginVertical: 10,
                }}
                raised
                onPress={() => Alert.alert("Pressed!")}
            />
            <MessageLog />
        </View>
    )
}

export default Home
