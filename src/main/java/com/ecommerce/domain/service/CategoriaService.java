package com.ecommerce.domain.service;

import com.ecommerce.domain.exception.DatabaseExcption;
import com.ecommerce.domain.exception.ResourceNotFoundException;
import com.ecommerce.domain.model.Categoria;
import com.ecommerce.domain.model.dto.CategoriaDTO;
import com.ecommerce.domain.repository.CategoriaRepository;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

  @Autowired
  CategoriaRepository categoriaRepository;

  public CategoriaDTO findById(Long id) {
    Categoria categoria = categoriaRepository
      .findById(id)
      .orElseThrow(
        () -> new ResourceNotFoundException("Categoria nao econtrada")
      );
    CategoriaDTO dto = new CategoriaDTO(categoria);
    return dto;
  }

  public List<CategoriaDTO> findAll() {
    List<Categoria> result = categoriaRepository.findAll();
    return result.stream().map(x -> new CategoriaDTO(x)).toList();
  }

  @Transactional
  public CategoriaDTO insert(CategoriaDTO categoriaDTO) {
    Categoria entity = new Categoria();
    copyDtoToEntity(categoriaDTO, entity);

    entity = categoriaRepository.save(entity);

    return new CategoriaDTO(entity);
  }

  public CategoriaDTO update(CategoriaDTO productDto, Long id) {
    try {
      Categoria entity = categoriaRepository
        .findById(id)
        .orElseThrow(
          () -> new ResourceNotFoundException("Categoria nao encontrada")
        );
      copyDtoToEntity(productDto, entity);
      entity = categoriaRepository.save(entity);
      return new CategoriaDTO(entity);
    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Recurso nao encontrado");
    }
  }

  // @Transactional(propagation = Propagation.SUPPORTS)
  public void deleteById(Long id) {
    try {
      categoriaRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Cliente nao econtrado");
    } catch (DataIntegrityViolationException e) {
      throw new DatabaseExcption("Falha de integridade Referencial");
    }
  }

  private void copyDtoToEntity(CategoriaDTO categoriaDTO, Categoria entity) {
    entity.setDescricao(categoriaDTO.getDescricao());
    entity.setNome(categoriaDTO.getNome());
  }
}
