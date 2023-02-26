package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.util.ddd.AbstractProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Scope("prototype")
public class ProdutoExporter extends AbstractProcessor {

	@Autowired private ProdutoService produtoService;

	private String fileName;
	private File file;

	private ProdutoFiltro filtro;

	@Override
	protected void execute2() throws Exception {

		System.out.println("ProdutoExporter.execute2()");

		file = produtoService.render(filtro);
		fileName = "produtos.xlsx";

		System.out.println("ProdutoExporter.execute2() end");
	}

	public String getFileName() {
		return fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFiltro(ProdutoFiltro filtro) {
		this.filtro = filtro;
	}
}
