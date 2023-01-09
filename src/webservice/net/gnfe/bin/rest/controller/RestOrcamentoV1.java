package net.gnfe.bin.rest.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.gnfe.bin.domain.entity.SessaoHttpRequest;
import net.gnfe.bin.rest.request.vo.RequestCadastrarOrcamento;
import net.gnfe.bin.rest.request.vo.RequestFiltroOrcamento;
import net.gnfe.bin.rest.response.vo.FiltroNotaFiscalResponse;
import net.gnfe.bin.rest.response.vo.ListaOrcamentoResponse;
import net.gnfe.bin.rest.response.vo.OrcamentoResponse;
import net.gnfe.bin.rest.service.OrcamentoServiceRest;
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

@CrossOrigin
@Controller
@RequestMapping("/orcamento/v1")
@Api(tags = "/orcamento", description = "Serviços relacionados ao Orçamento.")
public class RestOrcamentoV1 extends SuperController {

    @Autowired
    private OrcamentoServiceRest orcamentoServiceRest;

    @RequestMapping(
            path = "/buscar",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Buscar orçamento á partir de um filtro.",
            response = ListaOrcamentoResponse.class
    )
    public ResponseEntity buscar(HttpServletRequest request, @RequestBody RequestFiltroOrcamento requestFiltroOrcamento) {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        ListaOrcamentoResponse listaOrcamentoResponse = orcamentoServiceRest.consultar(sessaoHttpRequest.getUsuario(), requestFiltroOrcamento);
        return new ResponseEntity(listaOrcamentoResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/cadastrar",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Cadastrar orçamento.",
            response = OrcamentoResponse.class
    )
    public ResponseEntity buscar(HttpServletRequest request, @RequestBody RequestCadastrarOrcamento requestCadastrarOrcamento) {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        OrcamentoResponse orcamentoResponse = orcamentoServiceRest.cadastrar(sessaoHttpRequest.getUsuario(), requestCadastrarOrcamento);
        return new ResponseEntity(orcamentoResponse, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/emitir-nota-fiscal/{notaFiscalId}",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Solicitação de emissão de nota fiscal.",
            response = FiltroNotaFiscalResponse.class
    )
    public ResponseEntity emitirNotaFiscal(HttpServletRequest request, @PathVariable Long notaFiscalId) throws Exception {
        SessaoHttpRequest sessaoHttpRequest = getSessaoHttpRequest(request);
        FiltroNotaFiscalResponse filtroNotaFiscalResponse = orcamentoServiceRest.emitirNotaFiscal(sessaoHttpRequest.getUsuario(), notaFiscalId);
        return new ResponseEntity(filtroNotaFiscalResponse, HttpStatus.OK);
    }
}