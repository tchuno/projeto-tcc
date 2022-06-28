package net.gnfe.bin.domain.service;

import net.gnfe.bin.domain.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleService {

	@Autowired private RoleRepository roleRepository;

	@Transactional(rollbackFor=Exception.class)
	public void delete(Long roleId) {
		roleRepository.delete(roleId);
	}
}
