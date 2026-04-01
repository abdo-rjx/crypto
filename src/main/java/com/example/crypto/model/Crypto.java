package com.example.crypto.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import io.swagger.v3.oas.annotations.media.Schema;
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

@Schema(description = "Représente une cryptomonnaie dans le portefeuille")
@Entity
@Table(name = "CRYPTO")
@JsonFilter("cryptoFilter")
public class Crypto implements Serializable {

    @Schema(description = "Identifiant unique généré automatiquement", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(description = "Nom complet de la cryptomonnaie", example = "Bitcoin")
    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @Schema(description = "Symbole unique en majuscules (ex: BTC, ETH)", example = "BTC")
    @NotBlank(message = "Le symbole est obligatoire")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Le symbole doit être en majuscules sans espaces")
    @Column(nullable = false, unique = true, length = 10)
    private String symbole;

    @Schema(description = "Quantité de la crypto détenue (doit être >= 0)", example = "0.5")
    @NotNull(message = "La quantité détenue ne peut pas être nulle")
    @PositiveOrZero(message = "La quantité détenue ne peut pas être négative")
    @Column(nullable = false)
    private Double quantiteDetenue;

    @Schema(description = "Clé privée associée (optionnel, sensible)", example = "5HueCGU8rMjxECyDialwujZiuBRi4E3uiKjGXEi1CJNGa4Y4M")
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
