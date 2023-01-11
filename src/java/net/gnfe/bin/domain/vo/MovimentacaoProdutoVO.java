package net.gnfe.bin.domain.vo;

import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.enumeration.MotivoMovimentacao;

import java.math.BigDecimal;
import java.util.Date;

public class MovimentacaoProdutoVO {

    private Date data;
    private Orcamento orcamento;
    private Produto produto;
    private boolean isEntrada;
    private MotivoMovimentacao motivoMovimentacao;
    private BigDecimal valorTotal;
    private Integer quantidade;

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
        this.isEntrada = isEntrada;
    }

    public MotivoMovimentacao getMotivoMovimentacao() {
        return motivoMovimentacao;
    }

    public void setMotivoMovimentacao(MotivoMovimentacao motivoMovimentacao) {
        this.motivoMovimentacao = motivoMovimentacao;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
