package com.github.ykrank.androidautodispose;

import android.support.annotation.NonNull;
import android.view.View;

import com.github.ykrank.androidlifecycle.AndroidLifeCycle;
import com.github.ykrank.androidlifecycle.event.ViewEvent;
import com.github.ykrank.androidlifecycle.lifecycle.LifeCycleListener;
import com.github.ykrank.androidlifecycle.manager.ViewLifeCycleManager;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;

/**
 * Provide view lifecycle event
 */
final class ViewEventObservable extends Observable<AndroidRxEvent> {
    private final ViewLifeCycleManager lifeCycleManager;
    private final ViewEvent event;

    ViewEventObservable(View view, ViewEvent event) {
        this.lifeCycleManager = AndroidLifeCycle.with(view);
        this.event = event;
    }

    @Override
    protected void subscribeActual(final Observer<? super AndroidRxEvent> observer) {
        LifeCycleListener listener = new LifeCycleListener() {
            @Override
            public void accept() {
                observer.onNext(AndroidRxEvent.DISPOSE);
            }
        };
        observer.onSubscribe(new ListenerDispose(lifeCycleManager, listener, event));

        lifeCycleManager.listen(event, listener);

        observer.onNext(AndroidRxEvent.START);
    }

    private static final class ListenerDispose extends MainThreadDisposable {
        private final ViewLifeCycleManager lifeCycleManager;
        private final LifeCycleListener listener;
        private final ViewEvent event;

        ListenerDispose(@NonNull ViewLifeCycleManager lifeCycleManager,
                        @NonNull LifeCycleListener listener, ViewEvent event) {
            this.lifeCycleManager = lifeCycleManager;
            this.listener = listener;
            this.event = event;
        }

        @Override
        protected void onDispose() {
            lifeCycleManager.unListen(event, listener);
        }
    }
}
