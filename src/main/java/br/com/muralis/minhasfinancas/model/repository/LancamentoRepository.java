package br.com.muralis.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.muralis.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
