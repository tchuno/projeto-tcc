package net.gnfe.bin.rest.request.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.rest.annotations.NotNull;

/**
 * 
 * @author jonas.baggio@wasys.com.br
 * @create 25 de jul de 2018 17:17:31
 */
@ApiModel(value = "RequestLogin")
public class RequestLogin {

	@ApiModelProperty(notes = "Login do usuário.")
	private String login;

	@ApiModelProperty(notes = "Senha do usuário.")
	private String senha;

	@NotNull(messageKey = "request.required.parameter", nomeCampo="Login")
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@NotNull(messageKey = "request.required.parameter", nomeCampo="Senha")
	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}
