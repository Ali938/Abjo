package coleo.com.abjo.server_class;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import coleo.com.abjo.activity.CodeActivity;
import coleo.com.abjo.activity.Login;
import coleo.com.abjo.activity.MainActivity;
import coleo.com.abjo.activity.SignUpActivity;
import coleo.com.abjo.activity.Splash;
import coleo.com.abjo.constants.Constants;
import coleo.com.abjo.data_class.LeaderBoardData;
import coleo.com.abjo.data_class.NewUserForServer;
import coleo.com.abjo.data_class.ProfileData;
import coleo.com.abjo.data_class.User;
import coleo.com.abjo.data_class.UserLevel;

import static coleo.com.abjo.constants.Constants.getErrorMessage;

public class ServerClass {

    private static final String TAG = "server class";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private static void saveToken(Context context, JSONObject request) {
        try {
            Constants.setToken(context, request.getString("token"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void handleError(Context context, VolleyError error) {
        if (error.networkResponse == null) {
            Toast.makeText(context, "اتصال اینترنت خود را بررسی کنید", Toast.LENGTH_SHORT).show();
        } else {
            if (error.networkResponse.statusCode == 403) {
                Constants.setToken(context, "");
                Intent intent = new Intent(context, Splash.class);
                context.startActivity(intent);
                ((Activity) context).finish();
                return;
            }
            Toast.makeText(context, getErrorMessage(error), Toast.LENGTH_SHORT).show();
        }
    }

    public static void checkPhone(final Context context, final String phone) {

        String url = Constants.URL_CHECK_PHONE;
        url += phone;
        url += "/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ((Login) context).goCode(phone);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((Login) context).enable();
                        handleError(context, error);
                    }
                });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public static void sendCode(final Context context, final String phone, final String code) {

        String url = Constants.URL_SEND_CODE;
        url += phone;
        url += "/";
        url += code;
        url += "/";

        ObjectRequest jsonObjectRequest = new ObjectRequest
                (context, Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                saveToken(context, response);
                                ((CodeActivity) context).goSignUp();
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            if (error.networkResponse != null) {
                                if (error.networkResponse.statusCode == 201) {
                                    String jsonString = new String(error.networkResponse.data);
                                    try {
                                        saveToken(context, new JSONObject(jsonString));
                                        ((CodeActivity) context).goMainPage();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ((CodeActivity) context).finish();
                                }
                            }
                        }
                        ((CodeActivity) context).wrongCode();
                        ServerClass.handleError(context, error);
                    }
                }
                );

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public static void makeNewUser(final Context context, final NewUserForServer user) {

        String url = Constants.URL_MAKE_USER;

        JSONObject mainJson = new JSONObject();
        JSONObject userJsonObject = new JSONObject();
        try {
            userJsonObject.put("first_name", user.getFirstName());
            userJsonObject.put("last_name", user.getLastName());
            userJsonObject.put("student_id", user.getStudentId());
            userJsonObject.put("is_woman", user.isWoman());
            userJsonObject.put("introduce_by", user.getIntroduceCode());
            userJsonObject.put("app_version", Constants.VERSION);
            userJsonObject.put("android_version", "" + Build.VERSION.SDK_INT);
            mainJson.put("user", userJsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ObjectRequest jsonObjectRequest = new ObjectRequest
                (context, Request.Method.POST, url, mainJson,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                saveToken(context, response);
                                ((SignUpActivity) context).goMain();
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ((SignUpActivity) context).enable();
                        ServerClass.handleError(context, error);
                    }
                }
                );

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public static void getProfile(final Context context) {

        String url = Constants.URL_GET_USER_PROFILE;

        ObjectRequest jsonObjectRequest = new ObjectRequest
                (context, Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                saveToken(context, response);
                                ((MainActivity) context).updateProfile(parseProfile(response));
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error != null) {
                            if (error.networkResponse != null) {
                                if (error.networkResponse.statusCode == 201) {
                                    String jsonString = new String(error.networkResponse.data);
                                    try {
                                        saveToken(context, new JSONObject(jsonString));
                                        ((CodeActivity) context).goSignUp();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    ((CodeActivity) context).finish();
                                }
                            }
                        }
                        ServerClass.handleError(context, error);
                    }
                }
                );

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

    }

    public static void getLeaderBoard(final Context context) {
        String url = Constants.URL_GET_LEADER_BOARD;

        ObjectRequest jsonObjectRequest = new ObjectRequest
                (context, Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i(TAG, "getLeaderBoard: " + response.toString());
                                saveToken(context, response);
                                ArrayList<LeaderBoardData> arrayList = new ArrayList<>();
                                try {
                                    JSONArray users = response.getJSONArray("users");
                                    for (int i = 0; i < users.length(); i++) {
                                        LeaderBoardData temp = parseLeaderBoardUser(users.getJSONObject(i));
                                        if (temp != null)
                                            arrayList.add(temp);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                ((MainActivity) context).updateLeaderBoard(arrayList);
                            }
                        }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ServerClass.handleError(context, error);
                    }
                }
                );

        MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    private static LeaderBoardData parseLeaderBoardUser(JSONObject raw) {
        try {
            String first = raw.getString("first_name");
            String last = raw.getString("last_name");
            int point = raw.getInt("point");
            int rank = raw.getInt("rank");
            boolean is_mine = raw .getBoolean("is_you");
            User user = new User(first, last, "", false);
            return new LeaderBoardData(user, rank, point,is_mine,false);//todo blur
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ProfileData parseProfile(JSONObject response) {
        Log.i(TAG, "parseProfile: " + response.toString());
        int coins, hours;
        String note;
        try {
            User user = parseUser(response, "user_data");
            JSONObject user_data = response.getJSONObject("user_data");
            coins = user_data.getInt("coins");
            hours = user_data.getInt("hours");
            try {
                note = user_data.getString("note");
            } catch (JSONException e) {
                note = " ";
            }
            UserLevel level = parseLevel(user_data);
            return new ProfileData(user, coins, hours, level, note);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static UserLevel parseLevel(JSONObject response) {
        return parseLevel(response, "user_level");
    }

    private static UserLevel parseLevel(JSONObject response, String from) {
        UserLevel level = new UserLevel();
        try {
            JSONObject data = response.getJSONObject(from);
            level.setPoint(data.getInt("user_point"));
            JSONObject user_level = data.getJSONObject("user_level");
            level.setLevelMaxPoint(user_level.getInt("capacity"));
            level.setLevel(user_level.getInt("number"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return level;
    }

    private static User parseUser(JSONObject response) {
        return parseUser(response, "user");
    }

    private static User parseUser(JSONObject response, String from) {
        Log.i(TAG, "parseUser: " + response.toString());
        String first,last,number;
        boolean isWoman;
        try {
            JSONObject user = response.getJSONObject(from);
            first = user.getString("first_name");
            last = user.getString("last_name");
            number = user.getString("phone");
            isWoman = user.getBoolean("is_woman");
            return new User(first,last,number,isWoman);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
