package net.gnfe.bin.bean;

import net.gnfe.bin.domain.entity.NotaFiscal;
import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;
import net.gnfe.bin.domain.service.OrcamentoService;
import net.gnfe.bin.domain.service.ProdutoService;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.faces.AbstractBean;
import org.omnifaces.util.Faces;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
    private OrcamentoProduto orcamentoProduto;
    private List<OrcamentoProduto> produtosSelecionados = new ArrayList<>();

    public void initBean() {

        if (id != null) {
            this.orcamento = service.get(id);
        } else {
            this.orcamento = new Orcamento();
            NotaFiscal notaFiscal = new NotaFiscal();
            notaFiscal.setOrcamento(orcamento);
            notaFiscal.setDataCriacao(new Date());
            notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.PENDENTE);
            orcamento.setNotaFiscal(notaFiscal);
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
            service.saveOrUpdate(orcamento);

            addMessage(insert ? "registroCadastrado.sucesso" : "registroAlterado.sucesso");
        }
        catch (Exception e) {
            addMessageError(e);
        }
    }

    public String getDeleteButtonMessage() {
        if (hasSelectedProducts()) {
            int size = this.produtosSelecionados.size();
            return size > 1 ? size + " "+getMessage("produtosSelecionados.label") : getMessage("umProdutoSelecionado.label");
        }

        return getMessage("remover.label");
    }

    public void deleteProduct(OrcamentoProduto orcamentoProduto) {
        this.orcamento.getOrcamentoProdutos().remove(orcamentoProduto);
        this.produtosSelecionados.remove(orcamentoProduto);
    }

    public void deleteSelectedProducts() {
        this.produtosSelecionados.forEach(this.orcamento.getOrcamentoProdutos()::remove);
        this.produtosSelecionados = null;
    }

    public boolean hasSelectedProducts() {
        return this.produtosSelecionados != null && !this.produtosSelecionados.isEmpty();
    }

    public BigDecimal totalGeral(Set<OrcamentoProduto> orcamentoProdutos) {
        BigDecimal totalGeral = new BigDecimal(0);
        for(OrcamentoProduto orcamentoProduto : orcamentoProdutos) {
            Produto produto = orcamentoProduto.getProduto();
            BigDecimal valorUnidade = produto.getValorUnidade();
            Integer quantidade = orcamentoProduto.getQuantidade();
            valorUnidade = valorUnidade.multiply(new BigDecimal(quantidade));
            totalGeral = totalGeral.add(valorUnidade);
        }
        return totalGeral;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {

        if(orcamento == null) {
            orcamento = new Orcamento();
            NotaFiscal notaFiscal = new NotaFiscal();
            notaFiscal.setOrcamento(orcamento);
            notaFiscal.setDataCriacao(new Date());
            notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.PENDENTE);
            this.orcamento.setNotaFiscal(notaFiscal);
        }

        this.orcamento = orcamento;
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

    public OrcamentoProduto getOrcamentoProduto() {
        return orcamentoProduto;
    }

    public void setOrcamentoProduto(OrcamentoProduto orcamentoProduto) {
        if(orcamentoProduto == null) {
            orcamentoProduto = new OrcamentoProduto();
            orcamentoProduto.setOrcamento(orcamento);
        }
        this.orcamentoProduto = orcamentoProduto;
    }

    public List<OrcamentoProduto> getProdutosSelecionados() {
        return produtosSelecionados;
    }

    public void setProdutosSelecionados(List<OrcamentoProduto> produtosSelecionados) {
        this.produtosSelecionados = produtosSelecionados;
    }

    public void gerarOrcamento() {

        File file = DummyUtils.getFileFromResource("/net/gnfe/pdf/nota-fiscal.pdf");
        try {
            FileInputStream fis = new FileInputStream(file);
            Faces.sendFile(fis, "nota-fiscal.pdf", false);
        }
        catch (Exception e1) {
            addMessageError(e1);
        }
    }
}
