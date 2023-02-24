package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.CamposProduto;
import net.gnfe.bin.domain.enumeration.UnidadeMedida;
import net.gnfe.bin.domain.repository.ProdutoRepository;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public void iniciarProcessamentoDoArquivo(File file, Usuario usuario, String nomeOriginalFile) throws MessageKeyException {
		try {
			List<String> cabecalho = criarCabecalho(file);

			DummyUtils.systrace("processando arquivo: " + file.getAbsolutePath());
			processarArquivo(file, cabecalho);
		} catch (MessageKeyException e) {
			throw e;
		}
    }

	private void processarArquivo(File file, List<String> cabecalho) {

		int i = 0;
		try {
			List<Map<String, String>> mapList = criarMap(file, cabecalho);

			if(!mapList.isEmpty()) {
				criarProdutos(mapList);
			}
		}
		catch (Exception e) {
			handleException(file, e, i);
		}
	}

	private List<Map<String, String>> criarMap(File file, List<String> cabecalhos) throws IOException {

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

	private void handleException(File file, Exception e, int idx) {
		handleException(file, e, idx, true);
	}

	private void handleException(File file, Exception e, int idx, boolean moveFile) {

		DummyUtils.syserr("Erro inesperado" + e);
		String parent = file.getParent();
		String name = file.getName();
		String name2 = "ERRO-idx" + idx + "-" + name;

		try {
			File logFile = new File(parent, name2 + ".log");
			String stackTrace = ExceptionUtils.getStackTrace(e);
			FileUtils.writeStringToFile(logFile, stackTrace, "UTF-8");

			if(moveFile) {
				File destFile = new File(parent, "ERRO-idx" + idx + "-" + name);
				DummyUtils.systrace("tentando copiar " + file.getAbsolutePath() + " para " + destFile.getAbsolutePath());
				FileUtils.moveFile(file, destFile);
			}
		} catch (IOException ex) {
			DummyUtils.syserr("Erro inesperado" + ex);
			throw new RuntimeException(ex);
		}
	}

	private void criarProdutos(List<Map<String, String>> maps) throws Exception {

		try {
			int linha = 1;
			for (Map<String, String> map : maps) {
				DummyUtils.systrace("Importando Produto " + linha + " de " + maps.size());
				criaProduto(map);
				DummyUtils.sleep(400);
				linha++;
			}
		} catch (MessageKeyException e) {
			throw e;
		}
		DummyUtils.systrace("FIM DA IMPORTACAO DO ARQUIVO");
	}

	private void criaProduto(Map<String, String> map) throws Exception {
		Produto produto = isCriarNovoProduto(map);
		criarOuAtualizarProduto(produto, map);
	}

	private Produto isCriarNovoProduto(Map<String, String> map) throws MessageKeyException {
		ProdutoFiltro filtro = new ProdutoFiltro();

		String idProduto = map.get("ID");
		if(StringUtils.isNotBlank(idProduto)) {
			filtro.setIdProduto(idProduto);
			List<Produto> byFiltro = findByFiltro(filtro);
			if(!byFiltro.isEmpty()) {
				return byFiltro.get(0);
			}
		}

		return new Produto();
	}

	private void criarOuAtualizarProduto(Produto produto, Map<String, String> map) {

		String codigo = map.get(CamposProduto.COD.getNome());
		codigo = codigo.replace("\"", "");
		String nome = map.get(CamposProduto.DESC.getNome());
		nome = nome.replace("\"", "");
		String descricaoCurta = map.get(CamposProduto.DESC_CURTA.getNome());
		String gtin = map.get(CamposProduto.GTIN.getNome());
		String cnm = map.get(CamposProduto.CNM.getNome());
		String cest = map.get(CamposProduto.CEST.getNome());
		String estoque = map.get(CamposProduto.ESTOQUE.getNome());
		estoque = estoque.replace(".", "");
		estoque = estoque.replace(",", "");
		String unidadeMedida = map.get(CamposProduto.UNIDADE_MED.getNome());
		String valorUnidade = map.get(CamposProduto.PRECO.getNome());
		valorUnidade = valorUnidade.replace(".", "");
		valorUnidade = valorUnidade.replace(",", ".");
		String estoqueMinimo = map.get(CamposProduto.ESTOQUE_MIN.getNome());

		produto.setCod(codigo);
		produto.setNome(nome);
		produto.setDescricao(descricaoCurta);
		produto.setGtin(gtin);
		produto.setCnm(cnm);
		produto.setCest(cest);
		produto.setEstoqueAtual(StringUtils.isNotBlank(estoque) ? Integer.valueOf(estoque) : null);
		produto.setUnidadeMedida(StringUtils.isNotBlank(unidadeMedida) ? UnidadeMedida.valueOf(unidadeMedida.toUpperCase()) : null);
		produto.setValorUnidade(new BigDecimal(valorUnidade));
		produto.setEstoqueMinimo(StringUtils.isNotBlank(estoqueMinimo) ? Integer.valueOf(estoqueMinimo) : null);

		saveOrUpdate(produto);
	}
}
