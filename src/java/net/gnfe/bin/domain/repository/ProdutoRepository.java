package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.enumeration.UnidadeMedida;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.primefaces.model.SortOrder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@SuppressWarnings("unchecked")
public class ProdutoRepository extends HibernateRepository<Produto> {

	public ProdutoRepository() {
		super(Produto.class);
	}

	public List<Produto> findByFiltro(ProdutoFiltro filtro, Integer inicio, Integer max) {

		StringBuilder hql = new StringBuilder();

		hql.append( "select u from ").append(clazz.getName());
		hql.append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		makeOrderBy(filtro, hql);

		Query query = createQuery(hql.toString(), params);

		if(inicio != null) {
			query.setFirstResult(inicio);
		}

		if(max != null) {
			query.setMaxResults(max);
		}

		List<Produto> list = query.list();
		Set<Produto> set = new LinkedHashSet<>(list);//pra tirar os repetidos
		return new ArrayList<Produto>(set);
	}

	public int countByFiltro(ProdutoFiltro filtro) {

		StringBuilder hql = new StringBuilder();

		hql.append(" select count(distinct u.id) from ").append(clazz.getName()).append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		Query query = createQuery(hql.toString(), params);

		return ((Number) query.uniqueResult()).intValue();
	}

	private void makeOrderBy(ProdutoFiltro filtro, StringBuilder hql) {

		String campoOrdem = filtro.getCampoOrdem();
		if(org.apache.commons.lang3.StringUtils.isNotBlank(campoOrdem)) {

			campoOrdem = campoOrdem.replace("usuario.", "u.");

			SortOrder ordem = filtro.getOrdem();
			String ordemStr = SortOrder.DESCENDING.equals(ordem) ? " desc " : " asc ";

			hql.append(" order by ").append(campoOrdem).append(ordemStr);
		}
		else {

			hql.append(" order by u.id desc ");
		}
	}

	private Map<String, Object> makeQuery(ProdutoFiltro filtro, StringBuilder hql) {

		Long id = filtro.getId();
		String idProduto = filtro.getIdProduto();
		List<Long> ids = filtro.getIds();
		String cod = filtro.getCod();
		String nome = filtro.getNome();
		String descricao = filtro.getDescricao();
		String gtin = filtro.getGtin();
		String cnm = filtro.getCnm();
		String cst = filtro.getCst();
		String cest = filtro.getCest();
		Long fornecedorId = filtro.getFornecedorId();
		List<Long> fornecedorIds = filtro.getFornecedorIds();
		Integer estoqueAtual = filtro.getEstoqueAtual();
		UnidadeMedida unidadeMedida = filtro.getUnidadeMedida();
		BigDecimal valorUnidade = filtro.getValorUnidade();
		Integer tempoReposicao = filtro.getTempoReposicao();
		Integer estoqueMinimo = filtro.getEstoqueMinimo();

		Map<String, Object> params = new HashMap<>();

		hql.append(" where 1=1 ");
		
		if(id != null) {
			hql.append(" and u.id = :id ");
			params.put("id", id);
		}

		if(idProduto != null) {
			hql.append(" and u.idProduto = :idProduto ");
			params.put("idProduto", idProduto);
		}

		if(ids != null && !ids.isEmpty()) {
			hql.append(" and u.id in ( :ids ) ");
			params.put("ids", ids);
		}

		if(StringUtils.isNotBlank(nome)) {
			hql.append(" and upper(u.nome) like :nome ");
			params.put("nome", "%" + nome.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(cod)) {
			hql.append(" and upper(u.cod) like :cod ");
			params.put("cod", "%" + cod.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(descricao)) {
			hql.append(" and upper(u.descricao) like :descricao ");
			params.put("descricao", "%" + descricao.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(gtin)) {
			hql.append(" and upper(u.gtin) like :gtin ");
			params.put("gtin", "%" + gtin.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(cnm)) {
			hql.append(" and upper(u.cnm) like :cnm ");
			params.put("cnm", "%" + cnm.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(cst)) {
			hql.append(" and upper(u.cst) like :cst ");
			params.put("cst", "%" + cst.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(cest)) {
			hql.append(" and upper(u.cest) like :cest ");
			params.put("cest", "%" + cest.toUpperCase() + "%");
		}

		if(fornecedorId != null) {
			hql.append(" and u.fornecedor.id = :fornecedorId ");
			params.put("fornecedorId", fornecedorId);
		}

		if(ids != null && !ids.isEmpty()) {
			hql.append(" and u.fornecedor.id in ( :fornecedorIds ) ");
			params.put("fornecedorIds", fornecedorIds);
		}

		if(estoqueAtual != null) {
			hql.append(" and u.estoqueAtual = :estoqueAtual ");
			params.put("estoqueAtual", estoqueAtual);
		}

		if(unidadeMedida != null) {
			hql.append(" and u.unidadeMedida = :unidadeMedida ");
			params.put("unidadeMedida", unidadeMedida);
		}

		if(valorUnidade != null) {
			hql.append(" and u.valorUnidade => :valorUnidade ");
			params.put("valorUnidade", valorUnidade);
		}

		if(tempoReposicao != null) {
			hql.append(" and u.tempoReposicao => :tempoReposicao ");
			params.put("tempoReposicao", tempoReposicao);
		}

		if(valorUnidade != null) {
			hql.append(" and u.estoqueMinimo => :estoqueMinimo ");
			params.put("estoqueMinimo", estoqueMinimo);
		}


		return params;
	}
}
