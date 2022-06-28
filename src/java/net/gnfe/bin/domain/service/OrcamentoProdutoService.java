package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.repository.OrcamentoProdutoRepository;
import net.gnfe.bin.domain.vo.filtro.OrcamentoProdutoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrcamentoProdutoService {

	@Autowired private OrcamentoProdutoRepository orcamentoProdutoRepository;

	public OrcamentoProduto get(Long id) {
		return orcamentoProdutoRepository.get(id);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(OrcamentoProduto entity) throws MessageKeyException {
		try {
			orcamentoProdutoRepository.saveOrUpdate(entity);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long id) throws MessageKeyException {
		try {
			orcamentoProdutoRepository.deleteById(id);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<OrcamentoProduto> findByFiltro(OrcamentoProdutoFiltro filtro) {
		return orcamentoProdutoRepository.findByFiltro(filtro, null, null);
	}

	public List<OrcamentoProduto> findByFiltro(OrcamentoProdutoFiltro filtro, Integer inicio, Integer max) {
		return orcamentoProdutoRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(OrcamentoProdutoFiltro filtro) {
		return orcamentoProdutoRepository.countByFiltro(filtro);
	}
}
