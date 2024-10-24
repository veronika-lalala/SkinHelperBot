package bot;


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
import java.util.Objects;



public class BeautyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private int flag;
    private workWithSQL informationAboutComponent= new workWithSQL("jdbc:mysql://localhost:3306/mydbtest", "root","goddeskarina291005","users");

    public BeautyBot(String botToken) {

        this.telegramClient = new OkHttpTelegramClient(botToken);
        this.flag=0;
    }

    public void setButtons(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder().keyboardRow(new KeyboardRow(new String[]{"Помощь"})).keyboardRow(new KeyboardRow(new String[]{"Вернуться в начало"})).build());
    }

    public void consume(Update update) {
        String call_data;
        long chat_id;
        SendMessage struct_message;
        SendMessage comp_message;
        if (update.hasMessage() && update.getMessage().hasText()) {
            call_data = update.getMessage().getText();
            chat_id = update.getMessage().getChatId();
            if(flag==1) {
                try {
                    String text = informationAboutComponent.ReturnInfFromComponent(call_data);
                    if (Objects.equals(text, "")){
                        text="нет такого компонента:(";
                    }
                    SendMessage newSay=this.newMessage(text,chat_id);
                    send(newSay);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                flag = 0;
            }
            switch (call_data) {
                case "/start":
                    String userName = update.getMessage().getChat().getFirstName();
                    struct_message = this.newMessage("Привет," + userName + " меня зовут SkinHelper! Я помогу тебе разобраться в уходе за кожей лица.\nСо мной ты поймешь какие компоненты в косметике подойдут именно тебе!", chat_id);
                    struct_message.setReplyMarkup(InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(new InlineKeyboardButton[]{InlineKeyboardButton.builder().text("Что я умею").callbackData("I_can").build()})).build());
                    this.send(struct_message);
                    break;
                case "Помощь":
                    comp_message = this.newMessage("Сейчас я расскажу как правильно вводить данные. Про что хотите узнать, как использовать?", chat_id);
                    comp_message.setReplyMarkup(InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(new InlineKeyboardButton[]{InlineKeyboardButton.builder().text("Узнать про работу с анализом состава").callbackData("help struct").build()})).keyboardRow(new InlineKeyboardRow(new InlineKeyboardButton[]{InlineKeyboardButton.builder().text("Узнать про работу с одним компонентом").callbackData("help comp").build()})).build());
                    this.send(comp_message);
                case "Вернуться ":
            }
        } else if (update.hasCallbackQuery()) {
            call_data = update.getCallbackQuery().getData();
            chat_id = update.getCallbackQuery().getMessage().getChatId();
            switch (call_data) {
                case "I_can":
                    SendMessage message = this.newMessage("1. Я смогу помочь тебе выбрать подходящее уходовое средство, проанализировав состав продукта. Также я расскажу всю необходимую информацию об активных компонентах, и ,кроме того, предупрежу об опасностях для твоей кожи, скрывающихся в составе.\n2. Еще я смогу ответить на твои вопросы об отдельных активных веществах: объясню для чего они применяются, как они влияют на твою кожу, в чем принцип их работы.\nКак работает анализ состава?\nДля того, чтобы познакомится с составом поближе тебе необходимо:\n• Найти состав продукта в текстовом варианте (обязательно на английском!), в этом тебе может помочь любой онлайн магазин, где твое средство есть в наличии. В описании товара и должен быть состав.\n• Отправить состав мне в сообщении.\n• Любоваться результатом!\nКак узнать подробную информацию о компоненте?\nДля того, чтобы подробнее узнать о компоненте тебе необходимо:\n• Отправить мне сообщение, где будет название компонента (обязательно на английском!)\n• Любоваться результатом!\nЧем займемся?", chat_id);
                    message.setReplyMarkup(InlineKeyboardMarkup.builder().keyboardRow(new InlineKeyboardRow(new InlineKeyboardButton[]{InlineKeyboardButton.builder().text("Анализ состава").callbackData("structure").build()})).keyboardRow(new InlineKeyboardRow(new InlineKeyboardButton[]{InlineKeyboardButton.builder().text("Информация о компоненте").callbackData("Component").build()})).build());
                    this.send(message);
                    break;
                case "structure":
                    flag=2;
                    struct_message = this.newMessage("Введите состав вашего продукта!", chat_id);
                    this.send(struct_message);
                    break;
                case "Component":
                    flag=1;
                    comp_message = this.newMessage("Введите название актива", chat_id);
                    this.send(comp_message);
            }
        }

    }

    private SendMessage newMessage(String new_text, long id) {
        SendMessage newMessage = SendMessage.builder().chatId(id).text(new_text).build();
        return newMessage;
    }

    private void send(SendMessage message) {
        try {
            this.telegramClient.execute(message);
        } catch (TelegramApiException var3) {
            TelegramApiException e = var3;
            throw new RuntimeException(e);
        }
    }
}
