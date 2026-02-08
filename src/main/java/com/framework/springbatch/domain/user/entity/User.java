package com.framework.springbatch.domain.user.entity;

import com.framework.springbatch.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "USERS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 500)
    private String password;

    @Column(length = 200)
    private String email;

    @Column(length = 50)
    @Builder.Default
    private String role = "USER";

    @Column
    @Builder.Default
    private Boolean enabled = true;
}
