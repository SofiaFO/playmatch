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

    @Builder.Default
    @Relationship(
            type = "FAVORITOU",
            direction = Relationship.Direction.OUTGOING
    )
    private Set<JogoSalvo> favoritos = new HashSet<>();

    @Builder.Default
    @Relationship(
            type = "QUER_JOGAR",
            direction = Relationship.Direction.OUTGOING
    )
    private Set<JogoSalvo> desejos = new HashSet<>();

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

    public void validarPermissao(UUID usuarioAutenticadoId) {
        if (!Objects.equals(id, usuarioAutenticadoId)) {
            throw new BusinessException(
                    "Você não tem permissão para alterar dados de outro usuário",
                    HttpStatus.FORBIDDEN
            );
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

    /**
     * Avalia o jogo, ou atualiza a avaliação se ele já foi avaliado.
     * Quem avaliou já jogou, então o jogo sai da lista de desejos.
     */
    public void avaliar(Jogo jogo, Integer nota, String resenha) {
        buscarAvaliacao(jogo.getId())
                .ifPresentOrElse(
                        avaliacao -> avaliacao.atualizar(nota, resenha),
                        () -> avaliacoes.add(new Avaliacao(nota, resenha, jogo))
                );

        desejos.removeIf(salvo -> Objects.equals(salvo.getJogo().getId(), jogo.getId()));
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

    public void atualizarAvaliacao(Long jogoId, Integer nota, String resenha) {
        Optional<Avaliacao> avaliacao = buscarAvaliacao(jogoId);
        if (avaliacao.isPresent()) {
            avaliacao.get().atualizar(nota, resenha);
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

    public void favoritar(Jogo jogo) {
        if (contem(favoritos, jogo.getId())) {
            throw new BusinessException("Este jogo já está nos seus favoritos", HttpStatus.CONFLICT);
        }

        favoritos.add(new JogoSalvo(jogo));
    }

    public void desfavoritar(Long jogoId) {
        if (!favoritos.removeIf(salvo -> Objects.equals(salvo.getJogo().getId(), jogoId))) {
            throw new BusinessException("Este jogo não está nos seus favoritos", HttpStatus.NOT_FOUND);
        }
    }

    public void adicionarDesejo(Jogo jogo) {
        if (buscarAvaliacao(jogo.getId()).isPresent()) {
            throw new BusinessException(
                    "Você já avaliou este jogo, então ele não pode entrar na lista de desejos",
                    HttpStatus.UNPROCESSABLE_CONTENT
            );
        }
        if (contem(desejos, jogo.getId())) {
            throw new BusinessException("Este jogo já está na sua lista de desejos", HttpStatus.CONFLICT);
        }

        desejos.add(new JogoSalvo(jogo));
    }

    public void removerDesejo(Long jogoId) {
        if (!desejos.removeIf(salvo -> Objects.equals(salvo.getJogo().getId(), jogoId))) {
            throw new BusinessException("Este jogo não está na sua lista de desejos", HttpStatus.NOT_FOUND);
        }
    }

    public void adicionarNaLista(TipoLista tipo, Jogo jogo) {
        switch (tipo) {
            case FAVORITOS -> favoritar(jogo);
            case DESEJOS -> adicionarDesejo(jogo);
        }
    }

    public void removerDaLista(TipoLista tipo, Long jogoId) {
        switch (tipo) {
            case FAVORITOS -> desfavoritar(jogoId);
            case DESEJOS -> removerDesejo(jogoId);
        }
    }

    public Set<JogoSalvo> getLista(TipoLista tipo) {
        return switch (tipo) {
            case FAVORITOS -> favoritos;
            case DESEJOS -> desejos;
        };
    }

    private static boolean contem(Set<JogoSalvo> lista, Long jogoId) {
        return lista.stream().anyMatch(salvo -> Objects.equals(salvo.getJogo().getId(), jogoId));
    }
}
