package com.aryeh.CouponSystem.threads;

import com.aryeh.CouponSystem.rest.ClientSession;

import java.util.Iterator;
import java.util.Map;

public class ClientSessionCleanerTask implements Runnable {
    private boolean run = true;
    private static final long MINUTE_IN_MILLIS = 60 * 1_000;
    private static final long SECOND_IN_MILLIS = 1_000;
    private static final long MINUTE_IN_SECONDS = 60;
    private Map<String, ClientSession> tokensMap;

    public ClientSessionCleanerTask(Map<String, ClientSession> tokensMap) {
        this.tokensMap = tokensMap;
    }

    @Override
    public void run() {
        while (run) {
            try {
                Thread.sleep(SECOND_IN_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            iterateOverTokensMap();
        }
    }

    private synchronized void iterateOverTokensMap() {
        Iterator<String> it;
        String token;
        ClientSession session;
        it = tokensMap.keySet().iterator();
        while (it.hasNext()) {
            token = it.next();
            session = tokensMap.get(token);
            if (System.currentTimeMillis() - session.getLastAccessedMillis() > MINUTE_IN_MILLIS) {
                tokensMap.remove(token);
            }
        }
    }
}
