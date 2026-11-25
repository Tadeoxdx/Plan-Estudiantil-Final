package com.planestudiantil.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.planestudiantil.app.ui.CalendarioFragment;
import com.planestudiantil.app.ui.EstadisticasFragment;
import com.planestudiantil.app.ui.InicioFragment;
import com.planestudiantil.app.ui.MasFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        if (savedInstanceState == null) {
            cargarFragmento(new InicioFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment seleccionado = null;
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                seleccionado = new InicioFragment();
            } else if (id == R.id.nav_calendario) {
                seleccionado = new CalendarioFragment();
            } else if (id == R.id.nav_estadisticas) {
                seleccionado = new EstadisticasFragment();
            } else if (id == R.id.nav_mas) {
                seleccionado = new MasFragment();
            }

            if (seleccionado != null) {
                cargarFragmento(seleccionado);
                return true;
            }
            return false;
        });
    }

    private void cargarFragmento(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}