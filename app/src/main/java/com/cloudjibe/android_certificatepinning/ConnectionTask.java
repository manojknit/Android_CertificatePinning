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
                "BhMCVVMxEzARBgNVBAoTCkdvb2dsZSBJbmMxJTAjBgNVBAMTHEdvb2dsZSBJbnRl\n" +
                "cm5ldCBBdXRob3JpdHkgRzIwHhcNMTgwMzIwMTcwMzUxWhcNMTgwNjEyMTY1NDAw\n" +
                "WjBmMQswCQYDVQQGEwJVUzETMBEGA1UECAwKQ2FsaWZvcm5pYTEWMBQGA1UEBwwN\n" +
                "TW91bnRhaW4gVmlldzETMBEGA1UECgwKR29vZ2xlIEluYzEVMBMGA1UEAwwMKi5n\n" +
                "b29nbGUuY29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvNF+2SPX\n" +
                "t6qOLFVmKI+8KSWIGXy3YX0SUZphNc9s/X1YIe8HusrgCHxMtZGtHAcYZOrsNeNg\n" +
                "01p+uoGyR55A62PSBIgD7PQsuhAz/cvl+Unq1I56qNbhoZ/FPe1+v8UgVu2A7ycf\n" +
                "h3w3H0svdt2XXLEn36rI05DHlnxXEZXoIMuJtqTr8QUeLIWl6e2FzzXxtmsdJ50C\n" +
                "jmDZjGJvAsau++FeusOOtV+xwzdB9r7G1fjPtGrrHYRuaFJmvGnM3z+DcH0Ah2wB\n" +
                "F44EboLkuygjpAXIG44FSmNTmd9sXlfV7cG0zS5a9hZJUo1WxGNzmCgwK7ZH1OId\n" +
                "U0MhpQA6tv3khQIDAQABo4IFCzCCBQcwEwYDVR0lBAwwCgYIKwYBBQUHAwEwggPh\n" +
                "BgNVHREEggPYMIID1IIMKi5nb29nbGUuY29tgg0qLmFuZHJvaWQuY29tghYqLmFw\n" +
                "cGVuZ2luZS5nb29nbGUuY29tghIqLmNsb3VkLmdvb2dsZS5jb22CFCouZGI4MzM5\n" +
                "NTMuZ29vZ2xlLmNuggYqLmcuY2+CDiouZ2NwLmd2dDIuY29tghYqLmdvb2dsZS1h\n" +
                "bmFseXRpY3MuY29tggsqLmdvb2dsZS5jYYILKi5nb29nbGUuY2yCDiouZ29vZ2xl\n" +
                "LmNvLmlugg4qLmdvb2dsZS5jby5qcIIOKi5nb29nbGUuY28udWuCDyouZ29vZ2xl\n" +
                "LmNvbS5hcoIPKi5nb29nbGUuY29tLmF1gg8qLmdvb2dsZS5jb20uYnKCDyouZ29v\n" +
                "Z2xlLmNvbS5jb4IPKi5nb29nbGUuY29tLm14gg8qLmdvb2dsZS5jb20udHKCDyou\n" +
                "Z29vZ2xlLmNvbS52boILKi5nb29nbGUuZGWCCyouZ29vZ2xlLmVzggsqLmdvb2ds\n" +
                "ZS5mcoILKi5nb29nbGUuaHWCCyouZ29vZ2xlLml0ggsqLmdvb2dsZS5ubIILKi5n\n" +
                "b29nbGUucGyCCyouZ29vZ2xlLnB0ghIqLmdvb2dsZWFkYXBpcy5jb22CDyouZ29v\n" +
                "Z2xlYXBpcy5jboIUKi5nb29nbGVjb21tZXJjZS5jb22CESouZ29vZ2xldmlkZW8u\n" +
                "Y29tggwqLmdzdGF0aWMuY26CDSouZ3N0YXRpYy5jb22CCiouZ3Z0MS5jb22CCiou\n" +
                "Z3Z0Mi5jb22CFCoubWV0cmljLmdzdGF0aWMuY29tggwqLnVyY2hpbi5jb22CECou\n" +
                "dXJsLmdvb2dsZS5jb22CFioueW91dHViZS1ub2Nvb2tpZS5jb22CDSoueW91dHVi\n" +
                "ZS5jb22CFioueW91dHViZWVkdWNhdGlvbi5jb22CByoueXQuYmWCCyoueXRpbWcu\n" +
                "Y29tghphbmRyb2lkLmNsaWVudHMuZ29vZ2xlLmNvbYILYW5kcm9pZC5jb22CG2Rl\n" +
                "dmVsb3Blci5hbmRyb2lkLmdvb2dsZS5jboIcZGV2ZWxvcGVycy5hbmRyb2lkLmdv\n" +
                "b2dsZS5jboIEZy5jb4IGZ29vLmdsghRnb29nbGUtYW5hbHl0aWNzLmNvbYIKZ29v\n" +
                "Z2xlLmNvbYISZ29vZ2xlY29tbWVyY2UuY29tghhzb3VyY2UuYW5kcm9pZC5nb29n\n" +
                "bGUuY26CCnVyY2hpbi5jb22CCnd3dy5nb28uZ2yCCHlvdXR1LmJlggt5b3V0dWJl\n" +
                "LmNvbYIUeW91dHViZWVkdWNhdGlvbi5jb22CBXl0LmJlMGgGCCsGAQUFBwEBBFww\n" +
                "WjArBggrBgEFBQcwAoYfaHR0cDovL3BraS5nb29nbGUuY29tL0dJQUcyLmNydDAr\n" +
                "BggrBgEFBQcwAYYfaHR0cDovL2NsaWVudHMxLmdvb2dsZS5jb20vb2NzcDAdBgNV\n" +
                "HQ4EFgQUObC3YuhYs9WaL4xoRAoQ72EpjVYwDAYDVR0TAQH/BAIwADAfBgNVHSME\n" +
                "GDAWgBRK3QYWG7z2aLV29YG2u2IaulqBLzAhBgNVHSAEGjAYMAwGCisGAQQB1nkC\n" +
                "BQEwCAYGZ4EMAQICMDAGA1UdHwQpMCcwJaAjoCGGH2h0dHA6Ly9wa2kuZ29vZ2xl\n" +
                "LmNvbS9HSUFHMi5jcmwwDQYJKoZIhvcNAQELBQADggEBAF3vVbz5/6J5KR9XFu1m\n" +
                "wNAMx7v7j6VriqXLz6umZfMBJkLKQnIKGHQEnhXDIQj7R6HLS7kFGidRn9OQsBAw\n" +
                "AuqNevfd2SF+dajrCTKvpCC8T/sYomt0YJO/du1c37LwbUSsqguWZuE7cqZBfJaI\n" +
                "lWy4TV08sOqUWZsKPSXUMrt7ZoxNR0ojxyXTKrHYEd0aPu713kZQ4ixSstumQZ86\n" +
                "QOdp72qDs/DEqX9zxLWnpgAdD04INt31xlG12NI2lcoWaW4u4p5ZvD3rKISlSOeq\n" +
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


