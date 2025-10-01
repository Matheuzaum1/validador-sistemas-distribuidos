package com.distribuidos.test;

import com.distribuidos.common.MessageBuilder;
import com.distribuidos.common.Transacao;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransacaoResponseTest {
    @Test
    public void buildTransacoesResponse_includes_sender_and_receiver_names() throws Exception {
        Transacao t = new Transacao(1L, "111.222.333-44", "555.666.777-88", 99.5, LocalDateTime.now());
        t.setNomeEnviador("Pedro Oliveira Costa");
        t.setNomeRecebedor("Ana Costa Ferreira");

        List<Transacao> list = new ArrayList<>();
        list.add(t);

        String json = MessageBuilder.buildTransacoesResponse("transacao_ler", "ok", list);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        assertTrue(root.has("transacoes"));
        JsonNode transacoes = root.get("transacoes");
        assertTrue(transacoes.isArray());
        assertEquals(1, transacoes.size());

        JsonNode item = transacoes.get(0);
        assertTrue(item.has("usuario_enviador"));
        assertTrue(item.has("usuario_recebedor"));

        JsonNode enviador = item.get("usuario_enviador");
        JsonNode recebedor = item.get("usuario_recebedor");

        assertEquals("Pedro Oliveira Costa", enviador.get("nome").asText());
        assertEquals("111.222.333-44", enviador.get("cpf").asText());

        assertEquals("Ana Costa Ferreira", recebedor.get("nome").asText());
        assertEquals("555.666.777-88", recebedor.get("cpf").asText());
    }
}
