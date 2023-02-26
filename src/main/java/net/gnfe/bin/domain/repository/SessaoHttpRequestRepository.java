package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.util.ddd.HibernateRepository;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

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
		Map<String, Object> params = new HashMap<>();
		StringBuilder hql = new StringBuilder();

		hql.append(" select s from ").append(clazz.getName()).append(" s ");
		hql.append(" where s.jsessionId = :jsessionId and s.ativa = true ");
		hql.append(" order by s.id desc ");
		params.put("jsessionId", sessionId);

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
