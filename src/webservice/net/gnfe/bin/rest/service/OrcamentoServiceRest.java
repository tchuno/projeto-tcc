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
import net.gnfe.bin.rest.response.vo.FiltroNotaFiscalResponse;
import net.gnfe.bin.rest.response.vo.FiltroProdutoResponse;
import net.gnfe.bin.rest.response.vo.ListaOrcamentoResponse;
import net.gnfe.bin.rest.response.vo.OrcamentoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
        orcamentos.forEach(o -> {
            list.add(new OrcamentoResponse(o));
        });

        return new ListaOrcamentoResponse(list);
    }

    public OrcamentoResponse cadastrar(Usuario usuario, RequestCadastrarOrcamento requestCadastrarOrcamento) {
        validaRequestParameters(requestCadastrarOrcamento);

        Orcamento orcamentoNovo = new Orcamento();
        orcamentoNovo = saveOrUpdate(usuario, requestCadastrarOrcamento, orcamentoNovo);
        return new OrcamentoResponse(orcamentoNovo);
    }

    private Orcamento saveOrUpdate(Usuario usuario, RequestCadastrarOrcamento requestCadastrarOrcamento, Orcamento orcamentoNovo) {
        Usuario cliente = usuarioService.get(requestCadastrarOrcamento.getClienteId());

        orcamentoNovo.setAutor(usuario);
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
        notaFiscal.setDataCriacao(new Date());
        notaFiscal.setStatusNotaFiscal(StatusNotaFiscal.PENDENTE);
        orcamentoNovo.setNotaFiscal(notaFiscal);

        orcamentoService.saveOrUpdate(orcamentoNovo);
        return orcamentoNovo;
    }

    public FiltroNotaFiscalResponse emitirNotaFiscal(Usuario usuario, Long orcamentoId) throws Exception {

        Orcamento orcamento = orcamentoService.get(orcamentoId);
        NotaFiscal notaFiscal = orcamento.getNotaFiscal();
        notaFiscalService.enviarNotaFiscal(notaFiscal);

        return new FiltroNotaFiscalResponse(notaFiscal);
    }

    public FiltroNotaFiscalResponse cancelarNotaFiscal(Usuario usuario, Long orcamentoId) throws Exception {

        Orcamento orcamento = orcamentoService.get(orcamentoId);
        NotaFiscal notaFiscal = orcamento.getNotaFiscal();
        notaFiscalService.cancelarNotaFiscal(notaFiscal);

        return new FiltroNotaFiscalResponse(notaFiscal);
    }
}