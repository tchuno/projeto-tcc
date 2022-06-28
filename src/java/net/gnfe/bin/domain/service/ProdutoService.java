package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.repository.ProdutoRepository;
import net.gnfe.bin.domain.vo.filtro.ProdutoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

	@Autowired private ProdutoRepository produtoRepository;

	public Produto get(Long id) {
		return produtoRepository.get(id);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(Produto entity) throws MessageKeyException {
		try {
			produtoRepository.saveOrUpdate(entity);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long id) throws MessageKeyException {
		try {
			produtoRepository.deleteById(id);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<Produto> findByFiltro(ProdutoFiltro filtro) {
		return produtoRepository.findByFiltro(filtro, null, null);
	}

	public List<Produto> findByFiltro(ProdutoFiltro filtro, Integer inicio, Integer max) {
		return produtoRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(ProdutoFiltro filtro) {
		return produtoRepository.countByFiltro(filtro);
	}
}
