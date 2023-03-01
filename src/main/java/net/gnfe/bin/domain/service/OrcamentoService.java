package net.gnfe.bin.domain.service;

import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.exception.NfeException;
import net.gnfe.bin.domain.entity.*;
import net.gnfe.bin.domain.enumeration.FormaPagamento;
import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;
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

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
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
	@Autowired private NotaFiscalService notaFiscalService;

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

	public File gerarOrcamento(Orcamento orcamento, Usuario autor) {
		Map<String, Object> model = new HashMap<>();

		Date date = new Date();
		Long autorId = autor.getId();
		autor = usuarioService.get(autorId);
		Usuario cliente = orcamento.getCliente();
		Long clienteId = cliente.getId();
		cliente = usuarioService.get(clienteId);
		FormaPagamento formaPagamento = orcamento.getFormaPagamento();
		List<ProdutoVO> vos = new ArrayList<>();
		NotaFiscal notaFiscal = orcamento.getNotaFiscal();
		StatusNotaFiscal statusNotaFiscal = notaFiscal.getStatusNotaFiscal();
		String chaveAcesso = notaFiscal.getChaveAcesso();
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
		model.put("formaPagamento", StringEscapeUtils.escapeHtml4(messageService.getValue("FormaPagamento." + formaPagamento.name() + ".label")));
		model.put("totalGeral", "R$ "+ DummyUtils.formatCurrency(DummyUtils.totalGeral(orcamentoProdutos)));
		model.put("statusNotaFiscal", StringEscapeUtils.escapeHtml4(messageService.getValue("StatusNotaFiscal." + statusNotaFiscal.name() + ".label")));
		model.put("chaveAcesso", chaveAcesso);

		PDFCreator pdfCreator = new PDFCreator("/net/gnfe/pdf/orcamento.htm", model);
		pdfCreator.setPortrait(true);
		File pdf = pdfCreator.toFile();

		return pdf;
	}

	public void enviarNotaFiscal(NotaFiscal notaFiscal, Usuario usuario) throws JAXBException, FileNotFoundException, NfeException, CertificadoException, InterruptedException {
		notaFiscalService.enviarNotaFiscal(notaFiscal, usuario);
	}

	public void cancelarNotaFiscal(NotaFiscal notaFiscal, Usuario usuario) throws JAXBException, FileNotFoundException, NfeException, CertificadoException {
		notaFiscalService.cancelarNotaFiscal(notaFiscal, usuario);
	}
}
