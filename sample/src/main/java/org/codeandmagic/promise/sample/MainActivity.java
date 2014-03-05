package org.codeandmagic.promise.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TwoLineListItem;

import java.util.List;

import static org.codeandmagic.promise.sample.Utils.Sample;
import static org.codeandmagic.promise.sample.Utils.parseSamples;

/**
 * Created by evelina on 05/03/2014.
 */
public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(new DemoAdapter(this, parseSamples(getResources())));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.this, ((Sample) parent.getItemAtPosition(position)).activity));
            }
        });
    }


    private static class DemoAdapter extends ArrayAdapter<Sample> {
        public DemoAdapter(Context context, List<Sample> objects) {
            super(context, android.R.layout.simple_expandable_list_item_2, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TwoLineListItem view = (convertView != null) ? (TwoLineListItem) convertView :
                    (TwoLineListItem) View.inflate(getContext(), android.R.layout.simple_expandable_list_item_2, null);
            final Sample demo = getItem(position);
            view.getText1().setText(demo.title);
            view.getText2().setText(demo.subtitle);
            return view;
        }
    }
}
