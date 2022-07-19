package net.gnfe.bin.bean.datamodel;

import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.service.ProdutoService;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

public class ProdutoDataModel extends LazyDataModel<Produto> {

	private ProdutoService produtoService;
	private List<Produto> list;
	private ProdutoFiltro filtro;

	@Override
	public List<Produto> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		int count = produtoService.countByFiltro(filtro);
		setRowCount(count);

		if(count == 0) {
			return null;
		}

		list = produtoService.findByFiltro(filtro, first, pageSize);

		return list;
	}

	public void setService(ProdutoService produtoService) {
		this.produtoService = produtoService;
	}

	public void setFiltro(ProdutoFiltro filtro) {
		this.filtro = filtro;
	}
}
