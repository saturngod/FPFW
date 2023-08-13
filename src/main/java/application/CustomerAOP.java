package application;

import framework.annotations.After;
import framework.annotations.Aspect;

@Aspect
public class CustomerAOP {
    @After(pointcut = "CustomerDAO.showCustomer")
    public void showProcess() {
        System.out.println("ASPECT CALL");
    }
}
