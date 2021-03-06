package com.aryeh.CouponSystem.Service;

import com.aryeh.CouponSystem.data.entity.Admin;
import com.aryeh.CouponSystem.data.entity.Company;
import com.aryeh.CouponSystem.data.entity.Coupon;
import com.aryeh.CouponSystem.data.entity.Customer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AdminService {
    Admin createAdmin(Admin admin);

    Admin findById();

    void deleteById();

    Admin update(Admin admin);

    Company createCompany(Company company);

    Company getCompanyById(long companyId);

    Company updateCompany(Company company);

    Company deleteCompanyById(long companyId);

    List<Company> findAllCompanies();

    List<Company> findAllCompaniesWithoutCoupons();

    List<Coupon> findCompanyCoupons(long companyId);

    List<Company> findAllCompaniesWithNoCoupons();

    Customer getCustomerById(long customerId);

    Customer createCustomer(Customer customer);

    Customer updateCustomer(Customer customer);

    Customer deleteCustomerById(long customerId);

    List<Customer> findAllCustomers();

    List<Customer> findAllCustomersWithoutCoupons();

    List<Integer> findAllCategories();

    List<String> getEmailsCompsAndCustoms();

    List<String[]> findPairsEmailsOfCompsCustomersOrderedByCategory();

    List<Integer[]> CountPairsByCategory();

    void InsertRandomValuesToDB();

    void checkRootAdmin();

    String findPasswordByEmail(String email);

    Set<Set<String>> pairsCompaniesSameCustomer();
}
