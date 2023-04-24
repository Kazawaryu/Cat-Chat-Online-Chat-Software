package com.ghosnp.catchat.backendAssembly;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public final class ListenerHandle<T> implements ListenerHandles {

    private final ObservableValue<T> observable;
    private final ChangeListener<? super T> listener;

    private boolean attached;

    public ListenerHandle(ObservableValue<T> observable, ChangeListener<? super T> listener) {
        this.observable = observable;
        this.listener = listener;
    }

    public void attach() {
        if (!attached) {
            observable.addListener(listener);
            attached = true;
        }
    }

    public void detach() {
        if (attached) {
            observable.removeListener(listener);
            attached = false;
        }
    }


}