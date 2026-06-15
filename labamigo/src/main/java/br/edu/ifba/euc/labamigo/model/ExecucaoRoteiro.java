package br.edu.ifba.euc.labamigo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "execucoes_roteiro") // repare
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExecucaoRoteiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_execucao")
    private Long id;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id_usuario", nullable = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_roteiro", nullable = false)
    private RoteiroExperimental roteiro;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;
}