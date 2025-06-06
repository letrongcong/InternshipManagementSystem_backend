    package com.project.internship_backend.entities;

    import jakarta.persistence.*;
    import lombok.*;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.Date;
    import java.util.List;

    @Data
    @Getter
    @Setter
    @Entity
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Table(name = "users")
    public class User extends BaseEntity implements UserDetails{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(name = "full_name", nullable = false)
        private String fullName;

        @Column(name = "profile_image")
        private String profileImage;

        @Column(name = "email", nullable = false, unique = true)
        private String email;

        @Column(name = "phone_number", nullable = false, unique = true)
        private String phoneNumber;

        @Column(name = "gender", nullable = false)
        private String gender;

        @Column(name = "date_of_birth")
        private Date dateOfBirth;

        @Column(name = "desired_role", nullable = false)
        private String desiredRole;

        @Column(name = "password", nullable = false)
        private String password;

        @Column(name = "is_active")
        private boolean active;

        @ManyToOne
        @JoinColumn(name = "role_id", referencedColumnName = "id")
        private Role role;

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Lecturer lecturer;

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Student student;

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private Mentor mentor;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
            authorityList.add(new SimpleGrantedAuthority("ROLE_"+getRole().getName().toUpperCase()));
            return authorityList;
        }

        @Override
        public String getUsername() {
            return email;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
