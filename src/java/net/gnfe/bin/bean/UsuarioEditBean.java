package net.gnfe.bin.bean;

import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.MotivoDesativacaoUsuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.enumeration.StatusUsuario;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.faces.AbstractBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ManagedBean
@ViewScoped
public class UsuarioEditBean extends AbstractBean {

	@Autowired private UsuarioService usuarioService;

	private Long id;
	private Usuario usuario;
	private UsuarioAutorizacao autorizacao = new UsuarioAutorizacao();
	private List<MotivoDesativacaoUsuario> motivoDesativacaoUsuarios = new ArrayList<>();

	protected void initBean() {

		Usuario usuarioLogado = getUsuarioLogado();
		this.autorizacao.setUsuarioLogado(usuarioLogado);
		if (usuario == null) {
			if (id != null) {
				this.usuario = usuarioService.get(id);
			} else {
				this.usuario = new Usuario();
			}
			this.autorizacao.setUsuario(usuario);
		}

		this.motivoDesativacaoUsuarios = MotivoDesativacaoUsuario.getNotAllMotivoDesativacaoUsuario();
	}

	public void salvar() {

		try {
			boolean insert = isInsert(usuario);
			Usuario usuarioLogado = getUsuarioLogado();

			usuarioService.saveOrUpdate(usuario, usuarioLogado);
			addMessage(insert ? "registroCadastrado.sucesso" : "registroAlterado.sucesso");

			if(insert) {
				redirect("/cadastros/usuarios/usuario-edit.xhtml?id=" + usuario.getId());
			}

			initBean();
		}
		catch (MessageKeyException e) {
			addMessageError(e);
		}
	}

	public void cadastrar() {

		try {
			boolean insert = isInsert(usuario);

			String cpf = usuario.getCpf();
			if(!DummyUtils.isCpfValido(cpf)) {
				throw new MessageKeyException("cpfInvalido.error");
			}

			usuario.setRoleGNFE(RoleGNFE.PESSOA);
			usuarioService.saveOrUpdate(usuario, null);
			addMessage(insert ? "registroCadastrado.sucesso" : "registroAlterado.sucesso");

			if(insert) {
				redirect("/");
			}
		}
		catch (MessageKeyException e) {
			addMessageError(e);
		}
	}

	public void reiniciarSenha() {

		Usuario usuarioLogado = getUsuarioLogado();
		usuarioService.reiniciarSenha(usuario, usuarioLogado);

		addMessage("senhaResetada.sucesso");
		redirect("/cadastros/usuarios/edit/?id=" + usuario.getId());
	}

	public void desativarUsuario() {

		Usuario usuarioLogado = getUsuarioLogado();

		usuarioService.desativarUsuario(usuario, usuarioLogado);

		addMessage("usuarioDesativado.sucesso");
		redirect("/cadastros/usuarios/edit/?id=" + usuario.getId());
	}

	public void ativarUsuario() {

		Usuario usuarioLogado = getUsuarioLogado();

		usuarioService.ativarUsuario(usuario, usuarioLogado);

		addMessage("usuarioAtivado.sucesso");
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public List<RoleGNFE> getRolesBF() {
		return Arrays.asList(RoleGNFE.values());
	}

	public boolean getRenderDesativarBtn() {
		StatusUsuario status = usuario.getStatus();
		return !StatusUsuario.INATIVO.equals(status);
	}

	public boolean getRenderAtivarBtn() {
		StatusUsuario status = usuario.getStatus();
		return !StatusUsuario.ATIVO.equals(status);
	}

	public UsuarioAutorizacao getAutorizacao() {
		return autorizacao;
	}

	public List<MotivoDesativacaoUsuario> getMotivoDesativacaoUsuarios() {
		return motivoDesativacaoUsuarios;
	}
}
