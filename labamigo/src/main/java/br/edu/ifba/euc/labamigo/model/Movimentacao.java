package br.edu.ifba.euc.labamigo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "movimentacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimentacao")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Insumo insumo;

    @ManyToOne(optional = true)
    @JoinColumn(nullable = true, name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_execucao")
    private ExecucaoRoteiro execucaoRoteiro;

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "tipo", length = 30)
    private String tipo;

    @Column(name = "descricao_lote", length = 150)
    private String descricaoLote;

    @Column(name = "quantidade")
    private Double quantidade;

    @Column(name = "data_validade")
    private LocalDate dataValidade;
}
