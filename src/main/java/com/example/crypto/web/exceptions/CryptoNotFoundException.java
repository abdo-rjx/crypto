package com.example.crypto.web.exceptions;

public class CryptoNotFoundException extends RuntimeException {

    public CryptoNotFoundException(Long id) {
        super("Crypto introuvable avec l'id : " + id);
    }
}
