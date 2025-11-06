public class Main1 {
    public static void main(String[] args) {
        IMessage email = new EmailMessage();
        IMessage sms = new SMSMessage();
//        Client client = new Client(email);
        Client client = new Client();
        client.setMessage(email);

        client.processMessage("hello");
    }
}