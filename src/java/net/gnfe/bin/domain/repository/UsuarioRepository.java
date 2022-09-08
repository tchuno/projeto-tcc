package net.gnfe.bin.domain.repository;

import net.gnfe.bin.domain.entity.Role;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.MotivoDesativacaoUsuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.enumeration.StatusUsuario;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.DummyUtils;
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
public class UsuarioRepository extends HibernateRepository<Usuario> {

	public UsuarioRepository() {
		super(Usuario.class);
	}

	public List<Usuario> findByFiltro(UsuarioFiltro filtro, Integer inicio, Integer max) {

		StringBuilder hql = new StringBuilder();

		hql.append( "select u from ").append(clazz.getName());
		hql.append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		hql.append("order by u.nome");

		Query query = createQuery(hql.toString(), params);

		if(inicio != null) {
			query.setFirstResult(inicio);
		}

		if(max != null) {
			query.setMaxResults(max);
		}

		List<Usuario> list = query.list();
		Set<Usuario> set = new LinkedHashSet<>(list);//pra tirar os repetidos
		return new ArrayList<Usuario>(set);
	}

	public int countByFiltro(UsuarioFiltro filtro) {

		StringBuilder hql = new StringBuilder();

		hql.append(" select count(distinct u.id) from ").append(clazz.getName()).append(" u ");

		Map<String, Object> params = makeQuery(filtro, hql);

		Query query = createQuery(hql.toString(), params);

		return ((Number) query.uniqueResult()).intValue();
	}

	private Map<String, Object> makeQuery(UsuarioFiltro filtro, StringBuilder hql) {

		StatusUsuario status = filtro.getStatus();
		RoleGNFE roleGNFE = filtro.getRoleGNFE();

		String login = filtro.getLogin();
		String nome = filtro.getNome();
		List<Long> ids = filtro.getIds();
		Date dataAtualizacao = filtro.getDataAtualizacao();
		MotivoDesativacaoUsuario motivoDesativacaoUsuario = filtro.getMotivoDesativacaoUsuario();
		List<String> logins = filtro.getLogins();
		String cpfCnpj = filtro.getCpfCnpj();
		String endereco = filtro.getEndereco();
		Integer numero = filtro.getNumero();
		String bairro = filtro.getBairro();
		String cep = filtro.getCep();
		String cidade = filtro.getCidade();
		String estado = filtro.getEstado();
		String email = filtro.getEmail();

		Map<String, Object> params = new HashMap<>();

		hql.append(" where 1=1 ");

		if (roleGNFE != null) {
			hql.append(" and (select count(*) from ").append(Role.class.getName()).append(" r where r.usuario.id = u.id and r.nome = :roleGNFE) > 0 ");
			params.put("roleGNFE", roleGNFE.name());
		}
		
		if(StringUtils.isNotBlank(login)) {
			hql.append(" and u.login = :login ");
			params.put("login", login);
		}
		
		if(StringUtils.isNotBlank(nome)) {
			hql.append(" and upper(u.nome) like :nome ");
			params.put("nome", "%" + nome.toUpperCase() + "%");
		}
		
		if(status != null) {
			hql.append(" and u.status = :status ");
			params.put("status", status);
		}

		if(ids != null && !ids.isEmpty()) {
			hql.append(" and u.id in :ids ");
			params.put("ids", ids);
		}

		if(dataAtualizacao != null) {
			hql.append(" and u.dataAtualizacao > :dataAtualizacao ");
			params.put("dataAtualizacao", dataAtualizacao);
		}

		if(motivoDesativacaoUsuario != null) {
			hql.append(" and u.motivoDesativacao = :motivoDesativacaoUsuario ");
			params.put("motivoDesativacaoUsuario", motivoDesativacaoUsuario);
		}

		if(logins != null && !logins.isEmpty()) {
			hql.append(" and u.login in ( :logins )");
			params.put("logins", logins);
		}

		if(StringUtils.isNotBlank(cpfCnpj)) {
			hql.append(" and u.cpfCnpj = :cpfCnpj ");
			params.put("cpfCnpj", cpfCnpj);
		}

		if(StringUtils.isNotBlank(endereco)) {
			hql.append(" and upper(u.endereco) like :endereco ");
			params.put("endereco", "%" + endereco.toUpperCase() + "%");
		}

		if(numero != null) {
			hql.append(" and u.numero = :numero ");
			params.put("numero", numero);
		}

		if(StringUtils.isNotBlank(bairro)) {
			hql.append(" and upper(u.bairro) like :bairro ");
			params.put("bairro", "%" + bairro.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(cep)) {
			hql.append(" and u.cep = :cep ");
			params.put("cep", numero);
		}

		if(StringUtils.isNotBlank(cidade)) {
			hql.append(" and upper(u.cidade) like :cidade ");
			params.put("cidade", "%" + cidade.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(estado)) {
			hql.append(" and upper(u.estado) like :estado ");
			params.put("estado", "%" + estado.toUpperCase() + "%");
		}

		if(StringUtils.isNotBlank(email)) {
			hql.append(" and upper(u.email) like :email ");
			params.put("email", "%" + email.toUpperCase() + "%");
		}

		return params;
	}

	public Usuario getByLogin(String login) {

		login = StringUtils.lowerCase(login);

		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<>();

		hql.append(" select u ");
		hql.append(getStartQuery()).append(" u ");
		hql.append(" left outer join fetch u.roles rs ");
		hql.append(" where u.login = :login ");
		params.put("login", login);

		Query query = createQuery(hql.toString(), params);

		return (Usuario) query.uniqueResult();
	}

	public Usuario autenticar(String login, String senha) {

		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<>();

		hql.append(getStartQuery()).append(" u ");
		hql.append(" where u.login = :login ");
		hql.append(" and u.senha = :senha ");
		hql.append(" and (select count(*) from ");
		hql.append(Role.class.getName()).append(" r ");
		hql.append(" where r.usuario.id = u.id and r.nome = :roleGNFE) > 0 ");

		params.put("login", login);
		params.put("senha", senha);
		params.put("roleGNFE", RoleGNFE.ADMIN.name());

		Query query = createQuery(hql.toString(), params);

		return (Usuario) query.uniqueResult();
	}

    public List<Usuario> findClienteAutoComplete(String search) {

		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<>();

		hql.append( "select u from ").append(clazz.getName());
		hql.append(" u ");
		hql.append(" where 1=1 ");
		hql.append(" and upper(u.nome) like :nome or u.cpfCnpj like :cpfCnpj ");
		hql.append("order by u.nome");

		params.put("nome", "%" + search.toUpperCase().trim() + "%");
		params.put("cpfCnpj", "%" + DummyUtils.getCpfCnpjDesformatado(search.trim()) + "%");

		Query query = createQuery(hql.toString(), params);
		query.setMaxResults(10);

		return query.list();
    }
}
