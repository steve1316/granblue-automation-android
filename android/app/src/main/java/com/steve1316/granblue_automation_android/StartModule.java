package com.steve1316.granblue_automation_android;

import static com.steve1316.granblue_automation_android.MainActivity.loggerTag;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;

import com.steve1316.granblue_automation_android.R;
import com.steve1316.granblue_automation_android.utils.MediaProjectionService;
import com.steve1316.granblue_automation_android.utils.MyAccessibilityService;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.Objects;

public class StartModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    private final ReactApplicationContext reactContext;

    public StartModule(ReactApplicationContext reactContext) {
        super(reactContext); //required by React Native
        this.reactContext = reactContext;
        this.reactContext.addActivityEventListener(this);
    }

    @ReactMethod
    public void start() {
        if (readyCheck()) {
            startProjection();
        } else if (MediaProjectionService.Companion.isRunning()) {
            stopProjection();
        }
    }

    @NonNull
    @Override
    public String getName() {
        return "StartModule";
    }

    private boolean readyCheck() {
        return checkForOverlayPermission() && checkForAccessibilityPermission();
    }

    private boolean checkForOverlayPermission() {
        if (!Settings.canDrawOverlays(getCurrentActivity())) {
            Log.d(loggerTag, "Application is missing overlay permission.");

            AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
            builder.setTitle(R.string.overlay_disabled);
            builder.setMessage(R.string.overlay_disabled_message);

            builder.setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Send the user to the Overlay Settings.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${myContext.getPackageName()}"));
                    Objects.requireNonNull(getCurrentActivity()).startActivity(intent);
                }
            });

            builder.setNegativeButton(android.R.string.cancel, null);

            builder.show();
            return false;
        }

        Log.d(loggerTag, "Application has permission to draw overlay.");
        return true;
    }

    private boolean checkForAccessibilityPermission() {
        String prefString = Settings.Secure.getString(Objects.requireNonNull(getCurrentActivity()).getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

        if (prefString != null && !prefString.isEmpty()) {
            // Check the string of enabled accessibility services to see if this application's accessibility service is there.
            boolean enabled = prefString.contains(this.reactContext.getPackageName() + "/" + MyAccessibilityService.class.getName());

            if (enabled) {
                Log.d(loggerTag, "This application's Accessibility Service is currently turned on.");
                return true;
            }
        }

        // Moves the user to the Accessibility Settings if the service is not detected.
        AlertDialog.Builder builder = new AlertDialog.Builder(getCurrentActivity());
        builder.setTitle(R.string.accessibility_disabled);
        builder.setMessage(R.string.accessibility_disabled_message);

        builder.setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(loggerTag, "Accessibility Service is not detected. Moving user to Accessibility Settings.");
                Intent accessibilitySettingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                Objects.requireNonNull(getCurrentActivity()).startActivity(accessibilitySettingsIntent);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();

        return false;
    }

    private void startProjection() {
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) this.reactContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        this.reactContext.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 100, null);
    }

    private void stopProjection() {
        this.reactContext.startService(MediaProjectionService.Companion.getStopIntent(this.reactContext));
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.d(loggerTag, "onActivityResult LISTENER with:");
        Log.d(loggerTag, String.format("Request code: %d", requestCode));
        Log.d(loggerTag, String.format("Result code: %d", resultCode));
        Log.d(loggerTag, String.format("data intent: %s", data.toString()));

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            // Start up the MediaProjection service after the user accepts the onscreen prompt.
            Log.d(loggerTag, "Starting SERVICE here");
            this.reactContext.startService(MediaProjectionService.Companion.getStartIntent(this.reactContext, resultCode, data));
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.d(loggerTag, "onNewIntent LISTENER with:");
        Log.d(loggerTag, String.format("intent: %s", intent.toString()));
    }
}
