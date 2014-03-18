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

package org.codeandmagic.promise.sample.patterns;

import android.graphics.Bitmap;

/**
 * Created by evelina on 18/03/2014.
 */
class Pattern {
    public final String id;
    public final String title;
    public final String imageUrl;
    public final Bitmap bitmap;

    public Pattern(String id, String title, String imageUrl, Bitmap bitmap) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.bitmap = bitmap;
    }

    public Pattern withBitmap(Bitmap bitmap) {
        return new Pattern(id, title, imageUrl, bitmap);
    }
}
