/*
 * Copyright 2012 Cristian Vrabie, Evelina Vrabie
 *
 * This file is part of android-deferred-object.
 *
 * android-deferred-object is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * android-deferred-object is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.codeandmagic.deferredobject;

import org.codeandmagic.deferredobject.merge.*;

/** User: cvrabie1 Date: 09/07/2012 */
public abstract class DeferredObject<Resolved, Rejected, Progress> extends AbstractPromise<Resolved,Rejected,Progress> {

  public static <R1, R2> Promise<MergedPromiseResult2<R1, R2>, MergedPromiseReject, MergedPromiseProgress>
         when( final Promise<R1,?,?> p1, final Promise<R2,?,?> p2){
    return new MergedPromise2(p1,p2);
  }

  public static <R1, R2, R3> Promise<MergedPromiseResult3<R1, R2, R3>, MergedPromiseReject, MergedPromiseProgress>
  when( final Promise<R1,?,?> p1, final Promise<R2,?,?> p2, final Promise<R3,?,?> p3){
    return new MergedPromise3(p1,p2,p3);
  }

  public static <R1, R2, R3, R4> Promise<MergedPromiseResult4<R1, R2, R3, R4>, MergedPromiseReject, MergedPromiseProgress>
  when( final Promise<R1,?,?> p1, final Promise<R2,?,?> p2, final Promise<R3,?,?> p3, final Promise<R4,?,?> p4){
    return new MergedPromise4(p1,p2,p3,p4);
  }

  public static <R1, R2, R3, R4, R5> Promise<MergedPromiseResult5<R1, R2, R3, R4, R5>, MergedPromiseReject, MergedPromiseProgress>
  when( final Promise<R1,?,?> p1, final Promise<R2,?,?> p2, final Promise<R3,?,?> p3, final Promise<R4,?,?> p4, final Promise<R5,?,?> p5){
    return new MergedPromise5(p1,p2,p3,p4,p5);
  }

  public static Promise<Object[], MergedPromiseReject, MergedPromiseProgress> when( final Promise<?,?,?>[] promises ){
    return new MergedPromiseN(promises);
  }

  @Override
  public final void notify(Progress progress) {
    super.notify(progress);
  }

  @Override
  public final void resolve(Resolved resolved) {
    super.resolve(resolved);
  }

  @Override
  public final void reject(Rejected rejected) {
    super.reject(rejected);
  }
}
