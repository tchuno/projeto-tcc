package net.gnfe.bin.rest.service;

import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.entity.Usuario;
import net.gnfe.bin.domain.service.ProdutoService;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.bin.rest.request.vo.RequestFiltroProduto;
import net.gnfe.bin.rest.response.vo.ListaProdutoResponse;
import net.gnfe.bin.rest.response.vo.ProdutoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Novo service criado para centralizar as operaçõs que hoje são feitas no Bean JSF.
 */
@Service
public class ProdutoServiceRest extends SuperServiceRest {

    @Autowired private ProdutoService produtoService;

    public ListaProdutoResponse consultar(Usuario usuario, RequestFiltroProduto requestFiltroProduto) {

        String cod = requestFiltroProduto.getCod();
        String nome = requestFiltroProduto.getNome();

        ProdutoFiltro filtro = new ProdutoFiltro();
        filtro.setCod(cod);
        filtro.setNome(nome);

        List<Produto> produtos = produtoService.findByFiltro(filtro, 0 , 10);

        List<ProdutoResponse> list = new ArrayList<>();
        produtos.forEach(p -> {
            list.add(new ProdutoResponse(p));
        });

        return new ListaProdutoResponse(list);
    }
}