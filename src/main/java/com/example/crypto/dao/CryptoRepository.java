package com.example.crypto.dao;

import com.example.crypto.model.Crypto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoRepository extends JpaRepository<Crypto, Long> {

    List<Crypto> findAllByOrderByQuantiteDetenueDesc();
}
