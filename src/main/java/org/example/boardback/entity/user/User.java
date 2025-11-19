package org.example.boardback.entity.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.boardback.common.enums.Gender;
import org.example.boardback.common.enums.RoleType;
import org.example.boardback.entity.base.BaseTimeEntity;
import org.example.boardback.entity.file.FileInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_users_nickname", columnNames = "nickname"),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "username", updatable = false, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    // 프로필 이미지 파일 매핑
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_file_id",
            foreignKey = @ForeignKey(name = "fk_users_profile_file"))
    private FileInfo profileFile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

    @Builder
    private User(String username, String password, String email, String nickname, Gender gender, FileInfo profileFile) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.gender = gender;
        this.profileFile = profileFile;
    }

    // == 도메인 로직 == //
    public void changePassword(String password) {
        this.password = password;
    }

    public void updateProfile(String nickname, Gender gender) {
        this.nickname = nickname;
        this.gender = gender;
    }

    public void updateProfileImage(FileInfo newProfileFile) {
        this.profileFile = newProfileFile;
    }

    public void grantRole(Role role) {
        boolean exists = userRoles.stream()
                .anyMatch(userRole -> userRole.getRole().equals(role));
        if (!exists) {
            userRoles.add(new UserRole(this, role));
        }
    }

    public void revokeRole(Role role) {
        userRoles.removeIf(userRole -> userRole.getRole().equals(role));
    }

    public Set<RoleType> getRoleTypes() {
        return userRoles.stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .collect(Collectors.toUnmodifiableSet());
    }
}