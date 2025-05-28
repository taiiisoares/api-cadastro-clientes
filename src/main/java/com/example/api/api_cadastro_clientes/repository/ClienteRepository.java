package com.example.api.api_cadastro_clientes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api.api_cadastro_clientes.model.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional <Cliente> findByEmail(String email); //Buscar Cliente através do e-mail

    Optional <Cliente> findByCpf(String cpf); //Buscar Cliente através do CPF
}
