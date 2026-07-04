package shared;

public class Session{
    private static String username;

    public static void setUsername(String username){
        Session.username = username;
    }
    public static String getUsername(){
        return username;
    }
    public static void clear(){
        username =null;
    }
    public static boolean isLoggedIn(){
        return username != null;
    }
}