package com.example.gayatrid.weatherapp;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Gayatri D on 5/27/2015.
 */
public class WeatherListFragment extends Fragment implements AdapterView.OnItemClickListener {
    Cursor cursor;
    ListView lv;
    WeatherAdapter adapter;
    String[] mTitles;
    ArrayList<WeatherDetails> weatherReport;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weatherlist_fragment, container, false);
        MainActivity activity = (MainActivity) this.getActivity();
        Log.d("List fragment","in on create List fragment " );

        //Main code
       DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
        cursor = dbHelper.getAllRows();


        getActivity().startManagingCursor(cursor);

        adapter = new WeatherAdapter(getActivity(), cursor);

        lv = (ListView) view.findViewById(R.id.weatherList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);

      /*  weatherReport = activity.getWeatherDetails();
        mTitles = new String[10];

        for(int i = 0; i< mTitles.length; i++){
            mTitles[i] = weatherReport.get(i).forecast;
        }

        ListView lv =(ListView)view.findViewById(R.id.weatherList);
        lv.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, mTitles));
        lv.setOnItemClickListener(this);*/
        Log.d("List fragment", "before return ");


        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
   //     Toast.makeText(getActivity(), mTitles[i], Toast.LENGTH_LONG).show();
        cursor.moveToFirst();
        cursor.move(i);
        String forecast = cursor.getString(cursor.getColumnIndexOrThrow(Contract.WeatherEntry.FORECAST));
//        String description = cursor.getString(cursor.getColumnIndexOrThrow(Contract.WeatherEntry.DESCRIPTION));

        Log.d("in on click",forecast);
        PhotoFragment pf = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString("URL", forecast);//weatherReport.get(i).forecast);
        pf.setArguments(args);
        pf.setArguments(args);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, pf);
        ft.addToBackStack("Image");
        ft.commit();
    }

}