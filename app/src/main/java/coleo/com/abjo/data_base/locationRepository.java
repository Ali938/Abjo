package coleo.com.abjo.data_base;

import android.content.Intent;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import coleo.com.abjo.activity.MainActivity;
import coleo.com.abjo.server_class.ServerClass;
import coleo.com.abjo.service.SaverReceiver;

import static coleo.com.abjo.constants.Constants.context;

public class locationRepository {

    private UserLocationDao userLocationDao;
    private PauseDao pauseDao;
    private TravelDataBase travelDataBase;
    private static locationRepository repository;

    public void close() {
        travelDataBase.close();
    }

    public void setUserLocationDao(UserLocationDao userLocationDao) {
        this.userLocationDao = userLocationDao;
    }

    public void setActionDao(PauseDao userLocationDao) {
        this.pauseDao = userLocationDao;
    }

    private locationRepository(TravelDataBase travelDataBase) {
        this.travelDataBase = travelDataBase;
        this.userLocationDao = travelDataBase.userDao();
    }

    public static locationRepository get(TravelDataBase travelDataBase) {
        if (repository != null) {
            return repository;
        } else {
            if (travelDataBase != null) {
                repository = new locationRepository(travelDataBase);
                return repository;
            }
            return null;
        }
    }

    public void insert(UserLocation location) {
        new insertAsyncTask(userLocationDao).execute(location);
    }

    public void insert(Action action) {
        new insertPauseAsyncTask(pauseDao).execute(action);
    }

    public void nukeTable() {
        new nukePauseAsyncTask(pauseDao).execute();
        new nukeAsyncTask(userLocationDao).execute();
    }

    public void makeJsonAndSend() {
        new getAsyncTask(userLocationDao, pauseDao).execute();
    }

    public void updateUnsynced(MainActivity activity) {
        CountAsyncTask temp = new CountAsyncTask(pauseDao);
        temp.execute(activity);
    }

    private static class insertPauseAsyncTask extends AsyncTask<Action, Void, Void> {

        private PauseDao mAsyncTaskDao;

        insertPauseAsyncTask(PauseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Action... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<UserLocation, Void, Void> {

        private UserLocationDao mAsyncTaskDao;

        insertAsyncTask(UserLocationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final UserLocation... params) {
            UserLocation temp = params[0];
            for (int i = 0; i < 3; i++) {
                mAsyncTaskDao.insertAll(temp);
            }
            return null;
        }
    }

    private static class nukeAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserLocationDao mAsyncTaskDao;

        nukeAsyncTask(UserLocationDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.nukeTable();
            return null;
        }
    }

    private static class nukePauseAsyncTask extends AsyncTask<Void, Void, Void> {

        private PauseDao mAsyncTaskDao;

        nukePauseAsyncTask(PauseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mAsyncTaskDao.nukeTable();
            return null;
        }
    }

    private static class getAsyncTask extends AsyncTask<Void, Void, JSONObject> {

        private UserLocationDao mAsyncTaskDao;
        private PauseDao pauseDao;

        getAsyncTask(UserLocationDao dao, PauseDao pauseDao) {
            this.pauseDao = pauseDao;
            mAsyncTaskDao = dao;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            return makeJson(mAsyncTaskDao.getNotSynced(), pauseDao.getNotSynced());
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            Intent intent = new Intent(context, SaverReceiver.class);
            context.stopService(intent);
            if (ServerClass.isNetworkConnected(context))
                ServerClass.sendActivityData(jsonObject, context);
            else{

            }
        }

        private JSONObject makeJson(List<UserLocation> locations, List<Action> actions) {
            JSONObject object = new JSONObject();
            JSONArray actionObjects = new JSONArray();
            try {
                for (Action p : actions) {
                    JSONObject temp = new JSONObject();
                    temp.put("session", p.id);
                    temp.put("number", p.number);
                    temp.put("time", p.time);
                    temp.put("action", p.action);
                    actionObjects.put(temp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (UserLocation location : locations) {
                JSONObject locationJsonObject = new JSONObject();
                try {
                    locationJsonObject.put("session", location.id);
                    locationJsonObject.put("lat", location.latitude);
                    locationJsonObject.put("lng", location.longitude);
                    locationJsonObject.put("acc", location.accuracy);
                    locationJsonObject.put("time", location.time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                object.put("actions", actionObjects);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return object;
        }

    }

    private static class CountAsyncTask extends AsyncTask<MainActivity, Void, Integer> {

        private PauseDao pauseDao;

        CountAsyncTask(PauseDao pauseDao) {
            this.pauseDao = pauseDao;
        }

        @Override
        protected Integer doInBackground(MainActivity... mainActivities) {
            List<Action> actions = pauseDao.getNotSynced();
            ArrayList<Long> seen = new ArrayList<>();
            for (Action action : actions) {
                if (!action.synced) {
                    if (!seen.contains(action.id)) {
                        seen.add(action.id);
                    }
                }
            }
            mainActivities[0].syncCount(seen.size());
            return null;
        }
    }

}
