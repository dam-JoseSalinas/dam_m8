package com.example.asteroides;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class Preferencias extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        MyPreferenceFragment mPF = new MyPreferenceFragment();
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, mPF).commit();
        getSupportFragmentManager().executePendingTransactions();

        final Preference fragmentos = mPF.findPreference("fragmentos");
        fragmentos.setOnPreferenceChangeListener((preference, newValue) -> {
            int valor;
            try {
                valor = Integer.parseInt((String) newValue);
            } catch (Exception e) {
                Toast.makeText(context, "Ha de ser un número",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
            if (valor >= 0 && valor <= 9) {
                fragmentos.setSummary(
                        "En cuantos trozos se divide un asteroide (" + valor + ")");
                return true;
            } else {
                Toast.makeText(context, "Maximo de fragmentos 9",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferencias);
        }
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        }
    }
}