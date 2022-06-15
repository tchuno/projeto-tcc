package net.gnfe.bin.domain.repository;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import net.gnfe.bin.domain.entity.Parametro;
import net.gnfe.util.ddd.HibernateRepository;

@Repository
public class ParametroRepository extends HibernateRepository<Parametro> {

	public ParametroRepository() {
		super(Parametro.class);
	}

	public Parametro getByChave(String chave) {

		List<Object> params = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder();

		hql.append(getStartQuery());

		hql.append(" where chave = ? ");
		params.add(chave);

		Query query = createQuery(hql.toString(), params);
		query.setMaxResults(1);

		return (Parametro) query.uniqueResult();
	}
}
