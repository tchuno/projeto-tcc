package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.CamposProduto;
import net.gnfe.bin.domain.enumeration.OrigemMercadoria;
import net.gnfe.bin.domain.enumeration.UnidadeMedida;
import net.gnfe.bin.domain.repository.ProdutoRepository;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.excel.ExcelFormat;
import net.gnfe.util.excel.ExcelWriter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

@Service
public class ProdutoService {

	@Autowired private ProdutoRepository produtoRepository;
	@Autowired private UsuarioService usuarioService;
	@Autowired private SessionFactory sessionFactory;

	public Produto get(Long id) {
		return produtoRepository.get(id);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(Produto entity) throws MessageKeyException {
		try {
			produtoRepository.saveOrUpdate(entity);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long id) throws MessageKeyException {
		try {
			produtoRepository.deleteById(id);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<Produto> findByFiltro(ProdutoFiltro filtro) {
		return produtoRepository.findByFiltro(filtro, null, null);
	}

	public List<Produto> findByFiltro(ProdutoFiltro filtro, Integer inicio, Integer max) {
		return produtoRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(ProdutoFiltro filtro) {
		return produtoRepository.countByFiltro(filtro);
	}

	public List<Produto> findByIds(List<Long> ids) {
		return produtoRepository.findByIds(ids);
	}

	public List<Long> findIdsByFiltro(ProdutoFiltro filtro) {
		return produtoRepository.findIdsByFiltro(filtro);
	}

	private BufferedReader getBufferedReader(File file) throws FileNotFoundException {
		Charset charset =  StandardCharsets.UTF_8;
		FileInputStream fis = new FileInputStream(file);
		return new BufferedReader(new InputStreamReader(fis, charset));
	}

	private List<String> criarCabecalho(File file) {

		String readLine = null;
		List<String> cabecalhos = new ArrayList<>();

		try {
			BufferedReader bufferedReader = getBufferedReader(file);
			readLine = bufferedReader.readLine();
			List<String> valorLinhas = asList(readLine.split(";"));

			for (String valorLinha : valorLinhas) {
				String key = valorLinha.trim();
				key = DummyUtils.substituirCaracteresEspeciais(key);
				key = key.toUpperCase();
				cabecalhos.add(key);
			}

		} catch (IOException e) {
			DummyUtils.syserr("Erro inesperado" + e);
		}

		return cabecalhos;
	}

	@Transactional(rollbackFor=Exception.class)
    public void iniciarProcessamentoDoArquivo(File file) throws Exception {
		List<String> cabecalho = criarCabecalho(file);
		DummyUtils.systrace("processando arquivo: " + file.getAbsolutePath());
		processarArquivo(file, cabecalho);
	}

	private void processarArquivo(File file, List<String> cabecalho) throws Exception {

		int i = 0;
		try {
			List<Map<String, String>> mapList = criarMap(file, cabecalho);

			if(!mapList.isEmpty()) {
				criarProdutos(mapList);
			}
		}
		catch (Exception e) {
			throw e;
		}
	}

	private List<Map<String, String>> criarMap(File file, List<String> cabecalhos) throws Exception {

		BufferedReader bufferedReader = getBufferedReader(file);
		List<Map<String, String>> listMap = new ArrayList<>();

		int linha = 0;
		String line = null;
		try {

			Map<String, String> map = new LinkedHashMap<>();
			Map<String, String> dadosAdicionaisMap = new LinkedHashMap<>();
			while ((line = bufferedReader.readLine()) != null) {
				linha++;
				DummyUtils.systrace("Processando linha: " + linha);

				List<String> valorLinhas = asList(line.split(";"));
				int idx = 0;
				boolean isCabecalho = (linha == 1);
				for(String valorLinha : valorLinhas) {
					valorLinha = valorLinha.trim();

					if(!cabecalhos.isEmpty() && !isCabecalho) {
						String key = cabecalhos.get(idx);
						String value = DummyUtils.substituirCaracteresEspeciais(valorLinha);
						value = DummyUtils.htmlToString(value);
						map.put(key, value);
						idx++;
					}
				}

				if(!map.isEmpty()) {
					map.putAll(dadosAdicionaisMap);
					listMap.add(map);
					map = new LinkedHashMap<>();
				}
			}
		}
		catch (Exception e) {
			String exceptionMessage = DummyUtils.getExceptionMessage(e);
			DummyUtils.systrace("Erro na linha " + linha + " do arquivo " + file.getAbsolutePath() + ": " + exceptionMessage);
			String erro = "Linha " + linha + ": " + line;
			DummyUtils.systrace(erro);
			DummyUtils.syserr("Erro inesperado" + e);
			throw e;
		}
		finally {
			bufferedReader.close();
		}

		return listMap;
	}

	private void criarProdutos(List<Map<String, String>> maps) throws MessageKeyException {

		int linha = 1;
		try {
			for (Map<String, String> map : maps) {
				DummyUtils.systrace("Importando Produto " + linha + " de " + maps.size());
				criaProduto(map);
				DummyUtils.sleep(400);
				linha++;
			}
		} catch (Exception e) {
			throw new MessageKeyException("linha.error", linha);
		}
		DummyUtils.systrace("FIM DA IMPORTACAO DO ARQUIVO");
	}

	private void criaProduto(Map<String, String> map) {
		Produto produto = isCriarNovoProduto(map);
		criarOuAtualizarProduto(produto, map);
	}

	private Produto isCriarNovoProduto(Map<String, String> map) {

		String idProduto = map.get(CamposProduto.ID.getNome());
		if(StringUtils.isNotBlank(idProduto)) {
			Produto produto = get(Long.valueOf(idProduto));
			if(produto != null) {
				return produto;
			}
		}

		return new Produto();
	}

	private void criarOuAtualizarProduto(Produto produto, Map<String, String> map) {

		Long id = produto.getId();
		boolean isInsert = id == null;

		String codigo = map.get(CamposProduto.COD.getNome());
		String nome = map.get(CamposProduto.NOME.getNome());
		String desc = map.get(CamposProduto.DESC.getNome());
		String gtin = map.get(CamposProduto.GTIN.getNome());
		String cnm = map.get(CamposProduto.CNM.getNome());
		String cest = map.get(CamposProduto.CEST.getNome());
		String cfop = map.get(CamposProduto.CFOP.getNome());
		String fornecedor = map.get(CamposProduto.FORNECEDOR.getNome());
		String estoqueAtual = map.get(CamposProduto.ESTOQUE_ATUAL.getNome());
		String unidadeMedida = map.get(CamposProduto.UNIDADE_MEDIDA.getNome());
		String valorUnidade = map.get(CamposProduto.VALOR_UNIDADE.getNome());
		String valorCompra = map.get(CamposProduto.VALOR_COMPRA.getNome());
		String reposicao = map.get(CamposProduto.REPOSICAO.getNome());
		String estoqueMinimo = map.get(CamposProduto.ESTOQUE_MINIMO.getNome());
		String origemMercadoria = map.get(CamposProduto.ORIGEM_MERCADORIA.getNome());
		String icms = map.get(CamposProduto.ICMS.getNome());
		String pis = map.get(CamposProduto.PIS.getNome());
		String cofins = map.get(CamposProduto.COFINS.getNome());

		produto.setCod(codigo);
		produto.setNome(nome);
		produto.setDescricao(desc);
		produto.setGtin(gtin);
		produto.setCnm(cnm);
		produto.setCest(cest);
		produto.setCfop(cfop);
		produto.setFornecedor(StringUtils.isNotBlank(fornecedor) ? usuarioService.get(Long.valueOf(fornecedor)) : null );
		if(isInsert) {
			produto.setEstoqueAtual(StringUtils.isNotBlank(estoqueAtual) ? Integer.valueOf(estoqueAtual) : null);
		}
		produto.setUnidadeMedida(StringUtils.isNotBlank(unidadeMedida) ? UnidadeMedida.valueOf(unidadeMedida.toUpperCase()) : null);
		produto.setValorUnidade(StringUtils.isNotBlank(valorUnidade) ? new BigDecimal(valorUnidade) : null);
		produto.setValorCompra(StringUtils.isNotBlank(valorCompra) ? new BigDecimal(valorCompra) : null);
		produto.setTempoReposicao(StringUtils.isNotBlank(reposicao) ? Integer.valueOf(reposicao) : null);
		produto.setEstoqueMinimo(StringUtils.isNotBlank(estoqueMinimo) ? Integer.valueOf(estoqueMinimo) : null);
		produto.setOrigemMercadoria(StringUtils.isNotBlank(origemMercadoria) ? OrigemMercadoria.valueOf(origemMercadoria) : null);
		produto.setAliquotaICMS(StringUtils.isNotBlank(icms) ? new BigDecimal(icms) : null);
		produto.setAliquotaPIS(StringUtils.isNotBlank(pis) ? new BigDecimal(pis) : null);
		produto.setAliquotaCOFINS(StringUtils.isNotBlank(cofins) ? new BigDecimal(cofins) : null);

		saveOrUpdate(produto);
	}

	public File render(ProdutoFiltro filtro) {
		System.out.println("ProdutoService.render()");
		try {

			String fileOrigemNome = "produtos.xlsx";

			String extensao = DummyUtils.getExtensao(fileOrigemNome);

			File fileOrigem = DummyUtils.getFileFromResource("/net/gnfe/excel/" + fileOrigemNome);

			File file = File.createTempFile("produtos-", "." + extensao);
			//file.deleteOnExit();
			FileUtils.copyFile(fileOrigem, file);

			ExcelWriter ew = new ExcelWriter();
			ew.abrirArquivo(file);
			Workbook workbook = ew.getWorkbook();
			ExcelFormat ef = new ExcelFormat(workbook);
			ew.setExcelFormat(ef);

			Sheet sheet = workbook.getSheet("Produtos");
			renderRowsProdutos(sheet, ew, filtro);

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

	private void renderRowsProdutos(Sheet sheet, ExcelWriter ew, ProdutoFiltro filtro) {
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

			List<Produto> list = findByIds(ids2);

			for (int i = 0; i < list.size(); i++) {

				Produto mp = list.get(i);

				ew.criaLinha(sheet, rowNum++);
				renderBody(ew, mp);

			}

			Session session = sessionFactory.getCurrentSession();
			session.clear();
		}
		while (!ids.isEmpty());
	}

	private void renderBody(ExcelWriter ew, Produto mp) {

		Long id = mp.getId();
		ew.escrever(id);

		String cod = mp.getCod();
		ew.escrever(cod);

		String nome = mp.getNome();
		ew.escrever(nome);

		String descricao = mp.getDescricao();
		ew.escrever(descricao);

		String gtin = mp.getGtin();
		ew.escrever(gtin);

		String cnm = mp.getCnm();
		ew.escrever(cnm);

		String cest = mp.getCest();
		ew.escrever(cest);

		String cfop = mp.getCfop();
		ew.escrever(cfop);

		Usuario fornecedor = mp.getFornecedor();
		ew.escrever(fornecedor != null ? fornecedor.getId() : null);

		Integer estoqueAtual = mp.getEstoqueAtual();
		ew.escrever(estoqueAtual);

		UnidadeMedida unidadeMedida = mp.getUnidadeMedida();
		ew.escrever(unidadeMedida != null ? unidadeMedida.name() : null);

		BigDecimal valorUnidade = mp.getValorUnidade();
		ew.escrever(valorUnidade);

		BigDecimal valorCompra = mp.getValorCompra();
		ew.escrever(valorCompra);

		Integer tempoReposicao = mp.getTempoReposicao();
		ew.escrever(tempoReposicao);

		Integer estoqueMinimo = mp.getEstoqueMinimo();
		ew.escrever(estoqueMinimo);

		OrigemMercadoria origemMercadoria = mp.getOrigemMercadoria();
		ew.escrever(origemMercadoria != null ? origemMercadoria.name() : null);

		BigDecimal aliquotaICMS = mp.getAliquotaICMS();
		ew.escrever(aliquotaICMS);

		BigDecimal aliquotaPIS = mp.getAliquotaPIS();
		ew.escrever(aliquotaPIS);

		BigDecimal aliquotaCOFINS = mp.getAliquotaCOFINS();
		ew.escrever(aliquotaCOFINS);
	}
}
