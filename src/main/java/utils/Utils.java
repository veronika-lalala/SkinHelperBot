package utils;

public class Utils {
    public static String[] messageParser(String message){
        String cleanedInput = message.replaceAll("[^a-zA-Zа-яА-ЯёЁ0-9\\s-]+", ",");
        String[] words = cleanedInput.split(",\\s*");
        for (String word : words) {
            word.toLowerCase();
        }
        return words;
    }
}
