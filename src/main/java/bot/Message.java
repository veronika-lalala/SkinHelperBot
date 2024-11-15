package bot;

import java.util.List;

public class Message {//record
    String text;
    List<Button> buttons = null;

    public Message(String text, List<Button> buttons) {
        this.text = text;
        this.buttons = buttons;
    }
    public String getText(){
        return text;
    }
    public List<Button> getButtons(){
        return buttons;
    }
}