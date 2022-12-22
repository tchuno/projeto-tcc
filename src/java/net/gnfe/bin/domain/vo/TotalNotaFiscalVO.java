package net.gnfe.bin.domain.vo;

import java.math.BigDecimal;

public class TotalNotaFiscalVO {

    private BigDecimal vbcICMS = new BigDecimal(0);
    private BigDecimal vICMS = new BigDecimal(0);
    private BigDecimal vProd = new BigDecimal(0) ;
    private BigDecimal vPIS = new BigDecimal(0);
    private BigDecimal vCOFINS = new BigDecimal(0);
    private BigDecimal valorTotal = new BigDecimal(0);

    public BigDecimal getVbcICMS() {
        return vbcICMS;
    }

    public void setVbcICMS(BigDecimal vbcICMS) {
        this.vbcICMS = vbcICMS;
    }

    public BigDecimal getvICMS() {
        return vICMS;
    }

    public void setvICMS(BigDecimal vICMS) {
        this.vICMS = vICMS;
    }

    public BigDecimal getvProd() {
        return vProd;
    }

    public void setvProd(BigDecimal vProd) {
        this.vProd = vProd;
    }

    public BigDecimal getvPIS() {
        return vPIS;
    }

    public void setvPIS(BigDecimal vPIS) {
        this.vPIS = vPIS;
    }

    public BigDecimal getvCOFINS() {
        return vCOFINS;
    }

    public void setvCOFINS(BigDecimal vCOFINS) {
        this.vCOFINS = vCOFINS;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }
}
