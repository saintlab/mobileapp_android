package com.omnom.android.socket.listener;

import com.omnom.android.restaurateur.model.table.TableDataResponse;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by michaelpotter on 09/01/15.
 */
public class ListenersSet {

    private Set<BaseEventListener> listeners;

    public ListenersSet() {
        listeners = new HashSet<BaseEventListener>();
    }

    public ListenersSet(BaseEventListener... listeners) {
        this();
        if (listeners != null) {
            Collections.addAll(this.listeners, listeners);
        }
    }

    public void initTableSocket(final String tableId) {
        for (BaseEventListener listener: listeners) {
            listener.initTableSocket(tableId);
        }
    }

    public void initTableSocket(final TableDataResponse table) {
        for (BaseEventListener listener: listeners) {
            listener.initTableSocket(table);
        }
    }

    public void onPause() {
        for (BaseEventListener listener: listeners) {
            listener.onPause();
        }
    }

    public void onDestroy() {
        for (BaseEventListener listener: listeners) {
            listener.onDestroy();
        }
    }


}
