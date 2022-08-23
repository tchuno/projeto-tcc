package net.gnfe.bin.bean;

import net.gnfe.bin.bean.datamodel.ProdutoDataModel;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.service.ProdutoService;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.faces.AbstractBean;
import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.InputStream;
import java.util.List;

@ManagedBean
@ViewScoped
public class ProdutoCrudBean extends AbstractBean {

    @Autowired private ProdutoService service;
    @Autowired private UsuarioService usuarioService;

    private ProdutoDataModel dataModel;
    private ProdutoFiltro filtro = new ProdutoFiltro();
    private Produto produto = new Produto();
    private List<Usuario> fornecedores;

    public void initBean() {
        dataModel = new ProdutoDataModel();
        dataModel.setService(service);
        dataModel.setFiltro(filtro);

        UsuarioFiltro filtro = new UsuarioFiltro();
        filtro.setRoleGNFE(RoleGNFE.FORNECEDOR);
        fornecedores = usuarioService.findByFiltro(filtro);
    }

    public void salvar() {

        try {
            boolean insert = isInsert(produto);
            service.saveOrUpdate(produto);

            addMessage(insert ? "registroCadastrado.sucesso" : "registroAlterado.sucesso");
        }
        catch (Exception e) {
            addMessageError(e);
        }
    }

    public void excluir() {

        Long produtoId = produto.getId();

        try {
            service.excluir(produtoId);

            addMessage("registroExcluido.sucesso");
        }
        catch (Exception e) {
            addMessageError(e);
        }
    }

    public void uploadEvent(FileUploadEvent event) {

        UploadedFile updateFile = event.getFile();
        if(updateFile != null) {

            try {
                String fileName = updateFile.getFileName();
                String extensao = DummyUtils.getExtensao(fileName);
                if(!"csv".equals(extensao)) {
                    addMessageError("importacao.arquivoInvalido.error");
                    return;
                }

                InputStream inputstream = updateFile.getInputstream();
                String tempDirectoryPath = FileUtils.getTempDirectoryPath();
                File dir = new File(tempDirectoryPath);
                File fileDestino = DummyUtils.getFileDestino(dir, fileName);
                FileUtils.copyInputStreamToFile(inputstream, fileDestino);
                fileName = fileDestino.getName();

                Usuario usuario = getUsuarioLogado();

                service.iniciarProcessamentoDoArquivo(fileDestino, usuario, fileName);

                addMessage("importacao.sucesso");
            }
            catch (Exception e) {
                addMessageError(e);
            }
        }
    }

    public ProdutoDataModel getDataModel() {
        return dataModel;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {

        if(produto == null) {
            produto = new Produto();
        }

        this.produto = produto;
    }

    public ProdutoFiltro getFiltro() {
        return filtro;
    }

    public void setFiltro(ProdutoFiltro filtro) {
        this.filtro = filtro;
    }

    public List<Usuario> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<Usuario> fornecedores) {
        this.fornecedores = fornecedores;
    }
}
