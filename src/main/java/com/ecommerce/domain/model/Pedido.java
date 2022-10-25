package com.ecommerce.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Pedido /* extends AbstractAggregateRoot<Pedido> */{

  @EqualsAndHashCode.Include
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String codigo;

  private BigDecimal subtotal;
  private BigDecimal taxaFrete;
  private BigDecimal valorTotal;

  @Embedded
  private Endereco enderecoEntrega;

  @Enumerated(EnumType.STRING)
  private StatusPedido status = StatusPedido.CRIADO;

  @CreationTimestamp
  private OffsetDateTime dataCriacao;

  private OffsetDateTime dataConfirmacao;
  private OffsetDateTime dataCancelamento;
  private OffsetDateTime dataEntrega;

  @ManyToOne
  @JoinColumn(name = "usuario_cliente_id", nullable = false)
  private Cliente cliente;

  @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
  private List<ItemPedido> itens = new ArrayList<>();

  public void calcularValorTotal() {
    getItens().forEach(ItemPedido::calcularPrecoTotal);

    this.subtotal =
      getItens()
        .stream()
        .map(item -> item.getPrecoTotal())
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    this.valorTotal = this.subtotal.add(this.taxaFrete);
  }
  /* 
  public void confirmar() {
    setStatus(StatusPedido.CONFIRMADO);
    setDataConfirmacao(OffsetDateTime.now());

    registerEvent(new PedidoConfirmadoEvent(this));
  }

  public void entregar() {
    setStatus(StatusPedido.ENTREGUE);
    setDataEntrega(OffsetDateTime.now());
  }

  public void cancelar() {
    setStatus(StatusPedido.CANCELADO);
    setDataCancelamento(OffsetDateTime.now());

    registerEvent(new PedidoCanceladoEvent(this));
  } */

  /*   public boolean podeSerConfirmado() {
    return getStatus().podeAlterarPara(StatusPedido.CONFIRMADO);
  }

  public boolean podeSerEntregue() {
    return getStatus().podeAlterarPara(StatusPedido.ENTREGUE);
  }

  public boolean podeSerCancelado() {
    return getStatus().podeAlterarPara(StatusPedido.CANCELADO);
  } */

  /*   private void setStatus(StatusPedido novoStatus) {
    if (getStatus().naoPodeAlterarPara(novoStatus)) {
      throw new NegocioException(
        String.format(
          "Status do pedido %s não pode ser alterado de %s para %s",
          getCodigo(),
          getStatus().getDescricao(),
          novoStatus.getDescricao()
        )
      );
    }

    this.status = novoStatus;
  } */
  /*   @PrePersist
  private void gerarCodigo() {
    setCodigo(UUID.randomUUID().toString());
  } */
}
