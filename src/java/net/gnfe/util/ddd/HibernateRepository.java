package net.gnfe.util.ddd;

import org.hibernate.*;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HibernateRepository<T> {

	private static final String[] UNIQUE_VIOLATION_MESSAGE_EXCEPTION = {
			"violates unique constraint",
			"duplicar chave viola",
			"duplicar valor da chave viola",
			"restrição exclusiva"
	};

	private static final String[] FOREING_KEY_VIOLATION_MESSAGE_EXCEPTION = {
			"violates foreign key constraint",
			"de chave estrangeira",
	};

	protected Class<T> clazz;
	protected Session session;
	private SessionFactory sessionFactory;

	public HibernateRepository(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	protected String getStartQuery() {
		return new StringBuilder("from ").append(clazz.getName()).toString();
	}

	@Deprecated
	public static boolean isUniqueViolationException(RuntimeException e) {

		Throwable cause = e.getCause();

		String message = cause != null ? cause.getMessage() : null;
		boolean contains = false;

		if(message != null) {

			for (String str : UNIQUE_VIOLATION_MESSAGE_EXCEPTION) {
				contains |= message.contains(str);
			}
		}

		if(cause != null && contains) {
			return true;
		}

		if(cause instanceof BatchUpdateException) {

			BatchUpdateException batchUpdateException = (BatchUpdateException) cause;
			SQLException nextException = batchUpdateException.getNextException();

			String message2 = nextException.getMessage();
			boolean contains2 = false;
			for (String str : UNIQUE_VIOLATION_MESSAGE_EXCEPTION) {
				contains2 |= message2.contains(str);
			}

			if(contains2) {
				return true;
			}
		}

		return false;
	}

	public static void verifyConstrantViolation(RuntimeException e) throws MessageKeyException {

		String prefix = null;
		Pattern pattern = null;

		boolean foreignKeyViolation = isForeignKeyViolationException(e);
		if(foreignKeyViolation) {

			prefix = "foreignKeyViolation.";
			pattern = Pattern.compile("\\w+_fk\\w*");
		}

		boolean uniqueViolation = isUniqueViolationException(e);
		if(uniqueViolation) {

			prefix = "uniqueViolation.";
			pattern = Pattern.compile("\\w+_uk\\w*");
		}

		if(prefix != null) {

			String message = e.getMessage();

			String constrant = getConstrant(pattern, message);

			if(constrant == null) {

				Throwable cause = e.getCause();
				if(cause != null) {

					String message2 = cause.getMessage();
					constrant = getConstrant(pattern, message2);

					if(constrant == null && cause instanceof BatchUpdateException) {

						BatchUpdateException batchUpdateException = (BatchUpdateException) cause;
						SQLException nextException = batchUpdateException.getNextException();

						String message3 = nextException.getMessage();
						constrant = getConstrant(pattern, message3);
					}
				}
			}

			if(constrant != null) {

				throw new MessageKeyException(prefix + constrant + ".error");
			}
		}

		throw e;
	}

	private static String getConstrant(Pattern pattern, String message) {

		message = message.toLowerCase();

		Matcher matcher = pattern.matcher(message);
		if(matcher.find()) {

			String group = matcher.group();
			return group;
		}

		return null;
	}

	@Deprecated
	public static boolean isForeignKeyViolationException(RuntimeException e) {

		if(e instanceof ConstraintViolationException) {
			return true;
		}

		Throwable cause = e.getCause();
		if(cause == null) {
			throw e;
		}

		String message = cause.getMessage();

		if(message != null) {

			for (String str : FOREING_KEY_VIOLATION_MESSAGE_EXCEPTION) {

				if(message.contains(str)) {
					return true;
				}
			}
		}

		return false;
	}


	protected Session getSession() {

		if(sessionFactory != null) {
			session = sessionFactory.getCurrentSession();
		}

		if(session == null) {
			throw new RuntimeException("A Session está nula no Repositório. Precisa setar a dependência antes de utilizar.");
		}

		if(!session.isOpen()){
			throw new RuntimeException("A session deste repositorio foi fechada.");
		}

		return session;
	}

	protected String startQuery() {
		return new StringBuilder("from ").append(clazz.getName()).toString();
	}

	protected Query createQuery(StringBuilder hql){
		return createQuery(hql.toString());
	}

	protected Query createQuery(String query){
		return getSession().createQuery(query);
	}

	protected Query createNamedQuery(String queryName){
		return getSession().getNamedQuery(queryName);
	}

	protected Query createFilter(T collection, String filterSQL){
		return getSession().createFilter(collection, filterSQL);
	}

	protected Query createQuery(String sql, int firstResult, int maxResults){

		Query q = getSession().createQuery(sql);

		q.setFirstResult(firstResult);
		q.setMaxResults(maxResults);

		return q;
	}

	protected Query createQuery(StringBuilder hql, List<Object> params) {
		return createQuery(hql.toString(), params);
	}

	protected Query createQuery(String hql, List<Object> params) {

		Query query = getSession().createQuery(hql);

		for (int i = 0; i < params.size(); i++) {
			Object object = params.get(i);
			query.setParameter(i, object);
		}

		return query;
	}

	protected Query createQuery(StringBuilder query, Map<String, Object> params) {
		return createQuery(query.toString(), params);
	}

	protected Query createQuery(String query, Map<String, Object> params) {

		Query q = createQuery(query);

		setParameters(q, params);

		return q;
	}

	protected Criteria createCriteria(){
		return getSession().createCriteria(this.clazz);
	}

	protected Criteria createCriteria(int firstResult, int maxResults){

		Criteria c = getSession().createCriteria(this.clazz);

		c.setFirstResult(firstResult);
		c.setMaxResults(maxResults);

		return c;
	}

	protected void handleException(HibernateException e, T entity) {

		if(e instanceof NonUniqueObjectException){

			if(Hibernate.isInitialized(entity) && !getSession().contains(entity)) {
				System.err.println("A different object with the same identifier value was already associated with the session: " + entity);
				System.err.println("The Session doesn't contains the object, but it is already initialized (may be it is transient)");
			}

			throw e;
		}

		throw e;
	}

	public void delete(T entity) {

		try {
			getSession().delete(entity);
			// Se deletou remove da session (para evitar at� que reassocie por cascata)
			//org.hibernate.ObjectDeletedException: deleted object would be re-saved by cascade (remove deleted object from associations):
			getSession().evict(entity);
			getSession().flush();
		}
		catch (HibernateException e) {
			handleException(e, entity);
		}
	}

	public void update(T entity) {

		try {
			getSession().update(entity);
			getSession().flush();
		}
		catch (HibernateException e) {
			handleException(e, entity);
		}
	}

	@SuppressWarnings("unchecked")
	public T merge(T entity) {

		try {
			T merge = (T) getSession().merge(entity);
			getSession().flush();
			return merge;
		}
		catch (HibernateException e) {
			handleException(e, entity);
			return null;
		}
	}

	public void saveWithoutFlush(T entity) {
		save(entity, false);
	}

	public void save(T entity) {
		save(entity, true);
	}

	protected void save(T entity, boolean flush) {

		try {
			getSession().save(entity);

			if(flush) {
				getSession().flush();
			}
		}
		catch (HibernateException e) {
			handleException(e, entity);
		}
	}

	public void saveOrUpdate(T entity) {

		try {
			getSession().saveOrUpdate(entity);
			getSession().flush();
		}
		catch (HibernateException e) {
			handleException(e, entity);
		}
	}

	public void saveOrUpdateWithoutFlush(T entity) {
		try {
			getSession().saveOrUpdate(entity);
		}
		catch (HibernateException e) {
			handleException(e, entity);
		}
	};

	public void deleteById(Serializable id) {
		//Bulk Delete
		Query query = createQuery("delete " + clazz.getName() + " c where c.id = :idToExclude");
		query.setParameter("idToExclude", id);
		query.executeUpdate();
	}

	public T load(Serializable id) {
		return (T) getSession().load(this.clazz, id);
	}

	public T get(Serializable id) {
		return (T) getSession().get(this.clazz, id);
	}

	public T get(Long id) {
		return (T) getSession().get(this.clazz, id);
	}

	public T load(Serializable id, boolean returnProxy) {

		if (returnProxy) {
			return (T) getSession().load(this.clazz, id);
		} else {
			return (T) getSession().get(this.clazz, id);
		}
	}

	public void atach(T entity) {
		//Apenas deixa o objeto gerenciado pelo Hibernate
		getSession().lock(entity, LockMode.NONE);
	}

	public void deatach(Object entity) {
		//remove da sess�o, tornando transient
		getSession().evict(entity);
	}

	public void refresh(T entity) {
		getSession().refresh(entity);
	}

	public void clear() {
		getSession().clear();
	}

	public void flush() {
		getSession().flush();
	}

	public int getCount() {
		Criteria criteria = createCriteria();
		Projection rowCount = Projections.rowCount();
		criteria = criteria.setProjection(rowCount);
		int count = ((Number) criteria.uniqueResult()).intValue();
		return count;
	}

	protected SQLQuery createSQLQuery(StringBuilder sql, Map<String, Object> parameters) {
		return createSQLQuery(sql.toString(), parameters);
	}

	protected SQLQuery createSQLQuery(String sql, Map<String, Object> parameters) {
		SQLQuery sqlQuery = getSession().createSQLQuery(sql);
		if (parameters != null) {
			setParameters(sqlQuery, parameters);
		}
		return sqlQuery;
	}

	protected void setParameters(Query query, Map<String, Object> parameters) {
		String[] keys = query.getNamedParameters();
		if (keys != null) {
			for (String key: keys) {
				Object value = parameters.get(key);
				if (value != null) {
					if (value instanceof Entity) {
						query.setEntity(key, value);
					}
					else if (value.getClass().isArray()) {
						query.setParameterList(key, (Object[]) value);
					}
					else if (value instanceof Collection<?>) {
						query.setParameterList(key, (Collection<?>) value);
					}
					else {
						query.setParameter(key, value);
					}
				}
			}
		}
	}
}
