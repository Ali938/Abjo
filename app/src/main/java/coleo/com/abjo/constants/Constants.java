package coleo.com.abjo.constants;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import com.android.volley.VolleyError;
import com.microsoft.appcenter.analytics.Analytics;
import com.robinhood.ticker.TickerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import coleo.com.abjo.R;
import coleo.com.abjo.activity.MainActivity;
import coleo.com.abjo.activity.login.Splash;
import coleo.com.abjo.data_base.TravelDataBase;
import coleo.com.abjo.data_base.locationRepository;
import coleo.com.abjo.data_class.ProfileData;
import coleo.com.abjo.service.SaverReceiver;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.SENSOR_SERVICE;

public class Constants {

    public static final String VERSION = "0.0.1";

    public static final double fuckingRatio = 1.351543942992874;
    public static final double fuckingRatioHoshyar = 1.34261444077;
    public static final double fuckingRatioTop = 1.653934300993125;

    public static locationRepository repository;

    private final static String TOKEN_STORAGE = "someWhereInDarkness";
    private final static String TOKEN_DATA = "someWhereInDarkness12";
    public final static String NO_TOKEN = "nothingInTheBag";

    public static final String CURRENT_SESSION_USER = "session user";
    public static final String URL_CHECK_PHONE = Base_Url + "auth/check_phone/";
    public static final String URL_SEND_CODE = Base_Url + "auth/check_code/";
    public static final String URL_MAKE_USER = Base_Url + "users_store/";
    public static final String URL_GET_USER_PROFILE = Base_Url + "users_store/";
    public static final String URL_GET_LEADER_BOARD = Base_Url + "leader_board/";
    public static final String URL_GET_HISTORY = Base_Url + "user_records_store/";
    public static final String URL_OPEN_SESSION = Base_Url + "user_records_store/";//todo get this url

    //intent
    public static final String PHONE_FROM_LOGIN = "code data";
    public static final String DATA_INVITE_CODE = "invite data";
    public static final String OPEN_SESSION_ID = "session id";
    public static final String CURRENT_SESSION_ID = "session id long";
    //url
    private final static String Base_Url = "http://abjo.coleo.ir/api/v1/";
    public static final String HAVE_SESSION_ID = "session id valid";

    //timer Text view
    public static TickerView hour;
    public static TickerView minute;
    public static TickerView second;
    public static final int ONE_HOUR = 3_600_000;

    public static Context context = null;

    private static final String LAST_ACTION_PRE_NAME = "unknown";
    private static final String LAST_ACTION_SAVE_NAME = "noName";
    public interface ACTION {
        String UPDATE_FOREGROUND_ACTION = "com.coleo.foreground.service.action.update.foreground.step";
        String START_FOREGROUND_ACTION_STEP = "com.coleo.foreground.service.action.start.foreground.step";
        String START_FOREGROUND_ACTION_BIKE = "com.coleo.foreground.service.action.start.foreground.bike";
        String PAUSE_FOREGROUND_ACTION_STEP = "com.coleo.foreground.service.action.pause.foreground.step";
        String PAUSE_FOREGROUND_ACTION_BIKE = "com.coleo.foreground.service.action.pause.foreground.bike";
        String RESUME_FOREGROUND_ACTION_STEP = "com.coleo.foreground.service.action.resume.foreground.step";
        String RESUME_FOREGROUND_ACTION_BIKE = "com.coleo.foreground.service.action.resume.foreground.bike";
        String STOP_FOREGROUND_ACTION = "com.coleo.foreground.service.action.stop.foreground";
    }
    public static boolean isActionKindStep(String action){
        return !action.equals(ACTION.START_FOREGROUND_ACTION_BIKE) &&
                !action.equals(ACTION.PAUSE_FOREGROUND_ACTION_BIKE) &&
                !action.equals(ACTION.RESUME_FOREGROUND_ACTION_BIKE);

    }

    private static Sensor stepCounter;

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

    public static ImageView start_stop = null;
    public static ImageView pause_resume= null;
    public static boolean isWorking = false;
    public static boolean isPause = false;

    private static String NOTIFICATION_CHANEL_ID = "com.coleo.abjo";


    public static float lastCount = 0;
    public static int stepCounted = 0;

    public static String STEP = "start counting";
    public static String STEP_OR_BIKE = "start counting";
    public static String LAST_ACTION_INTENT = "twoInOne";//true == step & false == bike
    public static String FROM_NOTIFICATION = "fromOtherSide";

    public static NotificationCompat.Builder showNotification(String title, String message,
                                                              Context context, boolean makeSound,
                                                              boolean canClose, boolean isStep,
                                                              boolean isPause) {

        Intent intent = new Intent(context, Splash.class);
        intent.putExtra(Constants.FROM_NOTIFICATION, true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pause = new Intent(context, SaverReceiver.class);
        if (isPause) {
            if (isStep) {
                pause.setAction(ACTION.RESUME_FOREGROUND_ACTION_STEP);
            } else {
                pause.setAction(ACTION.RESUME_FOREGROUND_ACTION_BIKE);
            }
        } else {
            if (isStep) {
                pause.setAction(ACTION.PAUSE_FOREGROUND_ACTION_STEP);
            } else {
                pause.setAction(ACTION.PAUSE_FOREGROUND_ACTION_BIKE);
            }
        }
        PendingIntent snoozePendingIntent =
                PendingIntent.getBroadcast(context, 0, pause, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANEL_ID)
                .setSmallIcon(R.mipmap.profile_big_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setOnlyAlertOnce(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(!canClose)
                .addAction(android.R.drawable.ic_media_pause, context.getString(R.string.pause_service), snoozePendingIntent);

        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel(notificationManager);
            builder.setChannelId(NOTIFICATION_CHANEL_ID);
            if (makeSound) {
                builder.setPriority(NotificationManager.IMPORTANCE_DEFAULT);
            } else {
                builder.setPriority(NotificationManager.IMPORTANCE_MIN);
            }
        }

        return builder;
    }

    @TargetApi(26)
    private static void createChannel(NotificationManager notificationManager) {
        String name = NOTIFICATION_CHANEL_ID;
        String description = "Notifications for download status";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(name, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }

    @SuppressLint("RestrictedApi")
    public static void updateNotification(NotificationCompat.Builder notification,
                                          String title, String message,
                                          Context context,
                                          boolean isPause, boolean isStep) {

        Intent pause = new Intent(context, SaverReceiver.class);
        int drawable;
        String actionName;
        if (isPause) {
            actionName = "resume";
            drawable = android.R.drawable.ic_media_play;
            if (isStep) {
                pause.setAction(ACTION.RESUME_FOREGROUND_ACTION_STEP);
            } else {
                pause.setAction(ACTION.RESUME_FOREGROUND_ACTION_BIKE);
            }
        } else {
            actionName = "pause";
            drawable = android.R.drawable.ic_media_pause;
            if (isStep) {
                pause.setAction(ACTION.PAUSE_FOREGROUND_ACTION_STEP);
            } else {
                pause.setAction(ACTION.PAUSE_FOREGROUND_ACTION_BIKE);
            }
        }
        PendingIntent pausePendingIntent =
                PendingIntent.getBroadcast(context, 0, pause, 0);

        if (notification != null) {
            Log.i("Constants", "updateNotification: ");
            if (notification.mActions != null)
                notification.mActions.clear();

            notification.addAction(drawable, actionName, pausePendingIntent);
            notification.setContentTitle(title);
            notification.setContentText(message);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID.FOREGROUND_SERVICE, notification.build());
        }
    }

    private static void displayPromptForEnablingGPS(final AppCompatActivity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Do you want open GPS setting?";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                                ((MainActivity) activity).noPermission();
                            }
                        });
        builder.create().show();
    }

    private static void displayPromptForGettingPermission(final AppCompatActivity activity) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String message = "A need your permission to work probably\n" +
                "Do you want open App setting?";
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);


        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(intent);
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                                ((MainActivity) activity).noPermission();
                            }
                        });
        builder.create().show();
    }

    public static boolean checkPermission(AppCompatActivity context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Constants.displayPromptForGettingPermission(context);
        }
        return true;
    }

    public static boolean checkLocation(AppCompatActivity context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (!gps_enabled && !network_enabled) {
            Constants.displayPromptForEnablingGPS(context);
            return false;
        } else {
            return true;
        }
    }

    public static void saveLastAction(Context context,String action){
        SharedPreferences.Editor editor = context.getSharedPreferences(LAST_ACTION_PRE_NAME, MODE_PRIVATE).edit();
        editor.putString(LAST_ACTION_SAVE_NAME, action);
        editor.apply();
    }

    public static String getLastAction(){
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(LAST_ACTION_PRE_NAME, MODE_PRIVATE);
            return sharedPreferences.getString(LAST_ACTION_SAVE_NAME, ACTION.STOP_FOREGROUND_ACTION);
        }else{
            return "NOT_FOUND";
        }
    }

    public static float pxToDp(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static int getScreenWidth(AppCompatActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public static int getScreenHeight(AppCompatActivity context) {
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    public static void setToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TOKEN_DATA, token);
        editor.apply();
        editor.commit();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(TOKEN_STORAGE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TOKEN_DATA, NO_TOKEN);
    }

    public static String getErrorMessage(VolleyError error) {
        String temp = new String(error.networkResponse.data);
        try {
            JSONObject json = new JSONObject(temp);
            temp = json.getString("error_message");
        } catch (JSONException e) {
            e.printStackTrace();
            temp = "خطای سرور";
        }
        return temp;
    }

    public static void manageButton(String lastAction) {
        start_stop.setVisibility(View.VISIBLE);
        start_stop.setImageResource(R.mipmap.stop_icon);
        if (lastAction.equals(Constants.ACTION.RESUME_FOREGROUND_ACTION_BIKE) ||
                lastAction.equals(Constants.ACTION.RESUME_FOREGROUND_ACTION_STEP)) {
            pause_resume.setImageResource(R.mipmap.pause_icon);
        }
        if (lastAction.equals(Constants.ACTION.PAUSE_FOREGROUND_ACTION_BIKE) ||
                lastAction.equals(Constants.ACTION.PAUSE_FOREGROUND_ACTION_STEP)) {
            pause_resume.setImageResource(R.mipmap.play_icon);
        }
        if (lastAction.equals(Constants.ACTION.START_FOREGROUND_ACTION_BIKE)
                || lastAction.equals(Constants.ACTION.START_FOREGROUND_ACTION_STEP)) {
            pause_resume.setImageResource(R.mipmap.pause_icon);
        }
    }

    public static void getNotSendSession(MainActivity activity) {
        locationRepository repository = getRepository();
        repository.updateUnsynced(activity);
    }

    public static void sendJSONs() {
        repository.makeJsonAndSend();
    }

    private static JSONObject fileParser(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String all = "";
            String line = reader.readLine();
            while (line != null) {
                all = all.concat(line);
                line = reader.readLine();
            }
            return new JSONObject(all);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getStepCount(Context context) {
        if (stepCounter == null) {
            SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        }
        return (long) stepCounter.getResolution();
    }

    public static void setSession(long id) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.OPEN_SESSION_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Constants.HAVE_SESSION_ID,true);
        editor.putLong(Constants.CURRENT_SESSION_ID, id);
        editor.apply();
    }

    public static long getSession() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.OPEN_SESSION_ID, Context.MODE_PRIVATE);
        return sharedPref.getLong(Constants.CURRENT_SESSION_ID, -1);
    }

    public static void endSession(){
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.OPEN_SESSION_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Constants.HAVE_SESSION_ID,false);
        editor.putLong(Constants.CURRENT_SESSION_ID, -1);
        editor.apply();
    }

    private static HashMap<String, String> map = new HashMap<>();

    public static void saveUserSession(ProfileData data) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.OPEN_SESSION_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putStringSet(Constants.HAVE_SESSION_ID, data.toSet());
        editor.apply();
    }

    public static ProfileData getLastUserDataSession() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.OPEN_SESSION_ID, Context.MODE_PRIVATE);
        HashSet<String> set = (HashSet<String>) sharedPref.getStringSet(Constants.HAVE_SESSION_ID, null);
        return ProfileData.maker(set);
    }

    public static void makeDataBase() {
        TravelDataBase dataBase = Room.databaseBuilder(context.getApplicationContext(),
                TravelDataBase.class, "database-name").build();
        repository = locationRepository.get(dataBase);
        assert repository != null;
        repository.setUserLocationDao(dataBase.userDao());
        repository.setActionDao(dataBase.actionDao());
        repository.nukeTable();
    }

    public static locationRepository getRepository() {
        if (repository == null) {
            makeDataBase();
        }
        return repository;
    }

    public static boolean isActiveSession() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.OPEN_SESSION_ID, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(Constants.HAVE_SESSION_ID, false);
    }

    public static void trackEvent(String eventName) {
        Analytics.trackEvent(eventName, map);
    }

}
