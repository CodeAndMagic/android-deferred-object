package org.codeandmagic.deferredobject.merge;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromiseResult2<R1,R2>{
  public final R1 _1;
  public final R2 _2;
  public MergedPromiseResult2(R1 r1, R2 r2){
    _1 = r1;
    _2 = r2;
  }
  public R1 first(){
    return _1;
  }
  public R2 second(){
    return _2;
  }
}
