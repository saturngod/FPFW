package application;

import framework.annotations.Async;
import framework.annotations.EventListner;
import framework.annotations.Service;

@Service
public class AyncSendEmailListner {
    @Async
    @EventListner
    public void eventListender(SendEmailEvent event) {
        System.out.println("Async EMAIL TO " + event.toAddress);
        System.out.println("Async EMAIL Message " + event.message);
    }

}
