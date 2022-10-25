package com.ecommerce.domain.model;

import com.ecommerce.domain.event.PedidoCanceladoEvent;
import com.ecommerce.domain.event.PedidoConfirmadoEvent;
import com.ecommerce.domain.exception.NegocioException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.AbstractAggregateRoot;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Entity
public class Pedido extends AbstractAggregateRoot<Pedido> {

  @EqualsAndHashCode.Include
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id_pedido;

  private BigDecimal subtotal;
  private BigDecimal taxaFrete;
  private BigDecimal valorTotal;

  @Column(name = "data_envio")
  private OffsetDateTime dataEnvio;

  @Column(name = "data_pedido")
  @CreationTimestamp
  private OffsetDateTime dataPedido;

  @Column(name = "data_entrega")
  private OffsetDateTime dataEntrega;

  private OffsetDateTime dataConfirmacao;
  private OffsetDateTime dataCancelamento;

  @Embedded
  private Endereco enderecoEntrega;

  @Enumerated(EnumType.STRING)
  private StatusPedido status = StatusPedido.CRIADO;

  @OneToOne
  @JoinColumn(name = "id_cliente")
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
  }

  private void setStatus(StatusPedido novoStatus) {
    if (getStatus().naoPodeAlterarPara(novoStatus)) {
      throw new NegocioException(
        String.format(
          "Status do pedido %s não pode ser alterado de %s para %s",
          getStatus().getDescricao(),
          novoStatus.getDescricao()
        )
      );
    }

    this.status = novoStatus;
  }
}
