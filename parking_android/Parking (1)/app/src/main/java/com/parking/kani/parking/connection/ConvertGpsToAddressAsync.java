package com.parking.kani.parking.connection;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Kani on 2017-01-10.
 */

public class ConvertGpsToAddressAsync extends AsyncTask<String, Void, String>
{
    TextView textView;

    public ConvertGpsToAddressAsync(TextView textView)
    {
        this.textView = textView;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        return strings[0];
    }

    @Override
    protected void onPostExecute(String str)
    {
            textView.setText(str);
            Log.e("222222222", "@" + str + "@");
    }
}
