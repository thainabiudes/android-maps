package br.com.up.mypins.connection;

import android.os.AsyncTask;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


//tipagem do asyncTask - primeiro é o parâmetro do doInBackground, o segundo onProgressUpdate, e o terceiro do onPostExecute
public class ConnectionAsyncTask extends AsyncTask<String, Void, String> {
private OnRequestListener listener;

public ConnectionAsyncTask(OnRequestListener listener){
    this.listener = listener;
}
    //tudo que estiver dentro do método é assíncrono
@Override
protected String doInBackground(String... strings) {
    String urlString = strings[0];

    try {
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        if(connection.getResponseCode() == 200){
            InputStream inputStream = connection.getInputStream();

            //converter bytes concatenados e converte-los em string
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);

        }else {
           return null;
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
}

//método que nos ajuda a fazer ponte entre o que é assíncrono e o que será mostrado na interface
//o parâmetro a ser recebido é o que retorna do doInBackground
@Override
protected void onPostExecute(String s) {
    super.onPostExecute(s);

    try {
        JSONObject object = new JSONObject(s);
        listener.onRequestFinish(object);
    } catch (Exception e){
        listener.onRequestFinish(null);
    }

}

public interface OnRequestListener {
    void onRequestFinish(JSONObject object  );
}
}

