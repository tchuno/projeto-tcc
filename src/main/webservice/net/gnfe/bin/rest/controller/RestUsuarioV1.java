package net.gnfe.bin.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.rest.exception.HTTP401Exception;
import net.gnfe.bin.rest.request.vo.RequestCadastrarUsuario;
import net.gnfe.bin.rest.request.vo.RequestDesativarUsuario;
import net.gnfe.bin.rest.request.vo.RequestFiltroUsuario;
import net.gnfe.bin.rest.request.vo.RequestLogin;
import net.gnfe.bin.rest.response.vo.CadastroUsuarioResponse;
import net.gnfe.bin.rest.response.vo.ListaUsuarioResponse;
import net.gnfe.bin.rest.response.vo.LoginResponse;
import net.gnfe.bin.rest.response.vo.MotivoDesativarUsuarioResponse;
import net.gnfe.bin.rest.response.vo.UsuarioResponse;
import net.gnfe.bin.rest.service.SessaoHttpRequestService;
import net.gnfe.bin.rest.service.UsuarioServiceRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@CrossOrigin
@Controller
@RequestMapping("/usuario/v1")
@Api(tags = "/usuario", description = "Serviços relacionados ao Usuário.")
public class RestUsuarioV1 extends SuperController {

    @Autowired
    private UsuarioServiceRest usuarioServiceRest;
    @Autowired
    private SessaoHttpRequestService sessaoHttpRequestService;

    /**
     * @param request
     * @param response
     * @param requestLogin
     * @return
     * @throws Exception
     */
    @RequestMapping(
            path = "/login",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Autentica o usuário.",
            response = LoginResponse.class,
            notes = "Realiza autenticação do usuário através do realm."
    )
    public ResponseEntity login(HttpServletRequest request, @RequestBody RequestLogin requestLogin) throws Exception {

        SessaoHttpRequest sessaoHttpRequest = sessaoHttpRequestService.login(request, requestLogin);
        LoginResponse loginResponse = new LoginResponse(sessaoHttpRequest);
        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/logoff",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})

    @ApiOperation(
            value = "Logoff do usuário.",
            notes = "Logoff usuario",
            response = UsuarioResponse.class
    )
    public ResponseEntity logoff(HttpServletRequest request) throws Exception {
		SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        mataSessao(sessaoHttpRequest);
        request.getSession().invalidate();
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(
            path = "/cadastrar",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Cadastrar novo usuário.",
            notes = "Cadastra um novo usuário."
    )
    public ResponseEntity cadastrar(HttpServletRequest request, @RequestBody RequestCadastrarUsuario requestCadastrarUsuario) throws HTTP401Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        CadastroUsuarioResponse cadastroUsuarioResponse = usuarioServiceRest.cadastrar(sessaoHttpRequest.getUsuario(), requestCadastrarUsuario);
        return new ResponseEntity(cadastroUsuarioResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/consultar",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Consultar usuários á partir de um filtro.",
            response = ListaUsuarioResponse.class
    )
    public ResponseEntity consultar(HttpServletRequest request, @RequestBody RequestFiltroUsuario requestFiltroUsuario) throws HTTP401Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        ListaUsuarioResponse listaUsuarioResponse = usuarioServiceRest.consultar(sessaoHttpRequest.getUsuario(), requestFiltroUsuario);
        return new ResponseEntity(listaUsuarioResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/detalhar/{id}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Obtém os dados cadastrais de um usuário.",
            response = CadastroUsuarioResponse.class
    )
    public ResponseEntity detalhar(HttpServletRequest request, @PathVariable Long id) throws HTTP401Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        CadastroUsuarioResponse cadastroUsuarioResponse = usuarioServiceRest.detalhar(sessaoHttpRequest.getUsuario(), id);
        return new ResponseEntity(cadastroUsuarioResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/editar/{id}",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Altera os dados cadastrais do usuário.",
            response = CadastroUsuarioResponse.class
    )
    public ResponseEntity editar(HttpServletRequest request, @PathVariable Long id, @RequestBody RequestCadastrarUsuario requestCadastrarUsuario) throws HTTP401Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        CadastroUsuarioResponse cadastroUsuarioResponse= usuarioServiceRest.editar(sessaoHttpRequest.getUsuario(), id, requestCadastrarUsuario);
        return new ResponseEntity(cadastroUsuarioResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/excluir/{id}",
            method = RequestMethod.DELETE,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Excluir um usuário.",
            response = CadastroUsuarioResponse.class
    )
    public ResponseEntity excluir(HttpServletRequest request, @PathVariable Long id) throws HTTP401Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        boolean ok = usuarioServiceRest.excluir(sessaoHttpRequest.getUsuario(), id);
        return new ResponseEntity(null, ok ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(
            path = "/motivos-desativacao",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Consultar os motivos para desativação.",
            response = MotivoDesativarUsuarioResponse.class
    )
    public ResponseEntity getMotivosDesativacao(HttpServletRequest request) throws HTTP401Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        List<MotivoDesativarUsuarioResponse> list = usuarioServiceRest.getMotivosDesativacao(sessaoHttpRequest.getUsuario());
        return new ResponseEntity(list, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/desativar/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Desativa um usuário.",
            response = CadastroUsuarioResponse.class
    )
    public ResponseEntity desativar(HttpServletRequest request, @PathVariable Long id, @RequestBody RequestDesativarUsuario requestDesativarUsuario) throws Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        CadastroUsuarioResponse cadastroUsuarioResponse = usuarioServiceRest.desativar(sessaoHttpRequest.getUsuario(), id, requestDesativarUsuario);
        return new ResponseEntity(cadastroUsuarioResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/ativar/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Ativa um usuário.",
            response = CadastroUsuarioResponse.class
    )
    public ResponseEntity ativar(HttpServletRequest request, @PathVariable Long id) throws Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        CadastroUsuarioResponse cadastroUsuarioResponse = usuarioServiceRest.ativar(sessaoHttpRequest.getUsuario(), id);
        return new ResponseEntity(cadastroUsuarioResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/reiniciar-senha/{id}",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Reiniciar a senha de um usuário.",
            response = CadastroUsuarioResponse.class
    )
    public ResponseEntity reinicarSenha(HttpServletRequest request, @PathVariable Long id) throws Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        CadastroUsuarioResponse cadastroUsuarioResponse = usuarioServiceRest.reiniciarSenha(sessaoHttpRequest.getUsuario(), id);
        return new ResponseEntity(cadastroUsuarioResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/atualizar-senha",
            method = RequestMethod.PUT,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Reiniciar a senha de um usuário.",
            response = CadastroUsuarioResponse.class
    )
    public ResponseEntity atualizarSenha(HttpServletRequest request ,@RequestBody String novaSenha) throws Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        CadastroUsuarioResponse cadastroUsuarioResponse = usuarioServiceRest.atualizarSenha(sessaoHttpRequest.getUsuario(), novaSenha);
        return new ResponseEntity(cadastroUsuarioResponse, HttpStatus.OK);
    }

    /**
     * Verifica se o usuario esta logado
     *
     * @param request
     * @return
     */
    @RequestMapping(
            path = "/is-logado",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Consulta se o usuario ainda esta logado.",
            notes = "Consulta se o usuario ainda esta logado.",
            response = UsuarioResponse.class
    )
    public ResponseEntity isLogado(HttpServletRequest request) throws HTTP401Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        LoginResponse loginResponse = new LoginResponse(sessaoHttpRequest);
        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/redefinir-senha",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Redefine a seha.",
            response = String.class
    )
    public ResponseEntity redefinirSenha(HttpServletRequest request, @RequestBody RequestLogin requestLogin) throws HTTP401Exception {
        String login = usuarioServiceRest.redefinirSenha(requestLogin);
        return new ResponseEntity(login, HttpStatus.OK);
    }
}