package com.example.crypto.Controler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import com.example.crypto.model.Crypto;
import com.example.crypto.dao.CryptoRepository;
import com.example.crypto.web.exceptions.CryptoNotFoundException;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cryptos")
public class CryptoController {

    private final CryptoRepository cryptoRepository;

    public CryptoController(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    // ─────────────────────────────────────────────
    //  CRUD DE BASE
    // ─────────────────────────────────────────────

    /** GET /cryptos — Liste toutes les cryptos */
    @GetMapping
    public List<Crypto> listerCryptos() {
        return cryptoRepository.findAll();
    }

    /** GET /cryptos/{id} — Récupère une crypto par ID */
    @GetMapping("/{id}")
    public Crypto recupererUneCrypto(@PathVariable Long id) {
        return cryptoRepository.findById(id)
                .orElseThrow(() -> new CryptoNotFoundException(
                        "La crypto avec l'id " + id + " n'existe pas."));
    }

    /** POST /cryptos — Ajoute une nouvelle crypto, retourne 201 Created */
    @PostMapping
    public ResponseEntity<Crypto> ajouterCrypto(@Valid @RequestBody Crypto crypto) {
        Crypto cryptoAjoute = cryptoRepository.save(crypto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cryptoAjoute);
    }

    /** PUT /cryptos/{id} — Mise à jour complète d'une crypto existante */
    @PutMapping("/{id}")
    public Crypto mettreAJourCrypto(@PathVariable Long id, @Valid @RequestBody Crypto crypto) {
        if (!cryptoRepository.existsById(id)) {
            throw new CryptoNotFoundException("La crypto avec l'id " + id + " n'existe pas.");
        }
        crypto.setId(id);
        return cryptoRepository.save(crypto);
    }

    /** PATCH /cryptos/{id}/quantite — Mise à jour partielle de la quantité */
    @PatchMapping("/{id}/quantite")
    public Crypto mettreAJourQuantite(@PathVariable Long id,
                                      @RequestParam Double quantite) {
        Crypto crypto = cryptoRepository.findById(id)
                .orElseThrow(() -> new CryptoNotFoundException(
                        "La crypto avec l'id " + id + " n'existe pas."));
        crypto.setQuantiteDetenue(quantite);
        return cryptoRepository.save(crypto);
    }

    /** DELETE /cryptos/{id} — Supprime une crypto par ID */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCrypto(@PathVariable Long id) {
        if (!cryptoRepository.existsById(id)) {
            throw new CryptoNotFoundException("La crypto avec l'id " + id + " n'existe pas.");
        }
        cryptoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────
    //  RECHERCHE & FILTRAGE
    // ─────────────────────────────────────────────

    /** GET /cryptos/symbole/{symbole} — Cherche une crypto par son symbole (ex: BTC) */
    @GetMapping("/symbole/{symbole}")
    public Crypto recupererParSymbole(@PathVariable String symbole) {
        return cryptoRepository.findBySymbole(symbole.toUpperCase())
                .orElseThrow(() -> new CryptoNotFoundException(
                        "Aucune crypto trouvée avec le symbole : " + symbole));
    }

    /** GET /cryptos/search?nom=bitcoin — Recherche partielle par nom */
    @GetMapping("/search")
    public List<Crypto> rechercherParNom(@RequestParam String nom) {
        return cryptoRepository.findByNomContainingIgnoreCase(nom);
    }

    /** GET /cryptos/faible-stock?seuil=10 — Cryptos avec quantité < seuil */
    @GetMapping("/faible-stock")
    public List<Crypto> cryptosFaibleStock(
            @RequestParam(defaultValue = "10.0") Double seuil) {
        return cryptoRepository.findByQuantiteDetenueLessThan(seuil);
    }

    /** GET /cryptos/stock-eleve?seuil=100 — Cryptos avec quantité >= seuil */
    @GetMapping("/stock-eleve")
    public List<Crypto> cryptosStockEleve(
            @RequestParam(defaultValue = "100.0") Double seuil) {
        return cryptoRepository.findByQuantiteDetenueGreaterThanEqual(seuil);
    }

    // ─────────────────────────────────────────────
    //  TRI
    // ─────────────────────────────────────────────

    /** GET /cryptos/tri?ordre=asc|desc — Liste triée par quantité */
    @GetMapping("/tri")
    public List<Crypto> listerAvecTri(
            @RequestParam(defaultValue = "desc") String ordre) {
        if ("asc".equalsIgnoreCase(ordre)) {
            return cryptoRepository.findAllByOrderByQuantiteDetenueAsc();
        }
        return cryptoRepository.findAllByOrderByQuantiteDetenueDesc();
    }

    /** GET /cryptos/top5 — Top 5 des cryptos les plus détenues */
    @GetMapping("/top5")
    public List<Crypto> top5CryptosParQuantite() {
        return cryptoRepository.findTop5ByOrderByQuantiteDetenueDesc();
    }

    // ─────────────────────────────────────────────
    //  STATISTIQUES
    // ─────────────────────────────────────────────

    /** GET /cryptos/stats — Statistiques globales du portefeuille */
    @GetMapping("/stats")
    public Map<String, Object> statistiques() {
        List<Crypto> all = cryptoRepository.findAll();

        double totalQuantite = all.stream()
                .mapToDouble(Crypto::getQuantiteDetenue)
                .sum();

        double moyenneQuantite = all.isEmpty() ? 0 :
                all.stream().mapToDouble(Crypto::getQuantiteDetenue).average().orElse(0);

        Crypto maxCrypto = all.stream()
                .max((a, b) -> Double.compare(a.getQuantiteDetenue(), b.getQuantiteDetenue()))
                .orElse(null);

        Crypto minCrypto = all.stream()
                .min((a, b) -> Double.compare(a.getQuantiteDetenue(), b.getQuantiteDetenue()))
                .orElse(null);

        Map<String, Object> stats = new HashMap<>();
        stats.put("nombreTotal", all.size());
        stats.put("quantiteTotale", totalQuantite);
        stats.put("quantiteMoyenne", moyenneQuantite);
        stats.put("cryptoMaxQuantite", maxCrypto != null ? maxCrypto.getSymbole() : null);
        stats.put("cryptoMinQuantite", minCrypto != null ? minCrypto.getSymbole() : null);

        return stats;
    }
}