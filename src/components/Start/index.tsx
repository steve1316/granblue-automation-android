import RNFS from "react-native-fs"
import { BotStateContext, defaultSettings, Settings } from "../../context/BotStateContext"
import { MessageLogContext } from "../../context/MessageLogContext"
import { useContext, useEffect, useState } from "react"
import axios, { AxiosError, AxiosResponse } from "axios"
import { DeviceEventEmitter } from "react-native"

const Start = () => {
    const [firstTime, setFirstTime] = useState<boolean>(true)
    const [firstTimeAPIRequest, setFirstTimeAPIRequest] = useState<boolean>(true)
    const [apiData, setAPIData] = useState<APIData>({ itemName: "", amount: 1, elapsedTime: "" })
    const [sendAPIRequestNow, setSendAPIRequestNow] = useState<boolean>(false)
    const [successfulAPILogin, setSuccessfulAPILogin] = useState<boolean>(true)

    const bsc = useContext(BotStateContext)
    const mlc = useContext(MessageLogContext)

    interface APIData {
        itemName: string
        amount: number
        elapsedTime: string
    }

    // Load settings if local settings.json file exists in internal storage.
    useEffect(() => {
        loadSettings()

        // Add listener for API specific logic.
        DeviceEventEmitter.addListener("MessageLog", (data) => {
            if (successfulAPILogin && data["message"].includes("API-RESULT")) {
                // Format of the line is API-RESULT|ITEM NAME|ITEM AMOUNT|TIME
                let newLog: string[] = []
                let line = data["message"]
                const splitLine = line.split("|")
                if (splitLine.length !== 4) {
                    console.log(`Unable to send API request to Granblue Automation Statistics: Invalid request format of ${splitLine.length}.`)
                    newLog = [...mlc.asyncMessages, `\nUnable to send API request to Granblue Automation Statistics: Invalid request format of ${splitLine.length}.`]
                } else if (Number.isNaN(parseInt(splitLine[2]))) {
                    console.log(`Unable to send API request to Granblue Automation Statistics: Invalid type for item amount.`)
                    newLog = [...mlc.asyncMessages, `\nUnable to send API request to Granblue Automation Statistics: Invalid type for item amount.`]
                } else {
                    setAPIData({ itemName: splitLine[1], amount: parseInt(splitLine[2]), elapsedTime: splitLine[3] })
                    setSendAPIRequestNow(true)
                }

                newLog = [...newLog, `\n${line}`]
                mlc.setAsyncMessages(newLog)
            } else {
                let newLog = [...mlc.asyncMessages, `\n${data["message"]}`]
                mlc.setAsyncMessages(newLog)
            }
        })
    }, [])

    useEffect(() => {
        saveSettings()
    }, [bsc.settings])

    useEffect(() => {
        if (mlc.asyncMessages.length > 0) {
            const newLog = [...mlc.messageLog, ...mlc.asyncMessages]
            mlc.setMessageLog(newLog)
        }
    }, [mlc.asyncMessages])

    const saveSettings = async (newSettings?: Settings) => {
        if (!firstTime) {
            // Grab a local copy of the current settings.
            const localSettings: Settings = newSettings ? newSettings : bsc.settings

            // Save settings to local settings.json file in internal storage.
            const path = RNFS.ExternalDirectoryPath + "/settings.json"

            let toSave = JSON.stringify(localSettings, null, 4)

            // Delete settings.json file first as RNFS.writeFile() does not clear the file first before writing on top of it.
            // This is the reason why there are extra brackets and fields "appended" to the end of the file before.
            // Source: https://github.com/itinance/react-native-fs/issues/869#issuecomment-602067100
            // Note: It unfortunately still happens.
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
                    mlc.setAsyncMessages([])
                    mlc.setMessageLog([`\n[SUCCESS] Settings saved to ${path}`])
                })
                .catch((e) => {
                    console.error(`Error writing settings to path ${path}: ${e}`)
                    mlc.setMessageLog([...mlc.messageLog, `\n[ERROR] Error writing settings to path ${path}: \n${e}`])
                })
                .finally(() => {
                    handleReady()
                })
        } else {
            // Perform ready check upon loading up the settings on a cold boot.
            handleReady()
        }
    }

    const loadSettings = async () => {
        const path = RNFS.ExternalDirectoryPath + "/settings.json"
        let newSettings: Settings = defaultSettings
        let normalData = ""
        let corruptionFixed = false
        await RNFS.readFile(path)
            .then(async (data) => {
                console.log(`Loaded settings from settings.json file.`)
                normalData = data

                const parsed: Settings = JSON.parse(data)
                const fixedSettings: Settings = fixSettings(parsed)
                newSettings = fixedSettings
            })
            .catch((e: Error) => {
                if (e.name === "SyntaxError") {
                    // If file corruption occurred, attempt to fix by removing the offending characters one by one from the JSON string.
                    let fixedData = normalData
                    while (true) {
                        fixedData = fixedData.substring(0, fixedData.length - 1)
                        try {
                            const parsed: Settings = JSON.parse(fixedData)
                            const fixedSettings: Settings = fixSettings(parsed)
                            newSettings = fixedSettings
                            corruptionFixed = true
                        } catch {}

                        if (corruptionFixed || fixedData.length === 0) {
                            break
                        }
                    }

                    console.log("Finished attempting to fix corruption.")
                    if (corruptionFixed) {
                        console.log("Automatic fix was successful!")
                    } else {
                        console.error(`Error reading settings from path ${path}: ${e.name}`)
                        mlc.setMessageLog([
                            ...mlc.messageLog,
                            `\n[ERROR] Error reading settings from path ${path}: \n${e}`,
                            "\nNote that GAA sometimes corrupts the settings.json when saving. Automatic fix was not successful.",
                        ])
                    }
                } else if (!e.message.includes("No such file or directory")) {
                    console.error(`Error reading settings from path ${path}: ${e.name}`)
                    mlc.setMessageLog([...mlc.messageLog, `\n[ERROR] Error reading settings from path ${path}: \n${e}`])
                }
            })
            .finally(() => {
                console.log("Read: " + JSON.stringify(newSettings, null, 4))
                bsc.setSettings(newSettings)
                setFirstTime(false)
            })
    }

    // Attempt to fix missing key-value pairs in the settings before commiting them to state.
    const fixSettings = (decoded: Settings) => {
        var newSettings: Settings = decoded
        Object.keys(defaultSettings).forEach((key) => {
            if (decoded[key as keyof Settings] === undefined) {
                newSettings = {
                    ...newSettings,
                    [key as keyof Settings]: defaultSettings[key as keyof Settings],
                }
            }
        })

        return newSettings
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

        handleStart()
    }

    const handleStart = async () => {
        // Login to API.
        if (bsc.settings.api.enableOptInAPI && !successfulAPILogin) {
            await loginToAPI()
        }
    }

    // Login to the Granblue Automation Statistics API.
    const loginToAPI = async () => {
        await axios
            .post(
                `${bsc.entryPoint}/api/login`,
                {
                    username: bsc.settings.api.username,
                    password: bsc.settings.api.password,
                },
                {
                    withCredentials: true,
                }
            )
            .then(() => {
                let newLog = [...mlc.asyncMessages, `Successfully logged into Granblue Automation Statistics API.\n`]
                mlc.setAsyncMessages(newLog)
                setSuccessfulAPILogin(true)
                setFirstTimeAPIRequest(true)
            })
            .catch((e) => {
                let newLog = [...mlc.asyncMessages, `Failed to login to Granblue Automation Statistics API: ${e}\n`]
                mlc.setAsyncMessages(newLog)
            })
    }

    useEffect(() => {
        if (sendAPIRequestNow) {
            sendAPIRequest(apiData.itemName, apiData.amount, apiData.elapsedTime)
            setSendAPIRequestNow(false)
        }
    }, [sendAPIRequestNow])

    // Send a API request to create a new result in the database.
    const sendAPIRequest = async (itemName: string, amount: number, elapsedTime: string) => {
        // If this is the first time, create the item if it does not already exist in the database.
        let newLog: string[] = []
        if (firstTimeAPIRequest) {
            const data = {
                username: bsc.settings.api.username,
                password: bsc.settings.api.password,
                farmingMode: bsc.settings.game.farmingMode,
                mission: bsc.settings.game.mission,
                itemName: itemName,
            }

            console.log("Data: ", data)

            await axios
                .post(`${bsc.entryPoint}/api/create-item`, data, { withCredentials: true })
                .then(async (res: AxiosResponse) => {
                    console.log("[API] ", res.data)
                    newLog = [...mlc.asyncMessages, `\n[API] ${res.data}`]
                    setFirstTimeAPIRequest(false)
                    await axios
                        .post(
                            `${bsc.entryPoint}/api/create-result`,
                            {
                                username: bsc.settings.api.username,
                                password: bsc.settings.api.password,
                                farmingMode: bsc.settings.game.farmingMode,
                                mission: bsc.settings.game.mission,
                                itemName: itemName,
                                platform: "GAA",
                                amount: amount,
                                elapsedTime: elapsedTime,
                                appVersion: bsc.appVersion,
                            },
                            { withCredentials: true }
                        )
                        .then((res: AxiosResponse) => {
                            console.log("[API] ", res.data)
                            newLog = [...newLog, `\n[API] ${res.data}`]
                        })
                        .catch((e) => {
                            newLog = [...newLog, `\n[API] Failed to create result: ${e?.response?.data}`]
                        })
                        .finally(() => {
                            mlc.setAsyncMessages(newLog)
                        })
                })
                .catch((e: AxiosError) => {
                    console.error(`[API] Failed to create item for the first time: `, e.response?.request["_response"])
                    newLog = [...mlc.asyncMessages, `\n[API] Failed to create item for the first time: ${e.response?.request["_response"]}`]
                    mlc.setAsyncMessages(newLog)
                })
        } else {
            let newLog: string[] = []
            await axios
                .post(
                    `${bsc.entryPoint}/api/create-result`,
                    {
                        username: bsc.settings.api.username,
                        password: bsc.settings.api.password,
                        farmingMode: bsc.settings.game.farmingMode,
                        mission: bsc.settings.game.mission,
                        itemName: itemName,
                        platform: "GAA",
                        amount: amount,
                        elapsedTime: elapsedTime,
                        appVersion: bsc.appVersion,
                    },
                    { withCredentials: true }
                )
                .then((res: AxiosResponse) => {
                    console.log("[API] ", res.data)
                    newLog = [...mlc.asyncMessages, `\n[API] ${res.data} || ${bsc.settings.game.mission}`]
                })
                .catch((e: AxiosError) => {
                    newLog = [...mlc.asyncMessages, `\n[API] Failed to create result: ${e.response?.request["_response"]}`]
                })
                .finally(() => {
                    mlc.setAsyncMessages(newLog)
                })
        }
    }

    return null
}

export default Start
