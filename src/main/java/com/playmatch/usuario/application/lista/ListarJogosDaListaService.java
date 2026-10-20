package com.playmatch.usuario.application.lista;


import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.JogoSalvo;
import com.playmatch.usuario.domain.TipoLista;
import com.playmatch.usuario.infrastructure.dto.lista.JogoSalvoResponse;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * The Service Class ListarJogosDaListaService
 *
 * As listas são públicas para usuários autenticados, para que amigos vejam
 * os favoritos e a lista de desejos uns dos outros.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class ListarJogosDaListaService {

    private final UsuarioRepository usuarioRepository;

    public List<JogoSalvoResponse> executar(UUID usuarioId, TipoLista tipo) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND))
                .getLista(tipo)
                .stream()
                .sorted(Comparator.comparing(JogoSalvo::getAdicionadoEm, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(JogoSalvoResponse::fromDomain)
                .toList();
    }
}
