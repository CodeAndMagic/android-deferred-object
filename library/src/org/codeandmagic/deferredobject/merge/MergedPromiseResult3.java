package org.codeandmagic.deferredobject.merge;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromiseResult3<R1,R2,R3> extends MergedPromiseResult2<R1,R2>{
  public final R3 _3;
  public MergedPromiseResult3(R1 r1, R2 r2, R3 r3){
    super(r1, r2);
    _3 = r3;
  }
  public R3 third(){
    return _3;
  }
}
