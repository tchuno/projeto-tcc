package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.Parametro;
import net.gnfe.bin.domain.repository.ParametroRepository;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.other.Bolso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParametroService {

	private final static long TIMEOUT_CACHE = (1000 * 60 * 10);//10 minutos
	private final static Map<P, Bolso<?>> MAP_CACHE = new HashMap<>();

	public enum P {
		COR_BARRA,
		COR_MENU,
		COR_FONTE_TITULO_BARRA,
		COR_FONTE_MENU,
		COR_FONTE_MENU_SELECIONADO,
		TITULO
	}

	private static List<P> CUSTOMIZACAO_TELA = Arrays.asList(P.COR_BARRA, P.COR_FONTE_TITULO_BARRA, P.COR_MENU, P.COR_FONTE_MENU, P.COR_FONTE_MENU_SELECIONADO, P.TITULO);

	@Autowired private ParametroRepository parametroRepository;

	public Parametro get(Long id) {
		return parametroRepository.get(id);
	}

	@Transactional(readOnly=true)
	public String getValor(P p) {

		Parametro param = parametroRepository.getByChave(p.name());
		String valor = param != null ? param.getValor() : null;
		return valor;
	}

	public <T> T getValor(P p, Class<T> resultType) {

		String valor = getValor(p);
		T result = DummyUtils.convertTypes(valor, resultType);
		return result;
	}

	public String getValorCache(P p) {
		return getValorCache(p, String.class);
	}

	@Transactional(rollbackFor=Exception.class)
	public void setValor(P p, String valor) {

		Parametro parametro = parametroRepository.getByChave(p.name());

		if(parametro == null) {
			parametro = new Parametro();
			parametro.setChave(p.name());
		}

		parametro.setValor(valor);
		parametroRepository.saveOrUpdate(parametro);
	}

	@Transactional(readOnly=true)
	public Map<String, String> getCustomizacao () {

		Map<String, String> map = new HashMap<>();
		for (P p : CUSTOMIZACAO_TELA) {
			String valor = getValorCache(p);
			map.put(p.name(), valor);
		}

		return map;
	}

	@Transactional(rollbackFor=Exception.class)
	public void restaurarPadrao() throws IOException {

		Map<String, String> map = getCustomizacao();
		map.put(P.COR_BARRA.name(), "4f708c");
		map.put(P.COR_FONTE_TITULO_BARRA.name(), "ffffff");
		map.put(P.COR_MENU.name(), "2c3e50");
		map.put(P.COR_FONTE_MENU.name(), "9d9d9d");
		map.put(P.COR_FONTE_MENU_SELECIONADO.name(), "fff");
		map.put(P.TITULO.name(), "GNFE");

		salvarCustomizacao(map);
	}

	@Transactional(rollbackFor=Exception.class)
	public void salvarCustomizacao(Map<String, String> map) throws IOException {

		for (P p : CUSTOMIZACAO_TELA) {
			String valor = map.get(p.name());
			setValor(p, valor);
		}

		MAP_CACHE.clear();
	}

	@SuppressWarnings("unchecked")
	public <T> T getValorCache(P p, Class<T> resultType) {

		Bolso<T> cache = (Bolso<T>) MAP_CACHE.get(p);
		cache = cache != null ? cache : new Bolso<T>();
		MAP_CACHE.put(p, cache);

		T result = cache.getObjeto();
		long finalTime = cache.getFinalTime();

		long now = System.currentTimeMillis();
		if(result == null || finalTime < now) {

			result = getValor(p, resultType);
			cache.setObjeto(result);
			cache.setFinalTime(now + TIMEOUT_CACHE);
		}

		return result;
	}
}
