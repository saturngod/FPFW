package application;

import framework.annotations.Service;

@Service
public class MockCustomerDAO implements ICustomerDAO{
    public void showCustomer() {
        System.out.println("SHOW CUSTOMER FROM MOCK");
    }
}
