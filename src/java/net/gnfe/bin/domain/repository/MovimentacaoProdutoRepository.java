package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.MovimentacaoProduto;
import net.gnfe.bin.domain.entity.NotaFiscal;
import net.gnfe.bin.domain.vo.filtro.MovimentacaoProdutoFiltro;
import net.gnfe.bin.domain.vo.filtro.NotaFiscalFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import org.apache.commons.lang.StringUtils;
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

	private Map<String, Object> makeQuery(MovimentacaoProdutoFiltro filtro, StringBuilder hql) {

		Long id = filtro.getId();
		List<Long> ids = filtro.getIds();
		Long orcamentoId = filtro.getOrcamentoId();
		Long produtoId = filtro.getProdutoId();
		Date dataIncio = filtro.getDataIncio();
		Date dataFim = filtro.getDataFim();


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

		if(dataIncio != null) {
			hql.append(" and u.data >= :dataIncio ");
			params.put("dataIncio", dataIncio);
		}

		if(dataFim != null) {
			hql.append(" and u.data <= :dataFim ");
			params.put("dataFim", dataFim);
		}


		return params;
	}
}
