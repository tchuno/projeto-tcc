package net.gnfe.bin.domain.enumeration;

import java.util.Arrays;
import java.util.List;

public enum MotivoDesativacaoUsuario {

	DESLIGAMENTO,
	TRANSFERENCIA,
	INATIVIDADE_ACESSO,
	SOLICITACAO_GESTOR;

	private static List<MotivoDesativacaoUsuario> allMotivoDesativacaoUsuario = Arrays.asList(DESLIGAMENTO, TRANSFERENCIA, INATIVIDADE_ACESSO, SOLICITACAO_GESTOR);
	private static List<MotivoDesativacaoUsuario> notAllMotivoDesativacaoUsuario = Arrays.asList(DESLIGAMENTO, TRANSFERENCIA, INATIVIDADE_ACESSO, SOLICITACAO_GESTOR);

	public static List<MotivoDesativacaoUsuario> getAllMotivoDesativacaoUsuario() {
		return allMotivoDesativacaoUsuario;
	}

	public static List<MotivoDesativacaoUsuario> getNotAllMotivoDesativacaoUsuario() {
		return notAllMotivoDesativacaoUsuario;
	}
}
