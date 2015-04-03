package com.duam.porky;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.duam.porky.tasks.GetBalanceTask;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import static com.duam.porky.ConstantesPorky.PORKY_PREFS;
import static com.duam.porky.ConstantesPorky.PREF_ID_USUARIO;

@ContentView(R.layout.activity_balance)
public class BalanceActivity extends PorkyActivity {

    @InjectView(R.id.chart)
    BarChart chart;
    @InjectView(R.id.txtAnteAnterior)
    TextView txtAnteAnterior;
    @InjectView(R.id.txtAnterior)
    TextView txtAnterior;
    @InjectView(R.id.txtActual)
    TextView txtActual;

    @Override
    protected void onResume() {
        super.onResume();

        long usuarioId = getSharedPreferences(PORKY_PREFS, MODE_PRIVATE).getLong(PREF_ID_USUARIO, -1);
        Calendar to = Calendar.getInstance();
        to.set(Calendar.DAY_OF_MONTH, to.getActualMaximum(Calendar.DAY_OF_MONTH));

        Calendar from = Calendar.getInstance();
        from.set(Calendar.DAY_OF_MONTH, from.getActualMinimum(Calendar.DAY_OF_MONTH));
        from.add(Calendar.MONTH, -2);

        new GetBalanceTask(this, "http://porkitapi.duamsistemas.com.ar/data/short-summary", usuarioId, from.getTime(), to.getTime()) {
            @Override
            protected void onSuccess(Map<Integer, Double[]> stringDoubleMap) throws Exception {
                Iterator<Integer> keys = stringDoubleMap.keySet().iterator();

                if (stringDoubleMap.size() > 2) setBalance(keys.next(), stringDoubleMap, txtAnteAnterior);
                if (stringDoubleMap.size() > 1) setBalance(keys.next(), stringDoubleMap, txtAnterior);
                if (stringDoubleMap.size() > 0) {
                    Double[] current = setBalance(keys.next(), stringDoubleMap, txtActual);
                    setChartValues(current[0].floatValue(), current[1].floatValue());
                }
            }
        }.execute();
    }

    private Double[] setBalance(Integer month, Map<Integer, Double[]> stringDoubleMap, TextView textView) {
        DecimalFormat df = new DecimalFormat("###,###.##", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());

        Double balance = stringDoubleMap.get(month)[2];
        textView.setText(symbols.getMonths()[month - 1] +" "+ df.format(balance));
        textView.setTextColor(ColorStateList.valueOf(balance > 0 ? Color.BLACK : Color.RED));

        return stringDoubleMap.get(month);
    }

    private void setChartValues(float income, float outcome) {

        ArrayList<BarEntry> valsComp1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> valsComp2 = new ArrayList<BarEntry>();

        BarEntry c1e1 = new BarEntry(income, 0); // 0 == quarter 1
        BarEntry c1e2 = new BarEntry(outcome, 1); // 1 == quarter 2 ...

        valsComp1.add(c1e1);
        valsComp1.add(c1e2);

        BarDataSet dataSet = new BarDataSet(valsComp1, "Saldo del mes");
        dataSet.setColors(new int[]{Color.GREEN, Color.RED});

        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add("INGRESOS"); xVals.add("EGRESOS");

        BarData data = new BarData(xVals, dataSet);

        chart.setData(data);
        chart.invalidate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        setChartValues(0f, 0f);

        txtAnteAnterior.setText("...");
        txtAnterior.setText("...");
        txtActual.setText("...");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_balance, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_add:
                Intent intent = new Intent(this, MovimientoActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_list:
                Intent chartIntent = new Intent(this, MovimientosActivity.class);
                startActivity(chartIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
