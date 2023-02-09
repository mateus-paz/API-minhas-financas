package br.com.muralis.minhasfinancas.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.muralis.minhasfinancas.exception.RegraNegocioException;
import br.com.muralis.minhasfinancas.model.entity.Lancamento;
import br.com.muralis.minhasfinancas.model.entity.Usuario;
import br.com.muralis.minhasfinancas.model.enums.StatusLancamento;
import br.com.muralis.minhasfinancas.model.repository.LancamentoRepository;
import br.com.muralis.minhasfinancas.model.repository.LancamentoRepositoryTest;
import br.com.muralis.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//execucao
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificacao
		assertEquals(lancamento.getId(), lancamentoSalvo.getId());
		assertEquals(lancamento.getStatus(), StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		//cenário 
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);
		
		//execucao e verificacao
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//cenário
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//execucao
		service.atualizar(lancamentoSalvo);
		
		//verificacao
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();

		//execucao e verificacao
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);

		//execucao
		service.deletar(lancamento);
	
		//verificacao
		Mockito.verify( repository ).delete(lancamento);
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//execucao
		Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class);
	
		//verificacao
		Mockito.verify( repository, Mockito.never() ).delete(lancamento);
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		//cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
				
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//execucao
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//verificacoes
		assertNotEquals(resultado.size(), 0);
		assertEquals(resultado.size(), 1);
		assertTrue( resultado.contains(lancamento) );
	}

	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		//cenario
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//execucao
		service.atualizarStatus(lancamento, novoStatus);
		
		//verificacoes
		assertEquals(lancamento.getStatus(), novoStatus);
		Mockito.verify(service).atualizar(lancamento);
		
	}

	@Test
	public void deveObterUmLancamentoPorID() {
		//cenario 
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//execucao
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//verificacao
		assertTrue(resultado.isPresent());
		
	}
	
	@Test
	public void deveRetornarVazioQuandoOLancamentoNaoExiste() {
		//cenario 
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//execucao
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//verificacao
		assertFalse(resultado.isPresent());
		
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe uma Descrição válida.");
		
		lancamento.setDescricao("Salario");
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Mês válido.");
		
		lancamento.setMes(0);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Mês válido.");
		
		lancamento.setMes(13);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Mês válido.");
		
		lancamento.setMes(1);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Ano válido.");
		
		lancamento.setAno(202);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Ano válido.");
		
		lancamento.setAno(20202);

		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Ano válido.");
		
		lancamento.setAno(2022);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Usuário.");
		
		lancamento.setUsuario(new Usuario());
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Usuário.");
		
		lancamento.getUsuario().setId(1l);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(-1));
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(10));
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento));
		assertInstanceOf(RegraNegocioException.class, erro);
		assertEquals(erro.getMessage(), "Informe um Tipo de Lançamento.");
	}
	
}
