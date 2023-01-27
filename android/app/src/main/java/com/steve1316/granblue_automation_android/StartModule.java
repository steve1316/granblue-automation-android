package com.steve1316.granblue_automation_android;

import static com.steve1316.granblue_automation_android.MainActivity.loggerTag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.steve1316.automation_library.events.JSEvent;
import com.steve1316.automation_library.events.StartEvent;
import com.steve1316.automation_library.utils.MediaProjectionService;
import com.steve1316.automation_library.utils.MyAccessibilityService;
import com.steve1316.automation_library.utils.TwitterUtils;
import com.steve1316.granblue_automation_android.bot.Game;
import com.steve1316.granblue_automation_android.data.ConfigData;
import com.steve1316.granblue_automation_android.utils.CustomJSONParser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.PrivateChannel;
import org.javacord.api.entity.user.User;
import org.javacord.api.entity.user.UserStatus;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * Takes care of setting up internal processes such as the Accessibility and MediaProjection services, receiving and sending messages over to the
 * Javascript frontend, and handle tests involving Discord and Twitter API integrations if needed.
 * <p>
 * Loaded into the React PackageList via MainApplication's instantiation of the StartPackage.
 */
public class StartModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private final String tag = loggerTag + "StartModule";
    private static ReactApplicationContext reactContext;
    private final Context context;
    private static DeviceEventManagerModule.RCTDeviceEventEmitter emitter = null;

    public StartModule(ReactApplicationContext reactContext) {
        super(reactContext); //required by React Native
        StartModule.reactContext = reactContext;
        StartModule.reactContext.addActivityEventListener(this);
        context = reactContext.getApplicationContext();
    }

    @NonNull
    @Override
    public String getName() {
        return "StartModule";
    }

    @Override
    public void onNewIntent(Intent intent) {
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // Start up the MediaProjection service after the user accepts the onscreen prompt.
            reactContext.startService(com.steve1316.automation_library.utils.MediaProjectionService.Companion.getStartIntent(reactContext, resultCode, data));
            sendEvent("MediaProjectionService", "Running");
        }
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    // Interaction with the Start / Stop button.

    /**
     * This is called when the Start button is pressed back at the Javascript frontend and starts up the MediaProjection service along with the
     * BotService attached to it.
     */
    @ReactMethod
    public void start() {
        if (readyCheck()) {
            startProjection();
        }
    }

    /**
     * Register this module with EventBus in order to allow listening to certain events and then begin starting up the MediaProjection service.
     */
    private void startProjection() {
        EventBus.getDefault().register(this);
        Log.d(tag, "Event Bus registered for StartModule");
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) reactContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        reactContext.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 100, null);
    }

    /**
     * Unregister this module with EventBus and then stops the MediaProjection service.
     */
    private void stopProjection() {
        EventBus.getDefault().unregister(this);
        Log.d(tag, "Event Bus unregistered for StartModule");
        reactContext.startService(MediaProjectionService.Companion.getStopIntent(reactContext));
        sendEvent("MediaProjectionService", "Not Running");
    }

    /**
     * This is called when the Stop button is pressed and will begin stopping the MediaProjection service.
     */
    @ReactMethod
    public void stop() {
        stopProjection();
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    // Permissions

    /**
     * Checks the permissions for both overlay and accessibility for this app.
     *
     * @return True if both permissions were already granted and false otherwise.
     */
    private boolean readyCheck() {
        return checkForOverlayPermission() && checkForAccessibilityPermission();
    }

    /**
     * Checks for overlay permission and guides the user to enable it if it has not been granted yet.
     *
     * @return True if the overlay permission has already been granted.
     */
    private boolean checkForOverlayPermission() {
        if (!Settings.canDrawOverlays(getCurrentActivity())) {
            Log.d(tag, "Application is missing overlay permission.");

            AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
            builder.setTitle(R.string.overlay_disabled);
            builder.setMessage(R.string.overlay_disabled_message);

            builder.setPositiveButton(R.string.go_to_settings, (dialogInterface, i) -> {
                // Send the user to the Overlay Settings.
                String uri = String.format("package:%s", reactContext.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse(uri));
                Objects.requireNonNull(getCurrentActivity()).startActivity(intent);
            });

            builder.setNegativeButton(android.R.string.cancel, null);

            builder.show();
            return false;
        }

        Log.d(tag, "Application has permission to draw overlay.");
        return true;
    }

    /**
     * Checks for accessibility permission and guides the user to enable it if it has not been granted yet.
     *
     * @return True if the accessibility permission has already been granted.
     */
    private boolean checkForAccessibilityPermission() {
        String prefString = Settings.Secure.getString(Objects.requireNonNull(getCurrentActivity()).getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

        if (prefString != null && !prefString.isEmpty()) {
            // Check the string of enabled accessibility services to see if this application's accessibility service is there.
            boolean enabled = prefString.contains(reactContext.getPackageName() + "/" + MyAccessibilityService.class.getName());

            if (enabled) {
                Log.d(tag, "This application's Accessibility Service is currently turned on.");
                return true;
            }
        }

        // Moves the user to the Accessibility Settings if the service is not detected.
        AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
        builder.setTitle(R.string.accessibility_disabled);
        builder.setMessage(R.string.accessibility_disabled_message);

        builder.setPositiveButton(R.string.go_to_settings, (dialogInterface, i) -> {
            Log.d(tag, "Accessibility Service is not detected. Moving user to Accessibility Settings.");
            Intent accessibilitySettingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            Objects.requireNonNull(getCurrentActivity()).startActivity(accessibilitySettingsIntent);
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();

        return false;
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    // Twitter

    /**
     * Tests the Twitter API v1.1 integration with the user's keys and tokens.
     */
    @ReactMethod()
    public void startTwitterTest() {
        new Thread(() -> {
            // Initialize settings.
            CustomJSONParser parser = new CustomJSONParser();
            parser.initializeSettings(context);
            TwitterUtils twitter = new TwitterUtils(context, true);
            sendEvent("testTwitter", twitter.testConnection());
        }).start();
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    // Discord

    /**
     * Tests the Discord API integration with the user's ID and token.
     */
    @ReactMethod()
    public void startDiscordTest() {
        // Define a custom class to run inside a separate thread to avoid locking up the main thread.
        class DiscordRunner {
            DiscordApi api = null;
            User user = null;
            PrivateChannel privateChannel = null;

            public void disconnect() {
                if (api != null && api.getStatus() == UserStatus.ONLINE) {
                    api.disconnect();
                }
            }

            public void sendMessage(String message) {
                if (privateChannel != null) {
                    privateChannel.sendMessage(message).join();
                }
            }

            public void main() {
                CustomJSONParser parser = new CustomJSONParser();
                parser.initializeSettings(context);
                ConfigData configData = new ConfigData(context);
                Queue<String> queue = new LinkedList<>();
                String appName = context.getString(R.string.app_name);

                Log.d(tag, "Starting Discord test now...");

                try {
                    api = new DiscordApiBuilder().setToken(configData.getDiscordToken()).login().join();
                } catch (Exception e) {
                    sendEvent("testDiscord", "[DISCORD] Failed to connect to Discord API using provided token.");
                    return;
                }

                try {
                    user = api.getUserById(configData.getDiscordUserID()).join();
                } catch (Exception e) {
                    sendEvent("testDiscord", "[DISCORD] Failed to find user using provided user ID.");
                    return;
                }

                try {
                    privateChannel = user.openPrivateChannel().join();
                } catch (Exception e) {
                    sendEvent("testDiscord", "[DISCORD] Failed to open private channel with user.");
                    return;
                }

                Log.d(tag, "Successfully fetched reference to user and their private channel.");

                queue.add("```diff\n+ Successful connection to Discord API for " + appName + "\n```");
                queue.add("Testing 1 2 3");
                queue.add("```diff\n- Terminated connection to Discord API for " + appName + "\n```");

                try {
                    // Loop and send any messages inside the Queue.
                    while (true) {
                        if (!queue.isEmpty()) {
                            String message = queue.remove();
                            sendMessage(message);

                            if (message.contains("Terminated connection to Discord API")) {
                                break;
                            }
                        }
                    }

                    Log.d(tag, "Terminated connection to Discord API.");
                } catch (Exception e) {
                    Log.e(tag, e.getMessage());
                    sendEvent("testDiscord", e.getMessage());
                    return;
                }

                sendEvent("testDiscord", "Test successfully completed.");
            }
        }

        new Thread(() -> {
            DiscordRunner discord = new DiscordRunner();
            discord.main();
            discord.disconnect();
        }).start();
    }

    ////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////
    // Event interaction

    /**
     * Listener function to start this module's entry point.
     *
     * @param event The StartEvent object to parse its message.
     */
    @Subscribe
    public void onStartEvent(StartEvent event) {
        if (event.getMessage().equals("Entry Point ON")) {
            // Initialize settings.
            CustomJSONParser parser = new CustomJSONParser();
            parser.initializeSettings(context);

            Game entryPoint = new Game(context);
            entryPoint.start();
        }
    }

    /**
     * Sends the message back to the Javascript frontend along with its event name to be listened on.
     *
     * @param eventName The name of the event to be picked up on as defined in the developer's JS frontend.
     * @param message   The message string to pass on.
     */
    public void sendEvent(String eventName, String message) {
        WritableMap params = Arguments.createMap();
        params.putString("message", message);
        if (emitter == null) {
            // Register the event emitter to send messages to JS.
            emitter = StartModule.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
        }

        emitter.emit(eventName, params);
    }

    /**
     * Listener function to call the inner event sending function in order to send the message back to the Javascript frontend.
     *
     * @param event The JSEvent object to parse its event name and message.
     */
    @Subscribe
    public void onJSEvent(JSEvent event) {
        sendEvent(event.getEventName(), event.getMessage());
    }
}