package sky.blue.gameonline.utils;

/**
 * Created by Yami on 1/21/2018.
 */

public class FormatString {
    public static String toMessageBox(String message){
        String[] words=message.split(" ");
        StringBuilder builder=new StringBuilder();
        for (int i=0; i<words.length; i++){
            if (i%3==0){
                words[i]=words[i]+"@";
            }
            builder.append(words[i]).append(" ");
        }
        return builder.toString().trim();
    }
}
