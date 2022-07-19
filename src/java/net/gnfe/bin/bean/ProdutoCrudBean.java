package net.gnfe.bin.bean;

import net.gnfe.bin.bean.datamodel.ProdutoDataModel;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.service.ProdutoService;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.util.faces.AbstractBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean
@ViewScoped
public class ProdutoCrudBean extends AbstractBean {

    @Autowired private ProdutoService service;

    private ProdutoDataModel dataModel;
    private ProdutoFiltro produtoFiltro;
    private Produto produto;

    public void initBean() {
        dataModel = new ProdutoDataModel();
        dataModel.setService(service);
        dataModel.setFiltro(new ProdutoFiltro());
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
}
