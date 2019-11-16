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
        String token;
        ClientSession session;
        long TimeMillisBeforeForLoop;
        Iterator<String> it;

        while (run) {
            TimeMillisBeforeForLoop = System.currentTimeMillis();
            for (int i = 1; i <= MINUTE_IN_SECONDS; i++) {
                try {
                    Thread.sleep( i*SECOND_IN_MILLIS + TimeMillisBeforeForLoop - System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
    }
}
