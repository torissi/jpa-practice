package me.torissi.jpapractice.notice.domain.repository;

import static me.torissi.jpapractice.notice.domain.entity.QNotice.notice;
import static me.torissi.jpapractice.notice.domain.entity.QNoticeHitsLog.noticeHitsLog;
import static org.modelmapper.Conditions.isNotNull;
import static org.modelmapper.convention.MatchingStrategies.STRICT;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import me.torissi.jpapractice.notice.domain.dto.response.NoticeAllResponse;
import me.torissi.jpapractice.notice.domain.dto.response.NoticeStatisticsResponse;
import me.torissi.jpapractice.notice.domain.dto.response.QNoticeStatisticsResponse;
import me.torissi.jpapractice.notice.domain.entity.Notice;
import me.torissi.jpapractice.notice.domain.vo.Search;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
public class NoticeRepositoryCustomImpl implements NoticeRepositoryCustom{

  private final JPQLQueryFactory queryFactory;

  @Override
  public List<NoticeAllResponse> getNoticeAll(Search search) {
    ModelMapper modelMapper = new ModelMapper();

    modelMapper
        .getConfiguration()
        .setPropertyCondition(isNotNull())
        .setMatchingStrategy(STRICT);
    /*
     *        SELECT  T.title,
     *                (
     *                    SELECT  COUNT(L.*)
     *                      FROM  LOG L
     *                     WHERE  L.notice_id = T.id
     *                       AND  L.log_date >= NOW()
     *                       AND  L.log_date < DATE_ADD(NOW(), INTERVAL 1 DAY)
     *                ) AS hit
     *          FROM  NOTICE T
     *         WHERE  T.del_flag = false
     *           AND  T.use_flag = true
     */
    double division = ChronoUnit.DAYS.between(search.getStartDt(), search.getEndDt());
    List<NoticeStatisticsResponse> statistics = queryFactory
        .select(new QNoticeStatisticsResponse(
            notice.title,
            JPAExpressions
                .select(noticeHitsLog.count().castToNum(Double.class).divide(division))
                .from(noticeHitsLog)
                .where(
                    noticeHitsLog.notice.eq(notice),
                    noticeHitsLog.logDate.goe(search.getStartDt().atStartOfDay()),
                    noticeHitsLog.logDate.before(search.getEndDt().plusDays(1).atStartOfDay())
                )
        )).from(notice)
        .where(notice.delFlag.isFalse(), notice.useFlag.isTrue())
        .fetch();

    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    statistics.forEach(item -> System.out.println("TITLE: " + item.getTitle() + " / HIT: " + item.getHit()));
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

    /*
     *        SELECT  T.*
     *          FROM  NOTICE T
     *     LEFT JOIN  LOG L
     *            ON  L.notice_id = T.id
     *         WHERE  T.del_flag = false
     *           AND  T.use_flag = true
     *           AND  L.log_date >= NOW()
     *           AND  L.log_date < DATE_ADD(NOW(), INTERVAL 1 DAY)
     */
//    JPQLQuery<Notice> query = queryFactory
//        .selectFrom(notice)
//        .where(
//            notice.delFlag.isFalse(),
//            notice.useFlag.isTrue(),
//            notice.logList.any().logDate.goe(search.getStartDt().atStartOfDay()),
//            notice.logList.any().logDate.before(search.getEndDt().plusDays(1).atStartOfDay())
//        );
//
//    double dayCount = ChronoUnit.DAYS.between(search.getStartDt(), search.getEndDt());
    /*
     *        SELECT  T.*
     *          FROM  LOG T
     *         WHERE  T.notice_id = {?}
     *
     * List<Log> list;
     *
     * list.size();
     */
//    List<NoticeAllResponse> list = query.fetch().stream().map(noticeEntity -> {
//      NoticeAllResponse data = modelMapper.map(noticeEntity, NoticeAllResponse.class);
//
//      data.setAverageHits(noticeEntity.getLogList().size() / dayCount);
//
//      return data;
//    }).collect(Collectors.toList());
//    double hitsCount = query.fetchCount()/dayCount;
//
//    NoticeAllResponse dto = modelMapper.map(query.fetch(), NoticeAllResponse.class);
//    dto.setAverageHits(hitsCount);

    return null;
  }

/*  public NoticeAllResponse getNoticeAll2(Search search) {
    JPQLQuery<?> query = queryFactory
        .select(
            notice.title.count(),
            notice.createDt.between(search.getStartDt(), search.getEndDt()).count().as("dayCount")
        )
        .from(notice)
        .where(
            notice.delFlag.isFalse(),
            notice.useFlag.isTrue(),
            notice.logList.any().logDate.goe(search.getStartDt().atStartOfDay()),
            notice.logList.any().logDate.before(search.getEndDt().plusDays(1).atStartOfDay())
        );

    return dto;
  }*/
}
