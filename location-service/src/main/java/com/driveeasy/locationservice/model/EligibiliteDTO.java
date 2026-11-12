package com.driveeasy.locationservice.model;

public class EligibiliteDTO {
    private boolean eligible;
    private String raison;
    // getters/setters
    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
    public String getRaison() { return raison; }
    public void setRaison(String raison) { this.raison = raison; }
}