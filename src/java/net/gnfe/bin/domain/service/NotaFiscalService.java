package net.gnfe.bin.domain.service;

import br.com.swconsultoria.certificado.Certificado;
import br.com.swconsultoria.certificado.CertificadoService;
import br.com.swconsultoria.certificado.exception.CertificadoException;
import br.com.swconsultoria.nfe.Nfe;
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe;
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum;
import br.com.swconsultoria.nfe.dom.enuns.DocumentoEnum;
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum;
import br.com.swconsultoria.nfe.dom.enuns.StatusEnum;
import br.com.swconsultoria.nfe.exception.NfeException;
import br.com.swconsultoria.nfe.schema_4.enviNFe.*;
import br.com.swconsultoria.nfe.util.ChaveUtil;
import br.com.swconsultoria.nfe.util.ConstantesUtil;
import br.com.swconsultoria.nfe.util.RetornoUtil;
import br.com.swconsultoria.nfe.util.XmlNfeUtil;
import net.gnfe.bin.domain.entity.NotaFiscal;
import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;
import net.gnfe.bin.domain.repository.NotaFiscalRepository;
import net.gnfe.bin.domain.vo.filtro.NotaFiscalFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
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

	public void enviarNotaFiscal(NotaFiscal notaFiscal) throws JAXBException, FileNotFoundException, NfeException, CertificadoException, InterruptedException {

		try {
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

			ConfiguracoesNfe config = ConfiguracoesNfe.criarConfiguracoes(EstadosEnum.PR, ambienteEnum, certificado, caminhoSchemas);

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
			ide.setTpImp("3");
			ide.setTpEmis(tipoEmissao);
			ide.setCDV(cdv);
			ide.setTpAmb(config.getAmbiente().getCodigo());
			ide.setFinNFe("1");
			ide.setIndFinal("1");
			ide.setIndPres("1");
			ide.setProcEmi("0");
			ide.setVerProc("1.20");
			infNFe.setIde(ide);

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

			//Preenche o Destinatario
			Orcamento orcamento = notaFiscal.getOrcamento();
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

			//Preenche os dados do Produto da Nfe e adiciona a Lista
			//O Preenchimento deve ser feito por produto, Então deve ocorrer uma LIsta
			//O numero do Item deve seguir uma sequencia

			Set<OrcamentoProduto> orcamentoProdutos = orcamento.getOrcamentoProdutos();
			List<TNFe.InfNFe.Det> dets = new ArrayList<>();
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
				prod.setCFOP("5405");
				prod.setUCom(produto.getUnidadeMedida().name());
				prod.setQCom("1.0000");
				prod.setVUnCom("13.0000");
				prod.setVProd("13.00");
				prod.setCEANTrib("SEM GTIN");
				prod.setUTrib(produto.getUnidadeMedida().name());
				prod.setQTrib("1.0000");
				prod.setVUnTrib("13.0000");
				prod.setIndTot("1");
				det.setProd(prod);

				//Preenche dados do Imposto
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
				det.setImposto(imposto);

				dets.add(det);
				nrItem++;
			}

			infNFe.getDet().addAll(dets);

			//Preenche totais da NFe
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
			infNFe.setTotal(total);

			//Preenche os dados de Transporte
			TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
			transp.setModFrete("9");
			infNFe.setTransp(transp);

			// Preenche dados Pagamento
			TNFe.InfNFe.Pag pag = new TNFe.InfNFe.Pag();
			TNFe.InfNFe.Pag.DetPag detPag = new TNFe.InfNFe.Pag.DetPag();
			detPag.setTPag("01");
			detPag.setVPag("13.00");
			pag.getDetPag().add(detPag);
			infNFe.setPag(pag);

			// Preenche Informação Responsável Técnico
			TInfRespTec infRespTec = new TInfRespTec();
			infRespTec.setCNPJ(DummyUtils.getCpfCnpjDesformatado(customizacao.get(ParametroService.P.CNPJ_RESP_TEC.name())));
			infRespTec.setEmail(customizacao.get(ParametroService.P.EMAIL.name()));
			infRespTec.setXContato(customizacao.get(ParametroService.P.CONTATO.name()));
			infRespTec.setFone(DummyUtils.removerTracosPontosEspacoParentesesAspas(customizacao.get(ParametroService.P.TELEFONE.name())));
			infNFe.setInfRespTec(infRespTec);

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

		} catch (Exception e) {
			notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.ERRO);
			saveOrUpdate(notaFiscal);
			System.err.println();
			System.err.println("# Erro: " + e.getMessage());
			throw e;
		}
	}

}