package com.playmatch.shared.infrastructure.exception;

import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.application.AtualizarUsuarioService;
import com.playmatch.usuario.application.BuscarUsuarioService;
import com.playmatch.usuario.application.CriarUsuarioService;
import com.playmatch.usuario.application.DeletarUsuarioService;
import com.playmatch.usuario.application.ListarUsuariosService;
import com.playmatch.usuario.infrastructure.controller.UsuarioController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private final DeletarUsuarioService deletarUsuarioService = mock(DeletarUsuarioService.class);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UsuarioController controller = new UsuarioController(
                mock(CriarUsuarioService.class),
                mock(AtualizarUsuarioService.class),
                mock(BuscarUsuarioService.class),
                mock(ListarUsuariosService.class),
                deletarUsuarioService
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void idComFormatoInvalidoRetorna400() throws Exception {
        mockMvc.perform(get("/usuarios/nao-e-um-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O valor 'nao-e-um-uuid' é inválido para o parâmetro 'usuarioId'"));
    }

    @Test
    void jsonMalFormatadoRetorna400() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ nome: "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O corpo da requisição está ausente ou mal formatado"));
    }

    @Test
    void businessExceptionUsaOStatusDaExcecao() throws Exception {
        doThrow(new BusinessException("Sem permissão", HttpStatus.FORBIDDEN))
                .when(deletarUsuarioService).executar(any());

        mockMvc.perform(delete("/usuarios/" + UUID.randomUUID()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.mensagem").value("Sem permissão"));
    }
}
