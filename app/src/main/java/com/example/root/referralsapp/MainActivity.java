package com.example.root.referralsapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.host;

public class MainActivity extends AppCompatActivity {

    private SeekBar cookingQuery;
    private SeekBar houseQuery;
    private SeekBar educateQuery;
    private SeekBar entertainQuery;
    private SeekBar cookingAns;
    private SeekBar houseAns;
    private SeekBar educateAns;
    private SeekBar entertainAns;
    private Button queryBtn;


    private TextView cookQ,houseQ,educateQ,entertainQ,cookA,houseA,educateA,entertainA;


    private final String defaultActor = "default/";
    private final String urlName = "query/";
    //private final String hostName="http://10.0.2.2:9000/";
    private final String hostName = "https://referral-server-9x.herokuapp.com/";


    private double[] query = new double[4];

   // private TextView prog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cookingQuery = (SeekBar) findViewById(R.id.cookingQBar);
        houseQuery = (SeekBar) findViewById(R.id.HRQBar);
        educateQuery = (SeekBar) findViewById(R.id.educateQBar);
        entertainQuery = (SeekBar) findViewById(R.id.entertainQBar);
        cookingAns = (SeekBar) findViewById(R.id.cookingABar);
        houseAns = (SeekBar) findViewById(R.id.HRABar);
        educateAns = (SeekBar) findViewById(R.id.educateABar);
        entertainAns = (SeekBar) findViewById(R.id.entertainABar);
        queryBtn = (Button) findViewById(R.id.button);

        cookQ = (TextView) findViewById(R.id.textView12);
        houseQ = (TextView) findViewById(R.id.textView5);
        educateQ = (TextView) findViewById(R.id.textView11);
        entertainQ = (TextView) findViewById(R.id.textView10);
        cookA = (TextView) findViewById(R.id.textView16);
        houseA = (TextView) findViewById(R.id.textView15);
        educateA = (TextView) findViewById(R.id.textView17);
        entertainA = (TextView) findViewById(R.id.textView18);

        cookingAns.setEnabled(false);
        houseAns.setEnabled(false);
        educateAns.setEnabled(false);
        entertainAns.setEnabled(false);

        cookingQuery.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                Log.i("Seekbar progress for 1 ",String.valueOf(progress/100.0));
                query[0] = (progress/100.0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                cookQ.setText(String.valueOf(seekBar.getProgress()/100.0));
            }
        });

        houseQuery.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
               Log.i("Seekbar progress for 2",String.valueOf(progress/100.0));
                query[1] = (progress/100.0);

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                houseQ.setText(String.valueOf(seekBar.getProgress()/100.0));
            }
        });
        educateQuery.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                Log.i("Seekbar progress for 3",String.valueOf(progress/100.0));
                query[2] = (progress/100.0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                educateQ.setText(String.valueOf(seekBar.getProgress()/100.0));
            }
        });
        entertainQuery.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                Log.i("Seekbar progress for 4",String.valueOf(progress/100.0));
                query[3] = (progress/100.0);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                entertainQ.setText(String.valueOf(seekBar.getProgress()/100.0));
            }
        });

        queryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                apiCallForQuery();
            }
        });

    }

    public void apiCallForQuery() {
        RequestQueue q = Volley.newRequestQueue(MainActivity.this);
        String queryParams = "";
        for (int i = 0; i < query.length; i++) {
            queryParams = queryParams + String.valueOf(query[i]) + ",";
        }
        queryParams = stringTruncate(queryParams);
        Log.i("Query generated: ", queryParams);
        String url =  hostName + defaultActor + urlName + queryParams;
        //Log.i("My final url", url);
        try {
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.i("From the server : ",response.toString());
                            double[] answer = handleJsonResponse(response);
                            updateUI(answer);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO Auto-generated method stub
                            Log.i("Response Error", "Volley network Error");
                        }
                    });


            // Add the request to the RequestQueue.
            q.add(jsObjRequest);
        } catch (Exception e) {
            Log.i("Exception found", e.getMessage());
        }
    }

    public String stringTruncate(String str) {
        if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ',') {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public double[] handleJsonResponse(JSONObject response) {
        String status = "";
        double[] answerArray = new double[4];
        JSONArray array = new JSONArray();
        try {
            status = response.getString("status");
            if (status.equalsIgnoreCase("error")) {
                Log.i("Error Response", "Error Obtained in the result");
                Toast.makeText(getApplicationContext(),"Could not obtain the result",Toast.LENGTH_SHORT).show();
            } else if (status.equalsIgnoreCase("success")) {

                array = response.getJSONArray("answer");
                answerArray[0] = array.getDouble(0);
                answerArray[1] = array.getDouble(1);
                answerArray[2] = array.getDouble(2);
                answerArray[3] = array.getDouble(3);

                //Log.i("MyArray",String.valueOf(answerArray));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answerArray;
    }

    public void updateUI(double[] answer) {
        int progress = (int) (answer[0] * 100);
        cookingAns.setProgress(progress);
        cookA.setText(String.valueOf(progress/100.0));
        progress = (int) (answer[1] * 100);
        houseAns.setProgress(progress);
        houseA.setText(String.valueOf(progress/100.0));
        progress = (int) (answer[2] * 100);
        educateAns.setProgress(progress);
        educateA.setText(String.valueOf(progress/100.0));
        progress = (int) (answer[3] * 100);
        entertainAns.setProgress(progress);
        entertainA.setText(String.valueOf(progress/100.0));
    }
}
