package sample.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by algys on 19.02.17.
 */
public class Validator {
    public static Boolean email(String email){
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        Pattern p = Pattern.compile(ePattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static Boolean login(String login){
        if(login.length()<6 | login.length()>12){
            return false;
        }
        return true;
    }

    public static Boolean password(String password){
        if(password.length()<6 | password.length()>12){
            return false;
        }
        return true;
    }
}
