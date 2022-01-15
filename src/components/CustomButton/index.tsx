import React, { FC } from "react"
import { Alert, GestureResponderEvent, StyleSheet } from "react-native"
import { Button } from "react-native-elements/dist/buttons/Button"

interface Props {
    title: string
    width?: number | string
    borderRadius?: number
    backgroundColor?: string
    onPress?: (event: GestureResponderEvent) => void
    disabled?: boolean
}

const CustomButton: FC<Props> = ({ title, width = 100, borderRadius = 5, disabled = false, backgroundColor = "rgba(78, 116, 289, 1)", onPress = () => Alert.alert("Pressed!") }) => {
    const styles = StyleSheet.create({
        button: {
            backgroundColor: backgroundColor,
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

    return <Button title={title} disabled={disabled} buttonStyle={styles.button} containerStyle={styles.buttonContainer} raised onPress={onPress} />
}

export default CustomButton
