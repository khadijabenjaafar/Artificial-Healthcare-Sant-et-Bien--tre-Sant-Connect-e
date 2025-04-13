package org.example.entities;

public enum Status {
     ACTIVE, BANNED;
    @Override
    public String toString() {
            return this.name();
        }
}
