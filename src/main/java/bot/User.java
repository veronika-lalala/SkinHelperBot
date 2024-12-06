package bot;

public class User {
    long chatId;
    State state;
    String userName;

    public User(long chatId, State state, String name) {
        this.chatId = chatId;
        this.state = state;
        this.userName = name;
    }

    public State getState() {
        return state;
    }

    public void updateState(State state) {
        this.state = state;
    }

    public String getUserName() {
        return userName;
    }

    public long getChatId() {
        return chatId;
    }

}


