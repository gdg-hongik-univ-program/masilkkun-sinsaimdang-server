package com.sinsaimdang.masilkkoon.masil.article.dto;

import com.sinsaimdang.masilkkoon.masil.article.entity.ArticleTag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleSearchCondition {
    private List<ArticleTag> tags; // 태그 (여러 개 선택 가능)
    private String region;       // 지역
    private String sortOrder; // 정렬 기준
}