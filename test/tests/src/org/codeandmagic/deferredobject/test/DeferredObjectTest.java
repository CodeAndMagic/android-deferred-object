package org.codeandmagic.deferredobject.test;

import java.util.List;

import org.codeandmagic.deferredobject.CompleteCallback;
import org.codeandmagic.deferredobject.DeferredObject;
import org.codeandmagic.deferredobject.ProgressCallback;
import org.codeandmagic.deferredobject.RejectCallback;
import org.codeandmagic.deferredobject.ResolveCallback;

import junit.framework.TestCase;

public class DeferredObjectTest extends TestCase {
	
	public static class InspectableDeferred extends DeferredObject<Object, Object, Object> {
		public List<ResolveCallback<Object>> rs(){
			return resolveCallbacks;
		}
		public int rsl(){
			return resolveCallbacks.size();
		}
		public List<RejectCallback<Object>> rj(){
			return rejectedCallbacks;
		}
		public int rjl(){
			return rejectedCallbacks.size();
		}
		public List<ProgressCallback<Object>> pr(){
			return progressCallbacks;
		}	
		public int prl(){
			return progressCallbacks.size();
		}
		public List<CompleteCallback<Object,Object>> co(){
			return completeCallbacks;
		}	
		public int col(){
			return completeCallbacks.size();
		}
	}
	
	public static ResolveCallback<Object> resolved() {
		return new ResolveCallback<Object>() {
			@Override public void onResolve(Object resolved) {}
		}; 
	}
	
	public static RejectCallback<Object> rejected() {
		return new RejectCallback<Object>() {
			@Override public void onReject(Object resolved) {}
		};
	}; 
	
	public static ProgressCallback<Object> progress() {
		return new ProgressCallback<Object>() {
			@Override public void onProgress(Object progress) {}
		};
	}
	
	public static CompleteCallback<Object,Object> complete() {
		return new CompleteCallback<Object, Object>() {
			@Override public void onComplete(Object resolved, Object rejected) {}
		};
	}
	
	InspectableDeferred def = new InspectableDeferred();
	ResolveCallback<Object> rs1 = resolved();
	ResolveCallback<Object> rs2 = resolved();	
	RejectCallback<Object> rj1 = rejected();
	CompleteCallback<Object, Object> co1 = complete();
	ProgressCallback<Object> pr1 = progress();
				
	public void testResolvedCallbackAdd(){	
		assertEquals(0, def.rsl());
		
		def.done(rs1);
		assertEquals(1, def.rsl());
		assertEquals(def.rs().get(0), rs1);
		
		def.done(rs2);
		assertEquals(2, def.rsl());
		assertEquals(def.rs().get(1), rs2);
	}
	
	public void testRejectedCallbackAdd(){
		def.fail(rj1);
		assertEquals(1, def.rjl());
	}
	
	public void testProgressCallbackAdd(){
		def.progress(pr1);
		assertEquals(1, def.prl());
	}
	
	public void testCompleteCallbackAdd(){
		def.always(co1);
		assertEquals(1, def.col());
	}
	
	public void testThenCallbackAdd(){
		def.then(rs1, rj1, pr1, co1);
		def.then(rs2);
		assertEquals(2, def.rsl());
		assertEquals(1, def.rjl());
		assertEquals(1, def.prl());
		assertEquals(1, def.col());
	}
	
}
