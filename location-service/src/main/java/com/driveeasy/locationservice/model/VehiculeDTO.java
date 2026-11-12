package com.driveeasy.locationservice.model;

import java.math.BigDecimal;

public class VehiculeDTO {
    private Long id;
    private String immatriculation;
    private BigDecimal prixJournalier;
    private String statut;
    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getImmatriculation() { return immatriculation; }
    public void setImmatriculation(String immatriculation) { this.immatriculation = immatriculation; }
    public BigDecimal getPrixJournalier() { return prixJournalier; }
    public void setPrixJournalier(BigDecimal prixJournalier) { this.prixJournalier = prixJournalier; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
}