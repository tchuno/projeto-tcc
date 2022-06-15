package net.gnfe.bin.restws.dto;

import net.gnfe.bin.domain.entity.Usuario;

public class UsuarioDTO {

	public Long id;
	public String nome;
	public String email;
	public String telefone;
	public String cpf;
	
	public static UsuarioDTO from(Usuario usuario) {
		UsuarioDTO model = new UsuarioDTO();
		model.id = usuario.getId();
		model.nome = usuario.getNome();
		model.email = usuario.getEmail();
		model.telefone = usuario.getTelefone();
		model.cpf = usuario.getCpf();
		return model;
	}
}
