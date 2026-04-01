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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/cryptos")
@Tag(name = "Cryptos", description = "Gestion du portefeuille de cryptomonnaies")
public class CryptoController {

    private final CryptoRepository cryptoRepository;

    public CryptoController(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    // ─────────────────────────────────────────────
    //  CRUD DE BASE
    // ─────────────────────────────────────────────

    @Operation(summary = "Liste toutes les cryptos", description = "Retourne la liste complète du portefeuille")
    @ApiResponse(responseCode = "200", description = "Liste récupérée avec succès")
    @GetMapping
    public List<Crypto> listerCryptos() {
        return cryptoRepository.findAll();
    }

    @Operation(summary = "Récupère une crypto par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Crypto trouvée"),
        @ApiResponse(responseCode = "404", description = "Crypto introuvable")
    })
    @GetMapping("/{id}")
    public Crypto recupererUneCrypto(
            @Parameter(description = "ID de la crypto", required = true) @PathVariable Long id) {
        return cryptoRepository.findById(id)
                .orElseThrow(() -> new CryptoNotFoundException(
                        "La crypto avec l'id " + id + " n'existe pas."));
    }

    @Operation(summary = "Ajoute une nouvelle crypto", description = "Crée une crypto et retourne 201 Created")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Crypto créée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PostMapping
    public ResponseEntity<Crypto> ajouterCrypto(@Valid @RequestBody Crypto crypto) {
        Crypto cryptoAjoute = cryptoRepository.save(crypto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cryptoAjoute);
    }

    @Operation(summary = "Mise à jour complète d'une crypto existante")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Crypto mise à jour"),
        @ApiResponse(responseCode = "404", description = "Crypto introuvable"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public Crypto mettreAJourCrypto(
            @Parameter(description = "ID de la crypto", required = true) @PathVariable Long id,
            @Valid @RequestBody Crypto crypto) {
        if (!cryptoRepository.existsById(id)) {
            throw new CryptoNotFoundException("La crypto avec l'id " + id + " n'existe pas.");
        }
        crypto.setId(id);
        return cryptoRepository.save(crypto);
    }

    @Operation(summary = "Mise à jour partielle de la quantité")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Quantité mise à jour"),
        @ApiResponse(responseCode = "404", description = "Crypto introuvable")
    })
    @PatchMapping("/{id}/quantite")
    public Crypto mettreAJourQuantite(
            @Parameter(description = "ID de la crypto", required = true) @PathVariable Long id,
            @Parameter(description = "Nouvelle quantité détenue", required = true) @RequestParam Double quantite) {
        Crypto crypto = cryptoRepository.findById(id)
                .orElseThrow(() -> new CryptoNotFoundException(
                        "La crypto avec l'id " + id + " n'existe pas."));
        crypto.setQuantiteDetenue(quantite);
        return cryptoRepository.save(crypto);
    }

    @Operation(summary = "Supprime une crypto par ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Crypto supprimée"),
        @ApiResponse(responseCode = "404", description = "Crypto introuvable")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCrypto(
            @Parameter(description = "ID de la crypto", required = true) @PathVariable Long id) {
        if (!cryptoRepository.existsById(id)) {
            throw new CryptoNotFoundException("La crypto avec l'id " + id + " n'existe pas.");
        }
        cryptoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────────────────────────────────────────
    //  RECHERCHE & FILTRAGE
    // ─────────────────────────────────────────────

    @Operation(summary = "Cherche une crypto par symbole", description = "Ex : BTC, ETH")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Crypto trouvée"),
        @ApiResponse(responseCode = "404", description = "Symbole introuvable")
    })
    @GetMapping("/symbole/{symbole}")
    public Crypto recupererParSymbole(
            @Parameter(description = "Symbole de la crypto (ex: BTC)", required = true) @PathVariable String symbole) {
        return cryptoRepository.findBySymbole(symbole.toUpperCase())
                .orElseThrow(() -> new CryptoNotFoundException(
                        "Aucune crypto trouvée avec le symbole : " + symbole));
    }

    @Operation(summary = "Recherche partielle par nom", description = "Ex : ?nom=bitcoin")
    @ApiResponse(responseCode = "200", description = "Résultats de recherche")
    @GetMapping("/search")
    public List<Crypto> rechercherParNom(
            @Parameter(description = "Nom ou partie du nom", required = true) @RequestParam String nom) {
        return cryptoRepository.findByNomContainingIgnoreCase(nom);
    }

    @Operation(summary = "Cryptos avec quantité inférieure au seuil")
    @ApiResponse(responseCode = "200", description = "Liste des cryptos en faible stock")
    @GetMapping("/faible-stock")
    public List<Crypto> cryptosFaibleStock(
            @Parameter(description = "Seuil de quantité (défaut: 10.0)") @RequestParam(defaultValue = "10.0") Double seuil) {
        return cryptoRepository.findByQuantiteDetenueLessThan(seuil);
    }

    @Operation(summary = "Cryptos avec quantité supérieure ou égale au seuil")
    @ApiResponse(responseCode = "200", description = "Liste des cryptos en stock élevé")
    @GetMapping("/stock-eleve")
    public List<Crypto> cryptosStockEleve(
            @Parameter(description = "Seuil de quantité (défaut: 100.0)") @RequestParam(defaultValue = "100.0") Double seuil) {
        return cryptoRepository.findByQuantiteDetenueGreaterThanEqual(seuil);
    }

    // ─────────────────────────────────────────────
    //  TRI
    // ─────────────────────────────────────────────

    @Operation(summary = "Liste triée par quantité", description = "Utilisez ?ordre=asc ou ?ordre=desc")
    @ApiResponse(responseCode = "200", description = "Liste triée")
    @GetMapping("/tri")
    public List<Crypto> listerAvecTri(
            @Parameter(description = "Ordre de tri : asc ou desc (défaut: desc)") @RequestParam(defaultValue = "desc") String ordre) {
        if ("asc".equalsIgnoreCase(ordre)) {
            return cryptoRepository.findAllByOrderByQuantiteDetenueAsc();
        }
        return cryptoRepository.findAllByOrderByQuantiteDetenueDesc();
    }

    @Operation(summary = "Top 5 des cryptos les plus détenues")
    @ApiResponse(responseCode = "200", description = "Top 5 retourné")
    @GetMapping("/top5")
    public List<Crypto> top5CryptosParQuantite() {
        return cryptoRepository.findTop5ByOrderByQuantiteDetenueDesc();
    }

    // ─────────────────────────────────────────────
    //  STATISTIQUES
    // ─────────────────────────────────────────────

    @Operation(summary = "Statistiques globales du portefeuille",
               description = "Retourne le nombre total, la quantité totale, la moyenne, et les extrêmes")
    @ApiResponse(responseCode = "200", description = "Statistiques calculées")
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