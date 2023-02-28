package net.gnfe.bin.bean;

import net.gnfe.bin.GNFEConstants;
import net.gnfe.bin.bean.datamodel.ProdutoDataModel;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.MotivoMovimentacao;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.service.*;
import net.gnfe.bin.domain.vo.MovimentacaoProdutoVO;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.faces.AbstractBean;
import org.apache.commons.io.FileUtils;
import org.omnifaces.util.Ajax;
import org.omnifaces.util.Faces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.SortOrder;
import org.primefaces.model.file.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class ProdutoCrudBean extends AbstractBean {

    @Autowired private ProdutoService service;
    @Autowired private MovimentacaoProdutoService movimentacaoProdutoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private ApplicationContext applicationContext;

    private ProdutoExporter exporter;
    private ProdutoDataModel dataModel;
    private ProdutoFiltro filtro = new ProdutoFiltro();
    private Produto produto = new Produto();
    private List<Usuario> fornecedores;
    private List<Produto> produtos;
    private MovimentacaoProdutoVO movimentacaoProdutoVO;

    public void initBean() {
        dataModel = new ProdutoDataModel();
        dataModel.setService(service);
        dataModel.setFiltro(filtro);

        UsuarioFiltro filtro = new UsuarioFiltro();
        filtro.setRoleGNFE(RoleGNFE.FORNECEDOR);
        fornecedores = usuarioService.findByFiltro(filtro);

        carregarProdutos();
    }

    private void carregarProdutos() {
        ProdutoFiltro produtoFiltro = new ProdutoFiltro();
        produtoFiltro.setOrdenar("produto.nome", SortOrder.ASCENDING);
        produtos = service.findByFiltro(produtoFiltro);
    }

    public void salvar() {

        try {
            boolean insert = isInsert(produto);
            service.saveOrUpdate(produto);

            if(insert) {
                MovimentacaoProdutoVO vo = new MovimentacaoProdutoVO();
                vo.setData(new Date());
                vo.setMotivoMovimentacao(MotivoMovimentacao.CRIACAO_PRODUTO);
                vo.setEntrada(true);
                vo.setProduto(produto);
                vo.setQuantidade(produto.getEstoqueAtual());
                movimentacaoProdutoService.movimentarProduto(vo);
            }

            carregarProdutos();

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

            carregarProdutos();

            addMessage("registroExcluido.sucesso");
        }
        catch (Exception e) {
            addMessageError(e);
        }
    }

    public void importarArquivo(FileUploadEvent event) {

        UploadedFile updateFile = event.getFile();
        if(updateFile != null) {

            try {
                String fileName = updateFile.getFileName();
                String extensao = DummyUtils.getExtensao(fileName);
                if(!"csv".equals(extensao)) {
                    addMessageError("importacao.arquivoInvalido.error");
                    return;
                }

                InputStream inputstream = updateFile.getInputStream();
                File fileDestino = File.createTempFile("importacao", "."+ extensao);;
                FileUtils.copyInputStreamToFile(inputstream, fileDestino);

                Usuario usuarioLogado = getUsuarioLogado();

                service.iniciarProcessamentoDoArquivo(fileDestino, usuarioLogado);

                addMessage("importacao.sucesso");
            }
            catch (Exception e) {
                addMessageError(e);
            }
        }
    }

    public void carregarImagemProduto(FileUploadEvent event) {

        UploadedFile updateFile = event.getFile();
        if(updateFile != null) {

            try {
                String fileName = updateFile.getFileName();
                String extensao = DummyUtils.getExtensao(fileName);
                if(!GNFEConstants.IMAGEM_EXTENSOES.contains(extensao)) {
                    addMessageError("importacao.arquivoInvalido.error");
                    return;
                }

                InputStream inputstream = updateFile.getInputStream();
                File fileDestino = File.createTempFile("uploadedImagePrint_", "."+ extensao);;
                FileUtils.copyInputStreamToFile(inputstream, fileDestino);

                byte[] bytes = FileUtils.readFileToByteArray(fileDestino);
                Base64.Encoder encoder = Base64.getEncoder();
                String fileBase64 = encoder.encodeToString(bytes);

                produto.setNomeImagem(fileName);
                produto.setImagemBase64(fileBase64);

                addMessage("importacao.sucesso");
            }
            catch (Exception e) {
                addMessageError(e);
            }
        }
    }

    public void movimentarProduto() {
        movimentacaoProdutoService.movimentarProduto(movimentacaoProdutoVO);
        setRequestAttribute("fecharModal", true);
        addMessage("movimentacao.sucesso");
    }

    public void baixar() {

        Usuario usuario = getUsuarioLogado();
        String login = usuario.getLogin();
        DummyUtils.sysout("ProdutoCrudBean.baixar() " + login + " " + DummyUtils.getLogMemoria());

        Exception error = exporter.getError();
        if(error != null) {
            addMessageError(error);
        }
        else {
            File file = exporter.getFile();
            try {
                FileInputStream fis = new FileInputStream(file);
                Faces.sendFile(fis, "produtos.xlsx", false);
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
            DummyUtils.sysout("ProdutoCrudBean.verificar() " + login + " null " + DummyUtils.getLogMemoria());
            return;
        }

        if(exporter.isFinalizado()) {
            DummyUtils.sysout("ProdutoCrudBean.verificar() " + login + " finalizado " + DummyUtils.getLogMemoria());
            Ajax.data("terminou", true);
        }
        else {
            DummyUtils.sysout("ProdutoCrudBean.verificar() " + login + " não finalizado " + DummyUtils.getLogMemoria());
            Ajax.data("terminou", false);
        }
    }

    public void exportar() {

        if(exporter == null) {
            DummyUtils.sysout("ProdutoCrudBean.exportar()" + DummyUtils.getLogMemoria());

            exporter = applicationContext.getBean(ProdutoExporter.class);
            exporter.setFiltro(filtro);
            exporter.start();
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

    public List<Produto> getProdutos() {
        return produtos;
    }

    public MovimentacaoProdutoVO getMovimentacaoProdutoVO() {
        return movimentacaoProdutoVO;
    }

    public void setMovimentacaoProdutoVO(MovimentacaoProdutoVO movimentacaoProdutoVO) {
        if(movimentacaoProdutoVO == null) {
            movimentacaoProdutoVO = new MovimentacaoProdutoVO();
            movimentacaoProdutoVO.setData(new Date());
            movimentacaoProdutoVO.setMotivoMovimentacao(MotivoMovimentacao.MOVIMENTACAO_ESTOQUE);
        }

        this.movimentacaoProdutoVO = movimentacaoProdutoVO;
    }
}
