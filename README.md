# Android app to demonstrate Certificate Pinning
####                                                                                                     By Manoj Kumar
## Introduction 
Android mobile app using Certificate Pinning which plays an important role in preventing Man-in-the-middle attack.
In this app I am going to validate pre downloaded google.com certificate with google site and yahoo site.

## How to Run
1.	Prerequisite: Android Studio, some Java knowledge
2.	Download or clone project code and open in Android studio.
3.	Run in Nexus 5X API 26 emulator.


## Technologies Used
1.	Java.
2.	Android Studio

## Steps to generate bks file
### Mac terminal command to generate pem file
```
openssl s_client -showcerts -connect google.com:443 </dev/null 2>/dev/null|openssl x509 -outform PEM >mycertfile.pem
```
### Place following jar file in same directory where you are running command
/help/bcprov-ext-jdk15on-1.46.jar

### Mac terminal command to generate pem file
```
keytool -importcert -v -trustcacerts -file "mycertfile.pem" -alias ca -keystore "keystore.bks" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "bcprov-ext-jdk15on-1.46.jar" -storetype BKS -storepass testing
```
### If you want to user crt file for pinning you can download from browser. Also can check certificate as follows.
```
openssl s_client -connect google.com:443 | openssl x509 -text
```

## Application Code and Screenshots 
Testing google.com. as cerificate maches, it shows Trusted.
<img src="images/Android Emulator - Nexus_5X_API_265554 2018-04-07 23-24-16(1).png">
Testing yahoo.com. as cerificate does not mache, it shows Trusted.
<img src="images/ Android Emulator - Nexus_5X_API_265554 2018-04-07 23-24-40(1).png">

## Code: ConnectionTask.java
```
package com.cloudjibe.android_certificatepinning;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.util.Log;

public class ConnectionTask extends AsyncTask<Void, Void, Object> {
    private Context mContext;
    private URL url;

    public ConnectionTask(Context context, URL url1) {
        mContext = context; url= url1;
    }

    @Override
    protected Object doInBackground(Void... params) {

        Object result = null;

        try {
            //URL url = new URL("https://www.facebook.com");
            //URL url = new URL("https://www.google.com"); // trust only this
            // site
            InputStream in = makeRequest(mContext, url);
            copyInputStreamToOutputStream(in, System.out);

        } catch (Exception ex) {

            // Log error
            Log.e("doInBackground", ex.toString());

            // Prepare return value
            result = (Object) ex;
        }
//        String publicKey = "";
//        try {
//            publicKey = getCertificatePublicKey(url);
//        }
//        catch (Exception e) {
//            //log/handle the exception
//            Log.e("getCertificatePublicKey", e.toString());
//        }

        return result;
    }

    @Override
    protected void onPostExecute(Object result) {
        // MainActivity.myText.setText("Test");
        String expectedPublicKey = "Expected Public Key:\n MIIIPjCCByagAwIBAgIIfHYndDN+y4UwDQYJKoZIhvcNAQELBQAwSTELMAkGA1UE\n" +
                "D5Hdc++4f0JfP6r+Dh36AGvF0BjS04orXESx5fmKR2/b2QbhG7c0VpqOHThQW03d\n" +
                "XG4=";

        if (result instanceof Exception) {
            //MainActivity.myText.setText("Untrusted Certificate :-( \n\n"
            //        + result);
            MainActivity.myText.setText("Untrusted Certificate :-( \n\n"
                            + expectedPublicKey);
            return;
        }
        MainActivity.myText.setText(" Trusted Certificate :-) \n\n"
                + expectedPublicKey);
    }

    private void copyInputStreamToOutputStream(InputStream in, PrintStream out) {
        // TODO Auto-generated method stub
        byte[] buffer = new byte[1024]; // Adjust if you want
        int bytesRead = 0;
        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.i("TAG", String.valueOf(bytesRead));

    }

    //using bks
//    private InputStream makeRequest(Context context, URL url)
//            throws IOException, KeyStoreException, NoSuchAlgorithmException,
//            CertificateException, KeyManagementException {
//        AssetManager assetManager = context.getAssets();
//        InputStream keyStoreInputStream = assetManager.open("keystore.bks");
//        KeyStore trustStore = KeyStore.getInstance("BKS");
//
//        trustStore.load(keyStoreInputStream, "testing".toCharArray());
//
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
//        tmf.init(trustStore);
//
//        SSLContext sslContext = SSLContext.getInstance("TLS");
//        sslContext.init(null, tmf.getTrustManagers(), null);
//
//        HttpsURLConnection urlConnection = (HttpsURLConnection) url
//                .openConnection();
//        urlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
//
//        return urlConnection.getInputStream();
//    }

    //Cert
    private InputStream makeRequest(Context context, URL url)
            throws IOException, KeyStoreException, NoSuchAlgorithmException,
            CertificateException, KeyManagementException {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        // From https://google.com
        //InputStream caInput = new BufferedInputStream(new FileInputStream("wwwgooglecom.crt"));
        AssetManager assetManager = context.getAssets();
        InputStream caInput = assetManager.open("wwwgooglecom.crt");
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

// Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
        SSLContext context1 = SSLContext.getInstance("TLS");
        context1.init(null, tmf.getTrustManagers(), null);

// Tell the URLConnection to use a SocketFactory from our SSLContext
       // URL url = new URL("https://www.google.com/");
        HttpsURLConnection urlConnection =
                (HttpsURLConnection)url.openConnection();
        //urlConnection.setSSLSocketFactory(context1.getSocketFactory());

        //HttpsURLConnection.setDefaultSSLSocketFactory(context1.getSocketFactory());
        urlConnection.setDefaultSSLSocketFactory(context1.getSocketFactory());

        return urlConnection.getInputStream();
    }

    private String getCertificatePublicKey(URL url) throws Exception
    {
        int port = 443;
        String strCert = "";
        String hostname = "google.com";
        SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket(hostname, 443);
        socket.startHandshake();
        Certificate[] certs = socket.getSession().getPeerCertificates();
        Certificate cert = certs[0];
        PublicKey key = cert.getPublicKey();
        strCert = key.toString();
        socket.close();
        return strCert;
    }

}

```
## Code: MainActivity.java
```
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
```
## Refrence
- [*Pro Android 5*](https://github.com/Apress/pro-android-5) by Dave MacLean, Satya Komatineni, and Grant Allen (Apress, 2015)

## Thank You
#### [*Manoj Kumar*](https://www.linkedin.com/in/manojkumar19/)
#### Solutions Architect
