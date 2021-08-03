package com.api.dex.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findById(Integer id);

    Optional<Member> findByAccount(String account);

    Optional<Member> findByToken(String token);

    Optional<Member> findByIdAndAccount(long id, String account);

    @Transactional
    Long deleteByAccount(String account);
}
