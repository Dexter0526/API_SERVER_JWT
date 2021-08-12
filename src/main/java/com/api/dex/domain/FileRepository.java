package com.api.dex.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    File findById(Integer id);

    Page<File> findByFileMember_Account(String account, Pageable pageable);

    Page<File> findByFileMember_Id(long id, Pageable pageable);
}
