package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Coupon;

import java.util.List;

public abstract class AbsService {
    protected long clientId;

    public abstract long getClientId();

    public abstract void setClientId(long clientId);
}
