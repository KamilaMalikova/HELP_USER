package com.kamilamalikova.help.model;

public class EatingPlace {
    private int id;

    private boolean reserved;

    private String waiterUsername;

    private String waiterName;

    private boolean active;

    public EatingPlace(int id, boolean reserved, String waiterUsername, String waiterName, boolean active) {
        this.id = id;
        this.reserved = reserved;
        this.waiterUsername = waiterUsername;
        this.waiterName = waiterName;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public String getWaiterUsername() {
        return waiterUsername;
    }

    public void setWaiterUsername(String waiterUsername) {
        this.waiterUsername = waiterUsername;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
