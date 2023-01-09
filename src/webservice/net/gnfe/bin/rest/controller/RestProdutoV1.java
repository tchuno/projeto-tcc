package net.gnfe.bin.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.rest.exception.HTTP401Exception;
import net.gnfe.bin.rest.request.vo.RequestFiltroProduto;
import net.gnfe.bin.rest.response.vo.ListaProdutoResponse;
import net.gnfe.bin.rest.service.ProdutoServiceRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@CrossOrigin
@Controller
@RequestMapping("/produto/v1")
@Api(tags = "/produto", description = "Serviços relacionados ao Produto.")
public class RestProdutoV1 extends SuperController {

    @Autowired
    private ProdutoServiceRest produtoServiceRest;

    @RequestMapping(
            path = "/buscar",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Buscar produtos á partir de um filtro.",
            response = ListaProdutoResponse.class
    )
    public ResponseEntity buscar(HttpServletRequest request, @RequestBody RequestFiltroProduto requestFiltroProduto) throws HTTP401Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        ListaProdutoResponse listaProdutoResponse  = produtoServiceRest.consultar(sessaoHttpRequest.getUsuario(), requestFiltroProduto);
        return new ResponseEntity(listaProdutoResponse, HttpStatus.OK);
    }
}