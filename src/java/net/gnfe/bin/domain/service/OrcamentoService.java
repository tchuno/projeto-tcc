package net.gnfe.bin.domain.service;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import br.com.swconsultoria.nfe.dom.enuns.ConsultaDFeEnum;
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import br.com.swconsultoria.nfe.dom.enuns.PessoaEnum;
import br.com.swconsultoria.nfe.dom.enuns.StatusEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema.retdistdfeint.RetDistDFeInt;
import br.com.swconsultoria.nfe.schema_4.enviNFe.*;
import br.com.swconsultoria.nfe.util.ChaveUtil;
import br.com.swconsultoria.nfe.util.ConstantesUtil;
import br.com.swconsultoria.nfe.util.RetornoUtil;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
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
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

	public static ConfiguracoesNfe iniciaConfiguracoes() throws FileNotFoundException, CertificadoException {

		Certificado certificado = certifidoA1Pfx();

		ConfiguracoesNfe configuracoesNfe = ConfiguracoesNfe.criarConfiguracoes(EstadosEnum.PR, AmbienteEnum.HOMOLOGACAO, certificado, "D:\\Desenvolvimento\\Schemas");
		return configuracoesNfe;

		/*//Efetua Consulta
		TRetConsStatServ retorno = Nfe.statusServico(config, DocumentoEnum.NFE);

		//Resultado
		System.out.println();
		System.out.println("# Status: " + retorno.getCStat() + " - " + retorno.getXMotivo());*/
	}

	private static Certificado certifidoA1Pfx() throws CertificadoException, FileNotFoundException {
		String caminhoCertificado = "D:/Desenvolvimento/cert.pfx";
		String senha = "phsv1414";

		return CertificadoService.certificadoPfx(caminhoCertificado, senha);
	}

	public static void main(String args[]) {

		try {
			// Inicia As Configurações - ver https://github.com/Samuel-Oliveira/Java_NFe/wiki/1-:-Configuracoes
			ConfiguracoesNfe config = iniciaConfiguracoes();

			//Informe o Numero da NFe
			int numeroNfe = 8;
			//Informe o CNPJ do Emitente da NFe
			String cnpj = "72245384000113";
			//Informe a data de Emissao da NFe
			LocalDateTime dataEmissao = LocalDateTime.now();
			//Informe o cnf da NFCe com 8 digitos
			String cnf = DummyUtils.gerarDigitosAleatorios(8);
			//Informe o modelo da NFe
			String modelo = DocumentoEnum.NFE.getModelo();
			//Informe a Serie da NFe
			int serie = 1;
			//Informe o tipo de Emissao da NFe
			String tipoEmissao = "1";

			// MontaChave a NFe
			ChaveUtil chaveUtil = new ChaveUtil(config.getEstado(), cnpj, modelo, serie, numeroNfe, tipoEmissao, cnf, dataEmissao);
			String chave = chaveUtil.getChaveNF();
			String cdv = chaveUtil.getDigitoVerificador();

			TNFe.InfNFe infNFe = new TNFe.InfNFe();
			infNFe.setId(chave);
			infNFe.setVersao(ConstantesUtil.VERSAO.NFE);

			TInfRespTec infRespTec = new TInfRespTec();
			infRespTec.setCNPJ("72245384000113");
			infRespTec.setEmail("teste@teste.com");
			infRespTec.setXContato("Contato Resp Tec");
			infRespTec.setFone("4199999999");
			infNFe.setInfRespTec(infRespTec);

			//Preenche IDE
			infNFe.setIde(preencheIde(config, cnf, numeroNfe, tipoEmissao, modelo, serie, cdv, dataEmissao));

			//Preenche Emitente
			infNFe.setEmit(preencheEmitente(config, cnpj));

			//Preenche o Destinatario
			infNFe.setDest(preencheDestinatario());

			//Preenche os dados do Produto da Nfe e adiciona a Lista
			infNFe.getDet().addAll(preencheDet());

			//Preenche totais da NFe
			infNFe.setTotal(preencheTotal());

			//Preenche os dados de Transporte
			infNFe.setTransp(preencheTransporte());

			// Preenche dados Pagamento
			infNFe.setPag(preenchePag());

			TNFe nfe = new TNFe();
			nfe.setInfNFe(infNFe);

			// Monta EnviNfe
			TEnviNFe enviNFe = new TEnviNFe();
			enviNFe.setVersao(ConstantesUtil.VERSAO.NFE);
			enviNFe.setIdLote("1");
			enviNFe.setIndSinc("1");
			enviNFe.getNFe().add(nfe);

			// Monta e Assina o XML
			enviNFe = Nfe.montaNfe(config, enviNFe, true);

			// Envia a Nfe para a Sefaz
			TRetEnviNFe retorno = Nfe.enviarNfe(config, enviNFe, DocumentoEnum.NFE);

			//Valida se o Retorno é Assincrono
			if (RetornoUtil.isRetornoAssincrono(retorno)) {
				//Pega o Recibo
				String recibo = retorno.getInfRec().getNRec();
				int tentativa = 0;
				br.com.swconsultoria.nfe.schema_4.retConsReciNFe.TRetConsReciNFe retornoNfe = null;

				//Define Numero de tentativas que irá tentar a Consulta
				while (tentativa < 15) {
					retornoNfe = Nfe.consultaRecibo(config, recibo, DocumentoEnum.NFE);
					if (retornoNfe.getCStat().equals(StatusEnum.LOTE_EM_PROCESSAMENTO.getCodigo())) {
						System.out.println("INFO: Lote Em Processamento, vai tentar novamente apos 1 Segundo.");
						Thread.sleep(1000);
						tentativa++;
					} else {
						break;
					}
				}

				RetornoUtil.validaAssincrono(retornoNfe);
				System.out.println();
				System.out.println("# Status: " + retornoNfe.getProtNFe().get(0).getInfProt().getCStat() + " - " + retornoNfe.getProtNFe().get(0).getInfProt().getXMotivo());
				System.out.println("# Protocolo: " + retornoNfe.getProtNFe().get(0).getInfProt().getNProt());
				System.out.println("# XML Final: " + XmlNfeUtil.criaNfeProc(enviNFe, retornoNfe.getProtNFe().get(0)));

			} else {
				//Se for else o Retorno é Sincrono

				//Valida Retorno Sincrono
				RetornoUtil.validaSincrono(retorno);
				System.out.println();
				System.out.println("# Status: " + retorno.getProtNFe().getInfProt().getCStat()  + " - " + retorno.getProtNFe().getInfProt().getXMotivo());
				System.out.println("# Protocolo: " + retorno.getProtNFe().getInfProt().getNProt());
				System.out.println("# Xml Final :" + XmlNfeUtil.criaNfeProc(enviNFe, retorno.getProtNFe()));
			}

		} catch (Exception e) {
			System.err.println();
			System.err.println("# Erro: " + e.getMessage());
		}

	}

	/**
	 * Preenche o IDE
	 * @param config
	 * @param cnf
	 * @param numeroNfe
	 * @param tipoEmissao
	 * @param cDv
	 * @param dataEmissao
	 * @return
	 * @throws NfeException
	 */
	private static TNFe.InfNFe.Ide preencheIde(ConfiguracoesNfe config, String cnf, int numeroNfe, String tipoEmissao, String modelo, int serie, String cDv, LocalDateTime dataEmissao) throws NfeException {
		TNFe.InfNFe.Ide ide = new TNFe.InfNFe.Ide();
		ide.setCUF(config.getEstado().getCodigoUF());
		ide.setCNF(cnf);
		ide.setNatOp("NOTA FISCAL CONSUMIDOR ELETRONICA");
		ide.setMod(modelo);
		ide.setSerie(String.valueOf(serie));

		ide.setNNF(String.valueOf(numeroNfe));
		ide.setDhEmi(XmlNfeUtil.dataNfe(dataEmissao));
		ide.setTpNF("1");
		ide.setIdDest("1");
		ide.setCMunFG("4106902");
		ide.setTpImp("3");
		ide.setTpEmis(tipoEmissao);
		ide.setCDV(cDv);
		ide.setTpAmb(config.getAmbiente().getCodigo());
		ide.setFinNFe("1");
		ide.setIndFinal("1");
		ide.setIndPres("1");
		ide.setProcEmi("0");
		ide.setVerProc("1.20");

		return ide;
	}

	/**
	 * Preenche o Emitente da Nfe
	 * @param config
	 * @param cnpj
	 * @return
	 */
	private static TNFe.InfNFe.Emit preencheEmitente(ConfiguracoesNfe config, String cnpj) {
		TNFe.InfNFe.Emit emit = new TNFe.InfNFe.Emit();
		emit.setCNPJ(cnpj);
		emit.setXNome("PHSV DISTRIBUIDORA DE MATERIAIS DE CONSTRUCAO E FERRAGENS LT");

		TEnderEmi enderEmit = new TEnderEmi();
		enderEmit.setXLgr("RUA ELIAS JOAQUIM");
		enderEmit.setNro("182");
		enderEmit.setXBairro("PILARZINHO");
		enderEmit.setCMun("4106902");
		enderEmit.setXMun("CURITIBA");
		enderEmit.setUF(TUfEmi.valueOf(config.getEstado().toString()));
		enderEmit.setCEP("82120350");
		enderEmit.setCPais("1058");
		enderEmit.setXPais("Brasil");
		enderEmit.setFone("4199424484");
		emit.setEnderEmit(enderEmit);

		emit.setIE("9085551393");
		emit.setCRT("3");

		return emit;
	}

	/**
	 * Preenche o Destinatario da NFe
	 * @return
	 */
	private static TNFe.InfNFe.Dest preencheDestinatario() {
		TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();
		dest.setCPF("10735961921");
		dest.setXNome("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");

		TEndereco enderDest = new TEndereco();
		enderDest.setXLgr("Rua: Teste");
		enderDest.setNro("0");
		enderDest.setXBairro("TESTE");
		enderDest.setCMun("4106902");
		enderDest.setXMun("CURITIBA");
		enderDest.setUF(TUf.valueOf("PR"));
		enderDest.setCEP("84900000");
		enderDest.setCPais("1058");
		enderDest.setXPais("Brasil");
		enderDest.setFone("4845454545");
		dest.setEnderDest(enderDest);
		dest.setEmail("guialveser@gmail.com");
		dest.setIndIEDest("9");
		return dest;
	}

	/**
	 * Preenche Det Nfe
	 */
	private static List<TNFe.InfNFe.Det> preencheDet() {

		//O Preenchimento deve ser feito por produto, Então deve ocorrer uma LIsta
		TNFe.InfNFe.Det det = new TNFe.InfNFe.Det();
		//O numero do Item deve seguir uma sequencia
		det.setNItem("1");

		// Preenche dados do Produto
		det.setProd(preencheProduto());

		//Preenche dados do Imposto
		det.setImposto(preencheImposto());

		//Retorna a Lista de Det
		return Collections.singletonList(det);
	}

	/**
	 * Preenche dados do Produto
	 * @return
	 */
	private static TNFe.InfNFe.Det.Prod preencheProduto() {
		TNFe.InfNFe.Det.Prod prod = new TNFe.InfNFe.Det.Prod();
		prod.setCProd("0113AKRE306");
		prod.setCEAN("SEM GTIN");
		prod.setXProd("KIT REVISAO HOP - 12");
		prod.setNCM("87141000");
		prod.setCEST("0107600");
		prod.setCFOP("5405");
		prod.setUCom("UN");
		prod.setQCom("1.0000");
		prod.setVUnCom("13.0000");
		prod.setVProd("13.00");
		prod.setCEANTrib("SEM GTIN");
		prod.setUTrib("UN");
		prod.setQTrib("1.0000");
		prod.setVUnTrib("13.0000");
		prod.setIndTot("1");

		return prod;
	}

	/**
	 * Preenche dados do Imposto da Nfe
	 * @return
	 */
	private static TNFe.InfNFe.Det.Imposto preencheImposto() {
		TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();

		TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();

		TNFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS00();
		icms00.setOrig("0");
		icms00.setCST("00");
		icms00.setModBC("0");
		icms00.setVBC("13.00");
		icms00.setPICMS("7.00");
		icms00.setVICMS("0.91");

		icms.setICMS00(icms00);

		TNFe.InfNFe.Det.Imposto.PIS pis = new TNFe.InfNFe.Det.Imposto.PIS();
		TNFe.InfNFe.Det.Imposto.PIS.PISAliq pisAliq = new TNFe.InfNFe.Det.Imposto.PIS.PISAliq();
		pisAliq.setCST("01");
		pisAliq.setVBC("13.00");
		pisAliq.setPPIS("1.65");
		pisAliq.setVPIS("0.21");
		pis.setPISAliq(pisAliq);

		TNFe.InfNFe.Det.Imposto.COFINS cofins = new TNFe.InfNFe.Det.Imposto.COFINS();
		TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsAliq = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq();
		cofinsAliq.setCST("01");
		cofinsAliq.setVBC("13.00");
		cofinsAliq.setPCOFINS("7.60");
		cofinsAliq.setVCOFINS("0.99");
		cofins.setCOFINSAliq(cofinsAliq);

		imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoICMS(icms));
		imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoPIS(pis));
		imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoCOFINS(cofins));

		return imposto;
	}

	/**
	 * Prenche Total NFe
	 * @return
	 */
	private static TNFe.InfNFe.Total preencheTotal() {
		TNFe.InfNFe.Total total = new TNFe.InfNFe.Total();
		TNFe.InfNFe.Total.ICMSTot icmstot = new TNFe.InfNFe.Total.ICMSTot();
		icmstot.setVBC("13.00");
		icmstot.setVICMS("0.91");
		icmstot.setVICMSDeson("0.00");
		icmstot.setVFCP("0.00");
		icmstot.setVFCPST("0.00");
		icmstot.setVFCPSTRet("0.00");
		icmstot.setVBCST("0.00");
		icmstot.setVST("0.00");
		icmstot.setVProd("13.00");
		icmstot.setVFrete("0.00");
		icmstot.setVSeg("0.00");
		icmstot.setVDesc("0.00");
		icmstot.setVII("0.00");
		icmstot.setVIPI("0.00");
		icmstot.setVIPIDevol("0.00");
		icmstot.setVPIS("0.21");
		icmstot.setVCOFINS("0.99");
		icmstot.setVOutro("0.00");
		icmstot.setVNF("13.00");
		total.setICMSTot(icmstot);

		return total;
	}

	/**
	 * Preenche Transporte
	 * @return
	 */
	private static TNFe.InfNFe.Transp preencheTransporte(){
		TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
		transp.setModFrete("9");
		return transp;
	}

	/**
	 * Preenche dados Pagamento
	 * @return
	 */
	private static TNFe.InfNFe.Pag preenchePag() {
		TNFe.InfNFe.Pag pag = new TNFe.InfNFe.Pag();
		TNFe.InfNFe.Pag.DetPag detPag = new TNFe.InfNFe.Pag.DetPag();
		detPag.setTPag("01");
		detPag.setVPag("13.00");
		pag.getDetPag().add(detPag);

		return pag;
	}

	public static void consultarXmlNota() {
		try {
			// Inicia As Configurações - ver https://github.com/Samuel-Oliveira/Java_NFe/wiki/1-:-Configuracoes
			ConfiguracoesNfe config = iniciaConfiguracoes();

			//Informe o CNPJ Do Destinatario (Deve ser o Mesmo do Certificado)
			String cnpj = "72245384000113";

			RetDistDFeInt retorno;

			//Para Consulta Via CHAVE
			String chave = "41221272245384000113550110000254011444554450";
			retorno = Nfe.distribuicaoDfe(config, PessoaEnum.JURIDICA, cnpj, ConsultaDFeEnum.CHAVE, chave);


			if (StatusEnum.DOC_LOCALIZADO_PARA_DESTINATARIO.getCodigo().equals(retorno.getCStat())) {
				System.out.println();
				System.out.println("# Status: " + retorno.getCStat() + " - " + retorno.getXMotivo());
				System.out.println("# NSU Atual: " + retorno.getUltNSU());
				System.out.println("# Max NSU: " + retorno.getMaxNSU());
				System.out.println("# Max NSU: " + retorno.getMaxNSU());

				//Aqui Recebe a Lista De XML (No Maximo 50 por Consulta)
				List<RetDistDFeInt.LoteDistDFeInt.DocZip> listaDoc = retorno.getLoteDistDFeInt().getDocZip();
				for (RetDistDFeInt.LoteDistDFeInt.DocZip docZip : listaDoc) {
					System.out.println();
					System.out.println("# Schema: " + docZip.getSchema());
					switch (docZip.getSchema()) {
						case "resNFe_v1.01.xsd":
							System.out.println("# Este é o XML em resumo, deve ser feito a Manifestação para o Objeter o XML Completo.");
							break;
						case "procNFe_v4.00.xsd":
							System.out.println("# XML Completo.");
							break;
						case "procEventoNFe_v1.00.xsd":
							System.out.println("# XML Evento.");
							break;
					}
					//Transforma o GZip em XML
					String xml = XmlNfeUtil.gZipToXml(docZip.getValue());
					System.out.println("# XML: " + xml);
				}
			} else {
				System.out.println();
				System.out.println("# Status: " + retorno.getCStat() + " - " + retorno.getXMotivo());
			}
		} catch (Exception e) {
			System.err.println();
			System.err.println("# Erro: "+e.getMessage());
		}
	}

}
