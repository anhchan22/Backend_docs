public class Client {
    private IMessage messageService;
//    public Client(IMessage messageService) {
//        this.messageService = messageService;
//    }

    public void setMessage(IMessage messageService) {
        this.messageService = messageService;
    }

    public void processMessage(String message) {
        this.messageService.sendMessage(message);
    }
}
