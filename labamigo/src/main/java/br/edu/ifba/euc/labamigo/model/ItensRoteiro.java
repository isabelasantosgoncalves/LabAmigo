package br.edu.ifba.euc.labamigo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "itens_roteiro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItensRoteiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_itens")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_roteiro", nullable = false)
    private RoteiroExperimental roteiro;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private Insumo insumo;

    @Column(name = "quantidade_necessaria")
    private Double quantidadeNecessaria;
}