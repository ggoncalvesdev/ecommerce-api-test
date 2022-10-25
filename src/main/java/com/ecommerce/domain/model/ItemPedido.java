package com.ecommerce.domain.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class ItemPedido {

  @EqualsAndHashCode.Include
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "quantidade", nullable = false)
  private Integer quantidade;

  @Column(name = "preco_venda", nullable = false)
  private BigDecimal precoVenda;

  @Column(name = "percentual_desconto")
  private BigDecimal percentualDesconto;

  @Column(name = "valor_bruto", nullable = false)
  private BigDecimal valorBruto;

  @Column(name = "valor_liquido", nullable = false)
  private BigDecimal valorLiquido;

  private BigDecimal precoTotal;

  @ManyToOne
  @JoinColumn(nullable = false, name = "id_pedido")
  private Pedido pedido;

  @OneToOne(mappedBy = "itemPedido")
  private Produto produto;

  /*  public ItemPedido(
    Long id_item_pedido,
    Integer quantidade,
    BigDecimal precoVenda,
    BigDecimal percentualDesconto,
    BigDecimal valorBruto,
    BigDecimal valorLiquido,
    Pedido pedido,
    Produto produto
  ) {
    super();
    this.id_item_pedido = id_item_pedido;
    this.quantidade = quantidade;
    this.precoVenda = produto.getValorUnitario();
    this.percentualDesconto = percentualDesconto;
    this.valorBruto = precoVenda * quantidade;
    this.valorLiquido =
      valorBruto - ((valorBruto / 100) * this.percentualDesconto);
    this.pedido = pedido;
    this.produto = produto;
  } */

  public void calcularPrecoTotal() {
    BigDecimal precoVenda = this.getPrecoVenda();
    Integer quantidade = this.getQuantidade();

    if (precoVenda == null) {
      precoVenda = BigDecimal.ZERO;
    }

    if (quantidade == null) {
      quantidade = 0;
    }

    this.setPrecoTotal(precoVenda.multiply(new BigDecimal(quantidade)));
  }
  /*  public Object setId(Object value) {
    return null;
  } */
}
