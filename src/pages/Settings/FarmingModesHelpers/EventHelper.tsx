import { useContext } from "react"
import { View, Dimensions } from "react-native"
import { Text } from "react-native-elements"
import NumericInput from "react-native-numeric-input"
import CustomCheckbox from "../../../components/CustomCheckbox"
import { BotStateContext } from "../../../context/BotStateContext"

const EventHelper = () => {
    const bsc = useContext(BotStateContext)

    if (bsc.settings.game.farmingMode === "Event") {
        return (
            <CustomCheckbox
                text={`Enable Incrementation of Location\nby 1`}
                subtitle="Enable this if the event has its N/H missions at the very top so the bot can correctly select the correct quest. Or in otherwords, enable this if the Event tab in the Special page has 3 'Select' buttons instead of 2."
                isChecked={bsc.settings.event.enableLocationIncrementByOne}
                onPress={() => bsc.setSettings({ ...bsc.settings, event: { ...bsc.settings.event, enableLocationIncrementByOne: !bsc.settings.event.enableLocationIncrementByOne } })}
            />
        )
    } else if (bsc.settings.game.farmingMode === "Event (Token Drawboxes)") {
        return (
            <View>
                <CustomCheckbox
                    text="Enable if Event is in different position"
                    subtitle="Enable this to properly select the Event if it is not positioned first on the list of events in the Home Menu."
                    isChecked={bsc.settings.event.enableNewPosition}
                    onPress={() => bsc.setSettings({ ...bsc.settings, event: { ...bsc.settings.event, enableNewPosition: !bsc.settings.event.enableNewPosition } })}
                />

                {bsc.settings.event.enableNewPosition ? (
                    <View>
                        <Text style={{ marginBottom: 10, fontSize: 12, opacity: 0.7, color: "black" }}>Default is the first position or the value of 0</Text>
                        <NumericInput
                            type="plus-minus"
                            leftButtonBackgroundColor="#eb5056"
                            rightButtonBackgroundColor="#EA3788"
                            rounded
                            valueType="integer"
                            minValue={0}
                            maxValue={5}
                            step={1}
                            value={bsc.settings.event.newPosition}
                            onChange={(value) => bsc.setSettings({ ...bsc.settings, event: { ...bsc.settings.event, newPosition: value } })}
                            containerStyle={{ marginBottom: 10, alignSelf: "center" }}
                            totalWidth={Dimensions.get("screen").width * 0.9}
                            totalHeight={50}
                        />
                    </View>
                ) : null}

                <CustomCheckbox
                    text="Enable Selecting the Bottom Category"
                    subtitle="In the event of the raids being split between 2 categories, the bot selects the top category by default. Enable this to select the bottom category instead."
                    isChecked={bsc.settings.event.selectBottomCategory}
                    onPress={() => bsc.setSettings({ ...bsc.settings, event: { ...bsc.settings.event, selectBottomCategory: !bsc.settings.event.selectBottomCategory } })}
                />
            </View>
        )
    } else {
        return null
    }
}

export default EventHelper
