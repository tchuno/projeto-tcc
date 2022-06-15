package net.gnfe.bin.bean;

import net.gnfe.bin.bean.datamodel.UsuarioDataModel;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.StatusUsuario;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.faces.AbstractBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class UsuarioListBean extends AbstractBean {

	@Autowired private UsuarioService usuarioService;

	private Long usuarioId;
	private boolean bloqueadosHide = true;
	private boolean inativosHide = true;
	private UsuarioDataModel ativosDataModel;
	private UsuarioDataModel bloqueadosDataModel;
	private UsuarioDataModel inativosDataModel;
	private int countBloqueados;
	private int countInativos;
	private UsuarioFiltro filtro = new UsuarioFiltro();

	protected void initBean() {
		buscar();
	}

	public void buscar() {

		UsuarioFiltro filtro1 = filtro.clone();
		filtro1.setStatus(StatusUsuario.ATIVO);
		ativosDataModel = new UsuarioDataModel();
		ativosDataModel.setFiltro(filtro1);
		ativosDataModel.setService(usuarioService);

		UsuarioFiltro filtro2 = filtro.clone();
		filtro2.setStatus(StatusUsuario.BLOQUEADO);
		bloqueadosDataModel = new UsuarioDataModel();
		bloqueadosDataModel.setFiltro(filtro2);
		bloqueadosDataModel.setService(usuarioService);
		countBloqueados = usuarioService.countByFiltro(filtro2);

		UsuarioFiltro filtro3 = filtro.clone();
		filtro3.setStatus(StatusUsuario.INATIVO);
		inativosDataModel = new UsuarioDataModel();
		inativosDataModel.setFiltro(filtro3);
		inativosDataModel.setService(usuarioService);
		countInativos = usuarioService.countByFiltro(filtro3);
	}
	
	public void excluir() {

		Usuario usuarioLogado = getUsuarioLogado();

		try {
			usuarioService.excluir(usuarioId, usuarioLogado);

			bloqueadosHide = true;
			inativosHide = true;

			addMessage("registroExcluido.sucesso");
		}
		catch (Exception e) {
			addMessageError(e);
		}
	}

	public void carregarBloqueados() {

		if(bloqueadosHide) {
			bloqueadosHide = false;
		} else {
			bloqueadosHide = true;
		}
	}

	public void carregarInativos() {

		if(inativosHide) {
			inativosHide = false;
		} else {
			inativosHide = true;
		}
	}

	public boolean isBloqueadosHide() {
		return bloqueadosHide;
	}

	public boolean isInativosHide() {
		return inativosHide;
	}

	public UsuarioDataModel getAtivosDataModel() {
		return ativosDataModel;
	}

	public UsuarioDataModel getBloqueadosDataModel() {
		return bloqueadosDataModel;
	}

	public UsuarioDataModel getInativosDataModel() {
		return inativosDataModel;
	}

	public Long getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(Long usuarioId) {
		this.usuarioId = usuarioId;
	}

	public int getCountBloqueados() {
		return countBloqueados;
	}

	public int getCountInativos() {
		return countInativos;
	}

	public UsuarioFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(UsuarioFiltro filtro) {
		this.filtro = filtro;
	}
}
