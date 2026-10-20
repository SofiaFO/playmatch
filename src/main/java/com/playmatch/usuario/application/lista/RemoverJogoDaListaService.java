package com.playmatch.usuario.application.lista;


import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.TipoLista;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The Service Class RemoverJogoDaListaService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class RemoverJogoDaListaService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticado usuarioAutenticado;

    @Transactional
    public void executar(UUID usuarioId, TipoLista tipo, Long jogoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        usuario.validarPermissao(usuarioAutenticado.id());

        usuario.removerDaLista(tipo, jogoId);

        usuarioRepository.save(usuario);
    }
}
