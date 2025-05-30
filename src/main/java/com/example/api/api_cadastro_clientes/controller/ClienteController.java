package com.example.api.api_cadastro_clientes.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.api.api_cadastro_clientes.model.Cliente;
import com.example.api.api_cadastro_clientes.service.ClienteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService clienteService;
    private final ObjectMapper objectMapper;

    @Autowired
    public ClienteController (ClienteService clienteService, ObjectMapper objectMapper){
        this.clienteService = clienteService;
        this.objectMapper = objectMapper;
    }

    // POST /api/clientes
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> criarCliente(
        @RequestPart("cliente") String clienteStr,
        @RequestPart(name = "foto", required = false) MultipartFile foto){

            Cliente cliente;

            try{
                cliente = objectMapper.readValue(clienteStr, Cliente.class);
            } catch(JsonProcessingException e){
                return ResponseEntity.badRequest().body((Object) "Erro ao processar os dados do cliente (JSON inválido): " + e.getMessage());
            }

            try{
                Cliente novoCliente = clienteService.criarCliente(cliente, foto);
                return new ResponseEntity<>(novoCliente, HttpStatus.CREATED);
            } catch (IOException e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((Object) "Erro no upload da foto: " + e.getMessage());
            } catch (RuntimeException e){
                return ResponseEntity.badRequest().body((Object) e.getMessage());
            }
        } 

    // GET /api/clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodosClientes() {
        List<Cliente> clientes = clienteService.listarTodosClientes();
        return ResponseEntity.ok(clientes);
    }

    // GET /api/clientes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarClientePorId(@PathVariable Long id) {
        return clienteService.buscarClientePorId(id)
                .map(clienteEncontrado -> ResponseEntity.ok((Object) clienteEncontrado)) 
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) ("Cliente não encontrado com o ID: " + id)));
    }

    // GET /api/clientes/buscar_por_email?email=teste@example.com
    @GetMapping("/buscar_por_email")
    public ResponseEntity<?> buscarClientePorEmail(@RequestParam String email) {
        return clienteService.buscarClientePorEmail(email)
                .map(cliente -> ResponseEntity.ok((Object) cliente))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) ("Cliente não encontrado com o e-mail: " + email)));
    }

    // GET /api/clientes/buscar_por_cpf?cpf=12345678900
    @GetMapping("/buscar_por_cpf")
    public ResponseEntity<?> buscarClientePorCpf(@RequestParam String cpf) {
        return clienteService.buscarClientePorCpf(cpf)
                .map(cliente -> ResponseEntity.ok((Object) cliente))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) ("Cliente não encontrado com o CPF: " + cpf)));
    }

    @GetMapping("/{id}/foto-presignada")
    public ResponseEntity<?> obterUrlFotoPresignada(
        @PathVariable Long id,
        @RequestParam(name = "expiracaoMinutos", defaultValue = "15") int expiracaoMinutos) {

            String presignedUrl = clienteService.obterUrlFotoPresignada(id, expiracaoMinutos);

            if(presignedUrl != null){
                return ResponseEntity.ok().body(java.util.Map.of("presignedUrl", presignedUrl));
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) "Cliente não encontrado ou não possui foto");
            }
    }
    

    // PUT /api/clientes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarCliente(@PathVariable Long id, @RequestBody Cliente clienteAtualizado) {
        try {
            Cliente cliente = clienteService.atualizarCliente(id, clienteAtualizado);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            // Verifica se a mensagem da exceção indica que o cliente não foi encontrado
            // Certifique-se que a mensagem em ClienteServiceImpl para "não encontrado" seja EXATAMENTE "Erro: Cliente não encontrado com ID: " + id
            if (errorMessage != null && errorMessage.startsWith("Erro: Cliente não encontrado com ID:")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) errorMessage);
            }
            // Para outras exceções (como email/CPF duplicado), retorna badRequest
            return ResponseEntity.badRequest().body((Object) errorMessage);
        }
    }

    // DELETE /api/clientes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCliente(@PathVariable Long id) {
        try {
            clienteService.deletarCliente(id);
            return ResponseEntity.noContent().build(); // ResponseEntity<Void>
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            // Certifique-se que a mensagem em ClienteServiceImpl para "não encontrado" seja EXATAMENTE "Erro: Cliente não encontrado com ID: " + id
            if (errorMessage != null && errorMessage.startsWith("Erro: Cliente não encontrado com ID:")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) errorMessage);
            }
            return ResponseEntity.badRequest().body((Object) ("Erro ao processar a requisição: " + errorMessage));
        }
    }
}
