package net.gnfe.bin.bean;

import net.gnfe.bin.bean.datamodel.MovimentacaoProdutoDataModel;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.service.MovimentacaoProdutoService;
import net.gnfe.bin.domain.service.MovimentacaoProdutoExporter;
import net.gnfe.bin.domain.vo.filtro.MovimentacaoProdutoFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.faces.AbstractBean;
import org.omnifaces.util.Ajax;
import org.omnifaces.util.Faces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

@ManagedBean
@SessionScoped
public class MovimentacaoProdutoBean extends AbstractBean {

	@Autowired private MovimentacaoProdutoService movimentacaoProdutoService;
	@Autowired private ApplicationContext applicationContext;

	private MovimentacaoProdutoDataModel dataModel;
	private MovimentacaoProdutoExporter exporter;
	private MovimentacaoProdutoFiltro filtro = new MovimentacaoProdutoFiltro();

	protected void initBean() {

		Date date = new Date();
		filtro.setDataInicio(date);
		filtro.setDataFim(date);

		dataModel = new MovimentacaoProdutoDataModel();
		dataModel.setFiltro(filtro);
		dataModel.setService(movimentacaoProdutoService);

	}

	public void baixar() {

		Usuario usuario = getUsuarioLogado();
		String login = usuario.getLogin();
		DummyUtils.sysout("RelatorioGeralBean.baixar() " + login + " " + DummyUtils.getLogMemoria());

		Exception error = exporter.getError();
		if(error != null) {
			addMessageError(error);
		}
		else {
			File file = exporter.getFile();
			try {
				FileInputStream fis = new FileInputStream(file);
				Faces.sendFile(fis, "relatorio-geral.xlsx", false);
			}
			catch (Exception e1) {
				addMessageError(e1);
			}
		}

		exporter = null;
	}

	public void verificar() {

		Usuario usuario = getUsuarioLogado();
		String login = usuario.getLogin();

		if(exporter == null) {
			DummyUtils.sysout("RelatorioGeralBean.verificar() " + login + " null " + DummyUtils.getLogMemoria());
			return;
		}

		if(exporter.isFinalizado()) {
			DummyUtils.sysout("RelatorioGeralBean.verificar() " + login + " finalizado " + DummyUtils.getLogMemoria());
			Ajax.data("terminou", true);
		}
		else {
			DummyUtils.sysout("RelatorioGeralBean.verificar() " + login + " não finalizado " + DummyUtils.getLogMemoria());
			Ajax.data("terminou", false);
		}
	}

	public void exportar() {

		if(exporter == null) {

			Date dataInicio = filtro.getDataInicio();
			String dataInicioStr = DummyUtils.formatDate(dataInicio);
			Date dataFim = filtro.getDataFim();
			String dataFimStr = DummyUtils.formatDate(dataFim);
			DummyUtils.sysout("RelatorioGeralPage.exportar() Data Inicio: " + dataInicioStr + ". Data de Fim: " + dataFimStr + ". " + DummyUtils.getLogMemoria());

			exporter = applicationContext.getBean(MovimentacaoProdutoExporter.class);
			exporter.setFiltro(filtro);
			exporter.start();
		}
	}

	public MovimentacaoProdutoDataModel getDataModel() {
		return dataModel;
	}


	public MovimentacaoProdutoFiltro getFiltro() {
		return filtro;
	}

	public void setFiltro(MovimentacaoProdutoFiltro filtro) {
		this.filtro = filtro;
	}
}
