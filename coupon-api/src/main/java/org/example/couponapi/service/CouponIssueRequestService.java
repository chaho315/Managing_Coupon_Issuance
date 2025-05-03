package org.example.couponapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.couponapi.controller.dto.CouponIssueRequestDto;
import org.example.couponcore.component.DistributeLockExecutor;
import org.example.couponcore.service.CouponIssueService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;

    public void issueRequestV1(CouponIssueRequestDto requestDto){
        /*synchronized (this) {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        }*/
        /*distributeLockExecutor.execute("lock_"+requestDto.couponId(), 10000, 10000,() -> {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        });*///redis를 이용한 동시성 이슈 해결

        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        //mysql 을 통한 통시성 이슈 해결
        log.info("쿠폰 발급 완료. couponId: %s, userId: %s".formatted(requestDto.couponId(), requestDto.userId()));
    }
    //1. mysql, 2. redis 각각 rock을 구현하고 성능 테스트 진행
}
