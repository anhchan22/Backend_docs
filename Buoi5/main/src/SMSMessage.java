public class SMSMessage implements IMessage {
    @Override
    public void sendMessage(String message) {
        System.out.println("SMS message sent: " + message);
    }
}
