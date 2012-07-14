package org.codeandmagic.deferredobject.test;

import java.util.List;

import org.codeandmagic.deferredobject.CompleteCallback;
import org.codeandmagic.deferredobject.DeferredObject;
import org.codeandmagic.deferredobject.ProgressCallback;
import org.codeandmagic.deferredobject.Promise.State;
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
	
	public static class InspectableResolveCallback implements ResolveCallback<Object> {
		public int called = 0;
		@Override public void onResolve(Object resolved) { ++called; }
	}
	
	public static class InspectableRejectCallback implements RejectCallback<Object> {
		public int called = 0;
		@Override public void onReject(Object resolved) { ++called; }
	}; 
	
	public static class InspectableProgressCallback implements ProgressCallback<Object> {
		public int called = 0;
		@Override public void onProgress(Object progress) { ++called; }
	}
	
	public static class InspectableCompleteCallback implements CompleteCallback<Object,Object> {
		public int called = 0;
		@Override public void onComplete(Object resolved, Object rejected) { ++called; }
	}
	
	InspectableDeferred def = new InspectableDeferred();
	InspectableResolveCallback rs1 = new InspectableResolveCallback();
	InspectableResolveCallback rs2 = new InspectableResolveCallback();	
	InspectableRejectCallback rj1 = new InspectableRejectCallback();
	InspectableCompleteCallback co1 = new InspectableCompleteCallback();
	InspectableProgressCallback pr1 = new InspectableProgressCallback();
				
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
	
	public void testDoneTrigger(){
		def.done(rs1).done(rs2).always(co1).fail(rj1).progress(pr1);
		def.resolve(new Object());
		assertEquals(State.RESOLVED, def.state());
		assertEquals(1, rs1.called);
		assertEquals(1, rs2.called);
		assertEquals(1, co1.called);
		assertEquals(0, rj1.called);
		assertEquals(0, pr1.called);
	}
	
	public void testRejectTrigger(){
		def.done(rs1).done(rs2).always(co1).fail(rj1).progress(pr1);
		def.reject(new Object());
		assertEquals(State.REJECTED, def.state());
		assertEquals(0, rs1.called);
		assertEquals(0, rs2.called);
		assertEquals(1, co1.called);
		assertEquals(1, rj1.called);
		assertEquals(0, pr1.called);
	}
	
	public void testProgressTrigger(){
		def.done(rs1).done(rs2).always(co1).fail(rj1).progress(pr1);
		def.notify(new Object());
		assertEquals(State.PENDING, def.state());
		assertEquals(0, rs1.called);
		assertEquals(0, rs2.called);
		assertEquals(0, co1.called);
		assertEquals(0, rj1.called);
		assertEquals(1, pr1.called);
	}
	
	public void testBindAfterResolve(){
		assertEquals(State.PENDING, def.state());
		def.resolve(new Object());
		assertEquals(State.RESOLVED, def.state());
		def.done(rs1);
		assertEquals(1, rs1.called); //the callback gets called even if the deferred was resolved before the bind
		assertEquals(0, rs2.called);
		def.done(rs2);
		assertEquals(1, rs1.called); //each callback gets called only once
		assertEquals(1, rs2.called);
		assertEquals(0, co1.called);
		def.always(co1);
		assertEquals(1, co1.called);
		assertEquals(1, rs1.called); //each callback gets called only once
		assertEquals(1, rs2.called);
	}
	
}
