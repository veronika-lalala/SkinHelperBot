package bot;

public class Button {
    String name;
    String callback;
    String component;

    public Button(String name, String callback) {
        this.name=name;
        this.callback=callback;
    }
    public Button(String name, String callback, String component) {
        this.name=name;
        this.callback=callback;
        this.component=component;
    }


    public String getName() {
        return name;
    }

    public String getCallback() {
        return callback;
    }
}