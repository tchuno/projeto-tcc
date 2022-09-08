package net.gnfe.bin.domain.enumeration;

import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.util.menu.Item;

import java.util.*;

public enum Funcionalidade {
	PRODUTOS(
			new P(RoleGNFE.ADMIN, true, true, true, true),
			new P(RoleGNFE.FUNCIONARIO, false, false, false, false),
			new P(RoleGNFE.CLIENTE, false, false, false, false)),
	ORCAMENTOS(
			new P(RoleGNFE.ADMIN, true, true, true, true),
			new P(RoleGNFE.FUNCIONARIO, true, true, true, true),
			new P(RoleGNFE.CLIENTE, true, true, true, true)),
	ORCAMENTOS_EDIT(
			new P(RoleGNFE.ADMIN, true, true, true, true),
			new P(RoleGNFE.FUNCIONARIO, false, false, false, false),
			new P(RoleGNFE.CLIENTE, false, false, false, false)),
	USUARIOS(
			new P(RoleGNFE.ADMIN, true, true, true, true),
			new P(RoleGNFE.FUNCIONARIO, false, false, false, false),
			new P(RoleGNFE.CLIENTE, false, false, false, false)),
	USUARIOS_EDIT(
			new P(RoleGNFE.ADMIN, true, true, true, true),
			new P(RoleGNFE.FUNCIONARIO, false, false, false, false),
			new P(RoleGNFE.CLIENTE, false, false, false, false)),
	CUSTOMIZACAO(
			new P(RoleGNFE.ADMIN, true, true, true, true),
			new P(RoleGNFE.FUNCIONARIO, false, false, false, false),
			new P(RoleGNFE.CLIENTE, false, false, false, false)),
	;

	private Map<RoleGNFE, P> permissoesMap = new HashMap<>();

	private Funcionalidade(P... permissoes) {
		for (P p : permissoes) {
			RoleGNFE role = p.getRole();
			permissoesMap.put(role, p);
		}
	}

	public boolean isVisualizavel(Usuario usuario) {
		RoleGNFE role = usuario.getRoleGNFE();
		P permissao = permissoesMap.get(role);
		return permissao != null && permissao.getPodeVisualizar(usuario);
	}

	public boolean isCadastravel(Usuario usuario) {
		RoleGNFE role = usuario.getRoleGNFE();
		P permissao = permissoesMap.get(role);
		return permissao != null && permissao.getPodeCadastrar(usuario);
	}

	public boolean isEditavel(Usuario usuario) {
		RoleGNFE role = usuario.getRoleGNFE();
		P permissao = permissoesMap.get(role);
		return permissao.getPodeEditar(usuario);
	}

	public boolean isExcluivel(Usuario usuario) {
		RoleGNFE role = usuario.getRoleGNFE();
		P permissao = permissoesMap.get(role);
		return permissao.getPodeExcluir(usuario);
	}

	/** Permissão */
	private static class P {

		private RoleGNFE role;
		private boolean podeVisualizar;
		private boolean podeCadastrar;
		private boolean podeEditar;
		private boolean podeExcluir;

		public P(RoleGNFE role, boolean podeVisualizar, boolean podeCadastrar, boolean podeEditar, boolean podeExcluir) {
			this.role = role;
			this.podeVisualizar = podeVisualizar;
			this.podeCadastrar = podeCadastrar;
			this.podeEditar = podeEditar;
			this.podeExcluir = podeExcluir;
		}

		public RoleGNFE getRole() {
			return role;
		}

		public boolean getPodeVisualizar(Usuario usuario) {
			return podeVisualizar;
		}

		public boolean getPodeCadastrar(Usuario usuario) {
			return podeCadastrar;
		}

		public boolean getPodeEditar(Usuario usuario) {
			return podeEditar;
		}

		public boolean getPodeExcluir(Usuario usuario) {
			return podeExcluir;
		}
	}

	public List<RoleGNFE> getRoles() {

		Set<RoleGNFE> keySet = permissoesMap.keySet();
		ArrayList<RoleGNFE> list = new ArrayList<>(keySet);
		return list;
	}

	public boolean podeAcessar(Item mi, Usuario usuario) {
		if(!isVisualizavel(usuario)) {
			return false;
		}
		return true;
	}
}
