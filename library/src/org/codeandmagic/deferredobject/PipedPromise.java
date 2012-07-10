package org.codeandmagic.deferredobject;

/** User: cvrabie1 Date: 09/07/2012 */
public class PipedPromise<Resolved1, Rejected1, Progress1, Resolved2, Rejected2, Progress2>
  extends DeferredObject<Resolved2, Rejected2, Progress2>  {

  protected PipedPromise(final Promise<Resolved1, Rejected1, Progress1> pipedPromise,
                      final ResolveFilter<Resolved1, Resolved2> resolvedFilter,
                      final RejectFilter<Rejected1, Rejected2> rejectedFilter,
                      final ProgressFilter<Progress1, Progress2> progressFilter
  ){
    pipedPromise.done(new ResolveCallback<Resolved1>() {
      @Override
      public void onResolve(Resolved1 resolved1) {
        PipedPromise.this.resolve( resolvedFilter.filterResolved(resolved1) );
      }
    });

    pipedPromise.fail(new RejectCallback<Rejected1>() {
      @Override
      public void onReject(Rejected1 rejected1) {
        PipedPromise.this.reject( rejectedFilter.filterRejected(rejected1) );
      }
    });

    pipedPromise.progress(new ProgressCallback<Progress1>() {
      @Override
      public void onProgress(Progress1 progress1) {
        PipedPromise.this.notify( progressFilter.filterProgress(progress1) );
      }
    });
  }
}
