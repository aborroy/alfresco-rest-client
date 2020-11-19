package org.alfresco.rest.client.rest.bean;

public class Capabilities {

    private boolean isGuest;
    private boolean isAdmin;
    private boolean isMutable;

    public boolean getIsGuest() {
        return isGuest;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public boolean getIsMutable() {
        return isMutable;
    }

    public void setIsGuest(boolean isGuest) {
        this.isGuest = isGuest;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public void setIsMutable(boolean isMutable) {
        this.isMutable = isMutable;
    }
}
