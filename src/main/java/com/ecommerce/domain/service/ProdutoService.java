package com.ecommerce.domain.service;

import com.ecommerce.domain.exception.DatabaseExcption;
import com.ecommerce.domain.exception.ResourceNotFoundException;
import com.ecommerce.domain.model.Categoria;
import com.ecommerce.domain.model.Produto;
import com.ecommerce.domain.model.dto.CategoriaDTO;
import com.ecommerce.domain.model.dto.ProdutoDTO;
import com.ecommerce.domain.repository.CategoriaRepository;
import com.ecommerce.domain.repository.ProdutoRepository;
import com.ecommerce.domain.service.img.ImgBBDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProdutoService {

  @Autowired
  private ProdutoRepository produtoRepository;

  @Autowired
  private CategoriaRepository categoriaRepository;

  @Value("${imgbb.host.url}")
  private String imgBBHostUrl;

  @Value("${imgbb.host.key}")
  private String imgBBHostKey;

  public ProdutoDTO findById(Long id) {
    Produto produto = produtoRepository
      .findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    ProdutoDTO dto = new ProdutoDTO(produto);

    return dto;
  }

  public List<ProdutoDTO> listar() {
    List<Produto> produtos = produtoRepository.findAll();
    List<ProdutoDTO> produtoDTO = new ArrayList<>();

    for (Produto produto : produtos) {
      produtoDTO.add(new ProdutoDTO(produto));
    }

    return produtoDTO;
  }

  /*
   * public List<Produto> getAllProdutos() {
   * return produtoRepository.findAll();
   * }
   */
  public ProdutoDTO insert(String produtoTxt, MultipartFile file)
    throws IOException {
    RestTemplate restTemplate = new RestTemplate();
    String serverUrl = imgBBHostUrl + imgBBHostKey;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();

    ContentDisposition contentDisposition = ContentDisposition
      .builder("form-data")
      .name("image")
      .filename(file.getOriginalFilename())
      .build();

    fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());

    HttpEntity<byte[]> fileEntity = new HttpEntity<>(file.getBytes(), fileMap);

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    body.add("image", fileEntity);

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(
      body,
      headers
    );

    ResponseEntity<ImgBBDTO> response = null;
    ImgBBDTO imgDTO = new ImgBBDTO();
    Produto novoProduto = new Produto();
    // ProdutoDTO productDTO = new ProdutoDTO();
    try {
      response =
        restTemplate.exchange(
          serverUrl,
          HttpMethod.POST,
          requestEntity,
          ImgBBDTO.class
        );

      imgDTO = response.getBody();
      System.out.println("ImgBBDTO: " + imgDTO.getData().toString());
    } catch (HttpClientErrorException e) {
      e.printStackTrace();
    }
    Produto produtoFromJson = convertProdutoFromStringJson(produtoTxt);
    produtoFromJson.setImagemFileName(
      imgDTO.getData().getImage().getFilename()
    );
    produtoFromJson.setImagemNome(imgDTO.getData().getTitle());
    produtoFromJson.setImagemUrl(imgDTO.getData().getUrl());

    novoProduto = produtoRepository.save(produtoFromJson);
    Categoria categoria = categoriaRepository.getReferenceById(
      novoProduto.getCategoria().getId()
    );

    CategoriaDTO categoriaDTO = new CategoriaDTO(categoria);
    return paraDTO(novoProduto, categoriaDTO, categoria);
    
  }

  private Produto convertProdutoFromStringJson(String produtoJson) {
    Produto produto = new Produto();

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      produto = objectMapper.readValue(produtoJson, Produto.class);
    } catch (IOException err) {
      System.out.printf(err.toString());
    }

    return produto;
  }

  public ProdutoDTO paraDTO(Produto entity,CategoriaDTO categoriaDTO, Categoria categoria) {
    entity.setNome(entity.getNome());
    entity.setDataCadastro(entity.getDataCadastro());

    entity.setQtdEstoque(entity.getQtdEstoque());
    entity.setValorUnitario(entity.getValorUnitario());

    entity.setCategoria(entity.getCategoria());
    entity.setImagemUrl(entity.getImagemUrl());
    entity.setDescricao(entity.getDescricao());
    categoria.setId(categoriaDTO.getId());
    categoria.setDescricao(categoriaDTO.getDescricao());
    categoria.setNome(categoriaDTO.getNome());

    entity.setCategoria(categoria);
    

    entity = produtoRepository.save(entity);

    return new ProdutoDTO(entity);
  }

  public ProdutoDTO update(ProdutoDTO productDto, Long id) {
    try {
      Produto entity = produtoRepository.getReferenceById(id);
      copyDtoToEntity(productDto, entity);
      entity = produtoRepository.save(entity);
      return new ProdutoDTO(entity);
    } catch (EntityNotFoundException e) {
      throw new ResourceNotFoundException("Recurso nao encontrado");
    }
  }

  // @Transactional(propagation = Propagation.SUPPORTS)
  public void deleteById(Long id) {
    try {
      produtoRepository.deleteById(id);
    } catch (EmptyResultDataAccessException e) {
      throw new ResourceNotFoundException("Recurso nao encontrado");
    } catch (DataIntegrityViolationException e) {
      throw new DatabaseExcption("Falha de integridade Referencial");
    }
  }

  private void copyDtoToEntity(ProdutoDTO productDto, Produto entity) {
    entity.setNome(productDto.getNome());
    entity.setDataCadastro(productDto.getDataCadastro());
    entity.setQtdEstoque(productDto.getQtdEstoque());
    entity.setValorUnitario(productDto.getValorUnitario());
    entity.setCategoria(entity.getCategoria());
    entity.setImagemUrl(entity.getImagemUrl());
    entity.setDescricao(entity.getDescricao());
  }
}
