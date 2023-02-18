import { useContext } from "react"
import { View } from "react-native"
import { Divider, Text } from "react-native-elements"
import CustomCheckbox from "../../../components/CustomCheckbox"
import { BotStateContext } from "../../../context/BotStateContext"

const GenericHelper = () => {
    const bsc = useContext(BotStateContext)

    if (bsc.settings.game.farmingMode === "Generic") {
        return (
            <View>
                <Divider style={{ marginBottom: 10 }} />

                <Text style={{ marginBottom: 10, fontSize: 12, opacity: 0.7, color: "black" }}>
                    {`Selecting this will repeat the current mission on the screen until it finishes the required number of runs. Note that Generic does not provide any navigation.\n\nIt is required that the bot starts on either the Combat screen with the "Attack" button visible, the Loot Collection screen with the "Play Again" button visible, or the Coop Room screen with the "Start" button visible and party already selected.`}
                </Text>

                <Divider />

                <CustomCheckbox
                    text="Enable Forcing Reload after Attack"
                    subtitle="Enable this option to force Generic Farming Mode to reload after an attack. This does not take into account whether or not the current battle supports reloading after an attack."
                    isChecked={bsc.settings.generic.enableForceReload}
                    onPress={() => bsc.setSettings({ ...bsc.settings, generic: { ...bsc.settings.generic, enableForceReload: !bsc.settings.generic.enableForceReload } })}
                />
            </View>
        )
    } else {
        return null
    }
}

export default GenericHelper
