package org.example.entities;

public enum Status {
     ACTIVE, INACTIVE, BANNED;
    @Override
    public String toString() {
            return this.name();
        }
}
