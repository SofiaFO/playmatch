package com.playmatch.usuario.application.perfil;


import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Avaliacao;
import com.playmatch.usuario.domain.EstatisticasPerfil;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.dto.avaliacao.AvaliacaoResponse;
import com.playmatch.usuario.infrastructure.dto.perfil.PerfilResponse;
import com.playmatch.usuario.infrastructure.repository.PerfilRepository;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * The Service Class BuscarPerfilService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class BuscarPerfilService {

    static final int QUANTIDADE_ULTIMAS_AVALIACOES = 5;

    private final UsuarioRepository usuarioRepository;
    private final PerfilRepository perfilRepository;
    private final UsuarioAutenticado usuarioAutenticado;

    public PerfilResponse executar(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        UUID visitanteId = usuarioAutenticado.id();
        boolean ehVoce = usuarioId.equals(visitanteId);

        EstatisticasPerfil estatisticas = perfilRepository.buscarEstatisticas(usuarioId, visitanteId);

        return new PerfilResponse(
                usuario.getId(),
                usuario.getNome(),
                ehVoce,
                ehVoce ? null : estatisticas.visitanteSegue(),
                estatisticas.totalAvaliacoes(),
                estatisticas.notaMedia(),
                estatisticas.totalFavoritos(),
                estatisticas.totalDesejos(),
                estatisticas.seguidores(),
                estatisticas.seguindo(),
                estatisticas.generosFavoritos(),
                estatisticas.plataformasFavoritas(),
                ultimasAvaliacoes(usuario)
        );
    }

    public PerfilResponse executarParaUsuarioAutenticado() {
        return executar(usuarioAutenticado.id());
    }

    private static List<AvaliacaoResponse> ultimasAvaliacoes(Usuario usuario) {
        Comparator<Avaliacao> maisRecentes = Comparator.comparing(
                (Avaliacao avaliacao) -> avaliacao.getAtualizadaEm() != null
                        ? avaliacao.getAtualizadaEm()
                        : avaliacao.getCriadaEm(),
                Comparator.nullsLast(Comparator.<Instant>reverseOrder())
        );

        return usuario.getAvaliacoes().stream()
                .sorted(maisRecentes)
                .limit(QUANTIDADE_ULTIMAS_AVALIACOES)
                .map(AvaliacaoResponse::fromDomain)
                .toList();
    }
}
