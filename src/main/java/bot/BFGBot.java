package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * @author  Human (https://t.me/kkfes)
 * @version 1.0
 */


import java.sql.Connection;

public class BFGBot extends TelegramLongPollingBot {


    private static final String BOT_TOKEN = "";//Токен Бота
    private static final String BOT_NAME = "";//Юсер бот (без @)

    static {
        Connection c = DBManager.c;
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage()){
            if(update.getMessage().hasText()){
                if((System.currentTimeMillis()/1000)-update.getMessage().getDate()<=30){
                    new Users(update.getMessage().getFrom().getId(),update.getMessage().getFrom().getUserName(),update.getMessage().getFrom().getFirstName(),update.getMessage().getDate(),update.getMessage().getDate()).add();
                    Users.userCommands(update);
                    Business.businessCommands(update);
                }
            }
        }
    }

    public static Message sendMessage(long chat_id, String text, boolean web){
        if(text!=null){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(text);
            sendMessage.setParseMode("HTML");
            sendMessage.setChatId(String.valueOf(chat_id));
            sendMessage.setDisableWebPagePreview(web);
            try {
                return new BFGBot().execute(sendMessage);
            } catch (TelegramApiException e) {
                return sendMessage(chat_id,"⚠️ Ошибка Telegram:\n"+e,false);
            }
        }
        return null;
    }
}
