package com.hotel.booking.modules.iam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RolePermissionId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "role_id")
    private Short roleId;

    @Column(name = "permission_id")
    private Short permissionId;
}
