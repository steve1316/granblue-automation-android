import MIcon from "react-native-vector-icons/MaterialCommunityIcons"
import React, { FC } from "react"
import { Divider, Text } from "react-native-elements"
import { StyleSheet, View } from "react-native"

interface Props {
    title: string
    subtitle?: string
    hasIcon?: boolean
    iconName?: string
    iconSize?: number
    iconColor?: string
}

const TitleDivider: FC<Props> = ({ title, subtitle = "", hasIcon = false, iconName = "android", iconSize = 25, iconColor = "#000" }) => {
    const styles = StyleSheet.create({
        title: {
            marginBottom: 10,
        },
        divider: {
            marginTop: -5,
            marginBottom: 5,
        },
        subtitle: {
            marginBottom: 10,
        },
    })

    return (
        <View>
            <Text h4 style={styles.title}>
                {title} {hasIcon ? <MIcon name={iconName} size={iconSize} color={iconColor} /> : null}
            </Text>
            <Divider style={styles.divider} />
            {subtitle !== "" ? <Text style={styles.subtitle}>{subtitle}</Text> : null}
        </View>
    )
}

export default TitleDivider
