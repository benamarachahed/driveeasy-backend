package com.driveeasy.clientservice.model;

public class Eligibilite {

    private boolean eligible;
    private String raison;

    public Eligibilite() {
    }

    public Eligibilite(boolean eligible, String raison) {
        this.eligible = eligible;
        this.raison = raison;
    }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
    public String getRaison() { return raison; }
    public void setRaison(String raison) { this.raison = raison; }
}