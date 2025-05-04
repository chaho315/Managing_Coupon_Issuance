package org.example.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.couponcore.component.DistributeLockExecutor;
import org.example.couponcore.exception.CouponIssueException;
import org.example.couponcore.exception.ErrorCode;
import org.example.couponcore.model.Coupon;
import org.example.couponcore.repository.redis.RedisRepository;
import org.example.couponcore.repository.redis.dto.CouponIssueRequest;
import org.example.couponcore.repository.redis.dto.CouponRedisEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.example.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponIssueService couponIssueService;

    private final DistributeLockExecutor distributeLockExecutor;
    private final CouponCacheService couponCacheService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /*public void issue(long couponId, long userId) {
        // 1. 유저의 요청을 sorted set 적재
        String key = "issue.request.sorted_set.couponId=%s".formatted(couponId);
        redisRepository.zAdd(key, String.valueOf(userId),System.currentTimeMillis());
        // 2. 유저의 요청의 순서를 조회
        // 3. 조회 결과를 선착순 조건과 비교
        // 4. 쿠폰 발급 queue에 적재
    }*/
    public void issue(long couponId, long userId) {
        /*Coupon coupon = couponIssueService.findCoupon(couponId);*/
        CouponRedisEntity coupon = couponCacheService.getCouponCache(couponId);
        coupon.checkIssuableCoupon();
        /*if(!coupon.availableIssueDate()){
            throw new CouponIssueException(ErrorCode.INVAILD_COUPON_ISSUE_DATE,"발급 가능한 일자가 아닙니다. couponId: %s, issueStart: %s, issueEnd: %s".formatted(couponId, coupon.getDateIssueStart(), coupon.getDateIssueEnd()));
        }*/
        distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000, 3000, () -> {
            /*if(!couponIssueRedisService.availableTotalIssueQuantity(coupon.getTotalQuantity(), couponId)){
            if(!couponIssueRedisService.availableTotalIssueQuantity(coupon.totalQuantity(), couponId)){
                throw new CouponIssueException(ErrorCode.INVAILD_COUPON_ISSUE_QUANTITY, "발급가능한 수량을 초과합니다. couponId: %s, userId: %s".formatted(couponId, userId));
            }

            if(!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)){
                throw new CouponIssueException(ErrorCode.DUPLICATED_COUPON_ISSUE, "이미 발급 요청이 처리되었습니다. couponId: %s, userId: %s".formatted(couponId, userId));
            }*/
            couponIssueRedisService.checkCouponIssueQuantity(coupon, userId);
            issueRequest(couponId, userId);
        });

    }

    private void issueRequest(long couponId, long userId){
        CouponIssueRequest issueRequest = new CouponIssueRequest(couponId, userId);
        try {
            String value = objectMapper.writeValueAsString(issueRequest); // dto를 string 타입으로 직렬화
            redisRepository.sAdd(getIssueRequestKey(couponId), String.valueOf(userId));
            //쿠폰 발급 큐 적재를 해야하는 과정이 남음
            redisRepository.rPush(getIssueRequestQueueKey(), value);
        }catch(JsonProcessingException e){
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST,"input: %s".formatted(issueRequest));
        }

    }
}
