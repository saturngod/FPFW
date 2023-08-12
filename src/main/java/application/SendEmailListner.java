package application;

import framework.annotations.EventListner;
import framework.annotations.Service;

@Service
public class SendEmailListner {
    @EventListner
    public void eventListender(SendEmailEvent event) {
        System.out.println("EMAIL TO " + event.toAddress);
        System.out.println("EMAIL Message " + event.message);
    }

}
