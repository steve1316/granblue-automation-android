import { FC } from "react"
import { StyleSheet, GestureResponderEvent, Alert } from "react-native"
import { Button } from "react-native-elements/dist/buttons/Button"

interface Props {
    title: string
    width?: number | string
    borderRadius?: number
    onPress?: (event: GestureResponderEvent) => void
}

const CustomButton: FC<Props> = ({ title, width = 100, borderRadius = 5, onPress = () => Alert.alert("Pressed!") }) => {
    const styles = StyleSheet.create({
        button: {
            backgroundColor: "rgba(78, 116, 289, 1)",
            borderRadius: borderRadius,
            borderStyle: "solid",
            borderColor: "black",
            borderWidth: 1,
        },
        buttonContainer: {
            width: width,
            borderRadius: borderRadius,
            marginVertical: 10,
            alignSelf: "center",
        },
    })

    return <Button title={title} buttonStyle={styles.button} containerStyle={styles.buttonContainer} raised onPress={onPress} />
}

export default CustomButton
