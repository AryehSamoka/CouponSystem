package com.aryeh.CouponSystem.Service;

public abstract class AbsService {
    protected long clientId;

    public abstract long getClientId();

    public abstract void setClientId(long clientId);
}
