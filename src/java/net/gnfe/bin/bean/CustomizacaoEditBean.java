package net.gnfe.bin.bean;

import net.gnfe.bin.domain.service.ParametroService;
import net.gnfe.util.faces.AbstractBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Map;

@ManagedBean
@ViewScoped
public class CustomizacaoEditBean extends AbstractBean {

	@Autowired private ParametroService parametroService;

	private Map<String, String> map;

	protected void initBean() {

		map = parametroService.getCustomizacao();
	}

	public void salvar() {

		try {
			parametroService.salvarCustomizacao(map);

			addMessage("registroAlterado.sucesso");
			redirect("/admin/customizacoes.xhtml");
		}
		catch (Exception e) {
			addMessageError(e);
		}
	}

	public void restaurarPadrao() {

		try {
			parametroService.restaurarPadrao();

			addMessage("registroAlterado.sucesso");
			redirect("/admin/customizacoes.xhtml");
		}
		catch (Exception e) {
			addMessageError(e);
		}
	}

	public Map<String, String> getMap() {
		return map;
	}

	public void setMap(Map<String, String> map) {
		this.map = map;
	}
}
