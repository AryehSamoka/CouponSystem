package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Admin;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Customer;

public interface AdminService {
    public Admin createAdmin(Admin admin);

    public Admin deleteById();

    public Company createCompany(Company company);

    public Customer createCustomer(Customer customer);
}
