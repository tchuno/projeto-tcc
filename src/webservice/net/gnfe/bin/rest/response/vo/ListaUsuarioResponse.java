package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.gnfe.bin.rest.request.vo.SuperVo;

import java.util.ArrayList;
import java.util.List;


@ApiModel(value = "ListaUsuarioResponse")
public class ListaUsuarioResponse extends SuperVo {

    @ApiModelProperty(notes = "Lista de usuários ativos.")
    private List<FiltroUsuarioResponse> usuariosAtivos;

    @ApiModelProperty(notes = "Lista de usuários bloqueados.")
    private List<FiltroUsuarioResponse> usuariosBloqueados;

    @ApiModelProperty(notes = "Lista de usuários inativos.")
    private List<FiltroUsuarioResponse> usuariosInativos;

    public List<FiltroUsuarioResponse> getUsuariosAtivos() {
        return usuariosAtivos;
    }

    public void setUsuariosAtivos(List<FiltroUsuarioResponse> usuariosAtivos) {
        this.usuariosAtivos = usuariosAtivos;
    }

    public List<FiltroUsuarioResponse> getUsuariosBloqueados() {
        return usuariosBloqueados;
    }

    public void setUsuariosBloqueados(List<FiltroUsuarioResponse> usuariosBloqueados) {
        this.usuariosBloqueados = usuariosBloqueados;
    }

    public List<FiltroUsuarioResponse> getUsuariosInativos() {
        return usuariosInativos;
    }

    public void setUsuariosInativos(List<FiltroUsuarioResponse> usuariosInativos) {
        this.usuariosInativos = usuariosInativos;
    }

    public void addAtivo(FiltroUsuarioResponse filtroUsuarioResponse) {
        if(this.usuariosAtivos == null){
            this.usuariosAtivos = new ArrayList<>();
        }
        this.usuariosAtivos.add(filtroUsuarioResponse);
    }

    public void addBloqueado(FiltroUsuarioResponse filtroUsuarioResponse) {
        if(this.usuariosBloqueados== null){
            this.usuariosBloqueados = new ArrayList<>();
        }
        this.usuariosBloqueados.add(filtroUsuarioResponse);
    }

    public void addInativo(FiltroUsuarioResponse filtroUsuarioResponse) {
        if(this.usuariosInativos== null){
            this.usuariosInativos = new ArrayList<>();
        }
        this.usuariosInativos.add(filtroUsuarioResponse);
    }
}