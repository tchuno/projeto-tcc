package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.Role;
import net.gnfe.util.ddd.HibernateRepository;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepository extends HibernateRepository<Role> {

	public RoleRepository() {
		super(Role.class);
	}

	public void delete(Long roleId) {

		List<Object> params = new ArrayList<Object>();
		StringBuilder hql = new StringBuilder();

		hql.append(" delete from ").append(clazz.getName());
		hql.append(" where id = ? ");
		params.add(roleId);

		Query query = createQuery(hql.toString(), params);

		query.executeUpdate();
	}
}
