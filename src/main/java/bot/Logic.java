package bot;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Logic {
    private Message allComponentsMessage;
    private List<Button> buttonsComponents = new ArrayList<>();
    //TODO изменить работу с пользователем чтобы всё было через класс, поменять processState чтобы вызывалась только когда user null
    void processMessage(long chatId, Message message, BeautyBot bot, String userName) {
        String newText = "";
        List<Button> buttons = new ArrayList<>();
        switch (message.getText()) {
            case "/start":
                newText = "Привет," +userName + " меня зовут SkinHelper! Я помогу тебе разобраться в уходе за кожей лица.\nСо мной ты поймешь какие компоненты в косметике подойдут именно тебе!";
                Button newButton = new Button("Что я умею?", "I_can");
                buttons.add(newButton);
                if(bot.getUser().getState()!=State.DEFAULT) {
                    bot.getUser().updateState(State.DEFAULT);
                    updateState(bot.getUser(), bot);
                }
                break;
            case "Инструкция":
                newText = "Как работает анализ состава?\nДля того, чтобы познакомится с составом поближе тебе необходимо:\n• Найти состав продукта в текстовом варианте (обязательно на английском!), в этом тебе может помочь любой онлайн магазин, где твое средство есть в наличии. В описании товара и должен быть состав.\n• Отправить состав мне в сообщении.\n• Любоваться результатом!\nКак узнать подробную информацию о компоненте?\nДля того, чтобы подробнее узнать о компоненте тебе необходимо:\n• Отправить мне сообщение, где будет название компонента (обязательно на английском!)\n• Любоваться результатом!";
                Message answ = new Message(newText, null);
                bot.send(chatId, answ);
                Message answ2=new Message("А теперь введите необходимую информацию!",null);
                bot.send(chatId,answ2);
                return;
            case "Вернуться в начало":
                bot.getUser().updateState(State.DEFAULT);
                updateState(bot.getUser(), bot);
                newText="1. Я смогу помочь тебе выбрать подходящее уходовое средство, проанализировав состав продукта. Также я расскажу всю необходимую информацию об активных компонентах, и ,кроме того, предупрежу об опасностях для твоей кожи, скрывающихся в составе.\n2. Еще я смогу ответить на твои вопросы об отдельных активных веществах: объясню для чего они применяются, как они влияют на твою кожу, в чем принцип их работы.";
                Button newButton1 = new Button("Анализ состава", "Structure");
                Button newButton2 = new Button("Информация о компоненте", "Component");
                buttons.add(newButton1);
                buttons.add(newButton2);
                Message newMes=new Message(newText,buttons);
                bot.send(chatId,newMes);

                return;

        }
        if (bot.getUser().getState() == State.INGREDIENT) {
            try {
                String text = bot.getComponentBase().getInfFromComponent(message.getText(), "components");
                if (Objects.equals(text, "")) {
                    text = "нет такого компонента:(";
                }
                newText = text;
                newText += "\nХотите ли узнать информацию о другом компоненте?";
                Button yComponent = new Button("Да", "Component");
                Button nComponent = new Button("Нет", "No");
                buttons.add(yComponent);
                buttons.add(nComponent);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            bot.getUser().updateState(State.DEFAULT);
            updateState(bot.getUser(), bot);
        }
        if (bot.getUser().getState() == State.ALL) {
            try {
                String textFromMassage = message.getText();
                String[] componentsList = Shape.messageParser(textFromMassage);
                StringBuilder text = new StringBuilder();
                for (String component : componentsList) {
                    String detailInf = bot.getComponentBase().getInfFromComponent(component,"components");
                    if (detailInf.isEmpty()) {
                        continue;
                    } else {
                        Button componentButton = new Button(component, "Detailed:" + component);
                        buttonsComponents.add(componentButton);
                        text.append(component);
                        text.append(":\n");
                        text.append(detailInf);
                        text.append('\n');

                    }
                }
                text.append("Хотите ли узнать подробнее о каком-то из компонентов?");
                if (text.isEmpty()) {
                    text = new StringBuilder("ни одного компонента из состава не нашлось:(");
                }
                newText = text.toString();
                Button detailQuestion1 = new Button("Да", "YesDetail");
                Button detailQuestion2 = new Button("Нет", "No");
                buttons.add(detailQuestion1);
                buttons.add(detailQuestion2);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }

        Message answer = new Message(newText, buttons);
        bot.send(chatId, answer);
    }

    void processCallback(long chatId, String callback, BeautyBot bot) throws SQLException {
        String newText = "";
        List<Button> buttons = new ArrayList<>();
        String component = "";
        if (callback.split(":")[0].equals("Detailed")) {
            component = callback.split(":")[1];
            callback = "Detailed";
        }
        switch (callback) {
            case "I_can":
                newText = "1. Я смогу помочь тебе выбрать подходящее уходовое средство, проанализировав состав продукта. Также я расскажу всю необходимую информацию об активных компонентах, и ,кроме того, предупрежу об опасностях для твоей кожи, скрывающихся в составе.\n2. Еще я смогу ответить на твои вопросы об отдельных активных веществах: объясню для чего они применяются, как они влияют на твою кожу, в чем принцип их работы.";
                Button newButton1 = new Button("Анализ состава", "Structure");
                Button newButton2 = new Button("Информация о компоненте", "Component");
                buttons.add(newButton1);
                buttons.add(newButton2);
                break;
            case "Structure":
                bot.getUser().updateState(State.ALL);
                updateState(bot.getUser(), bot);
                newText = "Введите состав вашего продукта!";
                Button ButReply3=new Button("Инструкция", null);
                Button ButReply4 = new Button("Вернуться в начало", null);
                buttons.add(ButReply3);
                buttons.add(ButReply4);
                break;
            case "Component":
                bot.getUser().updateState(State.INGREDIENT);
                updateState(bot.getUser(), bot);
                newText = "Введите название актива";
                Button ButReply1=new Button("Инструкция", null);
                Button ButReply2 = new Button("Вернуться в начало", null);
                buttons.add(ButReply1);
                buttons.add(ButReply2);
                break;
            case "Detailed":
                newText = bot.getComponentBase().getDetailInfFromComponent(component,"components");
                newText += "\nХотите ли узнать подробнее о другом компоненте?";
                Button newButtonYes = new Button("Да", "Yes");
                Button newButtonNo = new Button("Нет", "No");
                buttons.add(newButtonYes);
                buttons.add(newButtonNo);
                break;
            case "Yes":
                if (buttonsComponents.isEmpty()){
                    newText="Для начла введите интересующий вас состав";
                    buttons.add(new Button("Анализ состава", "Structure"));
                }else {
                    newText = "Выберите еще компонент\n";
                    buttons = buttonsComponents;
                }
                break;
            case "YesDetail":
                if (buttonsComponents.isEmpty()){
                    newText="Для начла введите интересующий вас состав";
                    buttons.add(new Button("Анализ состава", "Structure"));
                }else {
                    newText = "Выберите компонент о котором хотите узнать подробнее";
                    buttons = buttonsComponents;

                }
                break;
            case "No":
                buttonsComponents.clear();
                newText = "Чем займемся дальше?";
                Button newButton11 = new Button("Анализ состава", "Structure");
                Button newButton22 = new Button("Информация о компоненте", "Component");
                buttons.add(newButton11);
                buttons.add(newButton22);
                break;
        }
        Message answ;
        if (buttons.isEmpty()){
            answ = new Message(newText, null);
        }
        else{
            answ = new Message(newText, buttons);
        }
        bot.send(chatId, answ);
    }


    public void processState(long chatId, BeautyBot bot, String userName) throws SQLException {//вызовется один раз когда пользователь только подключился
        String currentState = bot.getComponentBase().getState(chatId, "users");
        if (currentState.isEmpty()) {
            System.out.println("new user");
            bot.getComponentBase().addUser("users", chatId, State.DEFAULT);
            currentState = "DEFAULT";
        }
        User newUser = new User(chatId, State.valueOf(currentState), userName);
        bot.setUser(newUser);
    }

    public void updateState(User user, BeautyBot bot) {
        try {
            bot.getComponentBase().updateState("users", user.getChatId(), user.getState());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

