package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.NotaFiscal;
import net.gnfe.bin.domain.repository.NotaFiscalRepository;
import net.gnfe.bin.domain.vo.filtro.NotaFiscalFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotaFiscalService {

	@Autowired private NotaFiscalRepository notaFiscalRepository;

	public NotaFiscal get(Long id) {
		return notaFiscalRepository.get(id);
	}

	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(NotaFiscal entity) throws MessageKeyException {
		try {
			notaFiscalRepository.saveOrUpdate(entity);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
			throw e;
		}
	}

	@Transactional(rollbackFor=Exception.class)
	public void excluir(Long id) throws MessageKeyException {
		try {
			notaFiscalRepository.deleteById(id);
		}
		catch (RuntimeException e) {
			HibernateRepository.verifyConstrantViolation(e);
		}
	}

	public List<NotaFiscal> findByFiltro(NotaFiscalFiltro filtro) {
		return notaFiscalRepository.findByFiltro(filtro, null, null);
	}

	public List<NotaFiscal> findByFiltro(NotaFiscalFiltro filtro, Integer inicio, Integer max) {
		return notaFiscalRepository.findByFiltro(filtro, inicio, max);
	}

	public int countByFiltro(NotaFiscalFiltro filtro) {
		return notaFiscalRepository.countByFiltro(filtro);
	}
}
