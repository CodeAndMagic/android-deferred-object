package org.codeandmagic.deferredobject.merge;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromiseResult4<R1,R2,R3,R4> extends MergedPromiseResult3<R1,R2,R3>{
  public final R4 _4;
  public MergedPromiseResult4(R1 r1, R2 r2, R3 r3, R4 r4){
    super(r1, r2, r3);
    _4 = r4;
  }
  public R4 forth(){
    return _4;
  }
}
