package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.vo.filtro.MovimentacaoProdutoFiltro;
import net.gnfe.util.ddd.AbstractProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Scope("prototype")
public class MovimentacaoProdutoExporter extends AbstractProcessor {

	@Autowired private MovimentacaoProdutoService movimentacaoProdutoService;

	private String fileName;
	private File file;

	private MovimentacaoProdutoFiltro filtro;

	@Override
	protected void execute2() throws Exception {

		System.out.println("RelatorioGeralExporter.execute2()");

		file = movimentacaoProdutoService.render(filtro);
		fileName = "relatorio-geral.xlsx";

		System.out.println("RelatorioGeralExporter.execute2() end");
	}

	public String getFileName() {
		return fileName;
	}

	public File getFile() {
		return file;
	}

	public void setFiltro(MovimentacaoProdutoFiltro filtro) {
		this.filtro = filtro;
	}
}
