package org.codeandmagic.deferredobject.merge;

/** User: cvrabie1 Date: 10/07/2012 */
public class MergedPromiseResult5<R1,R2,R3,R4,R5> extends MergedPromiseResult4<R1,R2,R3,R4>{
  public final R5 _5;
  public MergedPromiseResult5(R1 r1, R2 r2, R3 r3, R4 r4, R5 r5){
    super(r1, r2, r3, r4);
    _5 = r5;
  }
  public R5 fifth(){
    return _5;
  }
}
