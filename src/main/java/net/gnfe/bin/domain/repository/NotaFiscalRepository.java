package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.NotaFiscal;
import net.gnfe.bin.domain.vo.filtro.NotaFiscalFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@SuppressWarnings("unchecked")
public class NotaFiscalRepository extends HibernateRepository<NotaFiscal> {

	public NotaFiscalRepository() {
		super(NotaFiscal.class);
	}

	public List<NotaFiscal> findByFiltro(NotaFiscalFiltro filtro, Integer inicio, Integer max) {

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

		List<NotaFiscal> list = query.list();
		Set<NotaFiscal> set = new LinkedHashSet<>(list);//pra tirar os repetidos
		return new ArrayList<NotaFiscal>(set);
	}

	public int countByFiltro(NotaFiscalFiltro filtro) {

		StringBuilder hql = new StringBuilder();

		hql.append(" select count(distinct u.id) from ").append(clazz.getName()).append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		Query query = createQuery(hql.toString(), params);

		return ((Number) query.uniqueResult()).intValue();
	}

	private Map<String, Object> makeQuery(NotaFiscalFiltro filtro, StringBuilder hql) {

		Long id = filtro.getId();
		List<Long> ids = filtro.getIds();
		Long orcamentoId = filtro.getOrcamentoId();
		Date dataCriacao = filtro.getDataCriacao();
		String chaveAcesso = filtro.getChaveAcesso();


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

		if(dataCriacao != null) {
			hql.append(" and u.dataCriacao = :dataCriacao ");
			params.put("dataCriacao", dataCriacao);
		}

		if(StringUtils.isNotBlank(chaveAcesso)) {
			hql.append(" and u.chaveAcesso = :chaveAcesso ");
			params.put("chaveAcesso", chaveAcesso);
		}
		return params;
	}
}
