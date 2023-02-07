package br.com.muralis.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.muralis.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
}
