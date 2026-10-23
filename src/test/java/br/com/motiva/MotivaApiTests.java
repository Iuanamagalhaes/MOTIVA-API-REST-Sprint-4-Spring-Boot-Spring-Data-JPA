package br.com.motiva;

import br.com.motiva.repository.EquipeManutencaoRepository;
import br.com.motiva.repository.IntervencaoOperacionalRepository;
import br.com.motiva.repository.RelatorioPrioridadeRepository;
import br.com.motiva.repository.TrechoRodoviaRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class MotivaApiTests {

    @Autowired WebApplicationContext context;
    @Autowired IntervencaoOperacionalRepository intervencaoRepository;
    @Autowired TrechoRodoviaRepository trechoRepository;
    @Autowired EquipeManutencaoRepository equipeRepository;
    @Autowired RelatorioPrioridadeRepository relatorioRepository;

    MockMvc mvc;

    private static final String TRECHO_JSON = """
            {"quilometroInicial":10.0,"quilometroFinal":15.0,"nivelVegetacao":45.0,
             "tipoAmbiente":"UMIDO","possuiSensorIoT":true}""";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(context).build();
        intervencaoRepository.deleteAll();
        trechoRepository.deleteAll();
        equipeRepository.deleteAll();
        relatorioRepository.deleteAll();
    }

    private long criarTrecho(String json) throws Exception {
        MvcResult r = mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn();
        Number id = JsonPath.read(r.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }


    @Test
    void crudDeTrecho() throws Exception {
        MvcResult criado = mvc.perform(post("/api/trechos")
                        .contentType(MediaType.APPLICATION_JSON).content(TRECHO_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.tipoAmbiente").value("UMIDO"))
                .andExpect(jsonPath("$.possuiSensorIoT").value(true))
                .andReturn();
        long id = ((Number) JsonPath.read(criado.getResponse().getContentAsString(), "$.id")).longValue();

        mvc.perform(get("/api/trechos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelVegetacao").value(45.0));

        mvc.perform(get("/api/trechos")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mvc.perform(put("/api/trechos/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quilometroInicial":10.0,"quilometroFinal":15.0,"nivelVegetacao":8.0,
                                 "tipoAmbiente":"SECO","possuiSensorIoT":false}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nivelVegetacao").value(8.0))
                .andExpect(jsonPath("$.tipoAmbiente").value("SECO"));

        mvc.perform(delete("/api/trechos/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/trechos/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void trechoInexistenteRetorna404() throws Exception {
        mvc.perform(get("/api/trechos/{id}", 999999)).andExpect(status().isNotFound());
        mvc.perform(put("/api/trechos/{id}", 999999).contentType(MediaType.APPLICATION_JSON)
                        .content(TRECHO_JSON))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/trechos/{id}", 999999)).andExpect(status().isNotFound());
    }

    @Test
    void trechoInvalidoRetorna400() throws Exception {
        mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quilometroInicial":20.0,"quilometroFinal":10.0,"nivelVegetacao":5.0,
                                 "tipoAmbiente":"PADRAO","possuiSensorIoT":false}"""))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes.length()").value(greaterThanOrEqualTo(1)));
        mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quilometroInicial":1.0,"quilometroFinal":5.0,"nivelVegetacao":-3.0,
                                 "tipoAmbiente":"PADRAO","possuiSensorIoT":false}"""))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/trechos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"quilometroInicial":1.0,"quilometroFinal":5.0,"nivelVegetacao":3.0,
                                 "tipoAmbiente":"MARCIANO","possuiSensorIoT":false}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void filtroPorNivelMinimoUsaDerivedQuery() throws Exception {
        criarTrecho(TRECHO_JSON);
        criarTrecho("""
                {"quilometroInicial":15.0,"quilometroFinal":20.0,"nivelVegetacao":5.0,
                 "tipoAmbiente":"PADRAO","possuiSensorIoT":false}""");

        mvc.perform(get("/api/trechos").param("nivelMinimo", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nivelVegetacao").value(45.0));
    }


    @Test
    void crudDeEquipe() throws Exception {
        MvcResult criada = mvc.perform(post("/api/equipes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Equipe Norte\",\"quantidadeIntegrantes\":5}"))
                .andExpect(status().isCreated())
                .andReturn();
        long id = ((Number) JsonPath.read(criada.getResponse().getContentAsString(), "$.id")).longValue();

        mvc.perform(get("/api/equipes/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Equipe Norte"));
        mvc.perform(put("/api/equipes/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Equipe Norte 2\",\"quantidadeIntegrantes\":6}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantidadeIntegrantes").value(6));
        mvc.perform(delete("/api/equipes/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/equipes/{id}", id)).andExpect(status().isNotFound());

        mvc.perform(post("/api/equipes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"\",\"quantidadeIntegrantes\":0}"))
                .andExpect(status().isBadRequest());
    }


    @Test
    void crudDeIntervencaoComPolimorfismo() throws Exception {
        long trechoId = criarTrecho(TRECHO_JSON);

        MvcResult criada = mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descricao":"Rocada mecanizada","idTrechoAlvo":%d,
                                 "tipoIntervencao":"MECANIZADA","tipoEquipamento":"Trator TR-200"}"""
                                .formatted(trechoId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoIntervencao").value("MECANIZADA"))
                .andExpect(jsonPath("$.tipoEquipamento").value("Trator TR-200"))
                .andReturn();
        long id = ((Number) JsonPath.read(criada.getResponse().getContentAsString(), "$.id")).longValue();

        mvc.perform(get("/api/intervencoes").param("tipo", "MECANIZADA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/intervencoes").param("tipo", "MANUAL"))
                .andExpect(jsonPath("$.length()").value(0));

        mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descricao":"Manual sem pessoas","idTrechoAlvo":%d,
                                 "tipoIntervencao":"MANUAL"}""".formatted(trechoId)))
                .andExpect(status().isBadRequest());

        mvc.perform(post("/api/intervencoes").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"descricao":"X","idTrechoAlvo":999999,"tipoIntervencao":"MANUAL","numeroPessoas":3}"""))
                .andExpect(status().isBadRequest());

        mvc.perform(delete("/api/trechos/{id}", trechoId)).andExpect(status().isConflict());

        mvc.perform(delete("/api/intervencoes/{id}", id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/intervencoes/{id}", id)).andExpect(status().isNotFound());
        mvc.perform(delete("/api/trechos/{id}", trechoId)).andExpect(status().isNoContent());
    }


    @Test
    void geraRelatorioEPersisteNoHistorico() throws Exception {
        criarTrecho(TRECHO_JSON);
        criarTrecho("""
                {"quilometroInicial":15.0,"quilometroFinal":20.0,"nivelVegetacao":25.0,
                 "tipoAmbiente":"TROPICAL","possuiSensorIoT":true}""");
        criarTrecho("""
                {"quilometroInicial":20.0,"quilometroFinal":25.0,"nivelVegetacao":5.0,
                 "tipoAmbiente":"PADRAO","possuiSensorIoT":false}""");

        mvc.perform(post("/api/relatorios"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.relatorio.qtCritico").value(1))
                .andExpect(jsonPath("$.relatorio.qtAtencao").value(1))
                .andExpect(jsonPath("$.relatorio.qtAlerta").value(0))
                .andExpect(jsonPath("$.relatorio.qtNormal").value(1))
                .andExpect(jsonPath("$.itens[0].prioridade").value("CRITICO"))
                .andExpect(jsonPath("$.itens[0].intervencaoSugerida").value("MECANIZADA"));

        mvc.perform(get("/api/relatorios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        String hoje = java.time.LocalDate.now().toString();
        mvc.perform(get("/api/relatorios/periodo").param("inicio", hoje).param("fim", hoje))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        mvc.perform(get("/api/relatorios/periodo")
                        .param("inicio", "2000-01-01").param("fim", "2000-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void relatorioSemTrechosOuPeriodoInvalidoRetorna400() throws Exception {
        mvc.perform(post("/api/relatorios")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/periodo").param("inicio", "2026-02-01").param("fim", "2026-01-01"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/periodo").param("inicio", "ontem").param("fim", "hoje"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/periodo")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/relatorios/{id}", 999999)).andExpect(status().isNotFound());
    }
}
