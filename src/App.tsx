import { registerRootComponent } from "expo"
import { NavigationContainer } from "@react-navigation/native"
import { createBottomTabNavigator } from "@react-navigation/bottom-tabs"
import Ionicons from "react-native-vector-icons/Ionicons"
import Home from "./pages/Home"
import Settings from "./pages/Settings"
import ExtraSettings from "./pages/ExtraSettings"

const Tab = createBottomTabNavigator()

function App() {
    return (
        <NavigationContainer>
            <Tab.Navigator
                screenOptions={({ route }) => ({
                    tabBarIcon: ({ focused, color, size }: { focused: boolean; color: string; size: number }) => {
                        let iconName = ""
                        if (route.name === "Home") {
                            iconName = focused ? "home" : "home-outline"
                        } else if (route.name === "Settings") {
                            iconName = focused ? "settings" : "settings-outline"
                        } else if (route.name === "Extra Settings") {
                            iconName = focused ? "cog" : "cog-outline"
                        }

                        return <Ionicons name={iconName} size={size} color={color} />
                    },
                    tabBarActiveTintColor: "tomato",
                    tabBarInactiveTintColor: "gray",
                })}
            >
                <Tab.Screen name="Home" component={Home} />
                <Tab.Screen name="Settings" component={Settings} />
                <Tab.Screen name="Extra Settings" component={ExtraSettings} />
            </Tab.Navigator>
        </NavigationContainer>
    )
}

export default registerRootComponent(App)
