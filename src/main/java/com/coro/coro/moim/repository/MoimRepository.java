package com.coro.coro.moim.repository;

import com.coro.coro.moim.domain.Moim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoimRepository extends JpaRepository<Moim, Long> {

    Optional<Moim> findByName(final String name);
}
