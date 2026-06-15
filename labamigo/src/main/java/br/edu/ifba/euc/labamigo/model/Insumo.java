package br.edu.ifba.euc.labamigo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "insumos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "estado_fisico", length = 50)
    private String estadoFisico;

    @Column(name = "quantidade")
    private Double quantidade;

    @Column(name = "unidade_medida", length = 20)
    private String unidadeMedida;

    @Column(name = "classificacao_risco", length = 100)
    private String classificacaoRisco;

    @Column(name = "qtd_alerta_minimo")
    private Integer qtdAlertaMinimo;

    @Column(name = "qtd_alerta_medio")
    private Integer qtdAlertaMedio;
}