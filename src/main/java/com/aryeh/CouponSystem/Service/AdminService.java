package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Admin;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Customer;

public interface AdminService {
    public Admin createAdmin(Admin admin);

    public Admin findById();

    public void deleteById();

    public Admin update(Admin admin);

    public Company createCompany(Company company);

    public Company updateCompany(Company company);

    public Customer createCustomer(Customer customer);

    public Customer updateCustomer(Customer customer);

    public Company deleteCompanyById(long companyId);

    public Customer deleteCustomerById(long customerId);
}
