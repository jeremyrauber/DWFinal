package com.projetos.ifpr.dwfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.utils.URLEncodedUtils;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;


public class MainActivity extends AppCompatActivity {

    EditText login, senha;
    ProgressDialog progress;
    Resposta[] respostas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = (EditText) findViewById(R.id.login);
        senha = (EditText) findViewById(R.id.senha);

        Button btnLogar = (Button) findViewById(R.id.btnLogar);

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progress = ProgressDialog.show(MainActivity.this, "Aguarde...",
                        "Verificando suas credenciais", true);

                ChamadaWeb chamada = new ChamadaWeb("http://10.0.2.2:8090/DW4Final/servicos/codigos",
                        login.getText().toString(), senha.getText().toString(), 1);
                chamada.execute();
            }
        });

    }


    private class ChamadaWeb extends AsyncTask<String, Void, String> {
        private String enderecoWeb;
        private String senhaWeb;
        private String loginWeb;
        private int tipoChamada;  //1 - GET 2 - POST


        public  ChamadaWeb(String endereco,  String login, String senha, int tipo){

            loginWeb = login;
            senhaWeb = senha;
            enderecoWeb = endereco;
            tipoChamada = tipo;

        }

        @Override
        protected String doInBackground(String... params) {
            HttpClient cliente = HttpClientBuilder.create().build();

            try {
                if(tipoChamada == 1){
                 try {
                        if (!enderecoWeb.endsWith("?"))
                            enderecoWeb += "?";

                        List<NameValuePair> parameters = new LinkedList<NameValuePair>();

                        if (!loginWeb.isEmpty())
                            parameters.add(new BasicNameValuePair("login", loginWeb));
                        if (!senhaWeb.isEmpty())
                            parameters.add(new BasicNameValuePair("senha", senhaWeb));

                        String paramString = URLEncodedUtils.format(parameters, "utf-8");

                        enderecoWeb += paramString;

                        System.out.println(enderecoWeb);

                        HttpGet chamada = new HttpGet(enderecoWeb);
                        HttpResponse resposta = cliente.execute(chamada);
                        return EntityUtils.toString(resposta.getEntity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else if(tipoChamada == 2){

                    HttpPost chamada = new HttpPost(enderecoWeb);
                    List<NameValuePair> parametros = new ArrayList<NameValuePair>(2); //o 2 eh referente ao numero de params

                    parametros.add(new BasicNameValuePair("login", loginWeb));
                    parametros.add(new BasicNameValuePair("senha", senhaWeb));

                    chamada.setEntity(new UrlEncodedFormEntity(parametros));
                    HttpResponse resposta = cliente.execute(chamada);
                    System.out.println(resposta);
                    String responseBody = EntityUtils.toString(resposta.getEntity()); // eh a resposta da servlet
                    return responseBody;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(String resultado){

            System.out.println(resultado);
            progress.dismiss();
            if(resultado != null){
                retornaMensagem(resultado);
            }else{
                Toast.makeText(MainActivity.this, "Você não está conectado! Verifique sua conexão com a internet", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void retornaMensagem(String resultado){
        Gson gson = new Gson();
        String a ="";


        if(!resultado.equals("nlogou")){
            
            respostas = gson.fromJson(resultado, Resposta[].class);
            System.out.println(">>>>>>>>>>>>>>>>>>>>"+ respostas[0].getCodigo()+" - "+respostas[0].getId()+ "\n" );

            for(Integer i=0;i<respostas.length;i++)
                a = a+ respostas[i].getId()+ " - "+respostas[i].getCodigo()+"\n";

            Intent intent = new Intent(getApplicationContext(), VisualizarCodigos.class);
            intent.putExtra("EXTRA_SESSION_ID", a);
            startActivity(intent);
        }
        else{
                Toast.makeText(this.getBaseContext(), "Usuário ou senha não conferem! Tente Novamente.", Toast.LENGTH_SHORT).show();
        }

    }


}
