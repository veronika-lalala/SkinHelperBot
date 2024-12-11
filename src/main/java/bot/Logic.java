package bot;

import userstate.State;
import userstate.User;
import utils.Utils;

import java.sql.SQLException;
import java.util.*;


public class Logic {

    public final static String START = "/start";
    public final static String INSTRUCTION = "Инструкция";
    public final static String RETURN_TO_START = "Вернуться в начало";
    public final static String COMPONENT = "Component";
    public final static String I_CAN = "I_can";
    public final static String STRUCTURE = "Structure";
    public final static String DETAILED = "Detailed";
    public final static String YES = "Yes";
    public final static String NO = "No";
    public final static String YES_DETAIL = "YesDetail";
    private Map<Long, List<Button>> mapButtonsComponents = new HashMap<>();

    void processMessage(Message message, BeautyBot bot) {
        String newText = "";
        List<Button> buttons = new ArrayList<>();
        switch (message.getText()) {
            case START:
                newText = "Привет," + bot.getUser().getUserName() + " меня зовут SkinHelper! Я помогу тебе разобраться в уходе за кожей лица.\nСо мной ты поймешь какие компоненты в косметике подойдут именно тебе!";
                Button newButton = new Button("Что я умею?", I_CAN);
                buttons.add(newButton);
                if (bot.getUser().getState() != State.DEFAULT) {
                    bot.getUser().updateState(State.DEFAULT);
                    updateState(bot.getUser(), bot);
                }
                break;
            case INSTRUCTION:
                newText = "Как работает анализ состава?\nДля того, чтобы познакомится с составом поближе тебе необходимо:\n• Найти состав продукта в текстовом варианте (обязательно на английском!), в этом тебе может помочь любой онлайн магазин, где твое средство есть в наличии. В описании товара и должен быть состав.\n• Отправить состав мне в сообщении.\n• Любоваться результатом!\nКак узнать подробную информацию о компоненте?\nДля того, чтобы подробнее узнать о компоненте тебе необходимо:\n• Отправить мне сообщение, где будет название компонента (обязательно на английском!)\n• Любоваться результатом!";
                Message answer = new Message(newText, null);
                bot.send(answer);
                Message answer2 = new Message("А теперь введите необходимую информацию!", null);
                bot.send(answer2);
                return;
            case RETURN_TO_START:
                mapButtonsComponents.remove(bot.getUser().getChatId());
                bot.getUser().updateState(State.DEFAULT);
                updateState(bot.getUser(), bot);
                newText = "1. Я смогу помочь тебе выбрать подходящее уходовое средство, проанализировав состав продукта. Также я расскажу всю необходимую информацию об активных компонентах, и ,кроме того, предупрежу об опасностях для твоей кожи, скрывающихся в составе.\n2. Еще я смогу ответить на твои вопросы об отдельных активных веществах: объясню для чего они применяются, как они влияют на твою кожу, в чем принцип их работы.";
                Button newButton1 = new Button("Анализ состава", STRUCTURE);
                Button newButton2 = new Button("Информация о компоненте", COMPONENT);
                buttons.add(newButton1);
                buttons.add(newButton2);
                Message newMes = new Message(newText, buttons);
                bot.send(newMes);
                return;

        }
        if (bot.getUser().getState() == State.INGREDIENT) {
            try {
                String text = bot.getComponentBase().getInfFromComponent(message.getText(), "components");
                if (Objects.equals(text, "") | text == null) {
                    text = "Данный компонент пока отсутствует в моей базе, но я работаю над этим!";
                }

                newText = text;
                newText += "\nХотите ли узнать информацию о другом компоненте?";
                Button yComponent = new Button("Да", COMPONENT);
                Button nComponent = new Button("Нет", NO);
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
                List<Button> buttonsComponents = new ArrayList<>();
                Long chatId = bot.getUser().getChatId();
                mapButtonsComponents.put(chatId, buttonsComponents);
                String textFromMassage = message.getText();
                String[] componentsList = Utils.messageParser(textFromMassage);
                StringBuilder textGoodComponents = new StringBuilder();
                StringBuilder textComedogenic = new StringBuilder();
                for (String component : componentsList) {
                    String inf = bot.getComponentBase().getInfFromComponent(component, "components");
                    if (inf == null) {
                        component = component.toUpperCase();
                        textComedogenic.append("В СОСТАВЕ ЭТОГО ПРОДУКТА БЫЛИ НАЙДЕНЫ КОМЕДОГЕННЫЕ КОМПОНЕНТЫ!\nКомедогенные компоненты — это ингредиенты в косметике, которые могут способствовать образованию комедонов на коже, когда поры забиваются жиром, омертвевшими клетками и другими загрязнениями.\n");
                        textComedogenic.append('•').append(component).append("←ВОТ ЭТОТ ГАД");
                        textComedogenic.append("\n\n");
                    } else if (inf.isEmpty()) {
                        continue;
                    } else {
                        component = component.toUpperCase();
                        Button componentButton = new Button(component, "Detailed:" + component);
                        mapButtonsComponents.get(chatId).add(componentButton);
                        textGoodComponents.append('•').append(component);
                        textGoodComponents.append(":\n");
                        textGoodComponents.append(inf);
                        textGoodComponents.append("\n\n");

                    }
                }
                if (textGoodComponents.isEmpty()) {
                    textGoodComponents = new StringBuilder("Ни одного компонента из состава не нашлось в моей базе, но я работаю над этим!\n");
                    newText = textGoodComponents.toString();
                    Button proceed = new Button("Продолжить", NO);
                    buttons.add(proceed);
                } else {
                    textGoodComponents.append(textComedogenic);
                    textGoodComponents.append("Хотите ли узнать подробнее о каком-то из компонентов?");
                    newText = textGoodComponents.toString();
                    Button detailQuestion1 = new Button("Да", YES_DETAIL);
                    Button detailQuestion2 = new Button("Нет", NO);
                    buttons.add(detailQuestion1);
                    buttons.add(detailQuestion2);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }

        Message answer = new Message(newText, buttons);
        bot.send(answer);
    }

    void processCallback(String callback, BeautyBot bot) throws SQLException {
        String newText = "";
        List<Button> buttons = new ArrayList<>();
        String component = "";
        if (callback.split(":")[0].equals("Detailed")) {
            component = callback.split(":")[1];
            callback = DETAILED;
        }
        switch (callback) {
            case I_CAN:
                newText = "1. Я смогу помочь тебе выбрать подходящее уходовое средство, проанализировав состав продукта. Также я расскажу всю необходимую информацию об активных компонентах, и ,кроме того, предупрежу об опасностях для твоей кожи, скрывающихся в составе.\n2. Еще я смогу ответить на твои вопросы об отдельных активных веществах: объясню для чего они применяются, как они влияют на твою кожу, в чем принцип их работы.";
                Button newButton1 = new Button("Анализ состава", STRUCTURE);
                Button newButton2 = new Button("Информация о компоненте", COMPONENT);
                buttons.add(newButton1);
                buttons.add(newButton2);
                break;
            case STRUCTURE:
                bot.getUser().updateState(State.ALL);
                updateState(bot.getUser(), bot);
                newText = "Введите состав вашего продукта!";
                Button ButReply3 = new Button("Инструкция", null);
                Button ButReply4 = new Button("Вернуться в начало", null);
                buttons.add(ButReply3);
                buttons.add(ButReply4);
                break;
            case COMPONENT:
                bot.getUser().updateState(State.INGREDIENT);
                updateState(bot.getUser(), bot);
                newText = "Введите название актива";
                Button ButReply1 = new Button("Инструкция", null);
                Button ButReply2 = new Button("Вернуться в начало", null);
                buttons.add(ButReply1);
                buttons.add(ButReply2);
                break;
            case DETAILED:
                newText = bot.getComponentBase().getDetailInfFromComponent(component, "components");
                newText += "\nХотите ли узнать подробнее о другом компоненте?";
                Button newButtonYes = new Button("Да", YES);
                Button newButtonNo = new Button("Нет", NO);
                buttons.add(newButtonYes);
                buttons.add(newButtonNo);
                break;
            case YES:
                if (mapButtonsComponents.get(bot.getUser().getChatId()).isEmpty()) {
                    newText = "Для начала введите интересующий вас состав";
                    buttons.add(new Button("Анализ состава", STRUCTURE));
                } else {
                    newText = "Выберите еще компонент\n";
                    buttons = mapButtonsComponents.get(bot.getUser().getChatId());
                }
                break;
            case YES_DETAIL:
                if (mapButtonsComponents.get(bot.getUser().getChatId()).isEmpty()) {
                    newText = "Для начала введите интересующий вас состав";
                    buttons.add(new Button("Анализ состава", STRUCTURE));
                } else {
                    newText = "Выберите компонент о котором хотите узнать подробнее";
                    buttons = mapButtonsComponents.get(bot.getUser().getChatId());

                }
                break;
            case NO:
                mapButtonsComponents.remove(bot.getUser().getChatId());
                newText = "Чем займемся дальше?";
                Button newButton11 = new Button("Анализ состава", STRUCTURE);
                Button newButton22 = new Button("Информация о компоненте", COMPONENT);
                buttons.add(newButton11);
                buttons.add(newButton22);
                break;
        }
        Message answer;
        if (buttons.isEmpty()) {
            answer = new Message(newText, null);
        } else {
            answer = new Message(newText, buttons);
        }
        bot.send(answer);
    }


    public void processState(long chatId, BeautyBot bot, String userName) throws SQLException {//вызовется один раз когда пользователь только подключился
        String currentState = bot.getComponentBase().getState(chatId, "users");
        if (currentState.isEmpty()) {
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

