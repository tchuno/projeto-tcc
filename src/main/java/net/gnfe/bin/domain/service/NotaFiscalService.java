package net.gnfe.bin.domain.service;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.Evento;
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import br.com.swconsultoria.nfe.dom.enuns.StatusEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema.envEventoCancNFe.TEnvEvento;
import br.com.swconsultoria.nfe.schema.envEventoCancNFe.TRetEnvEvento;
import br.com.swconsultoria.nfe.schema_4.enviNFe.*;
import br.com.swconsultoria.nfe.util.CancelamentoUtil;
import br.com.swconsultoria.nfe.util.ChaveUtil;
import br.com.swconsultoria.nfe.util.ConstantesUtil;
import br.com.swconsultoria.nfe.util.RetornoUtil;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
import net.gnfe.bin.GNFEConstants;
import net.gnfe.bin.domain.entity.NotaFiscal;
import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.MotivoMovimentacao;
import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;
import net.gnfe.bin.domain.repository.NotaFiscalRepository;
import net.gnfe.bin.domain.vo.MovimentacaoProdutoVO;
import net.gnfe.bin.domain.vo.TotalNotaFiscalVO;
import net.gnfe.bin.domain.vo.filtro.NotaFiscalFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class NotaFiscalService {

	@Autowired private ParametroService parametroService;
	@Autowired private NotaFiscalRepository notaFiscalRepository;
	@Autowired private MovimentacaoProdutoService movimentacaoProdutoService;

	public NotaFiscal get(Long id) {
		return notaFiscalRepository.get(id);
	}

	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(NotaFiscal entity) throws MessageKeyException {
		try {
			notaFiscalRepository.saveOrUpdate(entity);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long id) throws MessageKeyException {
		try {
			notaFiscalRepository.deleteById(id);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<NotaFiscal> findByFiltro(NotaFiscalFiltro filtro) {
		return notaFiscalRepository.findByFiltro(filtro, null, null);
	}

	public List<NotaFiscal> findByFiltro(NotaFiscalFiltro filtro, Integer inicio, Integer max) {
		return notaFiscalRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(NotaFiscalFiltro filtro) {
		return notaFiscalRepository.countByFiltro(filtro);
	}

	private ConfiguracoesNfe iniciaConfiguracoes() throws FileNotFoundException, CertificadoException {
		Map<String, String> customizacao = parametroService.getCustomizacao();

		String caminhoCertificado = customizacao.get(ParametroService.P.CAMINHO_CERTIFICADO.name());
		String senha = customizacao.get(ParametroService.P.SENHA.name());
		String caminhoSchemas = customizacao.get(ParametroService.P.CAMINHO_SCHEMAS.name());
		String ambiente = customizacao.get(ParametroService.P.AMBIENTE.name());

		AmbienteEnum ambienteEnum = AmbienteEnum.HOMOLOGACAO;
		if(ambiente.equals("PROD")){
			ambienteEnum = AmbienteEnum.PRODUCAO;
		}

		Certificado certificado = CertificadoService.certificadoPfx(caminhoCertificado, senha);

		return ConfiguracoesNfe.criarConfiguracoes(EstadosEnum.PR, ambienteEnum, certificado, caminhoSchemas);
	}

	public void enviarNotaFiscal(NotaFiscal notaFiscal) throws JAXBException, FileNotFoundException, NfeException, CertificadoException, InterruptedException {

		validarDados(notaFiscal);

		try {
			Map<String, String> customizacao = parametroService.getCustomizacao();

			ConfiguracoesNfe config = iniciaConfiguracoes();

			Long numeroNfe = notaFiscal.getId();
			int numeroNfeInt = numeroNfe.intValue();
			String cnpj = DummyUtils.getCpfCnpjDesformatado(customizacao.get(ParametroService.P.CNPJ.name()));
			LocalDateTime dataEmissao = LocalDateTime.now();
			String cnf = DummyUtils.gerarDigitosAleatorios(8);

			String modelo = DocumentoEnum.NFE.getModelo();
			//TODO VERITIFCAR SE numeroNfe para gerar numero de serie de acordo com a divisão de ID por 9999;
			int serie = 1;
			//TODO entender melhor o tipo de missão com danfe
			//1 = Emissão normal (não em contingência)
			//2 = Contingência FS-IA, com impressão do DANFE em formulário de segurança
			String tipoEmissao = "1";

			// MontaChave a NFe
			ChaveUtil chaveUtil = new ChaveUtil(config.getEstado(), cnpj, modelo, serie, numeroNfeInt, tipoEmissao, cnf, dataEmissao);
			String chave = chaveUtil.getChaveNF();
			String cdv = chaveUtil.getDigitoVerificador();

			TNFe.InfNFe infNFe = new TNFe.InfNFe();
			infNFe.setId(chave);
			infNFe.setVersao(ConstantesUtil.VERSAO.NFE);

			preencheIDE(customizacao, config, numeroNfeInt, dataEmissao, cnf, modelo, serie, tipoEmissao, cdv, infNFe);

			preencheEmitente(customizacao, config, cnpj, infNFe);

			Orcamento orcamento = notaFiscal.getOrcamento();
			preencheDestinatario(config.getAmbiente(), infNFe, orcamento);

			TotalNotaFiscalVO totalNotaFiscalVO = preencheProdutos(infNFe, orcamento);

			preencheTotaisNfe(infNFe, totalNotaFiscalVO);

			preencheFrete(infNFe);

			preencheDadosPagamento(infNFe, orcamento, totalNotaFiscalVO);

			preencheInfRespTec(customizacao, infNFe);

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
			notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.PROCESSANDO);
			saveOrUpdate(notaFiscal);

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
				notaFiscal.setProtocolo(retornoNfe.getProtNFe().get(0).getInfProt().getNProt());
				notaFiscal.setChaveAcesso(retornoNfe.getProtNFe().get(0).getInfProt().getChNFe());
				notaFiscal.setXml(XmlNfeUtil.criaNfeProc(enviNFe, retornoNfe.getProtNFe().get(0)));

			} else {
				//Se for else o Retorno é Sincrono

				//Valida Retorno Sincrono
				RetornoUtil.validaSincrono(retorno);
				System.out.println();
				System.out.println("# Status: " + retorno.getProtNFe().getInfProt().getCStat()  + " - " + retorno.getProtNFe().getInfProt().getXMotivo());
				System.out.println("# Protocolo: " + retorno.getProtNFe().getInfProt().getNProt());
				System.out.println("# Xml Final :" + XmlNfeUtil.criaNfeProc(enviNFe, retorno.getProtNFe()));
				notaFiscal.setProtocolo(retorno.getProtNFe().getInfProt().getNProt());
				notaFiscal.setChaveAcesso(retorno.getProtNFe().getInfProt().getChNFe());
				notaFiscal.setXml(XmlNfeUtil.criaNfeProc(enviNFe, retorno.getProtNFe()));
			}

			Date convertedDatetime = Date.from(dataEmissao.atZone(ZoneId.systemDefault()).toInstant());
			notaFiscal.setDataEnvio(convertedDatetime);
			notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.CONCLUIDO);
			saveOrUpdate(notaFiscal);

			MovimentacaoProdutoVO vo = new MovimentacaoProdutoVO();
			vo.setData(convertedDatetime);
			vo.setOrcamento(orcamento);
			vo.setMotivoMovimentacao(MotivoMovimentacao.NOTA_FISCAL_CONCLUIDA);
			vo.setValorTotal(totalNotaFiscalVO.getValorTotal());
			vo.setEntrada(false);
			movimentacaoProdutoService.movimentarProduto(vo);

		} catch (Exception e) {
			notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.ERRO);
			saveOrUpdate(notaFiscal);
			System.err.println();
			System.err.println("# Erro: " + e.getMessage());
			throw e;
		}
	}

	private void validarDados(NotaFiscal notaFiscal) {
		Orcamento orcamento = notaFiscal.getOrcamento();
		Usuario cliente = orcamento.getCliente();
		Set<OrcamentoProduto> orcamentoProdutos = orcamento.getOrcamentoProdutos();

		if(cliente == null || (orcamentoProdutos == null || orcamentoProdutos.isEmpty())) {
			throw new MessageKeyException("Orçamento não foi preenchido corretamente, favor informar cliente e produto(s) antes de enviar a nota fiscal.");
		}
	}

	private void preencheIDE(Map<String, String> customizacao, ConfiguracoesNfe config, int numeroNfeInt, LocalDateTime dataEmissao, String cnf, String modelo, int serie, String tipoEmissao, String cdv, TNFe.InfNFe infNFe) {
		//Preenche IDE
		TNFe.InfNFe.Ide ide = new TNFe.InfNFe.Ide();
		ide.setCUF(config.getEstado().getCodigoUF());
		ide.setCNF(cnf);
		ide.setNatOp(customizacao.get(ParametroService.P.NAT_OP.name()));
		ide.setMod(modelo);
		ide.setSerie(String.valueOf(serie));
		ide.setNNF(String.valueOf(numeroNfeInt));
		ide.setDhEmi(XmlNfeUtil.dataNfe(dataEmissao));
		ide.setTpNF("1");
		ide.setIdDest("1");
		ide.setCMunFG(customizacao.get(ParametroService.P.COD_MUNICIPIO.name()));
		ide.setTpImp("1");
		ide.setTpEmis(tipoEmissao);
		ide.setCDV(cdv);
		ide.setTpAmb(config.getAmbiente().getCodigo());
		ide.setFinNFe("1");
		ide.setIndFinal("1");
		ide.setIndPres("1");
		ide.setProcEmi("0");
		ide.setVerProc("1.20");
		infNFe.setIde(ide);
	}

	private void preencheEmitente(Map<String, String> customizacao, ConfiguracoesNfe config, String cnpj, TNFe.InfNFe infNFe) {
		//Preenche Emitente
		TNFe.InfNFe.Emit emit = new TNFe.InfNFe.Emit();
		emit.setCNPJ(cnpj);
		emit.setXNome(customizacao.get(ParametroService.P.NOME.name()));
		emit.setIE(customizacao.get(ParametroService.P.INSCRICAO_ESTADUAL.name()));
		emit.setCRT("3");

		TEnderEmi enderEmit = new TEnderEmi();
		enderEmit.setXLgr(customizacao.get(ParametroService.P.LOGRADOURO.name()));
		enderEmit.setNro(customizacao.get(ParametroService.P.NUMERO.name()));
		enderEmit.setXBairro(customizacao.get(ParametroService.P.BAIRRO.name()));
		enderEmit.setCMun(customizacao.get(ParametroService.P.COD_MUNICIPIO.name()));
		enderEmit.setXMun(customizacao.get(ParametroService.P.MUNICIPIO.name()));
		enderEmit.setUF(TUfEmi.valueOf(config.getEstado().toString()));
		enderEmit.setCEP(DummyUtils.removerTracosPontosEspacoParentesesAspas(customizacao.get(ParametroService.P.CEP.name())));
		enderEmit.setCPais(customizacao.get(ParametroService.P.COD_PAIS.name()));
		enderEmit.setXPais(customizacao.get(ParametroService.P.PAIS.name()));
		enderEmit.setFone(DummyUtils.removerTracosPontosEspacoParentesesAspas(customizacao.get(ParametroService.P.TELEFONE_EMITENTE.name())));
		emit.setEnderEmit(enderEmit);

		infNFe.setEmit(emit);
	}

	private void preencheDestinatario(AmbienteEnum ambienteEnum, TNFe.InfNFe infNFe, Orcamento orcamento) {
		//Preenche o Destinatario
		Usuario cliente = orcamento.getCliente();
		String cpfCnpj = cliente.getCpfCnpj();
		String nome = AmbienteEnum.HOMOLOGACAO.equals(ambienteEnum) ? "NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL" : cliente.getNome();
		String email = cliente.getEmail();

		TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();
		dest.setCPF(cpfCnpj);
		dest.setXNome(nome);
		dest.setEmail(email);
		dest.setIndIEDest("9");

		String endereco = cliente.getEndereco();
		Integer numero = cliente.getNumero();
		String bairro = cliente.getBairro();
		String codIbge = cliente.getCodIbge();
		String cidade = cliente.getCidade();
		String cep = cliente.getCep();
		String estado = cliente.getEstado();
		String telefone = cliente.getTelefone();

		TEndereco enderDest = new TEndereco();
		enderDest.setXLgr(endereco);
		enderDest.setNro(String.valueOf(numero));
		enderDest.setXBairro(bairro);
		enderDest.setCMun(codIbge);
		enderDest.setXMun(cidade);
		enderDest.setUF(TUf.valueOf(estado));
		enderDest.setCEP(DummyUtils.removerTracosPontosEspacoParentesesAspas(cep));
		enderDest.setCPais("1058");
		enderDest.setXPais("Brasil");
		enderDest.setFone(DummyUtils.removerTracosPontosEspacoParentesesAspas(telefone));
		dest.setEnderDest(enderDest);

		infNFe.setDest(dest);
	}

	private TotalNotaFiscalVO preencheProdutos(TNFe.InfNFe infNFe, Orcamento orcamento) {
		//Preenche os dados do Produto da Nfe e adiciona a Lista
		//O Preenchimento deve ser feito por produto, Então deve ocorrer uma LIsta
		//O numero do Item deve seguir uma sequencia

		Set<OrcamentoProduto> orcamentoProdutos = orcamento.getOrcamentoProdutos();
		List<TNFe.InfNFe.Det> dets = new ArrayList<>();
		TotalNotaFiscalVO totalNotaFiscalVO = new TotalNotaFiscalVO();
		int nrItem = 1;
		for(OrcamentoProduto op : orcamentoProdutos) {
			TNFe.InfNFe.Det det = new TNFe.InfNFe.Det();
			det.setNItem(Integer.toString(nrItem));

			Produto produto = op.getProduto();
			TNFe.InfNFe.Det.Prod prod = new TNFe.InfNFe.Det.Prod();
			prod.setCProd(produto.getCod());
			prod.setCEAN("SEM GTIN");
			prod.setXProd(produto.getNome());
			prod.setNCM(DummyUtils.removerTracosPontosEspacoParentesesAspas(produto.getCnm()));
			prod.setCEST(DummyUtils.removerTracosPontosEspacoParentesesAspas(produto.getCest()));
			prod.setCFOP(produto.getCfop());
			prod.setUCom(produto.getUnidadeMedida().name());
			BigDecimal quantidade = new BigDecimal(op.getQuantidade());
			prod.setQCom(DummyUtils.formatarNumero(quantidade, GNFEConstants.DECIMAL_FORMAT_2));
			prod.setVUnCom(DummyUtils.formatarNumero(produto.getValorUnidade(), GNFEConstants.DECIMAL_FORMAT_2));

			BigDecimal valorUnidade = produto.getValorUnidade();
			BigDecimal vProd = valorUnidade.multiply(quantidade);
			BigDecimal totalVProd = totalNotaFiscalVO.getValorTotal();
			totalVProd = totalVProd.add(vProd);
			totalNotaFiscalVO.setValorTotal(totalVProd);

			prod.setVProd(DummyUtils.formatarNumero(vProd, GNFEConstants.DECIMAL_FORMAT));
			prod.setCEANTrib("SEM GTIN");
			prod.setUTrib(produto.getUnidadeMedida().name());
			prod.setQTrib(DummyUtils.formatarNumero(quantidade, GNFEConstants.DECIMAL_FORMAT_2));
			prod.setVUnTrib(DummyUtils.formatarNumero(produto.getValorUnidade(), GNFEConstants.DECIMAL_FORMAT_2));
			prod.setIndTot("1");
			det.setProd(prod);


			//Preenche dados do Imposto
			TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();

			TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();

			TNFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS00();
			icms00.setOrig(produto.getOrigemMercadoria().getTipo());
			icms00.setCST("00");
			icms00.setModBC("0");
			icms00.setVBC(DummyUtils.formatarNumero(vProd, GNFEConstants.DECIMAL_FORMAT));
			icms00.setPICMS(DummyUtils.formatarNumero(produto.getAliquotaICMS(), GNFEConstants.DECIMAL_FORMAT));

			BigDecimal aliquotaICMS = produto.getAliquotaICMS();
			aliquotaICMS = aliquotaICMS == null ? new BigDecimal(BigInteger.ZERO) : aliquotaICMS;
			BigDecimal valorICMS = vProd.multiply(aliquotaICMS).divide(new BigDecimal(100));

			icms00.setVICMS(DummyUtils.formatarNumero(valorICMS, GNFEConstants.DECIMAL_FORMAT));
			icms.setICMS00(icms00);

			TNFe.InfNFe.Det.Imposto.PIS pis = new TNFe.InfNFe.Det.Imposto.PIS();
			TNFe.InfNFe.Det.Imposto.PIS.PISAliq pisAliq = new TNFe.InfNFe.Det.Imposto.PIS.PISAliq();
			pisAliq.setCST("01");
			pisAliq.setVBC(DummyUtils.formatarNumero(vProd, GNFEConstants.DECIMAL_FORMAT));
			pisAliq.setPPIS(DummyUtils.formatarNumero(produto.getAliquotaPIS(), GNFEConstants.DECIMAL_FORMAT));

			BigDecimal aliquotaPIS = produto.getAliquotaPIS();
			aliquotaPIS = aliquotaPIS == null ? new BigDecimal(BigInteger.ZERO) : aliquotaPIS;
			BigDecimal valorPIS = vProd.multiply(aliquotaPIS).divide(new BigDecimal(100));

			pisAliq.setVPIS(DummyUtils.formatarNumero(valorPIS, GNFEConstants.DECIMAL_FORMAT));
			pis.setPISAliq(pisAliq);

			BigDecimal valorPISTotal = totalNotaFiscalVO.getvPIS();
			BigDecimal addPIS = valorPISTotal.add(valorPIS);
			totalNotaFiscalVO.setvPIS(addPIS);

			TNFe.InfNFe.Det.Imposto.COFINS cofins = new TNFe.InfNFe.Det.Imposto.COFINS();
			TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsAliq = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq();
			cofinsAliq.setCST("01");
			cofinsAliq.setVBC(DummyUtils.formatarNumero(vProd, GNFEConstants.DECIMAL_FORMAT));
			cofinsAliq.setPCOFINS(DummyUtils.formatarNumero(produto.getAliquotaCOFINS(), GNFEConstants.DECIMAL_FORMAT));

			BigDecimal aliquotaCOFINS = produto.getAliquotaCOFINS();
			aliquotaCOFINS = aliquotaCOFINS == null ? new BigDecimal(BigInteger.ZERO) : aliquotaCOFINS;
			BigDecimal valorCOFINS = vProd.multiply(aliquotaCOFINS).divide(new BigDecimal(100));

			cofinsAliq.setVCOFINS(DummyUtils.formatarNumero(valorCOFINS, GNFEConstants.DECIMAL_FORMAT));
			cofins.setCOFINSAliq(cofinsAliq);

			BigDecimal valorCOFINSTotal = totalNotaFiscalVO.getvCOFINS();
			BigDecimal addCOFINS = valorCOFINSTotal.add(valorCOFINS);
			totalNotaFiscalVO.setvCOFINS(addCOFINS);

			imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoICMS(icms));
			imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoPIS(pis));
			imposto.getContent().add(new ObjectFactory().createTNFeInfNFeDetImpostoCOFINS(cofins));
			det.setImposto(imposto);

			dets.add(det);
			nrItem++;
		}

		infNFe.getDet().addAll(dets);
		return totalNotaFiscalVO;
	}

	private void preencheTotaisNfe(TNFe.InfNFe infNFe, TotalNotaFiscalVO totalNotaFiscalVO) {
		//Preenche totais da NFe
		TNFe.InfNFe.Total total = new TNFe.InfNFe.Total();
		TNFe.InfNFe.Total.ICMSTot icmstot = new TNFe.InfNFe.Total.ICMSTot();
		icmstot.setVBC(DummyUtils.formatarNumero(totalNotaFiscalVO.getValorTotal(), GNFEConstants.DECIMAL_FORMAT));

		BigDecimal aliquotaICMS = new BigDecimal(BigInteger.ZERO);
		BigDecimal valorICMS = totalNotaFiscalVO.getValorTotal().multiply(aliquotaICMS).divide(new BigDecimal(100));

		icmstot.setVICMS(DummyUtils.formatarNumero(valorICMS, GNFEConstants.DECIMAL_FORMAT));
		icmstot.setVICMSDeson("0.00");
		icmstot.setVFCP("0.00");
		icmstot.setVFCPST("0.00");
		icmstot.setVFCPSTRet("0.00");
		icmstot.setVBCST("0.00");
		icmstot.setVST("0.00");
		icmstot.setVProd(DummyUtils.formatarNumero(totalNotaFiscalVO.getValorTotal(), GNFEConstants.DECIMAL_FORMAT));
		icmstot.setVFrete("0.00");
		icmstot.setVSeg("0.00");
		icmstot.setVDesc("0.00");
		icmstot.setVII("0.00");
		icmstot.setVIPI("0.00");
		icmstot.setVIPIDevol("0.00");
		icmstot.setVPIS(DummyUtils.formatarNumero(totalNotaFiscalVO.getvPIS(), GNFEConstants.DECIMAL_FORMAT));
		icmstot.setVCOFINS(DummyUtils.formatarNumero(totalNotaFiscalVO.getvCOFINS(), GNFEConstants.DECIMAL_FORMAT));
		icmstot.setVOutro("0.00");
		icmstot.setVNF(DummyUtils.formatarNumero(totalNotaFiscalVO.getValorTotal(), GNFEConstants.DECIMAL_FORMAT));
		total.setICMSTot(icmstot);
		infNFe.setTotal(total);
	}

	private void preencheFrete(TNFe.InfNFe infNFe) {
		//Preenche os dados de Transporte
		TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
		transp.setModFrete("9");
		infNFe.setTransp(transp);
	}

	private void preencheDadosPagamento(TNFe.InfNFe infNFe, Orcamento orcamento, TotalNotaFiscalVO totalNotaFiscalVO) {
		// Preenche dados Pagamento
		TNFe.InfNFe.Pag pag = new TNFe.InfNFe.Pag();
		TNFe.InfNFe.Pag.DetPag detPag = new TNFe.InfNFe.Pag.DetPag();
		detPag.setTPag(orcamento.getFormaPagamento().getTipo());
		detPag.setVPag(DummyUtils.formatarNumero(totalNotaFiscalVO.getValorTotal(), GNFEConstants.DECIMAL_FORMAT));
		pag.getDetPag().add(detPag);
		infNFe.setPag(pag);
	}

	private void preencheInfRespTec(Map<String, String> customizacao, TNFe.InfNFe infNFe) {
		// Preenche Informação Responsável Técnico
		TInfRespTec infRespTec = new TInfRespTec();
		infRespTec.setCNPJ(DummyUtils.getCpfCnpjDesformatado(customizacao.get(ParametroService.P.CNPJ_RESP_TEC.name())));
		infRespTec.setEmail(customizacao.get(ParametroService.P.EMAIL.name()));
		infRespTec.setXContato(customizacao.get(ParametroService.P.CONTATO.name()));
		infRespTec.setFone(DummyUtils.removerTracosPontosEspacoParentesesAspas(customizacao.get(ParametroService.P.TELEFONE.name())));
		infNFe.setInfRespTec(infRespTec);
	}

	public void cancelarNotaFiscal(NotaFiscal notaFiscal) throws JAXBException, FileNotFoundException, NfeException, CertificadoException {

		try {

			Map<String, String> customizacao = parametroService.getCustomizacao();

			ConfiguracoesNfe config = iniciaConfiguracoes();

			String chaveAcesso = notaFiscal.getChaveAcesso();
			String protocolo = notaFiscal.getProtocolo();
			String cnpj = customizacao.get(ParametroService.P.CNPJ.name());
			LocalDateTime data = LocalDateTime.now();

			//Agora o evento pode aceitar uma lista de cancelaemntos para envio em Lote.
			//Para isso Foi criado o Objeto Cancela
			Evento cancela = new Evento();
			//Informe a chave da Nota a ser Cancelada
			cancela.setChave(chaveAcesso);
			//Informe o protocolo da Nota a ser Cancelada
			cancela.setProtocolo(protocolo);
			//Informe o CNPJ do emitente
			cancela.setCnpj(DummyUtils.getCpfCnpjDesformatado(cnpj));
			//Informe o Motivo do Cancelamento
			cancela.setMotivo(MotivoMovimentacao.NOTA_FISCAL_CANCELADA.name());
			//Informe a data do Cancelamento
			cancela.setDataEvento(data);

			//Monta o Evento de Cancelamento
			TEnvEvento enviEvento = CancelamentoUtil.montaCancelamento(cancela, config);

			//Envia o Evento de Cancelamento
			TRetEnvEvento retorno = Nfe.cancelarNfe(config, enviEvento, true, DocumentoEnum.NFE);

			//Valida o Retorno do Cancelamento
			RetornoUtil.validaCancelamento(retorno);

			//Resultado
			System.out.println();
			retorno.getRetEvento().forEach( resultado -> {
				String protocoloCancelamento = resultado.getInfEvento().getNProt();
				String statusCancelamento = resultado.getInfEvento().getCStat() + " - " + resultado.getInfEvento().getXMotivo();
				System.out.println("# Chave: " + resultado.getInfEvento().getChNFe());
				System.out.println("# Status: " + statusCancelamento);
				System.out.println("# Protocolo: " + protocoloCancelamento);
				notaFiscal.setProtocoloCancelamento(protocoloCancelamento);
			});

			//Cria ProcEvento de Cancelamento
			String proc = CancelamentoUtil.criaProcEventoCancelamento(config, enviEvento, retorno.getRetEvento().get(0));
			System.out.println("# ProcEvento : " + proc);

			MovimentacaoProdutoVO vo = new MovimentacaoProdutoVO();

			Orcamento orcamento = notaFiscal.getOrcamento();
			Date dataTypeDate = Date.from(data.atZone(ZoneId.systemDefault()).toInstant());

			vo.setData(dataTypeDate);
			vo.setOrcamento(orcamento);
			vo.setMotivoMovimentacao(MotivoMovimentacao.NOTA_FISCAL_CANCELADA);
			vo.setEntrada(true);
			movimentacaoProdutoService.movimentarProduto(vo);

			notaFiscal.setXmlCancelamento(proc);
			notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.CANCELADO);
			notaFiscal.setDataCancelamento(dataTypeDate);
			saveOrUpdate(notaFiscal);

		} catch (Exception e) {
			System.err.println();
			System.err.println("# Erro: "+e.getMessage());
			throw e;
		}
	}
}