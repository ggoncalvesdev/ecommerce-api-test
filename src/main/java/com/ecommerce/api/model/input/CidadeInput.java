package com.ecommerce.api.model.input;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CidadeInput {

  @ApiModelProperty(example = "Uberlândia", required = true)
  @NotBlank
  private String nome;

  @Valid
  @NotNull
  private EstadoIdInput estado;
}
