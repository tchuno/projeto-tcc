package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.repository.OrcamentoRepository;
import net.gnfe.bin.domain.vo.filtro.OrcamentoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrcamentoService {

	@Autowired private OrcamentoRepository orcamentoRepository;

	public Orcamento get(Long id) {
		return orcamentoRepository.get(id);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(Orcamento entity) throws MessageKeyException {
		try {
			orcamentoRepository.saveOrUpdate(entity);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long id) throws MessageKeyException {
		try {
			orcamentoRepository.deleteById(id);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<Orcamento> findByFiltro(OrcamentoFiltro filtro) {
		return orcamentoRepository.findByFiltro(filtro, null, null);
	}

	public List<Orcamento> findByFiltro(OrcamentoFiltro filtro, Integer inicio, Integer max) {
		return orcamentoRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(OrcamentoFiltro filtro) {
		return orcamentoRepository.countByFiltro(filtro);
	}
}
