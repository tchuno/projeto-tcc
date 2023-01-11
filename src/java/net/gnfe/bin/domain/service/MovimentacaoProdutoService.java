package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.MovimentacaoProduto;
import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.enumeration.MotivoMovimentacao;
import net.gnfe.bin.domain.repository.MovimentacaoProdutoRepository;
import net.gnfe.bin.domain.vo.MovimentacaoProdutoVO;
import net.gnfe.bin.domain.vo.filtro.MovimentacaoProdutoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
public class MovimentacaoProdutoService {

	@Autowired private MovimentacaoProdutoRepository movimentacaoProdutoRepository;
	@Autowired private ProdutoService produtoService;

	public MovimentacaoProduto get(Long id) {
		return movimentacaoProdutoRepository.get(id);
	}

	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(MovimentacaoProduto entity) throws MessageKeyException {
		try {
			movimentacaoProdutoRepository.saveOrUpdate(entity);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long id) throws MessageKeyException {
		try {
			movimentacaoProdutoRepository.deleteById(id);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<MovimentacaoProduto> findByFiltro(MovimentacaoProdutoFiltro filtro) {
		return movimentacaoProdutoRepository.findByFiltro(filtro, null, null);
	}

	public List<MovimentacaoProduto> findByFiltro(MovimentacaoProdutoFiltro filtro, Integer inicio, Integer max) {
		return movimentacaoProdutoRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(MovimentacaoProdutoFiltro filtro) {
		return movimentacaoProdutoRepository.countByFiltro(filtro);
	}

	public void movimentarProduto(MovimentacaoProdutoVO vo) {

		MovimentacaoProduto movimentacaoProduto = new MovimentacaoProduto(vo);

		MotivoMovimentacao motivoMovimentacao = vo.getMotivoMovimentacao();
		if(MotivoMovimentacao.NOTA_FISCAL_CONCLUIDA.equals(motivoMovimentacao)) {

			saveOrUpdate(movimentacaoProduto);

			Orcamento orcamento = vo.getOrcamento();
			Set<OrcamentoProduto> orcamentoProdutos = orcamento.getOrcamentoProdutos();
			for (OrcamentoProduto orcamentoProduto : orcamentoProdutos) {
				movimentarProduto(movimentacaoProduto, orcamentoProduto);
			}


		} else if (MotivoMovimentacao.NOTA_FISCAL_CANCELADA.equals(motivoMovimentacao)) {

			BigDecimal valorTotal = new BigDecimal(0);
			Orcamento orcamento = vo.getOrcamento();
			Set<OrcamentoProduto> orcamentoProdutos = orcamento.getOrcamentoProdutos();
			for (OrcamentoProduto orcamentoProduto : orcamentoProdutos) {
				Integer quantidade = orcamentoProduto.getQuantidade();
				Produto produto = orcamentoProduto.getProduto();
				BigDecimal valorUnidade = produto.getValorUnidade();
				BigDecimal vProd = valorUnidade.multiply(new BigDecimal(quantidade));
				valorTotal = valorTotal.add(vProd);
				movimentarProduto(movimentacaoProduto, orcamentoProduto);
			}

			movimentacaoProduto.setValorTotal(valorTotal);

			saveOrUpdate(movimentacaoProduto);

		} else if (MotivoMovimentacao.MOVIMENTACAO_ESTOQUE.equals(motivoMovimentacao)) {
			boolean isEntrada = vo.isEntrada();
			Produto produto = vo.getProduto();
			Integer emEstoque = produto.getEstoqueAtual();
			Integer quantidade = vo.getQuantidade();
			if(isEntrada) {
				Integer estoqueAtual = emEstoque + quantidade;
				produto.setEstoqueAtual(estoqueAtual);
				movimentacaoProduto.setEstoqueAtual(estoqueAtual);
				produtoService.saveOrUpdate(produto);
			} else {
				Integer estoqueAtual = emEstoque - quantidade;
				produto.setEstoqueAtual(estoqueAtual);
				movimentacaoProduto.setEstoqueAtual(estoqueAtual);
				produtoService.saveOrUpdate(produto);
			}

			saveOrUpdate(movimentacaoProduto);
		}

	}

	private void movimentarProduto(MovimentacaoProduto movimentacaoProduto, OrcamentoProduto orcamentoProduto) {
		MovimentacaoProduto movimentacao = new MovimentacaoProduto(movimentacaoProduto);

		Integer quantidade = orcamentoProduto.getQuantidade();
		Produto produto = orcamentoProduto.getProduto();
		BigDecimal valorUnidade = produto.getValorUnidade();
		BigDecimal vProd = valorUnidade.multiply(new BigDecimal(quantidade));

		Integer estoqueAtual;
		if(movimentacao.isEntrada()) {
			estoqueAtual = somarProdutoDoEstoque(orcamentoProduto);
		} else {
			estoqueAtual = subtrairProdutoDoEstoque(orcamentoProduto);
		}

		movimentacao.setValorTotal(vProd);
		movimentacao.setQuantidade(quantidade);
		movimentacao.setMotivoMovimentacao(MotivoMovimentacao.MOVIMENTACAO_ESTOQUE);
		movimentacao.setEstoqueAtual(estoqueAtual);
		saveOrUpdate(movimentacao);
	}

	private Integer subtrairProdutoDoEstoque(OrcamentoProduto orcamentoProduto) {
		Produto produto = orcamentoProduto.getProduto();
		Integer emEstoque = produto.getEstoqueAtual();
		Integer quantidade = orcamentoProduto.getQuantidade();
		Integer estoqueAtual = emEstoque - quantidade;

		produto.setEstoqueAtual(estoqueAtual);
		produtoService.saveOrUpdate(produto);

		return estoqueAtual;
	}

	private Integer somarProdutoDoEstoque(OrcamentoProduto orcamentoProduto) {
		Produto produto = orcamentoProduto.getProduto();
		Integer emEstoque = produto.getEstoqueAtual();
		Integer quantidade = orcamentoProduto.getQuantidade();
		Integer estoqueAtual = emEstoque + quantidade;

		produto.setEstoqueAtual(estoqueAtual);
		produtoService.saveOrUpdate(produto);

		return estoqueAtual;
	}

}