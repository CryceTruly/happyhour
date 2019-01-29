package com.crycetruly.happyhour;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by Elia on 26/04/2018.
 */

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {
    private  View window;
    private Context mcontext;
    private static final String TAG = "CustomInfoWindow";
    private Button contact;
    public CustomInfoWindow(Context mcontext) {
        this.mcontext = mcontext;
        window= LayoutInflater.from(mcontext).inflate(R.layout.custom_info_window,null);
        window.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: window clicked");
            }
        });

    }

    @Override
    public View getInfoWindow(Marker marker) {

        try {
            renderWindowText(marker,window);
        }catch (NullPointerException e){
            Log.d(TAG, "getInfoWindow:NullPointerException "+e.getMessage());
        }

        return window;

    }

    private void renderWindowText(final Marker marker, View view){
        final String title=marker.getTitle();
        String snippet=marker.getSnippet();
        TextView t=view.findViewById(R.id.title);
        if (!title.equals("")){
            t.setText(title);
            t.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (event.getKeyCode()==KeyEvent.ACTION_DOWN){
                        Log.d(TAG, "onEditorAction: pressed");
                    }
                    return true;
                }
            });
        }

        TextView d=view.findViewById(R.id.des);
        if (!title.equals("")){
            d.setText(snippet);
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker,window);
        return window;
    }
}
