package com.aryeh.CouponSystem.data.repository;

import com.aryeh.CouponSystem.data.entity.UnionEmailsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnionEmailsViewRepository extends JpaRepository<UnionEmailsView, Long> {
}
