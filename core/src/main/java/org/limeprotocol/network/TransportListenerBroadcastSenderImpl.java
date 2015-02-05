package org.limeprotocol.network;

import org.limeprotocol.Envelope;

import java.util.*;

public class TransportListenerBroadcastSenderImpl implements TransportListenerBroadcastSender {
    private final SortedMap<Integer, Set<Transport.TransportListener>> listeners;
    private final Integer DEFAULT_PRIORITY = 0;

    public TransportListenerBroadcastSenderImpl() {
        this.listeners = new TreeMap<>();
    }

    @Override
    public void addListener(Transport.TransportListener listener){
        addListener(listener, DEFAULT_PRIORITY);
    }

    @Override
    public void addListener(Transport.TransportListener listener, Integer priority){
        Set<Transport.TransportListener> currentListeners = listeners.get(priority);
        if (currentListeners == null){
            currentListeners = new HashSet<>();
            listeners.put(priority, currentListeners);
        }
        currentListeners.add(listener);
    }

    @Override
    public void removeListener(Transport.TransportListener listener){
        Iterator it = listeners.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            if (pairs.getValue().equals(listener)) {
                it.remove();
                return;
            }
        }
    }

    @Override
    public void broadcastOnReceive(Envelope envelope){
        Set<Integer> orderedPriorities = listeners.keySet();
        for (Integer i : orderedPriorities){
            Set<Transport.TransportListener> listenersSet = listeners.get(i);
            for (Transport.TransportListener listener : listenersSet){
                listener.onReceive(envelope);
            }
        }
    }

    @Override
    public void broadcastOnException(Exception e) {
        Set<Integer> orderedPriorities = listeners.keySet();
        for (Integer i : orderedPriorities){
            Set<Transport.TransportListener> listenersSet = listeners.get(i);
            for (Transport.TransportListener listener : listenersSet){
                e.printStackTrace();
                listener.onException(e);
            }
        }
    }

    @Override
    public void broadcastOnClosing(){
        Set<Integer> orderedPriorities = listeners.keySet();
        for (Integer i : orderedPriorities){
            Set<Transport.TransportListener> listenersSet = listeners.get(i);
            for (Transport.TransportListener listener : listenersSet){
                listener.onClosing();
            }
        }
    }

    @Override
    public void broadcastOnClosed(){
        Set<Integer> orderedPriorities = listeners.keySet();
        for (Integer i : orderedPriorities){
            Set<Transport.TransportListener> listenersSet = listeners.get(i);
            for (Transport.TransportListener listener : listenersSet){
                listener.onClosed();
            }
        }
    }
}
