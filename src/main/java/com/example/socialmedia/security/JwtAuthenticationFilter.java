package com.example.socialmedia.security;

import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        super((ReactiveAuthenticationManager) authentication -> Mono.just(authentication)); // dummy manager

        this.jwtService = jwtService;

        this.setRequiresAuthenticationMatcher(exchange ->
                extractToken(exchange)
                        .filter(jwtService::isTokenValid)
                        .flatMap(token -> ServerWebExchangeMatcher.MatchResult.match())
                        .switchIfEmpty(ServerWebExchangeMatcher.MatchResult.notMatch())
        );

        this.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
    }

    private Mono<String> extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return Mono.just(authHeader.substring(7));
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.web.server.WebFilterChain chain) {
        return extractToken(exchange)
                .filter(jwtService::isTokenValid)
                .map(token -> {
                    String username = jwtService.extractUsername(token);
                    return new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                })
                .flatMap(auth -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(new SecurityContextImpl(auth)))))
                .switchIfEmpty(chain.filter(exchange));
    }
}
