# Android Deferred Object

Android-Deferred-Object is a chainable utility object that can register multiple callbacks into callback queues, invoke callback queues, and relay the success or failure state of any synchronous or asynchronous function.

It's similar to the [DeferredObject in jQuery](http://api.jquery.com/category/deferred-object/) which in turn is bassed on the [CommonJS Promises/A](http://wiki.commonjs.org/wiki/Promises/A) design. In most cases the names of the functions are preserved as in jQuery so there's no confusion for those that already know the pattern. Obviously some things needed to be changed because of the lack of anonymous functions and the strong types in Java.

## How does it work
Well, simply put when you call a function that needs to execute asynchronously you get back a **Promise** object. On this promise you can attach callbacks that will get fired in case of success/failure/progress notifications. Whenever the piece of code that was executing asynchronously finishes as expected the Promise is called to be **resolved**. If some error occurred that promise is called to be **rejected**.

You can keep a promise long after the execution of the asynchronous code has ended and keep adding callbacks to it. In this case the callbacks will get fired immediately.

You can merge these promise objects into a **MergePromise** in order to attach a callback to be executed when several promises are resolved.

You can pipe promises so the pieces of asynchronous code gets executed serially like it would have been synchronous. 

## Why are you using both Promise and DeferredObject terms?

A **Promise** is a immutable version of a **DeferredObject**. All DeferredObjects are Promises which is the most generic term is preferred. Not all Promises are necessarily a DeferredObject. 

On a DeferredObject you have access to the actual **resolve** and **reject** methods which is obviously useful when you want to write you're own Deferred. But most library code should return a Promise to prevent users of that library to mess with the internal workings of the library. For example in case of a async HTTP call if would be weird if someone other than the actual piece of code that does the HTTP call fires the resolved callbacks.

## Why/When should I use the Deferred Object / Promise pattern?
The DeferredObject/Promise pattern can help you to better organise your code, especially the asynchronous type. It also makes it really easy to merge and pipe the execution of different pieces of asynchronous code, which otherwise would get really messy to code. 

We also have wrappers for common Android tasks like AsyncTask which makes it trivial to use DeferredObject in an Android program and combine that with your own DeferredObjects.

In fact we think that DeferredObject makes handling of async code so easy that it encourages your to use more of it which will improve the responsiveness of your interface.

## Common Use Cases
### Success and Failure callbacks for a task

Let's say you need to do something asynchronously like a HTTP request. You can wrap that request into a DeferredAsyncTask and attach callbacks to this action. (note that for HTTP requests we provide you with DeferredHttpUrlConnection or DeferredHttpRequest which makes it even more easy to use but we'll exemplify this scenario with a DeferredAsyncTask so you get the hang of how the pattern works and how you can extend it for your own tasks).

``` java
new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() {
    protected abstract Resolved doInBackground() throws Exception {
       //do your async code here
    }
}
.done( new ResolveCallback<HttpResponse> {
    public void onResolve(HttpResponse resolved) {
       //your success code here
    }
})
.fail ( new RejectCallback<HttpResponse> {
    public void onReject(HttpResponse rejected) {
       //your failure code here
    }
});
```

**See the main article on [Promise-Callbacks](wiki/Promise-Callbacks) to see how you can add multiple success or failure callbacks, how to add  callbacks that get triggered both in case of success and failure, how to add a callback that will get triggered for progress notifications and how you can add callbacks for activities that already finished.**

### Merging several promises 

Let's say that you need to do several async tasks and when they're all done you want to execute a piece of code. You can easily handle this my **merging** those promises with the **DeferredObject.when** method.

``` java
Promise<A1,B1,C1> p1 = new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() { ... }; 
Promise<A2,B2,C2> p1 = new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() { ... };
Promise<A3,B3,C3> p3 = new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() { ... };
//when gives you a new promise that gets triggered when all the merged promises are resolved or one of them fails
DeferredObject.when(p1,p2,p3)
.done(new ResolveCallback<MergedPromiseResult3<A1,A2,A3>() {
    public void onResolve(MergedPromiseResult3<A1,A2,A3> resolved){
        Log.i(TAG, "got: " + resolved.first() + resolved.second() + resolved.third());
    }
})
.fail(new RejectCallback<MergedPromiseReject>() {
    public void onReject(MergedPromiseReject rejected) {
        //failure handling here
    }
})
.progress(new ProgressCallback<MergedPromiseProgress>() {
    public void onProgress(final MergedPromiseProgress progress){
         //you get notified as the merged promises keep coming in
    }
});
//Merging doesn't stop you do add individual callbacks for promises that are in the merge
p1.done(...).fail(...)
//Or even merging them in another way
DeferredObject.when(p1,p2).done(...).fail(...)
```

**See the main article on [Merging Promises](wiki/Merging-Promises)**

### Piping and filtering deferreds 

Let's say that you need to do an async piece of code like two http calls in response to another async piece of code. Obviously you could write the dependant call in the done callback of the first call, and sometimes that's the right approach. However now you will be forced to add the callbacks to the second call in the callback of the first one. If you have several of these dependencies it will get (horizontally) awkward really fast.

``` java
new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() {...}.done( new ResolveCallback<HttpResponse> {
    public void onResolve(HttpResponse resolved) {
        //callback to first call here
        new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() {...}
        .done(
            //callback to second call here
            new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() {...}
            .done(
                //callback to third call here
            );
        );
     }
});
```

Instead you can **pipe** the first call with the second. Piping gives you a promise that doesn't exist yet so you can attach callbacks on it in a chain.

``` java
new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() {...}
.done( /* callback to first call here */ )
.pipe(new ResolvePipe<HttpResponse,HttpResponse,Void>() {
    public Promise<HttpResponse,HttpResponse,Void> pipeResolved(HttpResponse response1){
       return new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() { ... }
    }
})
.done( /* callback to second call here */ )
.pipe(new ResolvePipe<HttpResponse,HttpResponse,Void>() {
    public Promise<HttpResponse,HttpResponse,Void> pipeResolved(HttpResponse response1){
       return new DeferredAsyncTask<HttpResponse,HttpResponse,Void>() { ... }
    }
})
.done( /* callback to third call here */ )
```
