package net.gnfe.bin.bean.datamodel;

import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

public class UsuarioDataModel extends LazyDataModel<Usuario> {

	private UsuarioService usuarioService;
	private List<Usuario> list;
	private UsuarioFiltro filtro;

	@Override
	public List<Usuario> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		int count = usuarioService.countByFiltro(filtro);
		setRowCount(count);

		if(count == 0) {
			return null;
		}

		list = usuarioService.findByFiltro(filtro, first, pageSize);

		return list;
	}

	public void setService(UsuarioService logAlteracaoService) {
		this.usuarioService = logAlteracaoService;
	}

	public void setFiltro(UsuarioFiltro filtro) {
		this.filtro = filtro;
	}
}
