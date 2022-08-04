package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.entity.Orcamento;
import net.gnfe.bin.domain.entity.OrcamentoProduto;
import net.gnfe.bin.domain.entity.Produto;
import net.gnfe.bin.domain.repository.OrcamentoRepository;
import net.gnfe.bin.domain.vo.filtro.OrcamentoFiltro;
import net.gnfe.util.ddd.HibernateRepository;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class OrcamentoService {

	@Autowired private OrcamentoRepository orcamentoRepository;

	public Orcamento get(Long id) {
		return orcamentoRepository.get(id);
	}
	
	@Transactional(rollbackFor=Exception.class)
	public void saveOrUpdate(Orcamento entity, List<Produto> produtos) throws MessageKeyException {
		try {
			if (produtos != null) {
				Set<OrcamentoProduto> rtps = entity.getProdutos();
				Set<Long> antigos = new HashSet<>();
				for (OrcamentoProduto rtp : rtps) {
					Produto produto = rtp.getProduto();
					Long produtoId = produto.getId();
					antigos.add(produtoId);
				}
				for (Produto tp : produtos) {
					OrcamentoProduto rtp = new OrcamentoProduto();
					Long tpId = tp.getId();
					if (antigos.contains(tpId)) {
						antigos.remove(tpId);
						continue;
					}
					rtp.setProduto(tp);
					rtp.setOrcamento(entity);
					rtps.add(rtp);
				}
				for (OrcamentoProduto rtp : new ArrayList<>(rtps)) {
					Produto tp = rtp.getProduto();
					Long rtpId = tp.getId();
					if (antigos.contains(rtpId)) {
						rtps.remove(rtp);
					}
				}
			}

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
