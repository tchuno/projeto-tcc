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
import net.gnfe.util.faces.AbstractBean;
import org.omnifaces.util.Faces;
import org.primefaces.model.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class OrcamentoEditBean extends AbstractBean {

    @Autowired private OrcamentoService service;
    @Autowired private ProdutoService produtoService;
    @Autowired private UsuarioService usuarioService;

    private Long id;
    private Orcamento orcamento;
    private Usuario usuarioNovo;
    private List<Produto> produtos;
    private OrcamentoProduto orcamentoProduto;
    private List<OrcamentoProduto> produtosSelecionados = new ArrayList<>();

    public void initBean() {

        if (id != null) {
            this.orcamento = service.get(id);
        } else {
            this.orcamento = new Orcamento();
            orcamento.setDataCriacao(new Date());
            NotaFiscal notaFiscal = new NotaFiscal();
            notaFiscal.setOrcamento(orcamento);
            notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.PENDENTE);
            orcamento.setNotaFiscal(notaFiscal);
        }

        usuarioNovo = new Usuario();

        ProdutoFiltro produtoFiltro = new ProdutoFiltro();
        produtoFiltro.setOrdenar("produto.nome", SortOrder.ASCENDING);
        produtoFiltro.setTemEmEstoque(true);
        produtos = produtoService.findByFiltro(produtoFiltro);

    }

    public void salvar() {

        try {
            boolean insert = isInsert(orcamento);
            service.saveOrUpdate(orcamento);

            addMessage(insert ? "registroCadastrado.sucesso" : "registroAlterado.sucesso");

            if(insert) {
                redirect("/cadastros/orcamentos/edit/" + orcamento.getId());
            }
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

    public void cadastrarUsuarioNovo() {
        Usuario usuarioLogado = getUsuarioLogado();
        usuarioNovo.setRoleGNFE(RoleGNFE.CLIENTE);
        usuarioService.saveOrUpdate(usuarioNovo, usuarioLogado);

        boolean insert = isInsert(usuarioNovo);

        addMessage(insert ? "registroCadastrado.sucesso" : "registroAlterado.sucesso");

        orcamento.setCliente(usuarioNovo);
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {

        if(orcamento == null) {
            orcamento = new Orcamento();
            orcamento.setDataCriacao(new Date());
            NotaFiscal notaFiscal = new NotaFiscal();
            notaFiscal.setOrcamento(orcamento);
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

        Usuario usuarioLogado = getUsuarioLogado();
        File file = service.gerarOrcamento(orcamento, usuarioLogado);
        try {
            FileInputStream fis = new FileInputStream(file);
            Faces.sendFile(fis, "orcamento.pdf", false);
        }
        catch (Exception e1) {
            addMessageError(e1);
        }
    }

    public void enviarNotaFiscal() {

        try {
            boolean insert = isInsert(orcamento);
            if(insert) {
                addMessageWarn("salvarAntesEmitirNota.label");
                return;
            }

            Usuario usuarioLogado = getUsuarioLogado();

            NotaFiscal notaFiscal = orcamento.getNotaFiscal();
            service.enviarNotaFiscal(notaFiscal, usuarioLogado);

            addMessage("notaEmitida.sucesso");
        }
        catch (Exception e1) {
            String message = e1.getMessage();
            addMessageError("erroInesperado.error", message);
        }
    }

    public void cancelarNotaFiscal() {

        try {

            NotaFiscal notaFiscal = orcamento.getNotaFiscal();
            Usuario usuarioLogado = getUsuarioLogado();
            service.cancelarNotaFiscal(notaFiscal, usuarioLogado);

            addMessage("notaCancelada.sucesso");
        }
        catch (Exception e1) {
            String message = e1.getMessage();
            addMessageError("erroInesperado.error", message);
        }
    }

    public Usuario getUsuarioNovo() {
        return usuarioNovo;
    }

    public void setUsuarioNovo(Usuario usuarioNovo) {
        if(usuarioNovo == null) {
            usuarioNovo = new Usuario();
        }
        this.usuarioNovo = usuarioNovo;
    }

    public List<Usuario> findClienteAutoComplete(String search) {
        return usuarioService.findClienteAutoComplete(search);
    }

    public boolean podeEnviarNotaFiscal() {
        NotaFiscal notaFiscal = orcamento.getNotaFiscal();
        StatusNotaFiscal statusNotaFiscal = notaFiscal.getStatusNotaFiscal();
        return !Arrays.asList(StatusNotaFiscal.CONCLUIDO, StatusNotaFiscal.CANCELADO, StatusNotaFiscal.PROCESSANDO).contains(statusNotaFiscal);
    }

    public boolean podeCancelarNotaFiscal() {
        NotaFiscal notaFiscal = orcamento.getNotaFiscal();
        StatusNotaFiscal statusNotaFiscal = notaFiscal.getStatusNotaFiscal();
        return StatusNotaFiscal.CONCLUIDO.equals(statusNotaFiscal);
    }
}
