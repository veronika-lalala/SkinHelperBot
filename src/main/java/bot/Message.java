package bot;

import java.util.List;

public class Message {
    String text;
    List<Button> buttons;

    public Message(String text, List<Button> buttons) {
        this.text = text;
        this.buttons = buttons;
    }

    public String getText() {
        return text;
    }

    public List<Button> getButtons() {
        return buttons;
    }
}