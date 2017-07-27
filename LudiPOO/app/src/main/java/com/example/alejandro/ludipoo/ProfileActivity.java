package com.example.alejandro.ludipoo;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alejandro.ludipoo.alumnos.AlumnoGrupoActivity;
import com.example.alejandro.ludipoo.reactivos.Respuesta;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.server.response.FastJsonResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.example.alejandro.ludipoo.R.id.imageView;

/**
 * Created by Luis on 11-May-17.
 */

public class ProfileActivity extends AppCompatActivity {

    private RadarChart chart;
    private boolean esAlumno=false;
    private TextView _txtNombre, _txtGrupo, _txtEmail, _txtNoControl, _txtBuenas, _txtMalas;
    private int buenasUnidad[] = new int[6];
    private int malasUnidad[] = new int[6];
    private String idUser, nombreGrupo, idGrupo;
    private int buenas, malas, imgGrupo, unidadActual;
    private User usuarioActual;
    private static final int SELECT_PICTURE = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        _txtNombre = (TextView) findViewById(R.id.txt_profile_nombre);
        _txtEmail = (TextView) findViewById(R.id.txt_profile_email);
        _txtGrupo = (TextView) findViewById(R.id.txt_profile_grupo);
        _txtNoControl = (TextView) findViewById(R.id.txt_profile_nocontrol);
        _txtBuenas = (TextView) findViewById(R.id.txt_profile_buenas);
        chart = (RadarChart) findViewById(R.id.grafica);

        idUser = getIntent().getStringExtra("ID");
        idGrupo = getIntent().getStringExtra("IDGRUPO");
        imgGrupo = getIntent().getIntExtra("IMG",R.drawable.group);
        nombreGrupo = getIntent().getStringExtra("GRUPO");
        if(getIntent().hasExtra("BAND")){
            esAlumno = getIntent().getBooleanExtra("BAND",true);
        }
        if(getIntent().hasExtra("UNIDAD")){
            unidadActual = getIntent().getIntExtra("UNIDAD",0);
        }

        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReference("user/"+idUser);
        dr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot!=null){
                    usuarioActual = dataSnapshot.getValue(User.class);
                    llenarDatos();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("NEL PERRO");
            }
        });

        dr = fd.getReference("respuestas/");

        dr.orderByChild("idUser").equalTo(idUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot x : dataSnapshot.getChildren()){
                    Respuesta tmp = x.getValue(Respuesta.class);
                    if(tmp.isAcierto()){
                        buenasUnidad[tmp.getUnidad()]++;
                        buenas++;
                    }else{
                        malasUnidad[tmp.getUnidad()]++;
                        malas++;
                    }
                }
                _txtBuenas.setText("Buenas: "+buenas+ ", Malas: "+malas+", Total: "+(buenas+malas));
                setData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        addEventos();
    }

    private void llenarDatos(){
        _txtNoControl.setText(usuarioActual.getnControl());
        _txtNombre.setText(usuarioActual.getNombres());
        _txtEmail.setText(usuarioActual.getEmail());
        _txtGrupo.setText(nombreGrupo);
    }

    public void setData() {

        float mult = 80;
        float min = 20;
        int cnt = 5;

        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        ArrayList<RadarEntry> entries2 = new ArrayList<RadarEntry>();
        List<String> labels = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < 6; i++) {
            //float val1 = (float) (Math.random() * mult) + min;
            entries1.add(new RadarEntry((float)buenasUnidad[i]));

            //float val2 = (float) (Math.random() * mult) + min;
            entries2.add(new RadarEntry((float)malasUnidad[i]));

            labels.add("Unidad "+String.valueOf(i+1));
        }

        RadarDataSet set1 = new RadarDataSet(entries1, "Aciertos");
        set1.setColor(Color.rgb(0, 255, 0));
        set1.setFillColor(Color.rgb(0, 255, 0));

        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);
        //*set1.setColors(ColorTemplate.MATERIAL_COLORS);


        RadarDataSet set2 = new RadarDataSet(entries2, "Fallos");
        set2.setColor(Color.rgb(255,0,0));
        set2.setFillColor(Color.rgb(255,0,0));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);
        //set2.setColors(ColorTemplate.JOYFUL_COLORS);
        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private String[] mActivities = new String[]{"Unidad 1", "Unidad 2", "Unidad 3", "Unidad 4", "Unidad 5", "Unidad 6"};
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        //xAxis.setTextColor(Color.W);
        /*data.setLabels("Unidad 1", "Unidad 2", "Unidad 3", "Unidad 4", "Unidad 5", "Unidad 6");
        data.setLabels(labels);*/
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.invalidate();
    }

    private void addEventos(){

        /*_imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"),SELECT_PICTURE);
            }
        });
        onUploadButtonClick();*/
    }
    private Uri uriFicticio;
    protected  void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            if(requestCode==SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    String path = getPathFromURI(selectedImageUri);
                    Log.i("IMAGE PATH TAG", "Image Path : " + path);
                    // Set the image in ImageView
                    uriFicticio = selectedImageUri;

                }
            }
        }
    }
    private String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if(esAlumno){
            intent = new Intent(getApplicationContext(),MainActivity.class);
        }else{
            intent = new Intent(getApplicationContext(),AlumnoGrupoActivity.class);
            intent.putExtra("ID",idGrupo);
            intent.putExtra("Nombre",nombreGrupo);
            intent.putExtra("Imagen",imgGrupo);
            intent.putExtra("Unidad",unidadActual);
        }

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}

