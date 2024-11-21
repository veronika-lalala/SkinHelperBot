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
    private State state;
    private final WorkWithSQL componentBase = new WorkWithSQL("jdbc:mysql://localhost:3306/mydbtest", "root", "goddeskarina291005", "users");

    public BeautyBot(String botToken) throws SQLException {
        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.logic = new Logic();
        this.state = State.DEFAULT;
//        for (State value : State.values()) {
//            if (value.name() == user.state) {
//                this.state = value;
//            }
//        }
        System.out.println(state.name());
    }

    public WorkWithSQL getComponentBase() {
        return componentBase;
    }

    public void setState(State state) {

        this.state = state;
    }

    public State getState() {

        return state;
    }

    public void setButtons(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder().keyboardRow(new KeyboardRow(new String[]{"Помощь"})).keyboardRow(new KeyboardRow(new String[]{"Вернуться в начало"})).build());
    }

    @Override
    public void consume(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();
            String message_text = update.getMessage().getText();
            Message mes = new Message(message_text, null);
            String userName = update.getMessage().getChat().getFirstName();
            logic.processMessage(chat_id, mes, this, userName);//передали логике сообщение которое получили должна сделать всё и отправить сообщение там будут все ифы и проверки для сообщения
        } else if (update.hasCallbackQuery()) {
            long chat_id_callback = update.getCallbackQuery().getMessage().getChatId();
            String calldata = update.getCallbackQuery().getData();
            logic.processCallback(chat_id_callback, calldata, this);
        }
    }

    //todo впихнуть создание меню, лучше вызывать когда просим ввести компонент и нет других кнопок
    private SendMessage newMessage(String new_text, long id) {
        SendMessage newMessage = SendMessage.builder().chatId(id).text(new_text).build();
        return newMessage;
    }

    public void send(long chatId, Message message) {
        SendMessage sendMessage = SendMessage
                .builder()
                .text(message.getText())
                .chatId(chatId)
                .build();
        if (message.getButtons() != null) {
            messageButtons(sendMessage, message.getButtons());
        }
        try {
            this.telegramClient.execute(sendMessage);
        } catch (TelegramApiException var3) {
            TelegramApiException e = var3;
            throw new RuntimeException(e);
        }


    }

    public void messageButtons(SendMessage sendMessage, List<Button> buttons) {
        // Создаем список строк для кнопок
        List<InlineKeyboardRow> rows = new ArrayList<>();

        // Временный список для хранения кнопок в текущей строке
        InlineKeyboardRow currentRow = new InlineKeyboardRow();

        // Максимальная длина текста кнопки
        int maxButtonLength = 20; // Задайте необходимое значение в зависимости от вашего интерфейса

        for (Button button : buttons) {

            // Создаем кнопку
            InlineKeyboardButton butt = InlineKeyboardButton.builder()
                    .text(button.getName())
                    .callbackData(button.getCallback())
                    .build();

            // Проверяем длину текста кнопки
            if (button.getName().length() > maxButtonLength) {
                // Если текст слишком длинный, добавляем кнопку в новую строку
                if (!currentRow.isEmpty()) {
                    rows.add(currentRow); // Добавляем текущую строку, если она не пустая
                    currentRow = new InlineKeyboardRow(); // Создаем новую строку
                }
                // Добавляем длинную кнопку в отдельную строку
                InlineKeyboardRow longButtonRow = new InlineKeyboardRow();
                longButtonRow.add(butt);
                rows.add(longButtonRow);
            } else {
                // Добавляем кнопку в текущую строку
                currentRow.add(butt);

                // Если в текущей строке уже 2 кнопки, добавляем строку в список и создаем новую
                if (currentRow.size() == 2) {
                    rows.add(currentRow);
                    currentRow = new InlineKeyboardRow(); // Создаем новую строку
                }
            }
        }

        // Если осталась одна кнопка в текущей строке, добавляем ее в список
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        // Создаем разметку клавиатуры
        InlineKeyboardMarkup replyMarkup = InlineKeyboardMarkup.builder()
                .keyboard(rows)
                .build();

        sendMessage.setReplyMarkup(replyMarkup);
    }
}


