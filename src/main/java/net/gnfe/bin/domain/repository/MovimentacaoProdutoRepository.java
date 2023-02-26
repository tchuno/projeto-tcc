package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.MovimentacaoProduto;
import net.gnfe.bin.domain.vo.filtro.MovimentacaoProdutoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@SuppressWarnings("unchecked")
public class MovimentacaoProdutoRepository extends HibernateRepository<MovimentacaoProduto> {

	public MovimentacaoProdutoRepository() {
		super(MovimentacaoProduto.class);
	}

	public List<MovimentacaoProduto> findByFiltro(MovimentacaoProdutoFiltro filtro, Integer inicio, Integer max) {

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

		List<MovimentacaoProduto> list = query.list();
		Set<MovimentacaoProduto> set = new LinkedHashSet<>(list);//pra tirar os repetidos
		return new ArrayList<MovimentacaoProduto>(set);
	}

	public int countByFiltro(MovimentacaoProdutoFiltro filtro) {

		StringBuilder hql = new StringBuilder();

		hql.append(" select count(distinct u.id) from ").append(clazz.getName()).append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		Query query = createQuery(hql.toString(), params);

		return ((Number) query.uniqueResult()).intValue();
	}

	public List<Long> findIdsByFiltro(MovimentacaoProdutoFiltro filtro) {

		StringBuilder hql = new StringBuilder();
		hql.append(" select u.id from ").append(clazz.getName()).append(" u ");
		Map<String, Object> params = makeQuery(filtro, hql);
		hql.append("order by u.id");

		Query query = createQuery(hql.toString(), params);
		query.setFetchSize(100);
		return query.list();
	}

	public List<MovimentacaoProduto> findByIds(List<Long> ids) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<>();
		hql.append(getStartQuery());

		hql.append(" where id in ( :ids ) ");
		params.put("ids", ids);
		hql.append(" order by data ");

		Query query = createQuery(hql.toString(), params);
		query.setFetchSize(500);

		return query.list();
	}

	private Map<String, Object> makeQuery(MovimentacaoProdutoFiltro filtro, StringBuilder hql) {

		Long id = filtro.getId();
		List<Long> ids = filtro.getIds();
		Long orcamentoId = filtro.getOrcamentoId();
		Long produtoId = filtro.getProdutoId();
		Calendar calendar = Calendar.getInstance();
		Date dataInicio = filtro.getDataInicio();
		calendar.setTime(dataInicio);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		dataInicio = calendar.getTime();
		Date dataFim = filtro.getDataFim();
		calendar.setTime(dataFim);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		dataFim = calendar.getTime();


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

		if(produtoId != null) {
			hql.append(" and u.produto.id = :produtoId ");
			params.put("produtoId", produtoId);
		}

		if(dataInicio != null) {
			hql.append(" and u.data >= :dataInicio ");
			params.put("dataInicio", dataInicio);
		}

		if(dataFim != null) {
			hql.append(" and u.data <= :dataFim ");
			params.put("dataFim", dataFim);
		}


		return params;
	}
}
