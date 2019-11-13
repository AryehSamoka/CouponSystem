package com.aryeh.CouponSystem.threads;

import com.aryeh.CouponSystem.rest.ClientSession;

import java.util.Map;

    public class ClientSessionCleanerTask implements Runnable {
        private boolean run = true;
        private static final long MINUTE_IN_MILLIS = 60 * 1_000;
        private Map<String, ClientSession> tokensMap;

        public ClientSessionCleanerTask(Map<String, ClientSession> tokensMap) {
            this.tokensMap = tokensMap;
        }

        @Override
        public void run() {

            while (run) {
                for (Map.Entry<String, ClientSession> entry : tokensMap.entrySet()) {
                    ClientSession session = entry.getValue();
                    boolean minuteAccessed = System.currentTimeMillis() - session.getLastAccessedMillis() > MINUTE_IN_MILLIS;
                    if (minuteAccessed) {
                        tokensMap.remove(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }
