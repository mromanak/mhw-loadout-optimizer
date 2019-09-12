package com.mromanak.loadoutoptimizer.controller;

import com.mromanak.loadoutoptimizer.model.dto.SkillDto;
import com.mromanak.loadoutoptimizer.model.exception.BadRepositoryApiRequestException;
import com.mromanak.loadoutoptimizer.model.exception.EntityNotFoundException;
import com.mromanak.loadoutoptimizer.model.jpa.Skill;
import com.mromanak.loadoutoptimizer.repository.MonsterHunterRepository;
import com.mromanak.loadoutoptimizer.service.DtoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/repository")
public class RepositoryController {

    private final MonsterHunterRepository repository;
    private final DtoService dtoService;

    public RepositoryController(MonsterHunterRepository repository, DtoService dtoService) {
        this.repository = repository;
        this.dtoService = dtoService;
    }

    @RequestMapping(path = "/skill", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<Page<SkillDto>> getSkills(
        @RequestParam(defaultValue = "0")int page,
        @RequestParam(defaultValue = "25") int pageSize
    ) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, new Sort(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(repository.findAllSkills(pageRequest).map(dtoService::toDto));
    }

    @RequestMapping(path = "/skill/{skillId}", method = RequestMethod.GET)
    @Transactional
    public ResponseEntity<SkillDto> getSkill(
        @PathVariable String skillId
    ) {
        return repository.findSkill(skillId).
            map(dtoService::toDto).
            map(ResponseEntity::ok).
            orElseThrow(() -> new EntityNotFoundException("No skill found with ID " + skillId));
    }

    @RequestMapping(path = "/skill/{skillId}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<SkillDto> deleteSkill(
        @PathVariable String skillId
    ) {
        repository.deleteSkill(skillId);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/skill", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<SkillDto> updateSkill(
        @RequestBody @Valid SkillDto skillDto,
        @RequestParam(defaultValue = "false") boolean preserveRelationships
    ) {
        repository.saveSkill(dtoService.fromDto(skillDto, preserveRelationships));
        return getSkill(skillDto.getId());
    }
}
