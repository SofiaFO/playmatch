package com.playmatch.usuario.infrastructure.controller;


import com.playmatch.usuario.application.lista.AdicionarJogoNaListaService;
import com.playmatch.usuario.application.lista.ListarJogosDaListaService;
import com.playmatch.usuario.application.lista.RemoverJogoDaListaService;
import com.playmatch.usuario.domain.TipoLista;
import com.playmatch.usuario.infrastructure.dto.lista.JogoSalvoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * The Controller Class UsuarioListaController
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@RestController
@RequestMapping("/usuarios/{usuarioId}")
@RequiredArgsConstructor
public class UsuarioListaController {

    private final AdicionarJogoNaListaService adicionarJogoNaListaService;
    private final RemoverJogoDaListaService removerJogoDaListaService;
    private final ListarJogosDaListaService listarJogosDaListaService;

    @GetMapping("/favoritos")
    public List<JogoSalvoResponse> listarFavoritos(@PathVariable UUID usuarioId) {
        return listarJogosDaListaService.executar(usuarioId, TipoLista.FAVORITOS);
    }

    @PostMapping("/favoritos/{jogoId}")
    public ResponseEntity<Void> favoritar(@PathVariable UUID usuarioId, @PathVariable Long jogoId) {
        adicionarJogoNaListaService.executar(usuarioId, TipoLista.FAVORITOS, jogoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/favoritos/{jogoId}")
    public ResponseEntity<Void> desfavoritar(@PathVariable UUID usuarioId, @PathVariable Long jogoId) {
        removerJogoDaListaService.executar(usuarioId, TipoLista.FAVORITOS, jogoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/desejos")
    public List<JogoSalvoResponse> listarDesejos(@PathVariable UUID usuarioId) {
        return listarJogosDaListaService.executar(usuarioId, TipoLista.DESEJOS);
    }

    @PostMapping("/desejos/{jogoId}")
    public ResponseEntity<Void> adicionarDesejo(@PathVariable UUID usuarioId, @PathVariable Long jogoId) {
        adicionarJogoNaListaService.executar(usuarioId, TipoLista.DESEJOS, jogoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/desejos/{jogoId}")
    public ResponseEntity<Void> removerDesejo(@PathVariable UUID usuarioId, @PathVariable Long jogoId) {
        removerJogoDaListaService.executar(usuarioId, TipoLista.DESEJOS, jogoId);
        return ResponseEntity.noContent().build();
    }
}
