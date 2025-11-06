public class EmailMessage implements IMessage{

    @Override
    public void sendMessage(String message) {
        System.out.println("Email message sent: " + message);
    }
}
