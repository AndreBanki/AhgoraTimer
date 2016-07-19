package com.banki.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.banki.ahgora.R;
import com.banki.main.controller.BatidasHandler;
import com.banki.ahgora.controller.IAhgoraView;
import com.banki.targetprocess.controller.ITargetView;
import com.banki.ahgora.model.TimeConverter;
import com.banki.main.contador.ServiceActivity;
import com.banki.main.settings.SettingsActivity;

public class MainActivity extends ServiceActivity implements ITargetView, IAhgoraView {

    private FloatingActionButton refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contador);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        refreshBtn = (FloatingActionButton) findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batidasHandler().refreshDataFromWS();
            }
        });

        batidasHandler().trataAberturaViaNotificacao(getIntent());
    }

    @Override
    protected void inicializaHandler() {
        activityHandler = new BatidasHandler(this);
    }

    private BatidasHandler batidasHandler() {
        return (BatidasHandler)activityHandler;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contador, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent config = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(config);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void toast(String texto) {
        Toast t = Toast.makeText(MainActivity.this, texto, Toast.LENGTH_LONG);
        t.show();
    }


    public void iniciaIndicacaoProgresso() {
        atualizaListaBatidas(getResources().getString(R.string.consultando));
        atualizaCampoTarget(getResources().getString(R.string.consultando));
        refreshBtn.setAlpha((float)0.5);
    }

    public void terminaIndicacaoProgresso() {
        refreshBtn.setAlpha((float)1);
    }

    // ITargetView --------------------------------------

    @Override
    public void atualizaHorasTarget(int count) {
        atualizaCampoTarget(TimeConverter.horasMinutosAsString(count));
    }

    private void atualizaCampoTarget(String valor) {
        TextView horasTarget = (TextView) findViewById(R.id.horasTarget);
        horasTarget.setText(new StringBuilder().append(getResources().getString(R.string.horasTarget))
                .append(valor)
                .toString());
    }

    // IAhgoraView ----------------------------------------

    @Override
    public void atualizaHorasTrabalhadas(int segundos, boolean exibirSegundos) {
        TextView horasMinutosTxt = (TextView) findViewById(R.id.horasMinutos);
        horasMinutosTxt.setText(TimeConverter.horasMinutosAsString(segundos));

        TextView segundosTxt = (TextView) findViewById(R.id.segundos);
        if (exibirSegundos)
            segundosTxt.setText(TimeConverter.segundosAsString(segundos));
        else
            segundosTxt.setText("");
    }

    @Override
    public void atualizaIntervalo(int segundos, boolean exibirSegundos) {
        TextView valorHorasTxt = (TextView) findViewById(R.id.valorHoras);
        String texto = TimeConverter.horasMinutosAsString(segundos);
        if (exibirSegundos)
            texto += TimeConverter.segundosAsString(segundos);
        valorHorasTxt.setText(texto);

        destacaIntervaloSeMenorUmaHora(segundos);
    }

    @Override
    public void atualizaListaBatidas(String listaBatidas) {
        TextView listaBatidasTxt = (TextView) findViewById(R.id.txtListaBatidas);
        listaBatidasTxt.setText(new StringBuilder().append(getResources().getString(R.string.batidas_hoje))
                                                   .append(listaBatidas)
                                                   .toString());
    }

    private void destacaIntervaloSeMenorUmaHora(int segundos) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String jornadaEntry = settings.getString("jornadaTrabalho", "8");
        int segundosDuracaoIntervalo = jornadaEntry.equals("8") ? 60*60 : 60*15;

        LinearLayout frame = (LinearLayout) findViewById(R.id.frameIntervalo);
        if (segundos > 0 && segundos < segundosDuracaoIntervalo)
            frame.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorError));
        else
            frame.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                    R.color.colorPrimaryDark));

        frame.invalidate();
    }

}
