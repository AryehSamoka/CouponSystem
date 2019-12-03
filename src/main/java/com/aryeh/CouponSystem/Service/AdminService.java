package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Admin;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Customer;
import org.springframework.context.ApplicationContext;

import java.util.List;

public interface AdminService {
    public Admin createAdmin(Admin admin);

    public Admin findById();

    public void deleteById();

    public Admin update(Admin admin);

    public Company createCompany(Company company);

    public Company updateCompany(Company company);

    public Company deleteCompanyById(long companyId);

    public List<Company> findAllCompanies();

    List<Company> findAllCompaniesWithoutCoupons();

    public Customer createCustomer(Customer customer);

    public Customer updateCustomer(Customer customer);

    public Customer deleteCustomerById(long customerId);

    public List<Customer> findAllCustomers();

    public List<Customer> findAllCustomersWithoutCoupons();

    public List<Integer> findAllCategories();

    public List<String> getEmailsCompsAndCustoms();

    public void InsertRandomValuesToDB();
}
