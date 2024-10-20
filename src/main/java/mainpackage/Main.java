package mainpackage;

import bot.BeautyBot;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import config.ConfigLoader;

public class Main {
    public static void main(String[] args) throws TelegramApiException {
        String botToken = new ConfigLoader().getProperties().getProperty("bot_token");
        System.out.println(botToken);
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();

            try {
                botsApplication.registerBot(botToken, new BeautyBot(botToken));
                System.out.println("MyAmazingBot successfully started!");
                Thread.currentThread().join();
            } catch (Throwable var6) {
                try {
                    botsApplication.close();
                } catch (Throwable var5) {
                    var6.addSuppressed(var5);
                }

                throw var6;
            }

            botsApplication.close();
        } catch (Exception var7) {
            Exception e = var7;
            e.printStackTrace();
        }

    }
}
