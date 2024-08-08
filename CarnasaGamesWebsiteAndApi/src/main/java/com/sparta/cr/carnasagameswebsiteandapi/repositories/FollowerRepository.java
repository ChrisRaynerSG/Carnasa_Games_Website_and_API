package com.sparta.cr.carnasagameswebsiteandapi.repositories;

import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModel;
import com.sparta.cr.carnasagameswebsiteandapi.models.FollowerModelId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowerRepository extends JpaRepository<FollowerModel, FollowerModelId> {
}
