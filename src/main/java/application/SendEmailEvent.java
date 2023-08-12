package application;

public class SendEmailEvent {
    public String toAddress;
    public String message;

    public SendEmailEvent(String toAddress, String message) {
        this.toAddress = toAddress;
        this.message = message;
    }
}
