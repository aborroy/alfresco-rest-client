package org.alfresco.rest.client.rest.bean;

public class Entry {

    private String firstName;
    private String lastName;
    Capabilities CapabilitiesObject;
    private String displayName;
    private boolean emailNotificationsEnabled;
    private String id;
    private boolean enabled;
    private String email;
    private Company company;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Capabilities getCapabilities() {
        return CapabilitiesObject;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean getEmailNotificationsEnabled() {
        return emailNotificationsEnabled;
    }

    public String getId() {
        return id;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCapabilities(Capabilities capabilitiesObject) {
        this.CapabilitiesObject = capabilitiesObject;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmailNotificationsEnabled(boolean emailNotificationsEnabled) {
        this.emailNotificationsEnabled = emailNotificationsEnabled;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}