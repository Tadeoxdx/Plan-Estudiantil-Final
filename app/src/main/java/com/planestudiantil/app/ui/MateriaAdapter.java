package com.planestudiantil.app.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.planestudiantil.app.R;
import com.planestudiantil.app.model.ResumenMateria;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.MateriaViewHolder> {

    public interface OnMateriaClickListener {
        void onMateriaClick(ResumenMateria materia);
        void onMateriaLongClick(ResumenMateria materia);
    }

    private List<ResumenMateria> items = new ArrayList<>();
    private final OnMateriaClickListener listener;

    public MateriaAdapter(OnMateriaClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ResumenMateria> nuevosItems) {
        this.items = nuevosItems != null ? nuevosItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MateriaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_materia, parent, false);
        return new MateriaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MateriaViewHolder holder, int position) {
        ResumenMateria item = items.get(position);
        holder.tvNombre.setText(item.getNombreMateria());
        holder.tvDetalle.setText(String.format(Locale.getDefault(), "%.1f h (%.0f%%)",
                item.getHorasTotales(), item.getPorcentaje()));

        try {
            holder.viewColor.setBackgroundColor(Color.parseColor(item.getColorHex()));
        } catch (Exception e) {
            holder.viewColor.setBackgroundColor(Color.parseColor("#5E4AE3"));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMateriaClick(item);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onMateriaLongClick(item);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class MateriaViewHolder extends RecyclerView.ViewHolder {
        View viewColor;
        TextView tvNombre, tvDetalle;

        public MateriaViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.viewColorResumen);
            tvNombre = itemView.findViewById(R.id.tvNombreMateriaItem);
            tvDetalle = itemView.findViewById(R.id.tvDetalleMateriaItem);
        }
    }
}