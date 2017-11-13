package inacap.cl.appinacap1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    //Creamos objetos relacionados al layout
    private EditText patente;
    private EditText modelo;
    private Spinner marca;
    private EditText ano;
    private EditText uf;
    private Button calcular;
    private static String urlString;
    JSONObject objetoJSON;
    String marcaSeleccionada;
    ArrayList<String> listamarcas = new ArrayList<String>();
    private static final Calendar AUX_CALENDAR = Calendar.getInstance();


//http://portal.unap.cl/kb/aula_virtual/serviciosremotos/datos-uf-dia.php

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Asociamos los objetos con un elemento de la interfaz
        patente = (EditText) findViewById(R.id.edt_patente);
        modelo = (EditText) findViewById(R.id.edt_modelo);
        marca = (Spinner) findViewById(R.id.spn_marca);
        calcular = (Button) findViewById(R.id.btn_calcular);
        ano = (EditText) findViewById(R.id.edt_ano);
        uf = (EditText) findViewById(R.id.edt_uf);

        //resultado     =    (TextView) findViewById(R.id.rxv_resultado);
//Hay que cambiar la lista de marcas por
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
          //      R.array.marca_array, android.R.layout.simple_spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        //marca.setAdapter(adapter);
         marca.setOnItemSelectedListener(this);
         calcular.setOnClickListener(this);

        uf.setText("");
        //urlString2 = "http://portal.unap.cl/kb/aula_virtual/serviciosremotos/datos-uf-dia.php";
        urlString = "https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json";
        //urlString = "https://fipe.parallelum.com.br/api/v1/carros/marcas";
        new ProcessJSON().execute(urlString);

        this.persistFields();

    }

    private void persistFields() {
        Bundle bundle = this.getIntent().getExtras();

        if (bundle != null && !bundle.isEmpty()) {
            double valorUfIngresado = bundle.getDouble("valorUF");
            int anoVehiculo = bundle.getInt("ano");
            patente.setText(bundle.getString("patente"));
            modelo.setText(bundle.getString("modelo"));
            marca.setSelection(bundle.getInt("marca_posicion"));
            ano.setText("" + anoVehiculo);
            uf.setText("" + valorUfIngresado);

        }
    }

    private void enviarDatos() {
        if (!this.Comprobar(patente, modelo, ano, uf)) {
            return;
        } else if (!this.Comprobar(marca)) {

        } else if (!this.compAnoVehiculo(Integer.parseInt(ano.getText().toString()))) {
            Toast.makeText(this, "Año incorrecto", Toast.LENGTH_SHORT).show();
            return;
        }

        String patenteSalida = patente.getText().toString(),
                modeloSalida = modelo.getText().toString(),
                marcaSalida = marca.getSelectedItem().toString();
        int anoSalida = Integer.parseInt(ano.getText().toString()),
                marcaPos = marca.getSelectedItemPosition();
        double ufSalida = Double.parseDouble(uf.getText().toString());

        Intent enviar = new Intent(MainActivity.this, ResultadoActivity.class);
        enviar.putExtra("patente", patenteSalida);
        enviar.putExtra("marca", modeloSalida);
        enviar.putExtra("modelo", marcaSalida);
        enviar.putExtra("marcaPos", marcaPos);
        enviar.putExtra("ano", anoSalida);
        enviar.putExtra("uf", ufSalida);

        startActivity(enviar);
    }

    private boolean Comprobar(EditText... args) {
        if (args.length == 0) {
            return false;
        }
        for (EditText editText : args) {
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(this, editText.getHint() + " incorrecto", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean Comprobar(Spinner... args) {
        if (args.length == 0) {
            return false;
        }
        for (Spinner spinner : args) {
            System.out.println("Spinner flag: " + spinner.isSelected());
            if (spinner.isSelected()) {
                Toast.makeText(this, "Spinner erroneo", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean compAnoVehiculo(int ano) {
        int anoActual = AUX_CALENDAR.getInstance().get(Calendar.YEAR);
        return ano <= anoActual;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        System.out.println("onNothingSelected");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("onItemSelected");
    }

    @Override
    public void onClick(View view) {
        enviarDatos();
    }


    private class ProcessJSON extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) { //... significa que pasamos multiples string
            String stream = null;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }
        protected void onPostExecute(String stream){
            EditText uf = (EditText) findViewById(R.id.edt_uf);

            if(stream !=null){
                try{
                    // Obtenemos todos los datos HTTP medinte un objeto JSONObject

                    JSONObject reader= new JSONObject(stream);
                    JSONArray arrayReader = (JSONArray) reader.get("Results");

                    //JSONArray reader= new JSONArray(stream);
                    for (int i = 0; i < arrayReader.length(); i++) {
                        objetoJSON = arrayReader.getJSONObject(i);
                        String marcaAux = objetoJSON.getString("MakeName");
                        listamarcas.add(marcaAux);
                    }
                    marca.setAdapter(new ArrayAdapter<>(MainActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, listamarcas));

                    // Obtenemos uno de los valores que necesitamos
                    String valoruf = reader.getString("VALOR_UF");


                    //uf.setText("VALORUF "+ valoruf);
                    uf.setText(valoruf);


                }catch(JSONException e){
                    e.printStackTrace();
                }


            }
            this.compSpinner();
        }
        private void compSpinner(){
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && !bundle.isEmpty()) {
                marca.setSelection(bundle.getInt("marcaPos"));
            }
        }
        }

    }


