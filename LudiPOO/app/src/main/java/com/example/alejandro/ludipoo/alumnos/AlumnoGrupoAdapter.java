package com.example.alejandro.ludipoo.alumnos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alejandro.ludipoo.ProfileActivity;
import com.example.alejandro.ludipoo.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alejandro on 05/04/2017.
 */

public class AlumnoGrupoAdapter extends RecyclerView.Adapter<AlumnoGrupoAdapter.AlumnosGrupoViewHolder> {

    private List<AlumnoGrupoActivity.Usuario> items;
    private String nombreGrupo;
    private String idGrupo;
    private int imgGrupo;

    public static class AlumnosGrupoViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public Context context;
        public ImageView _imagen;
        public TextView _correo;
        public TextView _nombres;
        public Button _btnDetalles;

        public AlumnosGrupoViewHolder(View v) {
            super(v);
            context = v.getContext();
            _imagen = (ImageView) v.findViewById(R.id.imagen);
            _correo = (TextView) v.findViewById(R.id.txt_numero_unidad);
            _nombres = (TextView) v.findViewById(R.id.txt_reactivo_pregunta);
            _btnDetalles = (Button) v.findViewById(R.id.btn_detalles);
        }
    }

    public AlumnoGrupoAdapter(List<AlumnoGrupoActivity.Usuario> items, String idGrupo, String nombreGrupo, int imgGrupo) {
        this.items = items;
        this.nombreGrupo = nombreGrupo;
        this.idGrupo = idGrupo;
        this.imgGrupo = imgGrupo;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public AlumnosGrupoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.alumno_cardview, viewGroup, false);
        return new AlumnosGrupoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlumnosGrupoViewHolder viewHolder, final int i) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(items.get(i).getIdUser());
        //System.out.println("IDUSUARIO: "+items.get(i).getIdUser());

        /*Glide.with(this /* context )
                .using(new FirebaseImageLoader())
                .load(storageRef)
                .into(viewHolder._imagen);*/
        //StorageReference storageRef = FirebaseStorage.getInstance().reference().child("folderName/file.jpg");
        /*storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Uri uri = (Uri)o;
                //uri = uri.parse()
                System.out.println("Uri: "+uri.toString());
                Uri imgUri=Uri.parse(uri.toString());
                viewHolder._imagen.setImageURI(imgUri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });*/
        viewHolder._imagen.setImageResource(R.drawable.ic_profile_user);

        viewHolder._correo.setText(items.get(i).getEmail());
        viewHolder._nombres.setText(String.valueOf(items.get(i).getNombres()));
        viewHolder._btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetailsSuccess(viewHolder.context, items.get(i));
            }
        });
    }
    private void onDetailsSuccess(Context context, AlumnoGrupoActivity.Usuario alumno){

        Intent myIntent = new Intent(context.getApplicationContext(), ProfileActivity.class);
        myIntent.putExtra("ID",alumno.getIdUser());

        myIntent.putExtra("GRUPO",nombreGrupo);
        myIntent.putExtra("IDGRUPO",idGrupo);
        myIntent.putExtra("IMG",imgGrupo);
        myIntent.putExtra("UNIDAD",AlumnoGrupoActivity.unidadActual);

        context.startActivity(myIntent);

    }
}
