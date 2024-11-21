package bot;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.awt.SystemColor.text;

public class Logic {
    void processMessage(long chatId, Message message, BeautyBot bot, String userName) {
        String newText = "";
        List<Button> buttons = new ArrayList<>();
        if (bot.getState() == State.INGREDIENT) {
            try {
                String text = bot.getComponentBase().getInfFromComponent(message.getText());
                if (Objects.equals(text, "")) {
                    text = "нет такого компонента:(";
                }
                newText = text;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            bot.setState(State.DEFAULT);
        }
        if (bot.getState() == State.ALL) {
            try {
                String textFromMassage = message.getText();
                String[] componentsList = Shape.messageParser(textFromMassage);
                StringBuilder text = new StringBuilder();
                for (String component : componentsList) {
                    String detailInf = bot.getComponentBase().getDetailInfFromComponent(component);
                    if (detailInf.isEmpty()) {
                        continue;
                    }
                    else{
                        text.append(component);
                        text.append(":\n");
                        text.append(detailInf);
                        text.append('\n');
                    }
                }
                if (text.isEmpty()) {
                    text = new StringBuilder("ни одного компонента из состава не нашлось:(");
                }
                newText = text.toString();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            bot.setState(State.DEFAULT);
        }
        switch (message.getText()) {
            case "/start":
                newText = "Привет," + userName + " меня зовут SkinHelper! Я помогу тебе разобраться в уходе за кожей лица.\nСо мной ты поймешь какие компоненты в косметике подойдут именно тебе!";
                Button newButton = new Button("Что я умею?", "I_can");
                buttons.add(newButton);
                break;
            case "Помощь":
                newText = "Сейчас я расскажу как правильно вводить данные. Про что хотите узнать, как использовать?";
                Button newButtonHelp1 = new Button("Узнать про работу с анализом состава", "help struct");
                Button newButtonHelp2 = new Button("Узнать про работу с одним компонентом", "help comp");
                buttons.add(newButtonHelp1);
                buttons.add(newButtonHelp2);
                break;
            case "Вернуться ":
        }
        Message answer = new Message(newText, buttons);
        bot.send(chatId, answer);
    }

    void processCallback(long chatId, String callback, BeautyBot bot) {
        String newText = "";
        List<Button> buttons = new ArrayList<>();
        switch (callback) {
            case "I_can":
                newText = "1. Я смогу помочь тебе выбрать подходящее уходовое средство, проанализировав состав продукта. Также я расскажу всю необходимую информацию об активных компонентах, и ,кроме того, предупрежу об опасностях для твоей кожи, скрывающихся в составе.\n2. Еще я смогу ответить на твои вопросы об отдельных активных веществах: объясню для чего они применяются, как они влияют на твою кожу, в чем принцип их работы.\nКак работает анализ состава?\nДля того, чтобы познакомится с составом поближе тебе необходимо:\n• Найти состав продукта в текстовом варианте (обязательно на английском!), в этом тебе может помочь любой онлайн магазин, где твое средство есть в наличии. В описании товара и должен быть состав.\n• Отправить состав мне в сообщении.\n• Любоваться результатом!\nКак узнать подробную информацию о компоненте?\nДля того, чтобы подробнее узнать о компоненте тебе необходимо:\n• Отправить мне сообщение, где будет название компонента (обязательно на английском!)\n• Любоваться результатом!\nЧем займемся?";
                Button newButton1 = new Button("Анализ состава", "Structure");
                Button newButton2 = new Button("Информация о компоненте", "Component");
                buttons.add(newButton1);
                buttons.add(newButton2);
                break;
            case "Structure":
                bot.setState(State.ALL);
                newText = "Введите состав вашего продукта!";
                break;
            case "Component":
                bot.setState(State.INGREDIENT);
                newText = "Введите название актива";
        }
        Message answ = new Message(newText, buttons);
        bot.send(chatId, answ);
    }


}

