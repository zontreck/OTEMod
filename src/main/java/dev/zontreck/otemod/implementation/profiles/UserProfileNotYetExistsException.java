package dev.zontreck.otemod.implementation.profiles;

public class UserProfileNotYetExistsException extends Exception{
    String playerID;
    public UserProfileNotYetExistsException(String id){
        super("A user profile does not yet exist");
        playerID=id;
    }
}
