package com.playmatch.recomendacao.application;


import com.playmatch.recomendacao.domain.UsuarioCompativel;
import com.playmatch.recomendacao.infrastructure.repository.RecomendacaoRepository;
import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * The Service Class BuscarUsuariosCompativeisService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class BuscarUsuariosCompativeisService {

    private final RecomendacaoRepository recomendacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticado usuarioAutenticado;

    public List<UsuarioCompativel> executar(int limite) {
        UUID usuarioId = usuarioAutenticado.id();

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        return recomendacaoRepository.buscarUsuariosCompativeis(usuarioId, limite);
    }
}
