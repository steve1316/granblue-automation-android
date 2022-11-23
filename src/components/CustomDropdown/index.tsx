import React, { FC } from "react"
import { Alert, StyleSheet, View } from "react-native"
import SelectDropdown from "react-native-select-dropdown"
import FontAwesome from "react-native-vector-icons/FontAwesome"

interface Props {
    placeholder: string
    value: string
    data?: string[]
    disabled?: boolean
    search?: boolean
    onSelect?: (selectedItem: any, index: number) => void
}

const CustomDropdown: FC<Props> = ({ placeholder, value, data = [], disabled = false, search = false, onSelect = () => Alert.alert("Pressed!") }) => {
    const styles = StyleSheet.create({
        viewContainer: {
            marginVertical: 10,
            alignSelf: "center",
        },
        dropdown1BtnStyle: {
            width: "100%",
            height: 50,
            backgroundColor: "#FFF",
            borderRadius: 8,
            borderWidth: 1,
            borderColor: "#444",
        },
        dropdown1BtnTxtStyle: { color: "#444", textAlign: "left" },
        dropdown1DropdownStyle: { backgroundColor: "#EFEFEF" },
        dropdown1RowStyle: { backgroundColor: "#EFEFEF", borderBottomColor: "#C5C5C5" },
        dropdown1RowTxtStyle: { color: "#444", textAlign: "left" },
        dropdown1SelectedRowStyle: { backgroundColor: "rgba(0,0,0,0.1)" },
        dropdown1searchInputStyleStyle: {
            backgroundColor: "#EFEFEF",
            borderRadius: 8,
            borderBottomWidth: 1,
            borderBottomColor: "#444",
        },
    })

    return (
        <View style={styles.viewContainer}>
            <SelectDropdown
                data={data}
                defaultValue={value}
                onSelect={onSelect}
                defaultButtonText={placeholder}
                buttonTextAfterSelection={(selectedItem, index) => {
                    return selectedItem
                }}
                rowTextForSelection={(item, index) => {
                    return item
                }}
                buttonStyle={styles.dropdown1BtnStyle}
                buttonTextStyle={styles.dropdown1BtnTxtStyle}
                renderDropdownIcon={(isOpened) => {
                    return <FontAwesome name={isOpened ? "chevron-up" : "chevron-down"} color={"#444"} size={18} />
                }}
                dropdownIconPosition={"right"}
                dropdownStyle={styles.dropdown1DropdownStyle}
                rowStyle={styles.dropdown1RowStyle}
                rowTextStyle={styles.dropdown1RowTxtStyle}
                selectedRowStyle={styles.dropdown1SelectedRowStyle}
                disabled={disabled}
                search={search ? true : undefined}
                searchInputStyle={styles.dropdown1searchInputStyleStyle}
                searchPlaceHolder={"Search here"}
                searchPlaceHolderColor={"darkgrey"}
                renderSearchInputLeftIcon={() => {
                    return <FontAwesome name={"search"} color={"#444"} size={18} />
                }}
            />
        </View>
    )
}

export default CustomDropdown
