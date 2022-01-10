import React, { useContext, useEffect, useState } from "react"
import RNFS from "react-native-fs"
import { BotStateContext, defaultSettings, Settings } from "../../context/BotStateContext"
import { MessageLogContext } from "../../context/MessageLogContext"

const Start = () => {
    const [firstTime, setFirstTime] = useState<boolean>(true)

    const bsc = useContext(BotStateContext)
    const mlc = useContext(MessageLogContext)

    // Load settings if local settings.json file exists in internal storage.
    useEffect(() => {
        loadSettings()
        setFirstTime(false)
    }, [])

    useEffect(() => {
        saveSettings()
    }, [bsc.settings])

    const saveSettings = async (newSettings?: Settings) => {
        if (!firstTime) {
            mlc.setMessageLog([])
            mlc.setAsyncMessages([])

            // Grab a local copy of the current settings.
            const localSettings: Settings = newSettings ? newSettings : bsc.settings

            // Save settings to local settings.json file in internal storage.
            const path = RNFS.ExternalDirectoryPath + "/settings.json"

            let toSave = JSON.stringify(localSettings, null, 4)

            // Delete settings.json file first as RNFS.writeFile() does not clear the file first before writing on top of it.
            // This is the reason why there are extra brackets and fields "appended" to the end of the file before.
            // Source: https://github.com/itinance/react-native-fs/issues/869#issuecomment-602067100
            await RNFS.unlink(path)
                .then(() => {
                    console.log("settings.json file successfully deleted.")
                })
                .catch(() => {
                    console.log("settings.json file does not exist so no need to delete it before saving current settings.")
                })

            await RNFS.writeFile(path, toSave)
                .then(() => {
                    console.log("Settings saved to ", path)
                    mlc.setMessageLog([...mlc.messageLog, `\n[SUCCESS] Settings saved to ${path}`])
                })
                .catch((e) => {
                    console.error(`Error writing settings to path ${path}: ${e}`)
                    mlc.setMessageLog([...mlc.messageLog, `\n[ERROR] Error writing settings to path ${path}: \n${e}`])
                })
                .finally(() => {
                    handleReady()
                })
        }
    }

    const loadSettings = async () => {
        const path = RNFS.ExternalDirectoryPath + "/settings.json"
        let newSettings: Settings = defaultSettings
        let corruptedData = ""
        await RNFS.readFile(path)
            .then(async (data) => {
                console.log(`Loaded settings from settings.json file.`)
                corruptedData = data

                const parsed: Settings = JSON.parse(data)
                newSettings = parsed
            })
            .catch((e) => {
                console.error(`Error reading settings from path ${path}: ${e.name}`)
                mlc.setMessageLog([...mlc.messageLog, `\n[ERROR] Error reading settings from path ${path}: \n${e}`])
            })
            .finally(() => {
                bsc.setSettings(newSettings)
            })
    }

    // Determine whether the program is ready to start.
    const handleReady = () => {
        const farmingMode = bsc.settings.game.farmingMode
        if (farmingMode !== "Coop" && bsc.settings.game.farmingMode !== "Arcarum" && bsc.settings.game.farmingMode !== "Generic" && bsc.settings.game.farmingMode !== "") {
            bsc.setReadyStatus(bsc.settings.game.item !== "" && bsc.settings.game.mission !== "" && bsc.settings.game.summons.length !== 0)
        } else if (bsc.settings.game.farmingMode === "Coop" || bsc.settings.game.farmingMode === "Arcarum") {
            bsc.setReadyStatus(bsc.settings.game.item !== "" && bsc.settings.game.mission !== "")
        } else {
            bsc.setReadyStatus(farmingMode === "Generic" && bsc.settings.game.item !== "" && bsc.settings.game.summons.length !== 0)
        }
    }

    return null
}

export default Start
