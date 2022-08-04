package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.util.ddd.HibernateRepository;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@SuppressWarnings("unchecked")
public class SessaoHttpRequestRepository extends HibernateRepository<SessaoHttpRequest> {

	public SessaoHttpRequestRepository() {
		super(SessaoHttpRequest.class);
	}

	public int mataSessaoUsuario(Usuario usuario) {
		Query query = createQuery(" update " + clazz.getName() +" set ATIVA = false where USUARIO_ID = " + usuario.getId());
		return query.executeUpdate();
	}

	public SessaoHttpRequest findByJSessionId(String sessionId) {
		List<Object> params = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder();

		hql.append(getStartQuery());
		hql.append(" where JSESSIONID = ? and ATIVA = true ");
		params.add(sessionId);
		hql.append(" order by id desc ");

		Query query = createQuery(hql.toString(), params);
		query.setFirstResult(0);
		query.setMaxResults(1);
		return (SessaoHttpRequest) query.uniqueResult();
	}

	public int excluir(Date dataCorte) {

		StringBuilder sql = new StringBuilder();
		sql.append(" delete from sessao_http_request where id in ( ");
		sql.append(" 	select id from sessao_http_request where data < :dataCorte order by id desc ");
		sql.append(" ) ");

		Map<String, Object> params = new LinkedHashMap<>();
		params.put("dataCorte", dataCorte);

		Query query = createSQLQuery(sql.toString(), params);

		return query.executeUpdate();
	}
}
