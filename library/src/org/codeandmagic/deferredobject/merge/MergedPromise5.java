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

package org.codeandmagic.deferredobject.merge;

import org.codeandmagic.deferredobject.AbstractPromise;
import org.codeandmagic.deferredobject.MergedPromiseTracker;
import org.codeandmagic.deferredobject.Promise;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromise5<T extends MergedPromiseResult5<Resolved1,Resolved2, Resolved3, Resolved4, Resolved5>,
  Resolved1,Resolved2, Resolved3, Resolved4, Resolved5>
  extends AbstractPromise< T, MergedPromiseReject, MergedPromiseProgress>
  implements MergedPromise{

  public MergedPromise5( final Promise<Resolved1, ?, ?> promise1, final Promise<Resolved2, ?, ?> promise2,
                         final Promise<Resolved3, ?, ?> promise3, final Promise<Resolved4, ?, ?> promise4, final Promise<Resolved5, ?, ?> promise5){
		new MergedPromiseTracker<T>(this, promise1, promise2, promise3, promise4, promise5);
  }
}
