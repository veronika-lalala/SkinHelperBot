package bot;

public class Button {
    String name;
    String callback;

    public Button(String name, String callback) {
        this.name = name;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public String getCallback() {
        return callback;
    }
}