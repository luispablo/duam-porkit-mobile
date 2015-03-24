package com.duam.porky.tasks;

import android.content.Context;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import roboguice.util.Ln;
import roboguice.util.RoboAsyncTask;

/**
 * Created by luispablo on 23/03/15.
 */
public class GetBalanceTask extends RoboAsyncTask<Map<Integer, Double[]>> {

    private String apiUrl;
    private Long comunidadId;
    private Date from;
    private Date to;

    protected GetBalanceTask(Context context, String apiUrl, Long comunidadId, Date from, Date to) {
        super(context);

        this.apiUrl = apiUrl;
        this.comunidadId = comunidadId;
        this.from = from;
        this.to = to;
    }

    @Override
    public Map<Integer, Double[]> call() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String url = apiUrl +"/"+ comunidadId.toString() + "/" + sdf.format(from) +"/"+ sdf.format(to);

        Ln.d("Getting info from ["+ url +"]");

        // https://github.com/kevinsawicki/http-request
        String jsonRes = HttpRequest.get(url).body();
        Ln.d("Response ["+ jsonRes +"]");

        JSONObject res = new JSONObject(jsonRes);
        JSONArray results = res.getJSONArray("result");

        Map<Integer, Double[]> balances = new TreeMap<Integer, Double[]>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject balance = results.getJSONObject(i);
            balances.put(balance.getInt("month"), new Double[]{
                    balance.getDouble("income"), balance.getDouble("outcome"), balance.getDouble("balance")
            });
        }

        return balances;
    }
}
