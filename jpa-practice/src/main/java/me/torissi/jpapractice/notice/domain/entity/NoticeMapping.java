package me.torissi.jpapractice.notice.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = "TB_SCHOOL")
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor(access = PROTECTED)
public class NoticeMapping extends EntityExtension {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "schoolId")
  private School school;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "noticeId")
  private Notice notice;

}
