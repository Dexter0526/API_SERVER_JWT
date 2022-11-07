package com.api.dex.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findById(Integer id);

    Optional<Member> findByAccount(String account);

    Optional<Member> findByToken(String token);

    Optional<Member> findByIdAndAccount(long id, String account);

    @Query("select m from member m " +
            "left join fetch m.fallows ")
    Optional<Member> findByIdWithSubscribe(long id);

    @Transactional
    void deleteByAccount(String account);
}
