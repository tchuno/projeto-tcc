package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.*;
import net.gnfe.bin.domain.enumeration.Bandeira;
import net.gnfe.bin.domain.enumeration.FormaPagamento;
import net.gnfe.bin.domain.enumeration.MotivoMovimentacao;
import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;
import net.gnfe.bin.domain.repository.MovimentacaoProdutoRepository;
import net.gnfe.bin.domain.vo.MovimentacaoProdutoVO;
import net.gnfe.bin.domain.vo.filtro.MovimentacaoProdutoFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.excel.ExcelFormat;
import net.gnfe.util.excel.ExcelWriter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class MovimentacaoProdutoService {

	@Autowired private MovimentacaoProdutoRepository movimentacaoProdutoRepository;
	@Autowired private SessionFactory sessionFactory;
	@Autowired private ProdutoService produtoService;
	@Autowired private MessageService messageService;

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

	public List<MovimentacaoProduto> findByIds(List<Long> ids) {
		return movimentacaoProdutoRepository.findByIds(ids);
	}

	public List<Long> findIdsByFiltro(MovimentacaoProdutoFiltro filtro) {
		return movimentacaoProdutoRepository.findIdsByFiltro(filtro);
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
			emEstoque = emEstoque == null ? 0 : emEstoque;
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
		} else {
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
		movimentacao.setProduto(produto);
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

	public File render(MovimentacaoProdutoFiltro filtro) {

		System.out.println("RelatorioGeralService.render()");
		try {

			String fileOrigemNome = "relatorio-geral.xlsx";

			String extensao = DummyUtils.getExtensao(fileOrigemNome);

			File fileOrigem = DummyUtils.getFileFromResource("/net/gnfe/excel/" + fileOrigemNome);

			File file = File.createTempFile("relatorio-geral-", "." + extensao);
			//file.deleteOnExit();
			FileUtils.copyFile(fileOrigem, file);

			ExcelWriter ew = new ExcelWriter();
			ew.abrirArquivo(file);
			Workbook workbook = ew.getWorkbook();
			ExcelFormat ef = new ExcelFormat(workbook);
			ew.setExcelFormat(ef);

			Sheet sheet = workbook.getSheet("Movimentações");
			renderRowsMovimentacao(sheet, ew, filtro);

			file.delete();
			File fileDestino = File.createTempFile("relatorio-geral", ".xlsx");
			System.out.println("Criado arquivo temporario no destino: " + fileDestino.getAbsolutePath());
			System.out.println("Temp File Name: " + fileDestino.getName());
			//fileDestino.deleteOnExit();

			FileOutputStream fos = new FileOutputStream(fileDestino);
			workbook.write(fos);
			workbook.close();

			return fileDestino;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InvalidFormatException e) {
			throw new RuntimeException(e);
		}
	}

	private void renderRowsMovimentacao(Sheet sheet, ExcelWriter ew, MovimentacaoProdutoFiltro filtro) {

		final List<Long> ids = findIdsByFiltro(filtro);

		if(ids.isEmpty()){
			return;
		}

		int rowNum = 1;
		do {
			List<Long> ids2 = new ArrayList<Long>();
			for (int i = 0; i < 200 && !ids.isEmpty(); i++) {
				Long id = ids.remove(0);
				ids2.add(id);
			}

			List<MovimentacaoProduto> list = findByIds(ids2);

			for (int i = 0; i < list.size(); i++) {

				MovimentacaoProduto mp = list.get(i);

				ew.criaLinha(sheet, rowNum++);
				renderBody(ew, mp);

			}

			Session session = sessionFactory.getCurrentSession();
			session.clear();
		}
		while (!ids.isEmpty());
	}

	private void renderBody(ExcelWriter ew, MovimentacaoProduto mp) {

		Long id = mp.getId();
		ew.escrever(id);

		Orcamento orcamento = mp.getOrcamento();
		orcamento = orcamento == null ? new Orcamento() : orcamento;
		Long orcamentoId = orcamento.getId();
		ew.escrever(orcamentoId);

		NotaFiscal notaFiscal = orcamento.getNotaFiscal();
		notaFiscal = notaFiscal == null ? new NotaFiscal() : notaFiscal;
		Long notaFiscalId = notaFiscal.getId();
		ew.escrever(notaFiscalId);

		Produto produto = mp.getProduto();
		produto = produto == null ? new Produto() : produto;
		Long produtoId = produto.getId();
		ew.escrever(produtoId);

		Usuario autor = orcamento.getAutor();
		autor = autor == null ? new Usuario() : autor;
		String nomeAutor = autor.getNome();
		ew.escrever(nomeAutor);

		Usuario cliente = orcamento.getCliente();
		cliente = cliente == null ? new Usuario() : cliente;
		String clienteNome = cliente.getNome();
		ew.escrever(clienteNome);

		FormaPagamento formaPagamento = orcamento.getFormaPagamento();
		ew.escrever(messageService.getValue("FormaPagamento." + (formaPagamento != null ? formaPagamento.name() : null) + ".label"));

		Bandeira bandeira = orcamento.getBandeira();
		ew.escrever(bandeira != null ? bandeira.name() : null);

		Date dataCriacao = notaFiscal.getDataCriacao();
		ew.escrever(DummyUtils.formatDateTime(dataCriacao));

		StatusNotaFiscal statusNotaFiscal = notaFiscal.getStatusNotaFiscal();
		ew.escrever(messageService.getValue("StatusNotaFiscal." + (statusNotaFiscal != null ? statusNotaFiscal.name() : null) + ".label"));

		Date dataEnvio = notaFiscal.getDataEnvio();
		ew.escrever(DummyUtils.formatDateTime(dataEnvio));

		Date dataCancelamento = notaFiscal.getDataCancelamento();
		ew.escrever(DummyUtils.formatDateTime(dataCancelamento));

		String chaveAcesso = notaFiscal.getChaveAcesso();
		ew.escrever(chaveAcesso);

		String protocolo = notaFiscal.getProtocolo();
		ew.escrever(protocolo);

		String protocoloCancelamento = notaFiscal.getProtocoloCancelamento();
		ew.escrever(protocoloCancelamento);

		String nomeProduto = produto.getNome();
		ew.escrever(nomeProduto);

		BigDecimal valorUnidade = produto.getValorUnidade();
		ew.escrever(valorUnidade != null ? "R$ " + DummyUtils.formatCurrency(valorUnidade) : null);

		BigDecimal valorCompra = produto.getValorCompra();
		ew.escrever(valorCompra != null ? "R$ " + DummyUtils.formatCurrency(valorCompra) : null);

		Integer produtoEstoqueAtual = produto.getEstoqueAtual();
		ew.escrever(produtoEstoqueAtual);

		Date dataMovimentacao = mp.getData();
		ew.escrever(DummyUtils.formatDate(dataMovimentacao));

		ew.escrever(DummyUtils.formatTime(dataMovimentacao));

		MotivoMovimentacao motivoMovimentacao = mp.getMotivoMovimentacao();
		ew.escrever(messageService.getValue("MotivoMovimentacao." + motivoMovimentacao.name() + ".label"));

		boolean entrada = mp.isEntrada();
		ew.escrever(entrada ? messageService.getValue("entrada.label") : messageService.getValue("saida.label"));

		Integer estoqueAtual = mp.getEstoqueAtual();
		ew.escrever(estoqueAtual);

		BigDecimal valorTotal = mp.getValorTotal();
		ew.escrever(valorTotal != null ? "R$ " + DummyUtils.formatCurrency(valorTotal) : null);
	}

}