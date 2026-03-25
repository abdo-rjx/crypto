package com.example.crypto.dao;

import com.example.crypto.model.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {

    // Tri par quantité décroissante
    List<Crypto> findAllByOrderByQuantiteDetenueDesc();

    // Tri par quantité croissante
    List<Crypto> findAllByOrderByQuantiteDetenueAsc();

    // Recherche par symbole exact (ex: BTC)
    Optional<Crypto> findBySymbole(String symbole);

    // Recherche par nom (insensible à la casse, partielle)
    List<Crypto> findByNomContainingIgnoreCase(String nom);

    // Cryptos dont la quantité est inférieure à un seuil (alertes de stock bas)
    List<Crypto> findByQuantiteDetenueLessThan(Double seuil);

    // Cryptos dont la quantité est supérieure à un seuil
    List<Crypto> findByQuantiteDetenueGreaterThanEqual(Double seuil);

    // Top 5 par quantité décroissante
    List<Crypto> findTop5ByOrderByQuantiteDetenueDesc();
}
