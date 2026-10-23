package br.com.motiva;

import br.com.motiva.model.Prioridade;
import br.com.motiva.model.TipoAmbiente;
import br.com.motiva.model.TrechoRodovia;
import br.com.motiva.service.MotorPrioridade;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MotorPrioridadeTest {

    private final MotorPrioridade motor = new MotorPrioridade();

    private Prioridade classificar(double nivel) {
        return motor.classificarPrioridade(new TrechoRodovia(0, 5, nivel, TipoAmbiente.PADRAO, false));
    }

    @Test
    void classificaPelosLimiaresDaSprint2() {
        assertEquals(Prioridade.CRITICO, classificar(45));
        assertEquals(Prioridade.CRITICO, classificar(40));
        assertEquals(Prioridade.ATENCAO, classificar(25));
        assertEquals(Prioridade.ATENCAO, classificar(20));
        assertEquals(Prioridade.ALERTA, classificar(12));
        assertEquals(Prioridade.ALERTA, classificar(10));
        assertEquals(Prioridade.NORMAL, classificar(9.99));
        assertEquals(Prioridade.NORMAL, classificar(0));
    }
}
