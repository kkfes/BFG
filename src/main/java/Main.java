import bot.BFGBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            new TelegramBotsApi(DefaultBotSession.class).registerBot(new BFGBot());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
