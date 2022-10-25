package com.ecommerce.domain.service;

import com.ecommerce.domain.exception.PedidoNaoEncontradoException;
import com.ecommerce.domain.model.Cidade;
import com.ecommerce.domain.model.Pedido;
import com.ecommerce.domain.model.Produto;
import com.ecommerce.domain.repository.PedidoRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private CidadeService cidadeService;

  @Autowired
  private ProdutoService produtoService;

  /*   @Autowired
  private PedidoMapper pedidoMapper; */

  @Transactional
  public Pedido emitir(Pedido pedido) {
    validarPedido(pedido);
    validarItens(pedido);

    pedido.calcularValorTotal();

    return pedidoRepository.save(pedido);
  }

  private void validarPedido(Pedido pedido) {
    Cidade cidade = cidadeService.buscarOuFalhar(
      pedido.getEnderecoEntrega().getCidade().getId()
    );

    pedido.getEnderecoEntrega().setCidade(cidade);
  }

  private void validarItens(Pedido pedido) {
    pedido
      .getItens()
      .forEach(
        item -> {
          Produto produto = produtoService.buscarOuFalhar(
            item.getProduto().getId()
          );

          item.setPedido(pedido);
          item.setProduto(produto);
        }
      );
  }

  /*   @Transactional
  public PedidoResponseDTO salvar(PedidoRequestDTO request) {
    Pedido pedido = pedidoMapper.requestToModel(request);
    Pedido pedidoSalvaNoBanco = pedidoRepository.save(pedido);
    return pedidoMapper.modelToResponse(pedidoSalvaNoBanco);
  } */

  public Pedido buscarOuFalhar(Long pedidoId) {
    return pedidoRepository
      .findById(pedidoId)
      .orElseThrow(() -> new PedidoNaoEncontradoException(pedidoId));
  }
  /*   public PedidoResponseDTO listarPorId(Long id) {
    return pedidoMapper.modelToResponse(buscarOuFalhar(id));
  } */

  /*   public List<PedidoResponseDTO> listarTodos() {
    return pedidoRepository
      .findAll()
      .stream()
      .map(pedidoMapper::modelToResponse)
      .collect(Collectors.toList());
  } */
  /* 
  public PedidoResponseDTO substituir(Long id, PedidoRequestDTO pedidoDto) {
    Pedido pedidoNoBanco = buscarOuFalhar(id);
    Pedido pedido = pedidoMapper.requestToModel(pedidoDto);
    BeanUtils.copyProperties(pedido, pedidoNoBanco, "id");
    return pedidoMapper.modelToResponse(pedidoRepository.save(pedidoNoBanco));
  } */

  /*   @Transactional
  public void excluir(Long pedidoId) {
    buscarOuFalhar(pedidoId);
    pedidoRepository.deleteById(pedidoId);
  } */
}
