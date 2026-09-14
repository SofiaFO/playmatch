package com.playmatch.usuario.application.avaliacao;

import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.dto.avaliacao.AvaliacaoResponse;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * The Service Class ListarAvaliacoesService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */

@Service
@RequiredArgsConstructor
public class ListarAvaliacoesService {

    private final UsuarioRepository usuarioRepository;

    public List<AvaliacaoResponse> executar(UUID usuarioId){
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        return usuario.getAvaliacoes()
                .stream()
                .map(AvaliacaoResponse::fromDomain)
                .toList();
    }
}
