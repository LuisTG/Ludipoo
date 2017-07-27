package com.example.alejandro.ludipoo.reactivos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.alejandro.ludipoo.MaestroActivity;
import com.example.alejandro.ludipoo.R;
import com.example.alejandro.ludipoo.grupos.Grupo;
import com.example.alejandro.ludipoo.unidades.UnidadesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alejandro on 05/04/2017.
 */

public class ReactivosActivity extends AppCompatActivity {
    /*
    Declarar instancias globales
     */
    private int idUnidad;
    private List<Reactivo> items = new ArrayList<>();
    private List<String>   ids = new ArrayList<>();
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reactivos);
        idUnidad = getIntent().getIntExtra("ID",0);
        // Inicializar Grupos

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String mtro = auth.getCurrentUser().getUid();
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReference("preguntas/");
        //dr.orderByChild("maestro").equalTo(maestro);
        //System.out.println(maestro);
        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recycler_reactivos);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new ReactivosAdapter(items,ids);
        recycler.setAdapter(adapter);

        dr.orderByChild("unidad").equalTo(idUnidad).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                ids.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(!child.exists()) {
                        continue;
                    }
                    Reactivo tmp = child.getValue(Reactivo.class);
                    items.add(tmp);
                    ids.add(child.getKey());
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),UnidadesActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
