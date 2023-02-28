package net.gnfe.bin.domain.entity;

import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "NOTA_FISCAL")
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"ORCAMENTO_ID"}))
public class NotaFiscal extends net.gnfe.util.ddd.Entity {


	private Long id;
	private Orcamento orcamento;
	private StatusNotaFiscal statusNotaFiscal;
	private Date dataEnvio;
	private String protocolo;
	private String chaveAcesso;
	private String xml;
	private Date dataCancelamento;
	private String protocoloCancelamento;
	private String xmlCancelamento;

	@Id
	@Override
	@Column(name="ID", unique=true, nullable=false)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ORCAMENTO_ID")
	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_ENVIO")
	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="STATUS_NOTA_FISCAL")
	public StatusNotaFiscal getStatusNotaFiscal() {
		return statusNotaFiscal;
	}

	public void setStatusNotaFiscal(StatusNotaFiscal statusNotaFiscal) {
		this.statusNotaFiscal = statusNotaFiscal;
	}

	@Column(name="CHAVE_ACESSO")
	public String getChaveAcesso() {
		return chaveAcesso;
	}

	public void setChaveAcesso(String chaveAcesso) {
		this.chaveAcesso = chaveAcesso;
	}

	@Column(name="PROTOCOLO")
	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	@Column(name="XML")
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CANCELAMENTO")
	public Date getDataCancelamento() {
		return dataCancelamento;
	}

	public void setDataCancelamento(Date dataCancelamento) {
		this.dataCancelamento = dataCancelamento;
	}

	@Column(name="PROTOCOLO_CANCELAMENTO")
	public String getProtocoloCancelamento() {
		return protocoloCancelamento;
	}

	public void setProtocoloCancelamento(String protocoloCancelamento) {
		this.protocoloCancelamento = protocoloCancelamento;
	}

	@Column(name="XML_CANCELAMENTO")
	public String getXmlCancelamento() {
		return xmlCancelamento;
	}

	public void setXmlCancelamento(String xmlCancelamento) {
		this.xmlCancelamento = xmlCancelamento;
	}
}