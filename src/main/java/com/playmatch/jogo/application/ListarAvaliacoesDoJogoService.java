package com.playmatch.jogo.application;


import com.playmatch.jogo.infrastructure.dto.AvaliacaoDoJogoResponse;
import com.playmatch.jogo.infrastructure.repository.AvaliacaoJogoConsultaRepository;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.shared.infrastructure.dto.PaginaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * The Service Class ListarAvaliacoesDoJogoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class ListarAvaliacoesDoJogoService {

    private final JogoRepository jogoRepository;
    private final AvaliacaoJogoConsultaRepository avaliacaoJogoConsultaRepository;

    public PaginaResponse<AvaliacaoDoJogoResponse> executar(
            Long jogoId,
            boolean somenteComResenha,
            int pagina,
            int tamanho
    ) {
        if (!jogoRepository.existsById(jogoId)) {
            throw new BusinessException("Jogo não encontrado", HttpStatus.NOT_FOUND);
        }

        return PaginaResponse.de(
                avaliacaoJogoConsultaRepository.listarAvaliacoes(jogoId, somenteComResenha, pagina, tamanho),
                pagina,
                tamanho,
                avaliacaoJogoConsultaRepository.contarAvaliacoes(jogoId, somenteComResenha),
                AvaliacaoDoJogoResponse::fromDomain
        );
    }
}
