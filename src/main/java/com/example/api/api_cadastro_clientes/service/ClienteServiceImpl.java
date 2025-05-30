package com.example.api.api_cadastro_clientes.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.api.api_cadastro_clientes.model.Cliente;
import com.example.api.api_cadastro_clientes.repository.ClienteRepository;

import java.util.Optional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Service
public class ClienteServiceImpl implements ClienteService{
    
    private final ClienteRepository clienteRepository;
    private final S3StorageService s3StorageService;

    //Injeção de dependência via construtor
    public ClienteServiceImpl(ClienteRepository clienteRepository, S3StorageService s3StorageService){
        this.clienteRepository = clienteRepository;
        this.s3StorageService = s3StorageService;
    }

    @Override
    public Cliente criarCliente(Cliente cliente, MultipartFile foto) throws IOException{

        //Verificar se email ou CPF já existem
        if(clienteRepository.findByEmail(cliente.getEmail()).isPresent()){
            throw new RuntimeException("Erro: E-mail já cadastrado! " + cliente.getEmail());
        }
        if (clienteRepository.findByCpf(cliente.getCpf()).isPresent()) {
            throw new RuntimeException("Erro: CPF já cadastrado! " + cliente.getCpf());
        }

        if(foto != null && !foto.isEmpty()){
            String fotoUrl = s3StorageService.uploadFile(foto);
            cliente.setFotoUrl(fotoUrl);
        }
        //Se não existir, salvar o novo cliente
        return clienteRepository.save(cliente);
    }

    @Override
    public List<Cliente> listarTodosClientes(){
        return clienteRepository.findAll();
    }

    @Override
    public Optional<Cliente> buscarClientePorId(Long id){
        return clienteRepository.findById(id);
    }

    @Override
    public Optional<Cliente> buscarClientePorEmail(String email){
        return clienteRepository.findByEmail(email);
    }

    @Override
    public Optional<Cliente> buscarClientePorCpf(String cpf){
        return clienteRepository.findByCpf(cpf);
    }

    @Override
    public Cliente atualizarCliente(Long id, Cliente clienteAtualizado){
        //Verificar se o cliente a ser atualizado realmente existe
        return clienteRepository.findById(id).map(clienteExistente -> {
            clienteExistente.setNome(clienteAtualizado.getNome());
            clienteExistente.setEmail(clienteAtualizado.getEmail());
            clienteExistente.setCpf(clienteAtualizado.getCpf());
            // clienteExistente.setFotoUrl(clienteAtualizado.getFotoUrl());

            // Validações para email/CPF duplicados ao atualizar (exceto para o próprio cliente)
            Optional<Cliente> porEmail = clienteRepository.findByEmail(clienteAtualizado.getEmail());
            if (porEmail.isPresent() && !porEmail.get().getId().equals(id)) {
                throw new RuntimeException("Erro: E-mail já cadastrado para outro cliente: " + clienteAtualizado.getEmail());
            }

            Optional<Cliente> porCpf = clienteRepository.findByCpf(clienteAtualizado.getCpf());
            if(porCpf.isPresent() && !porCpf.get().getId().equals(id)){
                throw new RuntimeException("Erro: CPF já cadastrado para outro cliente: " + clienteAtualizado.getCpf());
            }

            return clienteRepository.save(clienteExistente);
        }).orElseThrow(() -> new RuntimeException("Erro: Cliente não encontrado com ID: " + id));
    }

    @Override
    public void deletarCliente(Long id){
        //Verificar se o cliente existe antes de deletar
        if(!clienteRepository.existsById(id)){
            throw new RuntimeException("Erro: Cliente não encontrado com ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Override
    public String obterUrlFotoPresignada(Long idCliente, int expirationInMinutes){
        Optional<Cliente> clienteOptional = clienteRepository.findById(idCliente);
        if(clienteOptional.isPresent()){
            Cliente cliente = clienteOptional.get();
            String fotoUrlCompleta = cliente.getFotoUrl();

            if(fotoUrlCompleta != null && !fotoUrlCompleta.isEmpty()){
                try{
                    URL url = new URL(fotoUrlCompleta);
                    String objectKey = url.getPath();
                    if(objectKey.startsWith("/")){
                        objectKey = objectKey.substring(1);
                    }

                    if(objectKey.trim().isEmpty()){
                        System.out.println("Chave do objeto S3 não pôde ser extraída da URL " + fotoUrlCompleta);
                        return null;
                    }

                    return s3StorageService.generatePresignedUrl(objectKey, expirationInMinutes);
                } catch(MalformedURLException e){
                    System.out.println("URL da foto armazenada está malformada " + fotoUrlCompleta + " - Erro: " + e.getMessage());
                    return null;
                }
            }
        }
        return null;
    }
}
