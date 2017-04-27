package edu.cornell.engineering.ewh.hippoandroid;

/**
 * Created by Albert on 4/27/2017.
 */

public class User{

    private String userId, email, lastName, firstName;
    private Object calls;

    public User(String userId, String email, String lastName, String firstName, Object calls){
        this.userId = userId;
        this.email = email;
        this.lastName = lastName;
        this.firstName = firstName;
        this.calls = calls;
    }

    public String getUserId(){
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
        return userId+", "+email+", "+firstName+", "+lastName+", "+calls;
    }

}
