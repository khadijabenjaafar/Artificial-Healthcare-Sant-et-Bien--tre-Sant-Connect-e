
package org.example.entities;

public class UserConnecter {
    // Private static instance of the class
    private static UserConnecter instance;

    // Private variable to store the logged-in user
    private Utilisateur userConnecter;

    // Private constructor to prevent instantiation from outside
    public UserConnecter() {}

    // Public method to provide access to the instance
    public static UserConnecter getInstance() {
        if (instance == null) {
            instance = new UserConnecter();
        }
        return instance;
    }

    // Method to set the logged-in user
    public void setUserConnecter(Utilisateur user) {
        this.userConnecter = user;
    }

    // Method to get the logged-in user
    public Utilisateur getUserConnecter() {
        return userConnecter;
    }

    // Method to clear the logged-in user (e.g., on logout)
    public void clearUserConnecter() {
        this.userConnecter = null;
    }
}
