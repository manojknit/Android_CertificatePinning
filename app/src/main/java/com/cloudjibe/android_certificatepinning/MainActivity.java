package com.cloudjibe.android_certificatepinning;

import java.io.IOException;

import android.support.v4.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    public static TextView myText;
    public static Activity this_app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this_app = this;

        myText= (TextView)findViewById(R.id.textView2);

    }

    // button onClick method
    public void startSSLGoogle(View v) throws IOException {
        myText.setText("");
        URL url = new URL("https://www.google.com"); //Should show trust. Validating google cert
        ConnectionTask task = new ConnectionTask(this, url);
        task.execute();

    }
    public void startSSLYahoo(View v) throws IOException {
        myText.setText("");
        URL url = new URL("https://www.yahoo.com");
        ConnectionTask task = new ConnectionTask(this, url);
        task.execute();

    }

}

