package com.example.alejandro.ludipoo.grupos;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alejandro.ludipoo.R;
import com.example.alejandro.ludipoo.alumnos.AlumnoGrupoActivity;

import java.util.List;

/**
 * Created by Alejandro on 05/04/2017.
 */

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder> {

    private List<Grupo> items;

    public static class GrupoViewHolder extends RecyclerView.ViewHolder {
        // Campos respectivos de un item
        public Context context;
        public ImageView _imagen;
        public TextView _matricula;
        public TextView _nombre;
        public TextView _horario;
        public Button _btnDetalles;
        public GrupoViewHolder(View v) {
            super(v);
            context = v.getContext();
            _imagen = (ImageView) v.findViewById(R.id.imagen);
            _matricula = (TextView) v.findViewById(R.id.txt_numero_unidad);
            _nombre = (TextView) v.findViewById(R.id.txt_reactivo_pregunta);
            _horario = (TextView) v.findViewById(R.id.txt_horario);
            _btnDetalles = (Button) v.findViewById(R.id.btn_detalles);
        }
    }

    public GrupoAdapter(List<Grupo> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public GrupoViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grupos_cardview, viewGroup, false);
        return new GrupoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final GrupoViewHolder viewHolder, final int i) {
        viewHolder._imagen.setImageResource(R.drawable.group);
        viewHolder._matricula.setText(items.get(i).getIdGrupo());
        viewHolder._nombre.setText(String.valueOf(items.get(i).getNombre()));
        String hora = formatearHora(items.get(i).getHoraEntrada(), items.get(i).getHoraSalida());
        viewHolder._horario.setText(hora);
        viewHolder._btnDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDetailsSuccess(viewHolder.context, items.get(i).getIdGrupo(), items.get(i).getNombre(), items.get(i).getImagen(), (int)items.get(i).getUnidadActual());
            }
        });
    }
    private void onDetailsSuccess(Context context, String idGrupo, String nombre, int imagen, int unidad){
        Intent myIntent = new Intent(context.getApplicationContext(), AlumnoGrupoActivity.class);

        myIntent.putExtra("ID",idGrupo);
        myIntent.putExtra("Nombre",nombre);
        myIntent.putExtra("Imagen",R.drawable.group);
        myIntent.putExtra("Unidad",unidad);

        context.startActivity(myIntent);

    }
    private String formatearHora(int hE, int hS){
        return String.format("Horario: %02d:00 - %02d:00",hE,hS);
    }
}
