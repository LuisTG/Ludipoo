package com.example.alejandro.ludipoo.grupos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.alejandro.ludipoo.MaestroActivity;
import com.example.alejandro.ludipoo.R;
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

public class GrupoActivity extends AppCompatActivity {
    /*
    Declarar instancias globales
     */
    private List<Grupo> items = new ArrayList<>();
    private RecyclerView recycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager lManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        // Inicializar Grupos

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String mtro = auth.getCurrentUser().getUid();
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        DatabaseReference dr = fd.getReference("grupos/");
        //dr.orderByChild("maestro").equalTo(maestro);
        //System.out.println(maestro);
        // Obtener el Recycler
        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Crear un nuevo adaptador
        adapter = new GrupoAdapter(items);
        recycler.setAdapter(adapter);

        dr.orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    if(!child.exists()) {
                        continue;
                    }
                    Grupo tmp = child.getValue(Grupo.class);
                    if(tmp.getMaestro().equals(mtro)){
                        //tmp.setImagen(R.drawable.group);
                        items.add(tmp);
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        /*items.add(new Grupo(R.drawable.group, "PE401F", "POO Mañana", "HOFDSA"));
        items.add(new Grupo(R.drawable.group, "PE401A", "POO Medio Día", "HOFDSA"));
        items.add(new Grupo(R.drawable.group, "PE401B", "POO Tarde", "HOFDSA"));
        items.add(new Grupo(R.drawable.group, "PE401C", "POO Noche", "HOFDSA"));
        items.add(new Grupo(R.drawable.group, "PE401D", "POO Especial", "HOFDSA"));*/


    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),MaestroActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }
}
