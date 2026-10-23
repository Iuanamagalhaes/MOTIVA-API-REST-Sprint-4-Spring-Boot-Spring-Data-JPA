package br.com.motiva.repository;

import br.com.motiva.model.IntervencaoOperacional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntervencaoOperacionalRepository extends JpaRepository<IntervencaoOperacional, Long> {

    List<IntervencaoOperacional> findByTipoIntervencao(String tipoIntervencao);

    List<IntervencaoOperacional> findByTrechoAlvoId(Long idTrecho);

    boolean existsByTrechoAlvoId(Long idTrecho);
}