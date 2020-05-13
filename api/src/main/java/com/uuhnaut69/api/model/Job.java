package com.uuhnaut69.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text", nullable = false)
    private String title;
}
