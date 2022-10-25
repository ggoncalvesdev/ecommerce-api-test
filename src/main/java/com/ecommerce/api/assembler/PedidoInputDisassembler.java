package com.ecommerce.api.assembler;

import com.ecommerce.api.model.input.PedidoInput;
import com.ecommerce.domain.model.Pedido;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PedidoInputDisassembler {

  @Autowired
  private ModelMapper modelMapper;

  public Pedido toDomainObject(PedidoInput pedidoInput) {
    return modelMapper.map(pedidoInput, Pedido.class);
  }

  public void copyToDomainObject(PedidoInput pedidoInput, Pedido pedido) {
    modelMapper.map(pedidoInput, pedido);
  }
}
