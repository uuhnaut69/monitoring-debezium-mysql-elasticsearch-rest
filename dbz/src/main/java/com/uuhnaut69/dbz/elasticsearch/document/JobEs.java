package com.uuhnaut69.dbz.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author uuhnaut
 * @project dbz-monitor
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "job-index", type = "job")
public class JobEs implements Serializable {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Nested)
    private CompanyEs companyEs;
}
