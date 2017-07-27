package com.example.alejandro.ludipoo.reactivos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.alejandro.ludipoo.MaestroActivity;
import com.example.alejandro.ludipoo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by lopezbeto on 7/05/17.
 */

public class CreaReactivoActivity extends AppCompatActivity {
    private static final String TAG = "CrearReactivoActivity";
    private EditText _txtPregunta,r1,r2,r3,r4;
    private RadioButton rad1,rad2,rad3,rad4;
    private Spinner _spinnerUnidad;
    private Button guardar,_btnLimpiar;
    private ProgressDialog progressDialog;
    private int indice=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_pregunta);

        _spinnerUnidad = (Spinner) findViewById(R.id.spinner_unidad);
        _txtPregunta=(EditText) findViewById(R.id.input_pregunta);
        r1=(EditText)findViewById(R.id.input_r1);
        r2=(EditText)findViewById(R.id.input_r2);
        r3=(EditText)findViewById(R.id.input_r3);
        r4=(EditText)findViewById(R.id.input_r4);
        rad1 = (RadioButton) findViewById(R.id.rd_btn1);
        rad2 = (RadioButton) findViewById(R.id.rd_btn2);
        rad3 = (RadioButton) findViewById(R.id.rd_btn3);
        rad4 = (RadioButton) findViewById(R.id.rd_btn4);
        View.OnClickListener oyente = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarIndice(v.getId());
            }
        };

        rad1.setOnClickListener(oyente);
        rad2.setOnClickListener(oyente);
        rad3.setOnClickListener(oyente);
        rad4.setOnClickListener(oyente);

        guardar=(Button)findViewById(R.id.btn_reactivo_guardar);
        _btnLimpiar=(Button)findViewById(R.id.btn_reactivo_limpiar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                crearReactivo();
            }
        });
        _btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });
    }

    private void actualizarIndice(int id){
        rad1.setChecked(false);
        rad2.setChecked(false);
        rad3.setChecked(false);
        rad4.setChecked(false);
        switch(id){
            case R.id.rd_btn1:
                rad1.setChecked(true);
                indice = 0;
                break;
            case R.id.rd_btn2:
                rad2.setChecked(true);
                indice = 1;
                break;
            case R.id.rd_btn3:
                rad3.setChecked(true);
                indice = 2;
                break;
            case R.id.rd_btn4:
                rad4.setChecked(true);
                indice = 3;
                break;
            default:
                System.out.println("HOLA PUTAS -1");
                indice = -1;
                break;
        }
    }
    private void crearReactivo(){
        String pregunta = _txtPregunta.getText().toString().trim();
        String resp1 = r1.getText().toString().trim();
        String resp2 = r2.getText().toString().trim();
        String resp3 = r3.getText().toString().trim();
        String resp4 = r4.getText().toString().trim();

        int unidad = _spinnerUnidad.getSelectedItemPosition();
        guardar.setEnabled(false);

        if (!validate(pregunta,resp1,resp2,resp3,resp4)) {
            onCreateFailed();
            return;
        }

        progressDialog = new ProgressDialog(CreaReactivoActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando Reactivo...");
        progressDialog.show();
        crearReactivo(pregunta,resp1,resp2,resp3,resp4,unidad);
    }

    private void crearReactivo(String pregunta, String resp1, String resp2, String resp3, String resp4, int unidad){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("preguntas").push();
        Reactivo nuevoReactivo = new Reactivo(pregunta,resp1,resp2,resp3,resp4,indice,unidad);
        ref.setValue(nuevoReactivo);
        //String postId = ref.getKey();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Reactivo tmp = dataSnapshot.getValue(Reactivo.class);
                onCreateReactivoSuccess();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private boolean validate(String pregunta, String resp1, String resp2, String resp3, String resp4){
        boolean valid = true;

        if (pregunta.isEmpty()) {
            _txtPregunta.setError("ingresa la pregunta para el reactivo");
            valid = false;
        } else {
            _txtPregunta.setError(null);
        }

        if (resp1.isEmpty()) {
            r1.setError("ingresa la respuesta");
            valid = false;
        } else {
            r1.setError(null);
        }

        if (resp2.isEmpty()) {
            r2.setError("ingresa la respuesta");
            valid = false;
        } else {
            r2.setError(null);
        }

        if (resp3.isEmpty()) {
            r3.setError("ingresa la respuesta");
            valid = false;
        } else {
            r3.setError(null);
        }

        if (resp4.isEmpty()) {
            r4.setError("ingresa la respuesta");
            valid = false;
        } else {
            r4.setError(null);
        }

        if(indice < 0 || indice > 3){
            rad1.setError("seleccione una opcion correcta");
            valid = false;
        }else{
            rad1.setError(null);
        }

        return valid;
    }

    private void onCreateFailed(){
        Toast.makeText(getBaseContext(), "No se pudo crear el reactivo", Toast.LENGTH_LONG).show();
        guardar.setEnabled(true);
    }

    private void onCreateReactivoSuccess(){
        if(progressDialog!=null) progressDialog.dismiss();
        Toast.makeText(getBaseContext(), "Reactivo creado con éxito", Toast.LENGTH_LONG).show();
        limpiarCampos();
        guardar.setEnabled(true);
    }

    private void limpiarCampos(){
        _txtPregunta.setText("");
        r1.setText("");
        r2.setText("");
        r3.setText("");
        r4.setText("");
        rad1.setChecked(false);
        rad2.setChecked(false);
        rad3.setChecked(false);
        rad4.setChecked(false);
        indice = -1;
        _spinnerUnidad.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MaestroActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
