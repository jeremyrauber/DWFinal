package com.projetos.ifpr.dwfinal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by jeremy on 21/06/2017.
 */

public class VisualizarCodigos extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visualizar_codigos);
        textView = (TextView) findViewById(R.id.visualizar);
        String s = getIntent().getStringExtra("EXTRA_SESSION_ID");
        System.out.println(s+" oi");
        textView.setText("Lista de vendas não validadas: \n Id -   Codigo                            \n"+s);

    }


}
