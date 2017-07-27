package com.example.alejandro.ludipoo;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.alejandro.ludipoo.reactivos.Reactivo;
import com.example.alejandro.ludipoo.reactivos.Respuesta;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class JuegoActivity extends AppCompatActivity {
    public final static int TIEMPO_PREGUNTA = 15;
    private ProgressBar _progressBar;
    private TextView _txtPregunta, _txtRespuesta1, _txtRespuesta2, _txtRespuesta3, _txtRespuesta4, _txtTiempo;
    private CardView _cardRespuesta1, _cardRespuesta2, _cardRespuesta3, _cardRespuesta4;
    private Reactivo reactivoActual;
    private HiloTiempo hilo;
    private Handler mHandler;
    private OyenteRespuesta oyente;
    private String idReactivo;
    private boolean isRunning=true;
    private int tiempoTranscurrido=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);
        _txtPregunta = (TextView) findViewById(R.id.txt_pregunta);
        _txtRespuesta1 = (TextView) findViewById(R.id.txt_respuesta1);
        _txtRespuesta2 = (TextView) findViewById(R.id.txt_respuesta2);
        _txtRespuesta3 = (TextView) findViewById(R.id.txt_respuesta3);
        _txtRespuesta4 = (TextView) findViewById(R.id.txt_respuesta4);
        _cardRespuesta1 = (CardView) findViewById(R.id.card_respuesta_1);
        _cardRespuesta2 = (CardView) findViewById(R.id.card_respuesta_2);
        _cardRespuesta3 = (CardView) findViewById(R.id.card_respuesta_3);
        _cardRespuesta4 = (CardView) findViewById(R.id.card_respuesta_4);
        _txtTiempo = (TextView) findViewById(R.id.txt_tiempo);
        _progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        reactivoActual = (Reactivo) getIntent().getSerializableExtra("Reactivo");
        idReactivo = getIntent().getStringExtra("ID");
        addEventos();
    }

    private void addEventos(){
        _txtPregunta.setText(reactivoActual.getPregunta());
        _txtRespuesta1.setText(reactivoActual.getRespuestas().get(0));
        _txtRespuesta2.setText(reactivoActual.getRespuestas().get(1));
        _txtRespuesta3.setText(reactivoActual.getRespuestas().get(2));
        _txtRespuesta4.setText(reactivoActual.getRespuestas().get(3));
        oyente = new OyenteRespuesta();
        _cardRespuesta1.setOnClickListener(oyente);
        _cardRespuesta2.setOnClickListener(oyente);
        _cardRespuesta3.setOnClickListener(oyente);
        _cardRespuesta4.setOnClickListener(oyente);
        mHandler = new Handler();
        hilo = new HiloTiempo();
        hilo.start();
    }

    class OyenteRespuesta implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int respuesta=-1;
            isRunning = false;
            switch (v.getId()) {
                case R.id.card_respuesta_1:
                    respuesta = 0;
                    break;
                case R.id.card_respuesta_2:
                    respuesta = 1;
                    break;
                case R.id.card_respuesta_3:
                    respuesta = 2;
                    break;
                case R.id.card_respuesta_4:
                    respuesta = 3;
                    break;
            }
            registrarRespuesta(respuesta == reactivoActual.getIndexCorrecto(),respuesta);
        }
    }

    private String getFechaActual(){
        Calendar cal = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        String mes = String.format("%02d",cal.get(Calendar.MONTH)+1);
        String dia = String.format("%02d",cal.get(Calendar.DAY_OF_MONTH));
        String hora = String.format("%02d",cal.get(Calendar.HOUR_OF_DAY));
        String mins = String.format("%02d",cal.get(Calendar.MINUTE));
        String segs = String.format("%02d",cal.get(Calendar.SECOND));

        sb.append(cal.get(Calendar.YEAR)).append('-').append(mes).append('-').append(dia);
        sb.append(' ').append(hora).append(':').append(mins).append(':').append(segs);
        return sb.toString();
    }

    private void registrarRespuesta(boolean respuesta, int indRespuesta){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("respuestas").push();
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();
        Respuesta resp = new Respuesta(respuesta,getFechaActual(),idReactivo,userID,indRespuesta,reactivoActual.getUnidad());
        ref.setValue(resp);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                cambiarVista();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    class HiloTiempo extends Thread{
        public void run(){
            tiempoTranscurrido = 1;

            while (tiempoTranscurrido < TIEMPO_PREGUNTA && isRunning) {
                // Update the progress bar
                mHandler.post(new Runnable() {
                    public void run() {
                        _txtTiempo.setText("Tiempo: "+Integer.toString(TIEMPO_PREGUNTA-tiempoTranscurrido)+"s");
                        _progressBar.setProgress(tiempoTranscurrido*100/TIEMPO_PREGUNTA);
                    }
                });
                tiempoTranscurrido++;
                android.os.SystemClock.sleep(1000); // Thread.sleep() doesn't guarantee 1000 msec sleep, it can be interrupted before
            }
            if(isRunning) jugadorPerdio();
        }
    }
    private void jugadorPerdio(){
        isRunning = false;
        registrarRespuesta(false,5);
    }
    private void cambiarVista(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
    @Override
    public void onBackPressed() {
        jugadorPerdio();
    }
}
