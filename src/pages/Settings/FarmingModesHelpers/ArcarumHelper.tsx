import { useContext } from "react"
import CustomCheckbox from "../../../components/CustomCheckbox"
import { BotStateContext } from "../../../context/BotStateContext"

const ArcarumHelper = () => {
    const bsc = useContext(BotStateContext)

    if (bsc.settings.game.farmingMode === "Arcarum") {
        return (
            <CustomCheckbox
                text="Enable Stop on Arcarum Boss"
                subtitle="Enable this option to have the bot stop upon encountering a Arcarum Boss (3-3, 6-3, 9-9)."
                isChecked={bsc.settings.arcarum.enableStopOnArcarumBoss}
                onPress={() => bsc.setSettings({ ...bsc.settings, arcarum: { ...bsc.settings.arcarum, enableStopOnArcarumBoss: !bsc.settings.arcarum.enableStopOnArcarumBoss } })}
            />
        )
    } else {
        return null
    }
}

export default ArcarumHelper
