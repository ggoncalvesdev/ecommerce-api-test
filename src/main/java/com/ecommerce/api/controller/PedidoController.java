package com.ecommerce.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.domain.model.dtos.PedidoRequestDTO;
import com.ecommerce.domain.model.dtos.PedidoResponseDTO;
import com.ecommerce.domain.service.PedidoService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {	

	@Autowired
	private PedidoService pedidoService;

	@GetMapping
	@ApiOperation(value="Lista todos os pedidos", notes="Listagem de Pedidos")
	@ApiResponses(value= {	 
	@ApiResponse(code=200, message="Retorna todos os pedidos"),	
	@ApiResponse(code=505, message="Exceção interna da aplicação"),
	})
	public ResponseEntity<List<PedidoResponseDTO>> listar() {
		return ResponseEntity.ok(pedidoService.listarTodos());
	}
	
	@GetMapping("/{pedidoId}")
	@ApiOperation(value="Listando um pedido por id", notes="Listagem de um pedido por id")
	@ApiResponses(value= {	 
	@ApiResponse(code=200, message="Retorna um pedido pelo id"),
	@ApiResponse(code=404, message="Recurso não encontrado"),
	@ApiResponse(code=505, message="Exceção interna da aplicação"),
	})
	public ResponseEntity<PedidoResponseDTO> listarPorId(@PathVariable Long pedidoId)  {
		PedidoResponseDTO pedidoDTO = pedidoService.listarPorId(pedidoId);
		return ResponseEntity.ok(pedidoDTO);
	}

	@PostMapping
	@ApiOperation(value="Cadastra um pedido ", notes="Cadatro de Pedidos")
	@ApiResponses(value= {	 
	@ApiResponse(code=201, message="Pedido cadastrado"),
	@ApiResponse(code=500, message="Ocorreu um Erro na execução"),
	@ApiResponse(code=505, message="Exceção interna da aplicação"),
	})
	public ResponseEntity<PedidoResponseDTO> adicionar(@RequestBody PedidoRequestDTO produto) {
		PedidoResponseDTO pedidoDTO = pedidoService.salvar(produto);
		return ResponseEntity.status(HttpStatus.CREATED).body(pedidoDTO);
	}

	@PutMapping("/{pedidoId}")
	@ApiOperation(value="Substitui um pedido pelo id", notes="Substitui Pedidos pelo id")
	@ApiResponses(value= {	 
	@ApiResponse(code=200, message="Modificações realizadas com sucesso"),
	@ApiResponse(code=404, message="Recurso não encontrado"),
	@ApiResponse(code=500, message="Ocorreu um Erro na execução"),
	@ApiResponse(code=505, message="Exceção interna da aplicação"),
	})
	public ResponseEntity<PedidoResponseDTO> atualizar(@PathVariable Long pedidoId, @RequestBody PedidoRequestDTO produto) {
		PedidoResponseDTO pedidoDTO = pedidoService.substituir(pedidoId, produto);
		return ResponseEntity.ok(pedidoDTO);
	}

	@DeleteMapping("/{pedidoId}")
	@ApiOperation(value="Deleta um pedido pelo id", notes="Deleta Pedidos pelo id")
	@ApiResponses(value= {	 
	@ApiResponse(code=204, message="Pedido excluído"),
	@ApiResponse(code=404, message="Recurso não encontrado"),
	@ApiResponse(code=500, message="Ocorreu um Erro na execução"),
	@ApiResponse(code=505, message="Exceção interna da aplicação"),
	})
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void remover(@PathVariable Long pedidoId) {
		pedidoService.excluir(pedidoId);
	}
}
