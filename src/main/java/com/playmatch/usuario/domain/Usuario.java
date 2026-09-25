package com.playmatch.usuario.domain;


import com.playmatch.jogo.domain.Jogo;
import com.playmatch.shared.exception.BusinessException;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * The Domain Class Usuario
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */

@Getter
@NoArgsConstructor
@Node("Usuario")
@SuperBuilder
public class Usuario {

    @Id
    @GeneratedValue(GeneratedValue.UUIDGenerator.class)
    private UUID id;

    private String nome;

    private String email;

    private String senhaHash;

    @Builder.Default
    @Relationship(
            type = "AVALIOU",
            direction = Relationship.Direction.OUTGOING
    )
    private Set<Avaliacao> avaliacoes = new HashSet<>();

    public static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public static void validarEmail(String email){
        if (email == null || !Pattern.compile(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        ).matcher(email).matches()) {
            throw new IllegalArgumentException("Email inválido.");
        }
    }

    public static void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }

        if (!Pattern.compile("^[A-Za-zÀ-ÿ'\\-\\s]+$").matcher(nome).matches()) {
            throw new IllegalArgumentException("Nome deve conter apenas letras, espaços, hífen ou apóstrofo.");
        }

        if (nome.trim().length() < 2) {
            throw new IllegalArgumentException("Nome deve conter pelo menos 2 caracteres.");
        }
    }

    public static void validarSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            throw new IllegalArgumentException("A senha deve conter pelo menos 8 caracteres.");
        }
        if (!Pattern.compile(".*[A-Z].*").matcher(senha).matches()) {
            throw new IllegalArgumentException("A senha deve conter ao menos uma letra maiúscula.");
        }
        if (!Pattern.compile(".*[a-z].*").matcher(senha).matches()) {
            throw new IllegalArgumentException("A senha deve conter ao menos uma letra minúscula.");
        }
        if (!Pattern.compile(".*\\d.*").matcher(senha).matches()) {
            throw new IllegalArgumentException("A senha deve conter ao menos um número.");
        }
        if (!Pattern.compile(".*[!@#$%^&*()\\-_=+{};:,<.>].*").matcher(senha).matches()) {
            throw new IllegalArgumentException("A senha deve conter ao menos um caractere especial.");
        }
        if(senha.length() > 72){
            throw new IllegalArgumentException("A senha deve conter no máximo 72 caracteres");
        }
    }

    public void atualizarDados(String nome, String email) {
        if (nome != null) {
            validarNome(nome);
        }
        if (email != null) {
            validarEmail(email);
        }

        if (nome != null) {
            this.nome = nome;
        }
        if (email != null) {
            this.email = email;
        }
    }

    public void avaliar(Jogo jogo, Integer nota) {
        avaliacoes.stream()
                .filter(avaliacao ->
                        Objects.equals(
                                avaliacao.getJogo().getId(),
                                jogo.getId()
                        )
                )
                .findFirst()
                .ifPresentOrElse(
                        avaliacao -> avaliacao.atualizarNota(nota),
                        () -> avaliacoes.add(new Avaliacao(nota, jogo))
                );
    }

    public Optional<Avaliacao> buscarAvaliacao(Long jogoId) {
        return avaliacoes.stream()
                .filter(avaliacao ->
                        Objects.equals(
                                avaliacao.getJogo().getId(),
                                jogoId
                        )
                )
                .findFirst();
    }

    public void atualizarAvaliacao(Long jogoId, Integer nota) {
        Optional<Avaliacao> avaliacao = buscarAvaliacao(jogoId);
        if (avaliacao.isPresent()) {
            avaliacao.get().atualizarNota(nota);
        } else {
            throw new BusinessException("Avaliação não encontrada", HttpStatus.NOT_FOUND);
        }
    }

    public void deletarAvaliacao(Long jogoId) {
        Optional<Avaliacao> avaliacao = buscarAvaliacao(jogoId);
        if (avaliacao.isPresent()) {
            avaliacoes.remove(avaliacao.get());
        } else {
            throw new BusinessException("Avaliação não encontrada", HttpStatus.NOT_FOUND);
        }
    }
}
