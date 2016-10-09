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
    private String email;
    private String token;
    private DBHelper db;

    public WebHandler(Context context) {
        this.email =  User.getEmail();
        this.token =  User.getToken();
        this.db = new DBHelper(context, email);
    }

    public void pull() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(PULL_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    JSONObject jsonCredentials = new JSONObject();
                    jsonCredentials.put("email", email);
                    jsonCredentials.put("token", token);
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

                    if (jsonRows.getString("success").equals("1")) {
                        int id;
                        long session, accessedTimestamp;
                        double latitude, longitude;
                        jsonRows = jsonRows.getJSONObject("data");
                        Iterator<?> keys = jsonRows.keys();
                        ArrayList<RawPositions> toInsert = new ArrayList<>();
                        while (keys.hasNext()) {
                            String key = (String) keys.next();
                            JSONObject jsonRawPosition = (JSONObject) jsonRows.get(key);
                            id = (jsonRawPosition).getInt("id");
                            session = (jsonRawPosition).getLong("session");
                            accessedTimestamp = jsonRawPosition.getLong("accessedTimestamp");
                            latitude = jsonRawPosition.getDouble("latitude");
                            longitude = jsonRawPosition.getDouble("longitude");
                            toInsert.add(new RawPositions(id, session, accessedTimestamp, latitude, longitude));
                        }

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
        }).start();
    }

    public void push(final int latest) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    URL url = new URL(PUSH_URL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    ArrayList<RawPositions> toInsert = db.getAfter(latest);

                    JSONObject jsonCredentials = new JSONObject();
                    jsonCredentials.put("email", email);
                    jsonCredentials.put("token", token);

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
