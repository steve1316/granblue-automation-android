import React, { FC } from "react"
import { StyleSheet, View } from "react-native"
import BouncyCheckbox from "react-native-bouncy-checkbox"
import { Text } from "react-native-elements"

interface Props {
    text?: string
    subtitle?: string
    iconSize?: number
    iconBorderColor?: string
    iconCheckedColor?: string
    iconUncheckedColor?: string
    state: boolean
    updateState: React.Dispatch<React.SetStateAction<boolean>>
}

const Checkbox: FC<Props> = ({ text = "", subtitle = "", iconSize = 30, iconBorderColor = "red", iconCheckedColor = "red", iconUncheckedColor = "white", state, updateState }) => {
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
                }}
                style={styles.checkboxContainer}
                isChecked={state}
                disableBuiltInState={true}
                onPress={() => updateState(!state)}
            />
            {subtitle !== "" ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
        </View>
    )
}

export default Checkbox
