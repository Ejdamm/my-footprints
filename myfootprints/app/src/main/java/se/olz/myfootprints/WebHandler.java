package se.olz.myfootprints;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import static java.lang.String.valueOf;

public class WebHandler {
    public static final String TAG = WebHandler.class.getSimpleName();

    /*private static final String PUSH_URL = "http://olz.se/my-footprints/webserver/push.php";
    private static final String PULL_URL = "http://olz.se/my-footprints/webserver/pull.php";
    private static final String LOGIN_URL = "http://olz.se/my-footprints/webserver/login.php";
    private static final String CREATEUSER_URL = "http://olz.se/my-footprints/webserver/createuser.php";*/

    private static final String PUSH_URL = "http://192.168.1.4/my-footprints/webserver/push.php";
    private static final String PULL_URL = "http://192.168.1.4/my-footprints/webserver/pull.php";
    private static final String LOGIN_URL = "http://192.168.1.4/my-footprints/webserver/login.php";
    private static final String CREATEUSER_URL = "http://192.168.1.4/my-footprints/webserver/createuser.php";

    private Context context;

    public WebHandler(Context context) {
        this.context = context;
    }

    private String jsonBuilder(String email, String password, int lastId, JSONObject data) throws Exception {
        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);
        if (lastId != -1) {
            json.put("lastid", lastId);
        }
        if (data != null) {
            json.put("data", data);
        }
        return json.toString();
    }

    private HttpURLConnection connect(URL url, int length) throws IOException {
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        urlConn.setDoOutput(true);
        //urlConn.setRequestMethod("POST");
        urlConn.setChunkedStreamingMode(length);
        return urlConn;
    }

    private void send(HttpURLConnection urlConn, String jsonString) throws IOException {
        OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream());
        out.write(jsonString);
        out.flush();
        out.close();
    }

    private String recieve(HttpURLConnection urlConn) throws IOException {
        InputStream inStream = urlConn.getInputStream();
        InputStreamReader in = new InputStreamReader(inStream);
        StringBuilder sb = new StringBuilder();
        int data;
        while ((data = in.read()) != -1) {
            sb.append((char) data);
        }
        Log.d(TAG, "Inputstream " + sb.toString());
        in.close();
        return sb.toString();
    }

    private void insertNewRows(JSONObject jsonRows, String email) throws JSONException {
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
        DBHelper db = new DBHelper(context, email);
        db.insertMultiple(toInsert);
    }



    public int createUser(final String email, final String password) {
        final int[] error = {0};
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(CREATEUSER_URL);
                    String jsonString = jsonBuilder(email, password, -1, null);
                    HttpURLConnection urlConn = connect(url, jsonString.getBytes().length);
                    send(urlConn, jsonString);
                    String resultString = recieve(urlConn);

                    JSONObject jsonRows = new JSONObject(resultString);
                    String token = jsonRows.getString("token");
                    if (token.equals("alreadyexists")) {
                        error[0] = -1;
                    } else {
                        DBUsers users = new DBUsers(context);
                        users.insert(email, token);
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
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    DBUsers dbUsers = new DBUsers(context);
                    int lastId;
                    boolean exist = dbUsers.exist(email);
                    if (exist) {
                        DBHelper dbHelper = new DBHelper(context, email);
                        lastId = dbHelper.getLastId();
                    } else {
                        lastId = 0;
                    }

                    String jsonString = jsonBuilder(email, password, lastId, null);
                    URL url = new URL(LOGIN_URL);
                    HttpURLConnection urlConn = connect(url, jsonString.getBytes().length);
                    send(urlConn, jsonString);
                    String resultString = recieve(urlConn);

                    final JSONObject jsonRows = new JSONObject(resultString);
                    success[0] = jsonRows.getInt("success");
                    if (success[0] == 0) {
                        if (!exist) {
                            dbUsers.insert(email, jsonRows.getString("token"));
                        }
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    insertNewRows(jsonRows, email);
                                } catch (JSONException e) {
                                    Log.w(TAG, "Exception " + e.toString());
                                }
                            }
                        }).start();
                        new User(email, jsonRows.getString("token"), jsonRows.getInt("lastid"));
                        dbUsers.updateToken();
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

    public int pull() {
        final int[] success = {-2};
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    DBHelper db = new DBHelper(context, User.getEmail());
                    String jsonString = jsonBuilder(User.getEmail(), User.getToken(), db.getLastId(), null);
                    URL url = new URL(PULL_URL);
                    HttpURLConnection urlConn = connect(url, jsonString.getBytes().length);
                    send(urlConn, jsonString);
                    String resultString = recieve(urlConn);

                    final JSONObject jsonRows = new JSONObject(resultString);
                    success[0] = jsonRows.getInt("success");
                    if (success[0] == 0) {
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    insertNewRows(jsonRows, User.getEmail());
                                } catch (JSONException e) {
                                    Log.w(TAG, "Exception " + e.toString());
                                }
                            }
                        }).start();
                        int lastId = jsonRows.getInt("lastid");
                        User.setServerLastId(lastId);

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
                    DBHelper dbHelper = new DBHelper(context, User.getEmail());
                    int lastId = User.getServerLastId();
                    ArrayList<RawPositions> toInsert = dbHelper.getAfter(lastId);
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

                    String jsonString = jsonBuilder(User.getEmail(), User.getToken(), -1, jsonData);
                    URL url = new URL(PUSH_URL);
                    HttpURLConnection urlConn = connect(url, jsonString.getBytes().length);
                    send(urlConn, jsonString);
                    String resultString = recieve(urlConn);

                    final JSONObject jsonRows = new JSONObject(resultString);
                    if (jsonRows.getInt("success") == 0);
                        User.setServerLastId(jsonRows.getInt("lastid"));

                } catch (IOException e) {
                    Log.d(TAG, "IOException " + e.toString());
                } catch (Exception e) {
                    Log.d(TAG, "Exception " + e.toString());
                }
            }
        }).start();
    }
}