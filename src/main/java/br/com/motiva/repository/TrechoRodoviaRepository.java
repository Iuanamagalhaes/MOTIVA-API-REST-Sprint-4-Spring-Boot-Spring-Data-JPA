package br.com.motiva.repository;

import br.com.motiva.model.TipoAmbiente;
import br.com.motiva.model.TrechoRodovia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrechoRodoviaRepository extends JpaRepository<TrechoRodovia, Long> {

    List<TrechoRodovia> findByNivelVegetacaoGreaterThanEqual(Double minimo);

    List<TrechoRodovia> findByTipoAmbiente(TipoAmbiente tipoAmbiente);

    List<TrechoRodovia> findByPossuiSensorIoT(boolean possuiSensorIoT);

    List<TrechoRodovia> findAllByOrderByNivelVegetacaoDesc();
}