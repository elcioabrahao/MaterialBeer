package br.com.qpainformatica.materialbeer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import br.com.qpainformatica.materialbeer.R;
import fr.ganfra.materialspinner.MaterialSpinner;

public class BeerChooserActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private MaterialSpinner spinner1;
    private MaterialSpinner spinner2;
    private MaterialSpinner spinner3;
    private Button button;

    private String estilo="";
    private String cor="";
    private String pais="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_chooser);


        String[] ITEMS1 = {"Selecionar o Estilo","india pale ale",
                "lager",
                "pilsner",
                "porter",
                "stout",
                "trapistas",
                "trigo/weiss",
                "tripel"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1 = (MaterialSpinner) findViewById(R.id.spinner1);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(this);

        String[] ITEMS2 = {"Selecionar a Cor","amarela",
                "amarela-palha",
                "âmbar",
                "cobre-claro",
                "dourada",
                "marrom",
                "marrom-clara",
                "marrom-escura",
                "preta",
                "preta-opaca"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2 = (MaterialSpinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(this);

        String[] ITEMS3 = {"Selecionar o Pais de Origem","Alemanha",
                "Argentina",
                "Áustria",
                "Austrália",
                "Bélgica",
                "Brasil",
                "Chile",
                "Dinamarca",
                "Escócia",
                "Espanha",
                "Estados Unidos",
                "Inglaterra",
                "Irlanda",
                "México",
                "Uruguai"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ITEMS3);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3 = (MaterialSpinner) findViewById(R.id.spinner3);
        spinner3.setAdapter(adapter3);
        spinner3.setOnItemSelectedListener(this);

        button = (Button)findViewById(R.id.button);
        button.setText("Seleciodar Todas");

        button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle conData = new Bundle();
                conData.putString("estilo", estilo);
                conData.putString("cor", cor);
                conData.putString("pais", pais);
                Intent intent = new Intent();
                intent.putExtras(conData);
                setResult(1, intent);
                finish();

            }
        });


    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        switch (parent.getId()) {
            case R.id.spinner1:
                estilo = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.spinner2:
                cor = parent.getItemAtPosition(pos).toString();
                break;
            case R.id.spinner3:
                pais = parent.getItemAtPosition(pos).toString();
                break;
            default:
                break;
        }

        if(estilo.equals("Selecionar o Estilo")&& cor.equals("Selecionar a Cor")&& pais.equals("Selecionar o Pais de Origem")){
            button.setText("Selecionar Todas");
            estilo="";
            cor="";
            pais="";
        }else{
            button.setText("Selecionar");
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


}
