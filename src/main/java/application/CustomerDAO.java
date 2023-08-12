package application;

import framework.annotations.Service;

@Service
public class CustomerDAO implements ICustomerDAO {
    public void showCustomer() {
        System.out.println("SHOW CUSTOMER FROM NORMAL");
    }
}
