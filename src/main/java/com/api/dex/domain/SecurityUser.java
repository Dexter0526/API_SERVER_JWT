package com.api.dex.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class SecurityUser implements UserDetails {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUser.class);

    private Member member;
    private Set<MemberRole> roles;

    private String JSESSIONID;

    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private boolean enabled;

    @Builder
    public SecurityUser(Member member, Set<MemberRole> roles, String JSESSIONID) {
        setMember(member);
        setRoles(roles);
        setJSESSIONID(JSESSIONID);
        this.accountNonExpired = true;
        this.accountNonLocked = true;
        this.credentialsNonExpired = true;
        this.enabled = true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getRoles();
    }

    public Set<MemberRole.RoleType> getRoleTypes() {
        return getRoles().stream().map(f -> f.getRoleName()).collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.member.getAccount();
    }

    @Override
    public String getPassword() {
        if(this.member != null){
            return this.member.getPassword();
        } else{
            return null;
        }

    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SecurityUser) {
            return this.member.getAccount().equals( ((SecurityUser) obj).getUsername() );
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.member.getAccount() != null ? this.member.getAccount().hashCode() : 0;
    }


}

