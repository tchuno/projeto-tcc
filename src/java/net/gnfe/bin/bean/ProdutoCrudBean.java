package net.gnfe.bin.bean;

import net.gnfe.bin.bean.datamodel.ProdutoDataModel;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.service.ProdutoService;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.faces.AbstractBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class ProdutoCrudBean extends AbstractBean {

    @Autowired private ProdutoService service;
    @Autowired private UsuarioService usuarioService;

    private ProdutoDataModel dataModel;
    private ProdutoFiltro produtoFiltro;
    private Produto produto;
    private List<Usuario> fornecedores;

    public void initBean() {
        dataModel = new ProdutoDataModel();
        dataModel.setService(service);
        dataModel.setFiltro(new ProdutoFiltro());

        UsuarioFiltro filtro = new UsuarioFiltro();
        filtro.setRoleGNFE(RoleGNFE.FORNCEDOR);
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

    public ProdutoFiltro getProdutoFiltro() {
        return produtoFiltro;
    }

    public void setProdutoFiltro(ProdutoFiltro produtoFiltro) {
        this.produtoFiltro = produtoFiltro;
    }

    public List<Usuario> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<Usuario> fornecedores) {
        this.fornecedores = fornecedores;
    }
}
