import { useContext } from "react"
import { View, Dimensions } from "react-native"
import { Text } from "react-native-elements"
import NumericInput from "react-native-numeric-input"
import CustomCheckbox from "../../../components/CustomCheckbox"
import { BotStateContext } from "../../../context/BotStateContext"

const ROTBHelper = () => {
    const bsc = useContext(BotStateContext)

    if (bsc.settings.game.farmingMode === "Rise of the Beasts") {
        return (
            <View>
                <CustomCheckbox
                    text={"Enable if Rise of the Beasts is in\ndifferent position"}
                    subtitle="Enable this to properly select Rise of the Beasts if it is not positioned first on the list of events in the Home Menu."
                    isChecked={bsc.settings.rotb.enableNewPosition}
                    onPress={() => bsc.setSettings({ ...bsc.settings, rotb: { ...bsc.settings.rotb, enableNewPosition: !bsc.settings.rotb.enableNewPosition } })}
                />

                {bsc.settings.rotb.enableNewPosition ? (
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
                            value={bsc.settings.rotb.newPosition}
                            onChange={(value) => bsc.setSettings({ ...bsc.settings, rotb: { ...bsc.settings.rotb, newPosition: value } })}
                            containerStyle={{ marginBottom: 10, alignSelf: "center" }}
                            totalWidth={Dimensions.get("screen").width * 0.9}
                            totalHeight={50}
                        />
                    </View>
                ) : null}
            </View>
        )
    } else {
        return null
    }
}

export default ROTBHelper
