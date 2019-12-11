package com.aryeh.CouponSystem.threads;

import com.aryeh.CouponSystem.rest.ClientSession;

import java.util.Iterator;
import java.util.Map;

public class ClientSessionCleanerTask implements Runnable {
    private boolean run = true;
    private static final long HALF_HOUR_IN_MILLIS = 30 * 60 * 1_000;
    private static final long TEN_SECONDS_IN_MILLIS = 10 * 1_000;
    private Map<String, ClientSession> tokensMap;

    public ClientSessionCleanerTask(Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @Override
    public void run() {
        while (run) {
            try {
                Thread.sleep(TEN_SECONDS_IN_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            iterateOverTokensMap();
        }
    }

    private synchronized void iterateOverTokensMap() {
        Iterator<String> it = tokensMap.keySet().iterator();
        while (it.hasNext()) {
            String token = it.next();
            ClientSession session = tokensMap.get(token);
            if (System.currentTimeMillis() - session.getLastAccessedMillis() > HALF_HOUR_IN_MILLIS) {
                tokensMap.remove(token);
            }
        }
    }
}
