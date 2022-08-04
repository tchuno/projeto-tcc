package net.gnfe.bin.bean;

import net.gnfe.bin.bean.datamodel.OrcamentoDataModel;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.service.OrcamentoService;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.OrcamentoFiltro;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.util.faces.AbstractBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;

@ManagedBean
@ViewScoped
public class OrcamentoListBean extends AbstractBean {

    @Autowired private OrcamentoService service;
    @Autowired private UsuarioService usuarioService;

    private OrcamentoDataModel dataModel;
    private OrcamentoFiltro orcamentoFiltro;
    private List<Usuario> clientes;
    private Long orcamentoId;

    public void initBean() {
        dataModel = new OrcamentoDataModel();
        dataModel.setService(service);
        dataModel.setFiltro(new OrcamentoFiltro());

        UsuarioFiltro filtro = new UsuarioFiltro();
        filtro.setRoleGNFE(RoleGNFE.CLIENTE);
        clientes = usuarioService.findByFiltro(filtro);

    }

    public void excluir() {

        try {
            service.excluir(orcamentoId);

            addMessage("registroExcluido.sucesso");
        }
        catch (Exception e) {
            addMessageError(e);
        }
    }

    public OrcamentoDataModel getDataModel() {
        return dataModel;
    }

    public OrcamentoFiltro getOrcamentoFiltro() {
        return orcamentoFiltro;
    }

    public void setOrcamentoFiltro(OrcamentoFiltro OrcamentoFiltro) {
        this.orcamentoFiltro = OrcamentoFiltro;
    }

    public List<Usuario> getClientes() {
        return clientes;
    }

    public void setClientes(List<Usuario> clientes) {
        this.clientes = clientes;
    }

    public Long getOrcamentoId() {
        return orcamentoId;
    }

    public void setOrcamentoId(Long orcamentoId) {
        this.orcamentoId = orcamentoId;
    }
}
