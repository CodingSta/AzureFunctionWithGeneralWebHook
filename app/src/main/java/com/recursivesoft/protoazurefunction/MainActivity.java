package com.recursivesoft.protoazurefunction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView textViewA;
    HttpURLConnection conn = null;
    final String TAG = "MainActivity";
    int numA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewA = (TextView)findViewById(R.id.textView);
    }

    public void testFunctionWithPOST(View v) {

        Thread threadA = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("TEST","testFunctionWithPOST");
                    String query = URLEncoder.encode("X1INNHwS1r5ldFFuYs/xSzBPRAz1W9z54kokk9C77uaBACDASE1aQ==", "utf-8");

                    // 송신 처리 : Azure Function - Factorial 출력.
                    URL url = new URL("https://recursivesoft.azurewebsites.net/api/MyAppFunctionCS?code="+query);
                    conn = (HttpURLConnection)url.openConnection();
                    if (conn != null) {
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(1000);
                        conn.setRequestMethod("POST");
                        conn.setUseCaches(false);   // 컨트롤 캐쉬 설정
                        conn.setDoInput(true);      // InputStream으로 서버로 부터 응답을 받겠다.
                        conn.setDoOutput(true);     // OutputStream으로 POST 데이터를 넘겨주겠다.
                        // Request Header값 셋팅
                        conn.setRequestProperty("Content-Type", "application/json");    // 서버로 Request Body 전달시 json 일 때.
                        conn.setRequestProperty("Accept", "application/json");          // 서버 Response Data를 json 으로 요청.

                        OutputStream os = conn.getOutputStream();   // // Request Body에 Data를 담기위해 OutputStream 객체 생성, internally change to 'POST'
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8")); //캐릭터셋 설정
                        // Sample 은 다음 처럼 "{\"first\":\"train\", \"last\":128}"
                        writer.write(
                                "{\"first\":1, \"last\":" + numA++ + "}"
                        );
                        writer.flush();
                        writer.close();
                        os.close();

                        // 실제 서버로 Request 요청 하여 응답 수신.
                        final int responseCode = conn.getResponseCode();
                        if(responseCode == HttpURLConnection.HTTP_OK) {
                            Log.d(TAG,"Connection OK");

                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = br.readLine()) != null) {
                                if(sb.length() > 0) {
                                    sb.append("\n");
                                }
                                sb.append(line);
                            }
                            final String resultA = sb.toString();
                            Log.d("response: ", resultA);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewA.setText(resultA);
                                }
                            });
                        } else {
                            Log.d(TAG,"Connection Error :" + responseCode);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textViewA.setText("Connection Error :" + responseCode);
                                }
                            });
                        }
                    }
                } catch(Exception ex) {
                    Log.d(TAG, "Exception: "+ex);
                    ex.printStackTrace();
                }  finally {
                    if(conn != null) {
                        conn.disconnect();
                    }
                }
            }
        });
        threadA.start();
    }
}
