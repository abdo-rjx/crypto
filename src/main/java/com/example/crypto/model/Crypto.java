package com.example.crypto.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import java.io.Serializable;

@Entity
@Table(name = "CRYPTO")
@JsonFilter("cryptoFilter")
public class Crypto implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le symbole est obligatoire")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Le symbole doit être en majuscules sans espaces")
    @Column(nullable = false, unique = true, length = 10)
    private String symbole;

    @NotNull(message = "La quantité détenue ne peut pas être nulle")
    @PositiveOrZero(message = "La quantité détenue ne peut pas être négative")
    @Column(nullable = false)
    private Double quantiteDetenue;

    @Column(name = "cle_privee")
    private String clePrivee;

    public Crypto() {
    }

    public Crypto(Long id, String nom, String symbole, Double quantiteDetenue, String clePrivee) {
        this.id = id;
        this.nom = nom;
        this.symbole = symbole;
        this.quantiteDetenue = quantiteDetenue;
        this.clePrivee = clePrivee;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getSymbole() {
        return symbole;
    }

    public void setSymbole(String symbole) {
        this.symbole = symbole;
    }

    public Double getQuantiteDetenue() {
        return quantiteDetenue;
    }

    public void setQuantiteDetenue(Double quantiteDetenue) {
        this.quantiteDetenue = quantiteDetenue;
    }

    public String getClePrivee() {
        return clePrivee;
    }

    public void setClePrivee(String clePrivee) {
        this.clePrivee = clePrivee;
    }
}
