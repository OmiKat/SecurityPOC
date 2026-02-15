package com.omi.SeXurityPOC.repo;

import com.omi.SeXurityPOC.pojos.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Locale;
import java.util.Optional;

public interface RefreshTokenRepo extends JpaRepository<RefreshTokenEntity,Long> {

    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

}
