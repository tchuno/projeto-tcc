package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.FormaPagamento;
import net.gnfe.bin.domain.enumeration.UnidadeMedida;
import net.gnfe.bin.domain.repository.OrcamentoRepository;
import net.gnfe.bin.domain.vo.ProdutoVO;
import net.gnfe.bin.domain.vo.filtro.OrcamentoFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.other.PDFCreator;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrcamentoService {

	@Autowired private OrcamentoRepository orcamentoRepository;
	@Autowired private MessageService messageService;
	@Autowired private UsuarioService usuarioService;

	public Orcamento get(Long id) {
		return orcamentoRepository.get(id);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(Orcamento entity) throws MessageKeyException {
		try {
			orcamentoRepository.saveOrUpdate(entity);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long id) throws MessageKeyException {
		try {
			orcamentoRepository.deleteById(id);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<Orcamento> findByFiltro(OrcamentoFiltro filtro) {
		return orcamentoRepository.findByFiltro(filtro, null, null);
	}

	public List<Orcamento> findByFiltro(OrcamentoFiltro filtro, Integer inicio, Integer max) {
		return orcamentoRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(OrcamentoFiltro filtro) {
		return orcamentoRepository.countByFiltro(filtro);
	}

	public File gerarOrcamento(Orcamento orcamento) {
		Map<String, Object> model = new HashMap<>();

		Date date = new Date();
		Usuario autor = orcamento.getAutor();
		Long autorId = autor.getId();
		autor = usuarioService.get(autorId);
		Usuario cliente = orcamento.getCliente();
		Long clienteId = cliente.getId();
		cliente = usuarioService.get(clienteId);
		FormaPagamento formaPagamento = orcamento.getFormaPagamento();
		List<ProdutoVO> vos = new ArrayList<>();
		Set<OrcamentoProduto> orcamentoProdutos = orcamento.getOrcamentoProdutos();
		for(OrcamentoProduto orcamentoProduto : orcamentoProdutos) {
			Produto produto = orcamentoProduto.getProduto();
			String nome = produto.getNome();
			BigDecimal valorUnidade = produto.getValorUnidade();
			UnidadeMedida unidadeMedida = produto.getUnidadeMedida();
			Integer quantidade = orcamentoProduto.getQuantidade();
			BigDecimal valor = valorUnidade.multiply(new BigDecimal(quantidade));

			ProdutoVO vo = new ProdutoVO();
			vo.setNome(nome);
			vo.setValorFormatado("R$ " + DummyUtils.formatCurrency(valorUnidade));
			vo.setUnidadeMedida(messageService.getValue("UnidadeMedida." + ( unidadeMedida != null ? unidadeMedida.name() : null ) + ".label"));
			vo.setQuantidade(quantidade);
			vo.setValor("R$ " + DummyUtils.formatCurrency(valor));

			vos.add(vo);
		}

		model.put("id", orcamento.getId());
		model.put("nome", StringEscapeUtils.escapeHtml4(autor.getNome()));
		model.put("email", autor.getEmail());
		model.put("telefone", autor.getTelefone());
		model.put("dataEmissao", DummyUtils.formatDateTime(date));
		model.put("clienteNome", StringEscapeUtils.escapeHtml4(cliente.getNome()));
		model.put("clienteTelefone", cliente.getTelefone());
		model.put("clienteEndereco", StringEscapeUtils.escapeHtml4(cliente.getEndereco()));
		model.put("clienteCidade", StringEscapeUtils.escapeHtml4(cliente.getCidade()));
		model.put("clienteEstado", StringEscapeUtils.escapeHtml4(cliente.getEstado()));
		model.put("clienteCep", cliente.getCep());
		model.put("produtos", vos);
		model.put("validade", DummyUtils.formatDate(DateUtils.addDays(date, 10)));
		model.put("garantia", DummyUtils.formatDate(DateUtils.addDays(date, 3)));
		model.put("formaPagamento", StringEscapeUtils.escapeHtml4(messageService.getValue("FormaPagamento." + formaPagamento.name() + ".label")));
		model.put("totalGeral", "R$ "+ DummyUtils.formatCurrency(DummyUtils.totalGeral(orcamentoProdutos)));

		PDFCreator pdfCreator = new PDFCreator("/net/gnfe/pdf/orcamento.htm", model);
		pdfCreator.setPortrait(true);
		File pdf = pdfCreator.toFile();

		return pdf;
	}
}
