package application;

import framework.annotations.AutoWire;
import framework.annotations.Qualifier;
import framework.annotations.Scheduled;
import framework.annotations.Service;
import framework.classes.FPFWEventPublisher;

import java.lang.reflect.InvocationTargetException;

@Service
public class ProductService {

    ProductDAO productDAO;

    IOrderDAO orderDAO;

    @AutoWire
    @Qualifier(value = "CustomerDAO")
    ICustomerDAO customerDAO;
    @AutoWire
    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
    @AutoWire
    public void setOrderDAO(IOrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @AutoWire
    FPFWEventPublisher publiser;

    public void show() {
        System.out.println("=====START=====");
        productDAO.printProductCount();
        orderDAO.printInformation();
        customerDAO.showCustomer();
        try {
            publiser.publishEvent(new SendEmailEvent("email@example.com","Body Data"));
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        System.out.println("=====END=====");
    }

    @Scheduled(fixedRate = 5000)
    public void welcome() {
        System.out.println("WELCOME at 5 seconds");
    }

    @Scheduled(cron = "1 1")
    public void welcomeSchedule() {
        System.out.println("WELCOMESchedule at 8 seconds");
    }
}
