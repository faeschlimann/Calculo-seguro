package inacap.cl.appinacap1;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ResultadoActivity extends AppCompatActivity {

    private static final Calendar AUX_CALENDAR = Calendar.getInstance();
    private static final int ANTIGUEDAD_MAX = 10;

    private TextView patente;
    private TextView marca;
    private TextView modelo;
    private TextView ano;
    private TextView uf;
    private TextView antiguedad;
    private TextView valido;
    private TextView valorSeguro;
    private ImageView resultadoImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        this.initFields();

        final Bundle bundle = this.getIntent().getExtras();

        double valorUFIngresado = bundle.getDouble("uf");
        int anoVehiculo = bundle.getInt("ano");


        patente.setText("Patente: " + bundle.getString("patente"));
        marca.setText("Marca: " + bundle.getString("marca"));
        modelo.setText("Modelo: " + bundle.getString("modelo"));
        ano.setText("Año: " + anoVehiculo);
        uf.setText("UF: " + valorUFIngresado + "$");


        int auxAntiguo = this.calcularAntiguedad(anoVehiculo);
        boolean esValido = this.esValido(auxAntiguo);
        antiguedad.setText(auxAntiguo + " año");
        valido.setText(esValido ? "Sí" : "No");
        valorSeguro.setText(this.valorSeguro(auxAntiguo, valorUFIngresado) + "$");

        this.setImgView(esValido);
    }

    private void initFields() {

        patente = (TextView) findViewById(R.id.txv_patente);
        marca = (TextView) findViewById(R.id.txv_marca);
        modelo = (TextView) findViewById(R.id.txv_modelo);
        ano = (TextView) findViewById(R.id.txv_ano);
        uf = (TextView) findViewById(R.id.txv_uf);
        resultadoImagen = (ImageView) findViewById(R.id.imv_resultado);
        antiguedad = (TextView) findViewById(R.id.txv_antiguedad);
        valido = (TextView) findViewById(R.id.txv_valido);
        valorSeguro = (TextView) findViewById(R.id.txv_valor_seguro);
    }

    private void calculo2(Bundle bundle) {

        Intent envio = new Intent(ResultadoActivity.this, MainActivity.class);
        envio.putExtra("patente", bundle.getString("patente"));
        envio.putExtra("marca", bundle.getString("marca"));
        envio.putExtra("modelo", bundle.getString("modelo"));
        envio.putExtra("modelo_pos", bundle.getInt("modelo_pos"));
        envio.putExtra("ano", bundle.getInt("ano"));
        envio.putExtra("uf", bundle.getDouble("uf"));
        startActivity(envio);
    }

    private int calcularAntiguedad(int ano) {
        int anoActual = AUX_CALENDAR.getInstance().get(Calendar.YEAR);

        return anoActual - ano;
    }

    private boolean esValido(int anos) {
        return anos > ANTIGUEDAD_MAX ? false : true;
    }

    private double valorSeguro(int antiguedad, double uf) {
        return this.valorSeguro(antiguedad, uf, this.esValido(antiguedad));
    }

    private double valorSeguro(int antiguedad, double uf, boolean esValido) {
        if (esValido) {
            return antiguedad == 0 || antiguedad == 1 ?
                    0.1 * uf : 0.1 * uf * antiguedad;
        }
        return 0;
    }

    private void setImgView(boolean esValido) {
        if (esValido) {
            resultadoImagen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.valido));
            Toast.makeText(getApplicationContext(), "VEHICULO ASEGURADO", Toast.LENGTH_LONG).show();
        } else {
            resultadoImagen.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.invalido));
            Toast.makeText(getApplicationContext(), "VEHICULO NO ASEGURADO", Toast.LENGTH_LONG).show();
        }

    }
}
