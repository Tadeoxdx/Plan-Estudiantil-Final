package com.planestudiantil.app.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.planestudiantil.app.R;
import com.planestudiantil.app.model.SesionEstudio;

import java.util.ArrayList;
import java.util.List;

public class SesionAdapter extends RecyclerView.Adapter<SesionAdapter.SesionViewHolder> {

    public interface OnSesionClickListener {
        void onSesionClick(SesionEstudio sesion);
        void onSesionLongClick(SesionEstudio sesion);
    }

    private List<SesionEstudio> sesiones = new ArrayList<>();
    private final OnSesionClickListener listener;

    public SesionAdapter(OnSesionClickListener listener) {
        this.listener = listener;
    }

    public void setSesiones(List<SesionEstudio> nuevasSesiones) {
        this.sesiones = nuevasSesiones != null ? nuevasSesiones : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SesionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sesion, parent, false);
        return new SesionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SesionViewHolder holder, int position) {
        SesionEstudio sesion = sesiones.get(position);
        holder.tvMateria.setText(sesion.getNombreMateria());
        holder.tvHorario.setText(sesion.getHoraInicio() + " - " + sesion.getHoraFin() +
                " (" + sesion.getDuracionMinutos() + " min)");
        holder.tvEstado.setText(sesion.getEstado());

        try {
            holder.viewColor.setBackgroundColor(Color.parseColor(sesion.getColorMateria()));
        } catch (Exception e) {
            holder.viewColor.setBackgroundColor(Color.parseColor("#5E4AE3"));
        }

        if (SesionEstudio.ESTADO_COMPLETADA.equals(sesion.getEstado())) {
            holder.tvEstado.setTextColor(Color.parseColor("#1B7F4E"));
            holder.tvEstado.setBackgroundColor(Color.parseColor("#E3F7EC"));
        } else if (SesionEstudio.ESTADO_REPROGRAMADA.equals(sesion.getEstado())) {
            holder.tvEstado.setTextColor(Color.parseColor("#9A5B00"));
            holder.tvEstado.setBackgroundColor(Color.parseColor("#FFF4E0"));
        } else {
            holder.tvEstado.setTextColor(Color.parseColor("#5E4AE3"));
            holder.tvEstado.setBackgroundColor(Color.parseColor("#EEF0FF"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSesionClick(sesion);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onSesionLongClick(sesion);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return sesiones.size();
    }

    static class SesionViewHolder extends RecyclerView.ViewHolder {
        View viewColor;
        TextView tvMateria, tvHorario, tvEstado;

        public SesionViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.viewColorMateria);
            tvMateria = itemView.findViewById(R.id.tvItemMateria);
            tvHorario = itemView.findViewById(R.id.tvItemHorario);
            tvEstado = itemView.findViewById(R.id.tvItemEstado);
        }
    }
}