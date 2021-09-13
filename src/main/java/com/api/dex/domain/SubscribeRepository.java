package com.api.dex.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    Page<Subscribe> findByLike_Id(long boardId, Pageable pageable);

    Page<Subscribe> findByOwner_Id(long ownerId, Pageable pageable);

    Subscribe findByOwner_IdAndFallow_Id(long ownerId, long fallowId);
    Subscribe findByLike_IdAndFallow_Id(long boardId, long fallowId);

    @Transactional
    void deleteByIdAndFallow_Account(long id, String account);
}
