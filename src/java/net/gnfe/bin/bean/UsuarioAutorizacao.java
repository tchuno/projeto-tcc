package net.gnfe.bin.bean;

import net.gnfe.bin.domain.entity.Usuario;

public class UsuarioAutorizacao {

	private Usuario usuario;
	private Usuario usuarioLogado;

	public Usuario getUsuario() {
		return usuario;
	}

	public Usuario getUsuarioLogado() {
		return usuarioLogado;
	}

	public void setUsuarioLogado(Usuario usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean podeAlterarSenha() {

		if(usuarioLogado.equals(usuario)) {
			return true;
		}

		return false;
	}
}
