package bot;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BeautyBot implements LongPollingSingleThreadUpdateConsumer {
    private static final Logger log = LoggerFactory.getLogger(BeautyBot.class);
    private final TelegramClient telegramClient;
    private final Logic logic;
    private User user;
    private final WorkWithSQL componentBase = new WorkWithSQL("jdbc:mysql://localhost:3306/bdinfcomp", "root", "qwer", "components");

    //"jdbc:mysql://localhost:3306/bdinfcomp", "root", "qwer", "components"
    public BeautyBot(String botToken) throws SQLException {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.logic = new Logic();
        this.user = null;
        //componentBase.addUser("users",ch,"jk");
//        for (State value : State.values()) {
//            if (value.name() == user.state) {
//                this.state = value;
//            }
//        }
    }

    public WorkWithSQL getComponentBase() {
        return componentBase;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }


    public void setButtons(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder().keyboardRow(new KeyboardRow(new String[]{"Помощь"})).keyboardRow(new KeyboardRow(new String[]{"Вернуться в начало"})).build());
    }

    @Override
    public void consume(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();
            String userName = update.getMessage().getChat().getFirstName();
            if(user==null){
                //long chat_id = update.getMessage().getChatId();
                //String userName = update.getMessage().getChat().getFirstName();
                try {
                    logic.processState(chat_id, this, userName);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println(user.getChatId());
            String message_text = update.getMessage().getText();
            Message mes = new Message(message_text, null);

            logic.processMessage(chat_id, mes, this, userName);//передали логике сообщение которое получили должна сделать всё и отправить сообщение там будут все ифы и проверки для сообщения
        } else if (update.hasCallbackQuery()) {
            long chat_id_callback = update.getCallbackQuery().getMessage().getChatId();
            if(this.user==null){
                try {
                    logic.processState(chat_id_callback, this, update.getCallbackQuery().getMessage().getChat().getFirstName());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            String calldata = update.getCallbackQuery().getData();
            try {
                logic.processCallback(chat_id_callback, calldata, this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public void send(long chatId, Message message) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(message.getText())
                .chatId(chatId)
                .build();
        if (message.getButtons() != null) {
            if(message.getButtons().getFirst().getCallback()!=null) {
                messageButtons(sendMessage, message.getButtons());
            }
            else{
                keyboardButtons(sendMessage,message.getButtons());
            }
        }
        try {
            this.telegramClient.execute(sendMessage);
        } catch (TelegramApiException var3) {
            TelegramApiException e = var3;
            throw new RuntimeException(e);
        }


    }

    private void keyboardButtons(SendMessage sendMessage,List<Button> buttons){
        //sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder().keyboardRow(new KeyboardRow(new String[]{"Помощь"})).keyboardRow(new KeyboardRow(new String[]{"Вернуться в начало"})).build());
//         List<KeyboardRow> rows= new ArrayList<KeyboardRow>();
//
//        for (Button button:buttons){
//            rows.add(new KeyboardRow(button.getName()));
//        }
//        sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder().keyboardRow(rows).build());
        List<KeyboardRow> rows = new ArrayList<>();


        for (Button button : buttons) {
            KeyboardRow row = new KeyboardRow(); // Создаем новую строку для каждой кнопки
            row.add(button.getName()); // Добавляем имя кнопки в строку
            rows.add(row); // Добавляем строку в список строк
        }

        // Устанавливаем разметку клавиатуры
        sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder().keyboard(rows).build());
    }

    public void messageButtons(SendMessage sendMessage, List<Button> buttons) {
        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow currentRow = new InlineKeyboardRow();

        int maxButtonLength = 20;
        for (Button button : buttons) {
            InlineKeyboardButton butt = InlineKeyboardButton.builder()
                    .text(button.getName())
                    .callbackData(button.getCallback())
                    .build();
            if (button.getName().length() > maxButtonLength) {
                if (!currentRow.isEmpty()) {
                    rows.add(currentRow);
                    currentRow = new InlineKeyboardRow();
                    rows.add(currentRow);
                    currentRow = new InlineKeyboardRow();
                }
                InlineKeyboardRow longButtonRow = new InlineKeyboardRow();
                longButtonRow.add(butt);
                rows.add(longButtonRow);
            } else {
                currentRow.add(butt);
                if (currentRow.size() == 2) {
                    rows.add(currentRow);
                    currentRow = new InlineKeyboardRow();
                }
            }
        }
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }
        InlineKeyboardMarkup replyMarkup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();
        sendMessage.setReplyMarkup(replyMarkup);
    }

}


