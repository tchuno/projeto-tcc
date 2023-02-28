package net.gnfe.bin.rest.service;

import net.gnfe.bin.domain.entity.NotaFiscal;
import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.enumeration.StatusNotaFiscal;
import net.gnfe.bin.domain.service.NotaFiscalService;
import net.gnfe.bin.domain.service.OrcamentoService;
import net.gnfe.bin.domain.service.ProdutoService;
import net.gnfe.bin.domain.service.UsuarioService;
import net.gnfe.bin.domain.vo.filtro.OrcamentoFiltro;
import net.gnfe.bin.rest.request.vo.RequestCadastrarOrcamento;
import net.gnfe.bin.rest.request.vo.RequestFiltroOrcamento;
import net.gnfe.bin.rest.response.vo.*;
import net.gnfe.util.DummyUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Novo service criado para centralizar as operaçõs que hoje são feitas no Bean JSF.
 */
@Service
public class OrcamentoServiceRest extends SuperServiceRest {

    @Autowired private OrcamentoService orcamentoService;
    @Autowired private UsuarioService usuarioService;
    @Autowired private ProdutoService produtoService;
    @Autowired private NotaFiscalService notaFiscalService;

    public ListaOrcamentoResponse consultar(Usuario usuario, RequestFiltroOrcamento requestFiltroOrcamento) {
        Long id = requestFiltroOrcamento.getId();
        String cpfCliente = requestFiltroOrcamento.getCpfCliente();

        OrcamentoFiltro filtro = new OrcamentoFiltro();
        filtro.setId(id);
        filtro.setCpfCnpj(cpfCliente);

        List<Orcamento> orcamentos = orcamentoService.findByFiltro(filtro);

        List<OrcamentoResponse> list = new ArrayList<>();
        orcamentos.forEach(o -> list.add(new OrcamentoResponse(o, usuario)));

        return new ListaOrcamentoResponse(list);
    }

    public OrcamentoResponse consultarById(Usuario usuario, Long orcamentoId) {

        OrcamentoFiltro filtro = new OrcamentoFiltro();
        filtro.setId(orcamentoId);

        List<Orcamento> orcamentos = orcamentoService.findByFiltro(filtro);

        if(orcamentos != null && !orcamentos.isEmpty()) {
            Orcamento orcamento = orcamentos.iterator().next();
            return new OrcamentoResponse(orcamento, usuario);
        }

        return null;
    }

    public OrcamentoResponse cadastrar(Usuario usuario, RequestCadastrarOrcamento requestCadastrarOrcamento) {
        validaRequestParameters(requestCadastrarOrcamento);

        Orcamento orcamentoNovo = new Orcamento();
        orcamentoNovo = saveOrUpdate(usuario, requestCadastrarOrcamento, orcamentoNovo);
        return new OrcamentoResponse(orcamentoNovo, usuario);
    }

    private Orcamento saveOrUpdate(Usuario usuario, RequestCadastrarOrcamento requestCadastrarOrcamento, Orcamento orcamentoNovo) {
            Usuario cliente = usuarioService.get(requestCadastrarOrcamento.getClienteId());
        orcamentoNovo.setDataCriacao(new Date());
        orcamentoNovo.setCliente(cliente);
        orcamentoNovo.setFormaPagamento(requestCadastrarOrcamento.getFormaPagamento());
        orcamentoNovo.setBandeira(requestCadastrarOrcamento.getBandeira());

        Set<OrcamentoProduto> orcamentoProdutos = orcamentoNovo.getOrcamentoProdutos();
        List<FiltroProdutoResponse> listaProdutoResponse = requestCadastrarOrcamento.getListaProdutoResponse();
        listaProdutoResponse.forEach(p -> {
            Produto produto = produtoService.get(p.getId());

            OrcamentoProduto op = new OrcamentoProduto();
            op.setProduto(produto);
            op.setOrcamento(orcamentoNovo);
            op.setQuantidade(p.getQuantidade());

            orcamentoProdutos.add(op);
        });
        orcamentoNovo.setOrcamentoProdutos(orcamentoProdutos);

        NotaFiscal notaFiscal = new NotaFiscal();
        notaFiscal.setOrcamento(orcamentoNovo);
        notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.PENDENTE);
        orcamentoNovo.setNotaFiscal(notaFiscal);

        orcamentoService.saveOrUpdate(orcamentoNovo);
        return orcamentoNovo;
    }

    public FiltroNotaFiscalResponse emitirNotaFiscal(Usuario usuario, Long orcamentoId) throws Exception {

        Orcamento orcamento = orcamentoService.get(orcamentoId);
        NotaFiscal notaFiscal = orcamento.getNotaFiscal();
        notaFiscalService.enviarNotaFiscal(notaFiscal, usuario);

        return new FiltroNotaFiscalResponse(notaFiscal);
    }

    public FiltroNotaFiscalResponse cancelarNotaFiscal(Usuario usuario, Long orcamentoId) throws Exception {

        Orcamento orcamento = orcamentoService.get(orcamentoId);
        NotaFiscal notaFiscal = orcamento.getNotaFiscal();
        notaFiscalService.cancelarNotaFiscal(notaFiscal, usuario);

        return new FiltroNotaFiscalResponse(notaFiscal);
    }

    public ArquivoPDFResponse gerarPDF(Usuario usuario, Long orcamentoId) throws IOException {

        Orcamento orcamento = orcamentoService.get(orcamentoId);
        Long usuarioId = usuario.getId();
        usuario = usuarioService.get(usuarioId);
        File file = orcamentoService.gerarOrcamento(orcamento, usuario);
        String fileName = file.getName();
        byte[] bytes = FileUtils.readFileToByteArray(file);

        Base64.Encoder encoder = Base64.getEncoder();
        String fileBase64 = encoder.encodeToString(bytes);

        ArquivoPDFResponse arquivoPDFResponse = new ArquivoPDFResponse();
        arquivoPDFResponse.setNomeArquivo(fileName);
        arquivoPDFResponse.setBase64(fileBase64);

        NotaFiscal notaFiscal = orcamento.getNotaFiscal();
        if(notaFiscal != null) {
            String xml = notaFiscal.getXml();
            String base64Xml = DummyUtils.convertStringToBase64(xml);
            arquivoPDFResponse.setBase64Xml(base64Xml);

            String xmlCancelamento = notaFiscal.getXmlCancelamento();
            String base64XmlCancelamento = DummyUtils.convertStringToBase64(xmlCancelamento);
            arquivoPDFResponse.setBase64XmlCancelamento(base64XmlCancelamento);
        }

        return arquivoPDFResponse;
    }
}