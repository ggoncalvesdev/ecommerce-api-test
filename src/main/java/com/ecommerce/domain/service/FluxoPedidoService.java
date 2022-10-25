package com.ecommerce.domain.service;

import com.ecommerce.domain.model.Pedido;
import com.ecommerce.domain.repository.PedidoRepository;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FluxoPedidoService {

  @Autowired
  private PedidoService pedidoService;

  @Autowired
  private PedidoRepository pedidoRepository;

  @Transactional
  public void confirmar(Long pedidoId) {
    Pedido pedido = pedidoService.buscarOuFalhar(pedidoId);
    pedido.confirmar();

    pedidoRepository.save(pedido);
  }

  @Transactional
  public void cancelar(Long pedidoId) {
    Pedido pedido = pedidoService.buscarOuFalhar(pedidoId);
    pedido.cancelar();

    pedidoRepository.save(pedido);
  }

  @Transactional
  public void entregar(Long pedidoId) {
    Pedido pedido = pedidoService.buscarOuFalhar(pedidoId);
    pedido.entregar();
  }
}
