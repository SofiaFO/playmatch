package com.playmatch.jogo.infrastructure.repository;


import com.playmatch.jogo.domain.AvaliacaoDoJogo;
import com.playmatch.jogo.domain.EstatisticasAvaliacao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * The Repository Port AvaliacaoJogoConsultaRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public interface AvaliacaoJogoConsultaRepository {

    EstatisticasAvaliacao buscarEstatisticas(Long jogoId);

    /** Estatísticas de vários jogos de uma vez; jogos sem avaliação ficam fora do mapa. */
    Map<Long, EstatisticasAvaliacao> buscarEstatisticas(Collection<Long> jogoIds);

    /** Avaliações do jogo, das mais recentes para as mais antigas. */
    List<AvaliacaoDoJogo> listarAvaliacoes(Long jogoId, boolean somenteComResenha, int pagina, int tamanho);

    long contarAvaliacoes(Long jogoId, boolean somenteComResenha);
}
