package bot;


import database.WorkWithSQL;
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
import userstate.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class BeautyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final Logic logic;
    private User user;
    private final WorkWithSQL componentBase = new WorkWithSQL("jdbc:mysql://localhost:3306/mydbtest", "root", "goddeskarina291005", "components");

    public BeautyBot(String botToken) throws SQLException {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.logic = new Logic();
        this.user = null;

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

    @Override
    public void consume(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            if (user == null) {
                long chat_id = update.getMessage().getChatId();
                String userName = update.getMessage().getChat().getFirstName();
                try {
                    logic.processState(chat_id, this, userName);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            String message_text = update.getMessage().getText();
            Message mes = new Message(message_text, null);

            logic.processMessage(mes, this);
        } else if (update.hasCallbackQuery()) {
            long chat_id_callback = update.getCallbackQuery().getMessage().getChatId();
            if (this.user == null) {
                try {
                    logic.processState(chat_id_callback, this, update.getCallbackQuery().getMessage().getChat().getFirstName());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            String calldata = update.getCallbackQuery().getData();
            try {
                logic.processCallback(calldata, this);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void send(Message message) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(message.getText())
                .chatId(getUser().getChatId())
                .build();
        if (message.getButtons() != null) {
            if (message.getButtons().getFirst().getCallback() != null) {
                messageButtons(sendMessage, message.getButtons());
            } else {
                keyboardButtons(sendMessage, message.getButtons());
            }
        }
        try {
            this.telegramClient.execute(sendMessage);
        } catch (TelegramApiException var3) {
            throw new RuntimeException(var3);
        }


    }

    private void keyboardButtons(SendMessage sendMessage, List<Button> buttons) {
        List<KeyboardRow> rows = new ArrayList<>();
        for (Button button : buttons) {
            KeyboardRow row = new KeyboardRow();
            row.add(button.getName());
            rows.add(row);
        }
        sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder().keyboard(rows).build());
    }

    private void messageButtons(SendMessage sendMessage, List<Button> buttons) {
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


