import Ionicons from "react-native-vector-icons/Ionicons"
import React, { FC, useEffect, useRef, useState } from "react"
import { Animated, FlatList, ListRenderItem, Modal, ScrollView, StyleProp, StyleSheet, Text, TextInput, TextStyle, TouchableOpacity, View, ViewStyle } from "react-native"
import { TouchableRipple } from "react-native-paper"

interface Props {
    data: Option[]
    value: string
    setValue: Function
    onSelect?: () => void
    placeholder?: string
    containerStyle?: StyleProp<ViewStyle>
    textfieldContainerStyle?: StyleProp<ViewStyle>
    textfieldTextStyle?: TextStyle
    dropdownContainerStyle?: StyleProp<ViewStyle>
    dropdownItemStyle?: StyleProp<ViewStyle>
    dropdownItemTextStyle?: TextStyle
    dropdownDisabledItemStyle?: StyleProp<ViewStyle>
    dropdownDisabledItemTextStyle?: TextStyle
    showModal?: boolean
    maxDropdownHeight?: number
    toggleDropdown?: boolean
    searchIconElement?: JSX.Element
    closeIconElement?: JSX.Element
    search?: boolean
    disabled?: boolean
    disabledContainerStyle?: StyleProp<ViewStyle>
}

type Option = {
    value: string
    disabled?: boolean
}

const CustomDropDownPicker: FC<Props> = ({
    data,
    value,
    setValue,
    onSelect = () => {},
    placeholder = "Placeholder",
    containerStyle,
    textfieldContainerStyle,
    textfieldTextStyle,
    dropdownContainerStyle,
    dropdownItemStyle,
    dropdownItemTextStyle,
    dropdownDisabledItemStyle,
    dropdownDisabledItemTextStyle,
    showModal = false,
    maxDropdownHeight = 200,
    toggleDropdown = false,
    searchIconElement,
    closeIconElement,
    search = false,
    disabled = false,
    disabledContainerStyle,
}) => {
    const [firstTime, setFirstTime] = useState<boolean>(true)
    const [selectedValue, setSelectedValue] = useState<string>(value)
    const [showDropdown, setShowDropdown] = useState<boolean>(toggleDropdown)
    const [filteredData, setFilteredData] = useState<Option[]>(data)
    const animatedValue = useRef(new Animated.Value(0)).current

    const styles = StyleSheet.create({
        container: {
            flexDirection: "row",
            justifyContent: "space-between",
            borderWidth: 1,
            borderRadius: 5,
            borderBottomLeftRadius: showDropdown ? 0 : 5,
            borderBottomRightRadius: showDropdown ? 0 : 5,
            borderColor: "#000",
            paddingVertical: 12,
            paddingHorizontal: 20,
        },
        containerWrapper: {
            flex: 1,
            flexDirection: "row",
            alignItems: "center",
        },
        dropdownContainer: {
            borderTopWidth: showDropdown ? 0 : 1,
            borderWidth: 1,
            borderBottomLeftRadius: 10,
            borderBottomRightRadius: 10,
            overflow: "hidden",
            backgroundColor: "lightgray",
        },
        dropdownItem: {
            paddingHorizontal: 20,
            paddingVertical: 8,
            overflow: "hidden",
        },
        dropdownText: {
            color: "#000",
        },
        dropdownDisabledItem: {
            opacity: 0.5,
        },
        dropdownDisabledText: {
            color: "#808080",
        },
        textfieldContainer: {
            flex: 1,
            padding: 0,
            height: 20,
            color: "#000",
        },
        textfieldText: {
            color: "#000",
        },
        icon: {
            color: "#000",
        },
    })

    useEffect(() => {
        setFilteredData(data)
    }, [data])

    useEffect(() => {
        setSelectedValue(value)
    }, [value])

    // Set the state to false to allow the dropdown animation to start.
    useEffect(() => {
        if (firstTime) {
            setFirstTime(false)
            return
        }

        // Call the onSelect callback if it is required.
        onSelect()
    }, [selectedValue])

    // Only animate after a value has been selected and the requisite state has been set to false.
    useEffect(() => {
        if (!firstTime) {
            if (toggleDropdown) animateDropdownSlideDown()
            else animateDropdownSlideUp()
        }
    }, [toggleDropdown])

    // Set the state to true and then animate the dropdown to slide down.
    const animateDropdownSlideDown = () => {
        setShowDropdown(true)
        Animated.timing(animatedValue, {
            toValue: maxDropdownHeight,
            duration: 250,
            useNativeDriver: false,
        }).start()
    }

    // Animate the dropdown to slide up and then set its state to false.
    const animateDropdownSlideUp = () => {
        Animated.timing(animatedValue, {
            toValue: 0,
            duration: 150,
            useNativeDriver: false,
        }).start(() => setShowDropdown(false))
    }

    const setStateAndCloseDropdown = (value: string = "") => {
        // Set the value states both inside the component and outside.
        setValue(value)
        setSelectedValue(value)

        // Close the dropdown, then reset the filtered data.
        animateDropdownSlideUp()
        setFilteredData(data)
    }

    const renderFlatListView: ListRenderItem<Option> = ({ item }: { item: Option }) => {
        if (item.disabled) {
            return (
                // Note: TouchableRipples are used instead of TouchableOpacity to avoid the "Excessive number of pending callbacks" with too many TouchableOpacity components inside a ScrollView.
                <TouchableRipple style={[styles.dropdownItem, styles.dropdownDisabledItem, dropdownDisabledItemStyle]}>
                    <Text style={[styles.dropdownDisabledText, dropdownDisabledItemTextStyle]}>{item.value}</Text>
                </TouchableRipple>
            )
        } else {
            return (
                <TouchableRipple style={[styles.dropdownItem, dropdownItemStyle]} onPress={() => setStateAndCloseDropdown(item.value)}>
                    <Text style={[styles.dropdownText, dropdownItemTextStyle]}>{item.value}</Text>
                </TouchableRipple>
            )
        }
    }

    return (
        <View>
            {/* Textfield component */}
            {search && showDropdown ? (
                <View style={[styles.container, disabled ? disabledContainerStyle : containerStyle]}>
                    <View style={styles.containerWrapper}>
                        {searchIconElement ? searchIconElement : <Ionicons name="search" size={20} style={[{ marginRight: 10 }, styles.icon]} />}
                        <TextInput
                            placeholder="Search..."
                            placeholderTextColor="#000"
                            style={[styles.textfieldContainer, textfieldContainerStyle]}
                            onChangeText={(tempValue) => {
                                let tempData = data.filter((item: Option) => {
                                    // Returns all options that match the search query in lowercase and then set it as the filtered data state.
                                    let lowercase: string = item.value.toLowerCase()
                                    return lowercase.search(tempValue.toLowerCase()) > -1
                                })

                                setFilteredData(tempData)
                            }}
                        />
                        <TouchableOpacity disabled={disabled} onPress={() => setStateAndCloseDropdown()}>
                            {closeIconElement ? closeIconElement : <Ionicons name="close" size={20} style={styles.icon} />}
                        </TouchableOpacity>
                    </View>
                </View>
            ) : (
                <TouchableOpacity
                    style={[styles.container, disabled ? disabledContainerStyle : containerStyle]}
                    onPress={() => {
                        // Animate when the container itself is pressed.
                        if (!disabled) {
                            if (!showDropdown) {
                                animateDropdownSlideDown()
                            } else {
                                animateDropdownSlideUp()
                            }
                        }
                    }}
                >
                    <Text style={[styles.textfieldText, textfieldTextStyle]}>{selectedValue == "" ? (placeholder ? placeholder : "Select option") : selectedValue}</Text>
                    {!showDropdown ? <Ionicons name="chevron-down-outline" size={20} style={{ color: "#000" }} /> : <Ionicons name="chevron-up-outline" size={20} style={{ color: "#000" }} />}
                </TouchableOpacity>
            )}

            {/* Dropdown component */}
            {showDropdown ? (
                showModal ? (
                    <Modal animationType="fade" visible={showDropdown} presentationStyle="fullScreen" onRequestClose={() => setShowDropdown(false)}>
                        <View style={styles.container}>
                            {searchIconElement ? searchIconElement : <Ionicons name="search" size={20} style={[{ marginRight: 10 }, styles.icon]} />}
                            <TextInput
                                placeholder="Search..."
                                placeholderTextColor="#000"
                                style={[styles.textfieldContainer, textfieldContainerStyle]}
                                onChangeText={(tempValue) => {
                                    let tempData = data.filter((item: Option) => {
                                        // Returns all options that match the search query in lowercase and then set it as the filtered data state.
                                        let lowercase: string = item.value.toLowerCase()
                                        return lowercase.search(tempValue.toLowerCase()) > -1
                                    })

                                    setFilteredData(tempData)
                                }}
                            />
                            <TouchableOpacity disabled={disabled} onPress={() => setStateAndCloseDropdown()}>
                                {closeIconElement ? closeIconElement : <Ionicons name="close" size={20} style={styles.icon} />}
                            </TouchableOpacity>
                        </View>

                        <FlatList data={filteredData} renderItem={renderFlatListView} />
                    </Modal>
                ) : (
                    <Animated.View style={[{ maxHeight: animatedValue }, styles.dropdownContainer, dropdownContainerStyle]}>
                        <ScrollView contentContainerStyle={{ paddingVertical: 5, overflow: "hidden" }} nestedScrollEnabled={true}>
                            {filteredData.length >= 1 ? (
                                filteredData.map((item: Option, index: number) => {
                                    // Render the option as disabled if it is present and true. Otherwise, render the option used the provided styles.
                                    if (item.disabled) {
                                        return (
                                            // Note: TouchableRipples are used instead of TouchableOpacity to avoid the "Excessive number of pending callbacks" with too many TouchableOpacity components inside a ScrollView.
                                            <TouchableRipple style={[styles.dropdownItem, styles.dropdownDisabledItem, dropdownDisabledItemStyle]} key={index}>
                                                <Text style={[styles.dropdownDisabledText, dropdownDisabledItemTextStyle]}>{item.value}</Text>
                                            </TouchableRipple>
                                        )
                                    } else {
                                        return (
                                            <TouchableRipple style={[styles.dropdownItem, dropdownItemStyle]} key={index} onPress={() => setStateAndCloseDropdown(item.value)}>
                                                <Text style={[styles.dropdownText, dropdownItemTextStyle]}>{item.value}</Text>
                                            </TouchableRipple>
                                        )
                                    }
                                })
                            ) : (
                                <TouchableRipple style={[styles.dropdownItem, dropdownItemStyle]} onPress={() => setStateAndCloseDropdown()}>
                                    <Text style={[styles.dropdownText, dropdownItemTextStyle]}>No results found</Text>
                                </TouchableRipple>
                            )}
                        </ScrollView>
                    </Animated.View>
                )
            ) : null}
        </View>
    )
}

export default CustomDropDownPicker
