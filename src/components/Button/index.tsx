import { StyleSheet, Pressable, Text, StyleProp, ViewStyle, GestureResponderEvent } from "react-native"

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

const Button = ({
    text,
    buttonStyle,
    textStyle,
    onPress,
}: {
    text: string
    buttonStyle?: StyleProp<ViewStyle> | null
    textStyle?: StyleProp<ViewStyle> | null
    onPress?: (event: GestureResponderEvent) => void | null
}) => {
    return (
        <Pressable style={buttonStyle == null ? styles.button : buttonStyle} onPress={onPress}>
            <Text style={textStyle == null ? styles.buttonText : textStyle}>{text}</Text>
        </Pressable>
    )
}

export default Button
