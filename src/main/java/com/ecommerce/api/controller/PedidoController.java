package com.ecommerce.api.controller;

import com.ecommerce.api.assembler.PedidoInputDisassembler;
import com.ecommerce.api.assembler.PedidoModelAssembler;
import com.ecommerce.api.assembler.PedidoResumoModelAssembler;
import com.ecommerce.api.model.PedidoModel;
import com.ecommerce.api.model.input.PedidoInput;
import com.ecommerce.domain.exception.EntidadeNaoEncontradaException;
import com.ecommerce.domain.exception.NegocioException;
import com.ecommerce.domain.model.Pedido;
import com.ecommerce.domain.repository.PedidoRepository;
import com.ecommerce.domain.service.PedidoService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/pedidos")
public class PedidoController {

  @Autowired
  private PedidoRepository pedidoRepository;

  @Autowired
  private PedidoService pedidoService;

  @Autowired
  private PedidoModelAssembler pedidoModelAssembler;

  @Autowired
  private PedidoResumoModelAssembler pedidoResumoModelAssembler;

  @Autowired
  private PedidoInputDisassembler pedidoInputDisassembler;

  /*  @GetMapping
  public Page<PedidoResumoModel> pesquisar(
    PedidoFilter filtro,
    @PageableDefault(size = 10) Pageable pageable
  ) {
    pageable = traduzirPageable(pageable);

    Page<Pedido> pedidosPage = pedidoRepository.findAll(
      PedidoSpecs.usandoFiltro(filtro),
      pageable
    );

    List<PedidoResumoModel> pedidosResumoModel = pedidoResumoModelAssembler.toCollectionModel(
      pedidosPage.getContent()
    );

    Page<PedidoResumoModel> pedidosResumoModelPage = new PageImpl<>(
      pedidosResumoModel,
      pageable,
      pedidosPage.getTotalElements()
    );

    return pedidosResumoModelPage;
  } */

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public PedidoModel adicionar(@Valid @RequestBody PedidoInput pedidoInput) {
    try {
      Pedido novoPedido = pedidoInputDisassembler.toDomainObject(pedidoInput);
      novoPedido = pedidoService.emitir(novoPedido);

      return pedidoModelAssembler.toModel(novoPedido);
    } catch (EntidadeNaoEncontradaException e) {
      throw new NegocioException(e.getMessage(), e);
    }
  }

  @GetMapping("/{codigoPedido}")
  public PedidoModel buscar(@PathVariable Long pedidoId) {
    Pedido pedido = pedidoService.buscarOuFalhar(pedidoId);

    return pedidoModelAssembler.toModel(pedido);
  }
  /*   private Pageable traduzirPageable(Pageable apiPageable) {
    var mapeamento = Map.of(
    );

    return PageableTranslator.translate(apiPageable, mapeamento);
  } */
}
