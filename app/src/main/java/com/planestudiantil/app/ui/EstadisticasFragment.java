package com.planestudiantil.app.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.planestudiantil.app.R;
import com.planestudiantil.app.data.PlanEstudiantilDao;
import com.planestudiantil.app.model.ResumenMateria;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EstadisticasFragment extends Fragment {

    private TextView tvTotalHorasEst, tvPorcentajeMetaEst;
    private RecyclerView rvResumenMaterias;

    private PlanEstudiantilDao dao;
    private MateriaAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estadisticas, container, false);

        dao = new PlanEstudiantilDao(requireContext());
        tvTotalHorasEst = view.findViewById(R.id.tvTotalHorasEst);
        tvPorcentajeMetaEst = view.findViewById(R.id.tvPorcentajeMetaEst);
        rvResumenMaterias = view.findViewById(R.id.rvResumenMaterias);

        rvResumenMaterias.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MateriaAdapter(null);
        rvResumenMaterias.setAdapter(adapter);

        cargarEstadisticas();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String inicioSemana = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        String finSemana = sdf.format(cal.getTime());

        int minutos = dao.obtenerMinutosCompletadosEnRango(inicioSemana, finSemana);
        double horas = minutos / 60.0;
        double meta = dao.obtenerMetaSemanalHoras();
        double porcentajeMeta = meta > 0 ? (horas * 100.0) / meta : 0.0;

        tvTotalHorasEst.setText(String.format(Locale.getDefault(), "%.1f h", horas));
        tvPorcentajeMetaEst.setText(String.format(Locale.getDefault(),
                "%.0f%% de la meta semanal (%.1f h)", porcentajeMeta, meta));

        List<ResumenMateria> resumen = dao.obtenerResumenPorMateria(inicioSemana, finSemana);
        adapter.setItems(resumen);
    }
}