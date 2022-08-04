package net.gnfe.bin.bean.datamodel;

import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.service.OrcamentoService;
import net.gnfe.bin.domain.vo.filtro.OrcamentoFiltro;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

public class OrcamentoDataModel extends LazyDataModel<Orcamento> {

	private OrcamentoService orcamentoService;
	private List<Orcamento> list;
	private OrcamentoFiltro filtro;

	@Override
	public List<Orcamento> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		int count = orcamentoService.countByFiltro(filtro);
		setRowCount(count);

		if(count == 0) {
			return null;
		}

		filtro.setOrdenar(sortField, sortOrder);

		list = orcamentoService.findByFiltro(filtro, first, pageSize);

		return list;
	}

	public void setService(OrcamentoService produtoService) {
		this.orcamentoService = produtoService;
	}

	public void setFiltro(OrcamentoFiltro filtro) {
		this.filtro = filtro;
	}
}
