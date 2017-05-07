package edu.cornell.engineering.ewh.hippoandroid;

/**
 *  User class stores information about a user.
 */

public class User{

    private int userId;
    private String email, lastName, firstName;
    private Object calls;

    public User(int userId, String email, String lastName, String firstName, Object calls){
        this.userId = userId;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.calls = calls;
    }

    public int getUserId(){
        return userId;
    }
    public String getEmail(){
        return email;
    }
    public String getLastName(){
        return lastName;
    }
    public String getFirstName(){
        return firstName;
    }
    public Object getCalls(){
        return calls;
    }

    public String toString(){
        return firstName+" "+lastName;
    }

}
