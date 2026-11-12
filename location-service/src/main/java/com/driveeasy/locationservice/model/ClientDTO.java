package com.driveeasy.locationservice.model;

public class ClientDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String categorieClient;
    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getCategorieClient() { return categorieClient; }
    public void setCategorieClient(String categorieClient) { this.categorieClient = categorieClient; }
}