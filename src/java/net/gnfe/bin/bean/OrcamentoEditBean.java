package net.gnfe.bin.bean;

import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.service.OrcamentoService;
import net.gnfe.bin.domain.service.ProdutoService;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.faces.AbstractBean;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
public class OrcamentoEditBean extends AbstractBean {

    @Autowired private OrcamentoService service;
    @Autowired private ProdutoService produtoService;
    @Autowired private UsuarioService usuarioService;

    private Long id;
    private Orcamento orcamento;
    private List<Usuario> clientes;
    private List<Produto> produtos;
    private Produto produto;
    private List<Produto> produtosOrcamento = new ArrayList<>();
    private List<Produto> produtosSelecionados = new ArrayList<>();

    public void initBean() {

        if (id != null) {
            this.orcamento = service.get(id);
            this.produtosOrcamento = orcamento.getProdutos().stream().map(OrcamentoProduto::getProduto).collect(Collectors.toList());
        } else {
            this.orcamento = new Orcamento();
        }

        UsuarioFiltro filtro = new UsuarioFiltro();
        filtro.setRoleGNFE(RoleGNFE.CLIENTE);
        clientes = usuarioService.findByFiltro(filtro);

        ProdutoFiltro produtoFiltro = new ProdutoFiltro();
        produtoFiltro.setOrdenar("produto.nome", SortOrder.ASCENDING);
        produtos = produtoService.findByFiltro(produtoFiltro);

    }

    public void salvar() {

        try {
            boolean insert = isInsert(orcamento);

            Usuario usuarioLogado = getUsuarioLogado();
            orcamento.setAutor(usuarioLogado);
            service.saveOrUpdate(orcamento, produtosOrcamento);

            addMessage(insert ? "registroCadastrado.sucesso" : "registroAlterado.sucesso");
        }
        catch (Exception e) {
            addMessageError(e);
        }
    }

    public String getDeleteButtonMessage() {
        if (hasSelectedProducts()) {
            int size = this.produtosSelecionados.size();
            return size > 1 ? size + getMessage("produtosSelecionados.label") : getMessage("umProdutoSelecionado.label");
        }

        return getMessage("remover.label");
    }

    public void deleteProduct(Produto produtoRemover) {
        this.produtosOrcamento.remove(produtoRemover);
        this.produtosSelecionados.remove(produtoRemover);
    }

    public void deleteSelectedProducts() {
        this.produtosOrcamento.removeAll(this.produtosSelecionados);
        this.produtosSelecionados = null;
    }

    public boolean hasSelectedProducts() {
        return this.produtosSelecionados != null && !this.produtosSelecionados.isEmpty();
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento Orcamento) {

        if(Orcamento == null) {
            Orcamento = new Orcamento();
        }

        this.orcamento = Orcamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Usuario> getClientes() {
        return clientes;
    }

    public void setClientes(List<Usuario> clientes) {
        this.clientes = clientes;
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public List<Produto> getProdutosOrcamento() {
        return produtosOrcamento;
    }

    public void setProdutosOrcamento(List<Produto> produtosOrcamento) {
        this.produtosOrcamento = produtosOrcamento;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public List<Produto> getProdutosSelecionados() {
        return produtosSelecionados;
    }

    public void setProdutosSelecionados(List<Produto> produtosSelecionados) {
        this.produtosSelecionados = produtosSelecionados;
    }

}
