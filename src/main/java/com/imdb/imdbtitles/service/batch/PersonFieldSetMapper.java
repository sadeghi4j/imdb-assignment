package com.imdb.imdbtitles.service.batch;

import com.imdb.imdbtitles.entity.Person;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;


public class PersonFieldSetMapper implements FieldSetMapper<Person> {

    @Override
    public Person mapFieldSet(FieldSet fieldSet) throws BindException {
        return new Person(fieldSet.readString("id"),
                fieldSet.readString("primaryName"),
                getValue(fieldSet.readString("birthYear")),
                getValue(fieldSet.readString("deathYear"))
        );
    }

    private Integer getValue(String field) {
        return field.equals("\\N") ? null : Integer.parseInt(field);
    }
}
