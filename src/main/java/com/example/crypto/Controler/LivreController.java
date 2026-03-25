package com.example.crypto.Controler; // استعملنا نفس السمية ديال المجلد لي عندكم

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import com.example.crypto.model.Crypto;
import com.example.crypto.dao.CryptoRepository; // جبناها من مجلد dao
import com.example.crypto.web.exceptions.CryptoNotFoundException; // جبناها من مجلد web.exceptions

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cryptos")
public class CryptoController {

    // استعملنا final باش نضمنو الحماية لي مطلوبة فـ Question bonus [cite: 56]
    private final CryptoRepository cryptoRepository;

    // حقن الـ Repository عبر المُنشئ (Injection via le constructeur) [cite: 33]
    public CryptoController(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    // مسار باش تجيب كاع العملات الرقمية (GET /cryptos) [cite: 36]
    @GetMapping
    public List<Crypto> listerCryptos() {
        return cryptoRepository.findAll();
    }

    // مسار باش تجيب عملة رقمية وحدة بالـ ID، ويلا مكانتش نلوحو CryptoNotFoundException [cite: 37]
    @GetMapping("/{id}")
    public Crypto recupererUneCrypto(@PathVariable Integer id) {
        return cryptoRepository.findById(id)
                .orElseThrow(() -> new CryptoNotFoundException("La crypto avec l'id " + id + " n'existe pas."));
    }
    // 1. مسار إضافة عملة جديدة (POST /cryptos) - كيرجع الكود 201 Created
    @PostMapping
    public ResponseEntity<Crypto> ajouterCrypto(@Valid @RequestBody Crypto crypto) {
        Crypto cryptoAjoute = cryptoRepository.save(crypto);
        return ResponseEntity.status(HttpStatus.CREATED).body(cryptoAjoute);
    }

    // 2. مسار تعديل عملة كاينة (PUT /cryptos) - الـ ID خاصو يكون فـ JSON
    @PutMapping
    public Crypto mettreAJourCrypto(@RequestBody Crypto crypto) {
        return cryptoRepository.save(crypto);
    }

    // 3. مسار حذف عملة بالـ ID ديالها (DELETE /cryptos/{id})
    @DeleteMapping("/{id}")
    public void supprimerCrypto(@PathVariable Integer id) {
        cryptoRepository.deleteById(id);
    }
}