package com.playmatch.usuario.infrastructure.controller;

import com.playmatch.usuario.application.avaliacao.AtualizarAvaliacaoService;
import com.playmatch.usuario.application.avaliacao.AvaliarJogoService;
import com.playmatch.usuario.application.avaliacao.BuscarAvaliacaoService;
import com.playmatch.usuario.application.avaliacao.DeletarAvaliacaoService;
import com.playmatch.usuario.application.avaliacao.ListarAvaliacoesService;
import com.playmatch.usuario.infrastructure.dto.avaliacao.AtualizarAvaliacaoRequest;
import com.playmatch.usuario.infrastructure.dto.avaliacao.AvaliacaoResponse;
import com.playmatch.usuario.infrastructure.dto.avaliacao.AvaliarJogoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * The Controller Class UsuarioAvaliacaoController
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */
@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class UsuarioAvaliacaoController {

    private final AvaliarJogoService avaliarJogoService;
    private final ListarAvaliacoesService listarAvaliacoesService;
    private final BuscarAvaliacaoService buscarAvaliacaoService;
    private final AtualizarAvaliacaoService atualizarAvaliacaoService;
    private final DeletarAvaliacaoService deletarAvaliacaoService;

    @PostMapping
    public ResponseEntity<Void> avaliarJogo(@Valid @RequestBody AvaliarJogoRequest request) {
        avaliarJogoService.executar(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping
    public ResponseEntity<Void> atualizarAvaliacao(@Valid @RequestBody AtualizarAvaliacaoRequest request) {
        atualizarAvaliacaoService.executar(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}")
    public List<AvaliacaoResponse> listarAvaliacoes(@PathVariable UUID usuarioId) {
        return listarAvaliacoesService.executar(usuarioId);
    }

    @GetMapping("/{usuarioId}/{jogoId}")
    public AvaliacaoResponse buscarAvaliacao(
            @PathVariable UUID usuarioId,
            @PathVariable Long jogoId
    ) {
        return buscarAvaliacaoService.executar(usuarioId, jogoId);
    }

    @DeleteMapping("/{usuarioId}/{jogoId}")
    public ResponseEntity<Void> deletarAvaliacao(
            @PathVariable UUID usuarioId,
            @PathVariable Long jogoId
    ) {
        deletarAvaliacaoService.executar(usuarioId, jogoId);
        return ResponseEntity.noContent().build();
    }
}
