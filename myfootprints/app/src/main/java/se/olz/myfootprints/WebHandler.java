package se.olz.myfootprints;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.String.valueOf;

public class WebHandler {
    public static final String TAG = WebHandler.class.getSimpleName();
    private static final String PUSH_URL = "http://olz.se/my-footprints/webserver/push.php";
    private static final String PULL_URL = "http://olz.se/my-footprints/webserver/pull.php";
    private static final String LOGIN_URL = "http://olz.se/my-footprints/webserver/login.php";
    private static final String CREATEUSER_URL = "http://olz.se/my-footprints/webserver/createuser.php";
    private Context context;

    public WebHandler(Context context) {
        this.context = context;
    }

    public int createUser(final String email, final String password) {
        final int[] error = {0};
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(CREATEUSER_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    JSONObject jsonCredentials = new JSONObject();
                    jsonCredentials.put("email", email);
                    jsonCredentials.put("password", password);

                    String jsonString = jsonCredentials.toString();

                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setChunkedStreamingMode(jsonString.getBytes().length);

                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                    out.write(jsonString);
                    out.flush();
                    out.close();

                    InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());

                    StringBuilder sb = new StringBuilder();
                    int data;
                    while ((data = in.read()) != -1) {
                        sb.append((char) data);
                    }
                    //Log.d(TAG, "Inputstream " + sb.toString());
                    in.close();

                    JSONObject jsonRows = new JSONObject(sb.toString());
                    String token = jsonRows.getString("token");
                    if (token.equals("alreadyexists")) {
                        error[0] = -1;
                    } else {
                        DBUsers users = new DBUsers(context);
                        users.insert(email, token);
                    }

                    if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "HTTP Error");
                    }
                } catch (IOException e) {
                    Log.d(TAG, "IOException " + e.toString());
                } catch (Exception e) {
                    Log.d(TAG, "Exception " + e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.d(TAG, "InterruptedException " + e.toString());
        }
        return error[0];
    }

    public int login(final String email, final String password) {
        final int[] success = {-2};
        //Thread thread = new Thread(new Runnable() {
            //public void run() {
                try {
                    URL url = new URL(LOGIN_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    DBUsers dbUsers = new DBUsers(context);
                    int lastId;
                    boolean exist = dbUsers.exist(email);
                    if (exist) {
                        DBHelper dbHelper = new DBHelper(context, email);
                        lastId = dbHelper.getLastId();
                    } else {
                        lastId = 0;
                    }

                    JSONObject jsonCredentials = new JSONObject();
                    jsonCredentials.put("email", email);
                    jsonCredentials.put("password", password);
                    jsonCredentials.put("lastid", lastId);

                    String jsonString = jsonCredentials.toString();

                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setChunkedStreamingMode(jsonString.getBytes().length);

                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                    out.write(jsonString);
                    out.flush();
                    out.close();

                    InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());

                    StringBuilder sb = new StringBuilder();
                    int data;
                    while ((data = in.read()) != -1) {
                        sb.append((char) data);
                    }
                    //Log.d(TAG, "Inputstream " + sb.toString());
                    in.close();

                    JSONObject jsonRows = new JSONObject(sb.toString());
                    success[0] = jsonRows.getInt("success");
                    if (success[0] == 0) {
                        if (!exist) {
                            dbUsers.insert(email, jsonRows.getString("token"));
                        }
                        int id;
                        long session, accessedTimestamp;
                        double latitude, longitude;
                        JSONObject jsonData = jsonRows.getJSONObject("data");
                        Iterator<?> keys = jsonData.keys();
                        ArrayList<RawPositions> toInsert = new ArrayList<>();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject jsonRawPosition = (JSONObject) jsonData.get(key);
                            id = (jsonRawPosition).getInt("id");
                            session = (jsonRawPosition).getLong("session");
                            accessedTimestamp = jsonRawPosition.getLong("accessedTimestamp");
                            latitude = jsonRawPosition.getDouble("latitude");
                            longitude = jsonRawPosition.getDouble("longitude");
                            toInsert.add(new RawPositions(id, session, accessedTimestamp, latitude, longitude));
                        }
                        new User(email, jsonRows.getString("token"), jsonRows.getInt("lastid"));
                        dbUsers.updateToken();

                        DBHelper dbHelper = new DBHelper(context, email);
                        dbHelper.insertMultiple(toInsert);
                    }

                    if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "HTTP Error");
                    }

                } catch (IOException e) {
                    Log.d(TAG, "IOException " + e.toString());
                } catch (Exception e) {
                    Log.d(TAG, "Exception " + e.toString());
                }
          //  }
        //});
        //thread.start();
        //try {
        //    thread.join();
        //} catch (InterruptedException e) {
        //    Log.d(TAG, "InterruptedException " + e.toString());
        //}
        return success[0];
    }

    public int pull() {
        final int[] success = {-2};
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(PULL_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    DBHelper db = new DBHelper(context, User.getEmail());
                    JSONObject jsonCredentials = new JSONObject();
                    jsonCredentials.put("email", User.getEmail());
                    jsonCredentials.put("token", User.getToken());
                    jsonCredentials.put("lastid", db.getLastId());

                    String jsonString = jsonCredentials.toString();

                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setChunkedStreamingMode(jsonString.getBytes().length);

                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                    out.write(jsonString);
                    out.flush();
                    out.close();

                    InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());

                    StringBuilder sb = new StringBuilder();
                    int data;
                    while ((data = in.read()) != -1) {
                        sb.append((char) data);
                    }
                    //Log.d(TAG, "Inputstream " + sb.toString());
                    in.close();

                    JSONObject jsonRows = new JSONObject(sb.toString());
                    success[0] = jsonRows.getInt("success");
                    if (success[0] == 0) {
                        int id;
                        long session, accessedTimestamp;
                        double latitude, longitude;
                        JSONObject jsonData = jsonRows.getJSONObject("data");
                        Iterator<?> keys = jsonData.keys();
                        ArrayList<RawPositions> toInsert = new ArrayList<>();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject jsonRawPosition = (JSONObject) jsonData.get(key);
                            id = (jsonRawPosition).getInt("id");
                            session = (jsonRawPosition).getLong("session");
                            accessedTimestamp = jsonRawPosition.getLong("accessedTimestamp");
                            latitude = jsonRawPosition.getDouble("latitude");
                            longitude = jsonRawPosition.getDouble("longitude");
                            toInsert.add(new RawPositions(id, session, accessedTimestamp, latitude, longitude));
                        }
                        int lastId = jsonRows.getInt("lastid");
                        User.setServerLastId(lastId);
                        db.insertMultiple(toInsert);
                    }


                    if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "HTTP Error");
                    }

                } catch (IOException e) {
                    Log.d(TAG, "IOException " + e.toString());
                } catch (Exception e) {
                    Log.d(TAG, "Exception " + e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            Log.d(TAG, "InterruptedException " + e.toString());
        }
        return success[0];
    }

    public void push() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(PUSH_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    DBHelper dbHelper = new DBHelper(context, User.getEmail());

                    ArrayList<RawPositions> toInsert = dbHelper.getAfter(User.getServerLastId());

                    JSONObject jsonCredentials = new JSONObject();
                    jsonCredentials.put("email", User.getEmail());
                    jsonCredentials.put("token", User.getToken());

                    JSONObject jsonData = new JSONObject();
                    for (int i = 0; i < toInsert.size(); i++) {
                        JSONObject jsonRow = new JSONObject();
                        jsonRow.put("id", toInsert.get(i).getId());
                        jsonRow.put("session", toInsert.get(i).getSession());
                        jsonRow.put("accessedTimestamp", toInsert.get(i).getAccessedTimestamp());
                        jsonRow.put("latitude", toInsert.get(i).getLatitude());
                        jsonRow.put("longitude", toInsert.get(i).getLongitude());
                        jsonData.put(valueOf(i), jsonRow);
                    }
                    JSONObject jsonOut = new JSONObject();
                    jsonOut.put("credentials", jsonCredentials);
                    jsonOut.put("data", jsonData);

                    String jsonString = jsonOut.toString();

                    urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    urlConnection.setDoOutput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setChunkedStreamingMode(jsonString.getBytes().length);

                    OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream());
                    out.write(jsonString);
                    out.flush();
                    out.close();

                    if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        Log.d(TAG, "HTTP Error");
                    }

                } catch (IOException e) {
                    Log.d(TAG, "IOException " + e.toString());
                } catch (Exception e) {
                    Log.d(TAG, "Exception " + e.toString());
                }
            }
        }).start();

    }
}
