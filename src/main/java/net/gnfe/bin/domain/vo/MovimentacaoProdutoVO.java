package net.gnfe.bin.domain.vo;

import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.MotivoMovimentacao;

import java.util.Date;

public class MovimentacaoProdutoVO {

    private Date data;
    private Orcamento orcamento;
    private OrcamentoProduto orcamentoProduto;
    private Produto produto;
    private boolean isEntrada;
    private MotivoMovimentacao motivoMovimentacao;
    private TotalNotaFiscalVO totalNotaFiscalVO;
    private Integer quantidade;
    private Usuario fornecedor;
    private Integer qtdEstoque;
    private Usuario autor;

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public OrcamentoProduto getOrcamentoProduto() {
        return orcamentoProduto;
    }

    public void setOrcamentoProduto(OrcamentoProduto orcamentoProduto) {
        this.orcamentoProduto = orcamentoProduto;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public boolean isEntrada() {
        return isEntrada;
    }

    public void setEntrada(boolean isEntrada) {
        if(!isEntrada) {
            this.fornecedor = null;
        }
        this.isEntrada = isEntrada;
    }

    public MotivoMovimentacao getMotivoMovimentacao() {
        return motivoMovimentacao;
    }

    public void setMotivoMovimentacao(MotivoMovimentacao motivoMovimentacao) {
        this.motivoMovimentacao = motivoMovimentacao;
    }

    public TotalNotaFiscalVO getTotalNotaFiscalVO() {
        return totalNotaFiscalVO;
    }

    public void setTotalNotaFiscalVO(TotalNotaFiscalVO totalNotaFiscalVO) {
        this.totalNotaFiscalVO = totalNotaFiscalVO;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Usuario getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Usuario fornecedor) {
        this.fornecedor = fornecedor;
    }

    public Integer getQtdEstoque() {
        return qtdEstoque;
    }

    public void setQtdEstoque(Integer qtdEstoque) {
        this.qtdEstoque = qtdEstoque;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }
}
