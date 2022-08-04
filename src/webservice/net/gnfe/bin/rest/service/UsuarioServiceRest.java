package net.gnfe.bin.rest.service;

import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.MotivoDesativacaoUsuario;
import net.gnfe.bin.domain.enumeration.RoleGNFE;
import net.gnfe.bin.domain.enumeration.StatusUsuario;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.UsuarioFiltro;
import net.gnfe.bin.rest.exception.UsuarioRestException;
import net.gnfe.bin.rest.request.vo.RequestCadastrarUsuario;
import net.gnfe.bin.rest.request.vo.RequestDesativarUsuario;
import net.gnfe.bin.rest.request.vo.RequestFiltroUsuario;
import net.gnfe.bin.rest.request.vo.RequestLogin;
import net.gnfe.bin.rest.response.vo.CadastroUsuarioResponse;
import net.gnfe.bin.rest.response.vo.FiltroUsuarioResponse;
import net.gnfe.bin.rest.response.vo.ListaUsuarioResponse;
import net.gnfe.bin.rest.response.vo.MotivoDesativarUsuarioResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Novo service criado para centralizar as operaçõs que hoje são feitas no Bean JSF.
 */
@Service
public class UsuarioServiceRest extends SuperServiceRest {

    @Autowired private UsuarioService usuarioService;

    public CadastroUsuarioResponse cadastrar(Usuario usuario, RequestCadastrarUsuario requestCadastrarUsuario) {
        validaRequestParameters(requestCadastrarUsuario);

        Usuario usuarioNovo = new Usuario();
        usuarioNovo = saveOrUpdate(usuario, requestCadastrarUsuario, usuarioNovo);
        return new CadastroUsuarioResponse(usuarioNovo);
    }

    private Usuario saveOrUpdate(Usuario usuarioLogado, RequestCadastrarUsuario requestCadastrarUsuario, Usuario usuarioNovo) {
        usuarioNovo.setNome(requestCadastrarUsuario.getNome());
        usuarioNovo.setLogin(requestCadastrarUsuario.getLogin());
        usuarioNovo.setEmail(requestCadastrarUsuario.getEmail());
        usuarioNovo.setRoleGNFE(requestCadastrarUsuario.getRoleGD());
        usuarioNovo.setTelefone(requestCadastrarUsuario.getTelefone());

        usuarioService.saveOrUpdate(usuarioNovo, usuarioLogado);
        return usuarioNovo;
    }

    public ListaUsuarioResponse consultar(Usuario usuario, RequestFiltroUsuario requestFiltroUsuario) {
        RoleGNFE roleGD = requestFiltroUsuario.getRoleGD();
        String login = requestFiltroUsuario.getLogin();
        String nome = requestFiltroUsuario.getNome();


        UsuarioFiltro filtroAtivos = new UsuarioFiltro();
        filtroAtivos.setNome(nome);
        filtroAtivos.setLogin(login);
        filtroAtivos.setRoleGNFE(roleGD);
        filtroAtivos.setStatus(StatusUsuario.ATIVO);

        UsuarioFiltro filtroBloqueados = filtroAtivos.clone();
        filtroBloqueados.setStatus(StatusUsuario.BLOQUEADO);

        UsuarioFiltro filtroInativos = filtroAtivos.clone();
        filtroInativos.setStatus(StatusUsuario.INATIVO);

        ListaUsuarioResponse listaUsuarioResponse = new ListaUsuarioResponse();

        List<Usuario> ativos = usuarioService.findByFiltro(filtroAtivos);
        ativos.forEach(u -> {
            listaUsuarioResponse.addAtivo(new FiltroUsuarioResponse(u));
        });

        List<Usuario> bloqueados = usuarioService.findByFiltro(filtroBloqueados);
        bloqueados.forEach(u -> {
            listaUsuarioResponse.addBloqueado(new FiltroUsuarioResponse(u));
        });

        List<Usuario> inativos = usuarioService.findByFiltro(filtroInativos);
        inativos.forEach(u -> {
            listaUsuarioResponse.addInativo(new FiltroUsuarioResponse(u));
        });

        return listaUsuarioResponse;
    }

    public CadastroUsuarioResponse detalhar(Usuario usuario, Long id) {
        Usuario usuarioSalvo = usuarioService.get(id);
        if(usuarioSalvo == null){
            throw new UsuarioRestException("usuario.nao.localizado.com.id", id);
        }

        return new CadastroUsuarioResponse(usuarioSalvo);
    }

    public CadastroUsuarioResponse editar(Usuario usuario, Long id, RequestCadastrarUsuario requestCadastrarUsuario) {
        Usuario usuarioSalvo = usuarioService.get(id);
        if(usuarioSalvo == null){
            throw new UsuarioRestException("usuario.nao.localizado.com.id", id);
        }
        usuarioSalvo = saveOrUpdate(usuario, requestCadastrarUsuario, usuarioSalvo);
        return new CadastroUsuarioResponse(usuarioSalvo);
    }

    public boolean excluir(Usuario usuario, Long id) {
        Usuario usuarioSalvo = usuarioService.get(id);
        if(usuarioSalvo == null){
            throw new UsuarioRestException("usuario.nao.localizado.com.id", id);
        }
        usuarioService.excluir(id);
        return true;
    }

    public List<MotivoDesativarUsuarioResponse> getMotivosDesativacao(Usuario usuario) {
        List<MotivoDesativarUsuarioResponse> list = new ArrayList<>();
        for (MotivoDesativacaoUsuario value : MotivoDesativacaoUsuario.values()) {

            String descricao = messageService.getValue("MotivoDesativacaoUsuario."+value.name()+".label");
            if(StringUtils.isEmpty(descricao)){
                descricao = value.name();
            }

            list.add(new MotivoDesativarUsuarioResponse(value, descricao));
        }
        return list;
    }

    public CadastroUsuarioResponse desativar(Usuario usuario, Long id, RequestDesativarUsuario requestDesativarUsuario) throws Exception {
        Usuario usuarioSalvo = usuarioService.get(id);
        if(usuarioSalvo == null){
            throw new UsuarioRestException("usuario.nao.localizado.com.id", id);
        }
        usuarioSalvo.setMotivoDesativacao(requestDesativarUsuario.getMotivo());
        usuarioService.desativarUsuario(usuarioSalvo, usuario);


        String descricao = messageService.getValue("MotivoDesativacaoUsuario."+requestDesativarUsuario.getMotivo().name()+".label");
        if(StringUtils.isEmpty(descricao)){
            descricao = requestDesativarUsuario.getMotivo().name();
        }

        CadastroUsuarioResponse cadastroUsuarioResponse = new CadastroUsuarioResponse(usuario);
        cadastroUsuarioResponse.setMotivoDesativacao(new MotivoDesativarUsuarioResponse(requestDesativarUsuario.getMotivo(), descricao ));
        return cadastroUsuarioResponse;
    }

    public CadastroUsuarioResponse ativar(Usuario usuario, Long id) throws Exception {
        Usuario usuarioSalvo = usuarioService.get(id);
        if(usuarioSalvo == null){
            throw new UsuarioRestException("usuario.nao.localizado.com.id", id);
        }
        usuarioService.ativarUsuario(usuarioSalvo, usuario);
        CadastroUsuarioResponse cadastroUsuarioResponse = new CadastroUsuarioResponse(usuario);
        return cadastroUsuarioResponse;
    }

    public CadastroUsuarioResponse reiniciarSenha(Usuario usuario, Long id) throws Exception {
        Usuario usuarioSalvo = usuarioService.get(id);
        if(usuarioSalvo == null){
            throw new UsuarioRestException("usuario.nao.localizado.com.id", id);
        }
        usuarioService.reiniciarSenha(usuarioSalvo, usuario);
        CadastroUsuarioResponse cadastroUsuarioResponse = new CadastroUsuarioResponse(usuario);
        return cadastroUsuarioResponse;
    }

    public CadastroUsuarioResponse atualizarSenha(Usuario usuario, String novaSenha) throws Exception {
        String login = usuario.getLogin();
        if(StringUtils.isBlank(login)){
            throw new UsuarioRestException("usuario.login.invalido");
        }
        usuarioService.atualizarSenha(login, novaSenha);
        CadastroUsuarioResponse cadastroUsuarioResponse = new CadastroUsuarioResponse(usuario);
        return cadastroUsuarioResponse;
    }

    public String redefinirSenha(RequestLogin requestLogin) {
        String login = requestLogin.getLogin();
        String novaSenha = requestLogin.getSenha();

        Usuario usuario = null;
        try {
            usuario = usuarioService.atualizarSenha(login, novaSenha);
        } catch (Exception e) {
            e.printStackTrace();
            throw new UsuarioRestException("erro.redefinir.senha");
        }
        return usuario.getLogin();
    }
}