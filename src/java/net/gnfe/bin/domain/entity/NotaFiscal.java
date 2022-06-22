package net.gnfe.bin.domain.entity;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "NOTA_FISCAL")
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"ID"}))
public class NotaFiscal extends net.gnfe.util.ddd.Entity {


	private Long id;
	private Orcamento orcamento;
	private Date dataCriacao;
	private String chaveAcesso;

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

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ORCAMENTO_ID")
	public Orcamento getOrcamento() {
		return orcamento;
	}

	public void setOrcamento(Orcamento orcamento) {
		this.orcamento = orcamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DATA_CRIACAO")
	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	@Column(name="CHAVE_ACESSO")
	public String getChaveAcesso() {
		return chaveAcesso;
	}

	public void setChaveAcesso(String chaveAcesso) {
		this.chaveAcesso = chaveAcesso;
	}
}