package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.Role;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.MotivoBloqueioUsuario;
import net.gnfe.bin.domain.enumeration.MotivoDesativacaoUsuario;
import net.gnfe.bin.domain.enumeration.StatusUsuario;
import net.gnfe.bin.domain.repository.UsuarioRepository;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.other.Criptografia;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {
	
	public static String SEPARADOR = "#SEP;#";
	public static int QTD_SENHAS_ANTERIORES = 3;

	@Autowired private UsuarioRepository usuarioRepository;

	public Usuario get(Long id) {
		return usuarioRepository.get(id);
	}

	public Usuario getByLogin(String login) {
		return usuarioRepository.getByLogin(login);
	}

	public Usuario autenticar(String login, String senha) {
		return usuarioRepository.autenticar(login, senha);
	}

	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(Usuario usuario, Usuario usuarioLogado) throws MessageKeyException {

		boolean isNew = usuario.getId() == null;

		String login = usuario.getCpfCnpj().trim();
		Date agora = new Date();
		usuario.setDataAtualizacao(agora);
		usuario.setUsuarioUltimaAtualizacao(usuarioLogado);
		usuario.setLogin(login);

		Date dataCadastro = usuario.getDataCadastro();
		if(dataCadastro == null) {
			usuario.setDataCadastro(agora);
		}

		StatusUsuario status = usuario.getStatus();
		if(status == null) {
			usuario.setStatus(StatusUsuario.ATIVO);
		}

		String senha = usuario.getSenha();
		if(StringUtils.isEmpty(senha)) {
			usuario.setSenha(login);
			usuario.setDataExpiracaoSenha(agora);
		} else if (isNew) {
			usuario.setDataExpiracaoSenha(agora);
		}

		try {
			usuarioRepository.saveOrUpdate(usuario);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}

	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long usuarioId) throws MessageKeyException {
		try {
			usuarioRepository.deleteById(usuarioId);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<Usuario> findByFiltro(UsuarioFiltro filtro) {
		return usuarioRepository.findByFiltro(filtro, null, null);
	}

	public List<Usuario> findByFiltro(UsuarioFiltro filtro, Integer inicio, Integer max) {
		return usuarioRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(UsuarioFiltro filtro) {
		return usuarioRepository.countByFiltro(filtro);
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario reiniciarSenha(Long usuarioId, Long usuarioLogadoId) {
		Usuario usuario = get(usuarioId);
		Usuario usuarioLogado = null;
		if(usuarioLogadoId != null) {
			usuarioLogado = get(usuarioLogadoId);
		}
		Usuario retorno = reiniciarSenha(usuario, usuarioLogado);
		return retorno;
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario reiniciarSenha(Usuario usuario, Usuario usuarioLogado) {

		Date agora = new Date();
		String login = usuario.getLogin();
		usuario.setSenha(login);
		usuario.setDataExpiracaoSenha(agora);
		usuario.setDataAtualizacao(agora);

		usuarioRepository.saveOrUpdate(usuario);

		return usuario;
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario desativarUsuario(Long usuarioId, MotivoDesativacaoUsuario motivoDesativacao, Long usuarioLogadoId) {
		Usuario usuario = get(usuarioId);
		Usuario usuarioLogado = get(usuarioLogadoId);
		usuario.setMotivoDesativacao(motivoDesativacao);
		Usuario retorno = desativarUsuario(usuario, usuarioLogado);
		return retorno;
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario desativarUsuario(Usuario usuario, Usuario usuarioLogado) {

		Date agora = new Date();
		usuario.setDataBloqueio(agora);
		usuario.setStatus(StatusUsuario.INATIVO);
		usuario.setDataExpiracaoBloqueio(null);
		usuario.setMotivoBloqueio(null);
		usuario.setDataAtualizacao(agora);

		usuarioRepository.saveOrUpdate(usuario);

		return usuario;
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario ativarUsuario(Long usuarioId, Long usuarioLogadoId) {
		Usuario usuario = get(usuarioId);
		Usuario usuarioLogado = usuarioLogadoId != null ? get(usuarioLogadoId) : null;
		Usuario retorno = ativarUsuario(usuario, usuarioLogado);
		return retorno;
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario ativarUsuario(Usuario usuario, Usuario usuarioLogado) {

		Date agora = new Date();
		usuario.setDataBloqueio(null);
		usuario.setMotivoBloqueio(null);
		usuario.setStatus(StatusUsuario.ATIVO);
		usuario.setMotivoDesativacao(null);
		usuario.setDataExpiracaoBloqueio(null);
		usuario.setDataAtualizacao(agora);
		usuario.setUsuarioUltimaAtualizacao(usuarioLogado);

		usuarioRepository.saveOrUpdate(usuario);

		return usuario;
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario login(String cpf) throws MessageKeyException {

		Usuario usuario = usuarioRepository.getByLogin(cpf);

		if(usuario == null) {
			return null;
		}

		StatusUsuario status = usuario.getStatus();
		if(StatusUsuario.BLOQUEADO.equals(status)) {

			boolean ativado = false;
			Date dataExpiracaoBloqueio = usuario.getDataExpiracaoBloqueio();

			if(dataExpiracaoBloqueio != null && dataExpiracaoBloqueio.before(new Date())) {

				ativarUsuario(usuario, null);
				ativado = true;
			}

			if(!ativado) {
				if(dataExpiracaoBloqueio != null) {
					throw new MessageKeyException("acessoBloqueadoTemporariamente.error");
				} else {
					throw new MessageKeyException("acessoBloqueado.error");
				}
			}
		}
		else if(StatusUsuario.INATIVO.equals(status)) {

			throw new MessageKeyException("acessoInativo.error");
		}

		usuario.setDataUltimoAcesso(new Date());
		usuarioRepository.saveOrUpdate(usuario);

		initUsuario(usuario);

		return usuario;
	}

	public void initUsuario(Usuario usuario) {

		Set<Role> roles = usuario.getRoles();

		for (Role role : roles) {
			usuarioRepository.deatach(role);
		}

		usuarioRepository.deatach(usuario);
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario bloquear(String email) throws MessageKeyException {

		Usuario usuario = usuarioRepository.getByLogin(email);

		if(usuario != null) {

			int tempoBloqueioMin = 10;
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MINUTE, tempoBloqueioMin);
			Date dataDesbloqueio = c.getTime();

			Date agora = new Date();
			usuario.setDataBloqueio(agora);
			usuario.setStatus(StatusUsuario.BLOQUEADO);
			usuario.setDataExpiracaoBloqueio(dataDesbloqueio);
			usuario.setMotivoBloqueio(MotivoBloqueioUsuario.TENTATIVAS);
			usuario.setDataAtualizacao(agora);

			usuarioRepository.saveOrUpdate(usuario);
		}

		return usuario;
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario enviarRedefinicaoSenha(String login) throws MessageKeyException {

		Usuario usuario = usuarioRepository.getByLogin(login);

		if(usuario == null) {
			throw new MessageKeyException("conta-nao-encontrada.error");
		}

		return usuario;
	}

	public static String encodeLogin(String email, String senha) {
		String aux = email + "\n\t\n" + senha;
		byte[] encodeBase64 = Base64.encodeBase64(aux.getBytes());
		aux = new String(encodeBase64);
		try {
			aux = URLEncoder.encode(aux, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return aux;
	}

	public static String[] decodeLogin(String str) {

		byte[] decodeBase64 = Base64.decodeBase64(str.getBytes());
		String aux = new String(decodeBase64);

		int indexOf = aux.indexOf("\n\t\n");
		if(indexOf <= 0) {
			return null;
		}

		String login = aux.substring(0, indexOf);
		String senha = aux.substring(indexOf + 3, aux.length());

		return new String[]{login, senha};
	}

	@Transactional(rollbackFor=Exception.class)
	public Usuario atualizarSenha(String login, String novaSenha) throws MessageKeyException {

		Usuario usuario = usuarioRepository.getByLogin(login);

		if(usuario == null) {
			throw new MessageKeyException("usuarioSenhaInvalido.error");
		}

		usuario.setSenha(novaSenha);
		validarSenhasAnteriores(usuario, novaSenha);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 90);
		Date dataExpiracaoSenha = c.getTime();
		dataExpiracaoSenha = DateUtils.truncate(dataExpiracaoSenha, Calendar.DAY_OF_MONTH);
		Date agora = new Date();
		usuario.setDataExpiracaoSenha(dataExpiracaoSenha);
		usuario.setDataAtualizacao(agora);

		usuarioRepository.saveOrUpdate(usuario);

		return usuario;
	}


	@Transactional(rollbackFor=Exception.class)
	public Usuario atualizarSenhaLocal(String login, String novaSenha) throws MessageKeyException {

		Usuario usuario = usuarioRepository.getByLogin(login);

		if(usuario == null) {
			throw new MessageKeyException("usuarioSenhaInvalido.error");
		}
		
		Boolean senhasAnterioresOK = validarSenhasAnteriores(usuario, novaSenha);
		
		if(!senhasAnterioresOK) {
			throw new MessageKeyException("usuarioSenhaRepetido.error");
		}
		
		if (novaSenha.length() < 8) {
			throw new MessageKeyException("criteriosSenhaInsuficientes.error");
		}
		
		int criteriosAtendidos = 0;
		if(novaSenha.matches(".*[A-Z].*"))
			criteriosAtendidos++;
		if(novaSenha.matches(".*[a-z].*"))
			criteriosAtendidos++;
		if(novaSenha.matches(".*[0-9].*"))
			criteriosAtendidos++;
		if(novaSenha.matches(".*[^A-Za-z0-9].*"))
			criteriosAtendidos++;
		if(criteriosAtendidos < 3)
			throw new MessageKeyException("criteriosSenhaInsuficientes.error");

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 90);
		Date dataExpiracaoSenha = c.getTime();
		dataExpiracaoSenha = DateUtils.truncate(dataExpiracaoSenha, Calendar.DAY_OF_MONTH);
		Date agora = new Date();
		
		usuario.setDataExpiracaoSenha(dataExpiracaoSenha);
		usuario.setDataAtualizacao(agora);
		usuario.setSenha(novaSenha);

		usuarioRepository.saveOrUpdate(usuario);

		return usuario;
	}

	private Boolean validarSenhasAnteriores(Usuario usuario, String novaSenha) {

		//todo aplicar decrypt aqui após criptografar senhas
		String senhasAnteriores = usuario.getSenhasAnteriores();

		if (StringUtils.isNotBlank(senhasAnteriores)) {
			List<String> senhas = Arrays.asList(senhasAnteriores.split(SEPARADOR));
			int count = 1;

			for (String senha : senhas) {
				if (senhas.size() - count >= QTD_SENHAS_ANTERIORES) {
					senhasAnteriores = senhasAnteriores.replace(senha + SEPARADOR, "");
				}
				String decrypt = Criptografia.decrypt(Criptografia.GNFE, senha);
				if (decrypt.equals(novaSenha)) {
					return false;
				}
				count++;
			}

			senhasAnteriores += SEPARADOR + Criptografia.encrypt(Criptografia.GNFE, novaSenha);
			usuario.setSenhasAnteriores(senhasAnteriores);
		} else {
			String senhaAnterior = usuario.getSenha();
			usuario.setSenhasAnteriores(senhaAnterior);
		}
		
		return true;
	}

	public List<Usuario> findClienteAutoComplete(String search) {
		return usuarioRepository.findClienteAutoComplete(search);
	}
}
