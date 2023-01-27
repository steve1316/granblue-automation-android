import React, { FC } from "react"
import { ActivityIndicator, Alert, GestureResponderEvent, StyleSheet, Text, TouchableOpacity, View } from "react-native"

interface Props {
    title: string
    loadingTitle: string
    isLoading: boolean
    onPress?: (event: GestureResponderEvent) => void
}

const LoadingButton = ({ title, loadingTitle, isLoading, onPress = () => Alert.alert("Pressed!") }: Props) => {
    const styles = StyleSheet.create({
        button: {
            display: "flex",
            flexDirection: "row",
            justifyContent: "space-evenly",
            alignItems: "center",
            borderWidth: 1,
            borderColor: "#666",
            borderRadius: 10,
        },
        buttonText: {
            color: "#fff",
            fontWeight: "bold",
            fontSize: 20,
        },
    })

    return (
        <TouchableOpacity onPress={onPress} disabled={isLoading} style={{ marginVertical: 10 }}>
            <View
                style={{
                    ...styles.button,
                    backgroundColor: isLoading ? "#4caf50" : "#8bc34a",
                }}
            >
                {isLoading ? <ActivityIndicator size="large" color="yellow" /> : null}
                <Text style={styles.buttonText}>{isLoading ? loadingTitle : title}</Text>
            </View>
        </TouchableOpacity>
    )
}

export default LoadingButton
