package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.vo.filtro.OrcamentoProdutoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@SuppressWarnings("unchecked")
public class OrcamentoProdutoRepository extends HibernateRepository<OrcamentoProduto> {

	public OrcamentoProdutoRepository() {
		super(OrcamentoProduto.class);
	}

	public List<OrcamentoProduto> findByFiltro(OrcamentoProdutoFiltro filtro, Integer inicio, Integer max) {

		StringBuilder hql = new StringBuilder();

		hql.append( "select u from ").append(clazz.getName());
		hql.append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		hql.append("order by u.id");

		Query query = createQuery(hql.toString(), params);

		if(inicio != null) {
			query.setFirstResult(inicio);
		}

		if(max != null) {
			query.setMaxResults(max);
		}

		List<OrcamentoProduto> list = query.list();
		Set<OrcamentoProduto> set = new LinkedHashSet<>(list);//pra tirar os repetidos
		return new ArrayList<OrcamentoProduto>(set);
	}

	public int countByFiltro(OrcamentoProdutoFiltro filtro) {

		StringBuilder hql = new StringBuilder();

		hql.append(" select count(distinct u.id) from ").append(clazz.getName()).append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		Query query = createQuery(hql.toString(), params);

		return ((Number) query.uniqueResult()).intValue();
	}

	private Map<String, Object> makeQuery(OrcamentoProdutoFiltro filtro, StringBuilder hql) {

		Long id = filtro.getId();
		List<Long> ids = filtro.getIds();
		Long orcamentoId = filtro.getOrcamentoId();
		List<Long> orcamentoIds = filtro.getOrcamentoIds();
		Long produtoId = filtro.getProdutoId();
		List<Long> produtoIds = filtro.getProdutoIds();

		Map<String, Object> params = new HashMap<>();

		hql.append(" where 1=1 ");
		
		if(id != null) {
			hql.append(" and u.id = :id ");
			params.put("id", id);
		}

		if(ids != null && !ids.isEmpty()) {
			hql.append(" and u.id in ( :ids ) ");
			params.put("ids", ids);
		}

		if(orcamentoId != null) {
			hql.append(" and u.orcamento.id = :orcamentoId ");
			params.put("orcamentoId", orcamentoId);
		}

		if(orcamentoIds != null && !orcamentoIds.isEmpty()) {
			hql.append(" and u.orcamento.id in ( :orcamentoIds )");
			params.put("orcamentoIds", orcamentoIds);
		}

		if(produtoId != null) {
			hql.append(" and u.produto.id = :produtoId ");
			params.put("produtoId", produtoId);
		}

		if(produtoIds != null && !produtoIds.isEmpty()) {
			hql.append(" and u.orcamento.id in ( :produtoIds )");
			params.put("produtoIds", produtoIds);
		}

		return params;
	}
}
