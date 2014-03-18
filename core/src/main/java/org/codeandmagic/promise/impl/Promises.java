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

package org.codeandmagic.promise.impl;

import org.codeandmagic.promise.Callback;
import org.codeandmagic.promise.Promise;
import org.codeandmagic.promise.Promises;
import org.codeandmagic.promise.Transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by evelina on 18/03/2014.
 */
class PromisesList<Success> implements Promises<Success> {

    private final Collection<Promise<Success>> mPromises;

    PromisesList(Collection<Promise<Success>> promises) {
        mPromises = promises;
    }

    @Override
    public <Success2> Promises<Success2> map(Transformation<Success, Success2> transform) {
        List<Promise<Success2>> newPromises = new ArrayList<Promise<Success2>>(mPromises.size());
        for (Promise<Success> p : mPromises) {
            newPromises.add(p.map(transform));
        }
        return new PromisesList<Success2>(newPromises);
    }

    @Override
    public Promises<Success> onSuccess(Callback<Success> onSuccess) {
        for (Promise<Success> p : mPromises) {
            p.onSuccess(onSuccess);
        }
        return this;
    }

    @Override
    public Promises<Success> onFailure(Callback<Throwable> onFailure) {
        for (Promise<Success> p : mPromises) {
            p.onFailure(onFailure);
        }
        return this;
    }

    @Override
    public Promises<Success> runOnUiThread() {
        List<Promise<Success>> newPromises = new ArrayList<Promise<Success>>(mPromises.size());
        for (Promise<Success> p : mPromises) {
            newPromises.add(p.runOnUiThread());
        }
        return new PromisesList<Success>(newPromises);
    }
}

class ProxyPromises<Success> implements Promises<Success> {

    private final List<Callback<Success>> mSuccessCallbacks;
    private final List<Callback<Throwable>> mFailureCallbacks;
    private final List<Callback<Collection<Promise<Success>>>> mSetCallbacks;

    ProxyPromises() {
        mSuccessCallbacks = new CopyOnWriteArrayList<Callback<Success>>();
        mFailureCallbacks = new CopyOnWriteArrayList<Callback<Throwable>>();
        mSetCallbacks = new CopyOnWriteArrayList<Callback<Collection<Promise<Success>>>>();
    }

    Promises<Success> setPromises(Collection<Promise<Success>> promises) {
        Promises<Success> thePromise = new PromisesList<Success>(promises);
        for (Callback<Success> c : mSuccessCallbacks) {
            thePromise.onSuccess(c);
        }
        for (Callback<Throwable> c : mFailureCallbacks) {
            thePromise.onFailure(c);
        }
        for (Callback<Collection<Promise<Success>>> c : mSetCallbacks) {
            c.onCallback(promises);
        }
        return thePromise;
    }

    @Override
    public <Success2> Promises<Success2> map(final Transformation<Success, Success2> transform) {
        final ProxyPromises<Success2> proxy = new ProxyPromises<Success2>();
        mSetCallbacks.add(new Callback<Collection<Promise<Success>>>() {
            @Override
            public void onCallback(Collection<Promise<Success>> promises) {
                Collection<Promise<Success2>> newPromises = new ArrayList<Promise<Success2>>(promises.size());
                for (Promise<Success> p : promises) {
                    newPromises.add(p.map(transform));
                }
                proxy.setPromises(newPromises);
            }
        });
        return proxy;
    }

    @Override
    public Promises<Success> onSuccess(Callback<Success> onSuccess) {
        mSuccessCallbacks.add(onSuccess);
        return this;
    }

    @Override
    public Promises<Success> onFailure(Callback<Throwable> onFailure) {
        mFailureCallbacks.add(onFailure);
        return this;
    }

    @Override
    public Promises<Success> runOnUiThread() {
        final ProxyPromises<Success> proxy = new ProxyPromises<Success>();
        mSetCallbacks.add(new Callback<Collection<Promise<Success>>>() {
            @Override
            public void onCallback(Collection<Promise<Success>> promises) {
                Collection<Promise<Success>> newPromises = new ArrayList<Promise<Success>>(promises.size());
                for (Promise<Success> p : promises) {
                    newPromises.add(p.runOnUiThread());
                }
                proxy.setPromises(newPromises);
            }
        });
        return proxy;
    }
}