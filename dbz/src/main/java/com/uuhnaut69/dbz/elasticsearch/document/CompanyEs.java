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
@Document(indexName = "company-index", type = "company")
public class CompanyEs implements Serializable {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String phone;
}
