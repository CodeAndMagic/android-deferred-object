package org.codeandmagic.promise.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.GridView;

/**
 * Created by evelina on 18/03/2014.
 */
public class PatternsActivity extends ActionBarActivity {

    private GridView mGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGridView = (GridView) findViewById(R.id.grid);
    }

}
