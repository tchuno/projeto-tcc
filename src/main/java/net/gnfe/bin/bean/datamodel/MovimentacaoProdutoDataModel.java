package net.gnfe.bin.bean.datamodel;


import net.gnfe.bin.domain.entity.MovimentacaoProduto;
import net.gnfe.bin.domain.service.MovimentacaoProdutoService;
import net.gnfe.bin.domain.vo.filtro.MovimentacaoProdutoFiltro;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

public class MovimentacaoProdutoDataModel extends LazyDataModel<MovimentacaoProduto> {

	private MovimentacaoProdutoService movimentacaoProdutoService;
	private List<MovimentacaoProduto> list;
	private MovimentacaoProdutoFiltro filtro;

	@Override
	public List<MovimentacaoProduto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, FilterMeta> filters) {

		int count = movimentacaoProdutoService.countByFiltro(filtro);
		setRowCount(count);

		if(count == 0) {
			return null;
		}

		list = movimentacaoProdutoService.findByFiltro(filtro, first, pageSize);

		return list;
	}

	public void setService(MovimentacaoProdutoService movimentacaoProdutoService) {
		this.movimentacaoProdutoService = movimentacaoProdutoService;
	}

	public void setFiltro(MovimentacaoProdutoFiltro filtro) {
		this.filtro = filtro;
	}
}
