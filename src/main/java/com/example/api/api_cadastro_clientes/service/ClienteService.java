package com.example.api.api_cadastro_clientes.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.example.api.api_cadastro_clientes.model.Cliente;

public interface ClienteService {
    Cliente criarCliente(Cliente cliente, MultipartFile foto) throws IOException;

    List<Cliente> listarTodosClientes();

    Optional<Cliente> buscarClientePorId(Long id);
    
    Optional<Cliente> buscarClientePorEmail(String email);

    Optional<Cliente> buscarClientePorCpf(String cpf);

    Cliente atualizarCliente(Long id, Cliente clienteAtualizado);

    void deletarCliente(Long id);

    String obterUrlFotoPresignada(Long idCliente, int expirationInMinutes);
}
