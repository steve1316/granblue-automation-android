import BouncyCheckbox from "react-native-bouncy-checkbox"
import React, { FC } from "react"
import { StyleSheet, View } from "react-native"
import { Text } from "react-native-elements"

interface Props {
    text?: string
    subtitle?: string
    iconSize?: number
    iconBorderColor?: string
    iconCheckedColor?: string
    iconUncheckedColor?: string
    isChecked: boolean
    onPress?: (checked: boolean) => void
}

const CustomCheckbox: FC<Props> = ({ text = "", subtitle = "", iconSize = 30, iconBorderColor = "red", iconCheckedColor = "red", iconUncheckedColor = "white", isChecked, onPress }) => {
    const styles = StyleSheet.create({
        checkboxContainer: {
            marginVertical: 5,
            marginLeft: 2,
        },
        subtitle: {
            marginBottom: 5,
            marginLeft: 2,
            fontSize: 12,
            opacity: 0.7,
        },
    })

    return (
        <View>
            <BouncyCheckbox
                size={iconSize}
                fillColor={iconCheckedColor}
                unfillColor={iconUncheckedColor}
                text={text}
                iconStyle={{ borderColor: iconBorderColor }}
                textStyle={{
                    textDecorationLine: "none",
                    color: "#000",
                }}
                style={styles.checkboxContainer}
                isChecked={isChecked}
                disableBuiltInState={true}
                onPress={onPress}
            />
            {subtitle !== "" ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
        </View>
    )
}

export default CustomCheckbox
