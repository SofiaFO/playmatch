package com.playmatch.usuario.application;


import com.playmatch.shared.application.security.SenhaEncoder;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.dto.CriarUsuarioRequest;
import com.playmatch.usuario.infrastructure.dto.UsuarioResponse;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * The Service Class CriarUsuarioService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Service
@RequiredArgsConstructor
public class CriarUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaEncoder senhaEncoder;

    public UsuarioResponse executar(CriarUsuarioRequest request){

        Usuario.validarNome(request.nome());
        Usuario.validarEmail(request.email());
        Usuario.validarSenha(request.senha());

        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email já cadastrado", HttpStatus.CONFLICT);
        }

        String senhaHash = senhaEncoder.codificar(request.senha());

        Usuario usuario = Usuario.builder()
                .nome(request.nome())
                .email(request.email())
                .senhaHash(senhaHash)
                .build();

        usuarioRepository.save(usuario);

        return UsuarioResponse.fromDomain(usuario);
    }
}
