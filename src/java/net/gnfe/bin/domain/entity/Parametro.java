package net.gnfe.bin.domain.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints=@UniqueConstraint(columnNames={"CHAVE"}))
public class Parametro extends net.gnfe.util.ddd.Entity {

	private Long id;
	private String chave;
	private String valor;

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

	@Column(name="CHAVE", length=50, nullable=false)
	public String getChave() {
		return this.chave;
	}

	public void setChave(String chavenome) {
		this.chave = chavenome;
	}

	@Column(name="VALOR", length=200, nullable=false)
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}
}
