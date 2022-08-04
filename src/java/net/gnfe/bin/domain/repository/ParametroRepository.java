package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.Parametro;
import net.gnfe.util.ddd.HibernateRepository;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class ParametroRepository extends HibernateRepository<Parametro> {

	public ParametroRepository() {
		super(Parametro.class);
	}

	public Parametro getByChave(String chave) {

		Map<String,Object> params = new LinkedHashMap<>();
		StringBuilder hql = new StringBuilder();

		hql.append(getStartQuery());

		hql.append(" where chave = :chave ");
		params.put("chave", chave);

		Query query = createQuery(hql.toString(), params);
		query.setMaxResults(1);

		return (Parametro) query.uniqueResult();
	}
}
