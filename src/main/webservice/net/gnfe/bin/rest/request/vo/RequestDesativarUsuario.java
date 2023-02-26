package net.gnfe.bin.rest.request.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.domain.enumeration.MotivoDesativacaoUsuario;
import net.gnfe.bin.rest.annotations.NotNull;


@ApiModel(value = "RequestDesativarUsuario")
public class RequestDesativarUsuario {

    @ApiModelProperty(notes = "Motivo da desativação do usuário.")
    private MotivoDesativacaoUsuario motivo;

    @NotNull
    public MotivoDesativacaoUsuario getMotivo() {
        return motivo;
    }

    public void setMotivo(MotivoDesativacaoUsuario motivo) {
        this.motivo = motivo;
    }
}
