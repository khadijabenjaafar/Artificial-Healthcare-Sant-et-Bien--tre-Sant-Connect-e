
package org.example.entities;

public enum EnumRole {
    ROLE_FREELANCER,
    ROLE_PATIENT,
    ROLE_MEDECIN,
    ROLE_PHARMACIEN,
    ROLE_ADMIN;

    @Override
    public String toString() {
        return this.name();
    }
}
