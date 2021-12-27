import { StyleSheet, View, Text } from "react-native"
import BouncyCheckbox from "react-native-bouncy-checkbox"

const styles = StyleSheet.create({
    root: {
        flex: 1,
        flexDirection: "column",
        justifyContent: "center",
        marginHorizontal: 10,
    },
})

const ExtraSettings = () => {
    return (
        <View style={styles.root}>
            <Text>Hello Extra Settings</Text>
                <BouncyCheckbox
                    size={30}
                    fillColor="red"
                    unfillColor="white"
                    text={"Enable Debug Mode"}
                    iconStyle={{ borderColor: "red" }}
                    textStyle={{
                        textDecorationLine: "none",
                    }}
                    style={{ marginVertical: 10 }}
                    isChecked={debugMode}
                    onPress={(isChecked: boolean) => {
                        setDebugMode(isChecked)
                    }}
                />
        </View>
    )
}

export default ExtraSettings
