import { useContext } from "react"
import { View } from "react-native"
import CustomCheckbox from "../../../components/CustomCheckbox"
import { BotStateContext } from "../../../context/BotStateContext"

const ArcarumSandboxHelper = () => {
    const bsc = useContext(BotStateContext)

    if (bsc.settings.game.farmingMode === "Arcarum Sandbox") {
        return (
            <View>
                <CustomCheckbox
                    text="Enable Defender settings"
                    subtitle="Enable additional settings to show up in the Extra Settings page."
                    isChecked={bsc.settings.sandbox.enableDefender}
                    onPress={() => bsc.setSettings({ ...bsc.settings, sandbox: { ...bsc.settings.sandbox, enableDefender: !bsc.settings.sandbox.enableDefender } })}
                />

                <CustomCheckbox
                    text="Enable gold chest opening"
                    subtitle="Experimental, it uses default party and the chosen script for combat."
                    isChecked={bsc.settings.sandbox.enableGoldChest}
                    onPress={() => bsc.setSettings({ ...bsc.settings, sandbox: { ...bsc.settings.sandbox, enableGoldChest: !bsc.settings.sandbox.enableGoldChest } })}
                />
            </View>
        )
    } else {
        return null
    }
}

export default ArcarumSandboxHelper
