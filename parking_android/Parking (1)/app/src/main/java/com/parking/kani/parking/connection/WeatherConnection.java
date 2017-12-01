package com.parking.kani.parking.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.parking.kani.parking.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kani on 2017-01-11.
 */

public class WeatherConnection extends AsyncTask<String, Void, String>
{
    Context context;
    TextView textView;

    public WeatherConnection(Context context, TextView textView)
    {
        this.context = context;
        this.textView = textView;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        URL url = null;
        BufferedReader br = null;

        String appKey = context.getString(R.string.TMAP_API_KEY1);
        String serverURL = "http://apis.skplanetx.com/weather/current/minutely?version=1";
        double lat = Double.parseDouble(strings[0]);
        double lon = Double.parseDouble(strings[1]);
        lat = 36.9690829;
        lon = 127.8692609;
        serverURL += "&appKey=" + appKey;
        serverURL += "&lat=" + lat + "&lon=" + lon;

        Log.e("Weather Server URL", serverURL);

        String result = "";

        try
        {
            url = new URL(serverURL);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);

            br=new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder stringBuilder=new StringBuilder();
            String str="";

            while((str=br.readLine())!=null)
            {
                stringBuilder.append(str+"\n");
            }

            Log.e("WeatherServerResponse", stringBuilder.toString());

            /*
            minutely
			    station
			    wind
			    precipitation
			        sinceOntime
			        type
			    sky
			        code
			        name
			    rain
			    temperature
			        tc
			        tmax
			        tmin
			    humidity
			    pressure
			    lightning
			    timeObservation
             */

            //JSON데이터를 넣어 JSON Object 로 만들어 준다.
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            JSONObject weatherObject = (JSONObject) jsonObject.get("weather");
            JSONArray minutelyArray = (JSONArray) weatherObject.get("minutely");
            JSONObject tempObject = (JSONObject) minutelyArray.get(0);

            JSONObject precipitationObject = (JSONObject) tempObject.get("precipitation");
            String typeObject = precipitationObject.get("type").toString();

            JSONObject skyObject = (JSONObject) tempObject.get("sky");
            String codeObject = skyObject.get("code").toString();
            String nameObject = skyObject.get("name").toString();

            JSONObject temperatureObject = (JSONObject) tempObject.get("temperature");
            String tcObject = temperatureObject.get("tc").toString();
            String tmaxObject = temperatureObject.get("tmax").toString();
            String tminObject = temperatureObject.get("tmin").toString();

            String humidityObject = tempObject.get("humidity").toString();

            Log.d("WeatherResponse1", "precipitation");
            Log.d("type(강수형태코드)",  typeObject + "\n");
            Log.d("WeatherResponse2", "sky");
            Log.d("code(하늘상태코드)",  codeObject);
            Log.d("name(하늘상태코드명)",  nameObject + "\n");
            Log.d("WeatherResponse3", "temperature");
            Log.d("tc(현재기온)",  tcObject);
            Log.d("tmax(최고기온)", tmaxObject);
            Log.d("tmin(최저기온)", tminObject + "\n");
            Log.d("WeatherResponse4", "humidity");
            Log.d("humidity(상대습도)", humidityObject);

            result = "";
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block

            Log.e("WeatherServerError", "에러발생");
            e.printStackTrace();

            result = "fail";
        }

        return result;
    }

    @Override
    protected void onPostExecute(String str)
    {
        Log.e("WeatherServerResult", str);

        if( !str.equals("fail") )
        {

        }
        else
        {

        }
    }
}