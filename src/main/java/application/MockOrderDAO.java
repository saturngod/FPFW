package application;

import framework.annotations.Profile;
import framework.annotations.Service;
import framework.annotations.Value;

@Service
@Profile("mock")
public class MockOrderDAO implements IOrderDAO{
    @Value("${smtpServer}")
    String myworld;

    public void printInformation() {
        System.out.println("Message FROM OrderDAO " + myworld);
    }
}

