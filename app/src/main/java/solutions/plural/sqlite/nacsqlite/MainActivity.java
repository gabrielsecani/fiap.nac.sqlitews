package solutions.plural.sqlite.nacsqlite;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FilmeDAO filmeDAO;
    private ListView listView;
    private FilmeAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        filmeDAO = new FilmeDAO(this);

        listView = (ListView) findViewById(R.id.listView);
        listAdapter = new FilmeAdapter(this, new ArrayList<Filme>());
        listView.setAdapter(listAdapter);

        atualizarLista();
    }

    private void atualizarLista() {
        List<Filme> lista = filmeDAO.selectAll();
        Log.i("MAINACTIVITY", "lista: " + lista.size());
        if (lista.size() == 0) {
            new WSTask().execute();
        } else {
            listAdapter.clear();
            listAdapter.addAll(lista);
        }
    }

    public class WSTask extends AsyncTask<Void, Void, JSONArray> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "loading...", "Buscando dados do WebService", true);
            Log.i("WSTASK", "onPreExecute");
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            Log.i("WSTASK", "doInBackground");
            try {
                URL url = new URL("http://ws.qoala.com.br/ITIssues/itflix");
                //URL url = new URL("http://localhost:52444/ITIssues/itflix");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    connection.setRequestMethod("GET");
                    //connection.setRequestProperty("Accept", "application/json");

                    String linha;
                    StringBuilder builder = new StringBuilder();
                    BufferedReader stream;
                    try {
                        InputStream is;
                        try {
                            is = connection.getInputStream();
                            Log.e("WSTASK", "Get OK... reading data");
                        } catch (Exception ex) {
                            Log.e("WSTASK", ex.getMessage(), ex);
                            ex.printStackTrace();
                            return null;
                        }

                        InputStreamReader isr = new InputStreamReader(is);
                        stream = new BufferedReader(isr);

                        while ((linha = stream.readLine()) != null) {
                            builder.append(linha);
                        }

                    } catch (Exception e) {
                        Log.e("WSTASK", "Erro lendo retorno!", e);
                    }
                    if (builder.length() == 0)
                        builder.append("[{}]");

                    JSONArray retArray = new JSONArray(builder.toString());
                    return retArray;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Log.e("WSTASK", "Error: " + ex.getMessage(), ex);
                } finally {
                    connection.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.e("WSTASK", "Get: " + ex.getMessage(), ex);
            }

            Log.i("WSTASK", "doInBackground return null");
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            Log.i("WSTASK", "onPostExecute: " + jsonArray.toString());
            try {
                if (jsonArray != null) {

                    try {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            int codigo = obj.getInt("codigo");
                            int tempo = obj.getInt("tempo");
                            String descricao = obj.getString("descricao");
                            filmeDAO.insert(new Filme(codigo, tempo, descricao));
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            } finally {
                progressDialog.dismiss();
            }
            atualizarLista();
        }
    }
}
