package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.Bandeira;
import net.gnfe.bin.domain.enumeration.FormaPagamento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


@ApiModel(value = "OrcamentoResponse")
public class OrcamentoResponse {

    @ApiModelProperty(notes = "ID.")
    private Long id;

    @ApiModelProperty(notes = "Data de Criação.")
    private Date dataCriacao;

    @ApiModelProperty(notes = "Autor.")
    private FiltroUsuarioResponse autor;

    @ApiModelProperty(notes = "Cliente.")
    private FiltroUsuarioResponse cliente;

    @ApiModelProperty(notes = "Forma de pagamento.")
    private FormaPagamento formadePagamento;

    @ApiModelProperty(notes = "Bandeira do Cartão.")
    private Bandeira bandeira;

    @ApiModelProperty(notes = "Produtos.")
    private List<OrcamentoProdutoResponse> listaProdutoResponse;

    @ApiModelProperty(notes = "Nota fiscal.")
    private FiltroNotaFiscalResponse notaFiscal;

    public OrcamentoResponse(Orcamento orcamento, Usuario usuario) {
        this.id = orcamento.getId();
        this.dataCriacao = new Date();
        this.autor = new FiltroUsuarioResponse(usuario);
        this.cliente = new FiltroUsuarioResponse(orcamento.getCliente());
        this.formadePagamento = orcamento.getFormaPagamento();
        this.bandeira = orcamento.getBandeira();

        Set<OrcamentoProduto> orcamentoProdutos = orcamento.getOrcamentoProdutos();
        List<OrcamentoProdutoResponse> list = new ArrayList<>();
        orcamentoProdutos.forEach(op -> {
            Produto produto = op.getProduto();
            OrcamentoProdutoResponse produtoResponse = new OrcamentoProdutoResponse(produto, op.getQuantidade());
            list.add(produtoResponse);
        });
        this.listaProdutoResponse = list;
        this.notaFiscal = new FiltroNotaFiscalResponse(orcamento.getNotaFiscal());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public FiltroUsuarioResponse getAutor() {
        return autor;
    }

    public void setAutor(FiltroUsuarioResponse autor) {
        this.autor = autor;
    }

    public FiltroUsuarioResponse getCliente() {
        return cliente;
    }

    public void setCliente(FiltroUsuarioResponse cliente) {
        this.cliente = cliente;
    }

    public FormaPagamento getFormadePagamento() {
        return formadePagamento;
    }

    public void setFormadePagamento(FormaPagamento formadePagamento) {
        this.formadePagamento = formadePagamento;
    }

    public FiltroNotaFiscalResponse getNotaFiscal() {
        return notaFiscal;
    }

    public void setNotaFiscal(FiltroNotaFiscalResponse notaFiscal) {
        this.notaFiscal = notaFiscal;
    }

    public Bandeira getBandeira() {
        return bandeira;
    }

    public void setBandeira(Bandeira bandeira) {
        this.bandeira = bandeira;
    }

    public List<OrcamentoProdutoResponse> getListaProdutoResponse() {
        return listaProdutoResponse;
    }

    public void setListaProdutoResponse(List<OrcamentoProdutoResponse> listaProdutoResponse) {
        this.listaProdutoResponse = listaProdutoResponse;
    }
}