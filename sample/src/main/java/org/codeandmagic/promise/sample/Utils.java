package org.codeandmagic.promise.sample;

import android.app.Activity;
import android.content.res.Resources;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by evelina on 05/03/2014.
 */
public class Utils {

    private Utils() {
    }

    public static class Sample {
        public final String title;
        public final String subtitle;
        public final Class<? extends Activity> activity;

        public Sample(String title, String subtitle, Class<Activity> activity) {
            this.title = title;
            this.subtitle = subtitle;
            this.activity = activity;
        }
    }

    public static List<Sample> parseSamples(Resources resources) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(resources.openRawResource(R.raw.samples)));
            final StringBuilder json = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }
            final List<Sample> samples = new ArrayList<Sample>();
            final JSONArray array = new JSONArray(json.toString());
            final int length = array.length();
            for (int i = 0; i < length; ++i) {
                samples.add(parseSample(array.getJSONObject(i)));
            }
            return samples;

        } catch (Exception e) {
            return Collections.emptyList();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Sample parseSample(JSONObject json) throws Exception {
        final Class<Activity> activity = (Class<Activity>) Class.forName(json.getString("activity"));
        return new Sample(json.getString("title"), json.getString("description"), activity);
    }
}
