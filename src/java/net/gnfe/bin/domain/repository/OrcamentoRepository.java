package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.enumeration.FormaPagamento;
import net.gnfe.bin.domain.vo.filtro.OrcamentoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.primefaces.model.SortOrder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
@SuppressWarnings("unchecked")
public class OrcamentoRepository extends HibernateRepository<Orcamento> {

	public OrcamentoRepository() {
		super(Orcamento.class);
	}

	public List<Orcamento> findByFiltro(OrcamentoFiltro filtro, Integer inicio, Integer max) {

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

		List<Orcamento> list = query.list();
		Set<Orcamento> set = new LinkedHashSet<>(list);//pra tirar os repetidos
		return new ArrayList<Orcamento>(set);
	}

	public int countByFiltro(OrcamentoFiltro filtro) {

		StringBuilder hql = new StringBuilder();

		hql.append(" select count(distinct u.id) from ").append(clazz.getName()).append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		Query query = createQuery(hql.toString(), params);

		return ((Number) query.uniqueResult()).intValue();
	}

	private void makeOrderBy(OrcamentoFiltro filtro, StringBuilder hql) {

		String campoOrdem = filtro.getCampoOrdem();
		if(org.apache.commons.lang3.StringUtils.isNotBlank(campoOrdem)) {

			campoOrdem = campoOrdem.replace("orcamento.", "u.");

			SortOrder ordem = filtro.getOrdem();
			String ordemStr = SortOrder.DESCENDING.equals(ordem) ? " desc " : " asc ";

			hql.append(" order by ").append(campoOrdem).append(ordemStr);
		}
		else {

			hql.append(" order by u.id desc ");
		}
	}

	private Map<String, Object> makeQuery(OrcamentoFiltro filtro, StringBuilder hql) {

		Long id = filtro.getId();
		List<Long> ids = filtro.getIds();
		Long autorId = filtro.getAutorId();
		List<Long> autorIds = filtro.getAutorIds();
		Long clienteId = filtro.getClienteId();
		List<Long> clientIds = filtro.getClientIds();
		FormaPagamento formaPagamento = filtro.getFormaPagamento();
		List<FormaPagamento> formaPagamentos = filtro.getFormaPagamentos();
		String cpfCnpj = filtro.getCpfCnpj();

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

		if(autorId != null) {
			hql.append(" and u.autor.id = :autorId ");
			params.put("autorId", autorId);
		}

		if(autorIds != null && !autorIds.isEmpty()) {
			hql.append(" and u.autor.id in ( :autorIds )");
			params.put("autorIds", autorIds);
		}

		if(clienteId != null) {
			hql.append(" and u.cliente.id = :clienteId ");
			params.put("clienteId", clienteId);
		}

		if(clientIds != null && !clientIds.isEmpty()) {
			hql.append(" and u.cliente.id in ( :clientIds )");
			params.put("clientIds", clientIds);
		}

		if(formaPagamento != null) {
			hql.append(" and u.formaPagamento = :formaPagamento ");
			params.put("formaPagamento", formaPagamento);
		}

		if(formaPagamentos != null && !formaPagamentos.isEmpty()) {
			hql.append(" and u.formaPagamentos in ( :formaPagamentos )");
			params.put("formaPagamentos", formaPagamentos);
		}

		if(StringUtils.isNotBlank(cpfCnpj)) {
			hql.append(" and u.cliente.cpfCnpj = :cpfCnpj ");
			params.put("cpfCnpj", cpfCnpj);
		}

		return params;
	}
}
