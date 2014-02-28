/*
 * Copyright (c) 2014 CodeAndMagic
 * Cristian Vrabie, Evelina Vrabie
 *
 * This file is part of android-promise.
 * android-promise is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License,or (at your option)
 * any later version.
 *
 * android-promise is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with android-promise. If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.promise.util;

import android.widget.TextView;
import org.codeandmagic.promise.Callback;

/**
 * Created by evelina on 24/02/2014.
 */
public final class TextViewUtils {

    private TextViewUtils() {
    }

    public static Callback<Float> setPercent(final TextView textView) {
        return new Callback<Float>() {
            @Override
            public void onCallback(Float result) {
                textView.setText(String.format("%.2f%%", result));
            }
        };
    }

    public static Callback<Float> setFloat(final TextView textView) {
        return new Callback<Float>() {
            @Override
            public void onCallback(Float result) {
                textView.setText(String.valueOf(result));
            }
        };
    }

    public static Callback<Integer> setInt(final TextView textView) {
        return new Callback<Integer>() {
            @Override
            public void onCallback(Integer result) {
                textView.setText(String.valueOf(result));
            }
        };
    }

    public static Callback<String> setString(final TextView textView) {
        return new Callback<String>() {
            @Override
            public void onCallback(String result) {
                textView.setText(result);
            }
        };
    }
}
