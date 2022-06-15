package net.gnfe.bin.restws;

import net.gnfe.bin.domain.entity.Role;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.restws.dto.UsuarioDTO;
import net.gnfe.bin.restws.model.CredencialModel;
import net.gnfe.util.ddd.MessageKeyException;
import net.gnfe.util.rest.AbstractController;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/usuario")
public class UsuarioController extends AbstractController {

    @Autowired private UsuarioService usuarioService;

    @RequestMapping(value="/autenticar", method= RequestMethod.POST)
    public ResponseEntity<?> autenticar(@RequestBody CredencialModel credentialModel) {
        try {
            Usuario usuario = usuarioService.autenticar(credentialModel.login, credentialModel.senha);
            if (usuario == null) {
                throw new MessageKeyException("usuarioInvalido.error");
            }
            Set<Role> roles = usuario.getRoles();
            if (CollectionUtils.isEmpty(roles)) {
                throw new MessageKeyException("erroInesperado.error");
            }
            Set<String> nomes = new HashSet<>();
            for (Role role : roles) {
                nomes.add(role.getNome());
            }

            UsuarioDTO usuarioDTO = UsuarioDTO.from(usuario);
            return new ResponseEntity<UsuarioDTO>(usuarioDTO, HttpStatus.OK);

        } catch (Exception e) {
            return handleException(e);
        }
    }

}
